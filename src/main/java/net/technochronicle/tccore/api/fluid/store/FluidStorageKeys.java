package net.technochronicle.tccore.api.fluid.store;

import net.technochronicle.tclib.TcUtil;

import net.technochronicle.tccore.api.fluid.FluidState;
import net.technochronicle.tccore.api.material.Material;
import net.technochronicle.tccore.api.material.property.FluidProperty;
import net.technochronicle.tccore.api.material.property.PropertyKey;
import net.technochronicle.tccore.data.material.TcMaterialIconTypes;

import org.jetbrains.annotations.NotNull;

public final class FluidStorageKeys {

    public static final FluidStorageKey LIQUID = new FluidStorageKey(TcUtil.id("liquid"),
            "liquids",
            TcMaterialIconTypes.liquid,
            m -> prefixedRegisteredName("liquid_", FluidStorageKeys.LIQUID, m),
            m -> m.hasProperty(PropertyKey.DUST) ? "tccore.fluid.liquid_generic" : "tccore.fluid.generic",
            FluidState.LIQUID, 0);

    public static final FluidStorageKey GAS = new FluidStorageKey(TcUtil.id("gas"),
            "gases",
            TcMaterialIconTypes.gas,
            m -> postfixedRegisteredName("_gas", FluidStorageKeys.GAS, m),
            m -> {
                if (m.hasProperty(PropertyKey.DUST)) {
                    return "tccore.fluid.gas_vapor";
                }
                if (m.isElement()) {
                    FluidProperty property = m.getProperty(PropertyKey.FLUID);
                    if (m.isElement() || (property != null && property.getPrimaryKey() != FluidStorageKeys.LIQUID)) {
                        return "tccore.fluid.gas_generic";
                    }
                }
                return "tccore.fluid.generic";
            },
            FluidState.GAS, 0);

    public static final FluidStorageKey PLASMA = new FluidStorageKey(TcUtil.id("plasma"),
            "plasmas",
            TcMaterialIconTypes.plasma,
            m -> m.getName() + "_plasma",
            m -> "tccore.fluid.plasma",
            FluidState.PLASMA, -1);

    public static final FluidStorageKey MOLTEN = new FluidStorageKey(TcUtil.id("molten"),
            "molten",
            TcMaterialIconTypes.molten,
            m -> "molten_" + m.getName(),
            m -> "tccore.fluid.molten",
            FluidState.LIQUID, -1);

    private FluidStorageKeys() {}

    private static @NotNull String prefixedRegisteredName(@NotNull String prefix, @NotNull FluidStorageKey key,
                                                          @NotNull Material material) {
        FluidProperty property = material.getProperty(PropertyKey.FLUID);
        if (property != null && property.getPrimaryKey() != key) {
            return prefix + material.getName();
        }
        return material.getName();
    }

    private static @NotNull String postfixedRegisteredName(@NotNull String postfix, @NotNull FluidStorageKey key,
                                                           @NotNull Material material) {
        FluidProperty property = material.getProperty(PropertyKey.FLUID);
        if (property != null && property.getPrimaryKey() != key) {
            return material.getName() + postfix;
        }
        return material.getName();
    }
}
