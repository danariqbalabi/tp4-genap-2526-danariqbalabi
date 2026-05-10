package entities.roles;

import entities.*;
import entities.interfaces.*;
import entities.monster.*;
import entities.roles.*;
import exception.*;
import quests.*;
import quests.enums.*;
import quests.interfaces.*;
import services.*;
import utils.*;
import utils.enums.*;
import utils.interfaces.*;

/**
 * Job class pengembara yang mengumpulkan overcharge lalu melepas Arcane Burst.
 */
public class Mage extends Wanderer {
    private boolean overcharged;
    private boolean usedBurstThisTurn;

    /**
     * Membuat mage baru dengan status overcharge kosong.
     *
     * @param idNumber nomor urut pengembara
     * @param name nama tampilan
     * @param username username login
     * @param password password login
     * @param maxHp HP maksimum
     * @param attack attack dasar
     * @param defense defense dasar
     */
    public Mage(int idNumber, String name, String username,
                String password, double  maxHp, double  attack, double  defense) {
        super(idNumber, name, username, password, maxHp, attack, defense);
        overcharged = false;
        usedBurstThisTurn = false;
    }

    /**
     * Menyiapkan flag turn agar Arcane Burst tidak langsung mengisi overcharge lagi.
     */
    @Override
    public void onTurnStart() {
        usedBurstThisTurn = false;
    }

    /**
     * Jika overcharged, damage berubah menjadi dua kali attack dan mengabaikan defense.
     *
     * @param baseDamage damage normal sebelum efek mage
     * @return damage akhir setelah efek mage
     */
    @Override
    public double modifyDamageDealt(double baseDamage) {
        double result = baseDamage;
        if (overcharged) {
            result = getAttackPower() * 2;
            overcharged = false;
            usedBurstThisTurn = true;
            setCustomDamageNote("[MAGE] Arcane Burst aktif!");
            recordPassiveTrigger("Arcane Burst aktif");
            BurhanLogger.getInstance().log(
                    LogCategory.BATTLE,
                    getActorName(),
                    "[MAGE] Arcane Burst aktif!");
        }
        return result;
    }

    /**
     * Mengisi overcharge setelah serangan normal berhasil.
     *
     * @param result indikator hasil serangan
     */
    @Override
    public void onTurnEnd(double result) {
        if (result > 0 && !overcharged && !usedBurstThisTurn) {
            overcharged = true;
            setCustomDamageNote("[MAGE] Mana terkumpul! Overcharged untuk serangan berikutnya.");
            recordPassiveTrigger("Overcharged terkumpul");
        }
    }

    /**
     * Menghapus overcharge sebelum battle baru dimulai.
     */
    @Override
    public void resetBattleState() {
        super.resetBattleState();
        overcharged = false;
        usedBurstThisTurn = false;
    }
}
