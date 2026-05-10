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
 * Quest harian yang tetap tersedia setelah diselesaikan.
 *
 * <p>Daily quest hanya menambah counter penyelesaian. Counter tersebut direset
 * saat hari berganti melalui {@link #onNewDay()}.</p>
 */
public class DailyQuest extends Quest implements Repeatable {
    private int timesCompleted;

    /**
     * Membuat daily quest baru dengan counter penyelesaian 0.
     *
     * @param idNumber nomor urut quest
     * @param name nama quest
     * @param description deskripsi quest
     * @param difficulty tingkat kesulitan
     * @param monster monster lawan
     * @param minLevel level minimum pengembara
     */
    public DailyQuest(int idNumber, String name, String description,
                      Difficulty difficulty, Monster monster, int minLevel) {
        super(idNumber, name, description, difficulty, monster, minLevel);
        this.timesCompleted = 0;
    }

    /**
     * Mengembalikan tipe quest harian.
     *
     * @return string "Daily"
     */
    @Override
    public String getQuestType() {
        return "Daily";
    }

    /**
     * Menandai daily quest selesai pada hari ini dengan menaikkan counter.
     */
    @Override
    public void complete() {
        timesCompleted++;
    }

    /**
     * Mereset jumlah penyelesaian harian ke nol.
     */
    @Override
    public void reset() {
        timesCompleted = 0;
    }

    /**
     * Mengembalikan jumlah penyelesaian quest pada hari berjalan.
     *
     * @return jumlah penyelesaian harian
     */
    @Override
    public int getTimesCompleted() { return timesCompleted; }

    @Override
    protected int getBonusExp() {
        return 0;
    }

    /**
     * Daily quest tidak perlu dipaksa ke status tersedia karena status
     * penyelesaiannya dikelola melalui reset harian.
     */
    @Override
    public void resetToAvailable() {
        // Daily quest tetap tersedia setelah battle; counter hanya direset saat hari berganti.
    }

    /**
     * Hook pergantian hari untuk mereset counter penyelesaian.
     */
    @Override
    public void onNewDay() {
        reset();
    }

    /**
     * Menampilkan deskripsi daily quest termasuk jumlah selesai hari ini.
     *
     * @return deskripsi lengkap daily quest
     */
    @Override
    public String toString() {
        return baseDescription()
                + "\nSudah Diselesaikan: " + timesCompleted + " kali";
    }
}
