package dk.bertola.meltingsnowblocks;


import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import net.fabricmc.loader.api.FabricLoader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.util.Arrays;
import java.util.List;

import static dk.bertola.meltingsnowblocks.MeltingSnowBlocks.LOGGER;


public class Config {
    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();
    private final File file;
    public List<String> heatSources = Arrays.asList(
            "minecraft:torch",
            "minecraft:wall_torch",
            "minecraft:lava",
            "minecraft:campfire",
            "minecraft:lantern"
    );
    public int meltRadius = 3;

    public Config(File file) {
        this.file = file;
    }

    public static Config createAndLoad() {
        File configFile = new File(FabricLoader.getInstance().getConfigDir().toFile(), "ImprovedSnowMelting.json");

        // Create config instance first
        Config config = new Config(configFile);

        // Then load data if file exists
        if (configFile.exists() && configFile.length() > 0) {
            try (FileReader reader = new FileReader(configFile)) {
                Config loaded = GSON.fromJson(reader, Config.class);
                // Copy the values from loaded config to our instance
                if (loaded.heatSources != null) {
                    config.heatSources = loaded.heatSources;
                }
                config.meltRadius = loaded.meltRadius;
                LOGGER.info("Config loaded successfully");
            } catch (Exception e) {
                LOGGER.warn("Failed to load config, using defaults: {}", e.getMessage());
                // Save defaults if loading fails
                config.save();
            }
        } else {
            // File doesn't exist or is empty, save defaults
            LOGGER.info("Creating new config file with defaults");
            config.save();
        }
        LOGGER.info("Finished Config Setup, with values heatSources: " + config.heatSources);
        return config;
    }

    public void save() {
        try (FileWriter writer = new FileWriter(file)) {
            GSON.toJson(this, writer);
        } catch (Exception e) {
            LOGGER.error(e.toString());
        }
    }
}
