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

public class BurhanQuestExceptionTest {
    @Test
    public void exceptionStoresMessage() {
        BurhanQuestException exception = new BurhanQuestException("boom");
        assertEquals("boom", exception.getMessage());
    }

    @Test
    public void exceptionStoresCause() {
        Throwable cause = new IllegalStateException("root");
        BurhanQuestException exception = new BurhanQuestException("boom", cause);
        assertSame(cause, exception.getCause());
    }
}
