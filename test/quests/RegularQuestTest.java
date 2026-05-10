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

public class RegularQuestTest {
    @Test
    public void regularQuestHasProperTypeAndDescription() {
        RegularQuest quest = createRegularQuest();

        assertEquals("Regular", quest.getQuestType());
        String description = quest.toString();
        assertTrue(description.contains("Tipe Quest: Regular"));
        assertTrue(description.contains("Nama Quest: Hunt Slime"));
    }

    private RegularQuest createRegularQuest() {
        Monster slime = new Monster(1, "Slime", 30, 5, 2, 10, 4);
        return new RegularQuest(1, "Hunt Slime", "Clear nearby field", Difficulty.MUDAH, slime, 1);
    }
}
