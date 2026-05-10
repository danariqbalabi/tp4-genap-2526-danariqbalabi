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

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class WandererExporterTest {
    @Test(expected = IllegalArgumentException.class)
    public void constructorRejectsNullGameManager() {
        new WandererExporter(null);
    }

    @Test
    public void exportToTxtWritesWandererProfileAndQuestHistory() throws Exception {
        GameManager manager = new GameManager();
        Admin admin = (Admin) manager.login("burhan", "burunghantu123");
        Wanderer wanderer = manager.registerWanderer(admin, "mage", "Frieren", "frieren_export", "123", 100, 25, 10);
        Quest quest = createRegularQuest();
        wanderer.recordQuestResult("MENANG", quest, 2);

        WandererExporter exporter = new WandererExporter(manager, wanderer);
        Path out = Files.createTempFile("wanderer-report-", ".txt");
        exporter.exportToTxt(out.toString(), AdminIOCsvMode.WANDERER);

        String content = Files.readString(out);
        assertTrue(content.contains("LAPORAN DATA PENGEMBARA"));
        assertTrue(content.contains("Frieren"));
        assertTrue(content.contains("Riwayat Quest"));
        assertTrue(content.contains("MENANG"));
    }

    @Test
    public void getReportLinesUsesFirstWandererWhenNoSpecificTargetSet() throws Exception {
        GameManager manager = new GameManager();
        Admin admin = (Admin) manager.login("burhan", "burunghantu123");
        manager.registerWanderer(admin, "1", "Fern", "fern_export", "123", 90, 18, 9);

        WandererExporter exporter = new WandererExporter(manager);
        ArrayList<String> lines = exporter.getReportLines("dummy.txt");

        assertTrue(lines.stream().anyMatch(line -> line.contains("Fern")));
    }

    @Test
    public void exportToCsvIsNotSupportedAndThrowsInvalidFormat() {
        GameManager manager = new GameManager();
        WandererExporter exporter = new WandererExporter(manager);
        try {
            exporter.exportToCsv("x.csv", AdminIOCsvMode.WANDERER);
        } catch (InvalidFormatException expected) {
            assertTrue(expected.getMessage().contains("x.csv"));
            return;
        } catch (DataFileException unexpected) {
            throw new AssertionError(unexpected);
        }
        throw new AssertionError("Expected InvalidFormatException");
    }

    @Test
    public void exportToTxtRejectsNonTxtExtension() {
        GameManager manager = new GameManager();
        WandererExporter exporter = new WandererExporter(manager);
        try {
            exporter.exportToTxt("x.csv", AdminIOCsvMode.WANDERER);
        } catch (InvalidFileTypeException expected) {
            assertTrue(expected.getMessage().contains("x.csv"));
            return;
        } catch (DataFileException unexpected) {
            throw new AssertionError(unexpected);
        }
        throw new AssertionError("Expected InvalidFileTypeException");
    }

    private Quest createRegularQuest() {
        Monster slime = new Monster(1, "Slime", 30, 5, 2, 10, 4);
        return new RegularQuest(1, "Hunt Slime", "Clear nearby field", Difficulty.MUDAH, slime, 1);
    }
}
