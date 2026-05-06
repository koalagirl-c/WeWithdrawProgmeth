package com.gachapet.model;

/**
 * Abstract Class ที่เป็นแม่แบบ (Template) ของสัตว์เลี้ยงทุกตัวในเกม
 * ใช้ Encapsulation โดยทำให้ฟิลด์ทั้งหมดเป็น private และควบคุมผ่าน getter/setter
 * Subclass ทุกตัวต้อง extends คลาสนี้และ implement Interface Actionable
 *
 * <p>หลักการ OOP ที่ใช้:</p>
 * <ul>
 *   <li>Inheritance: คลาสลูก Cat, Dog, MythicPet จะ extends AbstractPet</li>
 *   <li>Encapsulation: ฟิลด์ทุกตัวเป็น private มี getter/setter พร้อม validation</li>
 *   <li>Abstraction: เมธอด makeSound() เป็น abstract บังคับให้คลาสลูก implement</li>
 * </ul>
 */
public abstract class AbstractPet implements Actionable {

    // ==================== Constants ====================

    /** ค่า HP สูงสุดที่สัตว์เลี้ยงสามารถมีได้ */
    public static final int MAX_HP = 100;

    /** ค่าความหิวสูงสุด (100 = อิ่มมาก, 0 = หิวมาก) */
    public static final int MAX_HUNGER = 100;

    /** ค่าระดับสูงสุดที่สัตว์เลี้ยง Level up ได้ */
    public static final int MAX_LEVEL = 50;

    // ==================== Private Fields (Encapsulation) ====================

    /** ชื่อของสัตว์เลี้ยง */
    private String name;

    /** ค่าพลังชีวิต (HP) ของสัตว์เลี้ยง ระหว่าง 0 - 100 */
    private int hp;

    /** ค่าความหิว: 100 = อิ่มสุด, 0 = หิวโซ */
    private int hunger;

    /** ระดับ (Level) ของสัตว์เลี้ยง */
    private int level;

    /** ประสบการณ์สะสมเพื่อ Level up */
    private int experience;

    // ==================== Constructor ====================

    /**
     * Constructor สำหรับสร้างสัตว์เลี้ยงใหม่
     *
     * @param name ชื่อของสัตว์เลี้ยง ห้ามเป็น null หรือ empty
     * @throws IllegalArgumentException ถ้า name เป็น null หรือว่างเปล่า
     */
    public AbstractPet(String name) {
        if (name == null || name.trim().isEmpty()) {
            throw new IllegalArgumentException("ชื่อสัตว์เลี้ยงต้องไม่ว่างเปล่า!");
        }
        this.name = name;
        this.hp = MAX_HP;         // เริ่มต้น HP เต็ม
        this.hunger = MAX_HUNGER; // เริ่มต้นอิ่มสุด
        this.level = 1;
        this.experience = 0;
    }

    // ==================== Getters (Encapsulation) ====================

    /**
     * ดึงชื่อสัตว์เลี้ยง
     * @return ชื่อสัตว์เลี้ยง
     */
    public String getName() { return name; }

    /**
     * ดึงค่า HP ปัจจุบัน
     * @return ค่า HP (0 - 100)
     */
    public int getHp() { return hp; }

    /**
     * ดึงค่าความหิวปัจจุบัน
     * @return ค่าความหิว (0 = หิวมาก, 100 = อิ่มมาก)
     */
    public int getHunger() { return hunger; }

    /**
     * ดึง Level ปัจจุบัน
     * @return Level ของสัตว์เลี้ยง
     */
    public int getLevel() { return level; }

    /**
     * ดึงค่าประสบการณ์สะสม
     * @return ค่า Experience
     */
    public int getExperience() { return experience; }

    // ==================== Setters with Validation (Encapsulation) ====================

    /**
     * ตั้งชื่อสัตว์เลี้ยง
     * @param name ชื่อใหม่ที่ต้องการตั้ง
     * @throws IllegalArgumentException ถ้า name เป็น null หรือว่างเปล่า
     */
    public void setName(String name) {
        if (name == null || name.trim().isEmpty()) {
            throw new IllegalArgumentException("ชื่อสัตว์เลี้ยงต้องไม่ว่างเปล่า!");
        }
        this.name = name;
    }

    /**
     * ตั้งค่า HP พร้อม Validation ป้องกันค่าเกินขอบเขต
     * Encapsulation: ห้ามให้ HP ติดลบหรือเกิน MAX_HP
     *
     * @param hp ค่า HP ที่ต้องการตั้ง
     */
    public void setHp(int hp) {
        // Boundary check: ป้องกัน HP ติดลบหรือเกิน MAX
        this.hp = Math.max(0, Math.min(hp, MAX_HP));
    }

    /**
     * ตั้งค่าความหิวพร้อม Validation ป้องกันค่าเกินขอบเขต
     * Encapsulation: ห้ามให้ Hunger ติดลบหรือเกิน MAX_HUNGER
     *
     * @param hunger ค่าความหิวที่ต้องการตั้ง
     */
    public void setHunger(int hunger) {
        this.hunger = Math.max(0, Math.min(hunger, MAX_HUNGER));
    }

    /**
     * ตั้ง Level พร้อม Validation
     * @param level Level ที่ต้องการตั้ง
     */
    public void setLevel(int level) {
        this.level = Math.max(1, Math.min(level, MAX_LEVEL));
    }

    // ==================== Concrete Methods (ใช้ร่วมกันได้ทุกคลาสลูก) ====================

    /**
     * ให้อาหารสัตว์เลี้ยง เพิ่มค่าความหิวและ HP เล็กน้อย
     * คลาสลูกสามารถ Override เมธอดนี้ได้เพื่อเปลี่ยนพฤติกรรม (Polymorphism)
     *
     * @param amount จำนวนที่ต้องการเพิ่มค่าความหิว (1-50)
     */
    public void eat(int amount) {
        setHunger(this.hunger + amount);
        // กินอาหารแล้วฟื้น HP เล็กน้อย
        setHp(this.hp + (amount / 5));
        gainExperience(2);
        System.out.println(name + " กินอาหาร +Hunger:" + amount);
    }

    /**
     * เล่นกับสัตว์เลี้ยง ลดความหิวแต่เพิ่ม Experience
     * คลาสลูกสามารถ Override เมธอดนี้ได้เพื่อเปลี่ยน bonus ที่ได้รับ
     */
    public void play() {
        setHunger(this.hunger - 10); // เล่นแล้วหิว
        gainExperience(5);
        System.out.println(name + " เล่นสนุก! -Hunger:10 +EXP:5");
    }

    /**
     * เพิ่ม Experience และตรวจสอบการ Level up
     *
     * @param amount จำนวน Experience ที่จะเพิ่ม
     */
    public void gainExperience(int amount) {
        this.experience += amount;
        // ทุก 100 EXP = Level Up 1 ครั้ง
        int expNeeded = level * 100;
        if (this.experience >= expNeeded && this.level < MAX_LEVEL) {
            this.level++;
            this.experience = 0;
            System.out.println("🎉 " + name + " Level Up! ตอนนี้ Level " + this.level);
        }
    }

    /**
     * ตรวจสอบว่าสัตว์เลี้ยงยังมีชีวิตอยู่หรือไม่
     *
     * @return true ถ้า HP > 0
     */
    public boolean isAlive() {
        return this.hp > 0;
    }

    /**
     * อัปเดตสถานะสัตว์เลี้ยงตามเวลาที่ผ่านไป (เรียกทุก tick)
     * ลดค่าความหิวทีละนิด ถ้าหิวมากจะลด HP ด้วย
     */
    public void updateStatus() {
        // ลดความหิวตามเวลา (แต่ละ Subclass override ได้)
        int hungerDecay = getHungerDecayRate();
        setHunger(this.hunger - hungerDecay);

        // ถ้าหิวจัด (Hunger < 20) HP จะลดด้วย
        if (this.hunger < 20) {
            setHp(this.hp - 3);
        }
    }

    /**
     * ดึงอัตราการลดของความหิวต่อ tick
     * Subclass สามารถ Override เพื่อตั้งค่าเฉพาะของสายพันธุ์ได้
     *
     * @return อัตราการลดความหิว (หน่วยต่อ tick)
     */
    protected int getHungerDecayRate() {
        return 5; // ค่า Default
    }

    // ==================== Abstract Methods (บังคับให้ Subclass Implement) ====================

    /**
     * ดึงชนิดของสัตว์เลี้ยง (เช่น "แมว", "สุนัข", "สัตว์ในตำนาน")
     * ทุก Subclass ต้อง implement เมธอดนี้
     *
     * @return String ชนิดของสัตว์เลี้ยง
     */
    public abstract String getPetType();

    /**
     * ดึงชื่อ Emoji ของสัตว์เลี้ยงเพื่อแสดงใน UI
     *
     * @return String emoji ของสัตว์เลี้ยง
     */
    public abstract String getEmoji();

    // ==================== Override Interface Methods ====================

    /**
     * ให้สัตว์เลี้ยงส่งเสียงร้อง (จาก Interface Actionable)
     * ทุก Subclass ต้อง Override เมธอดนี้
     *
     * @return เสียงร้องของสัตว์เลี้ยง
     */
    @Override
    public abstract String makeSound();

    /**
     * การกระทำพิเศษเฉพาะของสัตว์เลี้ยงแต่ละสายพันธุ์ (จาก Interface Actionable)
     */
    @Override
    public abstract void performAction();

    // ==================== toString ====================

    /**
     * แสดงข้อมูลสัตว์เลี้ยงในรูปแบบ String
     *
     * @return ข้อมูลสรุปของสัตว์เลี้ยง
     */
    @Override
    public String toString() {
        return String.format("%s %s (Lv.%d) | HP: %d/%d | Hunger: %d/%d",
                getEmoji(), name, level, hp, MAX_HP, hunger, MAX_HUNGER);
    }

    /**
     * แปลงข้อมูลสัตว์เลี้ยงเป็นรูปแบบ CSV สำหรับบันทึกไฟล์
     *
     * @return String ในรูปแบบ CSV
     */
    public String toCsvString() {
        return String.format("%s,%s,%d,%d,%d,%d",
                getPetType(), name, hp, hunger, level, experience);
    }
}