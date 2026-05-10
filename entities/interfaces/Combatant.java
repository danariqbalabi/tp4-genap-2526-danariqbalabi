package entities.interfaces;

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
 * Kontrak untuk objek yang bisa ikut bertarung.
 *
 * <p>BattleManager hanya bergantung pada interface ini untuk membaca status
 * tempur, memberi damage, dan menampilkan ringkasan combatant.</p>
 */
public interface Combatant {
    /**
     * Mengembalikan nama yang ditampilkan saat battle.
     *
     * @return nama yang ditampilkan saat battle
     */
    String getName();

    /**
     * Mengembalikan nilai serangan dasar.
     *
     * @return nilai serangan dasar
     */
    double getAttackPower();

    /**
     * Mengembalikan nilai pertahanan dasar.
     *
     * @return nilai pertahanan dasar
     */
    double getDefense();

    /**
     * Mengurangi HP combatant.
     *
     * @param damage damage akhir yang diterima
     */
    void takeDamage(double damage);

    /**
     * Mengembalikan {@code true} jika HP sudah habis.
     *
     * @return {@code true} jika HP sudah habis
     */
    boolean isDefeated();

    /**
     * Mengembalikan informasi ringkas untuk ditampilkan sebelum battle.
     *
     * @return informasi ringkas untuk ditampilkan sebelum battle
     */
    String getCombatInfo();
}
