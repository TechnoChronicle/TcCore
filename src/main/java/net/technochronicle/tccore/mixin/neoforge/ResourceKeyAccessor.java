package net.technochronicle.tccore.mixin.neoforge;

import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

@Mixin(value = ResourceKey.class, remap = false)
public interface ResourceKeyAccessor {

    @Invoker("<init>")
    static <T> ResourceKey<T> callCreate(ResourceLocation registryName, ResourceLocation location) {
        throw new AssertionError();
    }
}
