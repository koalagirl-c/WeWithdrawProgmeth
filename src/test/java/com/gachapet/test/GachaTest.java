package com.gachapet.test;

import com.gachapet.model.*;
import org.junit.jupiter.api.*;
import static org.junit.jupiter.api.Assertions.*;

/**
 * ชุดทดสอบระบบตู้กาชา (GachaSystem)
 * ทดสอบ: Drop Rate, Pity System, และ Object Type
 *
 * <p>หมายเหตุ: การทดสอบ Drop Rate ใช้การรัน 1,000+ ครั้ง
 * ซึ่งยอมให้ค่าเบี่ยงเบนได้ ±5% จาก Rate ที่ตั้งไว้</p>
 */
@DisplayName("🎰 Gacha System Tests")
class GachaTest {

    /** ความคลาดเคลื่อนที่ยอมรับได้ในการทดสอบ Drop Rate (5%) */
    private static final double TOLERANCE = 0.05;

    /** จำนวนครั้งที่รันเพื่อทดสอบ Statistical */
    private static final int SAMPLE_SIZE = 1000;

    private GachaSystem gacha;

    @BeforeEach
    void setUp() {
        gacha = new GachaSystem(); // Random seed ปกติ
    }

    // ==================== Drop Rate Tests ====================

    /**
     * เหตุผล: ทดสอบว่า MythicPet ออกใกล้เคียง 10% จริงหรือไม่
     * Statistical Test: รัน 1,000 ครั้ง แล้ว Assert ว่าเรทอยู่ในช่วง [5%, 15%]
     */
    @Test
    @DisplayName("MythicPet ต้องออกใกล้เคียง 10% (ทดสอบ 1,000 ครั้ง)")
    void testMythicDropRateApproximately10Percent() {
        // Act: รัน Gacha 1,000 ครั้ง
        int mythicCount = 0;
        for (int i = 0; i < SAMPLE_SIZE; i++) {
            AbstractPet pet = gacha.roll();
            if (pet instanceof MythicPet) {
                mythicCount++;
            }
        }

        double actualRate = (double) mythicCount / SAMPLE_SIZE;
        System.out.printf("MythicPet actual rate: %.2f%% (%d/%d)%n",
            actualRate * 100, mythicCount, SAMPLE_SIZE);

        // Assert: ค่าต้องอยู่ในช่วง [5%, 15%]
        assertEquals(GachaSystem.MYTHIC_RATE, actualRate, TOLERANCE,
            String.format("อัตรา MythicPet (%.2f%%) ควรอยู่ใกล้ %.0f%% (±%.0f%%)",
                actualRate * 100, GachaSystem.MYTHIC_RATE * 100, TOLERANCE * 100));
    }

    /**
     * เหตุผล: ทดสอบว่า Cat ออกใกล้เคียง 50%
     */
    @Test
    @DisplayName("Cat ต้องออกใกล้เคียง 50% (ทดสอบ 1,000 ครั้ง)")
    void testCatDropRateApproximately50Percent() {
        int catCount = 0;
        for (int i = 0; i < SAMPLE_SIZE; i++) {
            if (gacha.roll() instanceof Cat) catCount++;
        }

        double actualRate = (double) catCount / SAMPLE_SIZE;
        System.out.printf("Cat actual rate: %.2f%%%n", actualRate * 100);

        assertEquals(GachaSystem.CAT_RATE, actualRate, TOLERANCE,
            "Cat rate ควรใกล้เคียง 50%");
    }

    /**
     * เหตุผล: ทดสอบว่า Dog ออกใกล้เคียง 40%
     */
    @Test
    @DisplayName("Dog ต้องออกใกล้เคียง 40% (ทดสอบ 1,000 ครั้ง)")
    void testDogDropRateApproximately40Percent() {
        int dogCount = 0;
        for (int i = 0; i < SAMPLE_SIZE; i++) {
            if (gacha.roll() instanceof Dog) dogCount++;
        }

        double actualRate = (double) dogCount / SAMPLE_SIZE;
        System.out.printf("Dog actual rate: %.2f%%%n", actualRate * 100);

        assertEquals(GachaSystem.DOG_RATE, actualRate, TOLERANCE,
            "Dog rate ควรใกล้เคียง 40%");
    }

    /**
     * เหตุผล: ทดสอบว่า Drop Rate รวมกันได้ 100%
     */
    @Test
    @DisplayName("Drop Rate รวมทั้งหมดต้องเท่ากับ 100%")
    void testTotalDropRateEqualsOneHundredPercent() {
        double total = GachaSystem.CAT_RATE + GachaSystem.DOG_RATE + GachaSystem.MYTHIC_RATE;
        assertEquals(1.0, total, 0.001, "Drop Rate รวมต้องเท่ากับ 1.0 (100%)");
    }

    // ==================== Roll Return Type Tests ====================

    /**
     * เหตุผล: ทดสอบว่า roll() ไม่คืน null เลย
     */
    @Test
    @DisplayName("roll() ต้องไม่คืน null ทุกครั้ง")
    void testRollNeverReturnsNull() {
        for (int i = 0; i < 100; i++) {
            AbstractPet pet = gacha.roll();
            assertNotNull(pet, "roll() ต้องไม่คืน null");
        }
    }

    /**
     * เหตุผล: ทดสอบว่า roll() คืน Object ที่เป็น Subclass ของ AbstractPet เสมอ
     * Polymorphism: ทุก Object ที่ได้ต้อง is-a AbstractPet
     */
    @Test
    @DisplayName("roll() ต้องคืน AbstractPet Subclass เสมอ (Polymorphism)")
    void testRollReturnsAbstractPetSubclass() {
        for (int i = 0; i < 50; i++) {
            AbstractPet pet = gacha.roll();
            assertTrue(pet instanceof Cat || pet instanceof Dog || pet instanceof MythicPet,
                "ผลลัพธ์ต้องเป็น Cat, Dog หรือ MythicPet เท่านั้น");
        }
    }

    /**
     * เหตุผล: ทดสอบว่าสัตว์เลี้ยงที่ได้จาก Gacha มีชื่อและค่าเริ่มต้นที่ถูกต้อง
     */
    @Test
    @DisplayName("สัตว์เลี้ยงจาก roll() ต้องมีชื่อและ HP เต็มหลอด")
    void testRolledPetHasValidInitialState() {
        AbstractPet pet = gacha.roll();

        assertNotNull(pet.getName(), "ชื่อต้องไม่ null");
        assertFalse(pet.getName().isEmpty(), "ชื่อต้องไม่ว่างเปล่า");
        assertEquals(AbstractPet.MAX_HP,     pet.getHp(),     "HP ต้องเต็มหลอดตั้งแต่เริ่ม");
        assertEquals(AbstractPet.MAX_HUNGER, pet.getHunger(), "Hunger ต้องเต็มหลอดตั้งแต่เริ่ม");
        assertEquals(1,                       pet.getLevel(),  "Level ต้องเริ่มที่ 1");
    }

    // ==================== Roll Ten / Pity System Tests ====================

    /**
     * เหตุผล: ทดสอบว่า rollTen() คืน Array ขนาด 10 เสมอ
     */
    @Test
    @DisplayName("rollTen() ต้องคืนสัตว์เลี้ยง 10 ตัวเสมอ")
    void testRollTenReturnsExactlyTenPets() {
        AbstractPet[] results = gacha.rollTen();
        assertEquals(10, results.length, "rollTen() ต้องคืน 10 ตัวเสมอ");
    }

    /**
     * เหตุผล: ทดสอบ Pity System ว่า rollTen() มี MythicPet อย่างน้อย 1 ตัวเสมอ
     */
    @Test
    @DisplayName("rollTen() ต้องมี MythicPet อย่างน้อย 1 ตัว (Pity System)")
    void testRollTenGuaranteesMythicPet() {
        // รัน 20 รอบ แต่ละรอบ rollTen() ต้องมี MythicPet อย่างน้อย 1 ตัว
        for (int round = 0; round < 20; round++) {
            AbstractPet[] results = gacha.rollTen();
            boolean hasMythic = false;
            for (AbstractPet pet : results) {
                if (pet instanceof MythicPet) {
                    hasMythic = true;
                    break;
                }
            }
            assertTrue(hasMythic,
                "rollTen() รอบที่ " + (round + 1) + " ต้องมี MythicPet อย่างน้อย 1 ตัว (Pity)");
        }
    }

    /**
     * เหตุผล: ทดสอบว่า rollTen() ไม่มี null ใน Array
     */
    @Test
    @DisplayName("rollTen() ต้องไม่มี null ใน Array")
    void testRollTenNoNullElements() {
        AbstractPet[] results = gacha.rollTen();
        for (int i = 0; i < results.length; i++) {
            assertNotNull(results[i], "ตัวที่ " + (i + 1) + " ต้องไม่ null");
        }
    }
}
