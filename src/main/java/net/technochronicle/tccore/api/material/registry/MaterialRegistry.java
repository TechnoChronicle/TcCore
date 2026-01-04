package net.technochronicle.tccore.api.material.registry;

import net.technochronicle.tccore.TcCore;
import net.technochronicle.tccore.api.material.Material;
import net.technochronicle.tccore.api.registry.TcRegistry;
import net.technochronicle.tccore.data.material.TcMaterials;

import net.minecraft.core.Holder;
import net.minecraft.core.RegistrationInfo;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;

import org.jetbrains.annotations.NotNull;

import java.util.*;
import java.util.stream.Stream;

public final class MaterialRegistry extends TcRegistry<Material> implements IMaterialRegistry {

    private final Set<String> usedNamespaces = new HashSet<>();
    private final Map<String, Material> fallbackMaterials = new HashMap<>();
    private Phase registrationPhase = Phase.PRE;

    public MaterialRegistry(ResourceKey<Registry<Material>> key) {
        super(key);
    }

    @Override
    public @NotNull Set<String> getUsedNamespaces() {
        return Collections.unmodifiableSet(usedNamespaces);
    }

    @Override
    public @NotNull Stream<Material> stream() {
        return super.stream();
    }

    public Material register(Material material) {
        return register(material.getResourceLocation(), material);
    }

    @Override
    public Material getMaterial(ResourceLocation name) {
        return getOrDefault(name, TcMaterials.NULL);
    }

    @Override
    public ResourceLocation getKey(Material material) {
        return material.getResourceLocation();
    }

    @Override
    public Holder.@NotNull Reference<Material> register(int id,
                                                        @NotNull ResourceKey<Material> key, @NotNull Material value,
                                                        @NotNull RegistrationInfo registrationInfo) {
        usedNamespaces.add(key.location().getNamespace());
        return super.register(id, key, value, registrationInfo);
    }

    /**
     * Set the fallback material for a namespace.
     * This is only for manual fallback usage.
     *
     * @param modId    the namespace to set the fallback for
     * @param material the fallback material
     */
    @Override
    public void setFallbackMaterial(@NotNull String modId, @NotNull Material material) {
        fallbackMaterials.put(modId, material);
    }

    /**
     * This is only for manual fallback usage.
     *
     * @param modId the namespace to get the fallback for
     * @return the fallback material, used for when another material does not exist
     */
    @Override
    @NotNull
    public Material getFallbackMaterial(@NotNull String modId) {
        return fallbackMaterials.getOrDefault(modId, getDefaultFallback());
    }

    @NotNull
    public Material getDefaultFallback() {
        return fallbackMaterials.get(TcCore.MOD_ID);
    }

    @NotNull
    @Override
    public Phase getPhase() {
        return registrationPhase;
    }

    public void unfreezeRegistries() {
        registrationPhase = Phase.OPEN;
    }

    public void closeRegistries() {
        registrationPhase = Phase.CLOSED;
    }

    public void freezeRegistries() {
        registrationPhase = Phase.FROZEN;
    }
}
