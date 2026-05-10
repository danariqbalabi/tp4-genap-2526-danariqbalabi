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

public class InsufficientLevelExceptionTest {
    @Test
    public void messageIncludesCurrentAndMinimumLevel() {
        InsufficientLevelException exception = new InsufficientLevelException(3, 6);
        assertTrue(exception.getMessage().contains("Level saat ini: 3"));
        assertTrue(exception.getMessage().contains("minimum: 6"));
    }
}
