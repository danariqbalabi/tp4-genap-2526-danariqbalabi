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

public class RepeatableTest {
    private static class DummyRepeatable implements Repeatable {
        private int count;

        @Override
        public void reset() {
            count = 0;
        }

        @Override
        public int getTimesCompleted() {
            return count;
        }

        void complete() {
            count++;
        }
    }

    @Test
    public void repeatableContractTracksResettableCount() {
        DummyRepeatable repeatable = new DummyRepeatable();
        repeatable.complete();
        repeatable.complete();
        assertEquals(2, repeatable.getTimesCompleted());
        repeatable.reset();
        assertEquals(0, repeatable.getTimesCompleted());
    }
}
