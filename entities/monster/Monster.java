package entities.monster;

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
 * Musuh yang digunakan dalam battle dan bisa dipakai oleh banyak quest.
 *
 * <p>Monster menyimpan stat tempur, reward, dan state sementara battle seperti
 * bleed. HP monster direset setiap battle agar satu monster dapat dipakai ulang
 * oleh quest lain.</p>
 */
public class Monster implements Combatant {
    private String monsterId;
    private String name;
    private double  maxHp;
    private double  currentHp;
    private double  attackPower;
    private double  defense;
    private int expReward;
    private int coinReward;
    private boolean bleeding;
    private int bleedDamage;
    private String bleedSourceName;
    private String turnStartNote;
    private double turnStartDamage;

    /**
     * Membuat monster baru.
     *
     * @param idNumber nomor urut monster
     * @param name nama monster
     * @param maxHp HP maksimum monster
     * @param attackPower attack dasar monster
     * @param defense defense dasar monster
     * @param expReward exp yang diberikan saat monster kalah
     * @param coinReward koin yang diberikan saat monster kalah
     */
    public Monster(int idNumber, String name, double  maxHp,
                   double  attackPower, double  defense,
                   int expReward, int coinReward) {
        if (idNumber <= 0) {
            throw new IllegalArgumentException("Nomor ID monster harus positif.");
        }
        if (BurhanQuestUtils.isBlank(name)) {
            throw new IllegalArgumentException("Nama monster tidak boleh kosong.");
        }
        if (maxHp <= 0) {
            throw new IllegalArgumentException("HP maksimal monster harus positif.");
        }
        if (attackPower < 0 || defense < 0 || expReward < 0 || coinReward < 0) {
            throw new IllegalArgumentException("Atribut monster tidak boleh negatif.");
        }
        this.monsterId = "M" + idNumber;
        this.name = name.trim();
        this.maxHp = maxHp;
        this.currentHp = maxHp;
        this.attackPower = attackPower;
        this.defense = defense;
        this.expReward = expReward;
        this.coinReward = coinReward;
    }

    /**
     * Mengembalikan ID monster seperti M1.
     *
     * @return ID monster seperti M1
     */
    public String getMonsterId() { return monsterId; }

    /**
     * Mengembalikan exp reward monster.
     *
     * @return exp reward monster
     */
    public int getExpReward() { return expReward; }

    /**
     * Mengembalikan coin reward monster.
     *
     * @return coin reward monster
     */
    public int getCoinReward() { return coinReward; }

    /**
     * Mengembalikan HP maksimum monster.
     *
     * @return HP maksimum monster
     */
    public double getMaxHp() { return maxHp; }

    /**
     * Mengembalikan HP monster saat ini.
     *
     * @return HP monster saat ini
     */
    public double getCurrentHp() { return currentHp; }

    /**
     * Mengembalikan HP monster ke nilai maksimum sebelum battle dimulai.
     */
    public void resetHp() {
        currentHp = maxHp;
    }

    /**
     * Memberikan efek bleed yang akan diproses di awal turn monster.
     *
     * @param sourceName nama sumber bleed
     * @param bleedDamage damage bleed yang akan masuk
     */
    public void applyBleed(String sourceName, int bleedDamage) {
        bleeding = true;
        bleedSourceName = sourceName;
        this.bleedDamage = Math.max(0, bleedDamage);
    }

    /**
     * Memproses efek awal turn, termasuk damage bleed jika aktif.
     */
    public void onTurnStart() {
        turnStartNote = null;
        turnStartDamage = 0;
        if (bleeding) {
            turnStartDamage = bleedDamage;
            takeDamage(turnStartDamage);
            turnStartNote = "[" + bleedSourceName + "] " + name + " menerima "
                    + BurhanQuestUtils.formatDouble(turnStartDamage)
                    + " damage dari Bleed!";
            bleeding = false;
            bleedDamage = 0;
            bleedSourceName = null;
        }
    }

    /**
     * Membersihkan state sementara battle.
     */
    public void resetBattleState() {
        bleeding = false;
        bleedDamage = 0;
        bleedSourceName = null;
        turnStartNote = null;
        turnStartDamage = 0;
    }

    // Implementasi Combatant
    /**
     * Mengembalikan nama monster.
     *
     * @return nama monster
     */
    @Override
    public String getName() { return name; }

    /**
     * Mengembalikan attack power monster.
     *
     * @return attack power monster
     */
    @Override
    public double getAttackPower() { return attackPower; }

    /**
     * Mengembalikan nilai defense monster.
     *
     * @return defense monster
     */
    @Override
    public double getDefense() { return defense; }

    /**
     * Mengurangi HP monster berdasarkan damage yang masuk.
     *
     * @param damage damage yang diterima
     */
    @Override
    public void takeDamage(double damage) {
        currentHp = Math.max(0, currentHp - Math.max(0, damage));
    }

    /**
     * Menentukan apakah monster sudah kalah.
     *
     * @return {@code true} jika HP monster habis
     */
    @Override
    public boolean isDefeated() { return currentHp <= 0; }

    /**
     * Menyusun ringkasan stat tempur monster untuk tampilan battle.
     *
     * @return informasi tempur monster
     */
    @Override
    public String getCombatInfo() {
        return name + " | HP: " + BurhanQuestUtils.formatDouble(currentHp)
                + "/" + BurhanQuestUtils.formatDouble(maxHp)
                + " | ATK: " + BurhanQuestUtils.formatDouble(attackPower)
                + " | DEF: " + BurhanQuestUtils.formatDouble(defense);
    }

    /**
     * Menyusun representasi lengkap data monster.
     *
     * @return deskripsi lengkap monster
     */
    @Override
    public String toString() {
        return "ID Monster: " + monsterId
                + "\nNama Monster: " + name
                + "\nHP: " + BurhanQuestUtils.formatDouble(currentHp)
                + "/" + BurhanQuestUtils.formatDouble(maxHp)
                + "\nAttack: " + BurhanQuestUtils.formatDouble(attackPower)
                + " | Defense: " + BurhanQuestUtils.formatDouble(defense)
                + "\nReward Exp: " + expReward
                + "\nReward Koin: " + coinReward;
    }

    /**
     * Mengambil catatan efek awal turn lalu mengosongkannya.
     *
     * @return catatan turn-start atau {@code null}
     */
    public String consumeTurnStartNote() {
        String note = turnStartNote;
        turnStartNote = null;
        return note;
    }

    /**
     * Mengambil damage efek awal turn lalu mengosongkannya.
     *
     * @return damage turn-start yang tertunda
     */
    public double consumeTurnStartDamage() {
        double damage = turnStartDamage;
        turnStartDamage = 0;
        return damage;
    }
}
