package com.gachapet.test;

import com.gachapet.model.AbstractPet;
import com.gachapet.model.Cat;
import com.gachapet.model.Dog;
import com.gachapet.model.GachaSystem;
import com.gachapet.model.MythicPet;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for GachaSystem.
 *
 * <p>These tests cover:</p>
 * <ul>
 *   <li>Drop rate configuration</li>
 *   <li>Roll return types</li>
 *   <li>Initial pet state</li>
 *   <li>Ten-roll behavior</li>
 *   <li>Pity system</li>
 * </ul>
 */
@DisplayName("Gacha System Tests")
class GachaTest {

    private static final double TOLERANCE = 0.05;
    private static final int SAMPLE_SIZE = 10_000;
    private static final long TEST_SEED = 12345L;

    private GachaSystem gacha;

    @BeforeEach
    void setUp() {
        gacha = new GachaSystem(TEST_SEED);
    }

    // ==================== Drop Rate Configuration Tests ====================

    @Test
    @DisplayName("Drop rates should add up to 100 percent")
    void testTotalDropRateEqualsOneHundredPercent() {
        double total = GachaSystem.CAT_RATE + GachaSystem.DOG_RATE + GachaSystem.MYTHIC_RATE;

        assertEquals(
                1.0,
                total,
                0.0001,
                "The total drop rate should be exactly 1.0."
        );
    }

    @Test
    @DisplayName("Gacha cost should be positive")
    void testGachaCostIsPositive() {
        assertTrue(GachaSystem.GACHA_COST > 0, "GACHA_COST should be greater than 0.");
    }

    // ==================== Statistical Drop Rate Tests ====================

    @Test
    @DisplayName("MythicPet drop rate should be close to 10 percent")
    void testMythicDropRateApproximatelyTenPercent() {
        int mythicCount = 0;

        for (int i = 0; i < SAMPLE_SIZE; i++) {
            if (gacha.roll() instanceof MythicPet) {
                mythicCount++;
            }
        }

        double actualRate = (double) mythicCount / SAMPLE_SIZE;

        assertEquals(
                GachaSystem.MYTHIC_RATE,
                actualRate,
                TOLERANCE,
                "MythicPet drop rate should be close to the configured rate."
        );
    }

    @Test
    @DisplayName("Dog drop rate should be close to 40 percent")
    void testDogDropRateApproximatelyFortyPercent() {
        int dogCount = 0;

        for (int i = 0; i < SAMPLE_SIZE; i++) {
            if (gacha.roll() instanceof Dog) {
                dogCount++;
            }
        }

        double actualRate = (double) dogCount / SAMPLE_SIZE;

        assertEquals(
                GachaSystem.DOG_RATE,
                actualRate,
                TOLERANCE,
                "Dog drop rate should be close to the configured rate."
        );
    }

    @Test
    @DisplayName("Cat drop rate should be close to 50 percent")
    void testCatDropRateApproximatelyFiftyPercent() {
        int catCount = 0;

        for (int i = 0; i < SAMPLE_SIZE; i++) {
            if (gacha.roll() instanceof Cat) {
                catCount++;
            }
        }

        double actualRate = (double) catCount / SAMPLE_SIZE;

        assertEquals(
                GachaSystem.CAT_RATE,
                actualRate,
                TOLERANCE,
                "Cat drop rate should be close to the configured rate."
        );
    }

    // ==================== Roll Return Type Tests ====================

    @Test
    @DisplayName("roll() should never return null")
    void testRollNeverReturnsNull() {
        for (int i = 0; i < 100; i++) {
            AbstractPet pet = gacha.roll();

            assertNotNull(pet, "roll() should never return null.");
        }
    }

    @Test
    @DisplayName("roll() should always return a valid pet type")
    void testRollReturnsValidPetType() {
        for (int i = 0; i < 100; i++) {
            AbstractPet pet = gacha.roll();

            assertTrue(
                    pet instanceof Cat || pet instanceof Dog || pet instanceof MythicPet,
                    "roll() should return Cat, Dog, or MythicPet only."
            );
        }
    }

    @Test
    @DisplayName("roll() should return an AbstractPet subtype")
    void testRollReturnsAbstractPetSubtype() {
        AbstractPet pet = gacha.roll();

        assertInstanceOf(
                AbstractPet.class,
                pet,
                "roll() should return an object that is an AbstractPet subtype."
        );
    }

    @Test
    @DisplayName("Rolled pet should have a valid initial state")
    void testRolledPetHasValidInitialState() {
        AbstractPet pet = gacha.roll();

        assertNotNull(pet.getName(), "Pet name should not be null.");
        assertFalse(pet.getName().trim().isEmpty(), "Pet name should not be empty.");

        assertEquals(AbstractPet.MAX_HP, pet.getHp(), "HP should start at maximum.");
        assertEquals(AbstractPet.MAX_HUNGER, pet.getHunger(), "Hunger should start at maximum.");
        assertEquals(AbstractPet.MAX_HAPPINESS, pet.getHappiness(), "Happiness should start at maximum.");
        assertEquals(AbstractPet.MAX_ENERGY, pet.getEnergy(), "Energy should start at maximum.");
        assertEquals(1, pet.getLevel(), "Level should start at 1.");

        assertNotNull(pet.getPetType(), "Pet type should not be null.");
        assertNotNull(pet.getEmoji(), "Pet emoji should not be null.");
        assertNotNull(pet.getSpecialSkill(), "Special skill should not be null.");
    }

    // ==================== Ten-Roll / Pity System Tests ====================

    @Test
    @DisplayName("rollTen() should return exactly ten pets")
    void testRollTenReturnsExactlyTenPets() {
        AbstractPet[] results = gacha.rollTen();

        assertNotNull(results, "rollTen() should not return null.");
        assertEquals(10, results.length, "rollTen() should return exactly ten pets.");
    }

    @Test
    @DisplayName("rollTen() should not contain null elements")
    void testRollTenNoNullElements() {
        AbstractPet[] results = gacha.rollTen();

        for (int i = 0; i < results.length; i++) {
            assertNotNull(results[i], "Pet at index " + i + " should not be null.");
        }
    }

    @Test
    @DisplayName("rollTen() should guarantee at least one MythicPet")
    void testRollTenGuaranteesMythicPet() {
        for (int round = 0; round < 20; round++) {
            AbstractPet[] results = gacha.rollTen();

            boolean hasMythic = false;

            for (AbstractPet pet : results) {
                if (pet instanceof MythicPet) {
                    hasMythic = true;
                    break;
                }
            }

            assertTrue(
                    hasMythic,
                    "rollTen() should guarantee at least one MythicPet because of the pity system."
            );
        }
    }

    @Test
    @DisplayName("canRoll() should return true only when coins are enough")
    void testCanRoll() {
        assertTrue(gacha.canRoll(GachaSystem.GACHA_COST));
        assertTrue(gacha.canRoll(GachaSystem.GACHA_COST + 1));
        assertFalse(gacha.canRoll(GachaSystem.GACHA_COST - 1));
        assertFalse(gacha.canRoll(0));
    }

    @Test
    @DisplayName("canRollTen() should return true only when coins are enough")
    void testCanRollTen() {
        int tenRollCost = GachaSystem.GACHA_COST * 10;

        assertTrue(gacha.canRollTen(tenRollCost));
        assertTrue(gacha.canRollTen(tenRollCost + 1));
        assertFalse(gacha.canRollTen(tenRollCost - 1));
        assertFalse(gacha.canRollTen(0));
    }

    @Test
    @DisplayName("getDropRateInfo() should include all pet types")
    void testGetDropRateInfoContainsAllPetTypes() {
        String info = gacha.getDropRateInfo();

        assertNotNull(info);
        assertTrue(info.contains("Cat"), "Drop rate info should include Cat.");
        assertTrue(info.contains("Dog"), "Drop rate info should include Dog.");
        assertTrue(info.contains("Mythic"), "Drop rate info should include Mythic.");
    }
}