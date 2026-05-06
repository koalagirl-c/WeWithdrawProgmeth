package com.gachapet.model;

/**
 * Interface ที่กำหนดพฤติกรรมที่สัตว์เลี้ยงทุกตัวต้องสามารถทำได้
 * ใช้ Interface เพื่อบังคับให้ทุก Subclass ต้อง Implement เมธอดเหล่านี้
 * (Design Pattern: Contract-based programming)
 */
public interface Actionable {

    /**
     * กำหนดการกระทำเฉพาะตัวของสัตว์เลี้ยงแต่ละสายพันธุ์
     * แต่ละคลาสจะ Override เมธอดนี้ให้มีพฤติกรรมที่ต่างกัน (Polymorphism)
     */
    void performAction();

    /**
     * ให้สัตว์เลี้ยงส่งเสียงร้องตามสายพันธุ์
     *
     * @return String เสียงร้องของสัตว์เลี้ยง
     */
    String makeSound();
}