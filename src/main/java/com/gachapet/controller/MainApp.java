package com.gachapet.controller;

import com.gachapet.data.FileDataHandler;
import com.gachapet.data.UserInventory;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

/**
 * จุดเริ่มต้นของโปรแกรม Kawaii Gacha Pet Sanctuary
 * extends javafx.application.Application ตาม JavaFX Standard
 *
 * <p>หน้าที่ของ MainApp:</p>
 * <ul>
 *   <li>โหลดหน้าจอหลัก (main_view.fxml)</li>
 *   <li>สร้าง UserInventory และส่งต่อให้ Controller</li>
 *   <li>ตั้งค่า Window ขนาดและชื่อโปรแกรม</li>
 * </ul>
 */
public class MainApp extends Application {

    /** ขนาดหน้าต่างโปรแกรม */
    private static final double WINDOW_WIDTH = 900;
    private static final double WINDOW_HEIGHT = 650;

    /** ชื่อโปรแกรม */
    private static final String APP_TITLE = "🐾 Kawaii Gacha Pet Sanctuary";

    /** Singleton: UserInventory ที่ใช้ร่วมกันทั้งโปรแกรม */
    private static UserInventory currentInventory;

    /** FileDataHandler สำหรับ Save/Load */
    private static FileDataHandler dataHandler = new FileDataHandler();

    /**
     * เมธอดหลักของ JavaFX - เรียกโดย JavaFX Runtime
     * โหลด FXML และแสดงหน้าต่างหลัก
     *
     * @param primaryStage Stage หลักของโปรแกรม
     */
    @Override
    public void start(Stage primaryStage) {
        // พยายาม Load ข้อมูล Save ที่มีอยู่
        currentInventory = dataHandler.loadData();
        if (currentInventory == null) {
            // ถ้าไม่มี Save ให้สร้างใหม่
            currentInventory = new UserInventory("ผู้เล่น");
        }

        try {
            // โหลด FXML
            FXMLLoader loader = new FXMLLoader(
                    getClass().getResource("/fxml/main_view.fxml")
            );
            Parent root = loader.load();

            // ส่ง Inventory ให้ MainController
            MainController controller = loader.getController();
            controller.setInventory(currentInventory);

            // ตั้งค่า Scene
            Scene scene = new Scene(root, WINDOW_WIDTH, WINDOW_HEIGHT);

            // โหลด CSS
            String cssPath = getClass().getResource("/css/kawaii.css") != null
                    ? getClass().getResource("/css/kawaii.css").toExternalForm()
                    : null;
            if (cssPath != null) {
                scene.getStylesheets().add(cssPath);
            }

            // ตั้งค่า Stage
            primaryStage.setTitle(APP_TITLE);
            primaryStage.setScene(scene);
            primaryStage.setMinWidth(700);
            primaryStage.setMinHeight(500);

            // บันทึกข้อมูลเมื่อปิดโปรแกรม
            primaryStage.setOnCloseRequest(event -> {
                dataHandler.saveData(currentInventory);
                System.out.println("👋 บันทึกข้อมูลและออกจากโปรแกรม");
            });

            primaryStage.show();

        } catch (IOException e) {
            System.err.println("❌ โหลดหน้าจอไม่สำเร็จ: " + e.getMessage());
            e.printStackTrace();
        }
    }

    // ==================== Static Accessors ====================

    /**
     * ดึง UserInventory ที่ใช้ร่วมกันทั้งโปรแกรม (Singleton Pattern)
     *
     * @return UserInventory ปัจจุบัน
     */
    public static UserInventory getCurrentInventory() {
        return currentInventory;
    }

    /**
     * ดึง FileDataHandler
     *
     * @return FileDataHandler
     */
    public static FileDataHandler getDataHandler() {
        return dataHandler;
    }

    /**
     * Main method - จุดเริ่มต้นของ Java Program
     *
     * @param args Command line arguments
     */
    public static void main(String[] args) {
        launch(args);
    }
}