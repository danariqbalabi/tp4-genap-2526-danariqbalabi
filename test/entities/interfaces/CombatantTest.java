package test.entities.interfaces;

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
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class CombatantTest {
    private static class DummyCombatant implements Combatant {
        private double hp = 10;

        @Override
        public String getName() {
            return "Dummy";
        }

        @Override
        public double getAttackPower() {
            return 5;
        }

        @Override
        public double getDefense() {
            return 2;
        }

        @Override
        public void takeDamage(double damage) {
            hp = Math.max(0, hp - damage);
        }

        @Override
        public boolean isDefeated() {
            return hp <= 0;
        }

        @Override
        public String getCombatInfo() {
            return "Dummy|HP:" + hp;
        }
    }

    @Test
    public void combatantContractCanBeImplemented() {
        DummyCombatant combatant = new DummyCombatant();

        assertEquals("Dummy", combatant.getName());
        assertFalse(combatant.isDefeated());
        combatant.takeDamage(15);
        assertTrue(combatant.isDefeated());
        assertTrue(combatant.getCombatInfo().contains("HP:0.0"));
    }
}
