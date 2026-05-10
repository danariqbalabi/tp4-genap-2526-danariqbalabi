package test.entities;

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

public class WandererTest {
    @Test(expected = IllegalArgumentException.class)
    public void constructorRejectsNonPositiveId() {
        new Wanderer(0, "Frieren", "fr", "123", 100, 20, 10);
    }

    @Test(expected = IllegalArgumentException.class)
    public void constructorRejectsNonPositiveHp() {
        new Wanderer(1, "Frieren", "fr", "123", 0, 20, 10);
    }

    @Test(expected = IllegalArgumentException.class)
    public void constructorRejectsNegativeStats() {
        new Wanderer(1, "Frieren", "fr", "123", 100, -1, 10);
    }

    @Test
    public void expAndCoinsAreAddedAndLevelCanIncrease() {
        Wanderer wanderer = createNovice();

        wanderer.addExp(5000);
        wanderer.addCoins(15);

        assertEquals(2, wanderer.getLevel());
        assertEquals(5000, wanderer.getExp());
        assertEquals(15, wanderer.getCoins());
    }

    @Test(expected = IllegalArgumentException.class)
    public void addExpRejectsNegativeAmount() {
        createNovice().addExp(-1);
    }

    @Test(expected = IllegalArgumentException.class)
    public void addCoinsRejectsNegativeAmount() {
        createNovice().addCoins(-1);
    }

    @Test
    public void hpOperationsAndCombatStatusWorkCorrectly() {
        Wanderer wanderer = createNovice();

        wanderer.setCurrentHp(150);
        assertEquals(100, wanderer.getCurrentHp(), 0.0001);
        wanderer.takeDamage(30);
        assertEquals(70, wanderer.getCurrentHp(), 0.0001);
        assertFalse(wanderer.isDefeated());
        wanderer.takeDamage(500);
        assertTrue(wanderer.isDefeated());
    }

    @Test
    public void questEligibilityAndHistoryWorkAsExpected() {
        Wanderer wanderer = createNovice();
        Quest quest = createRegularQuest();

        assertTrue(wanderer.canTakeQuest(Difficulty.MUDAH));
        assertFalse(wanderer.canTakeQuest(Difficulty.MENENGAH));

        wanderer.recordQuestResult("MENANG", quest, 3);
        ArrayList<String> history = wanderer.getQuestHistory();
        assertEquals(1, history.size());
        assertTrue(history.get(0).contains("Q1 - Hunt Slime"));
        assertTrue(history.get(0).contains("Hari ke-3"));
    }

    @Test
    public void jobMetadataAndProgressSetterWork() {
        Wanderer wanderer = createNovice();
        assertEquals("NOVICE", wanderer.getJobName());
        assertEquals("Novice", wanderer.getJobDisplayName());

        wanderer.setProgress(5, 100, 20);
        assertEquals(5, wanderer.getLevel());
        assertEquals(100, wanderer.getExp());
        assertEquals(20, wanderer.getCoins());
    }

    @Test(expected = IllegalArgumentException.class)
    public void setProgressRejectsInvalidValues() {
        createNovice().setProgress(0, -1, 0);
    }

    @Test
    public void combatInfoAndStringContainImportantFields() {
        Wanderer wanderer = createNovice();

        String combatInfo = wanderer.getCombatInfo();
        assertTrue(combatInfo.contains("Frieren"));
        assertTrue(combatInfo.contains("HP: 100/100"));
        assertTrue(combatInfo.contains("ATK: 20"));
        assertTrue(combatInfo.contains("DEF: 10"));

        String details = wanderer.toString();
        assertTrue(details.contains("ID Pengembara: P1"));
        assertTrue(details.contains("Level Pengembara: 1"));
        assertTrue(details.contains("Koin Didapatkan: 0 koin"));
    }

    @Test
    public void battleContextAndNotesCanBeSetAndCleared() {
        Wanderer wanderer = createNovice();
        Monster target = createSlime();

        wanderer.setBattleContext(target, 1.25);
        assertEquals(target, wanderer.getCurrentTarget());
        assertEquals(1.25, wanderer.getCurrentMultiplier(), 0.0001);

        wanderer.setCustomDamageNote("test note");
        assertEquals("test note", wanderer.consumeCustomDamageNote());
        assertNull(wanderer.consumeCustomDamageNote());
    }

    @Test
    public void passiveSummaryIncludesTriggerAndTotalHealWhenRecorded() {
        Wanderer wanderer = createNovice();
        wanderer.recordPassiveTrigger("Shield aktif");
        wanderer.recordHeal(5);

        ArrayList<String> lines = wanderer.getPassiveSummaryLines();
        assertEquals(3, lines.size());
        assertTrue(lines.get(0).contains("Shield aktif"));
        assertTrue(lines.get(1).contains("Heal aktif"));
        assertTrue(lines.get(2).contains("Total HP dipulihkan: 5"));
    }

    @Test
    public void resetBattleStateClearsContextAndPassiveSummary() {
        Wanderer wanderer = createNovice();
        wanderer.setBattleContext(createSlime(), 1.5);
        wanderer.recordPassiveTrigger("x");
        wanderer.recordHeal(4);
        wanderer.setCustomDamageNote("temp");

        wanderer.resetBattleState();

        assertNull(wanderer.getCurrentTarget());
        assertEquals(1.0, wanderer.getCurrentMultiplier(), 0.0001);
        assertNull(wanderer.consumeCustomDamageNote());
        assertTrue(wanderer.getPassiveSummaryLines().isEmpty());
    }

    @Test
    public void nextLevelExpGrowsExponentially() {
        Wanderer wanderer = createNovice();
        assertEquals(5000, wanderer.getNextLevelExp(1));
        assertEquals(10000, wanderer.getNextLevelExp(2));
        assertEquals(20000, wanderer.getNextLevelExp(3));
    }

    @Test
    public void historyGetterReturnsDefensiveCopy() {
        Wanderer wanderer = createNovice();
        wanderer.recordQuestResult("MENANG", createRegularQuest());

        ArrayList<String> copied = wanderer.getQuestHistory();
        copied.clear();

        assertEquals(1, wanderer.getQuestHistory().size());
        assertNotNull(wanderer.getWelcomeMessage());
        assertTrue(wanderer.getActorName().contains("[Pengembara (Novice): frieren]"));
    }

    @Test
    public void baseCombatHooksReturnUnchangedValues() {
        Wanderer wanderer = createNovice();
        assertEquals(11.5, wanderer.modifyDamageDealt(11.5), 0.0001);
        assertEquals(7.25, wanderer.modifyDamageTaken(7.25), 0.0001);
        wanderer.onTurnStart();
        wanderer.onTurnEnd(1);
        wanderer.onTurnEnd(0.5);
        assertEquals(20, wanderer.getAttackPower(), 0.0001);
        assertEquals(10, wanderer.getDefense(), 0.0001);
    }

    private Wanderer createNovice() {
        return new Wanderer(1, "Frieren", "frieren", "123", 100, 20, 10);
    }

    private Monster createSlime() {
        return new Monster(1, "Slime", 30, 5, 2, 10, 4);
    }

    private Quest createRegularQuest() {
        return new RegularQuest(1, "Hunt Slime", "Clear nearby field", Difficulty.MUDAH, createSlime(), 1);
    }
}
