package net.technochronicle.tccore.api.block.debug;

import net.technochronicle.tccore.api.material.ChemicalHelper;
import net.technochronicle.tccore.api.material.Material;

import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.ItemInteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.BucketItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;

import org.jetbrains.annotations.NotNull;

public class CheckMatBlock extends Block {

    public CheckMatBlock(Properties properties) {
        super(properties);
    }

    @Override
    protected @NotNull ItemInteractionResult useItemOn(@NotNull ItemStack stack, @NotNull BlockState state, @NotNull Level level, @NotNull BlockPos pos, @NotNull Player player, @NotNull InteractionHand hand, @NotNull BlockHitResult hitResult) {
        if (player.isShiftKeyDown()) {
            return ItemInteractionResult.CONSUME;
        }
        var item = stack.getItem();
        if (item instanceof BucketItem bi) {
            displayMaterial(player, ChemicalHelper.getMaterial(bi.content), "Material Fluid Bucket:%s");
        } else if (item instanceof BlockItem mbi) {
            displayMaterial(player, ChemicalHelper.getMaterialStack(mbi.getBlock()).material(), "Material Item:%s");
        } else {
            displayMaterial(player, ChemicalHelper.getMaterialStack(item).material(), "Material Item:%s");
        }
        return ItemInteractionResult.SUCCESS;
    }

    private void displayMaterial(Player player, Material material, String format) {
        player.displayClientMessage(Component.literal(format.formatted(material.getName())), true);
    }
}
