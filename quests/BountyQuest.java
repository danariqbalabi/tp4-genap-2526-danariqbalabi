package quests;

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
 * Quest sekali selesai yang memberi bonus exp dan koin di luar reward monster.
 */
public class BountyQuest extends Quest implements Rewardable {
    private int bonusExp;
    private int bonusCoin;

    /**
     * Membuat bounty quest baru.
     *
     * @param idNumber nomor urut quest
     * @param name nama quest
     * @param description deskripsi quest
     * @param difficulty tingkat kesulitan
     * @param monster monster lawan
     * @param minLevel level minimum pengembara
     * @param bonusExp bonus exp tambahan
     * @param bonusCoin bonus koin tambahan
     */
    public BountyQuest(int idNumber, String name, String description,
                       Difficulty difficulty, Monster monster,
                       int minLevel, int bonusExp, int bonusCoin) {
        super(idNumber, name, description, difficulty, monster, minLevel);
        if (bonusExp < 0 || bonusCoin < 0) {
            throw new IllegalArgumentException("Bonus bounty tidak boleh negatif.");
        }
        this.bonusExp = bonusExp;
        this.bonusCoin = bonusCoin;
    }

    /**
     * Mengembalikan tipe quest bounty.
     *
     * @return string "Bounty"
     */
    @Override
    public String getQuestType() {
        return "Bounty";
    }

    /**
     * Mengembalikan bonus EXP tambahan bounty.
     *
     * @return bonus EXP
     */
    @Override
    public int getBonusExp() { return bonusExp; }

    /**
     * Mengembalikan bonus koin tambahan bounty.
     *
     * @return bonus koin
     */
    @Override
    public int getBonusCoin() { return bonusCoin; }

    /**
     * Menyusun deskripsi lengkap bounty quest.
     *
     * @return deskripsi lengkap bounty quest
     */
    @Override
    public String toString() {
        return "ID Quest: " + getId()
                + "\nNama Quest: " + getName()
                + "\nTipe Quest: " + getQuestType()
                + "\nDeskripsi Quest: " + getDescription()
                + "\nTingkat Kesulitan: " + getDifficulty().getDisplayName()
                + "\nMonster: " + getMonster().getName()
                + "\nLevel Minimum: " + getMinLevel()
                + "\nReward Koin: " + getMonster().getCoinReward()
                + "\nReward Exp: " + getMonster().getExpReward()
                + "\nBonus Koin: " + bonusCoin
                + "\nBonus Exp: " + bonusExp
                + "\nStatus: " + getStatus().getDisplayName();
    }
}
