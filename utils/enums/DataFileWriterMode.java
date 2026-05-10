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
 * Format file yang didukung oleh DataFileWriter.
 */
public enum DataFileWriterMode {
    /** File comma-separated values. */
    CSV,

    /** File teks tabel fixed-width. */
    TXT
}
