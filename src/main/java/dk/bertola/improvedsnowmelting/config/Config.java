package dk.bertola.improvedsnowmelting.config;


import java.util.Arrays;
import java.util.List;
import java.util.Map;

public class Config {
    public Map<String, Integer> heatSources = Map.of(
            "minecraft:torch",2,
            "minecraft:wall_torch",2,
            "minecraft:lava",5,
            "minecraft:campfire",4,
            "minecraft:lantern",1
    );
    public int maxRadius = heatSources.values().stream().max(Integer::compareTo).orElse(0);
    /// Simple melting is the basic setting of just melting snow blocks and layered snow by light level.
    /// Default: True
    public boolean simpleMelting = false;
    public int simpleMeltingLightLevel = 11;

}
