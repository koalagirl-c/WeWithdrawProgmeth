package com.gachapet.model;

/**
 * คลาสแมว (Cat) สัตว์เลี้ยงพื้นฐานในเกม
 * extends AbstractPet เพื่อรับคุณสมบัติพื้นฐานทั้งหมด (Inheritance)
 *
 * <p>คุณสมบัติพิเศษของแมว:</p>
 * <ul>
 *   <li>ค่าความอิ่มลดช้ากว่าปกติ เพราะแมวกินน้อย</li>
 *   <li>เล่นแล้วได้ EXP เพิ่มพิเศษ</li>
 *   <li>การกระทำพิเศษ: ม้วนตัวนอน ฟื้น HP และ Energy แต่ทำให้หิวขึ้นเล็กน้อย</li>
 * </ul>
 */
public class Cat extends AbstractPet {

    private static final int CAT_PLAY_EXP_BONUS = 3;
    private static final int CAT_HUNGER_DECAY = 3;

    public Cat(String name) {
        super(name);
    }

    @Override
    public void eat(int amount) {
        if (!canDoAction()) return;

        setHunger(getHunger() + amount);
        setHp(getHp() + amount / 3);
        setHappiness(getHappiness() + 3);
        gainExperience(2);

        System.out.println(getName() + " เหมียวกินอาหารอย่างสง่า ✨");
    }

    @Override
    public void play() {
        if (!canDoAction()) return;

        setHappiness(getHappiness() + 25);
        setEnergy(getEnergy() - 10);
        setHunger(getHunger() - 8);
        gainExperience(5 + CAT_PLAY_EXP_BONUS);

        System.out.println(getName() + " เล่นลูกบอลอย่างสนุกสนาน! +EXP:" + (5 + CAT_PLAY_EXP_BONUS));
    }

    @Override
    protected int getHungerDecayRate() {
        return CAT_HUNGER_DECAY;
    }

    @Override
    public void performAction() {
        if (!canDoAction()) return;

        setHp(getHp() + 5);
        setEnergy(getEnergy() + 8);
        setHunger(getHunger() - 5);
        gainExperience(3);

        System.out.println(getName() + " ม้วนตัวนอนหลับอย่างน่ารัก 😴 +HP:5 +Energy:8");
    }

    @Override
    public String makeSound() {
        return "Meow~ Nyaa~ 🐱";
    }

    @Override
    public String getPetType() {
        return "CAT";
    }

    @Override
    public String getEmoji() {
        return "🐱";
    }

    @Override
    public String getSpecialSkill() {
        return "Cat Nap: แมวม้วนตัวนอนเพื่อฟื้น HP และ Energy";
    }
}