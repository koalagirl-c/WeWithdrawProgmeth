package com.gachapet.model;

/**
 * คลาสสุนัข (Dog) สัตว์เลี้ยงพื้นฐานในเกม
 * extends AbstractPet เพื่อรับคุณสมบัติพื้นฐานทั้งหมด (Inheritance)
 *
 * <p>คุณสมบัติพิเศษของสุนัข:</p>
 * <ul>
 * <li>ค่าความอิ่มลดเร็วกว่าปกติ เพราะสุนัขกินจุ</li>
 * <li>เล่นแล้วได้ HP Bonus เพิ่ม เพราะชอบออกกำลังกาย</li>
 * <li>การกระทำพิเศษ: โชว์ลูกเล่น ได้ EXP มาก แต่ใช้พลังงานเยอะ</li>
 * </ul>
 */
public class Dog extends AbstractPet {

    private static final int DOG_PLAY_HP_BONUS = 3;
    private static final int DOG_HUNGER_DECAY = 3;
    private static final int DOG_TRICK_EXP_REWARD = 12;


    public Dog(String name) {
        super(name);
    }

    @Override
    public void eat(int amount) {
        if (!canDoAction()) return;

        setHunger(getHunger() + amount + 5);
        setHp(getHp() + amount / 8);
        setHappiness(getHappiness() + 3);
        gainExperience(2);

        System.out.println(getName() + " Woof! Eats everything in the bowl! 🍖");
    }

    @Override
    public void play() {
        if (!canDoAction()) return;

        setHappiness(getHappiness() + 18);
        setEnergy(getEnergy() - 10);
        setHunger(getHunger() - 6);
        setHp(getHp() + DOG_PLAY_HP_BONUS);
        gainExperience(5);

        System.out.println(getName() + " runs around happily! +HP:" + DOG_PLAY_HP_BONUS);
    }


    @Override
    protected int getHungerDecayRate() {
        return DOG_HUNGER_DECAY;
    }

    @Override
    public void performAction() {
        if (!canDoAction()) return;

        gainExperience(DOG_TRICK_EXP_REWARD);
        setHappiness(getHappiness() + 10);
        setEnergy(getEnergy() - 10);
        setHunger(getHunger() - 8);

        System.out.println(getName() + " Sit! Stand! Backflip! +EXP:" + DOG_TRICK_EXP_REWARD + " 🐕");
    }


    @Override
    public String makeSound() {
        return "Woof! Woof! 🐶";
    }

    @Override
    public String getPetType() {
        return "DOG";
    }

    @Override
    public String getEmoji() {
        return "🐶";
    }

    @Override
    public String getSpecialSkill() {
        return "Trick Show: Perform tricks to gain a large amount of EXP";
    }
}