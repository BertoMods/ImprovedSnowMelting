package dk.bertola.meltingsnowblocks.mixin;


import com.llamalad7.mixinextras.expression.Definition;
import com.llamalad7.mixinextras.expression.Expression;
import net.minecraft.block.AbstractBlock;
import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.block.MapColor;
import net.minecraft.sound.BlockSoundGroup;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArg;


@Mixin(Blocks.class)
public class BlocksMixin {
    @Definition(id = "register", method = "Lnet/minecraft/block/Blocks;register(Ljava/lang/String;Lnet/minecraft/block/Block;)Lnet/minecraft/block/Block;")
    @Definition(id = "Block", type = Block.class)
    @Expression("register('snow_block', @(new Block(?)))")
    @ModifyArg(
            method = "<clinit>",
            at = @At("MIXINEXTRAS:EXPRESSION")
    )
    private static AbstractBlock.Settings modifySnowBlockSettings(AbstractBlock.Settings settings) {
        return settings.ticksRandomly();
    }
}
