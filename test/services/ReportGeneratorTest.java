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
import static org.junit.Assert.fail;

public class ReportGeneratorTest {
    @Test(expected = IllegalArgumentException.class)
    public void constructorRejectsNullGameManager() {
        new ReportGenerator(null);
    }

    @Test
    public void exportQuestAndWandererCsvCreatesExpectedHeaders() throws Exception {
        GameManager manager = new GameManager();
        Admin admin = (Admin) manager.login("burhan", "burunghantu123");
        manager.registerMonster(admin, "Slime", 30, 5, 2, 10, 4);
        manager.registerQuest(admin, "daily", "Daily Slime", "desc", "mudah", 1, 0, 0);
        manager.registerWanderer(admin, "1", "Frieren", "frieren_csv", "123", 100, 20, 10);

        ReportGenerator generator = new ReportGenerator(manager);
        Path questFile = Files.createTempFile("quest-export-", ".csv");
        Path wandererFile = Files.createTempFile("wanderer-export-", ".csv");

        generator.exportToCsv(questFile.toString(), AdminIOCsvMode.QUEST);
        generator.exportToCsv(wandererFile.toString(), AdminIOCsvMode.WANDERER);

        String questContent = Files.readString(questFile);
        String wandererContent = Files.readString(wandererFile);
        assertTrue(questContent.startsWith("id,name,description,difficulty,type,monster_name,minimum_level,status"));
        assertTrue(wandererContent.startsWith("id,name,username,level,exp,coins,current_hp,max_hp,attack,defense"));
    }

    @Test
    public void exportTxtAndPreviewCsvWork() throws Exception {
        GameManager manager = new GameManager();
        Admin admin = (Admin) manager.login("burhan", "burunghantu123");
        manager.registerMonster(admin, "Slime", 30, 5, 2, 10, 4);
        manager.registerQuest(admin, "daily", "Daily Slime", "desc", "mudah", 1, 0, 0);

        ReportGenerator generator = new ReportGenerator(manager);
        Path txtFile = Files.createTempFile("quest-export-", ".txt");
        generator.exportToTxt(txtFile.toString(), AdminIOCsvMode.QUEST);
        assertTrue(Files.readString(txtFile).contains("ID Quest"));

        Path csvFile = Files.createTempFile("preview-", ".csv");
        Files.writeString(csvFile, "id,name\nQ1,Quest One\n");
        ArrayList<ArrayList<String>> preview = generator.previewCsv(csvFile.toString());
        assertEquals(2, preview.size());
        assertEquals("id", preview.get(0).get(0));
        assertEquals("Q1", preview.get(1).get(0));
    }

    @Test
    public void importQuestAndWandererFromCsvAddsData() throws Exception {
        GameManager manager = new GameManager();
        Admin admin = (Admin) manager.login("burhan", "burunghantu123");
        manager.registerMonster(admin, "Slime", 30, 5, 2, 10, 4);
        ReportGenerator generator = new ReportGenerator(manager);

        Path questCsv = Files.createTempFile("quest-import-", ".csv");
        Files.writeString(questCsv,
                "name,description,difficulty,type,monster_name,bonus_exp,bonus_coin\n"
                        + "Bounty Slime,desc,mudah,bounty,Slime,3,1\n");
        generator.importFromCsv(questCsv.toString(), AdminIOCsvMode.QUEST);
        assertEquals(1, manager.getQuests().size());

        Path wandererCsv = Files.createTempFile("wanderer-import-", ".csv");
        Files.writeString(wandererCsv,
                "name,username,password,max_hp,attack,defense,job,level,exp,coins,current_hp\n"
                        + "Fern,fern_csv,123,90,18,9,mage,4,300,20,70\n");
        generator.importFromCsv(wandererCsv.toString(), AdminIOCsvMode.WANDERER);
        assertEquals(1, manager.getWandererObjects().size());
        assertEquals("fern_csv", manager.getWandererObjects().get(0).getUsername());
    }

    @Test
    public void exportRejectsUnsupportedMode() throws Exception {
        GameManager manager = new GameManager();
        ReportGenerator generator = new ReportGenerator(manager);
        Path file = Files.createTempFile("quest-export-", ".csv");

        try {
            generator.exportToCsv(file.toString(), null);
            fail("Expected InvalidFormatException");
        } catch (InvalidFormatException expected) {
            assertTrue(expected.getMessage().contains(file.toString()));
        }

        Path txt = Files.createTempFile("quest-export-", ".txt");
        try {
            generator.exportToTxt(txt.toString(), null);
            fail("Expected InvalidFormatException");
        } catch (InvalidFormatException expected) {
            assertTrue(expected.getMessage().contains(txt.toString()));
        }
    }

    @Test
    public void headerAndRowApisReturnEmptyForUnsupportedMode() throws Exception {
        GameManager manager = new GameManager();
        ReportGenerator generator = new ReportGenerator(manager);
        assertTrue(generator.getReportHeaders(null).isEmpty());
        assertTrue(generator.getReportRows(null).isEmpty());
    }

    @Test
    public void previewCsvRejectsEmptyFile() throws Exception {
        GameManager manager = new GameManager();
        ReportGenerator generator = new ReportGenerator(manager);
        Path empty = Files.createTempFile("empty-", ".csv");
        Files.writeString(empty, "");

        try {
            generator.previewCsv(empty.toString());
            fail("Expected InvalidFormatException");
        } catch (InvalidFormatException expected) {
            assertTrue(expected.getMessage().contains(empty.toString()));
        }
    }

    @Test
    public void importRejectsMalformedQuestRowsAndUnknownType() throws Exception {
        GameManager manager = new GameManager();
        Admin admin = (Admin) manager.login("burhan", "burunghantu123");
        manager.registerMonster(admin, "Slime", 30, 5, 2, 10, 4);
        ReportGenerator generator = new ReportGenerator(manager);

        Path mismatch = Files.createTempFile("quest-mismatch-", ".csv");
        Files.writeString(mismatch, "name,type,difficulty,monster_name\nonly_name\n");
        try {
            generator.importFromCsv(mismatch.toString(), AdminIOCsvMode.QUEST);
            fail("Expected InvalidFormatException");
        } catch (InvalidFormatException expected) {
            assertTrue(expected.getMessage().contains(mismatch.toString()));
        }

        Path unknownType = Files.createTempFile("quest-unknown-", ".csv");
        Files.writeString(unknownType,
                "name,description,difficulty,type,monster_name,bonus_exp,bonus_coin\n"
                        + "Quest X,desc,mudah,strange,Slime,0,0\n");
        try {
            generator.importFromCsv(unknownType.toString(), AdminIOCsvMode.QUEST);
            fail("Expected InvalidFormatException");
        } catch (InvalidFormatException expected) {
            assertTrue(expected.getMessage().contains(unknownType.toString()));
        }
    }

    @Test
    public void importRejectsInvalidWandererDataAndDuplicateUsername() throws Exception {
        GameManager manager = new GameManager();
        Admin admin = (Admin) manager.login("burhan", "burunghantu123");
        manager.registerWanderer(admin, "1", "Existing", "existing_user", "123", 100, 20, 10);
        ReportGenerator generator = new ReportGenerator(manager);

        Path invalidStats = Files.createTempFile("wanderer-invalid-", ".csv");
        Files.writeString(invalidStats,
                "name,username,password,max_hp,attack,defense,job,level,exp,coins,current_hp\n"
                        + "Fern,fern_invalid,123,90,0,9,mage,4,300,20,70\n");
        try {
            generator.importFromCsv(invalidStats.toString(), AdminIOCsvMode.WANDERER);
            fail("Expected InvalidFormatException");
        } catch (InvalidFormatException expected) {
            assertTrue(expected.getMessage().contains(invalidStats.toString()));
        }

        Path duplicate = Files.createTempFile("wanderer-duplicate-", ".csv");
        Files.writeString(duplicate,
                "name,username,password,max_hp,attack,defense,job,level,exp,coins,current_hp\n"
                        + "Fern,existing_user,123,90,18,9,mage,4,300,20,70\n");
        try {
            generator.importFromCsv(duplicate.toString(), AdminIOCsvMode.WANDERER);
            fail("Expected DuplicateWandererException");
        } catch (DuplicateWandererException expected) {
            assertEquals("existing_user", expected.getUsername());
        }
    }

    @Test
    public void importRejectsNullMode() throws Exception {
        GameManager manager = new GameManager();
        ReportGenerator generator = new ReportGenerator(manager);
        Path file = Files.createTempFile("any-", ".csv");
        Files.writeString(file, "a,b\n1,2\n");
        try {
            generator.importFromCsv(file.toString(), null);
            fail("Expected InvalidFormatException");
        } catch (InvalidFormatException expected) {
            assertTrue(expected.getMessage().contains(file.toString()));
        }
    }

    @Test
    public void importQuestRejectsUnknownMonsterAndInvalidDailyBonus() throws Exception {
        GameManager manager = new GameManager();
        Admin admin = (Admin) manager.login("burhan", "burunghantu123");
        manager.registerMonster(admin, "Slime", 30, 5, 2, 10, 4);
        ReportGenerator generator = new ReportGenerator(manager);

        Path unknownMonster = Files.createTempFile("quest-unknown-monster-", ".csv");
        Files.writeString(unknownMonster,
                "name,description,difficulty,type,monster_name,bonus_exp,bonus_coin\n"
                        + "Quest X,desc,mudah,bounty,NoMonster,2,1\n");
        try {
            generator.importFromCsv(unknownMonster.toString(), AdminIOCsvMode.QUEST);
            fail("Expected InvalidFormatException");
        } catch (InvalidFormatException expected) {
            assertTrue(expected.getMessage().contains(unknownMonster.toString()));
        }

        Path dailyBonus = Files.createTempFile("quest-daily-bonus-", ".csv");
        Files.writeString(dailyBonus,
                "name,description,difficulty,type,monster_name,bonus_exp,bonus_coin\n"
                        + "Quest Daily,desc,mudah,daily,Slime,1,0\n");
        try {
            generator.importFromCsv(dailyBonus.toString(), AdminIOCsvMode.QUEST);
            fail("Expected InvalidFormatException");
        } catch (InvalidFormatException expected) {
            assertTrue(expected.getMessage().contains(dailyBonus.toString()));
        }
    }

    @Test
    public void importWandererSupportsMaxHpNormalizationAndRejectsInvalidNumber() throws Exception {
        GameManager manager = new GameManager();
        ReportGenerator generator = new ReportGenerator(manager);

        Path normalizeHp = Files.createTempFile("wanderer-normalize-", ".csv");
        Files.writeString(normalizeHp,
                "name,username,password,max_hp,attack,defense,job,level,exp,coins,current_hp\n"
                        + "Fern,fern_norm,123,0,18,9,mage,4,300,20,70\n");
        generator.importFromCsv(normalizeHp.toString(), AdminIOCsvMode.WANDERER);
        Wanderer imported = manager.getWandererObjects().get(0);
        assertEquals(1.0, imported.getMaxHp(), 0.0001);
        assertEquals(1.0, imported.getCurrentHp(), 0.0001);

        Path invalidNumber = Files.createTempFile("wanderer-invalid-num-", ".csv");
        Files.writeString(invalidNumber,
                "name,username,password,max_hp,attack,defense,job,level,exp,coins,current_hp\n"
                        + "Stark,stark_norm,123,90,abc,9,fighter,4,300,20,70\n");
        try {
            generator.importFromCsv(invalidNumber.toString(), AdminIOCsvMode.WANDERER);
            fail("Expected InvalidFormatException");
        } catch (InvalidFormatException expected) {
            assertTrue(expected.getMessage().contains(invalidNumber.toString()));
        }
    }
}
