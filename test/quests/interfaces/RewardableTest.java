package test.quests.interfaces;

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

public class RewardableTest {
    private static class DummyRewardable implements Rewardable {
        @Override
        public int getBonusExp() {
            return 7;
        }

        @Override
        public int getBonusCoin() {
            return 3;
        }
    }

    @Test
    public void rewardableContractProvidesBonusValues() {
        DummyRewardable rewardable = new DummyRewardable();
        assertEquals(7, rewardable.getBonusExp());
        assertEquals(3, rewardable.getBonusCoin());
    }
}
