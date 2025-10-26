package dk.bertola.meltingsnowblocks.mixin;

import com.llamalad7.mixinextras.expression.Definition;
import com.llamalad7.mixinextras.expression.Expression;
import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import dk.bertola.meltingsnowblocks.MeltingSnowBlocks;
import dk.bertola.meltingsnowblocks.SnowMeltManager;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.SnowBlock;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.random.Random;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(SnowBlock.class)
public abstract class SnowBlockMixin {
    @Inject(method = "randomTick", at = @At("HEAD"), cancellable = true)
    private void heatBasedSnowMelting(BlockState state, ServerWorld world, BlockPos pos, Random random, CallbackInfo ci) {
        if (world.isClient) return;
        if(SnowMeltManager.checkAndMeltSnow(world,pos)){
            MeltingSnowBlocks.LOGGER.debug("Snow Block melt progress at {}", pos);
        }
    }

    @Definition(id = "getLightLevel", method = "Lnet/minecraft/server/world/ServerWorld;getLightLevel(Lnet/minecraft/world/LightType;Lnet/minecraft/util/math/BlockPos;)I")
    @Definition(id = "BLOCK", field = "Lnet/minecraft/world/LightType;BLOCK:Lnet/minecraft/world/LightType;")
    @Expression("?.getLightLevel(BLOCK, ?) > 11")
    @ModifyExpressionValue(method = "randomTick", at = @At("MIXINEXTRAS:EXPRESSION"))
    private boolean disableDefaultMeltBehavior(boolean original){
        return false;
    }
}
