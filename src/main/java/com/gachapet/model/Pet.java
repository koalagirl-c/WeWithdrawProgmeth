package com.gachapet.model;

import java.io.Serializable;

/**
 * Abstract class หลักของสัตว์เลี้ยง
 * ใช้แสดงหลักการ Abstraction, Encapsulation, Inheritance และ Access Modifier
 */
public abstract class Pet implements Actionable, Serializable {

    private static final long serialVersionUID = 1L;

    // protected = class ลูก เช่น Cat, Dog สามารถเข้าถึงได้
    protected String name;
    protected int hunger;
    protected int happiness;
    protected int energy;
    protected int hygiene;
    protected int health;
    protected int age;
    protected boolean alive;
    protected boolean sleeping;
    protected PetStatus status;

    public Pet(String name) {
        this.name = name;
        this.hunger = 80;
        this.happiness = 80;
        this.energy = 80;
        this.hygiene = 80;
        this.health = 100;
        this.age = 0;
        this.alive = true;
        this.sleeping = false;
        this.status = PetStatus.NORMAL;
    }

    /**
     * Method นี้จะถูกเรียกโดย GameLoop หรือ Thread ทุก ๆ 1 วินาที
     */
    public void tick() {
        if (!alive) {
            status = PetStatus.DEAD;
            return;
        }

        age++;

        if (sleeping) {
            increaseEnergy(5);
            decreaseHunger(2);
            decreaseHygiene(1);

            if (energy >= 100) {
                sleeping = false;
            }
        } else {
            decreaseHunger(getHungerDecayRate());
            decreaseHappiness(getHappinessDecayRate());
            decreaseEnergy(getEnergyDecayRate());
            decreaseHygiene(getHygieneDecayRate());
        }

        updateHealth();
        updateStatus();
    }

    /**
     * ให้ class ลูกกำหนดเองว่าแต่ละชนิดหิวเร็วแค่ไหน
     */
    protected abstract int getHungerDecayRate();

    /**
     * ให้ class ลูกกำหนดเองว่าความสุขลดเร็วแค่ไหน
     */
    protected abstract int getHappinessDecayRate();

    /**
     * ให้ class ลูกกำหนดเองว่าพลังงานลดเร็วแค่ไหน
     */
    protected abstract int getEnergyDecayRate();

    /**
     * ให้ class ลูกกำหนดเองว่าความสะอาดลดเร็วแค่ไหน
     */
    protected abstract int getHygieneDecayRate();

    protected void updateHealth() {
        if (hunger <= 0 || happiness <= 0 || energy <= 0 || hygiene <= 0) {
            decreaseHealth(5);
        }

        if (hunger > 50 && happiness > 50 && energy > 50 && hygiene > 50) {
            increaseHealth(1);
        }

        if (health <= 0) {
            health = 0;
            alive = false;
            sleeping = false;
            status = PetStatus.DEAD;
        }
    }

    protected void updateStatus() {
        if (!alive) {
            status = PetStatus.DEAD;
        } else if (sleeping) {
            status = PetStatus.SLEEPING;
        } else if (health <= 30) {
            status = PetStatus.SICK;
        } else if (hunger <= 30) {
            status = PetStatus.HUNGRY;
        } else if (hygiene <= 30) {
            status = PetStatus.DIRTY;
        } else if (energy <= 30) {
            status = PetStatus.TIRED;
        } else if (happiness <= 30) {
            status = PetStatus.SAD;
        } else {
            status = PetStatus.NORMAL;
        }
    }

    @Override
    public void feed() {
        if (!canDoAction()) return;

        increaseHunger(25);
        decreaseHygiene(5);
        increaseHappiness(3);
        updateStatus();
    }

    @Override
    public void play() {
        if (!canDoAction()) return;

        increaseHappiness(20);
        decreaseEnergy(15);
        decreaseHunger(8);
        decreaseHygiene(5);
        updateStatus();
    }

    @Override
    public void clean() {
        if (!canDoAction()) return;

        increaseHygiene(30);
        increaseHappiness(2);
        updateStatus();
    }

    @Override
    public void sleep() {
        if (!alive) return;

        sleeping = true;
        status = PetStatus.SLEEPING;
    }

    public void wakeUp() {
        if (!alive) return;

        sleeping = false;
        updateStatus();
    }

    protected boolean canDoAction() {
        return alive && !sleeping;
    }

    protected void increaseHunger(int amount) {
        hunger = Math.min(100, hunger + amount);
    }

    protected void decreaseHunger(int amount) {
        hunger = Math.max(0, hunger - amount);
    }

    protected void increaseHappiness(int amount) {
        happiness = Math.min(100, happiness + amount);
    }

    protected void decreaseHappiness(int amount) {
        happiness = Math.max(0, happiness - amount);
    }

    protected void increaseEnergy(int amount) {
        energy = Math.min(100, energy + amount);
    }

    protected void decreaseEnergy(int amount) {
        energy = Math.max(0, energy - amount);
    }

    protected void increaseHygiene(int amount) {
        hygiene = Math.min(100, hygiene + amount);
    }

    protected void decreaseHygiene(int amount) {
        hygiene = Math.max(0, hygiene - amount);
    }

    protected void increaseHealth(int amount) {
        health = Math.min(100, health + amount);
    }

    protected void decreaseHealth(int amount) {
        health = Math.max(0, health - amount);
    }

    public String getMoodText() {
        return switch (status) {
            case NORMAL -> "น้องกำลังสบายดี";
            case HUNGRY -> "น้องหิวแล้ว";
            case DIRTY -> "น้องตัวสกปรก";
            case TIRED -> "น้องเหนื่อยมาก";
            case SAD -> "น้องเหงา อยากเล่นด้วย";
            case SLEEPING -> "น้องกำลังนอน";
            case SICK -> "น้องป่วย ต้องดูแลด่วน";
            case DEAD -> "Game Over";
        };
    }

    public String getName() {
        return name;
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

    public int getHygiene() {
        return hygiene;
    }

    public int getHealth() {
        return health;
    }

    public int getAge() {
        return age;
    }

    public boolean isAlive() {
        return alive;
    }

    public boolean isSleeping() {
        return sleeping;
    }

    public PetStatus getStatus() {
        return status;
    }
}