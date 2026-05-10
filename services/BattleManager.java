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
import java.util.Scanner;

/**
 * Mengatur simulasi battle antara satu pengembara dan monster dari quest.
 *
 * <p>Class ini boleh melakukan terminal I/O karena battle membutuhkan tampilan
 * turn-by-turn. Efek unik job class dipanggil lewat hook polymorphism di
 * Wanderer, sehingga class ini tidak perlu mengecek job dengan instanceof.</p>
 */
public class BattleManager {
    private static final Scanner BATTLE_SCANNER = new Scanner(System.in);
    private static boolean lastBattleWon;

    /**
     * Utility class statis, tidak perlu dibuat objeknya.
     */
    private BattleManager() {
    }

    /**
     * Menjalankan battle tanpa informasi hari.
     *
     * @param wanderer pengembara yang mengambil quest
     * @param quest quest yang sedang dijalankan
     */
    public static void simulateBattle(Wanderer wanderer, Quest quest) {
        simulateBattle(wanderer, quest, 0);
    }

    /**
     * Menjalankan battle lengkap dan mencatat hasilnya ke riwayat pengembara.
     *
     * @param wanderer pengembara yang bertarung
     * @param quest quest yang menyediakan monster dan reward
     * @param currentDay hari saat battle berjalan, atau 0 jika tidak ingin dicatat
     */
    public static void simulateBattle(Wanderer wanderer, Quest quest, int currentDay) {
        if (wanderer == null || quest == null) {
            throw new IllegalArgumentException("Wanderer dan quest tidak boleh kosong.");
        }

        Monster monster = quest.getMonster();
        double initialHp = wanderer.getCurrentHp();
        double atkMultiplier = getAttackMultiplier(quest.getDifficulty());
        double defMultiplier = getDefenseMultiplier(quest.getDifficulty());
        double totalDamageDealt = 0;
        double totalDamageTaken = 0;
        int totalTurns = 0;
        boolean wandererTurn = true;

        monster.resetHp();
        wanderer.resetBattleState();
        monster.resetBattleState();

        BurhanLogger.getInstance().log(
                LogCategory.BATTLE,
                wanderer.getActorName(),
                "Memulai pertarungan melawan " + monster.getName()
                        + " (Quest: " + quest.getId() + ").");

        System.out.println();
        System.out.println("=== Battle Dimulai ===");
        System.out.println(wanderer.getCombatInfo());
        System.out.println("vs");
        System.out.println(monster.getCombatInfo());
        System.out.println("Quest: " + quest.getName() + " (" + quest.getDifficulty().name() + ")");

        while (!wanderer.isDefeated() && !monster.isDefeated()) {
            totalTurns++;
            System.out.println();
            System.out.println("--- Turn " + totalTurns + " ---");

            if (wandererTurn) {
                totalDamageDealt += runWandererTurn(wanderer, monster, atkMultiplier);
            } else {
                monster.onTurnStart();
                String startNote = monster.consumeTurnStartNote();
                double startDamage = monster.consumeTurnStartDamage();
                if (startNote != null) {
                    System.out.println(startNote);
                    totalDamageDealt += startDamage;
                    wanderer.recordPassiveTrigger("Bleed terpicu");
                }
                if (!monster.isDefeated()) {
                    totalDamageTaken += runMonsterTurn(wanderer, monster, defMultiplier);
                } else {
                    System.out.println(monster.getName() + " HP: "
                            + BurhanQuestUtils.formatDouble(monster.getCurrentHp())
                            + "/" + BurhanQuestUtils.formatDouble(monster.getMaxHp()));
                }
            }

            if (!wanderer.isDefeated() && !monster.isDefeated()) {
                waitForEnterIfInteractive();
            }
            wandererTurn = !wandererTurn;
        }

        lastBattleWon = !wanderer.isDefeated();
        finishBattle(wanderer, quest, initialHp, totalTurns,
                totalDamageDealt, totalDamageTaken, currentDay);
    }

    /**
     * Mengembalikan hasil battle terakhir; {@code true} jika pengembara menang.
     *
     * @return hasil battle terakhir; {@code true} jika pengembara menang
     */
    public static boolean wasLastBattleWon() {
        return lastBattleWon;
    }

    /**
     * Menjalankan satu giliran serangan milik pengembara.
     *
     * @param wanderer pengembara aktif
     * @param monster monster lawan
     * @param atkMultiplier multiplier serangan berdasarkan difficulty
     * @return total damage akhir yang diberikan pada giliran ini
     */
    private static double runWandererTurn(Wanderer wanderer, Monster monster, double atkMultiplier) {
        wanderer.onTurnStart();
        printConsumedNote(wanderer);

        wanderer.setBattleContext(monster, atkMultiplier);
        double baseDamage = Math.max(1, wanderer.getAttackPower() - monster.getDefense()) * atkMultiplier;
        double finalDamage = wanderer.modifyDamageDealt(baseDamage);
        String modifyNote = wanderer.consumeCustomDamageNote();

        boolean mageBurst = modifyNote != null && modifyNote.contains("Arcane Burst");
        if (modifyNote != null && !mageBurst) {
            System.out.println(modifyNote);
        }

        System.out.println(wanderer.getName() + " menyerang " + monster.getName() + "!");
        if (mageBurst) {
            System.out.println(modifyNote);
        }

        String damageNote = mageBurst
                ? "def 0"
                : "atk x" + BurhanQuestUtils.formatDouble(atkMultiplier);
        System.out.println("Damage ke " + monster.getName() + ": "
                + BurhanQuestUtils.formatDouble(finalDamage) + " (" + damageNote + ")");
        monster.takeDamage(finalDamage);
        wanderer.onTurnEnd(1.0);
        printConsumedNote(wanderer);
        System.out.println(monster.getName() + " HP: "
                + BurhanQuestUtils.formatDouble(monster.getCurrentHp())
                + "/" + BurhanQuestUtils.formatDouble(monster.getMaxHp()));
        return finalDamage;
    }

    /**
     * Menjalankan satu giliran serangan milik monster.
     *
     * @param wanderer pengembara yang menerima serangan
     * @param monster monster yang menyerang
     * @param defMultiplier multiplier pertahanan berdasarkan difficulty
     * @return total damage akhir yang diterima pengembara pada giliran ini
     */
    private static double runMonsterTurn(Wanderer wanderer, Monster monster, double defMultiplier) {
        double baseDamage = Math.max(1, monster.getAttackPower() - wanderer.getDefense()) * defMultiplier;
        double finalDamage = wanderer.modifyDamageTaken(baseDamage);
        printConsumedNote(wanderer);
        System.out.println(monster.getName() + " menyerang " + wanderer.getName() + "!");
        System.out.println("Damage ke " + wanderer.getName() + ": "
                + BurhanQuestUtils.formatDouble(finalDamage)
                + " (def x" + BurhanQuestUtils.formatDouble(defMultiplier) + ")");
        wanderer.takeDamage(finalDamage);
        System.out.println(wanderer.getName() + " HP: "
                + BurhanQuestUtils.formatDouble(wanderer.getCurrentHp())
                + "/" + BurhanQuestUtils.formatDouble(wanderer.getMaxHp()));
        return finalDamage;
    }

    /**
     * Menutup fase battle, menerapkan reward/rollback HP, lalu menampilkan
     * summary pertarungan.
     *
     * @param wanderer pengembara yang bertarung
     * @param quest quest yang dijalankan
     * @param initialHp HP pengembara sebelum battle
     * @param totalTurns jumlah turn yang terjadi
     * @param totalDamageDealt total damage yang diberikan pengembara
     * @param totalDamageTaken total damage yang diterima pengembara
     * @param currentDay hari game saat battle terjadi
     */
    private static void finishBattle(Wanderer wanderer, Quest quest, double initialHp,
                                     int totalTurns, double totalDamageDealt,
                                     double totalDamageTaken, int currentDay) {
        System.out.println();
        System.out.println("=== Battle Selesai ===");
        String result = lastBattleWon ? "Menang" : "Kalah";
        if (lastBattleWon) {
            quest.complete();
            int expReward = quest.getExpReward();
            int coinReward = quest.getCoinReward();
            wanderer.addExp(expReward);
            wanderer.addCoins(coinReward);
            wanderer.recordQuestResult("SELESAI", quest, currentDay);
            System.out.println(wanderer.getName() + " menang!");
            System.out.println(wanderer.getName() + " mendapatkan "
                    + expReward + " exp dan " + coinReward + " koin!");
            BurhanLogger.getInstance().log(
                    LogCategory.SYSTEM, 
                    "Sistem", 
                    "Quest " + quest.getId() + " selesai otomatis. Pengembara '" + wanderer.getUsername() + "' mendapatkan reward."
            );
        } else {
            quest.resetToAvailable();
            wanderer.setCurrentHp(initialHp);
            wanderer.recordQuestResult("KALAH", quest, currentDay);
            System.out.println(wanderer.getName() + " kalah!");
            System.out.println("HP dipulihkan ke kondisi sebelum battle.");
            System.out.println("Status quest: " + quest.getStatus().getDisplayName());
        }

        BurhanLogger.getInstance().log(
                LogCategory.BATTLE,
                wanderer.getActorName(),
                "Pertarungan selesai. Hasil: " + result + ".");

        System.out.println();
        System.out.println("=== Battle Summary ===");
        System.out.println("Total Turn: " + totalTurns);
        System.out.println("Total Damage Diberikan " + wanderer.getName()
                + ": " + BurhanQuestUtils.formatDouble(totalDamageDealt));
        System.out.println("Total Damage Diterima " + wanderer.getName()
                + ": " + BurhanQuestUtils.formatDouble(totalDamageTaken));
        ArrayList<String> passiveLines = wanderer.getPassiveSummaryLines();
        if (!passiveLines.isEmpty()) {
            System.out.println("Passive Trigger Summary:");
            for (String line : passiveLines) {
                System.out.println(line);
            }
        }
        System.out.println("Hasil Akhir: " + result);
    }

    /**
     * Mencetak catatan pasif/efek sementara dari pengembara jika ada.
     *
     * @param wanderer pengembara sumber catatan
     */
    private static void printConsumedNote(Wanderer wanderer) {
        String note = wanderer.consumeCustomDamageNote();
        if (note != null) {
            System.out.println(note);
        }
    }

    /**
     * Menampilkan prompt lanjut turn dan menunggu Enter saat mode interaktif.
     */
    private static void waitForEnterIfInteractive() {
        System.out.println();
        System.out.println("Tekan Enter untuk melanjutkan...");
        if (System.console() != null && BATTLE_SCANNER.hasNextLine()) {
            BATTLE_SCANNER.nextLine();
        }
    }

    /**
     * Menghitung multiplier serangan berdasarkan difficulty quest.
     *
     * @param difficulty tingkat kesulitan quest
     * @return multiplier serangan
     */
    private static double getAttackMultiplier(Difficulty difficulty) {
        double multiplier = 1.0;
        if (difficulty == Difficulty.MUDAH) {
            multiplier = 1.25;
        } else if (difficulty == Difficulty.SULIT) {
            multiplier = 0.75;
        }
        return multiplier;
    }

    /**
     * Menghitung multiplier pertahanan berdasarkan difficulty quest.
     *
     * @param difficulty tingkat kesulitan quest
     * @return multiplier pertahanan
     */
    private static double getDefenseMultiplier(Difficulty difficulty) {
        double multiplier = 1.0;
        if (difficulty == Difficulty.MUDAH) {
            multiplier = 0.75;
        } else if (difficulty == Difficulty.SULIT) {
            multiplier = 1.25;
        }
        return multiplier;
    }
}
