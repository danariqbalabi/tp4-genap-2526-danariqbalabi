package quests.interfaces;

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
 * Kontrak untuk quest yang memberi reward tambahan di luar reward monster.
 */
public interface Rewardable {
    /**
     * Mengembalikan bonus exp tambahan.
     *
     * @return bonus exp tambahan
     */
    int getBonusExp();

    /**
     * Mengembalikan bonus koin tambahan.
     *
     * @return bonus koin tambahan
     */
    int getBonusCoin();
}
