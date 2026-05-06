package com.gachapet.model;

/**
 * คลาสสัตว์ในตำนาน (MythicPet) สัตว์เลี้ยง SSR ที่หายากที่สุดในเกม
 * โอกาสสุ่มได้จากตู้กาชาเพียง 10% เท่านั้น
 * extends AbstractPet เพื่อรับคุณสมบัติพื้นฐานทั้งหมด (Inheritance)
 *
 * <p>คุณสมบัติพิเศษของสัตว์ในตำนาน:</p>
 * <ul>
 *   <li>ค่าความหิวลดช้ามาก (อึดทน)</li>
 *   <li>สามารถฟื้น HP ของตัวเองได้ (ความสามารถพิเศษ)</li>
 *   <li>ได้รับ EXP เพิ่มสองเท่าจากทุกกิจกรรม</li>
 *   <li>การกระทำพิเศษ: ใช้พลังเวทมนตร์ฟื้นฟู HP เต็ม</li>
 * </ul>
 */
public class MythicPet extends AbstractPet {

    /** ตัวคูณ EXP ที่สัตว์ในตำนานได้รับ */
    private static final double MYTHIC_EXP_MULTIPLIER = 2.0;

    /** อัตราการลดความหิวของสัตว์ในตำนานต่อ tick (ช้ามาก) */
    private static final int MYTHIC_HUNGER_DECAY = 1;

    /** จำนวนครั้งที่ยังสามารถใช้ Ultimate Skill ได้ */
    private int ultimateCharges;

    /**
     * สร้างสัตว์เลี้ยงในตำนานใหม่
     *
     * @param name ชื่อของสัตว์ในตำนาน
     */
    public MythicPet(String name) {
        super(name);
        this.ultimateCharges = 3; // เริ่มต้นมี 3 ครั้ง
    }

    /**
     * Override: สัตว์ในตำนานกินอาหารแล้วได้ EXP สองเท่า
     * Polymorphism: เมธอด eat() ที่ทรงพลังที่สุด
     *
     * @param amount จำนวนที่ต้องการเพิ่มค่าความหิว
     */
    @Override
    public void eat(int amount) {
        setHunger(getHunger() + amount);
        setHp(getHp() + (amount / 4));
        gainExperience((int)(2 * MYTHIC_EXP_MULTIPLIER)); // EXP สองเท่า
        System.out.println(getName() + " รับพลังงานจากอาหารเวทมนตร์ ✨🌟");
    }

    /**
     * Override: สัตว์ในตำนานเล่นแล้วได้ EXP สองเท่า และลดความหิวน้อยมาก
     * Polymorphism: เมธอด play() ที่ทรงพลังที่สุด
     */
    @Override
    public void play() {
        setHunger(getHunger() - 3); // ลดความหิวน้อยมาก
        gainExperience((int)(5 * MYTHIC_EXP_MULTIPLIER)); // EXP สองเท่า
        System.out.println(getName() + " เล่นอย่างลึกลับและน่าพิศวง! +EXP:" + (int)(5 * MYTHIC_EXP_MULTIPLIER));
    }

    /**
     * Override: กำหนดอัตราการลดความหิวเฉพาะของสัตว์ในตำนาน
     * อึดทนมาก Hunger ลดช้าที่สุด
     *
     * @return อัตราการลดความหิวของสัตว์ในตำนานต่อ tick
     */
    @Override
    protected int getHungerDecayRate() {
        return MYTHIC_HUNGER_DECAY;
    }

    /**
     * Ultimate Skill: ใช้พลังเวทมนตร์ฟื้นฟู HP เต็มหลอด
     * สามารถใช้ได้เฉพาะเมื่อยังมี ultimateCharges เหลืออยู่
     * Implement จาก Interface Actionable (Polymorphism)
     */
    @Override
    public void performAction() {
        if (ultimateCharges > 0) {
            setHp(MAX_HP); // ฟื้น HP เต็ม!
            ultimateCharges--;
            System.out.println(getName() + " ใช้พลังเวทมนตร์ฟื้นฟู HP เต็มหลอด! 🌟 (เหลือ " + ultimateCharges + " ครั้ง)");
        } else {
            // ถ้าไม่มี Charge เหลือ ทำ Default Action แทน
            gainExperience(5);
            System.out.println(getName() + " ไม่มีพลังงานเหลือแล้ว... ทำสมาธิแทน (+EXP:5)");
        }
    }

    /**
     * เสียงร้องของสัตว์ในตำนาน
     * Implement จาก Interface Actionable (Polymorphism)
     *
     * @return เสียงร้องอันลึกลับของสัตว์ในตำนาน
     */
    @Override
    public String makeSound() {
        return "✨ *เสียงเวทมนตร์* KYAAAA~~ 🌟";
    }

    /**
     * ดึงจำนวน Ultimate Charge ที่เหลือ
     *
     * @return จำนวน Ultimate Charges
     */
    public int getUltimateCharges() {
        return ultimateCharges;
    }

    /**
     * เติม Ultimate Charge (ใช้ได้เมื่อ Level Up)
     *
     * @param amount จำนวน Charge ที่ต้องการเติม
     */
    public void addUltimateCharges(int amount) {
        this.ultimateCharges = Math.min(this.ultimateCharges + amount, 5);
    }

    /**
     * ดึงชนิดสัตว์เลี้ยง
     *
     * @return "MYTHIC"
     */
    @Override
    public String getPetType() {
        return "MYTHIC";
    }

    /**
     * ดึง Emoji ของสัตว์ในตำนาน
     *
     * @return Emoji สัตว์ในตำนาน
     */
    @Override
    public String getEmoji() {
        return "🦄";
    }

    /**
     * Override toString เพื่อแสดงข้อมูล Ultimate Charges เพิ่มเติม
     *
     * @return ข้อมูลสรุปของสัตว์ในตำนาน
     */
    @Override
    public String toString() {
        return super.toString() + " | ⚡ Ultimate: " + ultimateCharges + "/3";
    }
}