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
import java.util.Comparator;

/**
 * GameManager
 *
 * Pengelola utama state dan alur tinggi permainan BurhanQuest. Kelas ini
 * menyimpan koleksi entity (users, wanderers, monsters, quests) dan
 * menyediakan API untuk operasi-operasi berikut:
 * - autentikasi user (login/logout)
 * - pendaftaran wanderer/monster/quest oleh admin
 * - pencarian dan penyaringan entity
 * - alur pengambilan dan pelaksanaan quest (menggunakan {@link BattleManager})
 * - pergantian hari permainan (pengisian HP, pemanggilan onNewDay pada quest)
 * - pencatatan tindakan penting melalui {@link BurhanLogger}
 *
 * Metode-metode merespons kondisi invalid dengan melempar {@link
 * IllegalArgumentException} atau pengecualian domain seperti {@link
 * DuplicateWandererException} dan {@link InsufficientLevelException}.
 */
public class GameManager {
    // Collection untuk semua user (Admin + Wanderers)
    private ArrayList<User> users;
    
    // Collection khusus untuk wanderers (untuk filtering/sorting efficiency)
    private ArrayList<Wanderer> wanderers;
    
    // Collection untuk semua monsters di game
    private ArrayList<Monster> monsters;
    
    // Collection untuk semua quests di game
    private ArrayList<Quest> quests;
    
    // Current day dalam game (increment via advanceDay())
    private int currentDay;

    /**
     * Buat instance {@code GameManager} yang siap dipakai.
     *
     * Inisialisasi membuat koleksi kosong untuk semua entity dan menetapkan
     * hari permainan ke 1. Sebuah akun admin default ditambahkan agar ada
     * akses administratif minimal.
     */
    public GameManager() {
        users = new ArrayList<>();
        wanderers = new ArrayList<>();
        monsters = new ArrayList<>();
        quests = new ArrayList<>();
        currentDay = 1;
        
        // Add default admin user
        users.add(new Admin());
    }

    /**
     * Mencoba autentikasi username dan password.
     *
     * Bila ditemukan akun yang cocok, method mengembalikan objek {@link
     * User} dan mencatat event login di logger. Bila tidak, mencatat upaya
     * gagal dan mengembalikan {@code null}.
     *
     * @param username nama pengguna yang akan diautentikasi
     * @param password kata sandi yang sesuai
     * @return {@link User} bila autentikasi berhasil; {@code null} bila gagal
     */
    public User login(String username, String password) {
        User result = null;
        
        // Cari user dengan username & password yang match
        for (User user : users) {
            if (user.authenticate(username, password)) {
                result = user;
            }
        }
        
        // Log event ke BurhanLogger (centralized logging)
        if (result != null) {
            BurhanLogger.getInstance().log(
                    LogCategory.AUTH,
                    result.getActorName(),
                    "Berhasil login ke dalam sistem.");
        } else {
            BurhanLogger.getInstance().log(
                    LogCategory.AUTH,
                    "Sistem",
                    username + " Gagal login: Autentikasi tidak valid");
        }
        return result;
    }

    /**
     * Menambahkan objek {@link Wanderer} baru ke sistem.
     *
     * Melakukan validasi input: {@code wanderer} tidak boleh {@code null} dan
     * username tidak boleh sudah terdaftar. Jika username sudah ada, method
     * melempar {@link DuplicateWandererException}.
     *
     * @param wanderer instance {@link Wanderer} yang akan didaftarkan
     * @throws DuplicateWandererException bila username sudah digunakan
     * @throws IllegalArgumentException bila {@code wanderer} null
     */
    public void addWanderer(Wanderer wanderer) throws DuplicateWandererException {
        if (wanderer == null) {
            throw new IllegalArgumentException("Data pengembara tidak boleh kosong.");
        }
        if (isUsernameTaken(wanderer.getUsername())) {
            throw new DuplicateWandererException(wanderer.getUsername());
        }
        users.add(wanderer);
        wanderers.add(wanderer);
    }

    /**
     * Memeriksa apakah sebuah username sudah terdaftar di koleksi {@code users}.
     *
     * @param username username yang akan diperiksa
     * @return {@code true} bila username sudah ada; {@code false} bila tidak
     */
    public boolean isUsernameTaken(String username) {
        boolean taken = false;
        for (User user : users) {
            if (user.getUsername().equals(username)) {
                taken = true;
            }
        }
        return taken;
    }

    /**
     * Mengembalikan ID numerik berikutnya untuk wanderer baru berdasarkan
     * jumlah wanderer saat ini.
     *
     * @return integer ID berikutnya (1-based)
     */
    public int nextWandererId() { return wanderers.size() + 1; }

    /**
     * Mendaftarkan wanderer baru yang dibuat oleh admin.
     *
     * Proses: buat instance wanderer melalui {@link #createWanderer(...)} lalu
     * tambahkan ke koleksi menggunakan {@link #addWanderer(Wanderer)}. Jika
     * berhasil, catat aksi di {@link BurhanLogger}.
     *
     * @param admin admin yang melakukan pendaftaran (untuk logging)
     * @param jobClass string yang menentukan kelas job (mis. "tank", "mage")
     * @param name nama tampilan wanderer
     * @param username nama akun untuk login
     * @param password kata sandi akun
     * @param maxHp HP maksimum
     * @param attack nilai serangan
     * @param defense nilai pertahanan
     * @return instance {@link Wanderer} yang baru dibuat dan didaftarkan
     * @throws DuplicateWandererException bila username sudah terpakai
     */
    public Wanderer registerWanderer(Admin admin, String jobClass, String name, String username,
                                     String password, double maxHp, double attack, double defense)
            throws DuplicateWandererException {
        Wanderer wanderer = createWanderer(jobClass, name, username, password, maxHp, attack, defense);
        addWanderer(wanderer);
        BurhanLogger.getInstance().log(
                LogCategory.ADMIN,
                admin.getActorName(),
                "Pengembara '" + wanderer.getName() + "' (" + wanderer.getId() + ") berhasil didaftarkan.");
        return wanderer;
    }

    /**
     * Menambahkan monster baru ke daftar monster permainan.
     *
     * Memastikan parameter tidak null dan nama monster belum digunakan.
     * @param monster objek {@link Monster} untuk ditambahkan
     * @throws IllegalArgumentException bila {@code monster} null atau nama terduplikasi
     */
    public void addMonster(Monster monster) {
        if (monster == null) {
            throw new IllegalArgumentException("Data monster tidak boleh kosong.");
        }
        if (isMonsterNameTaken(monster.getName())) {
            throw new IllegalArgumentException("Nama monster '" + monster.getName() + "' sudah digunakan.");
        }
        monsters.add(monster);
    }

    /**
     * Menghasilkan ID berikutnya untuk monster baru (1-based).
     *
     * @return integer ID berikutnya
     */
    public int nextMonsterId() { return monsters.size() + 1; }

    /**
     * Mendaftarkan monster baru ke permainan dan mencatat aksi admin.
     *
     * @param admin admin yang menambahkan monster (untuk logging)
     * @param name nama monster
     * @param maxHp HP maksimum monster
     * @param attack nilai serangan monster
     * @param defense nilai pertahanan monster
     * @param expReward EXP yang diberikan saat dikalahkan
     * @param coinReward Koin yang diberikan saat dikalahkan
     * @return instance {@link Monster} yang baru dibuat
     */
    public Monster registerMonster(Admin admin, String name, double maxHp, double attack,
                                   double defense, int expReward, int coinReward) {
        Monster monster = new Monster(nextMonsterId(), name, maxHp, attack, defense, expReward, coinReward);
        addMonster(monster);
        BurhanLogger.getInstance().log(
                LogCategory.ADMIN,
                admin.getActorName(),
                "Monster '" + monster.getName() + "' (ID: " + monster.getMonsterId() + ") berhasil ditambahkan.");
        return monster;
    }

    /**
     * Menambahkan quest baru ke daftar quest global.
     *
     * @param quest objek {@link Quest} yang akan ditambahkan
     * @throws IllegalArgumentException bila {@code quest} null
     */
    public void addQuest(Quest quest) {
        if (quest == null) {
            throw new IllegalArgumentException("Data quest tidak boleh kosong.");
        }
        quests.add(quest);
    }

    /**
     * Menghasilkan ID numerik berikutnya untuk quest baru.
     *
     * @return integer ID (jumlah quest saat ini + 1)
     */
    public int nextQuestId() { return quests.size() + 1; }

    /**
     * Mendaftarkan quest baru berdasarkan parameter yang diberikan.
     *
     * Melakukan konversi difficulty, pengambilan monster target, pembuatan
     * instance quest yang sesuai melalui {@link #createQuest(...)} dan
     * penyimpanan ke koleksi quest. Aksi admin dicatat pada logger.
     *
     * @param admin admin yang menambahkan quest
     * @param type tipe quest (nama atau kode angka)
     * @param name nama quest
     * @param description deskripsi singkat quest
     * @param difficultyRaw representasi string tingkat kesulitan
     * @param monsterNumber nomor monster target (1-based)
     * @param bonusExp bonus EXP (hanya relevan untuk bounty)
     * @param bonusCoin bonus koin (hanya relevan untuk bounty)
     * @return instance {@link Quest} yang baru dibuat
     * @throws IllegalArgumentException bila difficulty tidak dikenali atau monsterNumber salah
     */
    public Quest registerQuest(Admin admin, String type, String name, String description,
                               String difficultyRaw, int monsterNumber,
                               int bonusExp, int bonusCoin) {
        Difficulty difficulty = Difficulty.fromString(difficultyRaw);
        if (difficulty == null) {
            throw new IllegalArgumentException("Tingkat kesulitan tidak dikenal.");
        }
        Monster monster = getMonsterByNumber(monsterNumber);
        Quest quest = createQuest(type, name, description, difficulty, monster, bonusExp, bonusCoin);
        addQuest(quest);
        BurhanLogger.getInstance().log(
                LogCategory.ADMIN,
                admin.getActorName(),
                "Quest '" + quest.getName() + "' (" + quest.getId() + ") berhasil ditambahkan.");
        return quest;
    }

    /**
     * Membuat instance {@link Quest} berdasarkan tipe yang diberikan.
     *
     * Mendukung pembuatan {@link DailyQuest}, {@link RegularQuest} dan
     * {@link BountyQuest}. Melakukan validasi dasar terhadap parameter.
     *
     * @param type tipe quest (dinormalisasi sebelum switch)
     * @param name nama quest
     * @param description deskripsi quest
     * @param difficulty tingkat kesulitan sebagai {@link Difficulty}
     * @param monster monster target quest
     * @param bonusExp bonus EXP untuk tipe bounty
     * @param bonusCoin bonus koin untuk tipe bounty
     * @return instance {@link Quest} dari subclass yang sesuai
     * @throws IllegalArgumentException bila tipe quest tidak valid atau difficulty null
     */
    public Quest createQuest(String type, String name, String description,
                             Difficulty difficulty, Monster monster,
                             int bonusExp, int bonusCoin) {
        if (difficulty == null) {
            throw new IllegalArgumentException("Tingkat kesulitan quest tidak valid.");
        }
        int minLevel = difficulty.getMinWandererLevel();
        String normalizedType = type == null ? "" : type.trim().toLowerCase();
        Quest quest;
        switch (normalizedType) {
            case "1", "daily", "harian" ->
                    quest = new DailyQuest(nextQuestId(), name, description, difficulty, monster, minLevel);
            case "2", "regular", "biasa" ->
                    quest = new RegularQuest(nextQuestId(), name, description, difficulty, monster, minLevel);
            case "3", "bounty" ->
                    quest = new BountyQuest(nextQuestId(), name, description,
                            difficulty, monster, minLevel, bonusExp, bonusCoin);
            default -> throw new IllegalArgumentException("Tipe quest tidak valid.");
        }
        return quest;
    }

    /**
     * Menentukan apakah representasi tipe quest merupakan tipe "bounty".
     *
     * @param type string tipe quest (mis. "3" atau "bounty")
     * @return {@code true} bila tipe merepresentasikan bounty
     */
    public boolean isBountyQuestType(String type) {
        String normalizedType = type == null ? "" : type.trim().toLowerCase();
        return normalizedType.equals("3") || normalizedType.equals("bounty");
    }

    /**
     * Mendapatkan {@link Monster} berdasarkan nomor urut 1-based.
     *
     * @param monsterNumber nomor monster (1-based)
     * @return {@link Monster} yang bersangkutan
     * @throws IllegalArgumentException bila nomor tidak valid
     */
    public Monster getMonsterByNumber(int monsterNumber) {
        int monsterIndex = monsterNumber - 1;
        if (monsterIndex < 0 || monsterIndex >= monsters.size()) {
            throw new IllegalArgumentException("Nomor monster tidak valid.");
        }
        return monsters.get(monsterIndex);
    }

    /**
     * Mencari quest berdasarkan ID (pencarian case-insensitive).
     *
     * @param id ID quest
     * @return {@link Quest} bila ditemukan; {@code null} bila tidak
     */
    public Quest findQuestById(String id) {
        Quest result = null;
        for (Quest quest : quests) {
            if (quest.getId().equalsIgnoreCase(id)) {
                result = quest;
            }
        }
        return result;
    }

    /**
     * Mencari monster berdasarkan ID (case-insensitive).
     *
     * @param id ID monster
     * @return {@link Monster} bila ditemukan; {@code null} bila tidak
     */
    public Monster findMonsterById(String id) {
        Monster result = null;
        for (Monster monster : monsters) {
            if (monster.getMonsterId().equalsIgnoreCase(id)) {
                result = monster;
            }
        }
        return result;
    }

    /**
     * Mencari monster berdasarkan nama (case-insensitive).
     *
     * @param name nama monster
     * @return {@link Monster} bila ditemukan; {@code null} bila tidak
     */
    public Monster findMonsterByName(String name) {
        Monster result = null;
        for (Monster monster : monsters) {
            if (monster.getName().equalsIgnoreCase(name)) {
                result = monster;
            }
        }
        return result;
    }

    /**
     * Mengembalikan daftar quest yang memiliki tingkat kesulitan sama dengan
     * parameter yang diberikan.
     *
     * @param difficulty tingkat kesulitan yang dicari
     * @return daftar {@link Quest} yang cocok (bisa kosong)
     */
    public ArrayList<Quest> filterQuestByDifficulty(Difficulty difficulty) {
        ArrayList<Quest> result = new ArrayList<>();
        for (Quest quest : quests) {
            if (quest.getDifficulty() == difficulty) {
                result.add(quest);
            }
        }
        return result;
    }

    /**
     * Mengembalikan daftar quest yang saat ini tersedia untuk diambil
     * ({@link Quest#isCompletable()} == true).
     *
     * @return list quest yang dapat diambil
     */
    public ArrayList<Quest> getAvailableQuests() {
        ArrayList<Quest> result = new ArrayList<>();
        for (Quest quest : quests) {
            if (quest.isCompletable()) {
                result.add(quest);
            }
        }
        return result;
    }
    /**
     * Mengambil quest dari daftar available berdasarkan indeks 1-based.
     *
     * Metode ini memilih daftar quest yang dapat diambil, memvalidasi syarat
     * pengambilan melalui {@link #validateQuestFor(Wanderer, Quest)}, mencatat
     * aksi di logger dan mengembalikan objek quest. Bila level tidak cukup
     * atau quest tidak tersedia, akan melempar {@link InsufficientLevelException}
     * atau {@link IllegalStateException}.
     *
     * @param wanderer pengembara yang akan mengambil quest
     * @param questNumber nomor urut quest dalam daftar available (1-based)
     * @return {@link Quest} yang diambil
     * @throws InsufficientLevelException bila level wanderer kurang dari syarat
     * @throws IllegalArgumentException bila nomor quest di luar rentang
     */
    public Quest takeQuestByNumber(Wanderer wanderer, int questNumber)
            throws InsufficientLevelException {
        ArrayList<Quest> availableQuests = getAvailableQuests();
        int questIndex = questNumber - 1;
        if (questIndex < 0 || questIndex >= availableQuests.size()) {
            throw new IllegalArgumentException("Nomor quest tidak valid.");
        }
        Quest quest = availableQuests.get(questIndex);
        try {
            validateQuestFor(wanderer, quest);
            BurhanLogger.getInstance().log(
                    LogCategory.PENGEMBARA,
                    wanderer.getActorName(),
                    "Berhasil mengambil quest " + quest.getId() + ".");
        } catch (InsufficientLevelException | IllegalStateException e) {
            BurhanLogger.getInstance().log(
                    LogCategory.PENGEMBARA,
                    wanderer.getActorName(),
                    "Gagal mengambil quest " + quest.getId() + " (Syarat tidak terpenuhi).");
            logError(e);
            throw e;
        }
        return quest;
    }

    /**
     * Memulai quest berdasarkan nomor yang dipilih oleh pengembara.
     *
     * Mengambil quest via {@link #takeQuestByNumber(Wanderer, int)} lalu
     * mensimulasikan pertarungan melalui {@link BattleManager#simulateBattle}.
     *
     * @param wanderer pengembara yang memulai quest
     * @param questNumber nomor quest (1-based) dari daftar available
     * @throws InsufficientLevelException bila pengembara tidak memenuhi syarat
     */
    public void startQuestByNumber(Wanderer wanderer, int questNumber)
            throws InsufficientLevelException {
        Quest quest = takeQuestByNumber(wanderer, questNumber);
        BattleManager.simulateBattle(wanderer, quest, currentDay);
    }

    /**
     * Mengambil quest berdasarkan ID quest.
     *
     * Validasi: ID tidak boleh kosong dan harus merujuk ke quest yang ada.
     * Pengecekan syarat dilakukan melalui {@link #validateQuestFor}.
     *
     * @param wanderer pengembara yang akan mengambil quest
     * @param questId ID quest (string)
     * @return {@link Quest} yang diambil
     * @throws InsufficientLevelException bila level tidak mencukupi
     * @throws IllegalArgumentException bila ID null/blank atau tidak ditemukan
     */
    public Quest takeQuestById(Wanderer wanderer, String questId)
            throws InsufficientLevelException {
        if (questId == null || questId.isBlank()) {
            throw new IllegalArgumentException("ID quest tidak boleh kosong.");
        }

        Quest quest = findQuestById(questId.trim());
        if (quest == null) {
            throw new IllegalArgumentException("ID quest tidak valid.");
        }

        try {
            validateQuestFor(wanderer, quest);
            BurhanLogger.getInstance().log(
                    LogCategory.PENGEMBARA,
                    wanderer.getActorName(),
                    "Berhasil mengambil quest " + quest.getId() + ".");
        } catch (InsufficientLevelException | IllegalStateException e) {
            BurhanLogger.getInstance().log(
                    LogCategory.PENGEMBARA,
                    wanderer.getActorName(),
                    "Gagal mengambil quest " + quest.getId() + " (Syarat tidak terpenuhi).");
            logError(e);
            throw e;
        }
        return quest;
    }

    /**
     * Memulai quest berdasarkan ID quest.
     *
     * Mengambil quest melalui {@link #takeQuestById(Wanderer, String)} lalu
     * menjalankan simulasi battle menggunakan {@link BattleManager}.
     *
     * @param wanderer pengembara yang memulai quest
     * @param questId ID quest yang akan dimulai
     * @throws InsufficientLevelException bila validasi level gagal
     */
    public void startQuestById(Wanderer wanderer, String questId)
            throws InsufficientLevelException {
        Quest quest = takeQuestById(wanderer, questId);
        BattleManager.simulateBattle(wanderer, quest, currentDay);
    }

    /**
     * Memfilter quest berdasarkan nama tingkat kesulitan (string).
     *
     * @param difficultyRaw representasi string difficulty
     * @return daftar {@link Quest} sesuai difficulty
     * @throws IllegalArgumentException bila difficulty tidak dikenali
     */
    public ArrayList<Quest> filterQuestByDifficultyName(String difficultyRaw) {
        Difficulty difficulty = Difficulty.fromString(difficultyRaw);
        if (difficulty == null) {
            throw new IllegalArgumentException("Tingkat kesulitan tidak dikenal.");
        }
        return filterQuestByDifficulty(difficulty);
    }

    /**
     * Memfilter pengembara (wanderer) berdasarkan rentang level inklusif.
     *
     * @param min level minimum
     * @param max level maksimum
     * @return list {@link User} yang levelnya berada di antara min..max
     * @throws IllegalArgumentException bila min &gt; max
     */
    public ArrayList<User> filterWandererByLevel(int min, int max) {
        if (min > max) {
            throw new IllegalArgumentException("Rentang level tidak valid.");
        }
        ArrayList<User> result = new ArrayList<>();
        for (Wanderer wanderer : wanderers) {
            if (wanderer.getLevel() >= min && wanderer.getLevel() <= max) {
                result.add(wanderer);
            }
        }
        return result;
    }

    /**
     * Mengembalikan daftar quest yang diurutkan berdasarkan total reward
     * (EXP + coin).
     *
     * @param asc {@code true} untuk ascending, {@code false} untuk descending
     * @return daftar quest terurut berdasarkan reward
     */
    public ArrayList<Quest> sortQuestByReward(boolean asc) {
        ArrayList<Quest> result = new ArrayList<>(quests);
        Comparator<Quest> comparator = Comparator.comparingInt(
                quest -> quest.getExpReward() + quest.getCoinReward());
        result.sort(asc ? comparator : comparator.reversed());
        return result;
    }

    /**
     * Mengembalikan daftar quest yang diurutkan berdasarkan tingkat
     * kesulitan (min level required).
     *
     * @param asc {@code true} untuk ascending, {@code false} untuk descending
     * @return daftar quest terurut berdasarkan difficulty
     */
    public ArrayList<Quest> sortQuestByDifficulty(boolean asc) {
        ArrayList<Quest> result = new ArrayList<>(quests);
        Comparator<Quest> comparator = Comparator.comparingInt(
                quest -> quest.getDifficulty().getMinWandererLevel());
        result.sort(asc ? comparator : comparator.reversed());
        return result;
    }

    /**
     * Mengurutkan quest berdasarkan mode pilihan.
     *
     * Mode "1" = sort by reward, mode "2" = sort by difficulty.
     *
     * @param mode opsi sorting ("1" atau "2")
     * @param asc arah urut
     * @return daftar quest terurut
     * @throws IllegalArgumentException bila mode tidak dikenal
     */
    public ArrayList<Quest> sortQuest(String mode, boolean asc) {
        ArrayList<Quest> result;
        if (mode.equals("1")) {
            result = sortQuestByReward(asc);
        } else if (mode.equals("2")) {
            result = sortQuestByDifficulty(asc);
        } else {
            throw new IllegalArgumentException("Pilihan sort quest tidak valid.");
        }
        return result;
    }

    /**
     * Mengembalikan daftar wanderer (sebagai {@link User}) diurutkan berdasarkan
     * nama (case-insensitive).
     *
     * @param asc {@code true} untuk ascending, {@code false} untuk descending
     * @return daftar {@link User} terurut berdasarkan nama
     */
    public ArrayList<User> sortWandererByName(boolean asc) {
        ArrayList<Wanderer> sorted = new ArrayList<>(wanderers);
        Comparator<Wanderer> comparator = Comparator.comparing(Wanderer::getName, String.CASE_INSENSITIVE_ORDER);
        sorted.sort(asc ? comparator : comparator.reversed());
        return toUserList(sorted);
    }

    /**
     * Mengembalikan daftar wanderer (sebagai {@link User}) diurutkan berdasarkan
     * level.
     *
     * @param asc arah urut
     * @return daftar {@link User} terurut berdasarkan level
     */
    public ArrayList<User> sortWandererByLevel(boolean asc) {
        ArrayList<Wanderer> sorted = new ArrayList<>(wanderers);
        Comparator<Wanderer> comparator = Comparator.comparingInt(Wanderer::getLevel);
        sorted.sort(asc ? comparator : comparator.reversed());
        return toUserList(sorted);
    }

    /**
     * Mengurutkan pengembara berdasarkan mode yang dipilih.
     *
     * Mode "1" = nama, mode "2" = level.
     *
     * @param mode pilihan sorting ("1" atau "2")
     * @param asc arah urut
     * @return daftar {@link User} terurut
     */
    public ArrayList<User> sortWanderer(String mode, boolean asc) {
        ArrayList<User> result;
        if (mode.equals("1")) {
            result = sortWandererByName(asc);
        } else if (mode.equals("2")) {
            result = sortWandererByLevel(asc);
        } else {
            throw new IllegalArgumentException("Pilihan sort pengembara tidak valid.");
        }
        return result;
    }

    /**
     * Menaikkan hari permainan satu langkah.
     *
     * Efek:
     * - increment {@code currentDay}
     * - pulihkan HP semua wanderer ke nilai maksimal
     * - panggil {@link Quest#onNewDay()} untuk setiap quest
     * - catat event pergantian hari di logger
     */
    public void advanceDay() {
        currentDay++;
        for (Wanderer wanderer : wanderers) {
            wanderer.setCurrentHp(wanderer.getMaxHp());
        }
        for (Quest quest : quests) {
            quest.onNewDay();
        }
        BurhanLogger.getInstance().log(
                LogCategory.SYSTEM,
                "Sistem",
                "Hari berganti menjadi hari ke-" + currentDay + ".");
    }

    /**
     * Mengembalikan hari permainan saat ini (1-based).
     *
     * @return nilai {@code currentDay}
     */
    public int getCurrentDay() { return currentDay; }

    /**
     * Mendapatkan daftar wanderer sebagai {@link User} (salinan koleksi).
     *
     * @return list salinan {@link User} yang merepresentasikan wanderer
     */
    public ArrayList<User> getWanderers() { return toUserList(new ArrayList<>(wanderers)); }

    /**
     * Mendapatkan list objek {@link Wanderer} (salinan koleksi internal).
     *
     * @return salinan {@code ArrayList<Wanderer>}
     */
    public ArrayList<Wanderer> getWandererObjects() { return new ArrayList<>(wanderers); }

    /**
     * Mendapatkan salinan daftar {@link Monster}.
     *
     * @return salinan list monsters
     */
    public ArrayList<Monster> getMonsters() { return new ArrayList<>(monsters); }

    /**
     * Mendapatkan salinan daftar {@link Quest}.
     *
     * @return salinan list quests
     */
    public ArrayList<Quest> getQuests() { return new ArrayList<>(quests); }

    /**
     * Mencatat aksi logout user pada logger.
     *
     * @param user pengguna yang logout
     */
    public void logout(User user) {
        BurhanLogger.getInstance().log(
                LogCategory.AUTH,
                user.getActorName(),
                "Berhasil logout dari sistem");
    }

    /**
     * Mencatat exception sistem ke logger.
     *
     * @param e exception yang akan dicatat
     */
    public void logError(Exception e) {
        BurhanLogger.getInstance().log(
                LogCategory.ERROR,
                "Sistem",
                "Terjadi kesalahan sistem",
                e);
    }

    /**
     * Membuat {@link Wanderer} baru menggunakan ID yang dihasilkan otomatis.
     *
     * @see #createWanderer(int, String, String, String, String, double, double, double)
     */
    public Wanderer createWanderer(String jobClass, String name, String username, String password,
                                   double maxHp, double attack, double defense) {
        return createWanderer(nextWandererId(), jobClass, name, username, password, maxHp, attack, defense);
    }

    /**
     * Membuat instance {@link Wanderer} atau subclass sesuai nilai jobClass.
     *
     * JobClass dapat berupa angka atau nama (mis. "tank", "mage"). Method
     * mengembalikan objek turunan yang sesuai.
     *
     * @param idNumber ID numerik wanderer
     * @param jobClass representasi kelas job
     * @param name nama tampilan
     * @param username nama akun
     * @param password kata sandi
     * @param maxHp HP maksimum
     * @param attack nilai serangan
     * @param defense nilai pertahanan
     * @return objek {@link Wanderer} atau subclass yang relevan
     */
    public Wanderer createWanderer(int idNumber, String jobClass, String name, String username, String password,
                                   double maxHp, double attack, double defense) {
        String normalizedJob = jobClass == null ? "1" : jobClass.trim().toLowerCase();
        Wanderer created;
        switch (normalizedJob) {
            case "2", "tank" -> created = new Tank(idNumber, name, username, password, maxHp, attack, defense);
            case "3", "mage" -> created = new Mage(idNumber, name, username, password, maxHp, attack, defense);
            case "4", "assassin" -> created = new Assassin(idNumber, name, username, password, maxHp, attack, defense);
            case "5", "fighter" -> created = new Fighter(idNumber, name, username, password, maxHp, attack, defense);
            case "6", "support" -> created = new Support(idNumber, name, username, password, maxHp, attack, defense);
            default -> created = new Wanderer(idNumber, name, username, password, maxHp, attack, defense);
        }
        return created;
    }

    /**
     * Memvalidasi apakah seorang pengembara memenuhi syarat untuk menjalankan
     * quest tertentu.
     *
     * Cek yang dilakukan:
     * - parameter tidak null
     * - level pengembara >= required level
     * - quest masih dapat diambil (quest.isCompletable())
     *
     * @param wanderer pengembara yang divalidasi
     * @param quest quest yang akan diambil
     * @throws InsufficientLevelException bila level pengembara kurang dari syarat
     * @throws IllegalArgumentException bila parameter null
     * @throws IllegalStateException bila quest tidak dapat diambil (sudah selesai)
     */
    public void validateQuestFor(Wanderer wanderer, Quest quest) throws InsufficientLevelException {
        if (wanderer == null || quest == null) {
            throw new IllegalArgumentException("Pengembara dan quest tidak boleh kosong.");
        }
        int requiredLevel = Math.max(quest.getMinLevel(), quest.getDifficulty().getMinWandererLevel());
        if (wanderer.getLevel() < requiredLevel) {
            throw new InsufficientLevelException(wanderer.getLevel(), requiredLevel);
        }
        if (!quest.isCompletable()) {
            throw new IllegalStateException("Quest sudah selesai dan tidak tersedia.");
        }
    }

    /**
     * Memeriksa apakah nama monster sudah digunakan (case-insensitive).
     *
     * @param name nama monster yang akan dicek
     * @return {@code true} bila sudah ada, {@code false} bila belum
     */
    private boolean isMonsterNameTaken(String name) {
        boolean taken = false;
        for (Monster monster : monsters) {
            if (monster.getName().equalsIgnoreCase(name)) {
                taken = true;
            }
        }
        return taken;
    }

    /**
     * Mengonversi daftar {@link Wanderer} menjadi daftar {@link User}.
     *
     * Mengembalikan salinan baru, sehingga perubahan pada hasil tidak
     * memengaruhi koleksi internal.
     *
     * @param source daftar {@link Wanderer} sumber
     * @return daftar {@link User} baru yang berisi elemen yang sama
     */
    private ArrayList<User> toUserList(ArrayList<Wanderer> source) {
        ArrayList<User> result = new ArrayList<>();
        for (Wanderer wanderer : source) {
            result.add(wanderer);
        }
        return result;
    }
}
