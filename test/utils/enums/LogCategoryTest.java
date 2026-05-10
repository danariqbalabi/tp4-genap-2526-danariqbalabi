package test.utils.enums;

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

import java.util.EnumSet;

import static org.junit.Assert.assertTrue;

public class LogCategoryTest {
    @Test
    public void enumContainsAllRequiredCategories() {
        EnumSet<LogCategory> categories = EnumSet.allOf(LogCategory.class);
        assertTrue(categories.contains(LogCategory.AUTH));
        assertTrue(categories.contains(LogCategory.ADMIN));
        assertTrue(categories.contains(LogCategory.PENGEMBARA));
        assertTrue(categories.contains(LogCategory.BATTLE));
        assertTrue(categories.contains(LogCategory.SYSTEM));
        assertTrue(categories.contains(LogCategory.ERROR));
    }
}
