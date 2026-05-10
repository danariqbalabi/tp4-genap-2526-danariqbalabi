package exception;

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
 * Exception ketika level pengembara belum memenuhi syarat minimum quest.
 */
public class InsufficientLevelException extends BurhanQuestException {
    private static final long serialVersionUID = 1L;

    /**
     * Membuat exception level tidak cukup.
     *
     * @param currentLevel level pengembara saat ini
     * @param minimumLevel level minimum yang dibutuhkan quest
     */
    public InsufficientLevelException(int currentLevel, int minimumLevel) {
        super("Level pengembara belum cukup. Level saat ini: "
                + currentLevel + ", minimum: " + minimumLevel + ".");
    }
}
