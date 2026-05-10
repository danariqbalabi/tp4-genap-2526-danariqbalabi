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

public class AssassinTest {
    @Test
    public void damageValueRemainsBaseWhenNoTargetIsSet() {
        Assassin assassin = createAssassin();
        double dealt = assassin.modifyDamageDealt(17.5);
        assertEquals(17.5, dealt, 0.0001);
    }

    @Test
    public void assassinCanApplyBleedWithoutBreakingDamageFlow() {
        Assassin assassin = createAssassin();
        Monster monster = createSlime();
        assassin.setBattleContext(monster, 1.0);

        for (int i = 0; i < 30; i++) {
            assertEquals(10, assassin.modifyDamageDealt(10), 0.0001);
        }

        monster.onTurnStart();
        assertTrue(monster.getCurrentHp() <= monster.getMaxHp());
    }

    private Assassin createAssassin() {
        return new Assassin(1, "Shadow", "shadow", "123", 90, 22, 7);
    }

    private Monster createSlime() {
        return new Monster(1, "Slime", 30, 5, 2, 10, 4);
    }
}
