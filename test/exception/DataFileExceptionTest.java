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
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;

public class DataFileExceptionTest {
    @Test
    public void dataFileExceptionIsBurhanQuestException() {
        DataFileException exception = new DataFileException("io");
        assertTrue(exception instanceof BurhanQuestException);
        assertEquals("io", exception.getMessage());
    }

    @Test
    public void dataFileExceptionKeepsCause() {
        Throwable cause = new RuntimeException("disk");
        DataFileException exception = new DataFileException("io", cause);
        assertSame(cause, exception.getCause());
    }
}
