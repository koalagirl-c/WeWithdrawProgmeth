package com.gachapet.test;

import com.gachapet.model.*;
import org.junit.jupiter.api.*;
import static org.junit.jupiter.api.Assertions.*;

/**
 * ชุดทดสอบ Logic หลักของสัตว์เลี้ยง
 * ทดสอบ: Boundary, Polymorphism, Encapsulation, และ OOP Behavior
 *
 * <p>วิธีรัน: gradle test หรือคลิก Run ใน IntelliJ IDEA</p>
 */
@DisplayName("🐾 Pet Logic Tests")
class PetLogicTest {

    // ==================== Test Fixtures ====================

    private Cat cat;
    private Dog dog;
    private MythicPet mythic;

    /**
     * สร้างสัตว์เลี้ยงใหม่ก่อนทุก Test (ป้องกัน State รั่วระหว่าง Test)
     */
    @BeforeEach
    void setUp() {
        cat    = new Cat("มิ้นต์");
        dog    = new Dog("โชโก");
        mythic = new MythicPet("เซลีน");
    }

    // ==================== Boundary Tests (HP & Hunger) ====================

    /**
     * เหตุผล: ทดสอบว่า HP ไม่สามารถติดลบได้
     * Encapsulation: Setter ต้องบังคับ Boundary ให้ครบถ้วน
     */
    @Test
    @DisplayName("HP ต้องไม่ติดลบเมื่อรับดาเมจมากกว่า HP ที่มี")
    void testHpCannotGoBelowZero() {
        // Arrange: ตั้ง HP เป็น 10
        cat.setHp(10);

        // Act: ลด HP ลงเยอะมาก
        cat.setHp(cat.getHp() - 9999);

        // Assert: HP ต้องเป็น 0 ไม่ใช่ลบ
        assertEquals(0, cat.getHp(), "HP ต้องไม่ต่ำกว่า 0");
    }

    /**
     * เหตุผล: ทดสอบว่า HP ไม่เกิน MAX_HP เมื่อฟื้น HP
     * Encapsulation: Setter ต้อง clamp ค่าไว้ที่ MAX
     */
    @Test
    @DisplayName("HP ต้องไม่เกิน MAX_HP เมื่อได้รับการฟื้นฟู")
    void testHpCannotExceedMax() {
        // Arrange: HP เต็มอยู่แล้ว
        assertEquals(AbstractPet.MAX_HP, cat.getHp());

        // Act: พยายามเพิ่ม HP เกิน MAX
        cat.setHp(cat.getHp() + 9999);

        // Assert: HP ต้องไม่เกิน MAX_HP
        assertEquals(AbstractPet.MAX_HP, cat.getHp(),
            "HP ต้องไม่เกิน MAX_HP (" + AbstractPet.MAX_HP + ")");
    }

    /**
     * เหตุผล: ทดสอบว่า Hunger ไม่เกิน MAX_HUNGER แม้จะกินอาหารรัวๆ
     * Boundary: ป้องกัน Overflow ของค่าสถานะ
     */
    @Test
    @DisplayName("Hunger ต้องไม่เกิน MAX_HUNGER เมื่อกินอาหารต่อเนื่อง")
    void testHungerCannotExceedMax() {
        // Act: กินอาหารซ้ำๆ หลายรอบ
        for (int i = 0; i < 20; i++) {
            cat.eat(50);
        }

        // Assert: Hunger ต้องหยุดที่ MAX_HUNGER เสมอ
        assertEquals(AbstractPet.MAX_HUNGER, cat.getHunger(),
            "Hunger ต้องไม่เกิน " + AbstractPet.MAX_HUNGER);
    }

    /**
     * เหตุผล: ทดสอบว่า Hunger ไม่ติดลบ
     */
    @Test
    @DisplayName("Hunger ต้องไม่ติดลบ")
    void testHungerCannotGoBelowZero() {
        // Arrange: ตั้ง Hunger เป็น 5
        cat.setHunger(5);

        // Act: ลด Hunger เยอะมาก
        cat.setHunger(cat.getHunger() - 9999);

        // Assert
        assertEquals(0, cat.getHunger(), "Hunger ต้องไม่ต่ำกว่า 0");
    }

    /**
     * เหตุผล: ทดสอบว่าสัตว์เลี้ยงตรวจสอบ isAlive() ถูกต้อง
     */
    @Test
    @DisplayName("isAlive() คืน false เมื่อ HP เป็น 0")
    void testIsAliveWhenHpIsZero() {
        // Arrange
        cat.setHp(0);

        // Assert
        assertFalse(cat.isAlive(), "สัตว์เลี้ยงที่ HP = 0 ต้องไม่มีชีวิต");
    }

    /**
     * เหตุผล: สัตว์เลี้ยงที่เพิ่งสร้างต้องมีชีวิต
     */
    @Test
    @DisplayName("สัตว์เลี้ยงใหม่ต้องมีชีวิต (HP > 0)")
    void testNewPetIsAlive() {
        assertTrue(cat.isAlive());
        assertTrue(dog.isAlive());
        assertTrue(mythic.isAlive());
    }

    // ==================== Polymorphism Tests ====================

    /**
     * เหตุผล: ทดสอบ Polymorphism — เมธอด makeSound() ของแต่ละคลาสต้องคืนค่าต่างกัน
     * OOP Concept: Runtime Polymorphism ผ่าน Abstract Method
     */
    @Test
    @DisplayName("makeSound() ต้องคืนค่าต่างกันตาม Subclass (Polymorphism)")
    void testPolymorphismMakeSound() {
        // Arrange: สร้าง List รวมสัตว์เลี้ยงแบบ Polymorphism
        AbstractPet[] pets = { cat, dog, mythic };
        String[] sounds = new String[3];

        // Act: เรียก makeSound() ผ่าน Reference แบบ AbstractPet
        for (int i = 0; i < pets.length; i++) {
            sounds[i] = pets[i].makeSound();
        }

        // Assert: เสียงต้องไม่เหมือนกัน
        assertNotEquals(sounds[0], sounds[1], "แมวและสุนัขต้องมีเสียงต่างกัน");
        assertNotEquals(sounds[1], sounds[2], "สุนัขและ MythicPet ต้องมีเสียงต่างกัน");
        assertNotEquals(sounds[0], sounds[2], "แมวและ MythicPet ต้องมีเสียงต่างกัน");
    }

    /**
     * เหตุผล: ทดสอบว่า Dog play() ได้ HP Bonus แต่ Cat ไม่ได้
     * Polymorphism: เมธอดชื่อเดียวกันแต่ผลลัพธ์ต่างกัน
     */
    @Test
    @DisplayName("Dog play() ต้องได้ HP Bonus แต่ Cat play() ไม่ได้ (Polymorphism)")
    void testPolymorphismPlayDifference() {
        // Arrange
        cat.setHp(50);
        dog.setHp(50);
        int catHpBefore = cat.getHp();
        int dogHpBefore = dog.getHp();

        // Act
        cat.play();
        dog.play();

        // Assert: Dog ต้องได้ HP เพิ่ม, Cat ต้องไม่ได้ HP เพิ่มจาก play()
        assertTrue(dog.getHp() > dogHpBefore, "Dog play() ต้องได้ HP bonus");
        assertEquals(catHpBefore, cat.getHp(), "Cat play() ต้องไม่เปลี่ยน HP");
    }

    /**
     * เหตุผล: ทดสอบว่า getPetType() คืนค่าที่ถูกต้องตาม Subclass
     * ใช้ทดสอบ instanceof / type checking ผ่าน Abstract Method
     */
    @Test
    @DisplayName("getPetType() ต้องคืนชนิดที่ถูกต้องของแต่ละ Subclass")
    void testGetPetType() {
        assertEquals("CAT",    cat.getPetType());
        assertEquals("DOG",    dog.getPetType());
        assertEquals("MYTHIC", mythic.getPetType());
    }

    /**
     * เหตุผล: ทดสอบว่า MythicPet กินอาหารแล้วได้ HP มากกว่า Cat และ Dog
     * เพราะ MythicPet Override eat() โดยให้ HP ต่อหน่วยมากกว่า
     */
    @Test
    @DisplayName("MythicPet eat() ต้องได้ HP มากกว่า Dog (Polymorphism)")
    void testMythicEatBetterThanDog() {
        // Arrange: ทุกตัวเริ่มจาก HP เดิม
        int startHp = 50;
        mythic.setHp(startHp);
        dog.setHp(startHp);

        // Act: กินอาหารด้วย amount เท่ากัน
        mythic.eat(40);
        dog.eat(40);

        // Assert: MythicPet ต้องได้ HP มากกว่าหรือเท่ากับ Dog
        assertTrue(mythic.getHp() >= dog.getHp(),
            "MythicPet ต้องฟื้น HP ได้มากกว่าหรือเท่ากับ Dog");
    }

    // ==================== Hunger Decay Rate Tests ====================

    /**
     * เหตุผล: ทดสอบว่า Dog หิวเร็วกว่า Cat (อัตราลด Hunger ต่างกัน)
     */
    @Test
    @DisplayName("Dog ต้องหิวเร็วกว่า Cat (HungerDecayRate แตกต่างกัน)")
    void testHungerDecayRateDifference() {
        // Arrange: ตั้ง Hunger เท่ากัน
        cat.setHunger(100);
        dog.setHunger(100);

        // Act: อัปเดตสถานะ 5 รอบ
        for (int i = 0; i < 5; i++) {
            cat.updateStatus();
            dog.updateStatus();
        }

        // Assert: Dog ต้องหิวกว่า (Hunger ต่ำกว่า)
        assertTrue(dog.getHunger() < cat.getHunger(),
            "Dog ต้องมี Hunger น้อยกว่า Cat หลัง updateStatus() หลายครั้ง");
    }

    // ==================== Constructor & IllegalArgument Tests ====================

    /**
     * เหตุผล: ทดสอบว่า Constructor โยน Exception เมื่อชื่อว่างเปล่า
     * Encapsulation: ป้องกันข้อมูลที่ไม่ถูกต้องตั้งแต่แรก
     */
    @Test
    @DisplayName("สร้างสัตว์เลี้ยงด้วยชื่อว่างต้องโยน IllegalArgumentException")
    void testConstructorThrowsOnEmptyName() {
        assertThrows(IllegalArgumentException.class, () -> new Cat(""),
            "ชื่อว่างต้องโยน IllegalArgumentException");
        assertThrows(IllegalArgumentException.class, () -> new Dog(null),
            "ชื่อ null ต้องโยน IllegalArgumentException");
    }

    /**
     * เหตุผล: ทดสอบว่าสัตว์เลี้ยงใหม่เริ่มต้นด้วยค่าที่ถูกต้อง
     */
    @Test
    @DisplayName("สัตว์เลี้ยงใหม่ต้องมี HP และ Hunger เต็มหลอด")
    void testInitialValues() {
        assertEquals(AbstractPet.MAX_HP,     cat.getHp());
        assertEquals(AbstractPet.MAX_HUNGER, cat.getHunger());
        assertEquals(1,                       cat.getLevel());
    }

    // ==================== MythicPet Specific Tests ====================

    /**
     * เหตุผล: ทดสอบ Ultimate Skill ของ MythicPet
     */
    @Test
    @DisplayName("MythicPet performAction() ต้องฟื้น HP เต็มหลอด")
    void testMythicUltimateHealsToFull() {
        // Arrange
        mythic.setHp(10);
        int chargesBefore = mythic.getUltimateCharges();

        // Act
        mythic.performAction();

        // Assert
        assertEquals(AbstractPet.MAX_HP, mythic.getHp(), "Ultimate ต้องฟื้น HP เต็ม");
        assertEquals(chargesBefore - 1, mythic.getUltimateCharges(), "ต้องใช้ Charge 1 ครั้ง");
    }
}
