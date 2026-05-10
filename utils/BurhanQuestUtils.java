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

/**
 * Catatan implementasi:
 *
 * Class helper ini dipakai solusi untuk merapikan operasi utilitas umum
 * (format angka, validasi string, dan render tabel teks) agar kode utama
 * seperti Main/GameManager/BattleManager tetap fokus ke alur fitur.
 *
 * Pemakaian helper ini bersifat pilihan desain. Selama kebutuhan soal
 * terpenuhi dan pemisahan tanggung jawab class dijaga, implementasi boleh
 * menggunakan pendekatan lain (misalnya utility lokal per class atau built-in
 * Java langsung tanpa class helper khusus).
 */
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.ArrayList;
import java.util.Locale;

/**
 * Utility stateless untuk format angka, validasi string, dan pembuatan tabel.
 *
 * Class ini sengaja tidak melakukan print langsung ke terminal. Method
 * table builder mengembalikan daftar baris agar terminal I/O tetap berada di
 * Main atau BattleManager.
 */
public final class BurhanQuestUtils {
    private static final DecimalFormat DECIMAL_FORMAT =
            new DecimalFormat("0.##", DecimalFormatSymbols.getInstance(Locale.US));

    private BurhanQuestUtils() {
    }

    /**
     * Memformat angka desimal dengan pemisah titik dan maksimal dua digit
     * setelah koma.
     *
     * @param value angka yang akan diformat
     * @return angka dalam bentuk string yang stabil untuk I/O
     */
    public static String formatDouble(double value) {
        double normalized = Math.abs(value) < 0.0000001 ? 0 : value;
        return DECIMAL_FORMAT.format(normalized);
    }

    /**
     * Mengubah string menjadi kapital di huruf pertama dan huruf kecil di sisa
     * katanya.
     *
     * @param value teks sumber
     * @return teks yang sudah dikapitalisasi, atau string kosong jika input kosong
     */
    public static String capitalize(String value) {
        if (value == null || value.isEmpty()) {
            return "";
        }
        return value.substring(0, 1).toUpperCase() + value.substring(1).toLowerCase();
    }

    /**
     * Mengecek apakah string null, kosong, atau hanya berisi spasi.
     *
     * @param value teks yang dicek
     * @return {@code true} jika tidak ada isi bermakna
     */
    public static boolean isBlank(String value) {
        return value == null || value.trim().isEmpty();
    }

    /**
     * Membuat representasi tabel ASCII dari header dan rows.
     *
     * @param headers nama kolom tabel
     * @param rows isi tabel tanpa header
     * @return daftar baris tabel siap cetak
     */
    public static ArrayList<String> buildTable(ArrayList<String> headers, ArrayList<ArrayList<String>> rows) {
        ArrayList<String> lines = new ArrayList<>();
        ArrayList<Integer> widths = getColumnWidths(headers, rows);
        lines.add(tableSeparator(widths));
        lines.add(tableRow(headers, widths));
        lines.add(tableSeparator(widths));
        for (ArrayList<String> row : rows) {
            lines.add(tableRow(row, widths));
        }
        lines.add(tableSeparator(widths));
        return lines;
    }

    /**
     * Membuat representasi tabel ASCII dari struktur tabel yang baris pertamanya
     * berisi header.
     *
     * @param table data tabel dengan header di baris pertama
     * @return daftar baris tabel siap cetak
     */
    public static ArrayList<String> buildTable(ArrayList<ArrayList<String>> table) {
        ArrayList<String> lines = new ArrayList<>();
        if (table.isEmpty()) {
            return lines;
        }
        ArrayList<String> headers = table.get(0);
        ArrayList<ArrayList<String>> rows = new ArrayList<>();
        for (int i = 1; i < table.size(); i++) {
            rows.add(table.get(i));
        }
        return buildTable(headers, rows);
    }

    /**
     * Menghitung lebar kolom optimal dari header dan seluruh isi baris.
     *
     * @param headers daftar header kolom
     * @param rows data baris tabel
     * @return daftar lebar untuk setiap kolom
     */
    private static ArrayList<Integer> getColumnWidths(ArrayList<String> headers,
                                                       ArrayList<ArrayList<String>> rows) {
        ArrayList<Integer> widths = new ArrayList<>();
        for (String header : headers) {
            widths.add(Math.max(3, header.length()));
        }
        for (ArrayList<String> row : rows) {
            for (int i = 0; i < headers.size(); i++) {
                String value = i < row.size() ? row.get(i) : "";
                int width = widths.get(i);
                if (value.length() > width) {
                    widths.set(i, value.length());
                }
            }
        }
        return widths;
    }

    /**
     * Membentuk garis separator tabel ASCII.
     *
     * @param widths lebar tiap kolom
     * @return satu baris separator tabel
     */
    private static String tableSeparator(ArrayList<Integer> widths) {
        StringBuilder builder = new StringBuilder("+");
        for (int width : widths) {
            builder.append("-".repeat(width + 2)).append("+");
        }
        return builder.toString();
    }

    /**
     * Membentuk satu baris konten tabel ASCII.
     *
     * @param row data sel pada baris
     * @param widths lebar tiap kolom
     * @return satu baris tabel siap cetak
     */
    private static String tableRow(ArrayList<String> row, ArrayList<Integer> widths) {
        StringBuilder builder = new StringBuilder("|");
        for (int i = 0; i < widths.size(); i++) {
            String value = i < row.size() ? row.get(i) : "";
            builder.append(" ").append(padRight(value, widths.get(i))).append(" |");
        }
        return builder.toString();
    }

    /**
     * Menambahkan spasi di kanan teks hingga mencapai lebar target.
     *
     * @param value teks sumber
     * @param width lebar akhir yang diinginkan
     * @return teks hasil padding kanan
     */
    private static String padRight(String value, int width) {
        return value + " ".repeat(Math.max(0, width - value.length()));
    }
}
