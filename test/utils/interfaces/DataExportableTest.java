package test.utils.interfaces;

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

public class DataExportableTest {
    private static class DummyExportable implements DataExportable {
        private String lastCall;

        @Override
        public void exportToCsv(String path, AdminIOCsvMode mode) {
            lastCall = "csv:" + path + ":" + mode;
        }

        @Override
        public void exportToTxt(String path, AdminIOCsvMode mode) {
            lastCall = "txt:" + path + ":" + mode;
        }
    }

    @Test
    public void contractMethodsCanBeInvoked() throws DataFileException {
        DummyExportable exportable = new DummyExportable();
        exportable.exportToCsv("a.csv", AdminIOCsvMode.QUEST);
        assertEquals("csv:a.csv:QUEST", exportable.lastCall);

        exportable.exportToTxt("a.txt", AdminIOCsvMode.WANDERER);
        assertEquals("txt:a.txt:WANDERER", exportable.lastCall);
    }
}
