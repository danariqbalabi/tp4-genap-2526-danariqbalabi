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

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class CompletableTest {
    private static class DummyCompletable implements Completable {
        private boolean available = true;

        @Override
        public boolean isCompletable() {
            return available;
        }

        @Override
        public void complete() {
            available = false;
        }
    }

    @Test
    public void defaultIsAvailableUsesIsCompletable() {
        DummyCompletable completable = new DummyCompletable();
        assertTrue(completable.isAvailable());
        completable.complete();
        assertFalse(completable.isAvailable());
    }
}
