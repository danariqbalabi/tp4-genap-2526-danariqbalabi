package utils.interfaces;

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
 * Kontrak untuk class yang bisa membaca data aplikasi dari file CSV.
 */
public interface DataImportable {
    /**
     * Mengimpor data dari file CSV.
     *
     * @param path path sumber file CSV
     * @param mode jenis data yang diimpor
     * @throws BurhanQuestException jika file, format, atau isi data tidak valid
     */
    void importFromCsv(String path, AdminIOCsvMode mode) throws BurhanQuestException;
}
