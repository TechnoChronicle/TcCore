package net.technochronicle.tccore.api.attribute;

import net.technochronicle.tclib.TcUtil;

import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.ItemAttributeModifiers;

import java.util.List;

public interface IAttributeModifierProvider {

    ResourceLocation ROOT = TcUtil.id("equipping/");
    ResourceLocation ITEM = TcUtil.id("item/");

    static ResourceLocation equipping(ItemStack item) {
        ResourceLocation name = BuiltInRegistries.ITEM.getKey(item.getItem());
        return ROOT.withSuffix(name.getNamespace() + "/" + name.getPath());
    }

    static ResourceLocation path(ItemStack item) {
        ResourceLocation name = BuiltInRegistries.ITEM.getKey(item.getItem());
        return ITEM.withSuffix(name.getNamespace() + "/" + name.getPath());
    }

    List<ItemAttributeModifiers.Entry> getEntries();
}
