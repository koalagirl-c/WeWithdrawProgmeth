package com.gachapet.controller;

import com.gachapet.data.UserInventory;
import com.gachapet.model.AbstractPet;
import com.gachapet.model.GachaSystem;
import com.gachapet.model.MythicPet;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import javafx.util.Duration;

import java.net.URL;
import java.util.ResourceBundle;

/**
 * Controller สำหรับหน้าจอตู้กาชา (gacha_view.fxml)
 * จัดการการสุ่มสัตว์เลี้ยงและแสดงผลลัพธ์
 */
public class GachaController implements Initializable {

    // ==================== FXML Components ====================

    @FXML private Label coinLabel;
    @FXML private Label resultLabel;
    @FXML private Label resultDetailLabel;
    @FXML private Label dropRateLabel;
    @FXML private Button rollButton;
    @FXML private Button rollTenButton;
    @FXML private CheckBox soundCheckBox;
    @FXML private VBox resultContainer;

    // ==================== Fields ====================

    /** ระบบกาชาสำหรับสุ่มสัตว์เลี้ยง */
    private GachaSystem gachaSystem;

    /** Inventory ของผู้เล่น */
    private UserInventory inventory;

    /** Reference กลับไปยัง MainController เพื่อ refresh UI */
    private MainController mainController;

    // ==================== Initialize ====================

    /**
     * เมธอดเริ่มต้น เรียกอัตโนมัติหลัง FXML โหลดเสร็จ
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        gachaSystem = new GachaSystem();

        // แสดง Drop Rate
        if (dropRateLabel != null) {
            dropRateLabel.setText(
                    String.format("🐱 Cat: %.0f%%  |  🐶 Dog: %.0f%%  |  🦄 Mythic: %.0f%%",
                            GachaSystem.CAT_RATE * 100,
                            GachaSystem.DOG_RATE * 100,
                            GachaSystem.MYTHIC_RATE * 100)
            );
        }

        if (soundCheckBox != null) {
            soundCheckBox.setSelected(true);
        }
    }

    // ==================== Setter ====================

    /**
     * รับ Inventory และ Reference ไปยัง MainController
     *
     * @param inventory UserInventory ของผู้เล่น
     * @param mainController MainController สำหรับ refresh UI
     */
    public void setInventory(UserInventory inventory, MainController mainController) {
        this.inventory = inventory;
        this.mainController = mainController;
        refreshCoinLabel();
    }

    // ==================== FXML Action Methods ====================

    /**
     * ปุ่ม Roll 1 ครั้ง: สุ่มสัตว์เลี้ยง 1 ตัว
     */
    @FXML
    private void onRollButtonClick() {
        if (!checkCoins(GachaSystem.GACHA_COST)) return;

        inventory.spendCoins(GachaSystem.GACHA_COST);
        AbstractPet result = gachaSystem.roll();
        inventory.addPet(result);

        showSingleResult(result);
        refreshCoinLabel();
        if (mainController != null) mainController.refreshUI();
    }

    /**
     * ปุ่ม Roll 10 ครั้ง: สุ่มสัตว์เลี้ยง 10 ตัว (พร้อม Pity System)
     */
    @FXML
    private void onRollTenButtonClick() {
        int totalCost = GachaSystem.GACHA_COST * 10;
        if (!checkCoins(totalCost)) return;

        inventory.spendCoins(totalCost);
        AbstractPet[] results = gachaSystem.rollTen();

        // เพิ่มสัตว์เลี้ยงทั้ง 10 ตัวเข้า Inventory
        int added = 0;
        for (AbstractPet pet : results) {
            if (inventory.addPet(pet)) added++;
        }

        showTenPullResults(results, added);
        refreshCoinLabel();
        if (mainController != null) mainController.refreshUI();
    }

    // ==================== Private Helper Methods ====================

    /**
     * ตรวจสอบว่ามีเหรียญเพียงพอหรือไม่
     *
     * @param amount จำนวนเหรียญที่ต้องการ
     * @return true ถ้ามีเพียงพอ
     */
    private boolean checkCoins(int amount) {
        if (inventory == null || !inventory.hasEnoughCoins(amount)) {
            if (resultLabel != null) {
                resultLabel.setText("❌ เหรียญไม่พอ!");
                resultLabel.setStyle("-fx-text-fill: #e74c3c;");
            }
            if (resultDetailLabel != null) {
                resultDetailLabel.setText("ต้องการ " + amount + " เหรียญ มีอยู่ " +
                        (inventory != null ? inventory.getCoins() : 0) + " เหรียญ");
            }
            return false;
        }
        return true;
    }

    /**
     * แสดงผลลัพธ์การ Roll 1 ครั้ง พร้อม Animation
     *
     * @param pet สัตว์เลี้ยงที่ได้รับ
     */
    private void showSingleResult(AbstractPet pet) {
        if (resultLabel == null) return;

        boolean isMythic = pet instanceof MythicPet;

        // ตั้งค่าสีและข้อความตาม Rarity
        if (isMythic) {
            resultLabel.setText("🌟 SSR! ได้รับสัตว์ในตำนาน! 🌟");
            resultLabel.setStyle("-fx-text-fill: #f39c12; -fx-font-size: 18px; -fx-font-weight: bold;");
        } else {
            resultLabel.setText("✅ ได้รับสัตว์เลี้ยงใหม่!");
            resultLabel.setStyle("-fx-text-fill: #2ecc71; -fx-font-size: 16px;");
        }

        if (resultDetailLabel != null) {
            resultDetailLabel.setText(pet.toString());
        }

        // Animation กระพริบสำหรับ MythicPet
        if (isMythic) {
            playMythicAnimation();
        }
    }

    /**
     * แสดงผลลัพธ์การ Roll 10 ครั้ง
     *
     * @param results สัตว์เลี้ยงทั้ง 10 ตัว
     * @param addedCount จำนวนที่เพิ่มเข้า Inventory ได้จริง
     */
    private void showTenPullResults(AbstractPet[] results, int addedCount) {
        if (resultLabel == null) return;

        StringBuilder sb = new StringBuilder();
        int mythicCount = 0;

        for (AbstractPet pet : results) {
            sb.append(pet.getEmoji()).append(" ").append(pet.getName());
            if (pet instanceof MythicPet) {
                sb.append(" ⭐SSR");
                mythicCount++;
            }
            sb.append("\n");
        }

        if (mythicCount > 0) {
            resultLabel.setText("🌟 x10 Pull! ได้ SSR " + mythicCount + " ตัว! 🌟");
            resultLabel.setStyle("-fx-text-fill: #f39c12; -fx-font-weight: bold;");
        } else {
            resultLabel.setText("🎰 x10 Pull เสร็จแล้ว! (เพิ่มได้ " + addedCount + " ตัว)");
            resultLabel.setStyle("-fx-text-fill: #2ecc71;");
        }

        if (resultDetailLabel != null) {
            resultDetailLabel.setText(sb.toString());
        }
    }

    /**
     * เล่น Animation กระพริบสำหรับ MythicPet
     */
    private void playMythicAnimation() {
        if (resultLabel == null) return;

        Timeline timeline = new Timeline(
                new KeyFrame(Duration.millis(0),   e -> resultLabel.setOpacity(1.0)),
                new KeyFrame(Duration.millis(200), e -> resultLabel.setOpacity(0.3)),
                new KeyFrame(Duration.millis(400), e -> resultLabel.setOpacity(1.0)),
                new KeyFrame(Duration.millis(600), e -> resultLabel.setOpacity(0.3)),
                new KeyFrame(Duration.millis(800), e -> resultLabel.setOpacity(1.0))
        );
        timeline.setCycleCount(3);
        timeline.play();
    }

    /**
     * อัปเดต Label แสดงจำนวนเหรียญ
     */
    private void refreshCoinLabel() {
        if (coinLabel != null && inventory != null) {
            coinLabel.setText("💰 " + inventory.getCoins() + " เหรียญ");
        }
    }
}