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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class DataFileWriterModeTest {
    @Test
    public void enumContainsCsvAndTxtModes() {
        EnumSet<DataFileWriterMode> modes = EnumSet.allOf(DataFileWriterMode.class);
        assertEquals(2, modes.size());
        assertTrue(modes.contains(DataFileWriterMode.CSV));
        assertTrue(modes.contains(DataFileWriterMode.TXT));
    }
}
