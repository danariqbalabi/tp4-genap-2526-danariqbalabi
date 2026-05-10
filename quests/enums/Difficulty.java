package quests.enums;

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
 * Tingkat kesulitan quest.
 *
 * <p>Setiap difficulty membawa nama tampilan dan level minimum pengembara
 * yang dibutuhkan untuk mengambil quest tersebut.</p>
 */
public enum Difficulty {
    /** Quest awal dengan level minimum 1. */
    MUDAH("mudah", 1),

    /** Quest menengah dengan level minimum 6. */
    MENENGAH("menengah", 6),

    /** Quest akhir dengan level minimum 16. */
    SULIT("sulit", 16);

    private final String displayName;
    private final int minWandererLevel;

    Difficulty(String displayName, int minWandererLevel) {
        this.displayName = displayName;
        this.minWandererLevel = minWandererLevel;
    }

    /**
     * Mengembalikan nama difficulty sesuai format I/O soal.
     *
     * @return nama difficulty sesuai format I/O soal
     */
    public String getDisplayName() {
        return displayName;
    }

    /**
     * Mengembalikan level minimum pengembara untuk difficulty ini.
     *
     * @return level minimum pengembara untuk difficulty ini
     */
    public int getMinWandererLevel() {
        return minWandererLevel;
    }

    /**
     * Mengubah teks menjadi Difficulty.
     *
     * @param raw teks seperti {@code mudah}, {@code menengah}, atau {@code sulit}
     * @return difficulty yang cocok, atau {@code null} jika tidak valid
     */
    public static Difficulty fromString(String raw) {
        if (raw == null) return null;
        for (Difficulty difficulty : Difficulty.values()) {
            if (difficulty.displayName.equalsIgnoreCase(raw.trim())) {
                return difficulty;
            }
        }
        return null;
    }
}
