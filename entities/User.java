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
 * Base class abstrak untuk semua aktor yang bisa login ke BurhanQuest.
 *
 * <p>User menyimpan identitas dasar dan aturan autentikasi. Subclass seperti
 * Admin dan Wanderer menentukan pesan sambutan serta nama aktor yang dipakai
 * di logger.</p>
 */
public abstract class User {
    private String name;
    private String username;
    private String password;

    /**
     * Membuat user dengan validasi identitas dasar.
     *
     * @param name nama tampilan dengan kapital di awal setiap kata
     * @param username username login, hanya huruf, angka, dan underscore
     * @param password password login
     */
    public User(String name, String username, String password) {
        if (isBlank(name)) {
            // IllegalArgumentException dipakai karena identitas kosong membuat objek User tidak valid.
            throw new IllegalArgumentException("Nama pengguna tidak boleh kosong.");
        }
        if (!isValidName(name)) {
            // IllegalArgumentException dipakai karena format nama mengikuti constraint TP2.
            throw new IllegalArgumentException("Nama pengguna hanya boleh berisi huruf dan spasi dengan kapital di awal kata.");
        }
        if (isBlank(username)) {
            // IllegalArgumentException dipakai karena username kosong tidak dapat dipakai untuk login.
            throw new IllegalArgumentException("Username tidak boleh kosong.");
        }
        if (!username.matches("[A-Za-z0-9_]+")) {
            // IllegalArgumentException dipakai karena username TP2 hanya menerima huruf, angka, dan underscore.
            throw new IllegalArgumentException("Username hanya boleh berisi huruf, angka, dan underscore (_).");
        }
        if (isBlank(password)) {
            // IllegalArgumentException dipakai karena password kosong membuat autentikasi tidak valid.
            throw new IllegalArgumentException("Password tidak boleh kosong.");
        }
        this.name = name.trim();
        this.username = username.trim();
        this.password = password;
    }

    /**
     * Mengembalikan pesan sambutan setelah login berhasil.
     *
     * @return pesan sambutan setelah login berhasil
     */
    public abstract String getWelcomeMessage();

    /**
     * Mengembalikan nama aktor yang akan ditulis pada file log.
     *
     * @return nama aktor yang akan ditulis pada file log
     */
    public abstract String getActorName();

    /**
     * Memeriksa kecocokan username dan password.
     *
     * @param username username input
     * @param password password input
     * @return {@code true} jika kredensial cocok
     */
    public boolean authenticate(String username, String password) {
        return this.username.equals(username) && this.password.equals(password);
    }

    /**
     * Mengembalikan nama tampilan user.
     *
     * @return nama tampilan user
     */
    public String getName() {
        return name;
    }

    /**
     * Mengembalikan username login user.
     *
     * @return username login user
     */
    public String getUsername() {
        return username;
    }

    /**
     * Representasi ringkas user untuk debugging/monitoring.
     *
     * @return nama dan username user
     */
    @Override
    public String toString() {
        return name + " (" + username + ")";
    }

    /**
     * Memeriksa apakah string null/kosong/setelah trim tidak berisi karakter.
     *
     * @param value string yang dicek
     * @return {@code true} jika blank
     */
    private boolean isBlank(String value) {
        return value == null || value.trim().isEmpty();
    }

    /**
     * Memvalidasi format nama sesuai constraint TP (huruf/spasi, kapital awal
     * setiap kata).
     *
     * @param value nama yang dicek
     * @return {@code true} jika nama valid
     */
    private boolean isValidName(String value) {
        String trimmed = value.trim();
        boolean valid = trimmed.matches("[A-Za-z ]+");
        if (valid) {
            String[] words = trimmed.split(" +");
            for (String word : words) {
                if (!word.matches("[A-Z][a-z]*")) {
                    valid = false;
                }
            }
        }
        return valid;
    }
}
