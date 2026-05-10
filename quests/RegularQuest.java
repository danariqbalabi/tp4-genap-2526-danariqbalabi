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
 * Quest satu kali selesai tanpa bonus tambahan.
 */
public class RegularQuest extends Quest {

    /**
     * Membuat regular quest baru.
     *
     * @param idNumber nomor urut quest
     * @param name nama quest
     * @param description deskripsi quest
     * @param difficulty tingkat kesulitan
     * @param monster monster lawan
     * @param minLevel level minimum pengembara
     */
    public RegularQuest(int idNumber, String name, String description,
                        Difficulty difficulty, Monster monster, int minLevel) {
        super(idNumber, name, description, difficulty, monster, minLevel);
    }

    /**
     * Mengembalikan tipe quest reguler.
     *
     * @return string "Regular"
     */
    @Override
    public String getQuestType() {
        return "Regular";
    }

    /**
     * Menyusun deskripsi regular quest.
     *
     * @return deskripsi lengkap regular quest
     */
    @Override
    public String toString() {
        return baseDescription();
    }
}
