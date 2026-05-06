package com.gachapet.data;

import com.gachapet.model.AbstractPet;
import java.util.ArrayList;
import java.util.List;

/**
 * คลาสจัดการกระเป๋าสัตว์เลี้ยงและทรัพยากรของผู้เล่น
 * เก็บรายการสัตว์เลี้ยงทั้งหมดและจำนวนเหรียญ
 *
 * <p>ใช้ ArrayList ในการเก็บ AbstractPet ทำให้ใช้ Polymorphism ได้เต็มที่:
 * เก็บ Cat, Dog, และ MythicPet ใน List เดียวกันได้</p>
 */
public class UserInventory {

    // ==================== Constants ====================

    /** เหรียญเริ่มต้นที่ผู้เล่นได้รับ */
    public static final int STARTING_COINS = 500;

    /** จำนวนสัตว์เลี้ยงสูงสุดที่เก็บได้ */
    public static final int MAX_PET_CAPACITY = 30;

    // ==================== Fields ====================

    /** รายการสัตว์เลี้ยงทั้งหมด (ใช้ ArrayList) */
    private ArrayList<AbstractPet> pets;

    /** จำนวนเหรียญของผู้เล่น */
    private int coins;

    /** ชื่อของผู้เล่น */
    private String playerName;

    // ==================== Constructor ====================

    /**
     * สร้าง UserInventory ใหม่สำหรับผู้เล่น
     *
     * @param playerName ชื่อของผู้เล่น
     */
    public UserInventory(String playerName) {
        this.playerName = playerName;
        this.pets = new ArrayList<>();
        this.coins = STARTING_COINS;
    }

    // ==================== Pet Management ====================

    /**
     * เพิ่มสัตว์เลี้ยงเข้ากระเป๋า
     * ใช้ Polymorphism: รับ AbstractPet แต่ Object จริงอาจเป็น Cat, Dog หรือ MythicPet
     *
     * @param pet สัตว์เลี้ยงที่ต้องการเพิ่ม
     * @return true ถ้าเพิ่มสำเร็จ, false ถ้ากระเป๋าเต็ม
     */
    public boolean addPet(AbstractPet pet) {
        if (pets.size() >= MAX_PET_CAPACITY) {
            System.out.println("❌ กระเป๋าเต็มแล้ว! (" + MAX_PET_CAPACITY + "/" + MAX_PET_CAPACITY + ")");
            return false;
        }
        if (pet == null) {
            return false;
        }
        pets.add(pet);
        System.out.println("✅ ได้รับ " + pet.getEmoji() + " " + pet.getName() + " แล้ว!");
        return true;
    }

    /**
     * ลบสัตว์เลี้ยงออกจากกระเป๋าตาม Index
     *
     * @param index ตำแหน่งของสัตว์เลี้ยงใน List
     * @return AbstractPet ที่ถูกลบออก, หรือ null ถ้า Index ไม่ถูกต้อง
     */
    public AbstractPet removePet(int index) {
        if (index < 0 || index >= pets.size()) {
            return null;
        }
        return pets.remove(index);
    }

    /**
     * ดึงสัตว์เลี้ยงตาม Index
     *
     * @param index ตำแหน่งใน List
     * @return AbstractPet ที่ตำแหน่งนั้น, หรือ null ถ้า Index ไม่ถูกต้อง
     */
    public AbstractPet getPet(int index) {
        if (index < 0 || index >= pets.size()) {
            return null;
        }
        return pets.get(index);
    }

    /**
     * ดึง List สัตว์เลี้ยงทั้งหมด (คืนเป็น copy เพื่อป้องกัน Encapsulation leak)
     *
     * @return List ของ AbstractPet ทั้งหมด
     */
    public List<AbstractPet> getAllPets() {
        return new ArrayList<>(pets); // คืน copy ป้องกันการแก้ไขโดยตรง
    }

    /**
     * ดึงจำนวนสัตว์เลี้ยงทั้งหมดในกระเป๋า
     *
     * @return จำนวนสัตว์เลี้ยง
     */
    public int getPetCount() {
        return pets.size();
    }

    // ==================== Coin Management ====================

    /**
     * ดึงจำนวนเหรียญปัจจุบัน
     *
     * @return จำนวนเหรียญ
     */
    public int getCoins() {
        return coins;
    }

    /**
     * เพิ่มเหรียญ
     *
     * @param amount จำนวนเหรียญที่ต้องการเพิ่ม (ต้องมากกว่า 0)
     */
    public void addCoins(int amount) {
        if (amount > 0) {
            this.coins += amount;
        }
    }

    /**
     * ลดเหรียญ (ใช้เงิน)
     *
     * @param amount จำนวนเหรียญที่ต้องการใช้
     * @return true ถ้ามีเงินพอและหักสำเร็จ, false ถ้าเงินไม่พอ
     */
    public boolean spendCoins(int amount) {
        if (amount <= 0) return false;
        if (this.coins < amount) {
            System.out.println("❌ เหรียญไม่พอ! มี " + coins + " ต้องการ " + amount);
            return false;
        }
        this.coins -= amount;
        return true;
    }

    /**
     * ตรวจสอบว่ามีเหรียญเพียงพอหรือไม่
     *
     * @param amount จำนวนที่ต้องการตรวจสอบ
     * @return true ถ้ามีเหรียญเพียงพอ
     */
    public boolean hasEnoughCoins(int amount) {
        return this.coins >= amount;
    }

    // ==================== Getters ====================

    /**
     * ดึงชื่อผู้เล่น
     *
     * @return ชื่อผู้เล่น
     */
    public String getPlayerName() {
        return playerName;
    }

    /**
     * ตั้งชื่อผู้เล่น
     *
     * @param playerName ชื่อใหม่
     */
    public void setPlayerName(String playerName) {
        this.playerName = playerName;
    }

    /**
     * ดึง Reference ของ ArrayList โดยตรง (สำหรับใช้ภายใน package เท่านั้น)
     *
     * @return ArrayList ของ AbstractPet
     */
    ArrayList<AbstractPet> getPetsInternal() {
        return pets;
    }

    /**
     * แสดงสรุปข้อมูลกระเป๋า
     *
     * @return String สรุปข้อมูล
     */
    @Override
    public String toString() {
        return String.format("👤 %s | 💰 %d เหรียญ | 🐾 สัตว์เลี้ยง %d/%d ตัว",
                playerName, coins, pets.size(), MAX_PET_CAPACITY);
    }
}