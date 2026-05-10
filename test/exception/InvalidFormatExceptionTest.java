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

import static org.junit.Assert.assertTrue;

public class InvalidFormatExceptionTest {
    @Test
    public void messageContainsPath() {
        InvalidFormatException exception = new InvalidFormatException("quests.csv");
        assertTrue(exception.getMessage().contains("quests.csv"));
    }
}
