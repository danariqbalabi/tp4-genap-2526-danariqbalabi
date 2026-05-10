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
 * Kontrak untuk objek yang memiliki status penyelesaian.
 */
public interface Completable {
    /**
     * Mengembalikan {@code true} jika objek masih bisa diselesaikan.
     *
     * @return {@code true} jika objek masih bisa diselesaikan
     */
    boolean isCompletable();

    /**
     * Menandai objek sebagai selesai sesuai aturan implementasinya.
     */
    void complete();

    /**
     * Alias yang disediakan agar kode lama yang memakai istilah available tetap
     * bisa membaca status quest.
     *
     * @return {@code true} jika objek tersedia untuk diambil
     */
    default boolean isAvailable() {
        return isCompletable();
    }
}
