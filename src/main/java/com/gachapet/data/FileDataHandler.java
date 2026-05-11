package com.gachapet.data;

import com.gachapet.model.AbstractPet;
import com.gachapet.model.Cat;
import com.gachapet.model.Dog;
import com.gachapet.model.MythicPet;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

/**
 * Handles saving and loading UserInventory data to a text file.
 *
 * <p>Save file format:</p>
 * <pre>
 * PLAYER_NAME:PlayerName
 * COINS:500
 * PET:CAT,Luna,100,90,80,70,1,20,false
 * PET:DOG,Buddy,95,85,90,60,2,40,false
 * </pre>
 */
public class FileDataHandler {

    private static final String DEFAULT_SAVE_FILE = "save_data.txt";

    private static final String PREFIX_PLAYER = "PLAYER_NAME:";
    private static final String PREFIX_COINS = "COINS:";
    private static final String PREFIX_PET = "PET:";

    private final String filePath;

    public FileDataHandler() {
        this.filePath = DEFAULT_SAVE_FILE;
    }

    public FileDataHandler(String filePath) {
        this.filePath = filePath;
    }

    /**
     * Saves the current user inventory to a text file.
     *
     * @param inventory inventory to save
     * @return true if saved successfully, false otherwise
     */
    public boolean saveData(UserInventory inventory) {
        if (inventory == null) {
            System.err.println("Save failed: inventory is null.");
            return false;
        }

        try (PrintWriter writer = new PrintWriter(new FileWriter(filePath))) {
            writer.println(PREFIX_PLAYER + inventory.getPlayerName());
            writer.println(PREFIX_COINS + inventory.getCoins());

            for (AbstractPet pet : inventory.getAllPets()) {
                writer.println(PREFIX_PET + pet.toCsvString());
            }

            System.out.println("Game saved successfully -> " + filePath);
            return true;

        } catch (IOException e) {
            System.err.println("Save failed: " + e.getMessage());
            return false;
        }
    }

    /**
     * Loads user inventory data from a text file.
     *
     * @return loaded UserInventory, or null if no save file exists or loading fails
     */
    public UserInventory loadData() {
        Path path = Paths.get(filePath);

        if (!Files.exists(path)) {
            System.out.println("No save file found: " + filePath);
            return null;
        }

        try (BufferedReader reader = new BufferedReader(new FileReader(filePath))) {
            String playerName = "Player";
            int coins = UserInventory.STARTING_COINS;
            UserInventory inventory = null;

            String line;

            while ((line = reader.readLine()) != null) {
                line = line.trim();

                if (line.isEmpty()) {
                    continue;
                }

                if (line.startsWith(PREFIX_PLAYER)) {
                    playerName = line.substring(PREFIX_PLAYER.length()).trim();
                    inventory = new UserInventory(playerName);

                } else if (line.startsWith(PREFIX_COINS)) {
                    coins = Integer.parseInt(line.substring(PREFIX_COINS.length()).trim());

                    if (inventory == null) {
                        inventory = new UserInventory(playerName);
                    }

                    setInventoryCoins(inventory, coins);

                } else if (line.startsWith(PREFIX_PET)) {
                    if (inventory == null) {
                        inventory = new UserInventory(playerName);
                    }

                    AbstractPet pet = parsePetFromCsv(line.substring(PREFIX_PET.length()));

                    if (pet != null) {
                        inventory.addPet(pet);
                    }
                }
            }

            if (inventory != null) {
                System.out.println("Game loaded successfully: " + inventory);
            }

            return inventory;

        } catch (IOException | NumberFormatException e) {
            System.err.println("Load failed: " + e.getMessage());
            return null;
        }
    }

    /**
     * Deletes the save file.
     *
     * @return true if the file was deleted or did not exist
     */
    public boolean deleteSaveFile() {
        try {
            boolean deleted = Files.deleteIfExists(Paths.get(filePath));

            if (deleted) {
                System.out.println("Save file deleted successfully.");
            } else {
                System.out.println("No save file to delete.");
            }

            return true;

        } catch (IOException e) {
            System.err.println("Delete save file failed: " + e.getMessage());
            return false;
        }
    }

    /**
     * Checks whether a save file exists.
     *
     * @return true if a save file exists
     */
    public boolean hasSaveFile() {
        return Files.exists(Paths.get(filePath));
    }

    /**
     * Converts a CSV line into an AbstractPet object.
     *
     * <p>Supported format:</p>
     * <pre>
     * TYPE,NAME,HP,HUNGER,HAPPINESS,ENERGY,LEVEL,EXP,SLEEPING
     * </pre>
     *
     * <p>Also supports old format:</p>
     * <pre>
     * TYPE,NAME,HP,HUNGER,LEVEL,EXP
     * </pre>
     *
     * @param csvLine pet data line
     * @return created AbstractPet object, or null if parsing fails
     */
    private AbstractPet parsePetFromCsv(String csvLine) {
        try {
            String[] parts = csvLine.split(",");

            if (parts.length < 6) {
                System.err.println("Invalid pet data: " + csvLine);
                return null;
            }

            String petType = parts[0].trim();
            String name = parts[1].trim();

            AbstractPet pet = createPetByType(petType, name);

            if (pet == null) {
                System.err.println("Unknown pet type: " + petType);
                return null;
            }

            int hp = Integer.parseInt(parts[2].trim());
            int hunger = Integer.parseInt(parts[3].trim());

            pet.setHp(hp);
            pet.setHunger(hunger);

            if (parts.length >= 9) {
                int happiness = Integer.parseInt(parts[4].trim());
                int energy = Integer.parseInt(parts[5].trim());
                int level = Integer.parseInt(parts[6].trim());
                int experience = Integer.parseInt(parts[7].trim());
                boolean sleeping = Boolean.parseBoolean(parts[8].trim());

                pet.setHappiness(happiness);
                pet.setEnergy(energy);
                pet.setLevel(level);
                pet.setExperience(experience);
                pet.setSleeping(sleeping);

                if (parts.length >= 10) {
                    int imageVariant = Integer.parseInt(parts[9].trim());
                    pet.setImageVariant(imageVariant);
                }

            } else {
                int level = Integer.parseInt(parts[4].trim());
                int experience = Integer.parseInt(parts[5].trim());
                pet.setLevel(level);
                pet.setExperience(experience);
            }

            return pet;

        } catch (NumberFormatException e) {
            System.err.println("Failed to parse pet data: " + csvLine);
            return null;
        }
    }

    /**
     * Factory method for creating pets by type.
     */
    private AbstractPet createPetByType(String petType, String name) {
        return switch (petType) {
            case "CAT" -> new Cat(name);
            case "DOG" -> new Dog(name);
            case "MYTHIC" -> new MythicPet(name);
            default -> null;
        };
    }

    /**
     * Updates inventory coins based on available UserInventory methods.
     *
     * <p>This version assumes UserInventory starts with STARTING_COINS
     * and has addCoins(). If your UserInventory has setCoins(), use that instead.</p>
     */
    private void setInventoryCoins(UserInventory inventory, int targetCoins) {
        inventory.setCoins(targetCoins);
    }

    public String getFilePath() {
        return filePath;
    }
}
