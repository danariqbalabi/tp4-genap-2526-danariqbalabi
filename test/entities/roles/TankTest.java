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
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

public class TankTest {
    @Test
    public void shieldDoesNotTriggerAboveThirtyPercentHp() {
        Tank tank = createTank();
        double damage = tank.modifyDamageTaken(40);
        assertEquals(40, damage, 0.0001);
        assertNull(tank.consumeCustomDamageNote());
    }

    @Test
    public void shieldCutsDamageByHalfWhenLowHp() {
        Tank tank = createTank();
        tank.setCurrentHp(30); // 25% of max HP (120)

        double damage = tank.modifyDamageTaken(40);

        assertEquals(20, damage, 0.0001);
        assertTrue(tank.consumeCustomDamageNote().contains("Shield"));
    }

    private Tank createTank() {
        return new Tank(1, "Eisen", "eisen", "123", 120, 15, 20);
    }
}
