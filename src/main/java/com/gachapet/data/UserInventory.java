package com.gachapet.data;

import com.gachapet.model.AbstractPet;

import java.util.ArrayList;
import java.util.List;

/**
 * Manages the player's pet collection and coins.
 *
 * * <p>This class uses {@code ArrayList<AbstractPet>} to support polymorphism.
 * It can store Cat, Dog, MythicPet, or any other subclass of AbstractPet
 * in the same list.</p>
 */
public class UserInventory {

    // ==================== Constants ====================

    /** The number of coins the player starts with. */
    public static final int STARTING_COINS = 500;

    /** The maximum number of pets the player can own. */
    public static final int MAX_PET_CAPACITY = 30;

    // ==================== Fields ====================

    /** The player's pet collection. */
    private final ArrayList<AbstractPet> pets;

    /** The player's current coin balance. */
    private int coins;

    /** The player's display name. */
    private String playerName;

    // ==================== Constructor ====================

    /**
     * Creates a new inventory for a player.
     *
     * @param playerName the player's name
     */
    public UserInventory(String playerName) {
        setPlayerName(playerName);
        this.pets = new ArrayList<>();
        this.coins = STARTING_COINS;
    }

    // ==================== Pet Management ====================

    /**
     * Adds a pet to the inventory.
     *
     * <p>Polymorphism is used here because the method accepts AbstractPet,
     * but the actual object can be Cat, Dog, MythicPet, or another subclass.</p>
     *
     * @param pet the pet to add
     * @return true if the pet was added successfully, false otherwise
     */
    public boolean addPet(AbstractPet pet) {
        if (pet == null) {
            System.out.println("Cannot add pet: pet is null.");
            return false;
        }

        if (pets.size() >= MAX_PET_CAPACITY) {
            System.out.println("Pet inventory is full! (" + pets.size() + "/" + MAX_PET_CAPACITY + ")");
            return false;
        }

        pets.add(pet);
        System.out.println("You received " + pet.getEmoji() + " " + pet.getName() + "!");
        return true;
    }

    /**
     * Removes a pet from the inventory by index.
     *
     * @param index the index of the pet
     * @return the removed pet, or null if the index is invalid
     */
    public AbstractPet removePet(int index) {
        if (!isValidIndex(index)) {
            System.out.println("Remove failed: invalid pet index " + index + ".");
            return null;
        }

        AbstractPet removedPet = pets.remove(index);
        System.out.println("Removed pet: " + removedPet.getName() + ".");
        return removedPet;
    }

    /**
     * Gets a pet by index.
     *
     * @param index the index of the pet
     * @return the pet at the given index, or null if the index is invalid
     */
    public AbstractPet getPet(int index) {
        if (!isValidIndex(index)) {
            return null;
        }

        return pets.get(index);
    }

    /**
     * Returns a copy of all pets to protect encapsulation.
     *
     * @return a copy of the pet list
     */
    public List<AbstractPet> getAllPets() {
        return new ArrayList<>(pets);
    }

    /**
     * Returns the number of pets in the inventory.
     *
     * @return the pet count
     */
    public int getPetCount() {
        return pets.size();
    }

    /**
     * Checks whether the inventory is full.
     *
     * @return true if the inventory is full
     */
    public boolean isFull() {
        return pets.size() >= MAX_PET_CAPACITY;
    }

    /**
     * Checks whether the inventory is empty.
     *
     * @return true if the inventory has no pets
     */
    public boolean isEmpty() {
        return pets.isEmpty();
    }

    /**
     * Checks whether an index points to an existing pet.
     *
     * @param index the index to check
     * @return true if the index is valid
     */
    public boolean isValidIndex(int index) {
        return index >= 0 && index < pets.size();
    }

    // ==================== Coin Management ====================

    /**
     * Gets the player's current coin balance.
     *
     * @return the current number of coins
     */
    public int getCoins() {
        return coins;
    }

    /**
     * Sets the player's coin balance.
     *
     * @param coins the new coin amount
     */
    public void setCoins(int coins) {
        this.coins = Math.max(0, coins);
    }

    /**
     * Adds coins to the player's balance.
     *
     * @param amount the amount to add
     */
    public void addCoins(int amount) {
        if (amount <= 0) {
            System.out.println("Add coins failed: amount must be greater than 0.");
            return;
        }

        this.coins += amount;
        System.out.println("Added " + amount + " coins. Current balance: " + coins + ".");
    }

    /**
     * Spends coins from the player's balance.
     *
     * @param amount the amount to spend
     * @return true if the player had enough coins and the coins were spent successfully
     */
    public boolean spendCoins(int amount) {
        if (amount <= 0) {
            System.out.println("Spend coins failed: amount must be greater than 0.");
            return false;
        }

        if (this.coins < amount) {
            System.out.println("Not enough coins! Current: " + coins + ", required: " + amount + ".");
            return false;
        }

        this.coins -= amount;
        System.out.println("Spent " + amount + " coins. Current balance: " + coins + ".");
        return true;
    }

    /**
     * Checks whether the player has enough coins.
     *
     * @param amount the required amount
     * @return true if the player has enough coins
     */
    public boolean hasEnoughCoins(int amount) {
        return amount >= 0 && this.coins >= amount;
    }

    // ==================== Player Name ====================

    /**
     * Gets the player's name.
     *
     * @return the player's name
     */
    public String getPlayerName() {
        return playerName;
    }

    /**
     * Sets the player's name.
     *
     * @param playerName the new player name
     */
    public void setPlayerName(String playerName) {
        if (playerName == null || playerName.trim().isEmpty()) {
            this.playerName = "Player";
        } else {
            this.playerName = playerName.trim();
        }
    }

    // ==================== Package Internal Access ====================

    /**
     * Returns the internal pet list.
     *
     * <p>This method is package-private and should only be used by classes
     * in the data package when direct access is necessary.</p>
     *
     * @return the internal ArrayList of pets
     */
    ArrayList<AbstractPet> getPetsInternal() {
        return pets;
    }

    // ==================== Summary ====================

    /**
     * Returns a short summary of the inventory.
     *
     * @return inventory summary
     */
    @Override
    public String toString() {
        return String.format(
                "Player: %s | Coins: %d | Pets: %d/%d",
                playerName,
                coins,
                pets.size(),
                MAX_PET_CAPACITY
        );
    }
}