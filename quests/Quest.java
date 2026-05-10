package quests;

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
 * Base class abstrak untuk semua jenis quest.
 *
 * <p>Quest menyimpan identitas, difficulty, monster lawan, level minimum, dan
 * status. Subclass menentukan tipe quest serta aturan penyelesaian/reward
 * tambahan jika ada.</p>
 */
public abstract class Quest implements Completable {
    private String id;
    private String name;
    private String description;
    private Difficulty difficulty;
    private Monster monster;
    private int minLevel;
    private QuestStatus status;

    /**
     * Membuat quest baru dengan status awal tersedia.
     *
     * @param idNumber nomor urut yang dipakai untuk membentuk ID seperti Q1
     * @param name nama quest
     * @param description deskripsi quest
     * @param difficulty tingkat kesulitan quest
     * @param monster monster yang menjadi lawan quest
     * @param minLevel level minimum eksplisit; akan dinaikkan jika di bawah difficulty
     */
    public Quest(int idNumber, String name, String description,
                 Difficulty difficulty, Monster monster, int minLevel) {
        if (idNumber <= 0) {
            throw new IllegalArgumentException("Nomor ID quest harus positif.");
        }
        if (BurhanQuestUtils.isBlank(name)) {
            throw new IllegalArgumentException("Nama quest tidak boleh kosong.");
        }
        if (difficulty == null) {
            throw new IllegalArgumentException("Tingkat kesulitan quest tidak valid.");
        }
        if (monster == null) {
            throw new IllegalArgumentException("Monster quest tidak boleh kosong.");
        }
        this.id = "Q" + idNumber;
        this.name = name.trim();
        this.description = description == null ? "" : description.trim();
        this.difficulty = difficulty;
        this.monster = monster;
        this.minLevel = Math.max(minLevel, difficulty.getMinWandererLevel());
        this.status = QuestStatus.TERSEDIA;
    }

    /**
     * Mengembalikan nama tipe quest, misalnya Regular, Daily, atau Bounty.
     *
     * @return nama tipe quest, misalnya Regular, Daily, atau Bounty
     */
    public abstract String getQuestType();

    /**
     * Mengembalikan total exp reward dari monster dan bonus quest.
     *
     * @return total exp reward dari monster dan bonus quest
     */
    public int getExpReward() {
        return monster.getExpReward() + getBonusExp();
    }

    /**
     * Mengembalikan total coin reward dari monster dan bonus quest.
     *
     * @return total coin reward dari monster dan bonus quest
     */
    public int getCoinReward() {
        return monster.getCoinReward() + getBonusCoin();
    }

    /**
     * Mengembalikan bonus exp dari subclass, default 0.
     *
     * @return bonus exp dari subclass, default 0
     */
    protected int getBonusExp() { return 0; }

    /**
     * Mengembalikan bonus coin dari subclass, default 0.
     *
     * @return bonus coin dari subclass, default 0
     */
    protected int getBonusCoin() { return 0; }

    /**
     * Mengembalikan {@code true} jika quest masih tersedia.
     *
     * @return {@code true} jika quest masih tersedia
     */
    @Override
    public boolean isCompletable() { return status == QuestStatus.TERSEDIA; }

    /**
     * Menandai quest sebagai selesai.
     */
    @Override
    public void complete() {
        status = QuestStatus.SELESAI;
    }

    /**
     * Mengembalikan status quest menjadi tersedia.
     */
    public void resetToAvailable() {
        status = QuestStatus.TERSEDIA;
    }

    /**
     * Hook saat hari berganti. Subclass dapat override jika punya state harian.
     */
    public void onNewDay() {
    }

    /**
     * Mengembalikan ID quest.
     *
     * @return ID quest
     */
    public String getId() { return id; }

    /**
     * Mengembalikan nama quest.
     *
     * @return nama quest
     */
    public String getName() { return name; }

    /**
     * Mengembalikan deskripsi quest.
     *
     * @return deskripsi quest
     */
    public String getDescription() { return description; }

    /**
     * Mengembalikan difficulty quest.
     *
     * @return difficulty quest
     */
    public Difficulty getDifficulty() { return difficulty; }

    /**
     * Mengembalikan monster yang menjadi lawan quest.
     *
     * @return monster yang menjadi lawan quest
     */
    public Monster getMonster() { return monster; }

    /**
     * Mengembalikan level minimum pengembara.
     *
     * @return level minimum pengembara
     */
    public int getMinLevel() { return minLevel; }

    /**
     * Mengembalikan status quest saat ini.
     *
     * @return status quest saat ini
     */
    public QuestStatus getStatus() { return status; }

    /**
     * Menyusun deskripsi quest umum untuk tampilan terminal.
     *
     * @return deskripsi quest multi-baris
     */
    @Override
    public String toString() {
        return baseDescription();
    }

    /**
     * Menyusun deskripsi quest umum yang dipakai banyak subclass.
     *
     * @return deskripsi multi-baris untuk terminal
     */
    protected String baseDescription() {
        return "ID Quest: " + id
                + "\nNama Quest: " + name
                + "\nTipe Quest: " + getQuestType()
                + "\nDeskripsi Quest: " + description
                + "\nTingkat Kesulitan: " + difficulty.getDisplayName()
                + "\nMonster: " + monster.getName()
                + "\nLevel Minimum: " + minLevel
                + "\nReward Koin: " + monster.getCoinReward()
                + "\nReward Exp: " + monster.getExpReward()
                + "\nStatus: " + status.getDisplayName();
    }
}
