package com.gachapet.model;

/**
 * คลาสแมว (Cat) สัตว์เลี้ยงพื้นฐานในเกม
 * extends AbstractPet เพื่อรับคุณสมบัติพื้นฐานทั้งหมด (Inheritance)
 *
 * <p>คุณสมบัติพิเศษของแมว:</p>
 * <ul>
 *   <li>ค่าความหิวลดช้ากว่าปกติ (กินน้อย)</li>
 *   <li>เล่นแล้วได้ EXP เพิ่มพิเศษ (ชอบเล่น)</li>
 *   <li>การกระทำพิเศษ: ม้วนตัวนอน ลดความหิวได้</li>
 * </ul>
 */
public class Cat extends AbstractPet {

    /** โบนัส EXP ที่แมวได้รับเพิ่มเติมเมื่อเล่น */
    private static final int CAT_PLAY_EXP_BONUS = 3;

    /** อัตราการลดความหิวของแมวต่อ tick (ช้ากว่า Default) */
    private static final int CAT_HUNGER_DECAY = 3;

    /**
     * สร้างสัตว์เลี้ยงแมวใหม่
     *
     * @param name ชื่อของแมว
     */
    public Cat(String name) {
        super(name); // เรียก Constructor ของ AbstractPet
    }

    /**
     * Override: แมวกินอาหารได้มีประสิทธิภาพกว่าปกติ (ฟื้น HP ได้มากกว่า)
     * แสดงหลักการ Polymorphism - เมธอดชื่อเดียวกันแต่ทำงานต่างกัน
     *
     * @param amount จำนวนที่ต้องการเพิ่มค่าความหิว
     */
    @Override
    public void eat(int amount) {
        setHunger(getHunger() + amount);
        setHp(getHp() + (amount / 3)); // แมวฟื้น HP ดีกว่า default
        gainExperience(2);
        System.out.println(getName() + " เหมียวกินอาหารอย่างสง่า ✨");
    }

    /**
     * Override: แมวเล่นแล้วได้ EXP เพิ่มพิเศษ
     * Polymorphism: เมธอด play() แต่ผลลัพธ์ต่างจาก Dog
     */
    @Override
    public void play() {
        setHunger(getHunger() - 8); // แมวใช้พลังงานน้อยกว่า
        gainExperience(5 + CAT_PLAY_EXP_BONUS); // ได้ EXP bonus
        System.out.println(getName() + " เล่นลูกบอลอย่างสนุกสนาน! +EXP:" + (5 + CAT_PLAY_EXP_BONUS));
    }

    /**
     * Override: กำหนดอัตราการลดความหิวเฉพาะของแมว
     * แมวกินน้อย ดังนั้น Hunger ลดช้ากว่า
     *
     * @return อัตราการลดความหิวของแมวต่อ tick
     */
    @Override
    protected int getHungerDecayRate() {
        return CAT_HUNGER_DECAY;
    }

    /**
     * การกระทำพิเศษของแมว: ม้วนตัวนอนพักฟื้น
     * Implement จาก Interface Actionable (Polymorphism)
     */
    @Override
    public void performAction() {
        // แมวม้วนตัวนอน ฟื้นฟู HP เล็กน้อย
        setHp(getHp() + 5);
        setHunger(getHunger() - 5);
        System.out.println(getName() + " ม้วนตัวนอนหลับอย่างน่ารัก 😴 +HP:5");
    }

    /**
     * เสียงร้องของแมว
     * Implement จาก Interface Actionable (Polymorphism)
     *
     * @return เสียงร้องของแมว
     */
    @Override
    public String makeSound() {
        return "Meow~ Nyaa~ 🐱";
    }

    /**
     * ดึงชนิดสัตว์เลี้ยง
     *
     * @return "CAT"
     */
    @Override
    public String getPetType() {
        return "CAT";
    }

    /**
     * ดึง Emoji ของแมว
     *
     * @return Emoji แมว
     */
    @Override
    public String getEmoji() {
        return "🐱";
    }
}