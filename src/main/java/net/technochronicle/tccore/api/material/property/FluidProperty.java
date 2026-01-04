package net.technochronicle.tccore.api.material.property;

import net.technochronicle.tccore.api.fluid.FluidRegisterBuilder;
import net.technochronicle.tccore.api.fluid.store.FluidStorage;
import net.technochronicle.tccore.api.fluid.store.FluidStorageImpl;
import net.technochronicle.tccore.api.fluid.store.FluidStorageKey;
import net.technochronicle.tccore.api.fluid.store.FluidStorageKeys;
import net.technochronicle.tccore.api.material.Material;
import net.technochronicle.tccore.api.registry.registrate.TcRegistrate;

import net.minecraft.world.level.material.Fluid;
import net.neoforged.neoforge.fluids.FluidStack;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.function.Supplier;

/**
 * 流体属性
 */
@NoArgsConstructor
public class FluidProperty implements IMaterialProperty, FluidStorage {

    private final FluidStorageImpl storage = new FluidStorageImpl();
    @Getter
    @Setter
    private FluidStorageKey primaryKey = null;
    @Setter
    private @Nullable Fluid solidifyingFluid = null;

    public FluidProperty(@NotNull FluidStorageKey key, @NotNull FluidRegisterBuilder builder) {
        enqueueRegistration(key, builder);
    }

    public @NotNull FluidStorage getStorage() {
        return this;
    }

    @ApiStatus.Internal
    public void registerFluids(@NotNull Material material, @NotNull TcRegistrate registrate) {
        this.storage.registerFluids(material, registrate);
    }

    @Override
    public void enqueueRegistration(@NotNull FluidStorageKey key, @NotNull FluidRegisterBuilder builder) {
        storage.enqueueRegistration(key, builder);
        if (primaryKey == null) {
            primaryKey = key;
        }
    }

    @Override
    public void store(@NotNull FluidStorageKey key, @NotNull Supplier<? extends Fluid> fluid,
                      @Nullable FluidRegisterBuilder builder) {
        storage.store(key, fluid, builder);
        if (primaryKey == null) {
            primaryKey = key;
        }
    }

    @Override
    public @Nullable Fluid get(@NotNull FluidStorageKey key) {
        return storage.get(key);
    }

    @Override
    public @Nullable FluidEntry getEntry(@NotNull FluidStorageKey key) {
        return storage.getEntry(key);
    }

    @Override
    public @Nullable FluidRegisterBuilder getQueuedBuilder(@NotNull FluidStorageKey key) {
        return storage.getQueuedBuilder(key);
    }

    /**
     * @return the Fluid which solidifies into the material.
     */
    public @Nullable Fluid solidifiesFrom() {
        if (this.solidifyingFluid == null) {
            this.solidifyingFluid = getStorage().get(FluidStorageKeys.LIQUID);
        }
        return solidifyingFluid;
    }

    /**
     * @param amount the size of the returned FluidStack.
     * @return a FluidStack of the Fluid which solidifies into the material.
     */
    public @NotNull FluidStack solidifiesFrom(int amount) {
        Fluid fluid = solidifiesFrom();
        if (fluid == null) {
            return FluidStack.EMPTY;
        }
        return new FluidStack(fluid, amount);
    }

    @Override
    public void verifyProperty(MaterialProperties properties) {
        if (this.primaryKey == null) {
            throw new IllegalStateException("FluidProperty cannot be empty!");
        }
    }
}
