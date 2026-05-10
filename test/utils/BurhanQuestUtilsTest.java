package test.utils;

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

import java.util.ArrayList;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class BurhanQuestUtilsTest {
    @Test
    public void formatDoubleRemovesTrailingZerosAndUsesDot() {
        assertEquals("2", BurhanQuestUtils.formatDouble(2.0));
        assertEquals("2.5", BurhanQuestUtils.formatDouble(2.50));
        assertEquals("2.57", BurhanQuestUtils.formatDouble(2.567));
    }

    @Test
    public void capitalizeHandlesNullAndNormalWord() {
        assertEquals("", BurhanQuestUtils.capitalize(null));
        assertEquals("", BurhanQuestUtils.capitalize(""));
        assertEquals("Frieren", BurhanQuestUtils.capitalize("fRIEREN"));
    }

    @Test
    public void isBlankHandlesNullWhitespaceAndText() {
        assertTrue(BurhanQuestUtils.isBlank(null));
        assertTrue(BurhanQuestUtils.isBlank("   "));
        assertFalse(BurhanQuestUtils.isBlank("ok"));
    }

    @Test
    public void buildTableProducesFormattedAsciiTable() {
        ArrayList<String> headers = new ArrayList<>();
        headers.add("ID");
        headers.add("Name");

        ArrayList<ArrayList<String>> rows = new ArrayList<>();
        ArrayList<String> row = new ArrayList<>();
        row.add("Q1");
        row.add("Hunt");
        rows.add(row);

        ArrayList<String> table = BurhanQuestUtils.buildTable(headers, rows);
        assertTrue(table.get(0).startsWith("+"));
        assertTrue(table.get(1).contains("ID"));
        assertTrue(table.get(3).contains("Q1"));
    }
}
