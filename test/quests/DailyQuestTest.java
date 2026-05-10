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
import static org.junit.Assert.assertTrue;

public class DailyQuestTest {
    @Test
    public void dailyQuestTracksCompletionCountAndResetsOnNewDay() {
        DailyQuest quest = createDailyQuest();

        quest.complete();
        quest.complete();
        assertEquals(2, quest.getTimesCompleted());
        assertTrue(quest.isCompletable());

        quest.onNewDay();
        assertEquals(0, quest.getTimesCompleted());
    }

    @Test
    public void dailyQuestReportsItsType() {
        DailyQuest quest = createDailyQuest();
        assertEquals("Daily", quest.getQuestType());
    }

    @Test
    public void toStringIncludesCompletionCountAndCoreFields() {
        DailyQuest quest = createDailyQuest();
        quest.complete();

        String output = quest.toString();
        assertTrue(output.contains("Tipe Quest: Daily"));
        assertTrue(output.contains("Nama Quest: Daily Patrol"));
        assertTrue(output.contains("Sudah Diselesaikan: 1 kali"));
    }

    @Test
    public void resetToAvailableKeepsDailyQuestAvailable() {
        DailyQuest quest = createDailyQuest();
        quest.complete();
        quest.resetToAvailable();
        assertTrue(quest.isCompletable());
        assertEquals(1, quest.getTimesCompleted());
    }

    private DailyQuest createDailyQuest() {
        Monster slime = new Monster(1, "Slime", 30, 5, 2, 10, 4);
        return new DailyQuest(2, "Daily Patrol", "Patrol the gate", Difficulty.MUDAH, slime, 1);
    }
}
