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
import javafx.scene.media.AudioClip;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.stage.Modality;
import javafx.stage.Stage;

/**
 * คลาสหลักสำหรับรันแอปพลิเคชัน Kawaii Gacha Pet Sanctuary (UI และ Controller).
 * <p>
 * คลาสนี้สืบทอดจาก {@link Application} เพื่อสร้างหน้าต่าง JavaFX
 * ทำหน้าที่เชื่อมต่อการทำงานระหว่างส่วนข้อมูล (Model/Data) และส่วนแสดงผล (View)
 * รวมถึงจัดการ Thread นาฬิกาชีวิตของสัตว์เลี้ยงในเกม
 * </p>
 */

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
    private Image[] dogImages = new Image[3];
    private Image[] catImages = new Image[3];
    private Image mythicImage;
    private AudioClip dogBarkSound;
    private AudioClip catMeowSound;
    private AudioClip mythicHorseSound;
    private MediaPlayer bgmPlayer;

    // Styles
    private final String ROOT_STYLE = "-fx-background-color: #fdf4ff; -fx-font-family: 'Segoe UI', sans-serif;";
    private final String PANEL_STYLE = "-fx-background-color: #fff0ff; -fx-background-radius: 12; -fx-border-color: #e8b4e8; -fx-border-width: 2; -fx-border-radius: 12;";
    private final String TITLE_STYLE = "-fx-text-fill: #7b3f7b; -fx-font-size: 15px; -fx-font-weight: bold;";
    private final String GACHA_BTN_STYLE = "-fx-background-color: linear-gradient(to bottom, #e8a0ff, #c060e0); -fx-text-fill: white; -fx-font-size: 14px; -fx-font-weight: bold; -fx-background-radius: 20; -fx-cursor: hand;";
    private final String ACTION_BTN_STYLE = "-fx-background-color: linear-gradient(to bottom, #ff9966, #ff6633); -fx-text-fill: white; -fx-font-weight: bold; -fx-background-radius: 15; -fx-cursor: hand;";

    @Override
    public void start(Stage primaryStage) {
        loadAssets();
        playBackgroundMusic();

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
                            if (pet == selectedPet) {
                                pet.tick();
                            } else {
                                pet.tickInactive();
                            }
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
        primaryStage.setOnCloseRequest(e -> {
            if (bgmPlayer != null) {
                bgmPlayer.stop();
            }
            dataHandler.saveData(currentInventory);
        }); // เซฟตอนปิด[cite: 16]
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
        petImageView.setFitWidth(110); petImageView.setFitHeight(110);
        petImageView.setPreserveRatio(true);
        petImageView.setSmooth(true);
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
        Button renameBtn = new Button("Rename"); renameBtn.setOnAction(e -> renameSelectedPet());

        feedBtn.setStyle(ACTION_BTN_STYLE); playBtn.setStyle(ACTION_BTN_STYLE);
        sleepBtn.setStyle(ACTION_BTN_STYLE); skillBtn.setStyle(ACTION_BTN_STYLE); renameBtn.setStyle(ACTION_BTN_STYLE);
        actions.getChildren().addAll(feedBtn, playBtn, sleepBtn, skillBtn, renameBtn);

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
                playPetSound(selectedPet);
                break;
        }
        updateUI();
    }

    private void loadAssets() {
        var dogImageResource = getClass().getResource("/assets/images/dog.png");
        if (dogImageResource != null) {
            dogImages[0] = new Image(dogImageResource.toExternalForm());
        }

        for (int i = 1; i < dogImages.length; i++) {
            var dogVariantResource = getClass().getResource("/assets/images/dog-" + (i + 1) + ".png");
            if (dogVariantResource != null) {
                dogImages[i] = new Image(dogVariantResource.toExternalForm());
            }
        }

        var dogSoundResource = getClass().getResource("/assets/sounds/dog-bark.mp3");
        if (dogSoundResource != null) {
            dogBarkSound = new AudioClip(dogSoundResource.toExternalForm());
            dogBarkSound.setVolume(0.75);
        }

        for (int i = 0; i < catImages.length; i++) {
            var catImageResource = getClass().getResource("/assets/images/cat-" + (i + 1) + ".png");
            if (catImageResource != null) {
                catImages[i] = new Image(catImageResource.toExternalForm());
            }
        }

        var catSoundResource = getClass().getResource("/assets/sounds/cat-meow.mp3");
        if (catSoundResource != null) {
            catMeowSound = new AudioClip(catSoundResource.toExternalForm());
            catMeowSound.setVolume(0.75);
        }

        var mythicImageResource = getClass().getResource("/assets/images/mythic.png");
        if (mythicImageResource != null) {
            mythicImage = new Image(mythicImageResource.toExternalForm());
        }

        var mythicSoundResource = getClass().getResource("/assets/sounds/mythic-horse.mp3");
        if (mythicSoundResource != null) {
            mythicHorseSound = new AudioClip(mythicSoundResource.toExternalForm());
            mythicHorseSound.setVolume(0.75);
        }

        var bgmResource = getClass().getResource("/assets/music/bgm-love.mp3");
        if (bgmResource != null) {
            bgmPlayer = new MediaPlayer(new Media(bgmResource.toExternalForm()));
            bgmPlayer.setCycleCount(MediaPlayer.INDEFINITE);
            bgmPlayer.setVolume(0.18);
        }
    }

    private void playBackgroundMusic() {
        if (bgmPlayer != null) {
            bgmPlayer.play();
        }
    }

    private void playPetSound(AbstractPet pet) {
        if (pet != null && "DOG".equals(pet.getPetType()) && dogBarkSound != null) {
            dogBarkSound.stop();
            dogBarkSound.play();
        } else if (pet != null && "CAT".equals(pet.getPetType()) && catMeowSound != null) {
            catMeowSound.stop();
            catMeowSound.play();
        } else if (pet != null && "MYTHIC".equals(pet.getPetType()) && mythicHorseSound != null) {
            mythicHorseSound.stop();
            mythicHorseSound.play();
        }
    }

    private void renameSelectedPet() {
        if (selectedPet == null) return;

        TextInputDialog dialog = new TextInputDialog(selectedPet.getName());
        dialog.setTitle("Rename Pet");
        dialog.setHeaderText("Enter an English name");
        dialog.setContentText("Name:");

        dialog.showAndWait().ifPresent(newName -> {
            try {
                selectedPet.setName(newName);
                statusMsg.setText("Renamed pet to " + selectedPet.getName());
                updateUI();
            } catch (IllegalArgumentException ex) {
                statusMsg.setText("Pet name must use English letters and spaces only.");
            }
        });
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
            petImageView.setImage(getPetImage(selectedPet));

            if (selectedPet.isSleeping()) statusMsg.setText(selectedPet.getName() + " is sleeping...");
        }

        // อัปเดตรายชื่อด้านซ้าย
        petObservableList.clear();
        for (AbstractPet p : currentInventory.getAllPets()) {
            petObservableList.add(p.getEmoji() + " " + p.getName() + " (Lv." + p.getLevel() + ")");
        }
        coinLabel.setText("Coins: " + currentInventory.getCoins());

        String careAlert = getCareAlertText();
        if (!careAlert.isEmpty()) {
            statusMsg.setText(careAlert);
        }
    }

    private String getCareAlertText() {
        for (AbstractPet pet : currentInventory.getAllPets()) {
            if (pet.needsCareAlert()) {
                return pet.getCareAlertText();
            }
        }

        return "";
    }

    private Image getPetImage(AbstractPet pet) {
        if (pet == null) {
            return null;
        }

        if ("DOG".equals(pet.getPetType())) {
            int imageIndex = pet.getImageVariant() % dogImages.length;
            return dogImages[imageIndex];
        }

        if ("CAT".equals(pet.getPetType())) {
            int imageIndex = pet.getImageVariant() % catImages.length;
            return catImages[imageIndex];
        }

        if ("MYTHIC".equals(pet.getPetType())) {
            return mythicImage;
        }

        return null;
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
