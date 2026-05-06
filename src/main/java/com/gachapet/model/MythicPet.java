package com.gachapet.model;

/**
 * คลาสสัตว์ในตำนาน (MythicPet) สัตว์เลี้ยง SSR ที่หายากที่สุดในเกม
 * โอกาสสุ่มได้จากตู้กาชาเพียง 10% เท่านั้น
 * extends AbstractPet เพื่อรับคุณสมบัติพื้นฐานทั้งหมด (Inheritance)
 *
 * <p>คุณสมบัติพิเศษของสัตว์ในตำนาน:</p>
 * <ul>
 * <li>ค่าความอิ่มลดช้ามาก เพราะอึดทน</li>
 * <li>ฟื้น HP ของตัวเองได้</li>
 * <li>ได้รับ EXP เพิ่มสองเท่าจากกิจกรรมหลัก</li>
 * <li>การกระทำพิเศษ: ใช้พลังเวทมนตร์ฟื้นฟู HP เต็ม</li>
 * </ul>
 */
public class MythicPet extends AbstractPet {

    /** ตัวคูณ EXP ที่สัตว์ในตำนานได้รับ */
    private static final double MYTHIC_EXP_MULTIPLIER = 2.0;

    /** อัตราการลดค่าความอิ่มของสัตว์ในตำนานต่อ tick */
    private static final int MYTHIC_HUNGER_DECAY = 1;

    /** จำนวน Ultimate Charge สูงสุด */
    private static final int MAX_ULTIMATE_CHARGES = 3;

    /** จำนวนครั้งที่ยังสามารถใช้ Ultimate Skill ได้ */
    private int ultimateCharges;

    /**
     * สร้างสัตว์เลี้ยงในตำนานใหม่
     *
     * @param name ชื่อของสัตว์ในตำนาน
     */
    public MythicPet(String name) {
        super(name);
        this.ultimateCharges = MAX_ULTIMATE_CHARGES;
    }

    /**
     * Override: สัตว์ในตำนานกินอาหารแล้วได้ EXP สองเท่า
     *
     * @param amount จำนวนที่ต้องการเพิ่มค่าความอิ่ม
     */
    @Override
    public void eat(int amount) {
        if (!canDoAction()) return;

        setHunger(getHunger() + amount + 10);
        setHp(getHp() + amount / 4);
        setHappiness(getHappiness() + 5);
        gainExperience((int) (2 * MYTHIC_EXP_MULTIPLIER));

        System.out.println(getName() + " absorbs energy from magical food ✨🌟");
    }

    /**
     * Override: สัตว์ในตำนานเล่นแล้วได้ EXP สองเท่า และใช้พลังงานน้อย
     */
    @Override
    public void play() {
        if (!canDoAction()) return;

        setHappiness(getHappiness() + 30);
        setEnergy(getEnergy() - 8);
        setHunger(getHunger() - 3);
        gainExperience((int) (5 * MYTHIC_EXP_MULTIPLIER));

        System.out.println(getName() + " plays mysteriously! +EXP:" + (int) (5 * MYTHIC_EXP_MULTIPLIER));
    }

    /**
     * Override: กำหนดอัตราการลดค่าความอิ่มเฉพาะของสัตว์ในตำนาน
     *
     * @return อัตราการลดค่าความอิ่มของสัตว์ในตำนานต่อ tick
     */
    @Override
    protected int getHungerDecayRate() {
        return MYTHIC_HUNGER_DECAY;
    }

    /**
     * Override: ความสุขลดช้ากว่าสัตว์ทั่วไป
     *
     * @return อัตราการลด Happiness ต่อ tick
     */
    @Override
    protected int getHappinessDecayRate() {
        return 1;
    }

    /**
     * Override: พลังงานลดช้ากว่าสัตว์ทั่วไป
     *
     * @return อัตราการลด Energy ต่อ tick
     */
    @Override
    protected int getEnergyDecayRate() {
        return 1;
    }

    /**
     * Ultimate Skill: ใช้พลังเวทมนตร์ฟื้นฟู HP เต็มหลอด
     * ถ้าไม่มี charge เหลือ จะทำสมาธิเพื่อรับ EXP แทน
     */
    @Override
    public void performAction() {
        if (!canDoAction()) return;

        if (ultimateCharges > 0) {
            setHp(MAX_HP);
            setHappiness(getHappiness() + 10);
            setEnergy(getEnergy() + 10);
            ultimateCharges--;

            System.out.println(getName() + " uses magic to fully restore HP! 🌟 Remaining: " + ultimateCharges + " charges");
        } else {
            gainExperience(5);
            setHappiness(getHappiness() + 5);

            System.out.println(getName() + " has no magic power left... Meditating instead +EXP:5");
        }
    }

    /**
     * เสียงร้องของสัตว์ในตำนาน
     *
     * @return เสียงร้องอันลึกลับของสัตว์ในตำนาน
     */
    @Override
    public String makeSound() {
        return "✨ *Magical Sounds* KYAAAA~~ 🌟";
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
     * เติม Ultimate Charge
     *
     * @param amount จำนวน Charge ที่ต้องการเติม
     */
    public void addUltimateCharges(int amount) {
        if (amount <= 0) return;

        this.ultimateCharges = Math.min(this.ultimateCharges + amount, MAX_ULTIMATE_CHARGES);
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
     * ดึงสกิลพิเศษของ MythicPet
     *
     * @return รายละเอียดสกิลพิเศษ
     */
    @Override
    public String getSpecialSkill() {
        return "Ultimate Heal: Use magic power to fully restore HP and increase Happiness/Energy";
    }

    /**
     * Override toString เพื่อแสดงข้อมูล Ultimate Charges เพิ่มเติม
     *
     * @return ข้อมูลสรุปของสัตว์ในตำนาน
     */
    @Override
    public String toString() {
        return super.toString() + " | ⚡ Ultimate: " + ultimateCharges + "/" + MAX_ULTIMATE_CHARGES;
    }
}