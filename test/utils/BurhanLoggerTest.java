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

import static org.junit.Assert.assertTrue;

public class BurhanLoggerTest {
    @Test
    public void loggerWritesBasicMessageToConfiguredFile() throws Exception {
        Path tempFile = Files.createTempFile("burhan-logger-", ".log");
        BurhanLogger logger = BurhanLogger.getInstance();
        logger.setFilePath(tempFile.toString());

        logger.log(LogCategory.SYSTEM, "Sistem", "Hari berganti");

        String content = Files.readString(tempFile);
        assertTrue(content.contains("[SYSTEM]"));
        assertTrue(content.contains("[Sistem] - Hari berganti"));
    }

    @Test
    public void loggerWritesExceptionTypeForErrorLog() throws Exception {
        Path tempFile = Files.createTempFile("burhan-logger-error-", ".log");
        BurhanLogger logger = BurhanLogger.getInstance();
        logger.setFilePath(tempFile.toString());

        logger.log(LogCategory.ERROR, "Sistem", "Terjadi kesalahan sistem", new IllegalArgumentException("bad"));

        String content = Files.readString(tempFile);
        assertTrue(content.contains("[ERROR]"));
        assertTrue(content.contains("IllegalArgumentException"));
    }
}
