package net.technochronicle.tccore.api.material.registry;

import net.technochronicle.tccore.api.fluid.FluidRegisterBuilder;
import net.technochronicle.tccore.api.fluid.FluidState;
import net.technochronicle.tccore.api.fluid.store.FluidStorageKey;
import net.technochronicle.tccore.api.fluid.store.FluidStorageKeys;
import net.technochronicle.tccore.api.material.Element;
import net.technochronicle.tccore.api.material.Material;
import net.technochronicle.tccore.api.material.info.MaterialFlag;
import net.technochronicle.tccore.api.material.info.MaterialFlags;
import net.technochronicle.tccore.api.material.info.MaterialIconSet;
import net.technochronicle.tccore.api.material.property.*;
import net.technochronicle.tccore.api.material.stack.MaterialStack;
import net.technochronicle.tccore.api.tag.TagPrefix;
import net.technochronicle.tccore.data.material.TcMaterials;

import net.minecraft.resources.ResourceLocation;

import com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableList;
import org.jetbrains.annotations.NotNull;

import java.util.*;
import java.util.function.UnaryOperator;

@SuppressWarnings("UnusedReturnValue")
public class MaterialBuilder {

    private final Material.MaterialInfo materialInfo;
    private final MaterialProperties properties;
    private final MaterialFlags flags;
    private Set<TagPrefix> ignoredTagPrefixes = null;

    private String formula = null;

    /*
     * The temporary list of components for this Material.
     */
    private List<MaterialStack> composition = new ArrayList<>();

    /*
     * Temporary value to use to determine how to calculate default RGB
     */
    private boolean averageRGB = false;

    /**
     * Constructs a {@link Material}. This MaterialBuilder replaces the old constructors, and
     * no longer uses a class hierarchy, instead using a {@link MaterialProperties} system.
     *
     * @param resourceLocation The Name of this Material. Will be formatted as
     *                         "material.<name>" for the Translation Key.
     * @since GTCEu 2.0.0
     */
    public MaterialBuilder(ResourceLocation resourceLocation) {
        String name = resourceLocation.getPath();
        if (name.charAt(name.length() - 1) == '_')
            throw new IllegalArgumentException("Material name cannot end with a '_'!");
        materialInfo = new Material.MaterialInfo(resourceLocation);
        properties = new MaterialProperties();
        flags = new MaterialFlags();
    }

    /*
     * Material Types
     */

    /**
     * Add a {@link FluidProperty} to this Material.<br/>
     * Will be created as a {@link FluidStorageKeys#LIQUID}, without a Fluid Block.
     *
     * @throws IllegalArgumentException If a {@link FluidProperty} has already been added to this Material.
     */
    public MaterialBuilder fluid() {
        fluid(FluidStorageKeys.LIQUID, new FluidRegisterBuilder());
        return this;
    }

    /**
     * Add a {@link FluidProperty} to this Material.<br/>
     * Will be created with the specified state a with standard {@link FluidRegisterBuilder} defaults.
     * <p>
     * Can be called multiple times to add multiple fluids.
     */
    public MaterialBuilder fluid(@NotNull FluidStorageKey key, @NotNull FluidState state) {
        return fluid(key, new FluidRegisterBuilder().state(state));
    }

    /**
     * Add a {@link FluidProperty} to this Material.<br/>
     * <p>
     * Can be called multiple times to add multiple fluids.
     */
    public MaterialBuilder fluid(@NotNull FluidStorageKey key, @NotNull FluidRegisterBuilder builder) {
        properties.ensureSet(PropertyKey.FLUID);
        FluidProperty property = properties.getProperty(PropertyKey.FLUID);
        property.enqueueRegistration(key, builder);
        return this;
    }

    /**
     * Add a liquid for this material.
     *
     * @see #fluid(FluidStorageKey, FluidState)
     */
    public MaterialBuilder liquid() {
        return fluid(FluidStorageKeys.LIQUID, FluidState.LIQUID);
    }

    /**
     * Add a liquid for this material.
     *
     * @see #fluid(FluidStorageKey, FluidState)
     */
    public MaterialBuilder liquid(@NotNull FluidRegisterBuilder builder) {
        return fluid(FluidStorageKeys.LIQUID, builder.state(FluidState.LIQUID));
    }

    public MaterialBuilder liquid(int temp) {
        return liquid(new FluidRegisterBuilder().temperature(temp));
    }

    /**
     * Add a plasma for this material.
     *
     * @see #fluid(FluidStorageKey, FluidState)
     */
    public MaterialBuilder plasma() {
        return fluid(FluidStorageKeys.PLASMA, FluidState.PLASMA);
    }

    /**
     * Add a plasma for this material.
     *
     * @see #fluid(FluidStorageKey, FluidState)
     */
    public MaterialBuilder plasma(@NotNull FluidRegisterBuilder builder) {
        return fluid(FluidStorageKeys.PLASMA, builder.state(FluidState.PLASMA));
    }

    public MaterialBuilder plasma(int temp) {
        return plasma(new FluidRegisterBuilder().temperature(temp));
    }

    /**
     * Add a gas for this material.
     *
     * @see #fluid(FluidStorageKey, FluidState)
     */
    public MaterialBuilder gas() {
        return fluid(FluidStorageKeys.GAS, FluidState.GAS);
    }

    /**
     * Add a gas for this material.
     *
     * @see #fluid(FluidStorageKey, FluidState)
     */
    public MaterialBuilder gas(@NotNull FluidRegisterBuilder builder) {
        return fluid(FluidStorageKeys.GAS, builder.state(FluidState.GAS));
    }

    public MaterialBuilder gas(int temp) {
        return gas(new FluidRegisterBuilder().temperature(temp));
    }

    /**
     * Add a {@link DustProperty} to this Material.<br/>
     * Will be created with a Harvest Level of 2 and no Burn Time (Furnace Fuel).
     *
     * @throws IllegalArgumentException If a {@link DustProperty} has already been added to this Material.
     */
    public MaterialBuilder dust() {
        properties.ensureSet(PropertyKey.DUST);
        return this;
    }

    /**
     * Add a {@link DustProperty} to this Material.<br/>
     * Will be created with no Burn Time (Furnace Fuel).
     *
     * @param harvestLevel The Harvest Level of this block for Mining.<br/>
     *                     If this Material also has a {@link ToolProperty}, this value will
     *                     also be used to determine the tool's Mining Level.
     * @throws IllegalArgumentException If a {@link DustProperty} has already been added to this Material.
     */
    public MaterialBuilder dust(int harvestLevel) {
        return dust(harvestLevel, 0);
    }

    /**
     * Add a {@link DustProperty} to this Material.
     *
     * @param harvestLevel The Harvest Level of this block for Mining.<br/>
     *                     If this Material also has a {@link ToolProperty}, this value will
     *                     also be used to determine the tool's Mining Level.
     * @param burnTime     The Burn Time (in ticks) of this Material as a Furnace Fuel.
     * @throws IllegalArgumentException If a {@link DustProperty} has already been added to this Material.
     */
    public MaterialBuilder dust(int harvestLevel, int burnTime) {
        properties.setProperty(PropertyKey.DUST, new DustProperty(harvestLevel, burnTime));
        return this;
    }

    /**
     * Add a {@link WoodProperty} to this Material.<br/>
     * Useful for marking a Material as Wood for various additional behaviors.<br/>
     * Will be created with a Harvest Level of 0, and a Burn Time of 300 (Furnace Fuel).
     *
     * @throws IllegalArgumentException If a {@link DustProperty} has already been added to this Material.
     */
    public MaterialBuilder wood() {
        return wood(0, 300);
    }

    /**
     * Add a {@link WoodProperty} to this Material.<br/>
     * Useful for marking a Material as Wood for various additional behaviors.<br/>
     * Will be created with a Burn Time of 300 (Furnace Fuel).
     *
     * @param harvestLevel The Harvest Level of this block for Mining.<br/>
     *                     If this Material also has a {@link ToolProperty}, this value will
     *                     also be used to determine the tool's Mining Level.
     * @throws IllegalArgumentException If a {@link DustProperty} has already been added to this Material.
     */
    public MaterialBuilder wood(int harvestLevel) {
        return wood(harvestLevel, 300);
    }

    /**
     * Add a {@link WoodProperty} to this Material.<br/>
     * Useful for marking a Material as Wood for various additional behaviors.<br/>
     *
     * @param harvestLevel The Harvest Level of this block for Mining.<br/>
     *                     If this Material also has a {@link ToolProperty}, this value will
     *                     also be used to determine the tool's Mining Level.
     * @param burnTime     The Burn Time (in ticks) of this Material as a Furnace Fuel.
     * @throws IllegalArgumentException If a {@link DustProperty} has already been added to this Material.
     */
    public MaterialBuilder wood(int harvestLevel, int burnTime) {
        properties.setProperty(PropertyKey.DUST, new DustProperty(harvestLevel, burnTime));
        properties.ensureSet(PropertyKey.WOOD);
        return this;
    }

    /**
     * Add an {@link IngotProperty} to this Material.<br/>
     * Will be created with a Harvest Level of 2 and no Burn Time (Furnace Fuel).<br/>
     * Will automatically add a {@link DustProperty} to this Material if it does not already have one.
     *
     * @throws IllegalArgumentException If an {@link IngotProperty} has already been added to this Material.
     */
    public MaterialBuilder ingot() {
        properties.ensureSet(PropertyKey.INGOT);
        return this;
    }

    /**
     * Add an {@link IngotProperty} to this Material.<br/>
     * Will be created with no Burn Time (Furnace Fuel).<br/>
     * Will automatically add a {@link DustProperty} to this Material if it does not already have one.
     *
     * @param harvestLevel The Harvest Level of this block for Mining. 2 will make it require a iron tool.<br/>
     *                     If this Material also has a {@link ToolProperty}, this value will
     *                     also be used to determine the tool's Mining level (-1). So 2 will make the tool harvest
     *                     diamonds.<br/>
     *                     If this Material already had a Harvest Level defined, it will be overridden.
     * @throws IllegalArgumentException If an {@link IngotProperty} has already been added to this Material.
     */
    public MaterialBuilder ingot(int harvestLevel) {
        return ingot(harvestLevel, 0);
    }

    /**
     * Add an {@link IngotProperty} to this Material.<br/>
     * Will automatically add a {@link DustProperty} to this Material if it does not already have one.
     *
     * @param harvestLevel The Harvest Level of this block for Mining. 2 will make it require a iron tool.<br/>
     *                     If this Material also has a {@link ToolProperty}, this value will
     *                     also be used to determine the tool's Mining level (-1). So 2 will make the tool harvest
     *                     diamonds.<br/>
     *                     If this Material already had a Harvest Level defined, it will be overridden.
     * @param burnTime     The Burn Time (in ticks) of this Material as a Furnace Fuel.<br/>
     *                     If this Material already had a Burn Time defined, it will be overridden.
     * @throws IllegalArgumentException If an {@link IngotProperty} has already been added to this Material.
     */
    public MaterialBuilder ingot(int harvestLevel, int burnTime) {
        DustProperty prop = properties.getProperty(PropertyKey.DUST);
        if (prop == null) dust(harvestLevel, burnTime);
        else {
            if (prop.getHarvestLevel() == 2) prop.setHarvestLevel(harvestLevel);
            if (prop.getBurnTime() == 0) prop.setBurnTime(burnTime);
        }
        properties.ensureSet(PropertyKey.INGOT);
        return this;
    }

    /**
     * Add a {@link GemProperty} to this Material.<br/>
     * Will be created with a Harvest Level of 2 and no Burn Time (Furnace Fuel).<br/>
     * Will automatically add a {@link DustProperty} to this Material if it does not already have one.
     *
     * @throws IllegalArgumentException If a {@link GemProperty} has already been added to this Material.
     */
    public MaterialBuilder gem() {
        properties.ensureSet(PropertyKey.GEM);
        return this;
    }

    /**
     * Add a {@link GemProperty} to this Material.<br/>
     * Will be created with no Burn Time (Furnace Fuel).<br/>
     * Will automatically add a {@link DustProperty} to this Material if it does not already have one.
     *
     * @param harvestLevel The Harvest Level of this block for Mining.<br/>
     *                     If this Material also has a {@link ToolProperty}, this value will
     *                     also be used to determine the tool's Mining level.<br/>
     *                     If this Material already had a Harvest Level defined, it will be overridden.
     * @throws IllegalArgumentException If a {@link GemProperty} has already been added to this Material.
     */
    public MaterialBuilder gem(int harvestLevel) {
        return gem(harvestLevel, 0);
    }

    /**
     * Add a {@link GemProperty} to this Material.<br/>
     * Will automatically add a {@link DustProperty} to this Material if it does not already have one.
     *
     * @param harvestLevel The Harvest Level of this block for Mining.<br/>
     *                     If this Material also has a {@link ToolProperty}, this value will
     *                     also be used to determine the tool's Mining level.<br/>
     *                     If this Material already had a Harvest Level defined, it will be overridden.
     * @param burnTime     The Burn Time (in ticks) of this Material as a Furnace Fuel.<br/>
     *                     If this Material already had a Burn Time defined, it will be overridden.
     */
    public MaterialBuilder gem(int harvestLevel, int burnTime) {
        DustProperty prop = properties.getProperty(PropertyKey.DUST);
        if (prop == null) dust(harvestLevel, burnTime);
        else {
            if (prop.getHarvestLevel() == 2) prop.setHarvestLevel(harvestLevel);
            if (prop.getBurnTime() == 0) prop.setBurnTime(burnTime);
        }
        properties.ensureSet(PropertyKey.GEM);
        return this;
    }

    /**
     * Add a {@link PolymerProperty} to this Material.<br/>
     * Will be created with a Harvest Level of 2 and no Burn Time (Furnace Fuel).<br/>
     * Will automatically add a {@link DustProperty} to this Material if it does not already have one.
     *
     * @throws IllegalArgumentException If an {@link PolymerProperty} has already been added to this Material.
     */
    public MaterialBuilder polymer() {
        properties.ensureSet(PropertyKey.POLYMER);
        return this;
    }

    /**
     * Add a {@link PolymerProperty} to this Material.<br/>
     * Will automatically add a {@link DustProperty} to this Material if it does not already have one.
     * Will have a burn time of 0
     *
     * @param harvestLevel The Harvest Level of this block for Mining.<br/>
     *                     If this Material also has a {@link ToolProperty}, this value will
     *                     also be used to determine the tool's Mining level.<br/>
     *                     If this Material already had a Harvest Level defined, it will be overridden.
     * @throws IllegalArgumentException If an {@link PolymerProperty} has already been added to this Material.
     */
    public MaterialBuilder polymer(int harvestLevel) {
        DustProperty prop = properties.getProperty(PropertyKey.DUST);
        if (prop == null) dust(harvestLevel, 0);
        else if (prop.getHarvestLevel() == 2) prop.setHarvestLevel(harvestLevel);
        properties.ensureSet(PropertyKey.POLYMER);
        return this;
    }

    public MaterialBuilder burnTime(int burnTime) {
        DustProperty prop = properties.getProperty(PropertyKey.DUST);
        if (prop == null) {
            dust();
            prop = properties.getProperty(PropertyKey.DUST);
        }
        prop.setBurnTime(burnTime);
        return this;
    }

    /**
     * Set the Color of this Material.<br/>
     * Defaults to 0xFFFFFF unless {@link MaterialBuilder#colorAverage()} was called, where
     * it will be a weighted average of the components of the Material.
     *
     * @param color The RGB-formatted Color.
     */
    public MaterialBuilder color(int color) {
        color(color, true);
        return this;
    }

    /**
     * Set the Color of this Material.<br/>
     * Defaults to 0xFFFFFF unless {@link MaterialBuilder#colorAverage()} was called, where
     * it will be a weighted average of the components of the Material.
     *
     * @param color         The RGB-formatted Color.
     * @param hasFluidColor Whether the fluid should be colored or not.
     */
    public MaterialBuilder color(int color, boolean hasFluidColor) {
        this.materialInfo.getColors().set(0, color);
        this.materialInfo.setHasFluidColor(hasFluidColor);
        return this;
    }

    /**
     * Set the secondary color of this Material.<br/>
     * Defaults to 0xFFFFFF unless {@link MaterialBuilder#colorAverage()} was called, where
     * it will be a weighted average of the components of the Material.
     *
     * @param color The RGB-formatted Color.
     */
    public MaterialBuilder secondaryColor(int color) {
        this.materialInfo.getColors().set(1, color);
        return this;
    }

    public MaterialBuilder colorAverage() {
        this.averageRGB = true;
        return this;
    }

    /**
     * Set the {@link MaterialIconSet} of this Material.<br/>
     * Defaults vary depending on if the Material has a:<br/>
     * <ul>
     * <li>{@link GemProperty}, it will default to {@link MaterialIconSet#GEM_VERTICAL}
     * <li>{@link IngotProperty} or {@link DustProperty}, it will default to {@link MaterialIconSet#DULL}
     * <li>{@link FluidProperty}, it will default to {@link MaterialIconSet#FLUID}
     * </ul>
     * Default will be determined by first-found Property in this order, unless specified.
     *
     * @param iconSet The {@link MaterialIconSet} of this Material.
     */
    public MaterialBuilder iconSet(MaterialIconSet iconSet) {
        materialInfo.setIconSet(iconSet);
        return this;
    }

    public MaterialBuilder components(Object... components) {
        Preconditions.checkArgument(
                components.length % 2 == 0,
                "Material Components list malformed!");

        for (int i = 0; i < components.length; i += 2) {
            if (components[i] == null) {
                throw new IllegalArgumentException(
                        "Material in Components List is null for Material " + this.materialInfo.getResourceLocation());
            }
            composition.add(new MaterialStack(
                    components[i] instanceof CharSequence chars ? TcMaterials.get(chars.toString()) :
                            (Material) components[i],
                    ((Number) components[i + 1]).longValue()));
        }
        return this;
    }

    public MaterialBuilder componentStacks(MaterialStack... components) {
        composition = Arrays.asList(components);
        return this;
    }

    public MaterialBuilder componentStacks(ImmutableList<MaterialStack> components) {
        composition = components;
        return this;
    }

    /**
     * Add {@link MaterialFlags} to this Material.<br/>
     * Dependent Flags (for example, {@link MaterialFlags#GENERATE_LONG_ROD} requiring
     * {@link MaterialFlags#GENERATE_ROD}) will be automatically applied.
     */
    public MaterialBuilder flags(MaterialFlag... flags) {
        this.flags.addFlags(flags);
        return this;
    }

    /**
     * Add {@link MaterialFlags} to this Material.<br/>
     * Dependent Flags (for example, {@link MaterialFlags#GENERATE_LONG_ROD} requiring
     * {@link MaterialFlags#GENERATE_ROD}) will be automatically applied.
     *
     * @param f1 A {@link Collection} of {@link MaterialFlag}. Provided this way for easy Flag presets to be
     *           applied.
     * @param f2 An Array of {@link MaterialFlag}. If no {@link Collection} is required, use
     *           {@link MaterialBuilder#flags(MaterialFlag...)}.
     */
    // rename for kjs conflicts
    public MaterialBuilder appendFlags(Collection<MaterialFlag> f1, MaterialFlag... f2) {
        this.flags.addFlags(f1.toArray(new MaterialFlag[0]));
        this.flags.addFlags(f2);
        return this;
    }

    /**
     * Added {@link TagPrefix} to be ignored by this Material.<br/>
     */
    public MaterialBuilder ignoredTagPrefixes(TagPrefix... prefixes) {
        if (this.ignoredTagPrefixes == null) {
            this.ignoredTagPrefixes = new HashSet<>();
        }
        this.ignoredTagPrefixes.addAll(Arrays.asList(prefixes));
        return this;
    }

    public MaterialBuilder element(Element element) {
        this.materialInfo.setElement(element);
        return this;
    }

    public MaterialBuilder formula(String formula) {
        this.formula = formula;
        return this;
    }

    /**
     * Replaced the old toolStats methods which took many parameters.
     * Use {@link ToolProperty.Builder} instead to create a Tool Property.
     */
    public MaterialBuilder toolStats(ToolProperty toolProperty) {
        properties.setProperty(PropertyKey.TOOL, toolProperty);
        return this;
    }

    public MaterialBuilder blastTemp(int temp) {
        return blast(temp);
    }

    public MaterialBuilder blastTemp(int temp, int eutOverride) {
        return blast(b -> b.temp(temp).blastStats(eutOverride));
    }

    public MaterialBuilder blastTemp(int temp, int eutOverride, int durationOverride) {
        return blast(b -> b.temp(temp).blastStats(eutOverride, durationOverride));
    }

    public MaterialBuilder blast(int temp) {
        properties.setProperty(PropertyKey.BLAST, new BlastProperty(temp));
        return this;
    }

    public MaterialBuilder blast(UnaryOperator<BlastProperty.Builder> b) {
        properties.setProperty(PropertyKey.BLAST, b.apply(new BlastProperty.Builder()).build());
        return this;
    }

    public MaterialBuilder ore() {
        properties.ensureSet(PropertyKey.ORE);
        return this;
    }

    public MaterialBuilder ore(boolean emissive) {
        properties.setProperty(PropertyKey.ORE, new OreProperty(1, 1, emissive));
        return this;
    }

    public MaterialBuilder ore(int oreMultiplier, int byproductMultiplier) {
        properties.setProperty(PropertyKey.ORE, new OreProperty(oreMultiplier, byproductMultiplier));
        return this;
    }

    public MaterialBuilder ore(int oreMultiplier, int byproductMultiplier, boolean emissive) {
        properties.setProperty(PropertyKey.ORE, new OreProperty(oreMultiplier, byproductMultiplier, emissive));
        return this;
    }

    public MaterialBuilder washedIn(Material m) {
        properties.ensureSet(PropertyKey.ORE);
        properties.getProperty(PropertyKey.ORE).setWashedIn(m);
        return this;
    }

    public MaterialBuilder washedIn(Material m, int washedAmount) {
        properties.ensureSet(PropertyKey.ORE);
        properties.getProperty(PropertyKey.ORE).setWashedIn(m, washedAmount);
        return this;
    }

    public MaterialBuilder separatedInto(Material... m) {
        properties.ensureSet(PropertyKey.ORE);
        properties.getProperty(PropertyKey.ORE).setSeparatedInto(m);
        return this;
    }

    public MaterialBuilder oreSmeltInto(Material m) {
        properties.ensureSet(PropertyKey.ORE);
        properties.getProperty(PropertyKey.ORE).setDirectSmeltResult(m);
        return this;
    }

    public MaterialBuilder polarizesInto(Material m) {
        properties.ensureSet(PropertyKey.INGOT);
        properties.getProperty(PropertyKey.INGOT).setMagneticMaterial(m);
        return this;
    }

    public MaterialBuilder arcSmeltInto(Material m) {
        properties.ensureSet(PropertyKey.INGOT);
        properties.getProperty(PropertyKey.INGOT).setArcSmeltingInto(m);
        return this;
    }

    public MaterialBuilder macerateInto(Material m) {
        properties.ensureSet(PropertyKey.INGOT);
        properties.getProperty(PropertyKey.INGOT).setMacerateInto(m);
        return this;
    }

    public MaterialBuilder ingotSmeltInto(Material m) {
        properties.ensureSet(PropertyKey.INGOT);
        properties.getProperty(PropertyKey.INGOT).setSmeltingInto(m);
        return this;
    }

    public MaterialBuilder addOreByproducts(Material... byproducts) {
        properties.ensureSet(PropertyKey.ORE);
        properties.getProperty(PropertyKey.ORE).setOreByProducts(byproducts);
        return this;
    }

    public Material buildAndRegister() {
        materialInfo.setComponentList(ImmutableList.copyOf(composition));
        for (MaterialStack materialStack : materialInfo.getComponentList()) {
            Material material = materialStack.material();
        }

        var mat = new Material(materialInfo, properties, flags);
        if (formula != null) {
            mat.setFormula(formula);
        }
        materialInfo.verifyInfo(properties, averageRGB);
        mat.registerMaterial();
        if (ignoredTagPrefixes != null) {
            ignoredTagPrefixes.forEach(p -> p.setIgnored(mat));
        }
        return mat;
    }
}
