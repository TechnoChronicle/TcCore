package net.technochronicle.tccore.api.addon;

import net.technochronicle.tccore.api.addon.event.MaterialCasingCollectionEvent;
import net.technochronicle.tccore.api.registry.registrate.TcRegistrate;

import net.minecraft.data.recipes.RecipeOutput;
import net.minecraft.resources.ResourceLocation;

import java.util.function.Consumer;

@SuppressWarnings("unused")
public interface ITcAddon {

    /**
     * @return this addon's TcRegistrate instance.
     */
    TcRegistrate getRegistrate();

    /**
     * This runs after TcCore has set up it's content. Set up TcCore loading-dependent (but NOT ones dependent on
     * 
     * @apiNote DO NOT REGISTER ANY OF YOUR OWN CONTENT HERE, AS IF YOU DO, IT'LL REGISTER AS IF TcCore REGISTERED IT
     *          AND YOUR DATAGEN AND EVENTS WILL <b><i>NOT</i></b> WORK AS EXPECTED, IF AT ALL.
     */
    void tcInitComplete();

    /**
     * Call init on your custom TagPrefix class(es) here
     */
    default void registerTagPrefixes() {}

    /**
     * Call init on your custom IWorldGenLayer class(es) here
     */
    default void registerWorldgenLayers() {}

    /**
     * Call init on your custom VeinGenerator class(es) here
     */
    default void registerVeinGenerators() {}

    /**
     * Call init on your custom IndicatorGenerator class(es) here
     */
    default void registerIndicatorGenerators() {}

    default void addRecipes(RecipeOutput provider) {}

    default void removeRecipes(Consumer<ResourceLocation> consumer) {}

    /**
     * Register Material -> Casing block mappings here
     */
    default void collectMaterialCasings(MaterialCasingCollectionEvent event) {}

    /**
     * Does this addon require high-tier content to be enabled?
     *
     * @return if this addon requires highTier.
     */
    default boolean requiresHighTier() {
        return false;
    }
}
