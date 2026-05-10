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
import java.util.List;

import static org.junit.Assert.assertTrue;

public class DataFileWriterTest {
    @Test(expected = InvalidFileTypeException.class)
    public void forCsvRejectsNonCsvFilePath() throws DataFileException {
        DataFileWriter.forCsv("invalid.txt");
    }

    @Test
    public void csvWriterWritesJoinedRows() throws Exception {
        Path file = Files.createTempFile("writer-", ".csv");
        DataFileWriter writer = DataFileWriter.forCsv(file.toString());
        try {
            writer.writeRow(List.of("id", "name"));
            writer.writeRow(List.of("Q1", "Hunt"));
        } finally {
            writer.close();
        }

        String content = Files.readString(file);
        assertTrue(content.contains("id,name"));
        assertTrue(content.contains("Q1,Hunt"));
    }

    @Test
    public void txtWriterWritesHeaderRowsAndSeparator() throws Exception {
        Path file = Files.createTempFile("writer-", ".txt");
        DataFileWriter writer = DataFileWriter.forTxt(file.toString());
        try {
            writer.writeHeader(List.of("ID", "Name"));
            writer.writeRow(List.of("Q1", "Hunt"));
            writer.writeSeparator();
        } finally {
            writer.close();
        }

        String content = Files.readString(file);
        assertTrue(content.contains("+--------------------+--------------------+"));
        assertTrue(content.contains("| ID"));
        assertTrue(content.contains("| Q1"));
    }
}
