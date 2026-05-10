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
 * Exception ketika isi file tidak mengikuti format data BurhanQuest.
 */
public class InvalidFormatException extends DataFileException {
    private static final long serialVersionUID = 1L;

    /**
     * Membuat exception format file tidak valid.
     *
     * @param path path file yang formatnya bermasalah
     */
    public InvalidFormatException(String path) {
        super("Format file tidak valid untuk path: " + path);
    }
}
