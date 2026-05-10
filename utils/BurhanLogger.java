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

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.time.Instant;

/**
 * Singleton logger untuk menulis histori sistem ke file log.
 *
 * <p>Logger tidak mencetak apa pun ke terminal. Semua event disimpan ke file
 * agar alur I/O interaktif tetap rapi dan sesuai pembagian tanggung jawab
 * class.</p>
 */
public class BurhanLogger {
    private static BurhanLogger instance;
    private static final String DEFAULT_FILE_PATH = "guild_history.log";
    private static final String TEST_FILE_PATH = System.getProperty("java.io.tmpdir")
            + java.io.File.separator + "guild_history.test.log";
    private String filePath = isRunningUnderTest() ? TEST_FILE_PATH : DEFAULT_FILE_PATH;

    private BurhanLogger() {}

    /**
     * Mengambil satu-satunya instance logger.
     *
     * @return instance logger global
     */
    public static BurhanLogger getInstance() {
        if (instance == null) {
            instance = new BurhanLogger();
        }
        return instance;
    }

    /**
     * Mengganti lokasi file log.
     *
     * @param path path file tujuan log
     */
    public void setFilePath(String path) {
        if (path == null || path.trim().isEmpty()) {
            this.filePath = isRunningUnderTest() ? TEST_FILE_PATH : DEFAULT_FILE_PATH;
        } else {
            this.filePath = path;
        }
    }

    /**
     * Menulis event aplikasi tanpa detail exception.
     *
     * @param category kategori log
     * @param actor aktor yang memicu event
     * @param action deskripsi aksi yang terjadi
     */
    public void log(LogCategory category, String actor, String action) {
        String message = "[" + Instant.now() + "] [" + category.name() + "] ["
                + actor + "] - " + action;
        writeToFile(message);
    }

    /**
     * Menulis event aplikasi yang terkait exception.
     *
     * @param category kategori log
     * @param actor aktor yang memicu event
     * @param action deskripsi aksi yang terjadi
     * @param e exception yang ingin dicatat tipenya
     */
    public void log(LogCategory category, String actor, String action, Exception e) {
        String message = "[" + Instant.now() + "] [" + category.name() + "] ["
                + actor + "] - " + action + " (" + e.getClass().getSimpleName() + ")";
        writeToFile(message);
    }

    /**
     * Menulis satu baris log ke file target menggunakan append mode.
     *
     * @param message pesan log siap tulis
     */
    private void writeToFile(String message) {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(filePath, true))) {
            writer.write(message);
            writer.newLine();
        } catch (IOException e) {
            // Logger tidak mencetak ke terminal agar I/O interaktif tetap terpusat di Main/BattleManager.
        }
    }

    /**
     * Mendeteksi apakah runtime saat ini sedang menjalankan unit test JUnit.
     *
     * @return {@code true} bila command Java menunjukkan eksekusi JUnit
     */
    private static boolean isRunningUnderTest() {
        String command = System.getProperty("sun.java.command", "");
        return command.contains("org.junit.");
    }
}
