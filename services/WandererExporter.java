package services;

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
import java.util.ArrayList;

/**
 * Exporter khusus laporan data diri pengembara.
 *
 * <p>Class ini hanya mendukung TXT karena fitur pengembara pada soal meminta
 * output data diri yang mudah dibaca, bukan CSV tabel admin.</p>
 */
public class WandererExporter implements DataExportable {
    private GameManager gameManager;
    private Wanderer wanderer;

    /**
     * Membuat exporter yang memilih pengembara pertama dari GameManager.
     *
     * @param gameManager sumber data pengembara
     */
    public WandererExporter(GameManager gameManager) {
        this(gameManager, null);
    }

    /**
     * Membuat exporter untuk pengembara tertentu.
     *
     * @param gameManager sumber data aplikasi
     * @param wanderer pengembara yang akan diekspor
     */
    public WandererExporter(GameManager gameManager, Wanderer wanderer) {
        if (gameManager == null) {
            throw new IllegalArgumentException("GameManager tidak boleh kosong.");
        }
        this.gameManager = gameManager;
        this.wanderer = wanderer;
    }

    /**
     * CSV tidak didukung untuk laporan data diri pengembara.
     *
     * @param path path file yang diminta
     * @param mode mode export
     * @throws DataFileException selalu dilempar karena CSV tidak tersedia
     */
    @Override
    public void exportToCsv(String path, AdminIOCsvMode mode) throws DataFileException {
        throw new InvalidFormatException(path);
    }

    /**
     * Mengekspor laporan data diri pengembara ke file TXT.
     *
     * @param path path file TXT tujuan
     * @param mode harus bernilai {@link AdminIOCsvMode#WANDERER}
     * @throws DataFileException jika tipe file, mode, atau proses tulis gagal
     */
    @Override
    public void exportToTxt(String path, AdminIOCsvMode mode) throws DataFileException {
        if (!path.toLowerCase().endsWith(".txt")) {
            throw new InvalidFileTypeException(path);
        }
        if (mode != AdminIOCsvMode.WANDERER) {
            throw new InvalidFormatException(path);
        }
        Wanderer selected = resolveWanderer(path);
        try (PrintWriter writer = new PrintWriter(path)) {
            for (String line : buildReportLines(selected)) {
                writer.println(line);
            }
        } catch (FileNotFoundException e) {
            throw new DataFileException("File tidak dapat ditulis: " + path, e);
        }
    }

    /**
     * Mengambil isi laporan tanpa menulis file, dipakai untuk preview terminal.
     *
     * @param path path yang dipakai untuk validasi konteks error
     * @return baris laporan data diri
     * @throws InvalidFormatException jika tidak ada pengembara yang bisa dipilih
     */
    public ArrayList<String> getReportLines(String path) throws InvalidFormatException {
        return buildReportLines(resolveWanderer(path));
    }

    /**
     * Menentukan pengembara target untuk laporan.
     *
     * @param path path file untuk konteks error
     * @return pengembara yang dipilih
     * @throws InvalidFormatException bila tidak ada pengembara tersedia
     */
    private Wanderer resolveWanderer(String path) throws InvalidFormatException {
        Wanderer selected = wanderer;
        if (selected == null) {
            ArrayList<Wanderer> wanderers = gameManager.getWandererObjects();
            if (wanderers.isEmpty()) {
                throw new InvalidFormatException(path);
            }
            selected = wanderers.get(0);
        }
        return selected;
    }

    /**
     * Menyusun isi laporan data diri pengembara dalam bentuk baris teks.
     *
     * @param selected pengembara target
     * @return daftar baris laporan
     */
    private ArrayList<String> buildReportLines(Wanderer selected) {
        ArrayList<String> lines = new ArrayList<>();
        lines.add("========================================");
        lines.add("LAPORAN DATA PENGEMBARA");
        lines.add("========================================");
        lines.add("ID Pengembara : " + selected.getId());
        lines.add("Nama           : " + selected.getName());
        lines.add("Username       : " + selected.getUsername());
        lines.add("Job            : " + selected.getJobDisplayName());
        lines.add("Level          : " + selected.getLevel());
        lines.add("Exp            : " + selected.getExp() + " poin exp");
        lines.add("Koin           : " + selected.getCoins() + " koin");
        lines.add("HP             : " + BurhanQuestUtils.formatDouble(selected.getCurrentHp())
                + "/" + BurhanQuestUtils.formatDouble(selected.getMaxHp()));
        lines.add("Attack         : " + BurhanQuestUtils.formatDouble(selected.getAttack()));
        lines.add("Defense        : " + BurhanQuestUtils.formatDouble(selected.getDefense()));
        lines.add("----------------------------------------");
        lines.add("Riwayat Quest :");
        ArrayList<String> history = selected.getQuestHistory();
        if (history.isEmpty()) {
            lines.add("Belum ada riwayat quest.");
        }
        for (String line : history) {
            lines.add(line);
        }
        lines.add("========================================");
        return lines;
    }
}
