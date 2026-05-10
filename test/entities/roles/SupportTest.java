package test.entities.roles;

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
import static org.junit.Assert.assertTrue;

public class SupportTest {
    @Test
    public void supportHealsAtTurnStartWithoutExceedingMaxHp() {
        Support support = createSupport();
        support.takeDamage(40);

        support.onTurnStart();

        assertEquals(70, support.getCurrentHp(), 0.0001);
        assertTrue(support.consumeCustomDamageNote().contains("memulihkan"));
    }

    @Test
    public void supportHealAtMaxHpDoesNotOverflow() {
        Support support = createSupport();
        support.onTurnStart();
        assertEquals(support.getMaxHp(), support.getCurrentHp(), 0.0001);
    }

    private Support createSupport() {
        return new Support(1, "Heiter", "heiter", "123", 100, 14, 9);
    }
}
