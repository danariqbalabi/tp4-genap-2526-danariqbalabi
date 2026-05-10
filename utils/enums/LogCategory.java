package utils.enums;

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
 * Kategori event yang ditulis ke file log BurhanQuest.
 */
public enum LogCategory {
    /** Event login dan logout. */
    AUTH,

    /** Event yang dipicu admin. */
    ADMIN,

    /** Event yang dipicu pengembara. */
    PENGEMBARA,

    /** Event penting selama battle. */
    BATTLE,

    /** Event sistem otomatis. */
    SYSTEM,

    /** Event error dari exception handling. */
    ERROR
}
