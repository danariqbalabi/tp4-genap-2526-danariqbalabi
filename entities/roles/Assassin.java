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

import java.util.Random;

/**
 * Job class pengembara dengan peluang memberi efek bleed.
 */
public class Assassin extends Wanderer {
    private static final Random RANDOM = new Random();

    /**
     * Membuat assassin baru.
     *
     * @param idNumber nomor urut pengembara
     * @param name nama tampilan
     * @param username username login
     * @param password password login
     * @param maxHp HP maksimum
     * @param attack attack dasar
     * @param defense defense dasar
     */
    public Assassin(int idNumber, String name, String username,
                    String password, double  maxHp, double  attack, double  defense) {
        super(idNumber, name, username, password, maxHp, attack, defense);
    }

    /**
     * Berpeluang memberi bleed ke target tanpa mengubah damage langsung.
     *
     * @param baseDamage damage sebelum efek assassin
     * @return damage akhir setelah efek assassin
     */
    @Override
    public double modifyDamageDealt(double baseDamage) {
        Monster target = getCurrentTarget();
        if (target != null && RANDOM.nextBoolean()) {
            int bleedDamage = (int) Math.round(getAttackPower() * 0.2);
            target.applyBleed("ASSASSIN", bleedDamage);
            setCustomDamageNote("[ASSASSIN] " + target.getName() + " terkena Bleed!");
            recordPassiveTrigger("Bleed berhasil diterapkan");
            BurhanLogger.getInstance().log(
                    LogCategory.BATTLE,
                    getActorName(),
                    "[ASSASSIN] Berhasil memberikan efek Bleed kepada " + target.getName() + "!");
        }
        return baseDamage;
    }

}
