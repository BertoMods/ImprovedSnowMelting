package dk.bertola.meltingsnowblocks.mixin;

import dk.bertola.meltingsnowblocks.MeltingSnowBlocks;
import dk.bertola.meltingsnowblocks.SnowMeltManager;
import net.minecraft.block.AbstractBlock;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.random.Random;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(AbstractBlock.class)
public class SnowBlockAbstractMixin {

    @Inject(method = "randomTick", at = @At("HEAD"), cancellable = true)
    private void heatBasedSnowMelting(BlockState state, ServerWorld world, BlockPos pos, Random random, CallbackInfo ci) {
        if (world.isClient) return;
        if (!state.isOf(Blocks.SNOW_BLOCK)) return;
        if(SnowMeltManager.checkAndMeltSnow(world,pos)){
            MeltingSnowBlocks.LOGGER.debug("Snow Block melt progress at {}", pos.toString());
        }
    }
}
