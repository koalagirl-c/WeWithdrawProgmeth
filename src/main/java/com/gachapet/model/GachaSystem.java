package com.gachapet.model;

import java.util.Random;

/**
 * ระบบตู้กาชา (Gacha System) สำหรับสุ่มสัตว์เลี้ยง
 * ใช้ Random พร้อมระบบความน่าจะเป็นที่กำหนดไว้ล่วงหน้า
 *
 * <p>อัตราการสุ่ม (Drop Rate):</p>
 * <ul>
 *   <li>🐱 Cat - 50% (ธรรมดา)</li>
 *   <li>🐶 Dog - 40% (ธรรมดา)</li>
 *   <li>🦄 MythicPet - 10% (หายากมาก)</li>
 * </ul>
 */
public class GachaSystem {

    // ==================== Drop Rate Constants ====================

    /** อัตราการออกของแมว (50%) */
    public static final double CAT_RATE = 0.50;

    /** อัตราการออกของสุนัข (40%) */
    public static final double DOG_RATE = 0.40;

    /** อัตราการออกของสัตว์ในตำนาน (10%) */
    public static final double MYTHIC_RATE = 0.10;

    /** ราคาในการสุ่มกาชา 1 ครั้ง */
    public static final int GACHA_COST = 100;

    // ==================== Fields ====================

    /** Random object สำหรับการสุ่ม */
    private final Random random;

    /** รายชื่อสุ่มสำหรับแมว */
    private static final String[] CAT_NAMES = {
            "มิ้นต์", "ลูก้า", "ซากุระ", "โมจิ", "พัมกิ้น",
            "Nala", "Luna", "Bella", "Cleo", "Kitty"
    };

    /** รายชื่อสุ่มสำหรับสุนัข */
    private static final String[] DOG_NAMES = {
            "โชโก", "มาร์โก้", "โบล่า", "บัดดี้", "คูกี้",
            "Max", "Buddy", "Rocky", "Bear", "Duke"
    };

    /** รายชื่อสุ่มสำหรับสัตว์ในตำนาน */
    private static final String[] MYTHIC_NAMES = {
            "อาเธอร์", "เซลีน", "ไลล่า", "ออโรร่า", "นีโอ",
            "Seraphim", "Celestia", "Nexus", "Astra", "Orion"
    };

    // ==================== Constructor ====================

    /**
     * สร้าง GachaSystem ด้วย Random seed แบบสุ่ม
     */
    public GachaSystem() {
        this.random = new Random();
    }

    /**
     * สร้าง GachaSystem ด้วย Random seed ที่กำหนด (สำหรับ Testing)
     *
     * @param seed Random seed สำหรับทดสอบ
     */
    public GachaSystem(long seed) {
        this.random = new Random(seed);
    }

    // ==================== Core Gacha Methods ====================

    /**
     * สุ่มกาชา 1 ครั้ง และคืนสัตว์เลี้ยงที่ได้
     * ใช้หลักการ Polymorphism: คืนค่าเป็น AbstractPet แต่ Object จริงอาจเป็น Cat, Dog หรือ MythicPet
     *
     * @return AbstractPet สัตว์เลี้ยงที่สุ่มได้ (Cat, Dog, หรือ MythicPet)
     */
    public AbstractPet roll() {
        double chance = random.nextDouble(); // สุ่มตัวเลข 0.0 - 1.0

        if (chance < MYTHIC_RATE) {
            // 0.00 - 0.09 = MythicPet (10%)
            return createMythicPet();
        } else if (chance < MYTHIC_RATE + DOG_RATE) {
            // 0.10 - 0.49 = Dog (40%)
            return createDog();
        } else {
            // 0.50 - 1.00 = Cat (50%)
            return createCat();
        }
    }

    /**
     * สุ่มกาชา 10 ครั้งพร้อมกัน (แบบ Multi-pull)
     * รับประกัน MythicPet อย่างน้อย 1 ตัวใน 10 ครั้ง
     *
     * @return AbstractPet[] อาร์เรย์สัตว์เลี้ยง 10 ตัว
     */
    public AbstractPet[] rollTen() {
        AbstractPet[] results = new AbstractPet[10];
        boolean hasMythic = false;

        for (int i = 0; i < 10; i++) {
            results[i] = roll();
            if (results[i] instanceof MythicPet) {
                hasMythic = true;
            }
        }

        // Pity System: ถ้าไม่ได้ MythicPet เลย ให้ตัวสุดท้ายเป็น MythicPet
        if (!hasMythic) {
            results[9] = createMythicPet();
        }

        return results;
    }

    // ==================== Private Factory Methods ====================

    /**
     * สร้าง Cat พร้อมชื่อสุ่ม
     *
     * @return Cat object ใหม่
     */
    private Cat createCat() {
        String name = CAT_NAMES[random.nextInt(CAT_NAMES.length)];
        return new Cat(name);
    }

    /**
     * สร้าง Dog พร้อมชื่อสุ่ม
     *
     * @return Dog object ใหม่
     */
    private Dog createDog() {
        String name = DOG_NAMES[random.nextInt(DOG_NAMES.length)];
        return new Dog(name);
    }

    /**
     * สร้าง MythicPet พร้อมชื่อสุ่ม
     *
     * @return MythicPet object ใหม่
     */
    private MythicPet createMythicPet() {
        String name = MYTHIC_NAMES[random.nextInt(MYTHIC_NAMES.length)];
        return new MythicPet(name);
    }

    // ==================== Utility Methods ====================

    /**
     * ดึงข้อความอธิบายอัตราการสุ่ม
     *
     * @return String อธิบาย Drop Rate ทั้งหมด
     */
    public String getDropRateInfo() {
        return String.format(
                "🎰 Drop Rate:\n" +
                        "  🐱 Cat: %.0f%%\n" +
                        "  🐶 Dog: %.0f%%\n" +
                        "  🦄 MythicPet: %.0f%%\n" +
                        "💰 ราคา: %d coins/roll",
                CAT_RATE * 100, DOG_RATE * 100, MYTHIC_RATE * 100, GACHA_COST
        );
    }
    public boolean canRoll(int coins) {
        return coins >= GACHA_COST;
    }

    public boolean canRollTen(int coins) {
        return coins >= GACHA_COST * 10;
    }
}