package test.services;

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

import org.junit.Test;

import java.util.ArrayList;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

public class GameManagerTest {
    @Test
    public void loginSucceedsForDefaultAdmin() {
        GameManager manager = new GameManager();
        User user = manager.login("burhan", "burunghantu123");
        assertTrue(user instanceof Admin);
    }

    @Test
    public void loginReturnsNullForInvalidCredential() {
        GameManager manager = new GameManager();
        assertNull(manager.login("unknown", "bad"));
    }

    @Test
    public void registerWandererAndDuplicateValidationWork() throws Exception {
        GameManager manager = new GameManager();
        Admin admin = (Admin) manager.login("burhan", "burunghantu123");

        Wanderer created = manager.registerWanderer(admin, "mage", "Frieren", "frieren", "123", 100, 30, 10);
        assertEquals("P1", created.getId());
        assertEquals(1, manager.getWandererObjects().size());

        try {
            manager.registerWanderer(admin, "tank", "Eisen", "frieren", "123", 120, 20, 20);
        } catch (DuplicateWandererException exception) {
            assertEquals("frieren", exception.getUsername());
            return;
        }
        throw new AssertionError("Expected DuplicateWandererException");
    }

    @Test
    public void monsterAndQuestRegistrationThenFilteringWorks() {
        GameManager manager = new GameManager();
        Admin admin = (Admin) manager.login("burhan", "burunghantu123");

        manager.registerMonster(admin, "Slime", 30, 5, 2, 10, 4);
        manager.registerMonster(admin, "Orc", 60, 12, 6, 25, 12);
        manager.registerQuest(admin, "daily", "Daily Slime", "desc", "mudah", 1, 0, 0);
        manager.registerQuest(admin, "bounty", "Orc Chief", "desc", "menengah", 2, 10, 5);

        assertEquals(2, manager.getMonsters().size());
        assertEquals(2, manager.getQuests().size());
        assertEquals(1, manager.filterQuestByDifficulty(Difficulty.MUDAH).size());
        assertEquals(1, manager.filterQuestByDifficulty(Difficulty.MENENGAH).size());
    }

    @Test
    public void validateQuestRejectsInsufficientLevelAndUnavailableQuest() throws Exception {
        GameManager manager = new GameManager();
        Admin admin = (Admin) manager.login("burhan", "burunghantu123");
        Wanderer wanderer = manager.registerWanderer(admin, "1", "Frieren", "frieren2", "123", 100, 20, 10);
        Monster monster = manager.registerMonster(admin, "Goblin", 40, 8, 3, 15, 6);
        Quest quest = manager.createQuest("regular", "Goblin Hunt", "desc", Difficulty.MENENGAH, monster, 0, 0);

        try {
            manager.validateQuestFor(wanderer, quest);
        } catch (InsufficientLevelException expected) {
            wanderer.setProgress(6, 5000, 0);
        }

        manager.validateQuestFor(wanderer, quest);
        quest.complete();

        try {
            manager.validateQuestFor(wanderer, quest);
        } catch (IllegalStateException expected) {
            assertTrue(expected.getMessage().contains("Quest sudah selesai"));
            return;
        }
        throw new AssertionError("Expected IllegalStateException");
    }

    @Test
    public void sortingAndAdvanceDayOperateOnStoredData() throws Exception {
        GameManager manager = new GameManager();
        Admin admin = (Admin) manager.login("burhan", "burunghantu123");
        Wanderer w1 = manager.registerWanderer(admin, "1", "B", "wb", "123", 100, 10, 10);
        Wanderer w2 = manager.registerWanderer(admin, "1", "A", "wa", "123", 100, 10, 10);
        w2.setProgress(5, 10, 0);

        Monster m1 = manager.registerMonster(admin, "Slime", 30, 5, 2, 10, 4);
        Monster m2 = manager.registerMonster(admin, "Orc", 50, 8, 4, 20, 10);
        manager.addQuest(new DailyQuest(1, "Quest A", "desc", Difficulty.MUDAH, m1, 1));
        manager.addQuest(new BountyQuest(2, "Quest B", "desc", Difficulty.MENENGAH, m2, 6, 5, 5));

        ArrayList<User> sortedByName = manager.sortWandererByName(true);
        assertEquals("A", sortedByName.get(0).getName());
        ArrayList<Quest> sortedQuest = manager.sortQuestByReward(false);
        assertEquals("Quest B", sortedQuest.get(0).getName());

        w1.takeDamage(30);
        int beforeDay = manager.getCurrentDay();
        manager.advanceDay();
        assertEquals(beforeDay + 1, manager.getCurrentDay());
        assertEquals(w1.getMaxHp(), w1.getCurrentHp(), 0.0001);
    }

    @Test
    public void findersAndTakeQuestByIdWork() throws Exception {
        GameManager manager = new GameManager();
        Admin admin = (Admin) manager.login("burhan", "burunghantu123");
        Wanderer wanderer = manager.registerWanderer(admin, "1", "Frieren", "frieren3", "123", 100, 20, 10);
        wanderer.setProgress(10, 0, 0);
        Monster monster = manager.registerMonster(admin, "Slime", 30, 5, 2, 10, 4);
        Quest quest = manager.registerQuest(admin, "regular", "Slime Hunt", "desc", "mudah", 1, 0, 0);

        assertNotNull(manager.findMonsterByName("slime"));
        assertNotNull(manager.findMonsterById(monster.getMonsterId()));
        assertNotNull(manager.findQuestById(quest.getId()));
        assertEquals(quest.getId(), manager.takeQuestById(wanderer, quest.getId()).getId());
    }

    @Test(expected = IllegalArgumentException.class)
    public void addWandererRejectsNullData() throws Exception {
        new GameManager().addWanderer(null);
    }

    @Test(expected = IllegalArgumentException.class)
    public void addMonsterRejectsNullData() {
        new GameManager().addMonster(null);
    }

    @Test(expected = IllegalArgumentException.class)
    public void addQuestRejectsNullData() {
        new GameManager().addQuest(null);
    }

    @Test
    public void duplicateMonsterNameIsRejected() {
        GameManager manager = new GameManager();
        Admin admin = (Admin) manager.login("burhan", "burunghantu123");
        manager.registerMonster(admin, "Slime", 30, 5, 2, 10, 4);
        try {
            manager.registerMonster(admin, "slime", 20, 4, 2, 5, 2);
        } catch (IllegalArgumentException expected) {
            assertTrue(expected.getMessage().contains("sudah digunakan"));
            return;
        }
        throw new AssertionError("Expected IllegalArgumentException");
    }

    @Test
    public void createWandererFactoryCreatesEachJobClass() {
        GameManager manager = new GameManager();
        assertTrue(manager.createWanderer(1, "tank", "A", "u1", "123", 100, 10, 10) instanceof Tank);
        assertTrue(manager.createWanderer(2, "3", "B", "u2", "123", 100, 10, 10) instanceof Mage);
        assertTrue(manager.createWanderer(3, "assassin", "C", "u3", "123", 100, 10, 10) instanceof Assassin);
        assertTrue(manager.createWanderer(4, "5", "D", "u4", "123", 100, 10, 10) instanceof Fighter);
        assertTrue(manager.createWanderer(5, "support", "E", "u5", "123", 100, 10, 10) instanceof Support);
        assertTrue(manager.createWanderer(6, "unknown", "F", "u6", "123", 100, 10, 10).getClass() == Wanderer.class);
    }

    @Test
    public void createQuestFactorySupportsAliasesAndRejectsInvalidInput() {
        GameManager manager = new GameManager();
        Monster monster = createSlime();

        assertTrue(manager.createQuest("harian", "A", "d", Difficulty.MUDAH, monster, 0, 0) instanceof DailyQuest);
        assertTrue(manager.createQuest("biasa", "B", "d", Difficulty.MUDAH, monster, 0, 0) instanceof RegularQuest);
        assertTrue(manager.createQuest("3", "C", "d", Difficulty.MUDAH, monster, 1, 2) instanceof BountyQuest);

        try {
            manager.createQuest("x", "D", "d", Difficulty.MUDAH, monster, 0, 0);
        } catch (IllegalArgumentException expected) {
            assertTrue(expected.getMessage().contains("Tipe quest tidak valid"));
        }

        try {
            manager.createQuest("daily", "D", "d", null, monster, 0, 0);
        } catch (IllegalArgumentException expected) {
            assertTrue(expected.getMessage().contains("Tingkat kesulitan quest tidak valid"));
            return;
        }
        throw new AssertionError("Expected IllegalArgumentException");
    }

    @Test
    public void helperMethodsValidateInputAndModes() throws Exception {
        GameManager manager = new GameManager();
        Admin admin = (Admin) manager.login("burhan", "burunghantu123");
        assertFalse(manager.isUsernameTaken("none"));
        manager.registerWanderer(admin, "1", "Frieren", "fr10", "123", 100, 20, 10);
        assertTrue(manager.isUsernameTaken("fr10"));

        assertTrue(manager.isBountyQuestType("3"));
        assertTrue(manager.isBountyQuestType("bounty"));
        assertFalse(manager.isBountyQuestType("daily"));

        manager.registerMonster(admin, "Slime", 30, 5, 2, 10, 4);
        try {
            manager.getMonsterByNumber(2);
        } catch (IllegalArgumentException expected) {
            assertTrue(expected.getMessage().contains("Nomor monster tidak valid"));
        }

        try {
            manager.filterQuestByDifficultyName("invalid");
        } catch (IllegalArgumentException expected) {
            assertTrue(expected.getMessage().contains("tidak dikenal"));
        }

        try {
            manager.filterWandererByLevel(10, 2);
        } catch (IllegalArgumentException expected) {
            assertTrue(expected.getMessage().contains("Rentang level tidak valid"));
        }

        try {
            manager.sortQuest("x", true);
        } catch (IllegalArgumentException expected) {
            assertTrue(expected.getMessage().contains("Pilihan sort quest tidak valid"));
        }

        try {
            manager.sortWanderer("x", true);
        } catch (IllegalArgumentException expected) {
            assertTrue(expected.getMessage().contains("Pilihan sort pengembara tidak valid"));
        }

        assertNull(manager.findMonsterByName("none"));
        assertNull(manager.findMonsterById("M99"));
        assertNull(manager.findQuestById("Q99"));
    }

    @Test
    public void takingQuestValidatesIndexAndQuestIdInput() throws Exception {
        GameManager manager = new GameManager();
        Admin admin = (Admin) manager.login("burhan", "burunghantu123");
        Wanderer wanderer = manager.registerWanderer(admin, "1", "Frieren", "fr20", "123", 100, 20, 10);
        wanderer.setProgress(10, 0, 0);
        manager.registerMonster(admin, "Slime", 30, 5, 2, 10, 4);
        manager.registerQuest(admin, "daily", "Quest 1", "d", "mudah", 1, 0, 0);

        try {
            manager.takeQuestByNumber(wanderer, 9);
        } catch (IllegalArgumentException expected) {
            assertTrue(expected.getMessage().contains("Nomor quest tidak valid"));
        }

        try {
            manager.takeQuestById(wanderer, "   ");
        } catch (IllegalArgumentException expected) {
            assertTrue(expected.getMessage().contains("ID quest tidak boleh kosong"));
        }

        try {
            manager.takeQuestById(wanderer, "Q999");
        } catch (IllegalArgumentException expected) {
            assertTrue(expected.getMessage().contains("ID quest tidak valid"));
            return;
        }
        throw new AssertionError("Expected IllegalArgumentException");
    }

    @Test
    public void gettersReturnDefensiveCopies() throws Exception {
        GameManager manager = new GameManager();
        Admin admin = (Admin) manager.login("burhan", "burunghantu123");
        manager.registerWanderer(admin, "1", "Frieren", "fr30", "123", 100, 20, 10);
        manager.registerMonster(admin, "Slime", 30, 5, 2, 10, 4);
        manager.registerQuest(admin, "daily", "Quest 1", "d", "mudah", 1, 0, 0);

        ArrayList<User> w = manager.getWanderers();
        ArrayList<Monster> m = manager.getMonsters();
        ArrayList<Quest> q = manager.getQuests();
        w.clear();
        m.clear();
        q.clear();

        assertEquals(1, manager.getWanderers().size());
        assertEquals(1, manager.getMonsters().size());
        assertEquals(1, manager.getQuests().size());
    }

    @Test
    public void difficultyFilterAndSortFacadeMethodsReturnExpectedOrder() {
        GameManager manager = new GameManager();
        Monster m = createSlime();
        manager.addQuest(new RegularQuest(1, "Easy", "d", Difficulty.MUDAH, m, 1));
        manager.addQuest(new RegularQuest(2, "Mid", "d", Difficulty.MENENGAH, m, 6));
        manager.addQuest(new RegularQuest(3, "Hard", "d", Difficulty.SULIT, m, 16));

        assertEquals(1, manager.filterQuestByDifficultyName("mudah").size());
        assertEquals("Easy", manager.sortQuest("2", true).get(0).getName());
        assertEquals("Hard", manager.sortQuest("2", false).get(0).getName());
    }

    @Test
    public void validateQuestForRejectsNullParameters() {
        GameManager manager = new GameManager();
        try {
            manager.validateQuestFor(null, null);
        } catch (Exception expected) {
            assertTrue(expected instanceof IllegalArgumentException);
            return;
        }
        throw new AssertionError("Expected IllegalArgumentException");
    }

    @Test
    public void startQuestMethodsExecuteBattleFlowAndUpdateHistory() throws Exception {
        GameManager manager = new GameManager();
        Admin admin = (Admin) manager.login("burhan", "burunghantu123");
        Wanderer wanderer = manager.registerWanderer(admin, "mage", "Frieren", "fr_battle", "123", 100, 100, 20);
        wanderer.setProgress(10, 0, 0);

        manager.registerMonster(admin, "Tiny", 10, 1, 0, 5, 2);
        Quest q1 = manager.registerQuest(admin, "daily", "Q one", "d", "mudah", 1, 0, 0);
        Quest q2 = manager.registerQuest(admin, "regular", "Q two", "d", "mudah", 1, 0, 0);

        manager.startQuestByNumber(wanderer, 1);
        assertTrue(BattleManager.wasLastBattleWon());
        assertFalse(wanderer.getQuestHistory().isEmpty());
        assertTrue(((DailyQuest) q1).getTimesCompleted() > 0);

        manager.startQuestById(wanderer, q2.getId());
        assertTrue(q2.getStatus() == QuestStatus.SELESAI);
    }

    @Test
    public void logoutAndLogErrorCanBeCalledSafely() {
        GameManager manager = new GameManager();
        User admin = manager.login("burhan", "burunghantu123");
        manager.logout(admin);
        manager.logError(new IllegalStateException("x"));
    }

    private Monster createSlime() {
        return new Monster(1, "Slime", 30, 5, 2, 10, 4);
    }
}
