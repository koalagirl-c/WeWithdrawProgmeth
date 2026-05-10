package com.gachapet.test;

import com.gachapet.model.AbstractPet;
import com.gachapet.model.Cat;
import com.gachapet.model.Dog;
import com.gachapet.model.MythicPet;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * คลาสทดสอบ (Unit tests) สำหรับระบบตรรกะหลักของสัตว์เลี้ยง
 *
 * <p>การทดสอบนี้ครอบคลุมเรื่อง:</p>
 * <ul>
 * <li>พฤติกรรมเมื่อถึงขีดจำกัดของค่าต่างๆ (Boundary behavior) เช่น ค่าห้ามเกิน 100 หรือห้ามติดลบ</li>
 * <li>การปกป้องข้อมูล (Encapsulation) ผ่านการใช้งาน Setter</li>
 * <li>การพ้องรูป (Polymorphism) ที่ทำงานร่วมกันระหว่าง Cat, Dog และ MythicPet</li>
 * <li>การลดลงของสถานะตามเวลา (Status decay behavior)</li>
 * <li>ระบบการนอนหลับและการตื่น (Sleep and wake-up behavior)</li>
 * <li>พฤติกรรมการใช้สกิลอัลติเมตเฉพาะตัวของสัตว์ในตำนาน (MythicPet)</li>
 * </ul>
 */
@DisplayName("Pet Logic Tests")
class PetLogicTest {

    private Cat cat;
    private Dog dog;
    private MythicPet mythic;

    @BeforeEach
    void setUp() {
        cat = new Cat("Mint");
        dog = new Dog("Choco");
        mythic = new MythicPet("Celeste");
    }

    // ==================== Boundary Tests ====================

    @Test
    @DisplayName("HP should not go below zero")
    void testHpCannotGoBelowZero() {
        cat.setHp(10);
        cat.setHp(cat.getHp() - 9999);

        assertEquals(0, cat.getHp(), "HP should be clamped to 0.");
    }

    @Test
    @DisplayName("HP should not exceed MAX_HP")
    void testHpCannotExceedMax() {
        cat.setHp(AbstractPet.MAX_HP);
        cat.setHp(cat.getHp() + 9999);

        assertEquals(AbstractPet.MAX_HP, cat.getHp(), "HP should be clamped to MAX_HP.");
    }

    @Test
    @DisplayName("Hunger should not go below zero")
    void testHungerCannotGoBelowZero() {
        cat.setHunger(5);
        cat.setHunger(cat.getHunger() - 9999);

        assertEquals(0, cat.getHunger(), "Hunger should be clamped to 0.");
    }

    @Test
    @DisplayName("Hunger should not exceed MAX_HUNGER")
    void testHungerCannotExceedMax() {
        for (int i = 0; i < 20; i++) {
            cat.eat(50);
        }

        assertEquals(AbstractPet.MAX_HUNGER, cat.getHunger(), "Hunger should be clamped to MAX_HUNGER.");
    }

    @Test
    @DisplayName("Happiness should not go below zero")
    void testHappinessCannotGoBelowZero() {
        cat.setHappiness(-999);

        assertEquals(0, cat.getHappiness(), "Happiness should be clamped to 0.");
    }

    @Test
    @DisplayName("Happiness should not exceed MAX_HAPPINESS")
    void testHappinessCannotExceedMax() {
        cat.setHappiness(999);

        assertEquals(AbstractPet.MAX_HAPPINESS, cat.getHappiness(), "Happiness should be clamped to MAX_HAPPINESS.");
    }

    @Test
    @DisplayName("Energy should not go below zero")
    void testEnergyCannotGoBelowZero() {
        cat.setEnergy(-999);

        assertEquals(0, cat.getEnergy(), "Energy should be clamped to 0.");
    }

    @Test
    @DisplayName("Energy should not exceed MAX_ENERGY")
    void testEnergyCannotExceedMax() {
        cat.setEnergy(999);

        assertEquals(AbstractPet.MAX_ENERGY, cat.getEnergy(), "Energy should be clamped to MAX_ENERGY.");
    }

    // ==================== Life State Tests ====================

    @Test
    @DisplayName("A pet should be dead when HP is zero")
    void testIsAliveWhenHpIsZero() {
        cat.setHp(0);

        assertFalse(cat.isAlive(), "A pet with 0 HP should not be alive.");
    }

    @Test
    @DisplayName("New pets should be alive")
    void testNewPetIsAlive() {
        assertTrue(cat.isAlive());
        assertTrue(dog.isAlive());
        assertTrue(mythic.isAlive());
    }

    @Test
    @DisplayName("New pets should have full initial stats")
    void testInitialValues() {
        assertEquals(AbstractPet.MAX_HP, cat.getHp());
        assertEquals(AbstractPet.MAX_HUNGER, cat.getHunger());
        assertEquals(AbstractPet.MAX_HAPPINESS, cat.getHappiness());
        assertEquals(AbstractPet.MAX_ENERGY, cat.getEnergy());
        assertEquals(1, cat.getLevel());
        assertEquals(0, cat.getExperience());
    }

    // ==================== Constructor Tests ====================

    @Test
    @DisplayName("Constructor should reject empty or null names")
    void testConstructorThrowsOnInvalidName() {
        assertThrows(IllegalArgumentException.class, () -> new Cat(""));
        assertThrows(IllegalArgumentException.class, () -> new Cat("   "));
        assertThrows(IllegalArgumentException.class, () -> new Dog(null));
        assertThrows(IllegalArgumentException.class, () -> new Dog("โชโก"));
        assertThrows(IllegalArgumentException.class, () -> new Cat("Cat123"));
    }

    // ==================== Polymorphism Tests ====================

    @Test
    @DisplayName("makeSound() should return different sounds for each subclass")
    void testPolymorphismMakeSound() {
        AbstractPet[] pets = {cat, dog, mythic};

        String catSound = pets[0].makeSound();
        String dogSound = pets[1].makeSound();
        String mythicSound = pets[2].makeSound();

        assertNotEquals(catSound, dogSound);
        assertNotEquals(dogSound, mythicSound);
        assertNotEquals(catSound, mythicSound);
    }

    @Test
    @DisplayName("getPetType() should return the correct type for each subclass")
    void testGetPetType() {
        assertEquals("CAT", cat.getPetType());
        assertEquals("DOG", dog.getPetType());
        assertEquals("MYTHIC", mythic.getPetType());
    }

    @Test
    @DisplayName("getEmoji() should return a non-empty value for each pet")
    void testGetEmoji() {
        assertFalse(cat.getEmoji().isEmpty());
        assertFalse(dog.getEmoji().isEmpty());
        assertFalse(mythic.getEmoji().isEmpty());
    }

    @Test
    @DisplayName("getSpecialSkill() should return a non-empty value for each pet")
    void testGetSpecialSkill() {
        assertFalse(cat.getSpecialSkill().isEmpty());
        assertFalse(dog.getSpecialSkill().isEmpty());
        assertFalse(mythic.getSpecialSkill().isEmpty());
    }

    @Test
    @DisplayName("Dog play() should increase HP, while Cat play() should not")
    void testPolymorphismPlayDifference() {
        cat.setHp(50);
        dog.setHp(50);

        int catHpBefore = cat.getHp();
        int dogHpBefore = dog.getHp();

        cat.play();
        dog.play();

        assertEquals(catHpBefore, cat.getHp(), "Cat play() should not change HP.");
        assertTrue(dog.getHp() > dogHpBefore, "Dog play() should increase HP.");
    }

    @Test
    @DisplayName("MythicPet eat() should restore at least as much HP as Dog")
    void testMythicEatBetterThanDog() {
        int startHp = 50;

        mythic.setHp(startHp);
        dog.setHp(startHp);

        mythic.eat(40);
        dog.eat(40);

        assertTrue(
                mythic.getHp() >= dog.getHp(),
                "MythicPet should restore at least as much HP as Dog."
        );
    }

    // ==================== Action Behavior Tests ====================

    @Test
    @DisplayName("Cat play() should increase happiness and reduce energy")
    void testCatPlayChangesStats() {
        cat.setHappiness(50);
        cat.setEnergy(50);

        cat.play();

        assertTrue(cat.getHappiness() > 50, "Cat play() should increase happiness.");
        assertTrue(cat.getEnergy() < 50, "Cat play() should reduce energy.");
    }

    @Test
    @DisplayName("Dog play() should increase happiness and reduce energy")
    void testDogPlayChangesStats() {
        dog.setHappiness(50);
        dog.setEnergy(50);

        dog.play();

        assertTrue(dog.getHappiness() > 50, "Dog play() should increase happiness.");
        assertTrue(dog.getEnergy() < 50, "Dog play() should reduce energy.");
    }

    @Test
    @DisplayName("performAction() should affect stats differently by subclass")
    void testPerformActionPolymorphism() {
        cat.setEnergy(50);
        dog.setEnergy(50);
        mythic.setHp(50);

        cat.performAction();
        dog.performAction();
        mythic.performAction();

        assertTrue(cat.getEnergy() > 50, "Cat special action should increase energy.");
        assertTrue(dog.getEnergy() < 50, "Dog special action should decrease energy.");
        assertEquals(AbstractPet.MAX_HP, mythic.getHp(), "Mythic special action should heal HP to full.");
    }

    // ==================== Status Decay Tests ====================

    @Test
    @DisplayName("Dog should lose hunger faster than Cat")
    void testHungerDecayRateDifference() {
        cat.setHunger(100);
        dog.setHunger(100);

        for (int i = 0; i < 30; i++) {
            cat.updateStatus();
            dog.updateStatus();
        }

        assertTrue(
                dog.getHunger() < cat.getHunger(),
                "Dog should have lower Hunger than Cat after repeated status updates."
        );
    }

    @Test
    @DisplayName("tick() should update pet age without reducing stats every second")
    void testTickUpdatesAgeWithoutImmediateStatDecay() {
        int ageBefore = cat.getAge();
        int hungerBefore = cat.getHunger();

        cat.tick();

        assertEquals(ageBefore + 1, cat.getAge(), "tick() should increase age by 1.");
        assertEquals(hungerBefore, cat.getHunger(), "Hunger should not decay every second.");
    }

    @Test
    @DisplayName("Stats should decay after enough time passes")
    void testStatsDecayAfterCareInterval() {
        int hungerBefore = cat.getHunger();

        for (int i = 0; i < 30; i++) {
            cat.tick();
        }

        assertTrue(cat.getHunger() < hungerBefore, "Hunger should decay after the care interval.");
    }

    @Test
    @DisplayName("Inactive pets should decay slowly without losing energy")
    void testInactivePetStatsDecaySlowly() {
        int ageBefore = cat.getAge();
        int hungerBefore = cat.getHunger();
        int happinessBefore = cat.getHappiness();
        int energyBefore = cat.getEnergy();

        cat.tickInactive();

        assertEquals(ageBefore, cat.getAge(), "Inactive pets should not use active age ticks.");
        assertEquals(hungerBefore, cat.getHunger(), "Inactive pets should not lose Hunger every second.");
        assertEquals(happinessBefore, cat.getHappiness(), "Inactive pets should not lose Happiness every second.");
        assertEquals(energyBefore, cat.getEnergy(), "Inactive pets should not lose Energy.");

        for (int i = 1; i < 60; i++) {
            cat.tickInactive();
        }

        assertEquals(ageBefore, cat.getAge(), "Inactive pets should not use active age ticks.");
        assertTrue(cat.getHunger() < hungerBefore, "Inactive pets should slowly lose Hunger.");
        assertTrue(cat.getHappiness() < happinessBefore, "Inactive pets should slowly lose Happiness.");
        assertEquals(energyBefore, cat.getEnergy(), "Inactive pets should not lose Energy.");
    }

    @Test
    @DisplayName("Care alert should trigger when a stat is half or lower")
    void testCareAlertWhenStatsDropToHalf() {
        cat.setHunger(50);

        assertTrue(cat.needsCareAlert(), "A pet should need care when Hunger is 50 or lower.");
        assertTrue(cat.getCareAlertText().contains("Hunger 50/100"));
    }

    @Test
    @DisplayName("Low stats should reduce HP after updateStatus()")
    void testLowStatsReduceHp() {
        cat.setHp(50);
        cat.setHunger(0);
        cat.setHappiness(0);
        cat.setEnergy(0);

        for (int i = 0; i < 30; i++) {
            cat.updateStatus();
        }

        assertTrue(cat.getHp() < 50, "Low stats should reduce HP.");
    }

    @Test
    @DisplayName("Good stats should slowly restore HP after updateStatus()")
    void testGoodStatsRestoreHp() {
        cat.setHp(50);
        cat.setHunger(100);
        cat.setHappiness(100);
        cat.setEnergy(100);

        for (int i = 0; i < 30; i++) {
            cat.updateStatus();
        }

        assertTrue(cat.getHp() > 50, "Good stats should restore HP slightly.");
    }

    // ==================== Sleep Tests ====================

    @Test
    @DisplayName("sleep() should set sleeping state to true")
    void testSleepSetsSleepingTrue() {
        cat.sleep();

        assertTrue(cat.isSleeping(), "sleep() should set sleeping to true.");
    }

    @Test
    @DisplayName("wakeUp() should set sleeping state to false")
    void testWakeUpSetsSleepingFalse() {
        cat.sleep();
        cat.wakeUp();

        assertFalse(cat.isSleeping(), "wakeUp() should set sleeping to false.");
    }

    @Test
    @DisplayName("Sleeping pet should gain energy during updateStatus()")
    void testSleepingPetGainsEnergy() {
        cat.setEnergy(20);
        cat.sleep();

        for (int i = 0; i < 8; i++) {
            cat.updateStatus();
        }

        assertTrue(cat.getEnergy() > 20, "Sleeping pet should gain energy.");
    }

    @Test
    @DisplayName("Sleeping pet should not perform normal actions")
    void testSleepingPetCannotPerformNormalActions() {
        cat.sleep();

        int hungerBefore = cat.getHunger();
        int happinessBefore = cat.getHappiness();

        cat.eat(20);
        cat.play();

        assertEquals(hungerBefore, cat.getHunger(), "Sleeping pet should not eat.");
        assertEquals(happinessBefore, cat.getHappiness(), "Sleeping pet should not play.");
    }

    // ==================== MythicPet Specific Tests ====================

    @Test
    @DisplayName("MythicPet ultimate should heal HP to full")
    void testMythicUltimateHealsToFull() {
        mythic.setHp(10);
        int chargesBefore = mythic.getUltimateCharges();

        mythic.performAction();

        assertEquals(AbstractPet.MAX_HP, mythic.getHp(), "Ultimate should heal HP to full.");
        assertEquals(chargesBefore - 1, mythic.getUltimateCharges(), "Ultimate should consume one charge.");
    }

    @Test
    @DisplayName("addUltimateCharges() should not exceed the maximum charge limit")
    void testMythicUltimateChargesDoNotExceedMax() {
        mythic.addUltimateCharges(999);

        assertTrue(mythic.getUltimateCharges() <= 3, "Ultimate charges should not exceed the maximum limit.");
    }
}
