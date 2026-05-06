package com.gachapet.data;

import com.gachapet.model.AbstractPet;
import com.gachapet.model.Cat;
import com.gachapet.model.Dog;
import com.gachapet.model.MythicPet;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;

/**
 * คลาสสำหรับบันทึกและโหลดข้อมูล UserInventory ลงไฟล์ .txt
 * ใช้ File I/O พื้นฐาน (BufferedReader / PrintWriter)
 *
 * <p>รูปแบบไฟล์ save.txt:</p>
 * <pre>
 * PLAYER_NAME:ชื่อผู้เล่น
 * COINS:จำนวนเหรียญ
 * PET:ชนิด,ชื่อ,hp,hunger,level,exp
 * PET:ชนิด,ชื่อ,hp,hunger,level,exp
 * ...
 * </pre>
 */
public class FileDataHandler {

    /** ชื่อไฟล์ Save เริ่มต้น */
    private static final String DEFAULT_SAVE_FILE = "save_data.txt";

    /** Prefix สำหรับบรรทัด Player Name */
    private static final String PREFIX_PLAYER = "PLAYER_NAME:";

    /** Prefix สำหรับบรรทัด Coins */
    private static final String PREFIX_COINS = "COINS:";

    /** Prefix สำหรับบรรทัดข้อมูลสัตว์เลี้ยง */
    private static final String PREFIX_PET = "PET:";

    /** Path ของไฟล์ที่ใช้งาน */
    private final String filePath;

    // ==================== Constructors ====================

    /**
     * สร้าง FileDataHandler ด้วยชื่อไฟล์ Default
     */
    public FileDataHandler() {
        this.filePath = DEFAULT_SAVE_FILE;
    }

    /**
     * สร้าง FileDataHandler ด้วย Path ที่กำหนดเอง
     *
     * @param filePath Path ของไฟล์ที่ต้องการใช้
     */
    public FileDataHandler(String filePath) {
        this.filePath = filePath;
    }

    // ==================== Save / Load Methods ====================

    /**
     * บันทึกข้อมูล UserInventory ลงไฟล์ .txt
     * ใช้ PrintWriter สำหรับเขียนข้อมูลแบบ Text
     *
     * @param inventory UserInventory ที่ต้องการบันทึก
     * @return true ถ้าบันทึกสำเร็จ, false ถ้าเกิด Error
     */
    public boolean saveData(UserInventory inventory) {
        // ใช้ try-with-resources เพื่อให้ FileWriter ถูกปิดอัตโนมัติ
        try (PrintWriter writer = new PrintWriter(new FileWriter(filePath))) {

            // เขียน Header ข้อมูลผู้เล่น
            writer.println(PREFIX_PLAYER + inventory.getPlayerName());
            writer.println(PREFIX_COINS + inventory.getCoins());

            // วนเขียนข้อมูลสัตว์เลี้ยงแต่ละตัว (ใช้ Polymorphism: toCsvString() จาก AbstractPet)
            for (AbstractPet pet : inventory.getAllPets()) {
                writer.println(PREFIX_PET + pet.toCsvString());
            }

            System.out.println("✅ บันทึกข้อมูลสำเร็จ -> " + filePath);
            return true;

        } catch (IOException e) {
            System.err.println("❌ บันทึกข้อมูลไม่สำเร็จ: " + e.getMessage());
            return false;
        }
    }

    /**
     * โหลดข้อมูลจากไฟล์ .txt และสร้าง UserInventory กลับมา
     * ใช้ BufferedReader สำหรับอ่านข้อมูลแบบ Text
     *
     * @return UserInventory ที่โหลดมา, หรือ null ถ้าไม่พบไฟล์หรือเกิด Error
     */
    public UserInventory loadData() {
        // ตรวจสอบว่าไฟล์มีอยู่จริงหรือไม่
        if (!Files.exists(Paths.get(filePath))) {
            System.out.println("⚠️ ไม่พบไฟล์ save: " + filePath);
            return null;
        }

        // ใช้ try-with-resources เพื่อให้ BufferedReader ถูกปิดอัตโนมัติ
        try (BufferedReader reader = new BufferedReader(new FileReader(filePath))) {

            String playerName = "ผู้เล่น";
            int coins = UserInventory.STARTING_COINS;
            UserInventory inventory = null;

            String line;
            while ((line = reader.readLine()) != null) {
                line = line.trim();
                if (line.isEmpty()) continue; // ข้ามบรรทัดว่าง

                if (line.startsWith(PREFIX_PLAYER)) {
                    playerName = line.substring(PREFIX_PLAYER.length());
                    inventory = new UserInventory(playerName);

                } else if (line.startsWith(PREFIX_COINS) && inventory != null) {
                    coins = Integer.parseInt(line.substring(PREFIX_COINS.length()));
                    // Reset coins แล้วตั้งค่าใหม่
                    int diff = coins - inventory.getCoins();
                    if (diff > 0) inventory.addCoins(diff);

                } else if (line.startsWith(PREFIX_PET) && inventory != null) {
                    // แปลงข้อมูล CSV กลับเป็น Object
                    AbstractPet pet = parsePetFromCsv(line.substring(PREFIX_PET.length()));
                    if (pet != null) {
                        inventory.addPet(pet);
                    }
                }
            }

            if (inventory != null) {
                System.out.println("✅ โหลดข้อมูลสำเร็จ: " + inventory);
            }
            return inventory;

        } catch (IOException | NumberFormatException e) {
            System.err.println("❌ โหลดข้อมูลไม่สำเร็จ: " + e.getMessage());
            return null;
        }
    }

    /**
     * ลบไฟล์ Save
     *
     * @return true ถ้าลบสำเร็จ
     */
    public boolean deleteSaveFile() {
        try {
            return Files.deleteIfExists(Paths.get(filePath));
        } catch (IOException e) {
            System.err.println("❌ ลบไฟล์ไม่สำเร็จ: " + e.getMessage());
            return false;
        }
    }

    /**
     * ตรวจสอบว่าไฟล์ save มีอยู่หรือไม่
     *
     * @return true ถ้ามีไฟล์ save อยู่
     */
    public boolean hasSaveFile() {
        return Files.exists(Paths.get(filePath));
    }

    // ==================== Private Helper Methods ====================

    /**
     * แปลงข้อมูล CSV String กลับเป็น AbstractPet Object
     * ใช้ Factory Pattern โดยดูจาก petType
     *
     * @param csvLine ข้อมูลในรูปแบบ "ชนิด,ชื่อ,hp,hunger,level,exp"
     * @return AbstractPet Object, หรือ null ถ้า parse ไม่สำเร็จ
     */
    private AbstractPet parsePetFromCsv(String csvLine) {
        try {
            String[] parts = csvLine.split(",");
            if (parts.length < 6) return null;

            String petType = parts[0].trim();
            String name    = parts[1].trim();
            int hp         = Integer.parseInt(parts[2].trim());
            int hunger     = Integer.parseInt(parts[3].trim());
            int level      = Integer.parseInt(parts[4].trim());
            int experience = Integer.parseInt(parts[5].trim());

            // สร้าง Object ตามชนิดสัตว์เลี้ยง (Factory Pattern)
            AbstractPet pet;
            switch (petType) {
                case "CAT":    pet = new Cat(name);       break;
                case "DOG":    pet = new Dog(name);       break;
                case "MYTHIC": pet = new MythicPet(name); break;
                default:
                    System.err.println("⚠️ ชนิดสัตว์เลี้ยงไม่รู้จัก: " + petType);
                    return null;
            }

            // คืนค่า State กลับไป
            pet.setHp(hp);
            pet.setHunger(hunger);
            pet.setLevel(level);

            return pet;

        } catch (NumberFormatException e) {
            System.err.println("❌ Parse CSV ไม่สำเร็จ: " + csvLine);
            return null;
        }
    }

    /**
     * ดึง Path ของไฟล์ที่ใช้งาน
     *
     * @return file path
     */
    public String getFilePath() {
        return filePath;
    }
}