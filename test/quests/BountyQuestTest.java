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

public class BountyQuestTest {
    @Test(expected = IllegalArgumentException.class)
    public void constructorRejectsNegativeBonusValues() {
        new BountyQuest(1, "Bounty", "desc", Difficulty.MUDAH, createSlime(), 1, -1, 0);
    }

    @Test
    public void bountyQuestAddsBonusRewards() {
        BountyQuest quest = createBountyQuest();

        assertEquals("Bounty", quest.getQuestType());
        assertEquals(20, quest.getBonusExp());
        assertEquals(10, quest.getBonusCoin());
        assertEquals(35, quest.getExpReward());
        assertEquals(18, quest.getCoinReward());
        assertTrue(quest.toString().contains("Bonus Exp: 20"));
    }

    private Monster createSlime() {
        return new Monster(1, "Slime", 30, 5, 2, 10, 4);
    }

    private Monster createGoblin() {
        return new Monster(2, "Goblin", 45, 10, 4, 15, 8);
    }

    private BountyQuest createBountyQuest() {
        return new BountyQuest(3, "Goblin Chief", "Defeat goblin chief",
                Difficulty.MENENGAH, createGoblin(), 6, 20, 10);
    }
}
