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
 * Job class pengembara yang mengurangi damage saat HP rendah.
 */
public class Tank extends Wanderer {
    /**
     * Membuat tank baru.
     *
     * @param idNumber nomor urut pengembara
     * @param name nama tampilan
     * @param username username login
     * @param password password login
     * @param maxHp HP maksimum
     * @param attack attack dasar
     * @param defense defense dasar
     */
    public Tank(int idNumber, String name, String username,
                String password, double  maxHp, double  attack, double  defense) {
        super(idNumber, name, username, password, maxHp, attack, defense);
    }

    /**
     * Memotong damage masuk sebesar 50 persen saat HP di bawah atau sama dengan 30 persen.
     *
     * @param incomingDamage damage sebelum efek tank
     * @return damage akhir setelah efek tank
     */
    @Override
    public double modifyDamageTaken(double incomingDamage) {
        double finalDamage = incomingDamage;
        if (getCurrentHp() <= getMaxHp() * 0.3) {
            finalDamage = incomingDamage * 0.5;
            setCustomDamageNote("[TANK] Shield menyala! Damage terpotong 50%");
            recordPassiveTrigger("Shield aktif");
            BurhanLogger.getInstance().log(
                    LogCategory.BATTLE,
                    getActorName(),
                    "[TANK] Shield menyala! Damage Terpotong 50%");
        }
        return finalDamage;
    }
}
