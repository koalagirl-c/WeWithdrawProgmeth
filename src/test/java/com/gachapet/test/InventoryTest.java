package com.gachapet.test;

import com.gachapet.data.FileDataHandler;
import com.gachapet.data.UserInventory;
import com.gachapet.model.AbstractPet;
import com.gachapet.model.Cat;
import com.gachapet.model.Dog;
import com.gachapet.model.MythicPet;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * คลาสทดสอบ (Unit tests) สำหรับ UserInventory และ FileDataHandler
 *
 * <p>การทดสอบนี้ครอบคลุมเรื่อง:</p>
 * <ul>
 * <li>สถานะเริ่มต้นของกระเป๋าเก็บของ (Initial inventory state)</li>
 * <li>การจัดการสัตว์เลี้ยง (Pet management)</li>
 * <li>การจัดการเงินในเกม (Coin management)</li>
 * <li>การพ้องรูป (Polymorphism) ที่ใช้งานร่วมกับ AbstractPet</li>
 * <li>ระบบการบันทึกและโหลดข้อมูลจากไฟล์ (File save/load behavior)</li>
 * </ul>
 */
@DisplayName("Inventory and File I/O Tests")
class InventoryTest {

    private static final String TEST_FILE = "test_save_data.txt";

    private UserInventory inventory;
    private FileDataHandler dataHandler;

    @BeforeEach
    void setUp() {
        inventory = new UserInventory("TestPlayer");
        dataHandler = new FileDataHandler(TEST_FILE);
    }

    @AfterEach
    void tearDown() {
        File testFile = new File(TEST_FILE);
        if (testFile.exists()) {
            assertTrue(testFile.delete(), "Test save file should be deleted after each test.");
        }
    }

    // ==================== Initial State Tests ====================

    @Test
    @DisplayName("New inventory should start with the default number of coins")
    void testInitialCoins() {
        assertEquals(
                UserInventory.STARTING_COINS,
                inventory.getCoins(),
                "A new inventory should start with STARTING_COINS."
        );
    }

    @Test
    @DisplayName("New inventory should start with no pets")
    void testInitialPetCount() {
        assertEquals(0, inventory.getPetCount(), "A new inventory should start empty.");
        assertTrue(inventory.isEmpty(), "A new inventory should report that it is empty.");
    }

    @Test
    @DisplayName("Player name should be stored correctly")
    void testPlayerName() {
        assertEquals("TestPlayer", inventory.getPlayerName());
    }

    // ==================== Pet Management Tests ====================

    @Test
    @DisplayName("addPet() should add a pet successfully")
    void testAddPetSuccess() {
        Cat cat = new Cat("Milo");

        boolean result = inventory.addPet(cat);

        assertTrue(result, "addPet() should return true when the pet is added.");
        assertEquals(1, inventory.getPetCount(), "Inventory should contain one pet.");
        assertSame(cat, inventory.getPet(0), "The stored pet should be the same object.");
    }

    @Test
    @DisplayName("addPet(null) should return false")
    void testAddNullPetReturnsFalse() {
        boolean result = inventory.addPet(null);

        assertFalse(result, "addPet(null) should return false.");
        assertEquals(0, inventory.getPetCount(), "Null pet should not be added.");
    }

    @Test
    @DisplayName("addPet() should reject pets when inventory is full")
    void testAddPetWhenInventoryFull() {
        for (int i = 0; i < UserInventory.MAX_PET_CAPACITY; i++) {
            assertTrue(inventory.addPet(new Cat("Cat Pet " + (char) ('A' + (i % 26)))));
        }

        boolean result = inventory.addPet(new Dog("ExtraDog"));

        assertFalse(result, "addPet() should return false when inventory is full.");
        assertEquals(
                UserInventory.MAX_PET_CAPACITY,
                inventory.getPetCount(),
                "Pet count should not exceed MAX_PET_CAPACITY."
        );
        assertTrue(inventory.isFull(), "Inventory should report that it is full.");
    }

    @Test
    @DisplayName("Inventory should store different pet types using polymorphism")
    void testInventoryStoresDifferentPetTypes() {
        inventory.addPet(new Cat("Luna"));
        inventory.addPet(new Dog("Buddy"));
        inventory.addPet(new MythicPet("Astra"));

        assertEquals(3, inventory.getPetCount());

        AbstractPet firstPet = inventory.getPet(0);
        AbstractPet secondPet = inventory.getPet(1);
        AbstractPet thirdPet = inventory.getPet(2);

        assertInstanceOf(Cat.class, firstPet);
        assertInstanceOf(Dog.class, secondPet);
        assertInstanceOf(MythicPet.class, thirdPet);

        assertEquals("CAT", firstPet.getPetType());
        assertEquals("DOG", secondPet.getPetType());
        assertEquals("MYTHIC", thirdPet.getPetType());
    }

    @Test
    @DisplayName("getAllPets() should return a copy of the internal list")
    void testGetAllPetsReturnsCopy() {
        inventory.addPet(new Cat("CopyTestCat"));

        List<AbstractPet> copy = inventory.getAllPets();
        copy.clear();

        assertEquals(
                1,
                inventory.getPetCount(),
                "Modifying the returned list should not affect the inventory."
        );
    }

    @Test
    @DisplayName("removePet() should remove and return the selected pet")
    void testRemovePetSuccess() {
        Cat cat = new Cat("RemoveMe");
        Dog dog = new Dog("KeepMe");

        inventory.addPet(cat);
        inventory.addPet(dog);

        AbstractPet removed = inventory.removePet(0);

        assertSame(cat, removed, "removePet() should return the removed pet.");
        assertEquals(1, inventory.getPetCount(), "Inventory should contain one pet after removal.");
        assertSame(dog, inventory.getPet(0), "The remaining pet should shift into index 0.");
    }

    @Test
    @DisplayName("removePet() should return null for invalid index")
    void testRemovePetInvalidIndex() {
        inventory.addPet(new Cat("Milo"));

        assertNull(inventory.removePet(-1), "Negative index should return null.");
        assertNull(inventory.removePet(99), "Out-of-range index should return null.");
        assertEquals(1, inventory.getPetCount(), "Invalid removal should not change inventory.");
    }

    @Test
    @DisplayName("getPet() should return null for invalid index")
    void testGetPetInvalidIndex() {
        assertNull(inventory.getPet(-1));
        assertNull(inventory.getPet(0));
        assertNull(inventory.getPet(999));
    }

    // ==================== Coin Tests ====================

    @Test
    @DisplayName("spendCoins() should return false when funds are insufficient")
    void testSpendCoinsInsufficientFunds() {
        boolean result = inventory.spendCoins(99999);

        assertFalse(result, "spendCoins() should return false when coins are not enough.");
        assertEquals(
                UserInventory.STARTING_COINS,
                inventory.getCoins(),
                "Coins should not change when spending fails."
        );
    }

    @Test
    @DisplayName("spendCoins() should deduct coins when funds are sufficient")
    void testSpendCoinsSuccess() {
        boolean result = inventory.spendCoins(100);

        assertTrue(result, "spendCoins() should return true when spending succeeds.");
        assertEquals(UserInventory.STARTING_COINS - 100, inventory.getCoins());
    }

    @Test
    @DisplayName("addCoins() should increase the coin balance")
    void testAddCoins() {
        inventory.addCoins(50);

        assertEquals(UserInventory.STARTING_COINS + 50, inventory.getCoins());
    }

    @Test
    @DisplayName("Invalid coin operations should not change the coin balance")
    void testInvalidCoinOperations() {
        inventory.addCoins(0);
        inventory.addCoins(-100);
        inventory.spendCoins(0);
        inventory.spendCoins(-100);

        assertEquals(
                UserInventory.STARTING_COINS,
                inventory.getCoins(),
                "Invalid coin operations should not change the coin balance."
        );
    }

    @Test
    @DisplayName("setCoins() should not allow negative coin values")
    void testSetCoinsPreventsNegativeValues() {
        inventory.setCoins(-999);

        assertEquals(0, inventory.getCoins(), "Coins should be clamped to 0.");
    }

    @Test
    @DisplayName("hasEnoughCoins() should return the correct result")
    void testHasEnoughCoins() {
        assertTrue(inventory.hasEnoughCoins(100));
        assertTrue(inventory.hasEnoughCoins(UserInventory.STARTING_COINS));
        assertFalse(inventory.hasEnoughCoins(UserInventory.STARTING_COINS + 1));
    }

    // ==================== File I/O Tests ====================

    @Test
    @DisplayName("saveData() should create a save file")
    void testSaveDataCreatesFile() {
        inventory.addPet(new Cat("SavedCat"));

        boolean saved = dataHandler.saveData(inventory);

        assertTrue(saved, "saveData() should return true.");
        assertTrue(new File(TEST_FILE).exists(), "The save file should be created.");
    }

    @Test
    @DisplayName("loadData() should return null when the save file does not exist")
    void testLoadDataWithNoFile() {
        FileDataHandler handler = new FileDataHandler("nonexistent_test_save_file.txt");

        UserInventory result = handler.loadData();

        assertNull(result, "loadData() should return null when no save file exists.");
    }

    @Test
    @DisplayName("hasSaveFile() should reflect whether a save file exists")
    void testHasSaveFile() {
        assertFalse(dataHandler.hasSaveFile(), "Before saving, the save file should not exist.");

        dataHandler.saveData(inventory);

        assertTrue(dataHandler.hasSaveFile(), "After saving, the save file should exist.");
    }

    @Test
    @DisplayName("deleteSaveFile() should delete the save file")
    void testDeleteSaveFile() {
        dataHandler.saveData(inventory);
        assertTrue(dataHandler.hasSaveFile());

        boolean deleted = dataHandler.deleteSaveFile();

        assertTrue(deleted, "deleteSaveFile() should return true.");
        assertFalse(dataHandler.hasSaveFile(), "Save file should no longer exist.");
    }

    @Test
    @DisplayName("Save and load should preserve basic inventory data")
    void testSaveAndLoadRoundTrip() {
        inventory.addPet(new Cat("Mint"));
        inventory.addPet(new Dog("Choco"));
        inventory.spendCoins(200);

        inventory.getPet(0).setHp(75);
        inventory.getPet(0).setHunger(60);
        inventory.getPet(0).setHappiness(70);
        inventory.getPet(0).setEnergy(80);
        inventory.getPet(0).setLevel(3);
        inventory.getPet(0).setExperience(45);
        inventory.getPet(0).setImageVariant(2);

        dataHandler.saveData(inventory);
        UserInventory loaded = dataHandler.loadData();

        assertNotNull(loaded, "Loaded inventory should not be null.");
        assertEquals(inventory.getPlayerName(), loaded.getPlayerName());
        assertEquals(inventory.getCoins(), loaded.getCoins());
        assertEquals(inventory.getPetCount(), loaded.getPetCount());

        AbstractPet loadedPet = loaded.getPet(0);

        assertNotNull(loadedPet);
        assertEquals("CAT", loadedPet.getPetType());
        assertEquals("Mint", loadedPet.getName());
        assertEquals(75, loadedPet.getHp());
        assertEquals(60, loadedPet.getHunger());
        assertEquals(70, loadedPet.getHappiness());
        assertEquals(80, loadedPet.getEnergy());
        assertEquals(3, loadedPet.getLevel());
        assertEquals(45, loadedPet.getExperience());
        assertEquals(2, loadedPet.getImageVariant());
    }

    @Test
    @DisplayName("MythicPet should save and load correctly")
    void testMythicPetSaveLoad() {
        inventory.addPet(new MythicPet("Celestia"));

        dataHandler.saveData(inventory);
        UserInventory loaded = dataHandler.loadData();

        assertNotNull(loaded);
        assertEquals(1, loaded.getPetCount());

        AbstractPet pet = loaded.getPet(0);

        assertNotNull(pet);
        assertInstanceOf(MythicPet.class, pet);
        assertEquals("MYTHIC", pet.getPetType());
        assertEquals("Celestia", pet.getName());
    }
}
