package net.technochronicle.tccore.api.fluid.store;

import net.technochronicle.tccore.api.fluid.FluidRegisterBuilder;

import net.minecraft.world.level.material.Fluid;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.function.Supplier;

public interface FluidStorage {

    @AllArgsConstructor
    public static class FluidEntry {

        @Getter
        private Supplier<? extends Fluid> fluid;
        @Nullable
        @Getter
        @Setter
        private FluidRegisterBuilder builder;
        @Getter
        @Setter
        private FluidStorageKey key;
    }

    /**
     * Enqueue a fluid for registration
     *
     * @param key     the key corresponding with the fluid
     * @param builder the FluidBuilder to build
     */
    public void enqueueRegistration(@NotNull FluidStorageKey key, @NotNull FluidRegisterBuilder builder);

    /**
     * @param key the key corresponding with the FluidBuilder
     * @return the fluid builder queued to be registered
     */
    public @Nullable FluidRegisterBuilder getQueuedBuilder(@NotNull FluidStorageKey key);

    /**
     * @param key the key corresponding with the fluid
     * @return the fluid associated with the key
     */
    public @Nullable Fluid get(@NotNull FluidStorageKey key);

    public @Nullable FluidEntry getEntry(@NotNull FluidStorageKey key);

    /**
     * @param key   the key to associate with the fluid
     * @param fluid the fluid to associate with the key
     * @throws IllegalArgumentException if a key is already associated with another fluid
     */
    void store(@NotNull FluidStorageKey key, @NotNull Supplier<? extends Fluid> fluid, @Nullable FluidRegisterBuilder builder);
}
