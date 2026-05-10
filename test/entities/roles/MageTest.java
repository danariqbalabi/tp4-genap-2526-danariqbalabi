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

public class MageTest {
    @Test
    public void mageChargesThenBurstsOnNextAttack() {
        Mage mage = createMage();

        mage.onTurnEnd(10);
        assertTrue(mage.consumeCustomDamageNote().contains("Overcharged"));

        double burstDamage = mage.modifyDamageDealt(5);
        assertEquals(mage.getAttackPower() * 2, burstDamage, 0.0001);
        assertTrue(mage.consumeCustomDamageNote().contains("Arcane Burst"));
    }

    @Test
    public void resetBattleStateClearsOverchargeState() {
        Mage mage = createMage();
        mage.onTurnEnd(10);
        mage.resetBattleState();

        double regularDamage = mage.modifyDamageDealt(15);
        assertEquals(15, regularDamage, 0.0001);
    }

    private Mage createMage() {
        return new Mage(1, "Fern", "fern", "123", 80, 30, 8);
    }
}
