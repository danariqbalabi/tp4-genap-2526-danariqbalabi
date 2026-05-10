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
 * Job class pengembara yang memulihkan HP di awal turn.
 */
public class Support extends Wanderer {
    /**
     * Membuat support baru.
     *
     * @param idNumber nomor urut pengembara
     * @param name nama tampilan
     * @param username username login
     * @param password password login
     * @param maxHp HP maksimum
     * @param attack attack dasar
     * @param defense defense dasar
     */
    public Support(int idNumber, String name, String username,
                   String password, double  maxHp, double  attack, double  defense) {
        super(idNumber, name, username, password, maxHp, attack, defense);
    }

    /**
     * Memulihkan 10 persen max HP setiap awal turn support.
     */
    @Override
    public void onTurnStart() {
        double before = getCurrentHp();
        double heal = getMaxHp() * 0.1;
        setCurrentHp(before + heal);
        double actualHeal = getCurrentHp() - before;
        setCustomDamageNote("[SUPPORT]: " + getName() + " memulihkan "
                + BurhanQuestUtils.formatDouble(actualHeal) + " HP!");
        recordHeal(actualHeal);
        BurhanLogger.getInstance().log(
                LogCategory.BATTLE,
                getActorName(),
                "[SUPPORT] Berhasil memulihkan "
                        + BurhanQuestUtils.formatDouble(actualHeal) + " HP!");
    }

}
