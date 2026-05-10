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
 * Exception ketika ekstensi file tidak sesuai dengan format yang diminta.
 */
public class InvalidFileTypeException extends DataFileException {
    private static final long serialVersionUID = 1L;

    /**
     * Membuat exception tipe file tidak valid.
     *
     * @param path path file yang ekstensinya salah
     */
    public InvalidFileTypeException(String path) {
        super("Tipe file tidak valid untuk path: " + path);
    }
}
