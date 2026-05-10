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

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

public class DataFileReaderTest {
    @Test(expected = InvalidFileTypeException.class)
    public void constructorRejectsNonCsvExtension() throws DataFileException {
        new DataFileReader("quests.txt");
    }

    @Test
    public void readNextParsesCommaSeparatedValues() throws Exception {
        Path file = Files.createTempFile("reader-", ".csv");
        Files.writeString(file, "id,name\nQ1, Hunt Slime\n");

        DataFileReader reader = new DataFileReader(file.toString());
        try {
            ArrayList<String> header = reader.readNext();
            ArrayList<String> row = reader.readNext();
            ArrayList<String> eof = reader.readNext();

            assertEquals("id", header.get(0));
            assertEquals("name", header.get(1));
            assertEquals("Q1", row.get(0));
            assertEquals("Hunt Slime", row.get(1));
            assertNull(eof);
        } finally {
            reader.close();
        }
    }

    @Test
    public void readNextThrowsInvalidFormatForBlankLine() throws Exception {
        Path file = Files.createTempFile("reader-blank-", ".csv");
        Files.writeString(file, "id,name\n\n");

        DataFileReader reader = new DataFileReader(file.toString());
        try {
            reader.readNext();
            try {
                reader.readNext();
            } catch (InvalidFormatException exception) {
                assertTrue(exception.getMessage().contains("baris kosong"));
                return;
            }
        } finally {
            reader.close();
        }
        throw new AssertionError("Expected InvalidFormatException");
    }
}
