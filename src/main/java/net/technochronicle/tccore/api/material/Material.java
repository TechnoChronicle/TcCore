package net.technochronicle.tccore.api.material;

import net.technochronicle.tclib.utils.FormattingUtil;

import net.technochronicle.tccore.api.TcAPI;
import net.technochronicle.tccore.api.fluid.FluidRegisterBuilder;
import net.technochronicle.tccore.api.fluid.store.FluidStorageKey;
import net.technochronicle.tccore.api.fluid.store.FluidStorageKeys;
import net.technochronicle.tccore.api.material.info.MaterialFlag;
import net.technochronicle.tccore.api.material.info.MaterialFlags;
import net.technochronicle.tccore.api.material.info.MaterialIconSet;
import net.technochronicle.tccore.api.material.property.FluidProperty;
import net.technochronicle.tccore.api.material.property.IMaterialProperty;
import net.technochronicle.tccore.api.material.property.MaterialProperties;
import net.technochronicle.tccore.api.material.property.PropertyKey;
import net.technochronicle.tccore.api.material.stack.MaterialStack;
import net.technochronicle.tccore.api.tag.TagUtil;
import net.technochronicle.tccore.data.material.TcMaterialIconSet;
import net.technochronicle.tccore.data.material.TcMaterials;
import net.technochronicle.tccore.utils.TcMath;

import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.material.Fluid;
import net.neoforged.neoforge.fluids.FluidStack;

import com.google.common.collect.ImmutableList;
import it.unimi.dsi.fastutil.ints.IntArrayList;
import it.unimi.dsi.fastutil.ints.IntList;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;

public class Material implements Comparable<Material> {

    /**
     * 此材料的基本信息。
     *
     * @see MaterialInfo
     */
    @NotNull
    @Getter
    private final MaterialInfo materialInfo;

    /**
     * 此材料的属性。
     *
     * @see MaterialProperties
     */
    @NotNull
    @Getter
    private final MaterialProperties properties;

    /**
     * 此材料的生成标志。
     *
     * @see MaterialFlags
     */
    @NotNull
    @Getter
    private final MaterialFlags flags;

    /**
     * 此材料的化学式。
     */
    private String chemicalFormula;

    private String calculateChemicalFormula() {
        if (chemicalFormula != null) return this.chemicalFormula;
        if (materialInfo.element != null) {
            String[] split = materialInfo.element.symbol().split("-");
            String result;
            if (split.length > 1) {
                split[1] = FormattingUtil.toSmallUpNumbers(split[1]);
                result = split[0] + split[1];
            } else result = materialInfo.element.symbol();
            return result;
        }
        if (!materialInfo.componentList.isEmpty()) {
            StringBuilder components = new StringBuilder();
            for (MaterialStack component : materialInfo.componentList)
                components.append(component.toString());
            return components.toString();
        }
        return "";
    }

    public String getChemicalFormula() {
        return chemicalFormula;
    }

    public Material setFormula(String formula) {
        return setFormula(formula, true);
    }

    public Material setFormula(String formula, boolean withFormatting) {
        this.chemicalFormula = withFormatting ? FormattingUtil.toSmallDownNumbers(formula) : formula;
        return this;
    }

    public ImmutableList<MaterialStack> getMaterialComponents() {
        return materialInfo.componentList;
    }

    public Material setComponents(MaterialStack... components) {
        this.materialInfo.setComponents(components);
        this.chemicalFormula = this.calculateChemicalFormula();
        return this;
    }

    public Material(@NotNull MaterialInfo materialInfo, @NotNull MaterialProperties properties,
                    @NotNull MaterialFlags flags) {
        this.materialInfo = materialInfo;
        this.properties = properties;
        this.flags = flags;
        this.properties.setMaterial(this);
        verifyMaterial();
    }

    // thou shall not call
    protected Material(ResourceLocation resourceLocation) {
        materialInfo = new MaterialInfo(resourceLocation);
        materialInfo.iconSet = MaterialIconSet.DULL;
        properties = new MaterialProperties();
        flags = new MaterialFlags();
    }

    public void registerMaterial() {
        TcAPI.materialManager.register(this);
    }

    public String getName() {
        return materialInfo.resourceLocation.getPath();
    }

    public String getModid() {
        return materialInfo.resourceLocation.getNamespace();
    }

    public void addFlags(MaterialFlag... flags) {
        this.flags.addFlags(flags).verify(this);
    }

    public boolean hasFlag(MaterialFlag flag) {
        return flags.hasFlag(flag);
    }

    public boolean isElement() {
        return materialInfo.element != null;
    }

    @Nullable
    public Element getElement() {
        return materialInfo.element;
    }

    public boolean hasFlags(MaterialFlag... flags) {
        return Arrays.stream(flags).allMatch(this::hasFlag);
    }

    public boolean hasAnyOfFlags(MaterialFlag... flags) {
        return Arrays.stream(flags).anyMatch(this::hasFlag);
    }

    protected void calculateDecompositionType() {}

    /**
     * Retrieves a fluid from the material.
     * Attempts to retrieve with {@link FluidProperty#getPrimaryKey()}, {@link FluidStorageKeys#LIQUID} and
     * {@link FluidStorageKeys#GAS}.
     *
     * @return the fluid
     * @see #getFluid(FluidStorageKey)
     */
    public Fluid getFluid() {
        FluidProperty prop = getProperty(PropertyKey.FLUID);
        if (prop == null) {
            throw new IllegalArgumentException("Material " + getResourceLocation() + " does not have a Fluid!");
        }

        Fluid fluid = prop.get(prop.getPrimaryKey());
        if (fluid != null) return fluid;

        fluid = getFluid(FluidStorageKeys.LIQUID);
        if (fluid != null) return fluid;

        return getFluid(FluidStorageKeys.GAS);
    }

    /**
     * @param key the key for the fluid
     * @return the fluid corresponding with the key
     */
    public Fluid getFluid(@NotNull FluidStorageKey key) {
        FluidProperty prop = getProperty(PropertyKey.FLUID);
        if (prop == null) {
            throw new IllegalArgumentException("Material " + getResourceLocation() + " does not have a Fluid!");
        }

        return prop.get(key);
    }

    /**
     * @param amount the amount the FluidStack should have
     * @return a FluidStack with the fluid and amount
     * @see #getFluid(FluidStorageKey, int)
     */
    public FluidStack getFluid(int amount) {
        return new FluidStack(getFluid(), amount);
    }

    /**
     * @param key    the key for the fluid
     * @param amount the amount the FluidStack should have
     * @return a FluidStack with the fluid and amount
     */
    public FluidStack getFluid(@NotNull FluidStorageKey key, int amount) {
        return new FluidStack(getFluid(key), amount);
    }

    /**
     * @return a {@code TagKey<Fluid>} with the material's name as the tag key
     * @see #getFluid(FluidStorageKey, int)
     */
    public TagKey<Fluid> getFluidTag() {
        return TagUtil.createFluidTag(this.getName());
    }

    /**
     * Retrieves a fluid builder from the material.
     * <br/>
     * NOTE: only available before the fluids are registered.
     * <br/>
     * Attempts to retrieve with {@link FluidProperty#getPrimaryKey()}, {@link FluidStorageKeys#LIQUID} and
     * {@link FluidStorageKeys#GAS}.
     *
     * @return the fluid builder
     */
    public FluidRegisterBuilder getFluidBuilder() {
        FluidProperty prop = getProperty(PropertyKey.FLUID);
        if (prop == null) {
            throw new IllegalArgumentException("Material " + getResourceLocation() + " does not have a Fluid!");
        }

        FluidStorageKey key = prop.getPrimaryKey();
        FluidRegisterBuilder fluid = null;

        if (key != null) fluid = prop.getStorage().getQueuedBuilder(key);
        if (fluid != null) return fluid;

        fluid = getFluidBuilder(FluidStorageKeys.LIQUID);
        if (fluid != null) return fluid;

        return getFluidBuilder(FluidStorageKeys.GAS);
    }

    /**
     * NOTE: only available before the fluids are registered.
     *
     * @param key the key for the fluid
     * @return the fluid corresponding with the key
     */
    public FluidRegisterBuilder getFluidBuilder(@NotNull FluidStorageKey key) {
        FluidProperty prop = getProperty(PropertyKey.FLUID);
        if (prop == null) {
            throw new IllegalArgumentException("Material " + getResourceLocation() + " does not have a Fluid!");
        }

        return prop.getStorage().getQueuedBuilder(key);
    }

    public Item getBucket() {
        Fluid fluid = getFluid();
        return fluid.getBucket();
    }

    public int getBlockHarvestLevel() {
        if (!hasProperty(PropertyKey.DUST))
            throw new IllegalArgumentException("Material " + materialInfo.resourceLocation +
                    " does not have a harvest level! Is probably a Fluid");
        int harvestLevel = getProperty(PropertyKey.DUST).getHarvestLevel();
        return harvestLevel > 0 ? harvestLevel - 1 : harvestLevel;
    }

    public void setMaterialARGB(int materialRGB) {
        materialInfo.colors.set(0, materialRGB);
    }

    public void setMaterialSecondaryARGB(int materialRGB) {
        materialInfo.colors.set(1, materialRGB);
    }

    public int getLayerARGB(int layerIndex) {
        // get 2nd digit as positive if emissive layer
        if (layerIndex < -100) {
            layerIndex = (Math.abs(layerIndex) % 100) / 10;
        }
        if (layerIndex > materialInfo.colors.size() - 1 || layerIndex < 0) return -1;
        int layerColor = getMaterialARGB(layerIndex);
        if (layerColor != -1 || layerIndex == 0) return layerColor;
        else return getMaterialARGB(0);
    }

    public int getMaterialARGB() {
        return materialInfo.colors.getInt(0) | 0xff000000;
    }

    public int getMaterialSecondaryARGB() {
        return materialInfo.colors.getInt(1) | 0xff000000;
    }

    /**
     * 获取特定颜色图层的ARGB值。
     *
     * @param index 图层索引 [0,10)。如果传入值大于10会导致崩溃。
     * @return 特定颜色图层的ARGB值。
     */
    public int getMaterialARGB(int index) {
        return materialInfo.colors.getInt(index) | 0xff000000;
    }

    /**
     * 获取材料的RGB颜色值（默认图层）。
     *
     * @return 材料的RGB颜色值。
     */
    public int getMaterialRGB() {
        return materialInfo.colors.getInt(0);
    }

    /**
     * 获取特定颜色图层的RGB值。
     *
     * @param index 图层索引 [0,10)。如果传入值大于10会导致崩溃。
     * @return 特定颜色图层的RGB值。
     */
    public int getMaterialRGB(int index) {
        return materialInfo.colors.getInt(index);
    }

    public int getMaterialSecondaryRGB() {
        return materialInfo.colors.getInt(1);
    }

    public boolean hasFluidColor() {
        return materialInfo.hasFluidColor;
    }

    public void setMaterialIconSet(MaterialIconSet materialIconSet) {
        materialInfo.iconSet = materialIconSet;
    }

    public MaterialIconSet getMaterialIconSet() {
        return materialInfo.iconSet;
    }

    public boolean isRadioactive() {
        if (materialInfo.element != null)
            return materialInfo.element.halfLifeSeconds() >= 0;
        for (MaterialStack material : materialInfo.componentList)
            if (material.material().isRadioactive()) return true;
        return false;
    }

    public long getProtons() {
        if (materialInfo.element != null)
            return materialInfo.element.protons();
        if (materialInfo.componentList.isEmpty())
            return Math.max(1, 43);
        long totalProtons = 0, totalAmount = 0;
        for (MaterialStack material : materialInfo.componentList) {
            if (material.isEmpty()) continue;
            totalAmount += material.amount();
            totalProtons += material.amount() * material.material().getProtons();
        }
        if (totalAmount == 0) return 0;
        return totalProtons / totalAmount;
    }

    public long getNeutrons() {
        if (materialInfo.element != null)
            return materialInfo.element.neutrons();
        if (materialInfo.componentList.isEmpty())
            return 55;
        long totalNeutrons = 0, totalAmount = 0;
        for (MaterialStack material : materialInfo.componentList) {
            if (material.isEmpty()) continue;
            totalAmount += material.amount();
            totalNeutrons += material.amount() * material.material().getNeutrons();
        }
        if (totalAmount == 0) return 0;
        return totalNeutrons / totalAmount;
    }

    public long getMass() {
        if (materialInfo.element != null)
            return materialInfo.element.mass();
        if (materialInfo.componentList.isEmpty())
            return 98;
        long totalMass = 0, totalAmount = 0;
        for (MaterialStack material : materialInfo.componentList) {
            if (material.isEmpty()) continue;
            totalAmount += material.amount();
            totalMass += material.amount() * material.material().getMass();
        }
        if (totalAmount == 0) return 0;
        return totalMass / totalAmount;
    }

    public String toCamelCaseString() {
        return FormattingUtil.lowerUnderscoreToUpperCamel(getName());
    }

    @NotNull
    public ResourceLocation getResourceLocation() {
        return materialInfo.resourceLocation;
    }

    public String getUnlocalizedName() {
        return materialInfo.resourceLocation.toLanguageKey("material");
    }

    public MutableComponent getLocalizedName() {
        return Component.translatable(getUnlocalizedName());
    }

    @Override
    public int compareTo(Material material) {
        return toString().compareTo(material.toString());
    }

    @Override
    public String toString() {
        return materialInfo.resourceLocation.toString();
    }

    // must be named multiply for GroovyScript to allow `mat * quantity -> MaterialStack`
    public MaterialStack multiply(long amount) {
        return new MaterialStack(this, amount);
    }

    public <T extends IMaterialProperty> boolean hasProperty(PropertyKey<T> key) {
        return getProperty(key) != null;
    }

    public <T extends IMaterialProperty> T getProperty(PropertyKey<T> key) {
        return properties.getProperty(key);
    }

    public <T extends IMaterialProperty> void setProperty(PropertyKey<T> key, IMaterialProperty property) {
        properties.setProperty(key, property);
        properties.verify();
    }

    public boolean isSolid() {
        return hasProperty(PropertyKey.INGOT) || hasProperty(PropertyKey.GEM);
    }

    public boolean hasFluid() {
        return hasProperty(PropertyKey.FLUID);
    }

    public void verifyMaterial() {
        properties.verify();
        flags.verify(this);
        this.chemicalFormula = calculateChemicalFormula();
        calculateDecompositionType();
    }

    public boolean isNull() {
        return this == TcMaterials.NULL;
    }

    @Override
    public final boolean equals(Object o) {
        if (this == o)
            return true;
        if (!(o instanceof Material material))
            return false;

        return Objects.equals(this.getResourceLocation(), material.getResourceLocation());
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(this.getResourceLocation());
    }

    /**
     * 保存材料的基本信息，如名称、颜色、ID等。
     */
    @SuppressWarnings("UnusedReturnValue")
    @Accessors(chain = true)
    public static class MaterialInfo {

        /**
         * 此材料的modid和未本地化名称。
         * <p>
         * 必需项。
         */
        @Getter
        private final ResourceLocation resourceLocation;

        /**
         * 此材料的颜色。
         * 如果索引0之后的任何颜色值为-1，则表示未使用。
         * <p>
         * 默认值：若无成分则为0xFFFFFF，否则将为成分的平均值。
         */
        @Getter
        @Setter
        private IntList colors = new IntArrayList(List.of(-1, -1));

        /**
         * 此材料的流体颜色是否启用。
         * <p>
         * 默认值：true
         */
        @Getter
        @Setter
        private boolean hasFluidColor = true;

        /**
         * 此材料的成分列表。
         * <p>
         * 默认值：无。
         */
        @Getter
        @Setter
        private ImmutableList<MaterialStack> componentList;

        /**
         * 此材料的图标集。
         * <p>
         * 默认值：- 若具有GemProperty则为GEM_VERTICAL。
         * - 若具有DustProperty或IngotProperty则为DULL。
         */
        @Getter
        @Setter
        private MaterialIconSet iconSet;

        /**
         * 此材料的元素（如果是直接元素）。
         * <p>
         * 默认值：无。
         */
        @Getter
        @Setter
        private Element element;

        public MaterialInfo(ResourceLocation resourceLocation) {
            this.resourceLocation = resourceLocation;
        }

        public void verifyInfo(MaterialProperties p, boolean averageRGB) {
            // Verify IconSet

            if (iconSet == null) {
                if (p.hasProperty(PropertyKey.FLUID)) {
                    iconSet = TcMaterialIconSet.FLUID;
                } else iconSet = TcMaterialIconSet.DULL;
            }

            // Verify MaterialRGB
            if (colors.getInt(0) == -1) {
                if (!averageRGB || componentList.isEmpty())
                    colors.set(0, 0xFFFFFF);
                else {
                    long colorTemp = 0;
                    long divisor = 0;
                    for (MaterialStack stack : componentList) {
                        colorTemp += stack.material().getMaterialARGB() * stack.amount();
                        divisor += stack.amount();
                    }
                    colors.set(0, TcMath.saturatedCast(colorTemp / divisor));
                }
            }
        }

        public MaterialInfo setComponents(MaterialStack... components) {
            this.componentList = ImmutableList.copyOf(Arrays.stream(components).toList());
            return this;
        }
    }
}
