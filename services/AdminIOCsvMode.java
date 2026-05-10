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

/**
 * Pilihan jenis data yang bisa diimpor atau diekspor oleh admin.
 */
public enum AdminIOCsvMode {
    /** Mode import/export data quest. */
    QUEST("quest"),

    /** Mode import/export data pengembara. */
    WANDERER("wanderer");

    private final String displayName;

    AdminIOCsvMode(String displayName) {
        this.displayName = displayName;
    }

    /**
     * Mengembalikan nama mode yang dipakai pada pesan terminal.
     *
     * @return nama mode dalam huruf kecil
     */
    public String getDisplayName() {
        return displayName;
    }

    /**
     * Mengubah input teks menjadi mode data.
     *
     * <p>Method ini menerima nama enum seperti {@code QUEST} maupun nama
     * tampilan seperti {@code quest} agar input lebih fleksibel.</p>
     *
     * @param raw teks input pengguna atau isi file
     * @return mode yang cocok, atau {@code null} jika tidak dikenal
     */
    public static AdminIOCsvMode fromString(String raw) {
        if (raw == null) {
            return null;
        }
        AdminIOCsvMode result = null;
        for (AdminIOCsvMode mode : AdminIOCsvMode.values()) {
            if (mode.displayName.equalsIgnoreCase(raw.trim())
                    || mode.name().equalsIgnoreCase(raw.trim())) {
                result = mode;
            }
        }
        return result;
    }
}
