package dk.bertola.improvedsnowmelting;

import dk.bertola.improvedsnowmelting.config.Config;
import dk.bertola.improvedsnowmelting.config.ConfigManager;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.SnowBlock;
import net.minecraft.registry.Registries;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.LightType;
import net.minecraft.world.World;

public class SnowMeltManager {


    public static boolean checkAndMeltSnow(World world, BlockPos snowPos) {
        if (hasHeatSourceNearby(world, snowPos)) {
            ImprovedSnowMelting.LOGGER.debug("Heat source found near snow at {}", snowPos);
            meltSnowBlock(world, snowPos);
            return true;
        }
        return false;
    }

    private static boolean hasHeatSourceNearby(World world, BlockPos pos) {
        int radius = ConfigManager.getConfig().maxRadius;
        int diameter = radius * 2 + 1;
        for (int z = 1; z >= -radius; z--) {
            int x = 0, y = 0;
            int dx = 0, dy = -1;
            for (int i = 0; i < Math.pow(diameter - Math.abs(z) * 2, 2); i++) {
                if (Math.abs(x) <= radius && Math.abs(y) <= radius) {
                    BlockPos checkPos = pos.add(x, z, y); // x y z in this method is based on mathematical concepts not minecraft coordinates. z and y are swapped.
                    BlockState state = world.getBlockState(checkPos);
                    if (isHeatSource(state)) {
                        String blockId = Registries.BLOCK.getId(state.getBlock()).toString();
                        ImprovedSnowMelting.LOGGER.info("Heat Source {} found at {} {} {} with value of {}", blockId, x, y, z, ConfigManager.getConfig().heatSources.get(blockId));
                    }
                }
                if (x == y || (x < 0 && x == -y) || (x > 0 && x == 1 - y)) {
                    int tempDx = dx;
                    dx = -dy;
                    dy = tempDx;
                }
                x += dx;
                y += dy;
            }
        }
        return false;
    }


    private static boolean isHeatSource(BlockState state) {
        if (ConfigManager.getConfig().heatSources == null) {
            ImprovedSnowMelting.LOGGER.error("heatSources IS NULL!");
            return false;
        }
        String blockId = Registries.BLOCK.getId(state.getBlock()).toString();

        return ConfigManager.getConfig().heatSources.containsKey(blockId);
    }

    public static void meltSnowBlock(World world, BlockPos pos) {
        try {
            BlockState above = world.getBlockState(pos.up());

            while (above.isOf(Blocks.SNOW_BLOCK) || above.isOf(Blocks.SNOW)) {
                pos = pos.up();
                above = world.getBlockState(pos.up());
            }
            BlockState state = world.getBlockState(pos);

            if (state.isOf(Blocks.SNOW_BLOCK)) {
                world.setBlockState(pos, Blocks.SNOW.getDefaultState().with(SnowBlock.LAYERS, 8));
                world.playSound(null, pos, SoundEvents.BLOCK_FIRE_EXTINGUISH, SoundCategory.BLOCKS, 0.05f, 1.0f);
            } else if (state.isOf(Blocks.SNOW)) {
                int layers = state.get(SnowBlock.LAYERS);
                if (layers > 1) {
                    world.setBlockState(pos, state.with(SnowBlock.LAYERS, layers - 1));
                } else {
                    world.setBlockState(pos, Blocks.AIR.getDefaultState());
                }
                world.playSound(null, pos, SoundEvents.BLOCK_SNOW_BREAK, SoundCategory.BLOCKS, 0.5f, 1.0f);
            }

        } catch (Exception e) {
            ImprovedSnowMelting.LOGGER.error("Error melting snow block at {}: {}", pos, e.getMessage());
        }
    }

    /// Checks every direction around the block, for use on solid blocks like snow or 8 layers as the light level for the block itself is 0.
    public static void simpleCheckAndMeltSnow(ServerWorld world, BlockPos pos) {
        for (Direction dir :
                Direction.values()) {
            if (ConfigManager.getConfig().simpleMeltingLightLevel <= world.getLightLevel(LightType.BLOCK, pos.offset(dir))) {
                SnowMeltManager.meltSnowBlock(world, pos);
                return;
            }
        }
    }
}