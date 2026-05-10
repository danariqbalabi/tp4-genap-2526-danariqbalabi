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

import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.util.List;

/**
 * Writer untuk membuat file report CSV atau TXT.
 *
 * <p>Format TXT memakai tabel fixed-width sederhana, sedangkan CSV memakai
 * baris koma tanpa manipulasi tambahan.</p>
 */
public class DataFileWriter {
    private PrintWriter printWriter;
    private DataFileWriterMode mode;
    private int lastColumnCount;

    private DataFileWriter(String path, DataFileWriterMode mode) throws DataFileException {
        this.mode = mode;
        try {
            printWriter = new PrintWriter(path);
        } catch (FileNotFoundException e) {
            throw new DataFileException("File tidak dapat ditulis: " + path, e);
        }
    }

    /**
     * Membuat writer khusus file CSV.
     *
     * @param path path file CSV tujuan
     * @return writer yang siap dipakai
     * @throws DataFileException jika ekstensi file bukan CSV atau file gagal dibuat
     */
    public static DataFileWriter forCsv(String path) throws DataFileException {
        if (!path.toLowerCase().endsWith(".csv")) {
            throw new InvalidFileTypeException(path);
        }
        return new DataFileWriter(path, DataFileWriterMode.CSV);
    }

    /**
     * Membuat writer khusus file TXT.
     *
     * @param path path file TXT tujuan
     * @return writer yang siap dipakai
     * @throws DataFileException jika ekstensi file bukan TXT atau file gagal dibuat
     */
    public static DataFileWriter forTxt(String path) throws DataFileException {
        if (!path.toLowerCase().endsWith(".txt")) {
            throw new InvalidFileTypeException(path);
        }
        return new DataFileWriter(path, DataFileWriterMode.TXT);
    }

    /**
     * Menulis satu baris data sesuai mode writer.
     *
     * @param row nilai kolom yang akan ditulis
     */
    public void writeRow(List<String> row) {
        lastColumnCount = row.size();
        if (mode == DataFileWriterMode.CSV) {
            printWriter.println(String.join(",", row));
        } else {
            StringBuilder builder = new StringBuilder("|");
            for (String cell : row) {
                builder.append(" ").append(pad(cell, 18)).append(" |");
            }
            printWriter.println(builder);
        }
    }

    /**
     * Menulis header tabel TXT lengkap dengan separator awal.
     *
     * @param headers nama kolom
     */
    public void writeHeader(List<String> headers) {
        if (mode == DataFileWriterMode.TXT) {
            lastColumnCount = headers.size();
            writeSeparator(headers.size());
            writeRow(headers);
            writeSeparator(headers.size());
        }
    }

    /**
     * Menulis separator penutup untuk tabel TXT berdasarkan jumlah kolom terakhir.
     */
    public void writeSeparator() {
        if (lastColumnCount > 0) {
            writeSeparator(lastColumnCount);
        }
    }

    /**
     * Melakukan flush dan menutup file.
     */
    public void close() {
        if (printWriter != null) {
            printWriter.flush();
            printWriter.close();
        }
    }

    /**
     * Menulis separator tabel TXT berdasarkan jumlah kolom.
     *
     * @param columns jumlah kolom aktif
     */
    private void writeSeparator(int columns) {
        if (mode == DataFileWriterMode.TXT) {
            StringBuilder builder = new StringBuilder("+");
            for (int i = 0; i < columns; i++) {
                builder.append("--------------------+");
            }
            printWriter.println(builder);
        }
    }

    /**
     * Melakukan padding kanan untuk satu sel tabel TXT.
     *
     * @param value isi sel
     * @param width lebar kolom target
     * @return teks hasil padding
     */
    private String pad(String value, int width) {
        String text = value == null ? "" : value;
        if (text.length() > width) {
            text = text.substring(0, width);
        }
        return String.format("%-" + width + "s", text);
    }
}
