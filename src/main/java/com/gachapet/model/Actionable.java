package com.gachapet.model;

/**
 * Interface สำหรับบังคับให้สัตว์เลี้ยงทุกตัวต้องมี action พื้นฐาน
 * ใช้แสดงหลักการ Polymorphism
 */
public interface Actionable {

    void eat(int amount);

    void play();

    void sleep();

    void wakeUp();

    String makeSound();

    void performAction();

    String getSpecialSkill();
}