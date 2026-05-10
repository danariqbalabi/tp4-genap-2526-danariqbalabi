package test.quests.enums;

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

public class QuestStatusTest {
    @Test
    public void displayNameMatchesExpectedValue() {
        assertEquals("tersedia", QuestStatus.TERSEDIA.getDisplayName());
        assertEquals("selesai", QuestStatus.SELESAI.getDisplayName());
    }

    @Test
    public void fromStringHandlesValidAndInvalidInput() {
        assertEquals(QuestStatus.SELESAI, QuestStatus.fromString(" selesai "));
        assertEquals(QuestStatus.TERSEDIA, QuestStatus.fromString("TERSEDIA"));
        assertNull(QuestStatus.fromString("invalid"));
        assertNull(QuestStatus.fromString(null));
    }
}
