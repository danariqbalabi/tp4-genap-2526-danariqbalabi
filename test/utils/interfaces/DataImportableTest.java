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

public class DataImportableTest {
    private static class DummyImportable implements DataImportable {
        private String imported;

        @Override
        public void importFromCsv(String path, AdminIOCsvMode mode) {
            imported = path + ":" + mode;
        }
    }

    @Test
    public void contractMethodCanBeInvoked() throws BurhanQuestException {
        DummyImportable importable = new DummyImportable();
        importable.importFromCsv("in.csv", AdminIOCsvMode.QUEST);
        assertEquals("in.csv:QUEST", importable.imported);
    }
}
