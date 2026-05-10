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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class UserTest {
    private static class DummyUser extends User {
        DummyUser(String name, String username, String password) {
            super(name, username, password);
        }

        @Override
        public String getWelcomeMessage() {
            return "welcome";
        }

        @Override
        public String getActorName() {
            return "Dummy: " + getUsername();
        }
    }

    @Test(expected = IllegalArgumentException.class)
    public void constructorRejectsInvalidName() {
        new DummyUser("frieren", "frieren_1", "123");
    }

    @Test(expected = IllegalArgumentException.class)
    public void constructorRejectsInvalidUsername() {
        new DummyUser("Frieren", "frieren-1", "123");
    }

    @Test(expected = IllegalArgumentException.class)
    public void constructorRejectsBlankPassword() {
        new DummyUser("Frieren", "frieren_1", " ");
    }

    @Test
    public void authenticateUsesExactStoredCredentials() {
        DummyUser user = new DummyUser("Frieren", "frieren_1", "123");

        assertTrue(user.authenticate("frieren_1", "123"));
        assertFalse(user.authenticate("frieren_1", "wrong"));
        assertEquals("Frieren", user.getName());
        assertEquals("frieren_1", user.getUsername());
        assertEquals("Frieren (frieren_1)", user.toString());
    }
}
