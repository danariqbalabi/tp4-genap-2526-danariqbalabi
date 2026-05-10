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
 * Job class pengembara yang mengumpulkan Fury Stack untuk menaikkan damage.
 */
public class Fighter extends Wanderer {
    private int furyStacks;

    /**
     * Membuat fighter baru dengan fury stack awal 0.
     *
     * @param idNumber nomor urut pengembara
     * @param name nama tampilan
     * @param username username login
     * @param password password login
     * @param maxHp HP maksimum
     * @param attack attack dasar
     * @param defense defense dasar
     */
    public Fighter(int idNumber, String name, String username,
                   String password, double  maxHp, double attack, double defense) {
        super(idNumber, name, username, password, maxHp, attack, defense);
        furyStacks = 0;
    }

    /**
     * Menambahkan bonus damage berdasarkan jumlah fury stack saat ini.
     *
     * @param baseDamage damage sebelum bonus fighter
     * @return damage akhir setelah bonus fighter
     */
    @Override
    public double modifyDamageDealt(double baseDamage) {
        double bonus = getAttackPower() * 0.1 * furyStacks;
        if (furyStacks > 0) {
            String roman = toRoman(furyStacks);
            int percent = furyStacks * 10;
            setCustomDamageNote("[FIGHTER]: Fury Stacks (" + roman
                    + ") aktif, Damage bertambah sebesar " + percent + "% ATK!");
            recordPassiveTrigger("Fury Stacks " + roman + " aktif");
            BurhanLogger.getInstance().log(
                    LogCategory.BATTLE,
                    getActorName(),
                    "[FIGHTER] Fury stacks aktif! (Total: " + furyStacks + ")");
        }
        return baseDamage + bonus;
    }

    /**
     * Menambah stack setelah serangan berhasil dan meresetnya saat mencapai batas.
     *
     * @param result indikator hasil serangan
     */
    @Override
    public void onTurnEnd(double result) {
        if (result > 0) {
            if (furyStacks >= 3) {
                furyStacks = 0;
                setCustomDamageNote("[FIGHTER] Stacks full, Reset ke 0!");
                recordPassiveTrigger("Reset Fury Stack");
            } else {
                furyStacks++;
                setCustomDamageNote("[FIGHTER] Stacks bertambah!");
            }
        }
    }

    /**
     * Mereset fury stack sebelum battle baru.
     */
    @Override
    public void resetBattleState() {
        super.resetBattleState();
        furyStacks = 0;
    }

    /**
     * Mengonversi angka kecil ke format angka romawi untuk tampilan fury.
     *
     * @param value angka 1..3
     * @return representasi romawi
     */
    private String toRoman(int value) {
        String roman = "I";
        if (value == 2) {
            roman = "II";
        } else if (value == 3) {
            roman = "III";
        }
        return roman;
    }
}
