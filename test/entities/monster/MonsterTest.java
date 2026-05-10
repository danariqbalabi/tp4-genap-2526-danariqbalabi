package test.entities.monster;

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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

public class MonsterTest {
    @Test(expected = IllegalArgumentException.class)
    public void constructorRejectsNonPositiveId() {
        new Monster(0, "Slime", 10, 2, 1, 3, 1);
    }

    @Test(expected = IllegalArgumentException.class)
    public void constructorRejectsBlankName() {
        new Monster(1, "   ", 10, 2, 1, 3, 1);
    }

    @Test(expected = IllegalArgumentException.class)
    public void constructorRejectsNonPositiveMaxHp() {
        new Monster(1, "Slime", 0, 2, 1, 3, 1);
    }

    @Test(expected = IllegalArgumentException.class)
    public void constructorRejectsNegativeAttributes() {
        new Monster(1, "Slime", 10, -1, 1, 3, 1);
    }

    @Test
    public void takeDamageAndResetHpWorkCorrectly() {
        Monster monster = createSlime();

        monster.takeDamage(8.5);
        assertEquals(21.5, monster.getCurrentHp(), 0.0001);
        monster.takeDamage(-2);
        assertEquals(21.5, monster.getCurrentHp(), 0.0001);
        monster.takeDamage(1000);
        assertEquals(0, monster.getCurrentHp(), 0.0001);
        assertTrue(monster.isDefeated());

        monster.resetHp();
        assertEquals(monster.getMaxHp(), monster.getCurrentHp(), 0.0001);
        assertFalse(monster.isDefeated());
    }

    @Test
    public void bleedIsAppliedOnTurnStartAndThenCleared() {
        Monster monster = createSlime();

        monster.applyBleed("ASSASSIN", 6);
        monster.onTurnStart();

        assertEquals(24, monster.getCurrentHp(), 0.0001);
        assertTrue(monster.consumeTurnStartNote().contains("Bleed"));
        assertEquals(6, monster.consumeTurnStartDamage(), 0.0001);
        assertEquals(0, monster.consumeTurnStartDamage(), 0.0001);
    }

    @Test
    public void combatInfoContainsStats() {
        Monster monster = createSlime();
        String info = monster.getCombatInfo();
        assertTrue(info.contains("Slime"));
        assertTrue(info.contains("HP: 30/30"));
        assertTrue(info.contains("ATK: 5"));
        assertTrue(info.contains("DEF: 2"));
    }

    @Test
    public void resetBattleStateClearsPendingBleedArtifacts() {
        Monster monster = createSlime();
        monster.applyBleed("ASSASSIN", 3);
        monster.onTurnStart();

        monster.resetBattleState();

        assertEquals(0, monster.consumeTurnStartDamage(), 0.0001);
        assertNull(monster.consumeTurnStartNote());
    }

    @Test
    public void onTurnStartWithoutBleedDoesNotProduceMessage() {
        Monster monster = createSlime();
        monster.onTurnStart();
        assertNull(monster.consumeTurnStartNote());
        assertEquals(0, monster.consumeTurnStartDamage(), 0.0001);
    }

    @Test
    public void toStringContainsIdentityAndRewardInfo() {
        Monster monster = createSlime();
        String dump = monster.toString();
        assertTrue(dump.contains("ID Monster: M1"));
        assertTrue(dump.contains("Nama Monster: Slime"));
        assertTrue(dump.contains("Reward Exp: 10"));
        assertTrue(dump.contains("Reward Koin: 4"));
    }

    private Monster createSlime() {
        return new Monster(1, "Slime", 30, 5, 2, 10, 4);
    }
}
