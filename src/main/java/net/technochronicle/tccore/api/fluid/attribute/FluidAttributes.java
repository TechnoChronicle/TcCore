package net.technochronicle.tccore.api.fluid.attribute;

import net.technochronicle.tclib.TcUtil;

import net.minecraft.network.chat.Component;

public final class FluidAttributes {

    /**
     * Attribute for acidic fluids.
     */
    public static final FluidAttribute ACID = new FluidAttribute(TcUtil.id("acid"),
            list -> list.accept(Component.translatable("tccore.fluid.type_acid.tooltip")),
            list -> list.accept(Component.translatable("tccore.fluid_pipe.acid_proof")));

    private FluidAttributes() {}
}
