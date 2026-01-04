package net.technochronicle.tccore.api.item;

import net.technochronicle.tccore.api.fluid.TcFluid;
import net.technochronicle.tccore.api.material.Material;
import net.technochronicle.tccore.api.material.property.PropertyKey;

import net.minecraft.network.chat.Component;
import net.minecraft.world.item.BucketItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.material.Fluid;
import net.neoforged.neoforge.client.extensions.common.IClientFluidTypeExtensions;

import org.jetbrains.annotations.Nullable;

public class MaterialBucketItem extends BucketItem {

    public final Material material;
    public final String langKey;

    public MaterialBucketItem(Fluid fluid, Properties properties, Material material, String langKey) {
        super(fluid, properties);
        this.material = material;
        this.langKey = langKey;
    }

    public static int color(ItemStack itemStack, int index) {
        if (itemStack.getItem() instanceof MaterialBucketItem item) {
            if (index == 1) {
                return IClientFluidTypeExtensions.of(item.content).getTintColor();
            }
        }
        return -1;
    }

    @Override
    public Component getName(ItemStack stack) {
        Component materialName = material.getLocalizedName();
        return Component.translatable("item.tccore.bucket", Component.translatable(this.langKey, materialName));
    }

    @Override
    public int getBurnTime(ItemStack itemStack, @Nullable RecipeType<?> recipeType) {
        var property = material.getProperty(PropertyKey.FLUID);
        if (property != null) {
            var fluid = material.getFluid();
            if (fluid instanceof TcFluid bFluid) {
                return bFluid.getBurnTime();
            }
        }
        return 0;
    }
}
