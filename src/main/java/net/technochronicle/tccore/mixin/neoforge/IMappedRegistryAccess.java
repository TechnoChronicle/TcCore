package net.technochronicle.tccore.mixin.neoforge;

import net.minecraft.core.Holder;
import net.minecraft.core.RegistrationInfo;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;

import it.unimi.dsi.fastutil.objects.ObjectList;
import it.unimi.dsi.fastutil.objects.Reference2IntMap;

import java.util.Map;

public interface IMappedRegistryAccess<T> {

    default ObjectList<Holder.Reference<T>> tc$getById() {
        throw new AssertionError();
    }

    default Reference2IntMap<T> tc$getToId() {
        throw new AssertionError();
    }

    default Map<ResourceLocation, Holder.Reference<T>> tc$getByLocation() {
        throw new AssertionError();
    }

    default Map<ResourceKey<T>, Holder.Reference<T>> tc$getByKey() {
        throw new AssertionError();
    }

    default Map<T, Holder.Reference<T>> tc$getByValue() {
        throw new AssertionError();
    }

    default Map<ResourceKey<T>, RegistrationInfo> tc$getRegistrationInfos() {
        throw new AssertionError();
    }
}
