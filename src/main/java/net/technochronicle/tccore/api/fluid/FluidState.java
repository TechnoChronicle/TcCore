package net.technochronicle.tccore.api.fluid;

import net.technochronicle.tccore.data.tag.CustomTags;

import net.minecraft.tags.TagKey;
import net.minecraft.world.level.material.Fluid;
import net.neoforged.neoforge.common.Tags;

import lombok.Getter;
import org.jetbrains.annotations.NotNull;

public enum FluidState {

    LIQUID("tccore.fluid.state_liquid", CustomTags.LIQUID_FLUIDS),
    GAS("tccore.fluid.state_gas", Tags.Fluids.GASEOUS),
    PLASMA("tccore.fluid.state_plasma", CustomTags.PLASMA_FLUIDS),
    ;

    @Getter
    private final String translationKey;
    @Getter
    private final TagKey<Fluid> tagKey;

    FluidState(@NotNull String translationKey, @NotNull TagKey<Fluid> tagKey) {
        this.translationKey = translationKey;
        this.tagKey = tagKey;
    }
}
