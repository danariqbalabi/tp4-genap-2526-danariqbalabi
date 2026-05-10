package test.services;

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
import static org.junit.Assert.assertNull;

public class AdminIOCsvModeTest {
    @Test
    public void modeExposesDisplayName() {
        assertEquals("quest", AdminIOCsvMode.QUEST.getDisplayName());
        assertEquals("wanderer", AdminIOCsvMode.WANDERER.getDisplayName());
    }

    @Test
    public void fromStringAcceptsDisplayNameAndEnumName() {
        assertEquals(AdminIOCsvMode.QUEST, AdminIOCsvMode.fromString("quest"));
        assertEquals(AdminIOCsvMode.WANDERER, AdminIOCsvMode.fromString("WANDERER"));
        assertNull(AdminIOCsvMode.fromString("none"));
        assertNull(AdminIOCsvMode.fromString(null));
    }
}
