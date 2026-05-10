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
import static org.junit.Assert.assertTrue;

public class AdminTest {
    @Test
    public void defaultAdminHasExpectedCredentialsAndWelcomeMessage() {
        Admin admin = new Admin();

        assertEquals("Burhan", admin.getName());
        assertEquals("burhan", admin.getUsername());
        assertEquals("Selamat datang, Burhan.", admin.getWelcomeMessage());
    }

    @Test
    public void actorNameAndStringContainAdminIdentity() {
        Admin admin = new Admin("Asta", "asta_admin", "secret");

        assertEquals("Admin: asta_admin", admin.getActorName());
        assertTrue(admin.toString().contains("Admin: Asta"));
        assertTrue(admin.toString().contains("Username: asta_admin"));
    }
}
