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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Menghasilkan report admin dan memproses import data CSV.
 *
 * <p>Export menghasilkan file CSV/TXT untuk quest atau pengembara. Import
 * dilakukan secara transactional: semua baris divalidasi dulu, baru data
 * dimasukkan ke GameManager jika tidak ada error.</p>
 */
public class ReportGenerator implements DataExportable, DataImportable {
    private GameManager gameManager;

    /**
     * Membuat generator yang membaca dan menulis data melalui GameManager.
     *
     * @param gameManager sumber data aplikasi
     */
    public ReportGenerator(GameManager gameManager) {
        if (gameManager == null) {
            throw new IllegalArgumentException("GameManager tidak boleh kosong.");
        }
        this.gameManager = gameManager;
    }

    /**
     * Mengekspor daftar quest atau wanderer ke CSV.
     *
     * @param path path file CSV tujuan
     * @param mode jenis data yang diekspor
     * @throws DataFileException jika path, tipe file, atau mode tidak valid
     */
    @Override
    public void exportToCsv(String path, AdminIOCsvMode mode) throws DataFileException {
        DataFileWriter writer = DataFileWriter.forCsv(path);
        try {
            if (mode == AdminIOCsvMode.QUEST) {
                writer.writeRow(List.of("id", "name", "description", "difficulty",
                        "type", "monster_name", "minimum_level", "status"));
                for (Quest quest : gameManager.getQuests()) {
                    writer.writeRow(questCsvRow(quest));
                }
            } else if (mode == AdminIOCsvMode.WANDERER) {
                writer.writeRow(List.of("id", "name", "username", "level", "exp",
                        "coins", "current_hp", "max_hp", "attack", "defense"));
                for (Wanderer wanderer : gameManager.getWandererObjects()) {
                    writer.writeRow(wandererCsvRow(wanderer));
                }
            } else {
                throw new InvalidFormatException(path);
            }
        } finally {
            writer.close();
        }
    }

    /**
     * Mengekspor daftar quest atau wanderer ke TXT berbentuk tabel.
     *
     * @param path path file TXT tujuan
     * @param mode jenis data yang diekspor
     * @throws DataFileException jika path, tipe file, atau mode tidak valid
     */
    @Override
    public void exportToTxt(String path, AdminIOCsvMode mode) throws DataFileException {
        DataFileWriter writer = DataFileWriter.forTxt(path);
        try {
            ArrayList<String> headers = getReportHeaders(mode);
            ArrayList<ArrayList<String>> rows = getReportRows(mode);
            if (headers.isEmpty()) {
                throw new InvalidFormatException(path);
            }
            writer.writeHeader(headers);
            for (ArrayList<String> row : rows) {
                writer.writeRow(row);
            }
            writer.writeSeparator();
        } finally {
            writer.close();
        }
    }

    /**
     * Mengimpor data CSV ke sistem sesuai mode.
     *
     * @param path path file CSV sumber
     * @param mode jenis data yang diimpor
     * @throws BurhanQuestException jika file tidak valid, format salah, atau data duplikat
     */
    @Override
    public void importFromCsv(String path, AdminIOCsvMode mode) throws BurhanQuestException {
        DataFileReader reader = new DataFileReader(path);
        try {
            ArrayList<String> header = reader.readNext();
            if (header == null || mode == null) {
                throw new InvalidFormatException(path);
            }
            ArrayList<String> normalizedHeader = normalizeHeader(header);
            ArrayList<Quest> importedQuests = new ArrayList<>();
            ArrayList<Wanderer> importedWanderers = new ArrayList<>();
            ArrayList<String> pendingUsernames = new ArrayList<>();
            ArrayList<String> row = reader.readNext();
            while (row != null) {
                HashMap<String, String> values = toRowMap(normalizedHeader, row, path);
                if (mode == AdminIOCsvMode.QUEST) {
                    int questIdNumber = gameManager.nextQuestId() + importedQuests.size();
                    importedQuests.add(buildImportedQuest(values, path, questIdNumber));
                } else if (mode == AdminIOCsvMode.WANDERER) {
                    int wandererIdNumber = gameManager.nextWandererId() + importedWanderers.size();
                    Wanderer wanderer = buildImportedWanderer(values, path, wandererIdNumber, pendingUsernames);
                    importedWanderers.add(wanderer);
                    pendingUsernames.add(wanderer.getUsername());
                } else {
                    throw new InvalidFormatException(path);
                }
                row = reader.readNext();
            }
            for (Quest quest : importedQuests) {
                gameManager.addQuest(quest);
            }
            for (Wanderer wanderer : importedWanderers) {
                gameManager.addWanderer(wanderer);
            }
        } finally {
            reader.close();
        }
    }

    /**
     * Mengambil header report yang dipakai untuk preview terminal dan TXT.
     *
     * @param mode jenis data report
     * @return daftar nama kolom
     */
    public ArrayList<String> getReportHeaders(AdminIOCsvMode mode) {
        ArrayList<String> headers = new ArrayList<>();
        if (mode == AdminIOCsvMode.QUEST) {
            headers.add("ID Quest");
            headers.add("Nama Quest");
            headers.add("Deskripsi");
            headers.add("Kesulitan");
            headers.add("Tipe");
            headers.add("Monster");
            headers.add("Min. Lv");
            headers.add("Status");
        } else if (mode == AdminIOCsvMode.WANDERER) {
            headers.add("ID");
            headers.add("Nama");
            headers.add("Username");
            headers.add("Level");
            headers.add("Exp");
            headers.add("Koin");
            headers.add("HP Saat Ini");
            headers.add("HP Maksimal");
            headers.add("Attack");
            headers.add("Defense");
        }
        return headers;
    }

    /**
     * Mengambil isi report dalam bentuk baris tabel.
     *
     * @param mode jenis data report
     * @return daftar rows tanpa header
     */
    public ArrayList<ArrayList<String>> getReportRows(AdminIOCsvMode mode) {
        ArrayList<ArrayList<String>> rows = new ArrayList<>();
        if (mode == AdminIOCsvMode.QUEST) {
            for (Quest quest : gameManager.getQuests()) {
                rows.add(questCsvRow(quest));
            }
        } else if (mode == AdminIOCsvMode.WANDERER) {
            for (Wanderer wanderer : gameManager.getWandererObjects()) {
                rows.add(wandererCsvRow(wanderer));
            }
        }
        return rows;
    }

    /**
     * Membaca file CSV untuk ditampilkan sebagai preview sebelum import.
     *
     * @param path path file CSV
     * @return isi file sebagai tabel mentah
     * @throws DataFileException jika file tidak bisa dibaca atau format baris invalid
     */
    public ArrayList<ArrayList<String>> previewCsv(String path) throws DataFileException {
        DataFileReader reader = new DataFileReader(path);
        ArrayList<ArrayList<String>> table = new ArrayList<>();
        try {
            ArrayList<String> row = reader.readNext();
            if (row == null) {
                throw new InvalidFormatException(path);
            }
            table.add(row);
            row = reader.readNext();
            while (row != null) {
                table.add(row);
                row = reader.readNext();
            }
        } finally {
            reader.close();
        }
        return table;
    }

    /**
     * Membuat satu baris CSV/TXT untuk data quest.
     *
     * @param quest quest sumber data
     * @return baris kolom quest
     */
    private ArrayList<String> questCsvRow(Quest quest) {
        ArrayList<String> row = new ArrayList<>();
        row.add(quest.getId());
        row.add(quest.getName());
        row.add(quest.getDescription());
        row.add(quest.getDifficulty().getDisplayName());
        row.add(getReportQuestType(quest));
        row.add(quest.getMonster().getName());
        row.add(String.valueOf(quest.getMinLevel()));
        row.add(quest.getStatus().getDisplayName());
        return row;
    }

    /**
     * Membuat baris TXT quest (format kolom sama dengan CSV).
     *
     * @param quest quest sumber data
     * @return baris kolom quest
     */
    private ArrayList<String> questTxtRow(Quest quest) {
        return questCsvRow(quest);
    }

    /**
     * Membuat satu baris CSV/TXT untuk data pengembara.
     *
     * @param wanderer pengembara sumber data
     * @return baris kolom pengembara
     */
    private ArrayList<String> wandererCsvRow(Wanderer wanderer) {
        ArrayList<String> row = new ArrayList<>();
        row.add(wanderer.getId());
        row.add(wanderer.getName());
        row.add(wanderer.getUsername());
        row.add(String.valueOf(wanderer.getLevel()));
        row.add(String.valueOf(wanderer.getExp()));
        row.add(String.valueOf(wanderer.getCoins()));
        row.add(BurhanQuestUtils.formatDouble(wanderer.getCurrentHp()));
        row.add(BurhanQuestUtils.formatDouble(wanderer.getMaxHp()));
        row.add(BurhanQuestUtils.formatDouble(wanderer.getAttack()));
        row.add(BurhanQuestUtils.formatDouble(wanderer.getDefense()));
        return row;
    }

    /**
     * Membuat baris TXT pengembara (format kolom sama dengan CSV).
     *
     * @param wanderer pengembara sumber data
     * @return baris kolom pengembara
     */
    private ArrayList<String> wandererTxtRow(Wanderer wanderer) {
        return wandererCsvRow(wanderer);
    }

    /**
     * Membuat objek Quest dari satu baris CSV yang sudah dipetakan.
     */
    private Quest buildImportedQuest(HashMap<String, String> values, String path, int questIdNumber)
            throws DataFileException {
        String name = firstValue(values, "name", "nama");
        String description = values.getOrDefault("description", "");
        Difficulty difficulty = Difficulty.fromString(values.get("difficulty"));
        String type = values.get("type");
        String monsterReference = firstValue(values, "monsterName", "monster");
        if (BurhanQuestUtils.isBlank(name) || difficulty == null
                || BurhanQuestUtils.isBlank(type) || BurhanQuestUtils.isBlank(monsterReference)) {
            throw new InvalidFormatException(path);
        }
        Monster monster = gameManager.findMonsterById(monsterReference);
        if (monster == null) {
            monster = gameManager.findMonsterByName(monsterReference);
        }
        if (monster == null) {
            throw new InvalidFormatException(path);
        }
        int bonusExp = parseInt(values.get("bonusExp"), path);
        int bonusCoin = parseInt(values.get("bonusCoin"), path);
        if (bonusExp < 0 || bonusCoin < 0) {
            throw new InvalidFormatException(path);
        }
        Quest quest;
        String normalizedType = type.trim().toLowerCase();
        if (normalizedType.equals("bounty")) {
            quest = new BountyQuest(questIdNumber, name, description,
                    difficulty, monster, difficulty.getMinWandererLevel(), bonusExp, bonusCoin);
        } else if (normalizedType.equals("daily") || normalizedType.equals("harian")) {
            if (bonusExp != 0 || bonusCoin != 0) {
                throw new InvalidFormatException(path);
            }
            quest = new DailyQuest(questIdNumber, name, description,
                    difficulty, monster, difficulty.getMinWandererLevel());
        } else if (normalizedType.equals("regular") || normalizedType.equals("biasa")) {
            if (bonusExp != 0 || bonusCoin != 0) {
                throw new InvalidFormatException(path);
            }
            quest = new RegularQuest(questIdNumber, name, description,
                    difficulty, monster, difficulty.getMinWandererLevel());
        } else {
            throw new InvalidFormatException(path);
        }
        return quest;
    }

    /**
     * Membuat objek Wanderer dari satu baris CSV yang sudah dipetakan.
     */
    private Wanderer buildImportedWanderer(HashMap<String, String> values, String path,
                                           int wandererIdNumber, ArrayList<String> pendingUsernames)
            throws BurhanQuestException {
        String name = firstValue(values, "name", "nama");
        String username = values.get("username");
        String password = values.get("password");
        double maxHp = parseDouble(values.get("maxHp"), path);
        double attack = parseDouble(values.get("attack"), path);
        double defense = parseDouble(values.get("defense"), path);
        if (maxHp <= 0) {
            maxHp = 1;
        }
        if (BurhanQuestUtils.isBlank(name) || BurhanQuestUtils.isBlank(username)
                || BurhanQuestUtils.isBlank(password) || attack <= 0 || defense <= 0) {
            throw new InvalidFormatException(path);
        }
        if (gameManager.isUsernameTaken(username) || isPendingUsernameTaken(pendingUsernames, username)) {
            throw new DuplicateWandererException(username);
        }
        Wanderer wanderer = gameManager.createWanderer(wandererIdNumber, firstValue(values, "job", "jobClass"),
                name, username, password, maxHp, attack, defense);
        int level = parseInt(values.get("level"), path);
        int exp = parseInt(values.get("exp"), path);
        int coins = parseInt(values.get("coins"), path);
        wanderer.setProgress(Math.max(1, level), Math.max(0, exp), Math.max(0, coins));
        double currentHp = parseDouble(values.get("currentHp"), path);
        if (currentHp > 0) {
            wanderer.setCurrentHp(currentHp);
        }
        return wanderer;
    }

    /**
     * Menormalkan header CSV ke camelCase sebelum dipetakan.
     *
     * @param header header asli dari file
     * @return header yang sudah dinormalisasi
     */
    private ArrayList<String> normalizeHeader(ArrayList<String> header) {
        ArrayList<String> normalized = new ArrayList<>();
        for (String column : header) {
            normalized.add(toCamelCase(column));
        }
        return normalized;
    }

    /**
     * Memetakan header dan row menjadi pasangan key-value.
     *
     * @param header daftar nama kolom
     * @param row nilai kolom pada satu baris
     * @param path path file untuk konteks error
     * @return map nilai berdasarkan nama kolom
     * @throws InvalidFormatException bila jumlah kolom tidak cocok
     */
    private HashMap<String, String> toRowMap(ArrayList<String> header, ArrayList<String> row, String path)
            throws InvalidFormatException {
        if (row.size() != header.size()) {
            throw new InvalidFormatException(path);
        }
        HashMap<String, String> values = new HashMap<>();
        for (int i = 0; i < header.size(); i++) {
            values.put(header.get(i), row.get(i));
        }
        return values;
    }

    /**
     * Mengonversi satu nama kolom ke konvensi camelCase.
     *
     * @param raw nama kolom mentah
     * @return nama kolom camelCase
     */
    private String toCamelCase(String raw) {
        String value = raw == null ? "" : raw.trim();
        String result = "";
        if (value.contains("_") || value.contains("-") || value.contains(" ")) {
            String[] parts = value.split("[_\\-\\s]+");
            for (int i = 0; i < parts.length; i++) {
                String part = parts[i];
                if (i == 0) {
                    result += part.toLowerCase();
                } else if (!part.isEmpty()) {
                    result += BurhanQuestUtils.capitalize(part);
                }
            }
        } else if (!value.isEmpty()) {
            result = value.substring(0, 1).toLowerCase() + value.substring(1);
        }
        return result;
    }

    /**
     * Mem-parse integer dengan fallback exception format file.
     *
     * @param raw nilai teks
     * @param path path file untuk konteks error
     * @return nilai integer hasil parse
     * @throws InvalidFormatException bila format angka tidak valid
     */
    private int parseInt(String raw, String path) throws InvalidFormatException {
        int value = 0;
        if (!BurhanQuestUtils.isBlank(raw)) {
            try {
                value = Integer.parseInt(raw.trim());
            } catch (NumberFormatException e) {
                throw new InvalidFormatException(path);
            }
        }
        return value;
    }

    /**
     * Mem-parse desimal dengan fallback exception format file.
     *
     * @param raw nilai teks
     * @param path path file untuk konteks error
     * @return nilai double hasil parse
     * @throws InvalidFormatException bila format angka tidak valid
     */
    private double parseDouble(String raw, String path) throws InvalidFormatException {
        double value = 0;
        if (!BurhanQuestUtils.isBlank(raw)) {
            try {
                value = Double.parseDouble(raw.trim());
            } catch (NumberFormatException e) {
                throw new InvalidFormatException(path);
            }
        }
        return value;
    }

    /**
     * Mengambil nilai pertama yang tersedia dari daftar key kandidat.
     *
     * @param values map nilai baris
     * @param keys urutan key alternatif
     * @return nilai pertama yang ditemukan, atau {@code null}
     */
    private String firstValue(HashMap<String, String> values, String... keys) {
        String result = null;
        for (String key : keys) {
            if (result == null && values.containsKey(key)) {
                result = values.get(key);
            }
        }
        return result;
    }

    /**
     * Memeriksa duplikasi username di batch import yang belum disimpan.
     *
     * @param pendingUsernames daftar username sementara
     * @param username username yang dicek
     * @return {@code true} jika sudah ada duplikasi
     */
    private boolean isPendingUsernameTaken(ArrayList<String> pendingUsernames, String username) {
        boolean taken = false;
        for (String pendingUsername : pendingUsernames) {
            if (pendingUsername.equalsIgnoreCase(username)) {
                taken = true;
            }
        }
        return taken;
    }

    /**
     * Menormalkan nama tipe quest untuk kebutuhan output report.
     *
     * @param quest quest sumber data
     * @return nama tipe quest terformat output
     */
    private String getReportQuestType(Quest quest) {
        String type = quest.getQuestType();
        if (type.equalsIgnoreCase("Daily")) {
            type = "Harian";
        } else if (type.equalsIgnoreCase("Regular")) {
            type = "Biasa";
        }
        return type;
    }
}
