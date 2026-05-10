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
 * Exception ketika username pengembara sudah dipakai.
 */
public class DuplicateWandererException extends BurhanQuestException {
    private static final long serialVersionUID = 1L;

    /** Username yang memicu exception duplikasi. */
    private String username;

    /**
     * Membuat exception untuk username duplikat.
     *
     * @param username username yang sudah terdaftar
     */
    public DuplicateWandererException(String username) {
        super("Username '" + username + "' sudah digunakan.");
        this.username = username;
    }

    /**
     * Mengembalikan username yang menyebabkan duplikasi.
     *
     * @return username yang menyebabkan duplikasi
     */
    public String getUsername() {
        return username;
    }
}
