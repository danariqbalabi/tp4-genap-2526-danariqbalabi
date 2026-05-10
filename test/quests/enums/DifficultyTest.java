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

public class DifficultyTest {
    @Test
    public void difficultyValuesExposeDisplayNameAndMinimumLevel() {
        assertEquals("mudah", Difficulty.MUDAH.getDisplayName());
        assertEquals(1, Difficulty.MUDAH.getMinWandererLevel());
        assertEquals(6, Difficulty.MENENGAH.getMinWandererLevel());
        assertEquals(16, Difficulty.SULIT.getMinWandererLevel());
    }

    @Test
    public void fromStringSupportsCaseInsensitiveMatch() {
        assertEquals(Difficulty.MUDAH, Difficulty.fromString(" MUDAH "));
        assertEquals(Difficulty.MENENGAH, Difficulty.fromString("menengah"));
        assertNull(Difficulty.fromString("unknown"));
        assertNull(Difficulty.fromString(null));
    }
}
