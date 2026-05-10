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
 * Status quest yang tampil di daftar quest dan file report.
 */
public enum QuestStatus {
    /** Quest masih bisa diambil. */
    TERSEDIA("tersedia"),

    /** Quest sudah selesai dan tidak tersedia lagi, kecuali daily quest. */
    SELESAI("selesai");

    private final String displayName;

    QuestStatus(String displayName) {
        this.displayName = displayName;
    }

    /**
     * Mengembalikan nama status sesuai format I/O soal.
     *
     * @return nama status sesuai format I/O soal
     */
    public String getDisplayName() {
        return displayName;
    }

    /**
     * Mengubah teks menjadi QuestStatus.
     *
     * @param raw teks status dari input atau file
     * @return status yang cocok, atau {@code null} jika tidak valid
     */
    public static QuestStatus fromString(String raw) {
        if (raw == null) return null;
        for (QuestStatus status : QuestStatus.values()) {
            if (status.displayName.equalsIgnoreCase(raw.trim())) {
                return status;
            }
        }
        return null;
    }
}
