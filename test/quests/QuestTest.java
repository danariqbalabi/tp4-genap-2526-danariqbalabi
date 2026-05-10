package test.quests;

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
import static org.junit.Assert.assertTrue;

public class QuestTest {
    @Test
    public void baseQuestFieldsAndRewardsAreDerivedCorrectly() {
        Monster monster = createSlime();
        Quest quest = new BountyQuest(1, "Clear Slime", "Clear the gate", Difficulty.MUDAH, monster, 1, 5, 2);

        assertEquals("Q1", quest.getId());
        assertEquals("Clear Slime", quest.getName());
        assertEquals(Difficulty.MUDAH, quest.getDifficulty());
        assertEquals(15, quest.getExpReward());
        assertEquals(6, quest.getCoinReward());
        assertTrue(quest.isCompletable());
    }

    @Test
    public void completeAndResetToAvailableToggleAvailability() {
        Quest quest = createRegularQuest();

        quest.complete();
        assertFalse(quest.isCompletable());

        quest.resetToAvailable();
        assertTrue(quest.isCompletable());
    }

    @Test
    public void minimumLevelFollowsDifficultyFloor() {
        Quest quest = new RegularQuest(9, "Hard Run", "desc", Difficulty.MENENGAH, createGoblin(), 1);
        assertEquals(6, quest.getMinLevel());
    }

    private Monster createSlime() {
        return new Monster(1, "Slime", 30, 5, 2, 10, 4);
    }

    private Monster createGoblin() {
        return new Monster(2, "Goblin", 45, 10, 4, 15, 8);
    }

    private RegularQuest createRegularQuest() {
        return new RegularQuest(1, "Hunt Slime", "Clear nearby field", Difficulty.MUDAH, createSlime(), 1);
    }
}
