package com.gachapet.controller;

import com.gachapet.data.FileDataHandler;
import com.gachapet.data.UserInventory;
import com.gachapet.model.AbstractPet;
import com.gachapet.model.GachaSystem;
import com.gachapet.model.MythicPet;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.util.Duration;

/**
 * Main application class for Kawaii Gacha Pet Sanctuary.
 * This version uses JavaFX code only.
 * No FXML and no external CSS files are required.
 */
public class MainApp extends Application {

    private static final double WINDOW_WIDTH = 900;
    private static final double WINDOW_HEIGHT = 650;
    private static final String APP_TITLE = "Kawaii Gacha Pet Sanctuary";

    private static UserInventory currentInventory;
    private static final FileDataHandler dataHandler = new FileDataHandler();

    private final GachaSystem gachaSystem = new GachaSystem();

    private AbstractPet selectedPet;

    private Label coinsLabel;
    private Label petNameLabel;
    private Label petTypeLabel;
    private Label petStatusLabel;
    private Label resultLabel;
    private Label resultDetailLabel;
    private Label petCountLabel;

    private ProgressBar hpBar;
    private ProgressBar hungerBar;
    private ProgressBar happinessBar;
    private ProgressBar energyBar;

    private Button feedButton;
    private Button playButton;
    private Button sleepButton;
    private Button wakeButton;
    private Button specialButton;
    private Button rollButton;
    private Button rollTenButton;
    private Button saveButton;

    private Timeline gameLoop;

    @Override
    public void start(Stage primaryStage) {
        loadOrCreateInventory();

        BorderPane root = new BorderPane();
        root.setPadding(new Insets(20));
        root.setStyle("-fx-background-color: #fff7f0;");

        Label titleLabel = new Label("🐾 Kawaii Gacha Pet Sanctuary");
        titleLabel.setStyle("-fx-font-size: 28px; -fx-font-weight: bold;");

        coinsLabel = new Label();
        coinsLabel.setStyle("-fx-font-size: 16px; -fx-font-weight: bold;");

        petCountLabel = new Label();
        petCountLabel.setStyle("-fx-font-size: 14px;");

        HBox topBar = new HBox(20, titleLabel, coinsLabel, petCountLabel);
        topBar.setAlignment(Pos.CENTER_LEFT);
        topBar.setPadding(new Insets(0, 0, 20, 0));

        root.setTop(topBar);
        root.setCenter(createPetPanel());
        root.setRight(createGachaPanel());
        root.setBottom(createBottomPanel());

        Scene scene = new Scene(root, WINDOW_WIDTH, WINDOW_HEIGHT);

        primaryStage.setTitle(APP_TITLE);
        primaryStage.setScene(scene);
        primaryStage.setMinWidth(750);
        primaryStage.setMinHeight(550);

        primaryStage.setOnCloseRequest(event -> {
            saveGame();
            stopGameLoop();
            System.out.println("Game saved successfully. Exiting application.");
        });

        selectFirstPetIfAvailable();
        refreshUI();
        startGameLoop();

        primaryStage.show();
    }

    private VBox createPetPanel() {
        petNameLabel = new Label("No pet selected");
        petNameLabel.setStyle("-fx-font-size: 24px; -fx-font-weight: bold;");

        petTypeLabel = new Label("Type: -");
        petTypeLabel.setStyle("-fx-font-size: 16px;");

        petStatusLabel = new Label("Status: -");
        petStatusLabel.setStyle("-fx-font-size: 16px;");

        hpBar = createProgressBar();
        hungerBar = createProgressBar();
        happinessBar = createProgressBar();
        energyBar = createProgressBar();

        GridPane statGrid = new GridPane();
        statGrid.setHgap(10);
        statGrid.setVgap(12);
        statGrid.setPadding(new Insets(20));

        statGrid.add(new Label("HP"), 0, 0);
        statGrid.add(hpBar, 1, 0);

        statGrid.add(new Label("Hunger"), 0, 1);
        statGrid.add(hungerBar, 1, 1);

        statGrid.add(new Label("Happiness"), 0, 2);
        statGrid.add(happinessBar, 1, 2);

        statGrid.add(new Label("Energy"), 0, 3);
        statGrid.add(energyBar, 1, 3);

        feedButton = new Button("Feed");
        playButton = new Button("Play");
        sleepButton = new Button("Sleep");
        wakeButton = new Button("Wake Up");
        specialButton = new Button("Special Skill");

        feedButton.setOnAction(e -> {
            if (selectedPet != null) {
                selectedPet.eat(25);
                resultLabel.setText(selectedPet.getName() + " enjoyed the food!");
                refreshUI();
            }
        });

        playButton.setOnAction(e -> {
            if (selectedPet != null) {
                selectedPet.play();
                resultLabel.setText(selectedPet.getName() + " had fun playing!");
                refreshUI();
            }
        });

        sleepButton.setOnAction(e -> {
            if (selectedPet != null) {
                selectedPet.sleep();
                resultLabel.setText(selectedPet.getName() + " is sleeping.");
                refreshUI();
            }
        });

        wakeButton.setOnAction(e -> {
            if (selectedPet != null) {
                selectedPet.wakeUp();
                resultLabel.setText(selectedPet.getName() + " woke up.");
                refreshUI();
            }
        });

        specialButton.setOnAction(e -> {
            if (selectedPet != null) {
                selectedPet.performAction();
                resultLabel.setText(selectedPet.getSpecialSkill());
                refreshUI();
            }
        });

        HBox actionRow1 = new HBox(10, feedButton, playButton, sleepButton);
        actionRow1.setAlignment(Pos.CENTER);

        HBox actionRow2 = new HBox(10, wakeButton, specialButton);
        actionRow2.setAlignment(Pos.CENTER);

        VBox petPanel = new VBox(15);
        petPanel.setAlignment(Pos.TOP_CENTER);
        petPanel.setPadding(new Insets(20));
        petPanel.setStyle("-fx-background-color: white; -fx-border-color: #e0c7b7; -fx-border-radius: 12; -fx-background-radius: 12;");
        petPanel.getChildren().addAll(
                petNameLabel,
                petTypeLabel,
                petStatusLabel,
                statGrid,
                actionRow1,
                actionRow2
        );

        return petPanel;
    }

    private VBox createGachaPanel() {
        Label gachaTitle = new Label("🎰 Gacha Machine");
        gachaTitle.setStyle("-fx-font-size: 22px; -fx-font-weight: bold;");

        Label dropRateLabel = new Label(
                String.format(
                        "Drop Rates\nCat: %.0f%%\nDog: %.0f%%\nMythic: %.0f%%",
                        GachaSystem.CAT_RATE * 100,
                        GachaSystem.DOG_RATE * 100,
                        GachaSystem.MYTHIC_RATE * 100
                )
        );

        rollButton = new Button("Roll x1 (" + GachaSystem.GACHA_COST + " coins)");
        rollTenButton = new Button("Roll x10 (" + (GachaSystem.GACHA_COST * 10) + " coins)");
        CheckBox soundCheckBox = new CheckBox("Sound");
        soundCheckBox.setSelected(true);

        resultLabel = new Label("Ready to roll!");
        resultLabel.setStyle("-fx-font-size: 16px; -fx-font-weight: bold;");

        resultDetailLabel = new Label("Spend coins to receive a random pet.");
        resultDetailLabel.setWrapText(true);
        resultDetailLabel.setMaxWidth(260);

        rollButton.setOnAction(e -> rollOnce());
        rollTenButton.setOnAction(e -> rollTen());

        VBox gachaPanel = new VBox(15);
        gachaPanel.setPadding(new Insets(20));
        gachaPanel.setPrefWidth(300);
        gachaPanel.setStyle("-fx-background-color: #fffdf7; -fx-border-color: #e0c7b7; -fx-border-radius: 12; -fx-background-radius: 12;");
        gachaPanel.getChildren().addAll(
                gachaTitle,
                dropRateLabel,
                rollButton,
                rollTenButton,
                soundCheckBox,
                resultLabel,
                resultDetailLabel
        );

        return gachaPanel;
    }

    private HBox createBottomPanel() {
        saveButton = new Button("Save Game");
        Button selectNextButton = new Button("Next Pet");
        Button addCoinsButton = new Button("Add 100 Coins");

        saveButton.setOnAction(e -> {
            saveGame();
            resultLabel.setText("Game saved successfully.");
        });

        selectNextButton.setOnAction(e -> {
            selectNextPet();
            refreshUI();
        });

        addCoinsButton.setOnAction(e -> {
            currentInventory.addCoins(100);
            resultLabel.setText("Added 100 coins.");
            refreshUI();
        });

        HBox bottomPanel = new HBox(10, saveButton, selectNextButton, addCoinsButton);
        bottomPanel.setAlignment(Pos.CENTER);
        bottomPanel.setPadding(new Insets(20, 0, 0, 0));

        return bottomPanel;
    }

    private ProgressBar createProgressBar() {
        ProgressBar bar = new ProgressBar(1.0);
        bar.setPrefWidth(300);
        return bar;
    }

    private void rollOnce() {
        if (!currentInventory.hasEnoughCoins(GachaSystem.GACHA_COST)) {
            resultLabel.setText("Not enough coins!");
            resultDetailLabel.setText("Required: " + GachaSystem.GACHA_COST + " coins.");
            return;
        }

        currentInventory.spendCoins(GachaSystem.GACHA_COST);

        AbstractPet pet = gachaSystem.roll();
        boolean added = currentInventory.addPet(pet);

        if (added) {
            selectedPet = pet;

            if (pet instanceof MythicPet) {
                resultLabel.setText("SSR Pull! You got a Mythic Pet!");
                playMythicAnimation();
            } else {
                resultLabel.setText("New pet acquired!");
            }

            resultDetailLabel.setText(formatPet(pet));
        } else {
            resultLabel.setText("Inventory is full!");
            resultDetailLabel.setText("The new pet could not be added.");
        }

        refreshUI();
    }

    private void rollTen() {
        int cost = GachaSystem.GACHA_COST * 10;

        if (!currentInventory.hasEnoughCoins(cost)) {
            resultLabel.setText("Not enough coins!");
            resultDetailLabel.setText("Required: " + cost + " coins.");
            return;
        }

        currentInventory.spendCoins(cost);

        AbstractPet[] results = gachaSystem.rollTen();

        StringBuilder details = new StringBuilder();
        int addedCount = 0;
        int mythicCount = 0;

        for (int i = 0; i < results.length; i++) {
            AbstractPet pet = results[i];

            if (currentInventory.addPet(pet)) {
                addedCount++;
                selectedPet = pet;
            }

            if (pet instanceof MythicPet) {
                mythicCount++;
            }

            details.append(i + 1)
                    .append(". ")
                    .append(formatPet(pet))
                    .append("\n");
        }

        resultLabel.setText("x10 Pull Complete! Mythic Pets: " + mythicCount);
        resultDetailLabel.setText(details + "\nAdded to inventory: " + addedCount + "/" + results.length);

        if (mythicCount > 0) {
            playMythicAnimation();
        }

        refreshUI();
    }

    private String formatPet(AbstractPet pet) {
        String rarity = pet instanceof MythicPet ? "SSR" : "Normal";

        return String.format(
                "%s %s | Type: %s | Rarity: %s | Skill: %s",
                pet.getEmoji(),
                pet.getName(),
                pet.getPetType(),
                rarity,
                pet.getSpecialSkill()
        );
    }

    private void selectFirstPetIfAvailable() {
        if (currentInventory.getPetCount() > 0) {
            selectedPet = currentInventory.getPet(0);
        }
    }

    private void selectNextPet() {
        if (currentInventory.getPetCount() == 0) {
            selectedPet = null;
            resultLabel.setText("You do not have any pets yet.");
            return;
        }

        if (selectedPet == null) {
            selectedPet = currentInventory.getPet(0);
            return;
        }

        int currentIndex = -1;

        for (int i = 0; i < currentInventory.getPetCount(); i++) {
            if (currentInventory.getPet(i) == selectedPet) {
                currentIndex = i;
                break;
            }
        }

        int nextIndex = (currentIndex + 1) % currentInventory.getPetCount();
        selectedPet = currentInventory.getPet(nextIndex);

        resultLabel.setText("Selected pet: " + selectedPet.getName());
    }

    private void refreshUI() {
        coinsLabel.setText("Coins: " + currentInventory.getCoins());
        petCountLabel.setText("Pets: " + currentInventory.getPetCount() + "/" + UserInventory.MAX_PET_CAPACITY);

        if (selectedPet == null) {
            petNameLabel.setText("No pet selected");
            petTypeLabel.setText("Type: -");
            petStatusLabel.setText("Status: Roll gacha to get your first pet.");

            hpBar.setProgress(0);
            hungerBar.setProgress(0);
            happinessBar.setProgress(0);
            energyBar.setProgress(0);

            setActionButtonsDisabled(true);
        } else {
            petNameLabel.setText(selectedPet.getEmoji() + " " + selectedPet.getName());
            petTypeLabel.setText("Type: " + selectedPet.getPetType() + " | Level: " + selectedPet.getLevel());
            petStatusLabel.setText("Status: " + selectedPet.getStatusText());

            hpBar.setProgress(selectedPet.getHp() / 100.0);
            hungerBar.setProgress(selectedPet.getHunger() / 100.0);
            happinessBar.setProgress(selectedPet.getHappiness() / 100.0);
            energyBar.setProgress(selectedPet.getEnergy() / 100.0);

            setActionButtonsDisabled(!selectedPet.isAlive());
        }

        rollButton.setDisable(!currentInventory.hasEnoughCoins(GachaSystem.GACHA_COST));
        rollTenButton.setDisable(!currentInventory.hasEnoughCoins(GachaSystem.GACHA_COST * 10));
    }

    private void setActionButtonsDisabled(boolean disabled) {
        feedButton.setDisable(disabled);
        playButton.setDisable(disabled);
        sleepButton.setDisable(disabled);
        wakeButton.setDisable(disabled);
        specialButton.setDisable(disabled);
    }

    private void startGameLoop() {
        gameLoop = new Timeline(new KeyFrame(Duration.seconds(1), event -> {
            if (selectedPet != null && selectedPet.isAlive()) {
                selectedPet.tick();
                refreshUI();
            }
        }));

        gameLoop.setCycleCount(Timeline.INDEFINITE);
        gameLoop.play();
    }

    private void stopGameLoop() {
        if (gameLoop != null) {
            gameLoop.stop();
        }
    }

    private void saveGame() {
        dataHandler.saveData(currentInventory);
    }

    private void loadOrCreateInventory() {
        currentInventory = dataHandler.loadData();

        if (currentInventory == null) {
            currentInventory = new UserInventory("Player");
            System.out.println("No save file found. Created a new player profile.");
        } else {
            System.out.println("Save data loaded successfully.");
        }
    }

    private void playMythicAnimation() {
        Timeline timeline = new Timeline(
                new KeyFrame(Duration.millis(0), e -> resultLabel.setOpacity(1.0)),
                new KeyFrame(Duration.millis(200), e -> resultLabel.setOpacity(0.3)),
                new KeyFrame(Duration.millis(400), e -> resultLabel.setOpacity(1.0)),
                new KeyFrame(Duration.millis(600), e -> resultLabel.setOpacity(0.3)),
                new KeyFrame(Duration.millis(800), e -> resultLabel.setOpacity(1.0))
        );

        timeline.setCycleCount(3);
        timeline.play();
    }

    public static UserInventory getCurrentInventory() {
        return currentInventory;
    }

    public static FileDataHandler getDataHandler() {
        return dataHandler;
    }

    public static void main(String[] args) {
        launch(args);
    }
}