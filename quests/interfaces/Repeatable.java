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
 * Kontrak untuk quest yang bisa diselesaikan lebih dari sekali.
 */
public interface Repeatable {
    /**
     * Mengembalikan counter pengulangan ke nilai awal.
     */
    void reset();

    /**
     * Mengembalikan jumlah penyelesaian dalam periode aktif.
     *
     * @return jumlah penyelesaian dalam periode aktif
     */
    int getTimesCompleted();
}
