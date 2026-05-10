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

public class FighterTest {
    @Test
    public void furyStackIncreasesDamageAfterSuccessfulTurns() {
        Fighter fighter = createFighter();
        double base = 20;

        fighter.onTurnEnd(5);
        double increased = fighter.modifyDamageDealt(base);

        assertEquals(base + (fighter.getAttackPower() * 0.1), increased, 0.0001);
        assertTrue(fighter.consumeCustomDamageNote().contains("Fury"));
    }

    @Test
    public void furyStackResetsAfterReachingMaximum() {
        Fighter fighter = createFighter();
        fighter.onTurnEnd(1);
        fighter.onTurnEnd(1);
        fighter.onTurnEnd(1);
        fighter.onTurnEnd(1);

        assertTrue(fighter.consumeCustomDamageNote().contains("Reset"));

        double nextDamage = fighter.modifyDamageDealt(30);
        assertEquals(30, nextDamage, 0.0001);
    }

    private Fighter createFighter() {
        return new Fighter(1, "Stark", "stark", "123", 110, 25, 12);
    }
}
