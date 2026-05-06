package com.gachapet;

import com.gachapet.data.FileDataHandler;
import com.gachapet.data.UserInventory;
import com.gachapet.model.*;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.stage.Modality;
import javafx.stage.Stage;

public class KawaiiPetApp extends Application {

    private static UserInventory currentInventory;
    private static FileDataHandler dataHandler = new FileDataHandler();
    private AbstractPet selectedPet;

    // UI Components
    private ProgressBar hpBar, hungerBar, happyBar, energyBar;
    private Label hpLabel, hungerLabel, happyLabel, energyLabel, coinLabel, statusMsg, petNameLabel, petTypeLabel;
    private ListView<String> petListView;
    private ObservableList<String> petObservableList = FXCollections.observableArrayList();
    private ImageView petImageView;

    // Styles
    private final String ROOT_STYLE = "-fx-background-color: #fdf4ff; -fx-font-family: 'Segoe UI', sans-serif;";
    private final String PANEL_STYLE = "-fx-background-color: #fff0ff; -fx-background-radius: 12; -fx-border-color: #e8b4e8; -fx-border-width: 2; -fx-border-radius: 12;";
    private final String TITLE_STYLE = "-fx-text-fill: #7b3f7b; -fx-font-size: 15px; -fx-font-weight: bold;";
    private final String GACHA_BTN_STYLE = "-fx-background-color: linear-gradient(to bottom, #e8a0ff, #c060e0); -fx-text-fill: white; -fx-font-size: 14px; -fx-font-weight: bold; -fx-background-radius: 20; -fx-cursor: hand;";
    private final String ACTION_BTN_STYLE = "-fx-background-color: linear-gradient(to bottom, #ff9966, #ff6633); -fx-text-fill: white; -fx-font-weight: bold; -fx-background-radius: 15; -fx-cursor: hand;";

    @Override
    public void start(Stage primaryStage) {
        currentInventory = dataHandler.loadData();
        if (currentInventory == null) {
            currentInventory = new UserInventory("Player");
            currentInventory.addPet(new Cat("Mochi")); // แจกตัวแรกถ้าไม่มีเซฟ
        }

        BorderPane root = buildMainView();
        Scene scene = new Scene(root, 900, 650);

        // เลือกรุ่นแรกเริ่ม
        if (currentInventory.getPetCount() > 0) {
            selectedPet = currentInventory.getPet(0);
            updateUI();
        }

        // Thread นาฬิกาชีวิต[cite: 18, 24]
        Thread lifeThread = new Thread(() -> {
            while (true) {
                try {
                    Thread.sleep(1000); // เดินวินาทีละครั้งตามลอจิกเพื่อน
                    if (currentInventory != null) {
                        for (AbstractPet pet : currentInventory.getAllPets()) {
                            pet.tick(); // เรียกใช้ระบบ tick ของเพื่อน[cite: 18, 24]
                        }
                        Platform.runLater(this::updateUI);
                    }
                } catch (InterruptedException e) { break; }
            }
        });
        lifeThread.setDaemon(true);
        lifeThread.start();

        primaryStage.setTitle("🐾 Kawaii Gacha Pet Sanctuary");
        primaryStage.setScene(scene);
        primaryStage.setOnCloseRequest(e -> dataHandler.saveData(currentInventory)); // เซฟตอนปิด[cite: 16]
        primaryStage.show();
    }

    private BorderPane buildMainView() {
        BorderPane root = new BorderPane();
        root.setStyle(ROOT_STYLE);

        // MenuBar[cite: 4]
        MenuBar menuBar = new MenuBar();
        menuBar.setStyle("-fx-background-color: #e8b4e8;");
        Menu fileMenu = new Menu("File");
        MenuItem saveItem = new MenuItem("Save");
        saveItem.setOnAction(e -> dataHandler.saveData(currentInventory));
        MenuItem exitItem = new MenuItem("Exit");
        exitItem.setOnAction(e -> System.exit(0));
        fileMenu.getItems().addAll(saveItem, new SeparatorMenuItem(), exitItem);
        menuBar.getMenus().add(fileMenu);
        root.setTop(menuBar);

        TabPane tabPane = new TabPane();
        tabPane.setTabClosingPolicy(TabPane.TabClosingPolicy.UNAVAILABLE);

        // --- Tab: Sanctuary ---
        Tab sanctuaryTab = new Tab("🐾 Sanctuary");
        HBox mainContent = new HBox(15);
        mainContent.setPadding(new Insets(15));

        // Left Panel: Pet List[cite: 4, 17]
        VBox leftPanel = new VBox(10);
        leftPanel.setPrefWidth(250);
        leftPanel.setPadding(new Insets(10));
        leftPanel.setStyle(PANEL_STYLE);

        petListView = new ListView<>(petObservableList);
        petListView.getSelectionModel().selectedIndexProperty().addListener((obs, oldV, newV) -> {
            if (newV.intValue() >= 0) {
                selectedPet = currentInventory.getPet(newV.intValue());
                updateUI();
            }
        });

        Button openGachaBtn = new Button("🎰 Open Gacha!");
        openGachaBtn.setStyle(GACHA_BTN_STYLE);
        openGachaBtn.setMaxWidth(Double.MAX_VALUE);
        openGachaBtn.setOnAction(e -> openGachaWindow());

        leftPanel.getChildren().addAll(new Label("My Pets", new Label().getGraphic()), petListView, openGachaBtn);

        // Right Panel: Status & Actions[cite: 4, 18]
        VBox rightPanel = new VBox(15);
        rightPanel.setPadding(new Insets(15));
        rightPanel.setStyle(PANEL_STYLE);
        HBox.setHgrow(rightPanel, Priority.ALWAYS);

        // Header
        HBox header = new HBox(15);
        petImageView = new ImageView();
        petImageView.setFitWidth(80); petImageView.setFitHeight(80);
        VBox nameInfo = new VBox(5);
        petNameLabel = new Label("Select a Pet");
        petNameLabel.setStyle("-fx-font-size: 20px; -fx-font-weight: bold;");
        petTypeLabel = new Label("Type: -");
        nameInfo.getChildren().addAll(petNameLabel, petTypeLabel);
        header.getChildren().addAll(petImageView, nameInfo);

        // ProgressBars[cite: 18]
        VBox bars = new VBox(8);
        hpBar = new ProgressBar(); hpBar.setMaxWidth(Double.MAX_VALUE); hpBar.setStyle("-fx-accent: #ff4d4d;");
        hpLabel = new Label("HP: -");
        hungerBar = new ProgressBar(); hungerBar.setMaxWidth(Double.MAX_VALUE); hungerBar.setStyle("-fx-accent: #ffa64d;");
        hungerLabel = new Label("Hunger: -");
        happyBar = new ProgressBar(); happyBar.setMaxWidth(Double.MAX_VALUE); happyBar.setStyle("-fx-accent: #ffff4d;");
        happyLabel = new Label("Happiness: -");
        energyBar = new ProgressBar(); energyBar.setMaxWidth(Double.MAX_VALUE); energyBar.setStyle("-fx-accent: #4d94ff;");
        energyLabel = new Label("Energy: -");

        bars.getChildren().addAll(hpLabel, hpBar, hungerLabel, hungerBar, happyLabel, happyBar, energyLabel, energyBar);

        // Action Buttons[cite: 19]
        HBox actions = new HBox(10);
        Button feedBtn = new Button("🍖 Feed"); feedBtn.setOnAction(e -> handleAction("feed"));
        Button playBtn = new Button("🎮 Play"); playBtn.setOnAction(e -> handleAction("play"));
        Button sleepBtn = new Button("😴 Sleep/Wake"); sleepBtn.setOnAction(e -> handleAction("sleep"));
        Button skillBtn = new Button("✨ Special"); skillBtn.setOnAction(e -> handleAction("skill"));

        feedBtn.setStyle(ACTION_BTN_STYLE); playBtn.setStyle(ACTION_BTN_STYLE);
        sleepBtn.setStyle(ACTION_BTN_STYLE); skillBtn.setStyle(ACTION_BTN_STYLE);
        actions.getChildren().addAll(feedBtn, playBtn, sleepBtn, skillBtn);

        rightPanel.getChildren().addAll(header, new Separator(), bars, new Separator(), actions);
        mainContent.getChildren().addAll(leftPanel, rightPanel);
        sanctuaryTab.setContent(mainContent);

        tabPane.getTabs().add(sanctuaryTab);
        root.setCenter(tabPane);

        // Status Bar
        HBox statusBar = new HBox(20);
        statusBar.setPadding(new Insets(8));
        statusBar.setStyle("-fx-background-color: #f0d0f0;");
        coinLabel = new Label("Coins: 0"); coinLabel.setStyle("-fx-font-weight: bold;");
        statusMsg = new Label("Welcome!");
        statusBar.getChildren().addAll(coinLabel, new Separator(), statusMsg);
        root.setBottom(statusBar);

        return root;
    }

    private void handleAction(String action) {
        if (selectedPet == null || !selectedPet.isAlive()) return;

        switch (action) {
            case "feed":
                if (currentInventory.spendCoins(20)) { selectedPet.eat(20); statusMsg.setText("Fed " + selectedPet.getName()); }
                break;
            case "play":
                selectedPet.play(); statusMsg.setText("Played with " + selectedPet.getName() + " (Found some coins!)");
                currentInventory.addCoins(30);
                break;
            case "sleep":
                if (selectedPet.isSleeping()) selectedPet.wakeUp(); else selectedPet.sleep();
                break;
            case "skill":
                selectedPet.performAction(); statusMsg.setText("Used Skill: " + selectedPet.makeSound());
                break;
        }
        updateUI();
    }

    private void updateUI() {
        if (selectedPet != null) {
            hpBar.setProgress(selectedPet.getHp() / 100.0);
            hungerBar.setProgress(selectedPet.getHunger() / 100.0);
            happyBar.setProgress(selectedPet.getHappiness() / 100.0);
            energyBar.setProgress(selectedPet.getEnergy() / 100.0);

            hpLabel.setText("HP: " + selectedPet.getHp() + "/100");
            hungerLabel.setText("Hunger: " + selectedPet.getHunger() + "/100");
            happyLabel.setText("Happiness: " + selectedPet.getHappiness() + "/100");
            energyLabel.setText("Energy: " + selectedPet.getEnergy() + "/100");

            petNameLabel.setText(selectedPet.getEmoji() + " " + selectedPet.getName() + " (Lv." + selectedPet.getLevel() + ")");
            petTypeLabel.setText("Type: " + selectedPet.getPetType() + " | Status: " + selectedPet.getStatusText());

            if (selectedPet.isSleeping()) statusMsg.setText(selectedPet.getName() + " is sleeping...");
        }

        // อัปเดตรายชื่อด้านซ้าย
        petObservableList.clear();
        for (AbstractPet p : currentInventory.getAllPets()) {
            petObservableList.add(p.getEmoji() + " " + p.getName() + " (Lv." + p.getLevel() + ")");
        }
        coinLabel.setText("Coins: " + currentInventory.getCoins());
    }

    private void openGachaWindow() {
        Stage gachaStage = new Stage();
        gachaStage.initModality(Modality.APPLICATION_MODAL);
        VBox gachaBox = new VBox(20);
        gachaBox.setAlignment(Pos.CENTER);
        gachaBox.setPadding(new Insets(20));
        gachaBox.setStyle("-fx-background-color: #3a1060;");

        Label title = new Label("🎰 Gacha Banner"); title.setStyle("-fx-text-fill: #ffd700; -fx-font-size: 24px;");
        Button rollBtn = new Button("Roll 1x (100 Coins)");
        rollBtn.setStyle(GACHA_BTN_STYLE);

        Label resultLabel = new Label("Try your luck!"); resultLabel.setStyle("-fx-text-fill: white;");

        rollBtn.setOnAction(e -> {
            if (currentInventory.spendCoins(100)) {
                AbstractPet newPet = new GachaSystem().roll(); // ใช้ระบบสุ่มของเพื่อน[cite: 22]
                currentInventory.addPet(newPet);
                resultLabel.setText("Got: " + newPet.getEmoji() + " " + newPet.getName() + "!");
                updateUI();
            } else {
                resultLabel.setText("Not enough coins!");
            }
        });

        gachaBox.getChildren().addAll(title, rollBtn, resultLabel);
        gachaStage.setScene(new Scene(gachaBox, 400, 300));
        gachaStage.show();
    }

    public static void main(String[] args) { launch(args); }
}