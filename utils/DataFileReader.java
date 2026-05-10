package utils;

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

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Scanner;

/**
 * Reader sederhana untuk membaca file CSV baris demi baris.
 *
 * <p>Class ini dipakai oleh fitur import. Scanner di sini hanya membaca file,
 * bukan input keyboard, sehingga tetap sesuai pengecualian Subtask 5.</p>
 */
public class DataFileReader {
    private Scanner scanner;

    /**
     * Membuka file CSV untuk dibaca.
     *
     * @param path path file CSV
     * @throws DataFileException jika ekstensi salah atau file tidak ditemukan
     */
    public DataFileReader(String path) throws DataFileException {
        if (!path.toLowerCase().endsWith(".csv")) {
            throw new InvalidFileTypeException(path);
        }
        try {
            scanner = new Scanner(new File(path));
        } catch (FileNotFoundException e) {
            throw new DataFileException("File tidak ditemukan: " + path, e);
        }
    }

    /**
     * Membaca satu baris CSV dan memisahkan nilainya berdasarkan koma.
     *
     * @return daftar nilai pada baris berikutnya, atau {@code null} jika EOF
     * @throws DataFileException jika ditemukan baris kosong atau format invalid
     */
    public ArrayList<String> readNext() throws DataFileException {
        ArrayList<String> row = null;
        if (scanner.hasNextLine()) {
            String line = scanner.nextLine();
            if (line.trim().isEmpty()) {
                throw new InvalidFormatException("baris kosong");
            }
            row = new ArrayList<>();
            String[] values = line.split(",", -1);
            for (String value : values) {
                row.add(value.trim());
            }
        }
        return row;
    }

    /**
     * Menutup scanner file setelah proses baca selesai.
     */
    public void close() {
        if (scanner != null) {
            scanner.close();
        }
    }
}
