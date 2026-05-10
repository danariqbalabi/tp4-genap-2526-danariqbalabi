import entities.*;
import entities.monster.*;
import exception.*;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Scanner;
import quests.*;
import services.*;
import utils.*;
import utils.enums.*;

/**
 * Main UI Controller untuk sistem BurhanQuest.
 * 
 * Kelas ini mengelola seluruh alur user interface dan interaksi dengan GameManager.
 * Mendukung dua tipe user: Admin (untuk manajemen data) dan Wanderer (untuk bermain).
 * 
 * Flow utama:
 * 1. Login menu: user pilih login atau keluar
 * 2. Authentikasi: cek username/password via GameManager
 * 3. Conditional menu: tampilkan Admin menu atau Wanderer menu berdasarkan user type
 * 4. Action handling: proses pilihan user (lihat/tambah/filter/sort/ambil quest)
 * 5. Error handling: tangkap dan tampilkan error ke user
 */
public class Main {
    // Global input scanner untuk membaca input user dari console
    static Scanner input = new Scanner(System.in);
    
    // Global GameManager instance untuk akses data game dan business logic
    static GameManager gm = new GameManager();

    /**
     * Entry point aplikasi BurhanQuest.
     * 
     * Menampilkan login menu secara loop sampai user memilih keluar.
     * Untuk setiap login, authentikasi user dan tampilkan menu sesuai role (Admin/Wanderer).
     */
    public static void main(String[] args) {
        System.out.println("Selamat datang di BurhanQuest!");
        boolean running = true;
        
        // Loop utama aplikasi sampai user keluar
        while (running) {
            showLoginMenu();
            System.out.print("Masukkan pilihan: ");
            String choice = input.nextLine().trim();
            
            if (choice.equals("1")) {
                // User pilih login: proses authentikasi
                handleLogin();
            } else if (choice.equals("2")) {
                // User pilih keluar: terminate aplikasi
                running = false;
                System.out.println("Terima kasih telah menggunakan BurhanQuest!");
            } else {
                System.out.println("Pilihan tidak valid.");
            }
        }
    }

    /**
     * Menampilkan menu login utama.
     * 
     * Menampilkan nomor hari saat ini dan pilihan login/keluar.
     */
    static void showLoginMenu() {
        System.out.println();
        // Tampilkan hari saat ini di game
        System.out.println("=== Hari ke-" + gm.getCurrentDay() + " ===");
        System.out.println("1. Login");
        System.out.println("2. Keluar dari program");
    }

    static void showAdminMenu(Admin admin) {
        boolean loggedIn = true;
        while (loggedIn) {
            printAdminMenu();
            System.out.print("Masukkan pilihan: ");
            String choice = input.nextLine().trim();
            try {
                switch (choice) {
                    case "1" -> printQuestList(gm.getQuests());
                    case "2" -> printWandererList(gm.getWanderers());
                    case "3" -> addQuest(admin);
                    case "4" -> addWanderer(admin);
                    case "5" -> addMonster(admin);
                    case "6" -> printMonsterList(gm.getMonsters());
                    case "7" -> filterQuest();
                    case "8" -> filterWanderer();
                    case "9" -> sortQuest();
                    case "10" -> sortWanderer();
                    case "11" -> advanceDay();
                    case "12" -> handleDataIo(admin);
                    case "0" -> {
                        logLogout(admin);
                        loggedIn = false;
                    }
                    default -> System.out.println("Pilihan tidak valid.");
                }
            } catch (DuplicateWandererException e) {
                logError(e);
                System.out.println(e.getMessage());
            } catch (IllegalArgumentException e) {
                logError(e);
                System.out.println("Input tidak valid: " + e.getMessage());
            }
        }
    }

    static void showWandererMenu(Wanderer wanderer) {
        boolean loggedIn = true;
        while (loggedIn) {
            printWandererMenu(wanderer);
            System.out.print("Masukkan pilihan: ");
            String choice = input.nextLine().trim();
            try {
                switch (choice) {
                    case "1" -> {
                        System.out.println();
                        System.out.println("=== Data Diri ===");
                        System.out.println(wanderer);
                    }
                    case "2" -> printQuestList(gm.getQuests());
                    case "3" -> filterQuest();
                    case "4" -> sortQuest();
                    case "5" -> takeQuest(wanderer);
                    case "6" -> exportWandererData(wanderer);
                    case "0" -> {
                        logLogout(wanderer);
                        loggedIn = false;
                    }
                    default -> System.out.println("Pilihan tidak valid.");
                }
            } catch (InsufficientLevelException e) {
                logError(e);
                System.out.println(e.getMessage());
            } catch (DataFileException e) {
                logError(e);
                System.out.println("Kesalahan file: " + e.getMessage());
            } catch (IllegalStateException e) {
                logError(e);
                System.out.println(e.getMessage());
            } catch (IllegalArgumentException e) {
                logError(e);
                System.out.println("Input tidak valid: " + e.getMessage());
            }
        }
    }

    private static void handleLogin() {
        System.out.print("Masukkan username: ");
        String username = input.nextLine().trim();
        System.out.print("Masukkan password: ");
        String password = input.nextLine().trim();
        User user = gm.login(username, password);
        if (user == null) {
            System.out.println("Username atau password salah.");
        } else {
            System.out.println("Login berhasil! " + user.getWelcomeMessage());
            if (user instanceof Admin admin) {
                showAdminMenu(admin);
            } else if (user instanceof Wanderer wanderer) {
                showWandererMenu(wanderer);
            }
        }
    }

    /**
     * Menampilkan menu opsi admin dengan 12 action items.
     * 
     * Options:
     * 1. View all quests
     * 2. View all wanderers
     * 3. Add quest
     * 4. Add wanderer
     * 5. Add monster
     * 6. View all monsters
     * 7. Filter quests by difficulty
     * 8. Filter wanderers by level range
     * 9. Sort quests (by reward/difficulty)
     * 10. Sort wanderers (by name/level)
     * 11. Advance to next day
     * 12. Export/Import data
     * 0. Logout
     */
    private static void printAdminMenu() {
        System.out.println();
        System.out.println("=== Menu Admin (Hari ke-" + gm.getCurrentDay() + ") ===");
        System.out.println("1. Lihat daftar quest");
        System.out.println("2. Lihat daftar pengembara");
        System.out.println("3. Tambah quest");
        System.out.println("4. Tambah pengembara");
        System.out.println("5. Tambah monster");
        System.out.println("6. Lihat daftar monster");
        System.out.println("7. Filter daftar quest");
        System.out.println("8. Filter daftar pengembara");
        System.out.println("9. Tampilkan daftar quest terurut");
        System.out.println("10. Tampilkan daftar pengembara terurut");
        System.out.println("11. Lanjut ke hari berikutnya");
        System.out.println("12. Ekspor atau impor data");
        System.out.println("0. Keluar");
    }

    /**
     * Menampilkan menu opsi wanderer (player) dengan 7 action items.
     * 
     * Options:
     * 1. View personal data (stats)
     * 2. View all quests
     * 3. Filter quests by difficulty
     * 4. Sort quests (by reward/difficulty)
     * 5. Take quest (trigger battle)
     * 6. Export personal data to file
     * 0. Logout
     */
    private static void printWandererMenu(Wanderer wanderer) {
        System.out.println();
        System.out.println("=== Menu Pengembara: " + wanderer.getName()
                + " (Hari ke-" + gm.getCurrentDay() + ") ===");
        System.out.println("1. Lihat data diri");
        System.out.println("2. Lihat daftar quest");
        System.out.println("3. Filter daftar quest");
        System.out.println("4. Tampilkan daftar quest terurut");
        System.out.println("5. Ambil quest");
        System.out.println("6. Ekspor data diri (Output: txt)");
        System.out.println("0. Keluar");
    }

    /**
     * Menampilkan dan memproses form penambahan wanderer baru.
     * 
     * Admin akan diminta input:
     * - Nama, username, password
     * - Stats: max HP, attack power, defense
     * - Job class selection (1=Novice, 2=Tank, 3=Mage, 4=Assassin, 5=Fighter, 6=Support)
     * 
     * Flow:
     * 1. Collect wanderer data dari user input
     * 2. Call gm.registerWanderer() untuk create & register
     * 3. Display confirmation message dengan job name
     * 
     * Throws DuplicateWandererException jika username sudah digunakan
     */
    private static void addWanderer(Admin admin) throws DuplicateWandererException {
        System.out.println();
        System.out.println("--- Tambah Pengembara ---");
        System.out.print("Masukkan nama pengembara: ");
        String name = input.nextLine().trim();
        System.out.print("Masukkan username pengembara: ");
        String username = input.nextLine().trim();
        System.out.print("Masukkan password pengembara: ");
        String password = input.nextLine().trim();
        System.out.print("Masukkan HP maksimal: ");
        double maxHp = Double.parseDouble(input.nextLine().trim());
        System.out.print("Masukkan attack power: ");
        double attack = Double.parseDouble(input.nextLine().trim());
        System.out.print("Masukkan defense: ");
        double defense = Double.parseDouble(input.nextLine().trim());
        System.out.println();
        
        // Show job class options
        System.out.println("Pilih Job Class:");
        System.out.println("1. Novice (Tanpa Skill)");
        System.out.println("2. Tank");
        System.out.println("3. Mage");
        System.out.println("4. Assassin");
        System.out.println("5. Fighter");
        System.out.println("6. Support");
        System.out.print("Pilihan: ");
        String job = input.nextLine().trim();
        
        // Register wanderer via GameManager
        Wanderer wanderer = gm.registerWanderer(admin, job, name, username, password, maxHp, attack, defense);
        System.out.println("Pengembara " + wanderer.getName() + " ("
                + wanderer.getJobName() + ") berhasil ditambahkan.");
    }

    /**
     * Menampilkan dan memproses form penambahan monster baru.
     * 
     * Admin akan diminta input:
     * - Nama monster
     * - Stats: max HP, attack power, defense
     * - Rewards: experience & coin untuk winner battle
     * 
     * Flow:
     * 1. Collect monster data dari user input
     * 2. Call gm.registerMonster() untuk create & register
     * 3. Display confirmation message
     */
    private static void addMonster(Admin admin) {
        System.out.println();
        System.out.println("--- Tambah Monster ---");
        System.out.print("Masukkan nama monster: ");
        String name = input.nextLine().trim();
        System.out.print("Masukkan HP maksimal monster: ");
        double maxHp = Double.parseDouble(input.nextLine().trim());
        System.out.print("Masukkan attack power monster: ");
        double attack = Double.parseDouble(input.nextLine().trim());
        System.out.print("Masukkan defense monster: ");
        double defense = Double.parseDouble(input.nextLine().trim());
        System.out.print("Masukkan exp reward monster: ");
        int expReward = Integer.parseInt(input.nextLine().trim());
        System.out.print("Masukkan coin reward monster: ");
        int coinReward = Integer.parseInt(input.nextLine().trim());
        
        // Register monster via GameManager
        gm.registerMonster(admin, name, maxHp, attack, defense, expReward, coinReward);
        System.out.println("Monster berhasil ditambahkan!");
    }

    /**
     * Menampilkan dan memproses form penambahan quest baru.
     * 
     * Admin akan diminta input:
     * - Nama quest, deskripsi, difficulty
     * - Pilih monster dari daftar
     * - Pilih tipe quest (Daily/Regular/Bounty)
     * - Jika Bounty: input bonus exp dan bonus coin
     * 
     * Validasi: minimal ada 1 monster sebelum create quest
     */
    private static void addQuest(Admin admin) {
        ArrayList<Monster> monsters = gm.getMonsters();
        
        // Check: pastikan sudah ada monster sebelum create quest
        if (monsters.isEmpty()) {
            System.out.println("Belum ada monster. Tambahkan monster terlebih dahulu.");
            return;
        }
        
        System.out.println();
        System.out.println("=== Tambah Quest ===");
        System.out.print("Masukkan nama quest: ");
        String name = input.nextLine().trim();
        System.out.print("Masukkan deskripsi quest: ");
        String description = input.nextLine().trim();
        System.out.print("Masukkan tingkat kesulitan (mudah/menengah/sulit): ");
        String difficulty = input.nextLine().trim();
        
        // Show all available monsters and let admin select
        System.out.println("Pilih monster:");
        for (int i = 0; i < monsters.size(); i++) {
            System.out.println((i + 1) + ". " + monsters.get(i).getName());
        }
        System.out.print("Masukkan nomor monster: ");
        int monsterNumber = Integer.parseInt(input.nextLine().trim());
        
        // Show quest type options
        System.out.println("Pilih tipe quest:");
        System.out.println("1. Daily");
        System.out.println("2. Regular");
        System.out.println("3. Bounty");
        System.out.print("Masukkan pilihan tipe quest: ");
        String type = input.nextLine().trim();
        
        // If Bounty quest: request bonus rewards
        int bonusExp = 0;
        int bonusCoin = 0;
        if (gm.isBountyQuestType(type)) {
            System.out.print("Masukkan bonus exp: ");
            bonusExp = Integer.parseInt(input.nextLine().trim());
            System.out.print("Masukkan bonus koin: ");
            bonusCoin = Integer.parseInt(input.nextLine().trim());
        }
        
        // Register quest via GameManager
        gm.registerQuest(admin, type, name, description, difficulty,
                monsterNumber, bonusExp, bonusCoin);
        System.out.println("Quest berhasil ditambahkan!");
    }

    /**
     * Menangani alur wanderer mengambil quest dan memicu battle.
     * 
     * Flow:
     * 1. Ambil daftar quest yang tersedia (completable)
     * 2. Tampilkan quest dengan nomor index
     * 3. Minta user pilih quest nomor
     * 4. Trigger battle via GameManager.startQuestByNumber()
     * 
     * Throws InsufficientLevelException jika wanderer level terlalu rendah
     */
    private static void takeQuest(Wanderer wanderer) throws InsufficientLevelException {
        // Get all quests that are available (not yet taken/completed)
        ArrayList<Quest> availableQuests = gm.getAvailableQuests();
        
        if (availableQuests.isEmpty()) {
            System.out.println("Tidak ada quest yang tersedia.");
            return;
        }
        
        // Display all available quests with index
        System.out.println("Daftar quest yang tersedia:");
        for (int i = 0; i < availableQuests.size(); i++) {
            Quest quest = availableQuests.get(i);
            System.out.println((i + 1) + ". " + quest.getName()
                    + " (" + quest.getQuestType() + ") - Difficulty: "
                    + quest.getDifficulty().getDisplayName());
        }
        
        // Prompt user to select quest or cancel
        System.out.print("Pilih quest (nomor): ");
        String choice = input.nextLine().trim();
        if (choice.equalsIgnoreCase("x")) {
            // User cancel: return to menu
            return;
        }
        
        // Start quest (trigger battle) with selected quest number
        gm.startQuestByNumber(wanderer, Integer.parseInt(choice));
    }

    /**
     * Filter dan tampilkan daftar quest berdasarkan tingkat kesulitan.
     * 
     * User diminta input difficulty level (mudah/menengah/sulit).
     * Call gm.filterQuestByDifficultyName() untuk filter.
     * Tampilkan hasil filtered quests.
     */
    private static void filterQuest() {
        System.out.print("Masukkan tingkat kesulitan (mudah/menengah/sulit): ");
        String difficulty = input.nextLine().trim();
        // Filter quests by difficulty and display result
        printQuestList(gm.filterQuestByDifficultyName(difficulty));
    }

    /**
     * Filter dan tampilkan daftar wanderer berdasarkan level range.
     * 
     * User diminta input level minimum dan maksimum.
     * Call gm.filterWandererByLevel() untuk filter.
     * Tampilkan hasil filtered wanderers.
     */
    private static void filterWanderer() {
        System.out.print("Masukkan level minimum: ");
        int min = Integer.parseInt(input.nextLine().trim());
        System.out.print("Masukkan level maksimum: ");
        int max = Integer.parseInt(input.nextLine().trim());
        // Filter wanderers by level range and display result
        printWandererList(gm.filterWandererByLevel(min, max));
    }

    /**
     * Sort dan tampilkan daftar quest berdasarkan sorting criteria.
     * 
     * User pilih sort mode (1=Reward, 2=Difficulty).
     * User pilih sort direction (1=Ascending, 2=Descending).
     * Call gm.sortQuest() untuk sort.
     * Tampilkan hasil sorted quests.
     */
    private static void sortQuest() {
        System.out.println("Urutkan quest berdasarkan:");
        System.out.println("1. Reward");
        System.out.println("2. Difficulty");
        System.out.print("Pilihan: ");
        String mode = input.nextLine().trim();
        // Ask sort direction (ascending/descending)
        boolean asc = askAscending();
        // Sort quests and display result
        printQuestList(gm.sortQuest(mode, asc));
    }

    /**
     * Sort dan tampilkan daftar wanderer berdasarkan sorting criteria.
     * 
     * User pilih sort mode (1=Name, 2=Level).
     * User pilih sort direction (1=Ascending, 2=Descending).
     * Call gm.sortWanderer() untuk sort.
     * Tampilkan hasil sorted wanderers.
     */
    private static void sortWanderer() {
        System.out.println("Urutkan pengembara berdasarkan:");
        System.out.println("1. Nama");
        System.out.println("2. Level");
        System.out.print("Pilihan: ");
        String mode = input.nextLine().trim();
        // Ask sort direction (ascending/descending)
        boolean asc = askAscending();
        // Sort wanderers and display result
        printWandererList(gm.sortWanderer(mode, asc));
    }

    /**
     * Tanya ke user untuk sort direction preference.
     * 
     * Return true jika Ascending (pilihan 1).
     * Return false jika Descending (pilihan 2).
     * 
     * @return true untuk ascending, false untuk descending
     */
    private static boolean askAscending() {
        System.out.println("Urutan:");
        System.out.println("1. Ascending");
        System.out.println("2. Descending");
        System.out.print("Pilihan: ");
        // Return true untuk ascending (choice != "2")
        return !input.nextLine().trim().equals("2");
    }

    /**
     * Advance game time ke hari berikutnya.
     * 
     * Flow:
     * 1. Call gm.advanceDay()
     * 2. Reset semua wanderer HP ke max
     * 3. Trigger daily quest reset
     * 4. Display confirmation dengan hari baru
     */
    private static void advanceDay() {
        // Advance to next day (HP restore, daily quest reset)
        gm.advanceDay();
        System.out.println("Hari berganti menjadi hari ke-" + gm.getCurrentDay());
    }

    /**
     * Handle data import/export operations untuk admin.
     * 
     * Options:
     * 1. Export data (quests or wanderers) to CSV & TXT files
     * 2. Import data (quests or wanderers) from CSV file
     * X. Return ke admin menu
     * 
     * Flow:
     * 1. Show submenu sampai user pilih exit
     * 2. User select export/import
     * 3. User choose data type (Quest/Wanderer)
     * 4. Process export/import via ReportGenerator
     * 5. Log event to BurhanLogger
     */
    private static void handleDataIo(Admin admin) {
        boolean inMenu = true;
        while (inMenu) {
            System.out.println();
            System.out.println("=== Ekspor atau Impor Data ===");
            System.out.println("1. Ekspor (Output: csv dan txt)");
            System.out.println("2. Impor (Input: csv)");
            System.out.println("X. Kembali ke menu utama");
            System.out.print("Masukkan pilihan: ");
            String choice = input.nextLine().trim();
            
            if (choice.equalsIgnoreCase("x")) {
                // User chose to return to admin menu
                inMenu = false;
            } else if (choice.equals("1")) {
                // Export option selected
                AdminIOCsvMode mode = chooseDataMode();
                if (mode != null) {
                    exportAdminData(mode, admin);
                }
            } else if (choice.equals("2")) {
                // Import option selected
                AdminIOCsvMode mode = chooseDataMode();
                if (mode != null) {
                    importData(mode, admin);
                }
            } else {
                System.out.println("Pilihan tidak valid.");
            }
        }
    }

    /**
     * Tanya ke admin untuk memilih data type untuk import/export.
     * 
     * Options:
     * 1. Quest data
     * 2. Wanderer data
     * X. Cancel
     * 
     * @return AdminIOCsvMode.QUEST or AdminIOCsvMode.WANDERER, atau null jika cancel
     */
    private static AdminIOCsvMode chooseDataMode() {
        AdminIOCsvMode mode = null;
        System.out.println("Pilih data yang ingin diekspor:");
        System.out.println("1. Quest");
        System.out.println("2. Wanderer");
        System.out.println("X. Kembali");
        System.out.print("Masukkan pilihan: ");
        String choice = input.nextLine().trim();
        
        if (choice.equals("1")) {
            // Return QUEST mode
            mode = AdminIOCsvMode.QUEST;
        } else if (choice.equals("2")) {
            // Return WANDERER mode
            mode = AdminIOCsvMode.WANDERER;
        } else if (!choice.equalsIgnoreCase("x")) {
            System.out.println("Pilihan tidak valid.");
        }
        // Return null jika cancel (user chose "x")
        return mode;
    }

    /**
     * Import data dari CSV file.
     * 
     * Flow:
     * 1. Minta user input filename
     * 2. Preview data sebelum import (ReportGenerator.previewCsv)
     * 3. If user confirm: call importFromCsv()
     * 4. Handle errors (file not found, duplicate, format error)
     * 5. Log event to BurhanLogger
     * 
     * @param mode Data type to import (QUEST or WANDERER)
     * @param admin Admin yang melakukan import
     */
    private static void importData(AdminIOCsvMode mode, Admin admin) {
        boolean importing = true;
        while (importing) {
            System.out.println("Ketik file yang ingin diimpor! Pastikan berada di satu root folder yang sama!");
            System.out.print("Nama file (kosongkan untuk membatalkan): ");
            String path = input.nextLine().trim();
            
            if (BurhanQuestUtils.isBlank(path)) {
                // User cancel: exit import loop
                importing = false;
            } else {
                try {
                    // Create ReportGenerator & preview CSV file
                    ReportGenerator generator = new ReportGenerator(gm);
                    ArrayList<ArrayList<String>> preview = generator.previewCsv(path);
                    
                    // Display data preview
                    System.out.println("Data yang akan disimpan:");
                    printTable(preview);
                    
                    // Import from CSV
                    generator.importFromCsv(path, mode);
                    
                    // Log successful import
                    BurhanLogger.getInstance().log(
                            LogCategory.ADMIN,
                            admin.getActorName(),
                            "Admin berhasil mengimpor data " + getDataModeLabel(mode)
                                    + " ke " + path + " dan "
                                    + path.replaceAll("(?i)\\.csv$", ".txt") + ".");
                    System.out.println("Data berhasil disimpan!");
                    importing = false;
                } catch (BurhanQuestException e) {
                    // Handle import errors (file error, format error, duplicate)
                    logError(e);
                    printImportError(e);
                }
            }
        }
    }

    /**
     * Export data ke CSV & TXT files.
     * 
     * Flow:
     * 1. Create ReportGenerator instance
     * 2. Generate timestamp-based filename
     * 3. Export to both CSV & TXT format
     * 4. Display data preview
     * 5. Show file saved confirmation
     * 6. Log event to BurhanLogger
     * 7. Handle DataFileException jika ada I/O error
     * 
     * @param mode Data type to export (QUEST or WANDERER)
     * @param admin Admin yang melakukan export
     */
    private static void exportAdminData(AdminIOCsvMode mode, Admin admin) {
        try {
            // Create ReportGenerator
            ReportGenerator generator = new ReportGenerator(gm);
            
            // Generate filename dengan timestamp
            String prefix = mode == AdminIOCsvMode.QUEST ? "report_quests_" : "report_wanderers_";
            String timestamp = safeTimestamp();
            String csvPath = prefix + timestamp + ".csv";
            String txtPath = prefix + timestamp + ".txt";
            
            // Get report data
            ArrayList<String> headers = generator.getReportHeaders(mode);
            ArrayList<ArrayList<String>> rows = generator.getReportRows(mode);
            
            // Export to both formats
            generator.exportToCsv(csvPath, mode);
            generator.exportToTxt(txtPath, mode);
            
            // Log event
            BurhanLogger.getInstance().log(
                    LogCategory.ADMIN,
                    admin.getActorName(),
                    "Admin berhasil mengekspor data " + getDataModeLabel(mode)
                            + " ke " + csvPath + " dan " + txtPath + ".");
            
            // Display data preview & confirmation
            printTable(headers, rows);
            System.out.println("File disimpan ke " + csvPath);
            System.out.println("File disimpan ke " + txtPath);
        } catch (DataFileException e) {
            // Handle file I/O errors
            logError(e);
            printImportError(e);
        }
    }

    /**
     * Export wanderer personal data ke TXT file.
     * 
     * Flow:
     * 1. Generate filename dengan username, ID, timestamp
     * 2. Create WandererExporter instance
     * 3. Get report lines
     * 4. Export to TXT file
     * 5. Display report data ke console
     * 6. Log event to BurhanLogger
     * 
     * @param wanderer Wanderer yang export data diri-nya
     * @throws DataFileException jika ada file I/O error
     */
    private static void exportWandererData(Wanderer wanderer) throws DataFileException {
        // Generate filename dengan unique identifier & timestamp
        String path = "wanderer_" + wanderer.getUsername() + "_" + wanderer.getId()
                + "_" + safeTimestamp() + ".txt";
        
        // Create WandererExporter & export
        WandererExporter exporter = new WandererExporter(gm, wanderer);
        ArrayList<String> lines = exporter.getReportLines(path);
        exporter.exportToTxt(path, AdminIOCsvMode.WANDERER);
        
        // Log event
        BurhanLogger.getInstance().log(
                LogCategory.PENGEMBARA,
                wanderer.getActorName(),
                "Pengembara '" + wanderer.getUsername()
                        + "' berhasil mengekspor data diri ke " + path + ".");
        
        // Display report to console
        for (String line : lines) {
            System.out.println(line);
        }
        System.out.println("Data diri disimpan di " + path);
    }

    /**
     * Menampilkan error message yang user-friendly untuk import/export errors.
     * 
     * Menganalisis exception type & display detail message yang sesuai:
     * - InvalidFileTypeException: "kesalahan nama tipe file"
     * - InvalidFormatException: "kesalahan format & integritas data"
     * - DuplicateWandererException: "duplikasi wanderer terdeteksi: [username]"
     * - Default: "kesalahan nama file"
     * 
     * @param e Exception yang terjadi
     */
    private static void printImportError(Exception e) {
        String detail = "kesalahan nama file";
        
        if (e.getClass() == InvalidFileTypeException.class) {
            detail = "kesalahan nama tipe file";
        } else if (e.getClass() == InvalidFormatException.class) {
            detail = "kesalahan format & integritas data";
        } else if (e.getClass() == DuplicateWandererException.class) {
            DuplicateWandererException duplicate = (DuplicateWandererException) e;
            detail = "duplikasi wanderer terdeteksi: " + duplicate.getUsername();
        }
        
        // Display error message
        System.out.println("Terjadi error: " + e.getClass().getSimpleName() + ", " + detail);
    }

    /**
     * Menampilkan daftar quest dengan formatted output.
     * 
     * Untuk setiap quest: display quest details via toString()
     * Jika list kosong: tampilkan "Belum ada quest."
     * 
     * @param quests ArrayList of Quest objects untuk ditampilkan
     */
    private static void printQuestList(ArrayList<Quest> quests) {
        if (quests.isEmpty()) {
            System.out.println("Belum ada quest.");
        }
        // Print each quest with blank line separator
        for (Quest quest : quests) {
            System.out.println();
            System.out.println(quest);
        }
    }

    /**
     * Menampilkan daftar wanderer dengan formatted output.
     * 
     * Untuk setiap wanderer: display wanderer details via toString()
     * Jika list kosong: tampilkan "Belum ada pengembara."
     * 
     * @param wanderers ArrayList of User (Wanderer) objects untuk ditampilkan
     */
    private static void printWandererList(ArrayList<User> wanderers) {
        if (wanderers.isEmpty()) {
            System.out.println("Belum ada pengembara.");
        } else {
            System.out.println();
            System.out.println("Pengembara yang terdaftar:");
        }
        // Print each wanderer with blank line separator
        for (User wanderer : wanderers) {
            System.out.println(wanderer);
            System.out.println();
        }
    }

    /**
     * Menampilkan daftar monster dengan formatted output.
     * 
     * Untuk setiap monster: display monster details via toString()
     * Jika list kosong: tampilkan "Belum ada monster."
     * 
     * @param monsters ArrayList of Monster objects untuk ditampilkan
     */
    private static void printMonsterList(ArrayList<Monster> monsters) {
        if (monsters.isEmpty()) {
            System.out.println("Belum ada monster.");
        }
        // Print each monster with blank line separator
        for (Monster monster : monsters) {
            System.out.println();
            System.out.println(monster);
        }
    }

    /**
     * Menampilkan data dalam format table dengan headers & rows.
     * 
     * Menggunakan BurhanQuestUtils.buildTable() untuk format rows.
     * 
     * @param headers ArrayList of header strings
     * @param rows ArrayList of ArrayList<String> untuk each row
     */
    private static void printTable(ArrayList<String> headers, ArrayList<ArrayList<String>> rows) {
        for (String line : BurhanQuestUtils.buildTable(headers, rows)) {
            System.out.println(line);
        }
    }

    /**
     * Menampilkan data dalam format table (auto-format).
     * 
     * Menggunakan BurhanQuestUtils.buildTable() untuk detect headers & format.
     * 
     * @param table ArrayList of ArrayList<String> untuk display
     */
    private static void printTable(ArrayList<ArrayList<String>> table) {
        for (String line : BurhanQuestUtils.buildTable(table)) {
            System.out.println(line);
        }
    }

    /**
     * Logout user & display confirmation.
     * 
     * Call gm.logout() untuk log event.
     * Display logout success message.
     * 
     * @param user User yang logout
     */
    private static void logLogout(User user) {
        gm.logout(user);
        System.out.println("Logout berhasil.");
    }

    /**
     * Log error ke BurhanLogger via GameManager.
     * 
     * @param e Exception untuk dilog
     */
    private static void logError(Exception e) {
        gm.logError(e);
    }

    /**
     * Generate safe timestamp string (replace colons dengan hyphens).
     * 
     * Format: YYYY-MM-DD HH-MM-SS.NNNNNNNNNZ
     * (colons diganti hyphens untuk aman sebagai filename)
     * 
     * @return Timestamp string yang safe untuk filename
     */
    private static String safeTimestamp() {
        return Instant.now().toString().replace(":", "-");
    }

    /**
     * Get human-readable label untuk data mode (Quest atau Wanderer).
     * 
     * @param mode AdminIOCsvMode untuk convert ke label
     * @return "Quest" atau "Wanderer"
     */
    private static String getDataModeLabel(AdminIOCsvMode mode) {
        String label = "Quest";
        if (mode == AdminIOCsvMode.WANDERER) {
            label = "Wanderer";
        }
        return label;
    }
}
