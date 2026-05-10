package entities;

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
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Pengembara yang bisa mengambil quest dan bertarung.
 *
 * <p>Class ini menjadi base job class. Subclass seperti Mage, Tank, Assassin,
 * Fighter, dan Support mengubah hook battle tanpa membuat BattleManager perlu
 * mengecek tipe job menggunakan instanceof.</p>
 */
public class Wanderer extends User implements Combatant {
    private static final int MAX_LEVEL = 20;
    private static final int MAX_EXP = 1_310_720_000;
    private String id;
    private int level;
    private int exp;
    private int coins;
    private double currentHp;
    private double maxHp;
    private double attack;
    private double defense;
    private Monster currentTarget;
    private double currentMultiplier;
    private String customDamageNote;
    private LinkedHashMap<String, Integer> passiveTriggers;
    private double totalHealed;
    private ArrayList<String> questHistory;

    /**
     * Membuat pengembara baru dengan level, exp, koin, dan HP awal default.
     *
     * @param idNumber nomor urut pengembara
     * @param name nama tampilan pengembara
     * @param username username login
     * @param password password login
     * @param maxHp HP maksimum
     * @param attack attack dasar
     * @param defense defense dasar
     */
    public Wanderer(int idNumber, String name, String username,
                    String password, double maxHp, double attack, double defense) {
        super(name, username, password);
        if (idNumber <= 0) {
            // IllegalArgumentException dipakai karena nomor id harus bernilai positif.
            throw new IllegalArgumentException("Nomor ID pengembara harus positif.");
        }
        if (maxHp <= 0) {
            // IllegalArgumentException dipakai karena HP maksimal nol/negatif tidak valid untuk battle.
            throw new IllegalArgumentException("HP maksimal harus bilangan positif.");
        }
        if (attack < 0 || defense < 0) {
            // IllegalArgumentException dipakai karena atribut tempur tidak boleh negatif.
            throw new IllegalArgumentException("Attack dan defense tidak boleh negatif.");
        }
        this.id = "P" + idNumber;
        this.level = 1;
        this.exp = 0;
        this.coins = 0;
        this.maxHp = maxHp;
        this.currentHp = maxHp;
        this.attack = attack;
        this.defense = defense;
        this.currentMultiplier = 1.0;
        this.passiveTriggers = new LinkedHashMap<>();
        this.questHistory = new ArrayList<>();
    }

    /**
     * Mengecek apakah level pengembara memenuhi difficulty quest.
     *
     * @param difficulty difficulty quest
     * @return {@code true} jika level pengembara cukup
     */
    public boolean canTakeQuest(Difficulty difficulty) {
        return difficulty != null && level >= difficulty.getMinWandererLevel();
    }

    /**
     * Menambahkan exp dan memproses level up jika ambang exp tercapai.
     *
     * @param amount jumlah exp yang ditambahkan
     */
    public void addExp(int amount) {
        if (amount < 0) {
            throw new IllegalArgumentException("Jumlah exp tidak boleh negatif.");
        }
        exp = Math.min(MAX_EXP, exp + amount);
        while (level < MAX_LEVEL && exp >= getNextLevelExp(level)) {
            level++;
            BurhanLogger.getInstance().log(
                    LogCategory.SYSTEM,
                    "Sistem",
                    "Pengembara '" + getUsername() + "' naik ke level " + level + ".");
        }
    }

    /**
     * Menambahkan koin pengembara.
     *
     * @param amount jumlah koin yang ditambahkan
     */
    public void addCoins(int amount) {
        if (amount < 0) {
            throw new IllegalArgumentException("Jumlah koin tidak boleh negatif.");
        }
        coins += amount;
    }

    /**
     * Menghitung kebutuhan exp untuk naik dari level tertentu.
     *
     * @param currentLevel level yang dihitung
     * @return exp minimum untuk level berikutnya
     */
    public int getNextLevelExp(int currentLevel) {
        if (currentLevel <= 1) {
            return 5000;
        }
        return 2 * getNextLevelExp(currentLevel - 1);
    }

    /**
     * Mengubah HP saat ini dan memastikan nilainya tetap di rentang 0 sampai maxHp.
     *
     * @param hp nilai HP baru
     */
    public void setCurrentHp(double hp) {
        currentHp = Math.max(0, Math.min(maxHp, hp));
    }

    /**
     * Mengembalikan HP pengembara saat ini.
     *
     * @return HP pengembara saat ini
     */
    public double getCurrentHp() {
        return currentHp;

    }
    
    /**
     * Menyimpan konteks target dan multiplier untuk hook job class selama turn.
     *
     * @param target monster yang sedang diserang
     * @param multiplier multiplier damage dari difficulty
     */
    public void setBattleContext(Monster target, double multiplier) {
        currentTarget = target;
        currentMultiplier = multiplier;
    }

    /**
     * Mengembalikan target monster aktif saat turn pengembara.
     *
     * @return target monster aktif saat turn pengembara
     */
    public Monster getCurrentTarget() {
        return currentTarget;
    }

    /**
     * Mengembalikan multiplier damage aktif dari difficulty.
     *
     * @return multiplier damage aktif dari difficulty
     */
    public double getCurrentMultiplier() {
        return currentMultiplier;
    }

    /**
     * Menyimpan pesan pasif job class untuk dicetak oleh BattleManager.
     *
     * @param customDamageNote pesan yang akan dikonsumsi
     */
    public void setCustomDamageNote(String customDamageNote) {
        this.customDamageNote = customDamageNote;
    }

    /**
     * Mengambil dan menghapus pesan pasif job class.
     *
     * @return pesan pasif yang tertunda, atau {@code null}
     */
    public String consumeCustomDamageNote() {
        String note = customDamageNote;
        customDamageNote = null;
        return note;
    }

    /**
     * Mengembalikan pesan sambutan ketika pengembara berhasil login.
     *
     * @return pesan sambutan pengembara
     */
    @Override
    public String getWelcomeMessage() {
        return "Selamat datang, " + getName() + ".";
    }

    /**
     * Mengembalikan identitas aktor pengembara lengkap dengan job class untuk
     * keperluan logger.
     *
     * @return label aktor pengembara
     */
    @Override
    public String getActorName() {
        return "[Pengembara (" + getJobDisplayName() + "): " + getUsername() + "]";
    }

    // Implementasi Combatant
    /**
     * Mengembalikan nama pengembara.
     *
     * @return nama pengembara
     */
    @Override
    public String getName() { return super.getName(); }

    /**
     * Mengembalikan nilai attack pengembara.
     *
     * @return attack power pengembara
     */
    @Override
    public double getAttackPower() { return attack; }

    /**
     * Mengembalikan nilai defense pengembara.
     *
     * @return defense pengembara
     */
    @Override
    public double getDefense() { return defense; }

    /**
     * Mengurangi HP pengembara berdasarkan damage masuk.
     *
     * @param damage damage yang diterima
     */
    @Override
    public void takeDamage(double damage) {
        setCurrentHp(currentHp - Math.max(0, damage));
    }

    // Implementasi mekanisme pertempuran
    /**
     * Hook yang dipanggil di awal turn pengembara.
     */
    public void onTurnStart() {
    }

    /**
     * Hook untuk mengubah damage yang diberikan pengembara.
     *
     * @param baseDamage damage awal sebelum efek job class
     * @return damage akhir
     */
    public double modifyDamageDealt(double baseDamage) {
        return baseDamage;
    }

    /**
     * Hook untuk mengubah damage yang diterima pengembara.
     *
     * @param incomingDamage damage awal dari monster
     * @return damage akhir
     */
    public double modifyDamageTaken(double incomingDamage) {
        return incomingDamage;
    }

    /**
     * Hook akhir turn versi integer untuk kompatibilitas.
     *
     * @param result indikator hasil turn
     */
    public void onTurnEnd(int result) {
        onTurnEnd((double) result);
    }

    /**
     * Hook yang dipanggil setelah pengembara selesai menyerang.
     *
     * @param result indikator hasil turn
     */
    public void onTurnEnd(double result) {
    }

    /**
     * Membersihkan state sementara battle sebelum battle baru dimulai.
     */
    public void resetBattleState() {
        currentTarget = null;
        currentMultiplier = 1.0;
        customDamageNote = null;
        passiveTriggers.clear();
        totalHealed = 0;
    }


    /**
     * Menentukan apakah pengembara sudah kalah.
     *
     * @return {@code true} jika HP sudah habis
     */
    @Override
    public boolean isDefeated() { return currentHp <= 0; }

    /**
     * Menyusun ringkasan stat tempur pengembara.
     *
     * @return informasi tempur pengembara
     */
    @Override
    public String getCombatInfo() {
        return getName() + " | HP: " + BurhanQuestUtils.formatDouble(currentHp)
                + "/" + BurhanQuestUtils.formatDouble(maxHp)
                + " | ATK: " + BurhanQuestUtils.formatDouble(attack)
                + " | DEF: " + BurhanQuestUtils.formatDouble(defense);
    }

    /**
     * Menyusun representasi lengkap data pengembara.
     *
     * @return deskripsi lengkap pengembara
     */
    @Override
    public String toString() {
        return "ID Pengembara: " + id
                + "\nNama Pengembara: " + getName()
                + "\nUsername: " + getUsername()
                + "\nLevel Pengembara: " + level
                + "\nExp Pengembara: " + exp + " poin exp"
                + "\nKoin Didapatkan: " + coins + " koin"
                + "\nHP: " + BurhanQuestUtils.formatDouble(currentHp)
                + "/" + BurhanQuestUtils.formatDouble(maxHp)
                + "\nAttack: " + BurhanQuestUtils.formatDouble(attack)
                + " | Defense: " + BurhanQuestUtils.formatDouble(defense);
    }

    /**
     * Mengembalikan ID pengembara seperti P1.
     *
     * @return ID pengembara seperti P1
     */
    public String getId() {
        return id;
    }

    /**
     * Mengembalikan level pengembara.
     *
     * @return level pengembara
     */
    public int getLevel() {
        return level;
    }

    /**
     * Mengembalikan exp terkumpul.
     *
     * @return exp terkumpul
     */
    public int getExp() {
        return exp;
    }

    /**
     * Mengembalikan jumlah koin terkumpul.
     *
     * @return jumlah koin terkumpul
     */
    public int getCoins() {
        return coins;
    }

    /**
     * Mengembalikan HP maksimum pengembara.
     *
     * @return HP maksimum pengembara
     */
    public double getMaxHp() {
        return maxHp;
    }

    /**
     * Mengembalikan attack dasar pengembara.
     *
     * @return attack dasar pengembara
     */
    public double getAttack() {
        return attack;
    }

    /**
     * Mengembalikan nama job dalam format internal uppercase.
     *
     * @return nama job dalam format internal uppercase
     */
    public String getJobName() {
        String simpleName = getClass().getSimpleName();
        String jobName = simpleName.equals("Wanderer") ? "NOVICE" : simpleName.toUpperCase();
        return jobName;
    }

    /**
     * Mengembalikan nama job yang ramah dibaca di report/log.
     *
     * @return nama job yang ramah dibaca di report/log
     */
    public String getJobDisplayName() {
        return BurhanQuestUtils.capitalize(getJobName());
    }

    /**
     * Mencatat kemunculan pasif job class untuk battle summary.
     *
     * @param label nama pasif yang aktif
     */
    public void recordPassiveTrigger(String label) {
        passiveTriggers.put(label, passiveTriggers.getOrDefault(label, 0) + 1);
    }

    /**
     * Mencatat total heal selama battle.
     *
     * @param amount jumlah HP yang dipulihkan
     */
    public void recordHeal(double amount) {
        recordPassiveTrigger("Heal aktif");
        totalHealed += amount;
    }

    /**
     * Mengembalikan baris ringkasan pasif untuk battle summary.
     *
     * @return baris ringkasan pasif untuk battle summary
     */
    public ArrayList<String> getPassiveSummaryLines() {
        ArrayList<String> lines = new ArrayList<>();
        for (Map.Entry<String, Integer> entry : passiveTriggers.entrySet()) {
            lines.add("- " + entry.getKey() + ": " + entry.getValue() + " kali");
        }
        if (totalHealed > 0) {
            lines.add("- Total HP dipulihkan: " + BurhanQuestUtils.formatDouble(totalHealed));
        }
        return lines;
    }

    /**
     * Mengatur progress pengembara dari import file.
     *
     * @param level level baru
     * @param exp exp baru
     * @param coins jumlah koin baru
     */
    public void setProgress(int level, int exp, int coins) {
        if (level <= 0 || exp < 0 || coins < 0) {
            throw new IllegalArgumentException("Progress pengembara tidak valid.");
        }
        this.level = level;
        this.exp = exp;
        this.coins = coins;
    }

    /**
     * Mencatat hasil quest tanpa informasi hari.
     *
     * @param status status hasil battle
     * @param quest quest yang dijalankan
     */
    public void recordQuestResult(String status, Quest quest) {
        recordQuestResult(status, quest, 0);
    }

    /**
     * Mencatat hasil quest untuk report pengembara.
     *
     * @param status status hasil battle
     * @param quest quest yang dijalankan
     * @param currentDay hari saat quest dijalankan
     */
    public void recordQuestResult(String status, Quest quest, int currentDay) {
        String dayInfo = currentDay > 0 ? " (Hari ke-" + currentDay + ")" : "";
        questHistory.add("[" + status + "] " + quest.getId() + " - " + quest.getName() + dayInfo);
    }

    /**
     * Mengembalikan salinan riwayat quest pengembara.
     *
     * @return salinan riwayat quest pengembara
     */
    public ArrayList<String> getQuestHistory() {
        return new ArrayList<>(questHistory);
    }
}
