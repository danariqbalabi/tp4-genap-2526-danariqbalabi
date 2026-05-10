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
 * Base exception untuk seluruh exception khusus BurhanQuest.
 *
 * <p>Dengan satu parent exception, caller bisa menangkap error aplikasi secara
 * umum tanpa kehilangan tipe exception yang lebih spesifik.</p>
 */
public class BurhanQuestException extends Exception {
    private static final long serialVersionUID = 1L;

    /**
     * Membuat exception dengan pesan error.
     *
     * @param message pesan error yang menjelaskan penyebab kegagalan
     */
    public BurhanQuestException(String message) {
        super(message);
    }  

    /**
     * Membuat exception dengan pesan error dan cause asli.
     *
     * @param message pesan error yang menjelaskan penyebab kegagalan
     * @param cause exception asal yang dibungkus
     */
    public BurhanQuestException(String message, Throwable cause) {
        super(message, cause);
    }
}
