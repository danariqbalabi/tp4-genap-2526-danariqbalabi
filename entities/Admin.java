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

/**
 * User khusus pengelola sistem.
 *
 * <p>Admin memakai kredensial bawaan untuk masuk ke menu pengelolaan quest,
 * monster, pengembara, serta fitur import/export data.</p>
 */
public class Admin extends User {

    /**
     * Membuat admin default yang selalu tersedia saat aplikasi dimulai.
     */
    public Admin() {
        this("Burhan", "burhan", "burunghantu123");
    }

    /**
     * Membuat admin dengan identitas login tertentu.
     *
     * @param name nama tampilan admin
     * @param username username untuk autentikasi
     * @param password password untuk autentikasi
     */
    public Admin(String name, String username, String password) {
        super(name, username, password);
    }

    /**
     * Mengembalikan pesan sambutan ketika admin berhasil login.
     *
     * @return pesan sambutan admin
     */
    @Override
    public String getWelcomeMessage() {
        return "Selamat datang, " + getName() + ".";
    }

    /**
     * Mengembalikan identitas aktor admin untuk logger.
     *
     * @return label aktor admin
     */
    @Override
    public String getActorName() {
        return "Admin: " + getUsername();
    }

    /**
     * Representasi ringkas data admin.
     *
     * @return string identitas admin
     */
    @Override
    public String toString() {
        return "Admin: " + getName() + "\nUsername: " + getUsername();
    }
}
