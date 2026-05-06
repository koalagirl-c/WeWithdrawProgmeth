package com.gachapet.model;

import java.io.Serializable;

/**
 * Abstract Class ที่เป็นแม่แบบของสัตว์เลี้ยงทุกตัวในเกม
 * ใช้ Encapsulation, Inheritance, Abstraction, Polymorphism และ Interface
 */
public abstract class AbstractPet implements Actionable, Serializable {

    private static final long serialVersionUID = 1L;

    public static final int MAX_HP = 100;
    public static final int MAX_HUNGER = 100;
    public static final int MAX_HAPPINESS = 100;
    public static final int MAX_ENERGY = 100;
    public static final int MAX_LEVEL = 50;

    private static final int ACTIVE_DECAY_INTERVAL_SECONDS = 30;
    private static final int INACTIVE_DECAY_INTERVAL_SECONDS = 60;
    private static final int SLEEP_ENERGY_INTERVAL_SECONDS = 8;
    private static final int SLEEP_HUNGER_INTERVAL_SECONDS = 45;
    private static final String ENGLISH_NAME_PATTERN = "[A-Za-z ]+";

    private String name;
    private int hp;
    private int hunger;      // 100 = full, 0 = starving
    private int happiness;   // 100 = very happy
    private int energy;      // 100 = full energy
    private int level;
    private int experience;
    private int age;
    private int inactiveAge;
    private boolean sleeping;

    public AbstractPet(String name) {
        setName(name);
        this.hp = MAX_HP;
        this.hunger = MAX_HUNGER;
        this.happiness = MAX_HAPPINESS;
        this.energy = MAX_ENERGY;
        this.level = 1;
        this.experience = 0;
        this.age = 0;
        this.inactiveAge = 0;
        this.sleeping = false;
    }

    // ==================== Getters ====================

    public String getName() {
        return name;
    }

    public int getHp() {
        return hp;
    }

    public int getHunger() {
        return hunger;
    }

    public int getHappiness() {
        return happiness;
    }

    public int getEnergy() {
        return energy;
    }

    public int getLevel() {
        return level;
    }

    public int getExperience() {
        return experience;
    }

    public int getAge() {
        return age;
    }

    public boolean isSleeping() {
        return sleeping;
    }

    public boolean isAlive() {
        return hp > 0;
    }

    // ==================== Setters ====================

    public void setName(String name) {
        if (name == null || name.trim().isEmpty()) {
            throw new IllegalArgumentException("Pet name cannot be empty");
        }

        String trimmedName = name.trim();
        if (!trimmedName.matches(ENGLISH_NAME_PATTERN)) {
            throw new IllegalArgumentException("Pet name must contain English letters and spaces only");
        }

        this.name = trimmedName;
    }

    public void setHp(int hp) {
        this.hp = clamp(hp, 0, MAX_HP);
    }

    public void setHunger(int hunger) {
        this.hunger = clamp(hunger, 0, MAX_HUNGER);
    }

    public void setHappiness(int happiness) {
        this.happiness = clamp(happiness, 0, MAX_HAPPINESS);
    }

    public void setEnergy(int energy) {
        this.energy = clamp(energy, 0, MAX_ENERGY);
    }

    public void setLevel(int level) {
        this.level = clamp(level, 1, MAX_LEVEL);
    }

    public void setSleeping(boolean sleeping) {
        if (!isAlive()) {
            this.sleeping = false;
            return;
        }
        this.sleeping = sleeping;
    }

    private int clamp(int value, int min, int max) {
        return Math.max(min, Math.min(value, max));
    }

    // ==================== Shared Logic ====================

    protected boolean canDoAction() {
        return isAlive() && !sleeping;
    }

    @Override
    public void eat(int amount) {
        if (!canDoAction()) return;

        setHunger(this.hunger + amount);
        setHp(this.hp + amount / 5);
        setHappiness(this.happiness + 2);
        gainExperience(2);

        System.out.println(name + " eats food. +Hunger:" + amount);
    }

    @Override
    public void play() {
        if (!canDoAction()) return;

        setHappiness(this.happiness + 15);
        setEnergy(this.energy - 10);
        setHunger(this.hunger - 10);
        gainExperience(5);

        System.out.println(name + " plays happily! +Happiness:15 -Energy:10 -Hunger:10");
    }

    @Override
    public void sleep() {
        if (!isAlive()) return;

        this.sleeping = true;
        System.out.println(name + " is falling asleep...");
    }

    @Override
    public void wakeUp() {
        if (!isAlive()) return;

        this.sleeping = false;
        System.out.println(name + " woke up!");
    }

    public void gainExperience(int amount) {
        if (amount <= 0 || !isAlive()) return;

        this.experience += amount;

        int expNeeded = level * 100;

        while (this.experience >= expNeeded && this.level < MAX_LEVEL) {
            this.experience -= expNeeded;
            this.level++;

            setHp(this.hp + 10);
            setHappiness(this.happiness + 5);

            System.out.println("🎉 " + name + " Level Up! Reached Level " + this.level);

            expNeeded = level * 100;
        }

        if (this.level >= MAX_LEVEL) {
            this.level = MAX_LEVEL;
            this.experience = 0;
        }
    }

    /**
     * ใช้ให้ Thread หรือ GameLoop เรียกทุก ๆ 1 วินาที
     */
    public void updateStatus() {
        if (!isAlive()) {
            sleeping = false;
            return;
        }

        age++;

        if (sleeping) {
            if (age % SLEEP_ENERGY_INTERVAL_SECONDS == 0) {
                setEnergy(this.energy + 5);
            }

            if (age % SLEEP_HUNGER_INTERVAL_SECONDS == 0) {
                setHunger(this.hunger - 1);
            }

            if (this.energy >= MAX_ENERGY) {
                sleeping = false;
                System.out.println(name + " is fully rested!");
            }
        } else {
            if (age % ACTIVE_DECAY_INTERVAL_SECONDS == 0) {
                setHunger(this.hunger - getHungerDecayRate());
                setHappiness(this.happiness - getHappinessDecayRate());
                setEnergy(this.energy - getEnergyDecayRate());
            }
        }

        if (age % ACTIVE_DECAY_INTERVAL_SECONDS == 0) {
            updateHpByCondition();
        }
    }

    /**
     * alias สำหรับเรียกสั้น ๆ จาก GameLoop
     */
    public void tick() {
        updateStatus();
    }

    /**
     * ใช้กับสัตว์ที่ไม่ได้ถูกเลือกอยู่ ให้เวลายังเดินแต่ลดช้ากว่าตัวที่กำลังดูแล
     */
    public void tickInactive() {
        if (!isAlive()) {
            sleeping = false;
            return;
        }

        inactiveAge++;

        if (inactiveAge % INACTIVE_DECAY_INTERVAL_SECONDS == 0) {
            setHunger(this.hunger - 1);
            setHappiness(this.happiness - 1);
            updateHpByCondition();
        }
    }

    public boolean needsCareAlert() {
        return isAlive() && (hunger <= 50 || happiness <= 50 || energy <= 50);
    }

    public String getCareAlertText() {
        if (!needsCareAlert()) {
            return "";
        }

        StringBuilder alert = new StringBuilder(getEmoji())
                .append(" ")
                .append(name)
                .append(" needs care: ");

        boolean hasPreviousStat = false;

        if (hunger <= 50) {
            alert.append("Hunger ").append(hunger).append("/100");
            hasPreviousStat = true;
        }

        if (happiness <= 50) {
            if (hasPreviousStat) alert.append(", ");
            alert.append("Happiness ").append(happiness).append("/100");
            hasPreviousStat = true;
        }

        if (energy <= 50) {
            if (hasPreviousStat) alert.append(", ");
            alert.append("Energy ").append(energy).append("/100");
        }

        return alert.toString();
    }

    private void updateHpByCondition() {
        int damage = 0;

        if (hunger < 20) {
            damage += 3;
        }

        if (happiness < 20) {
            damage += 2;
        }

        if (energy < 10) {
            damage += 2;
        }

        if (damage > 0) {
            setHp(this.hp - damage);
        } else if (hunger > 60 && happiness > 60 && energy > 60) {
            setHp(this.hp + 1);
        }

        if (hp <= 0) {
            hp = 0;
            sleeping = false;
            System.out.println("💀 " + name + " fainted... Game Over");
        }
    }

    public String getStatusText() {
        if (!isAlive()) {
            return "Game Over";
        }

        if (sleeping) {
            return "Sleeping";
        }

        if (hp <= 30) {
            return "Poor health, needs immediate care";
        }

        if (hunger <= 30) {
            return "Hungry";
        }

        if (happiness <= 30) {
            return "Lonely, wants to play";
        }

        if (energy <= 30) {
            return "Tired, wants to rest";
        }

        return "Healthy";
    }

    // ==================== Decay Rate Methods ====================

    protected int getHungerDecayRate() {
        return 2;
    }

    protected int getHappinessDecayRate() {
        return 1;
    }

    protected int getEnergyDecayRate() {
        return 1;
    }

    // ==================== Abstract Methods ====================

    public abstract String getPetType();

    public abstract String getEmoji();

    @Override
    public abstract String makeSound();

    @Override
    public abstract void performAction();

    @Override
    public abstract String getSpecialSkill();

    @Override
    public String toString() {
        return String.format(
                "%s %s (Lv.%d) | HP: %d/%d | Hunger: %d/%d | Happiness: %d/%d | Energy: %d/%d | EXP: %d",
                getEmoji(),
                name,
                level,
                hp,
                MAX_HP,
                hunger,
                MAX_HUNGER,
                happiness,
                MAX_HAPPINESS,
                energy,
                MAX_ENERGY,
                experience
        );
    }

    public String toCsvString() {
        return String.format(
                "%s,%s,%d,%d,%d,%d,%d,%d,%b",
                getPetType(),
                name,
                hp,
                hunger,
                happiness,
                energy,
                level,
                experience,
                sleeping
        );
    }
}
