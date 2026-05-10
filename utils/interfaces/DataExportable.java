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
 * Kontrak untuk class yang bisa menulis data aplikasi ke file.
 */
public interface DataExportable {
    /**
     * Mengekspor data ke format CSV.
     *
     * @param path path tujuan file CSV
     * @param mode jenis data yang diekspor
     * @throws DataFileException jika path atau proses penulisan file gagal
     */
    void exportToCsv(String path, AdminIOCsvMode mode) throws DataFileException;

    /**
     * Mengekspor data ke format TXT.
     *
     * @param path path tujuan file TXT
     * @param mode jenis data yang diekspor
     * @throws DataFileException jika path atau proses penulisan file gagal
     */
    void exportToTxt(String path, AdminIOCsvMode mode) throws DataFileException;
}
