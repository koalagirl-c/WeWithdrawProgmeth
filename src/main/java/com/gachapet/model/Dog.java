package com.gachapet.model;

/**
 * คลาสสุนัข (Dog) สัตว์เลี้ยงพื้นฐานในเกม
 * extends AbstractPet เพื่อรับคุณสมบัติพื้นฐานทั้งหมด (Inheritance)
 *
 * <p>คุณสมบัติพิเศษของสุนัข:</p>
 * <ul>
 *   <li>ค่าความหิวลดเร็วกว่าปกติ (กินจุ)</li>
 *   <li>เล่นแล้วได้ HP Bonus เพิ่ม (ชอบออกกำลังกาย)</li>
 *   <li>การกระทำพิเศษ: โชว์ลูกเล่น เพิ่ม EXP มาก</li>
 * </ul>
 */
public class Dog extends AbstractPet {

    /** โบนัส HP ที่สุนัขได้รับเมื่อเล่น */
    private static final int DOG_PLAY_HP_BONUS = 5;

    /** อัตราการลดความหิวของสุนัขต่อ tick (เร็วกว่า Default) */
    private static final int DOG_HUNGER_DECAY = 8;

    /**
     * สร้างสัตว์เลี้ยงสุนัขใหม่
     *
     * @param name ชื่อของสุนัข
     */
    public Dog(String name) {
        super(name); // เรียก Constructor ของ AbstractPet
    }

    /**
     * Override: สุนัขกินเยอะ แต่ฟื้น HP ได้น้อยกว่า
     * Polymorphism: เมธอด eat() แต่ผลลัพธ์ต่างจาก Cat
     *
     * @param amount จำนวนที่ต้องการเพิ่มค่าความหิว
     */
    @Override
    public void eat(int amount) {
        setHunger(getHunger() + amount);
        setHp(getHp() + (amount / 8)); // สุนัขฟื้น HP น้อยกว่า
        gainExperience(2);
        System.out.println(getName() + " วูฟ! กินอาหารจนหมดชาม! 🍖");
    }

    /**
     * Override: สุนัขเล่นแล้วได้ HP Bonus เพิ่ม (ชอบออกกำลังกาย)
     * Polymorphism: เมธอด play() แต่ผลลัพธ์ต่างจาก Cat
     */
    @Override
    public void play() {
        setHunger(getHunger() - 15); // สุนัขใช้พลังงานมากกว่า
        setHp(getHp() + DOG_PLAY_HP_BONUS); // ออกกำลังกายแล้วแข็งแรง
        gainExperience(5);
        System.out.println(getName() + " วิ่งเล่นสนุกสุดๆ! +HP:" + DOG_PLAY_HP_BONUS);
    }

    /**
     * Override: กำหนดอัตราการลดความหิวเฉพาะของสุนัข
     * สุนัขกินจุ ดังนั้น Hunger ลดเร็วกว่า
     *
     * @return อัตราการลดความหิวของสุนัขต่อ tick
     */
    @Override
    protected int getHungerDecayRate() {
        return DOG_HUNGER_DECAY;
    }

    /**
     * การกระทำพิเศษของสุนัข: โชว์ลูกเล่น ได้ EXP เยอะมาก
     * Implement จาก Interface Actionable (Polymorphism)
     */
    @Override
    public void performAction() {
        // สุนัขโชว์ลูกเล่น ได้ EXP เยอะ แต่หิวมากขึ้น
        gainExperience(15);
        setHunger(getHunger() - 20);
        System.out.println(getName() + " นั่ง! ยืน! ตีลังกา! +EXP:15 🐕");
    }

    /**
     * เสียงร้องของสุนัข
     * Implement จาก Interface Actionable (Polymorphism)
     *
     * @return เสียงร้องของสุนัข
     */
    @Override
    public String makeSound() {
        return "Woof! Woof! 🐶";
    }

    /**
     * ดึงชนิดสัตว์เลี้ยง
     *
     * @return "DOG"
     */
    @Override
    public String getPetType() {
        return "DOG";
    }

    /**
     * ดึง Emoji ของสุนัข
     *
     * @return Emoji สุนัข
     */
    @Override
    public String getEmoji() {
        return "🐶";
    }
}