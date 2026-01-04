package net.technochronicle.tccore.api.block;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.BlockAndTintGetter;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class AppearanceBlock extends Block implements IAppearance {

    public AppearanceBlock(Properties properties) {
        super(properties);
    }

    @Override
    public @NotNull BlockState getAppearance(@NotNull BlockState state, @NotNull BlockAndTintGetter level, @NotNull BlockPos pos, @NotNull Direction side,
                                             @Nullable BlockState queryState, @Nullable BlockPos queryPos) {
        var appearance = this.getBlockAppearance(state, level, pos, side, queryState, queryPos);
        return appearance == null ? state : appearance;
    }
}
