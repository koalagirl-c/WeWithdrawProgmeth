package com.gachapet.controller;

import com.gachapet.data.FileDataHandler;
import com.gachapet.data.UserInventory;
import com.gachapet.model.AbstractPet;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.ImageView;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

/**
 * Controller สำหรับหน้าจอหลัก (main_view.fxml)
 * เชื่อมโยงระหว่าง UI (FXML) กับ Logic (Model)
 *
 * <p>ใช้ Pattern: MVC (Model-View-Controller)</p>
 * <ul>
 *   <li>Model: AbstractPet, UserInventory</li>
 *   <li>View: main_view.fxml</li>
 *   <li>Controller: คลาสนี้</li>
 * </ul>
 */
public class MainController implements Initializable {

    // ==================== FXML Components (ผูกกับ FXML ด้วย @FXML) ====================

    @FXML private MenuBar menuBar;
    @FXML private TabPane mainTabPane;
    @FXML private ListView<String> petListView;
    @FXML private Label selectedPetNameLabel;
    @FXML private Label selectedPetTypeLabel;
    @FXML private Label coinLabel;
    @FXML private ProgressBar hpProgressBar;
    @FXML private ProgressBar hungerProgressBar;
    @FXML private Label hpLabel;
    @FXML private Label hungerLabel;
    @FXML private ImageView petImageView;
    @FXML private Label statusMessageLabel;
    @FXML private Label levelLabel;
    @FXML private ComboBox<String> foodComboBox;
    @FXML private CheckBox soundCheckBox;
    @FXML private Slider bgmSlider;

    // ==================== Fields ====================

    /** UserInventory ที่รับมาจาก MainApp */
    private UserInventory inventory;

    /** FileDataHandler สำหรับ Save/Load */
    private FileDataHandler dataHandler;

    /** Observable List สำหรับ ListView */
    private ObservableList<String> petObservableList;

    /** สัตว์เลี้ยงที่กำลังเลือกอยู่ */
    private AbstractPet selectedPet;

    // ==================== Initialize ====================

    /**
     * เมธอดเริ่มต้น เรียกอัตโนมัติหลัง FXML โหลดเสร็จ
     *
     * @param url URL ของ FXML
     * @param rb ResourceBundle
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        dataHandler = new FileDataHandler();
        petObservableList = FXCollections.observableArrayList();

        if (petListView != null) {
            petListView.setItems(petObservableList);

            // เมื่อเลือกสัตว์เลี้ยงใน ListView
            petListView.getSelectionModel().selectedIndexProperty().addListener(
                    (obs, oldVal, newVal) -> {
                        if (newVal.intValue() >= 0 && inventory != null) {
                            selectedPet = inventory.getPet(newVal.intValue());
                            updatePetStatusPanel();
                        }
                    }
            );
        }

        // ตั้งค่า ComboBox อาหาร
        if (foodComboBox != null) {
            foodComboBox.setItems(FXCollections.observableArrayList(
                    "🍖 อาหารเม็ด (+20 Hunger)",
                    "🍣 อาหารพิเศษ (+35 Hunger)",
                    "🎂 ขนมหวาน (+15 Hunger +HP)"
            ));
            foodComboBox.getSelectionModel().selectFirst();
        }

        // ตั้งค่า BGM Slider
        if (bgmSlider != null) {
            bgmSlider.setValue(50);
        }

        setStatusMessage("ยินดีต้อนรับสู่ Kawaii Gacha Pet Sanctuary! 🐾");
    }

    // ==================== Setter (รับข้อมูลจาก MainApp) ====================

    /**
     * รับ UserInventory จาก MainApp และอัปเดต UI
     *
     * @param inventory UserInventory ของผู้เล่น
     */
    public void setInventory(UserInventory inventory) {
        this.inventory = inventory;
        refreshUI();
    }

    // ==================== FXML Action Methods ====================

    /**
     * ปุ่มให้อาหาร: เพิ่มค่าความหิวของสัตว์เลี้ยงที่เลือก
     */
    @FXML
    private void onFeedButtonClick() {
        if (selectedPet == null) {
            setStatusMessage("⚠️ กรุณาเลือกสัตว์เลี้ยงก่อน!");
            return;
        }

        // ดูว่าเลือกอาหารอะไร
        int foodIndex = foodComboBox != null
                ? foodComboBox.getSelectionModel().getSelectedIndex() : 0;

        switch (foodIndex) {
            case 0: selectedPet.eat(20); break;
            case 1: selectedPet.eat(35); break;
            case 2:
                selectedPet.eat(15);
                selectedPet.setHp(selectedPet.getHp() + 10);
                break;
            default: selectedPet.eat(20);
        }

        updatePetStatusPanel();
        refreshCoinLabel();
        setStatusMessage("😋 " + selectedPet.getName() + " กินอาหารแล้ว!");
    }

    /**
     * ปุ่มเล่นกับสัตว์เลี้ยง
     */
    @FXML
    private void onPlayButtonClick() {
        if (selectedPet == null) {
            setStatusMessage("⚠️ กรุณาเลือกสัตว์เลี้ยงก่อน!");
            return;
        }
        selectedPet.play();
        updatePetStatusPanel();
        setStatusMessage("🎮 " + selectedPet.getName() + " เล่นสนุกมาก!");
    }

    /**
     * ปุ่มใช้ Action พิเศษของสัตว์เลี้ยง
     */
    @FXML
    private void onActionButtonClick() {
        if (selectedPet == null) {
            setStatusMessage("⚠️ กรุณาเลือกสัตว์เลี้ยงก่อน!");
            return;
        }
        selectedPet.performAction();
        updatePetStatusPanel();
        setStatusMessage("✨ " + selectedPet.getName() + " ใช้ Action พิเศษ!");
    }

    /**
     * เปิดหน้าจอตู้กาชา
     */
    @FXML
    private void onOpenGachaButtonClick() {
        try {
            FXMLLoader loader = new FXMLLoader(
                    getClass().getResource("/fxml/gacha_view.fxml")
            );
            Parent root = loader.load();

            // ส่ง Inventory ให้ GachaController
            GachaController gachaCtrl = loader.getController();
            gachaCtrl.setInventory(inventory, this);

            Stage gachaStage = new Stage();
            gachaStage.setTitle("🎰 ตู้กาชา - Kawaii Gacha");
            gachaStage.initModality(Modality.APPLICATION_MODAL);
            gachaStage.setScene(new Scene(root, 600, 500));
            gachaStage.showAndWait();

            // หลังจากปิดหน้า Gacha ให้ refresh UI
            refreshUI();

        } catch (IOException e) {
            setStatusMessage("❌ เปิดหน้า Gacha ไม่ได้: " + e.getMessage());
            e.printStackTrace();
        }
    }

    // ==================== Menu Actions ====================

    /** เมนู File -> Save */
    @FXML
    private void onMenuSave() {
        if (inventory != null && dataHandler.saveData(inventory)) {
            setStatusMessage("💾 บันทึกข้อมูลสำเร็จ!");
        } else {
            setStatusMessage("❌ บันทึกข้อมูลไม่สำเร็จ");
        }
    }

    /** เมนู File -> Exit */
    @FXML
    private void onMenuExit() {
        if (inventory != null) dataHandler.saveData(inventory);
        System.exit(0);
    }

    /** เมนู Help -> How to Play */
    @FXML
    private void onMenuHowToPlay() {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("วิธีเล่น");
        alert.setHeaderText("🐾 Kawaii Gacha Pet Sanctuary");
        alert.setContentText(
                "1. กด 'เปิดตู้กาชา' เพื่อสุ่มสัตว์เลี้ยง (ราคา 100 เหรียญ)\n" +
                        "2. เลือกสัตว์เลี้ยงจากรายการทางซ้าย\n" +
                        "3. กด 'ให้อาหาร' เพื่อเพิ่มค่าความหิว\n" +
                        "4. กด 'เล่น' เพื่อเพิ่ม EXP และ Level Up\n" +
                        "5. อย่าปล่อยให้ Hunger หรือ HP ลดถึง 0!\n" +
                        "\n🦄 MythicPet มีโอกาสออก 10% เท่านั้น!"
        );
        alert.showAndWait();
    }

    // ==================== UI Update Methods ====================

    /**
     * Refresh UI ทั้งหมด (เรียกหลังมีการเปลี่ยนแปลงข้อมูล)
     */
    public void refreshUI() {
        refreshPetList();
        refreshCoinLabel();
    }

    /**
     * อัปเดตรายการสัตว์เลี้ยงใน ListView
     */
    private void refreshPetList() {
        if (inventory == null || petObservableList == null) return;

        petObservableList.clear();
        // ใช้ Polymorphism: toString() ของแต่ละ Object จะแสดงผลต่างกัน
        for (AbstractPet pet : inventory.getAllPets()) {
            petObservableList.add(pet.toString());
        }
    }

    /**
     * อัปเดต Label แสดงจำนวนเหรียญ
     */
    private void refreshCoinLabel() {
        if (inventory != null && coinLabel != null) {
            coinLabel.setText("💰 " + inventory.getCoins() + " เหรียญ");
        }
    }

    /**
     * อัปเดต Panel แสดงสถานะสัตว์เลี้ยงที่เลือก
     */
    private void updatePetStatusPanel() {
        if (selectedPet == null) return;

        if (selectedPetNameLabel != null)
            selectedPetNameLabel.setText(selectedPet.getEmoji() + " " + selectedPet.getName());

        if (selectedPetTypeLabel != null)
            selectedPetTypeLabel.setText("ชนิด: " + selectedPet.getPetType());

        if (levelLabel != null)
            levelLabel.setText("Lv." + selectedPet.getLevel());

        // อัปเดต ProgressBar (0.0 - 1.0)
        if (hpProgressBar != null)
            hpProgressBar.setProgress(selectedPet.getHp() / (double) AbstractPet.MAX_HP);

        if (hungerProgressBar != null)
            hungerProgressBar.setProgress(selectedPet.getHunger() / (double) AbstractPet.MAX_HUNGER);

        // อัปเดต Label ตัวเลข
        if (hpLabel != null)
            hpLabel.setText("HP: " + selectedPet.getHp() + "/" + AbstractPet.MAX_HP);

        if (hungerLabel != null)
            hungerLabel.setText("Hunger: " + selectedPet.getHunger() + "/" + AbstractPet.MAX_HUNGER);

        // อัปเดต ListView ด้วย (ค่าอาจเปลี่ยน)
        refreshPetList();
    }

    /**
     * แสดงข้อความสถานะด้านล่าง
     *
     * @param message ข้อความที่ต้องการแสดง
     */
    public void setStatusMessage(String message) {
        if (statusMessageLabel != null) {
            statusMessageLabel.setText(message);
        }
    }
}