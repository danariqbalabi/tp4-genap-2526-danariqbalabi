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
 * Exception untuk kegagalan baca/tulis file pada fitur import dan export.
 */
public class DataFileException extends BurhanQuestException {
    private static final long serialVersionUID = 1L;

    /**
     * Membuat exception file dengan pesan error.
     *
     * @param message pesan error file
     */
    public DataFileException(String message) { 
        super(message); 
    }

    /**
     * Membuat exception file dengan cause asli dari operasi I/O.
     *
     * @param message pesan error file
     * @param cause exception asal yang dibungkus
     */
    public DataFileException(String message, Throwable cause) {
        super(message, cause);
    }
}
