package Codes;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.nio.file.Files;
import java.nio.file.Paths;

// Handles all file reading and writing
public class SaveManager {
    private static final String SAVE_DIR = "Saves";
    private static final String SAVE_FILE = SAVE_DIR + File.separator + "Savegame.dat";

    // Checks if a save file exists
    public static boolean hasSave() {
        return Files.exists(Paths.get(SAVE_FILE));
    }

    // Writes SaveData to disk
    public static boolean save(SaveData data) {
        try {
            // Create saves/directory if missing
            Files.createDirectories(Paths.get(SAVE_DIR));
            
            // Serialize and write
            try (ObjectOutputStream out = new ObjectOutputStream(new FileOutputStream(SAVE_FILE))) {
                out.writeObject(data);
            }

            System.out.println("[SaveManager] Game saved: " + data);
            return true;
        } catch (IOException e) {
            System.err.println("[SaveManager] Save failed: " + e.getMessage());
            return false;
        }
    }

    // Read SaveData from disk
    public static SaveData load() {
        if (!hasSave()) {
            System.out.println("[SaveManager] No save file found.");
            return null;
        }

        try (ObjectInputStream in = new ObjectInputStream(new FileInputStream(SAVE_FILE))) {
            SaveData data = (SaveData) in.readObject();
            System.out.println("[SaveManager] Game loaded: " + data);
            return data;
        } catch (IOException e) {
            System.err.println("[SaveManager] Load failed (corrupt file): " + e.getMessage());
            return null;
        } catch (ClassNotFoundException e) {
            System.err.println("[SaveManager] Load failed (incompatible save): " + e.getMessage());
            return null;
        }
    }

    // Erase save file
    public static boolean deleteSave() {
        try {
            boolean deleted = Files.deleteIfExists(Paths.get(SAVE_FILE));
            if (deleted) {
                System.out.println("[SaveManager] Save file deleted.");
            }
            return true;
        } catch (IOException e) {
            System.err.println("[SaveManager] Delete failed: " + e.getMessage());
            return false;
        }
    }
}