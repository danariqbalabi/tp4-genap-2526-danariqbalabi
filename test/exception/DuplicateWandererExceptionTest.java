package test.exception;

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

public class DuplicateWandererExceptionTest {
    @Test
    public void messageAndUsernameAreAvailable() {
        DuplicateWandererException exception = new DuplicateWandererException("frieren");

        assertEquals("frieren", exception.getUsername());
        assertEquals("Username 'frieren' sudah digunakan.", exception.getMessage());
    }
}
