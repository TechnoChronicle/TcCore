package net.technochronicle.tccore.api.tag;

import net.technochronicle.tclib.utils.FormattingUtil;

import net.technochronicle.tccore.api.TcAPI;
import net.technochronicle.tccore.api.material.ItemMaterialData;
import net.technochronicle.tccore.api.material.Material;
import net.technochronicle.tccore.api.material.info.MaterialIconType;
import net.technochronicle.tccore.api.material.property.PropertyKey;
import net.technochronicle.tccore.api.material.stack.MaterialStack;
import net.technochronicle.tccore.data.material.TcMaterialIconTypes;
import net.technochronicle.tccore.data.material.TcMaterials;
import net.technochronicle.tccore.utils.memoization.CacheMemoizer;

import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;

import com.google.common.base.Preconditions;
import com.google.common.collect.Table;
import com.lowdragmc.lowdraglib2.utils.LocalizationUtils;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import it.unimi.dsi.fastutil.objects.Object2FloatMap;
import it.unimi.dsi.fastutil.objects.Object2FloatOpenHashMap;
import it.unimi.dsi.fastutil.objects.Object2ObjectLinkedOpenHashMap;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.annotations.Unmodifiable;

import java.util.*;
import java.util.function.*;

import static net.technochronicle.tccore.api.tag.TagPrefix.Conditions.*;

@SuppressWarnings("unused")
@Accessors(chain = true, fluent = true)
public class TagPrefix {

    public final static Map<String, TagPrefix> PREFIXES = new HashMap<>();
    public static final Map<TagPrefix, OreType> ORES = new Object2ObjectLinkedOpenHashMap<>();

    public static final Codec<TagPrefix> CODEC = Codec.STRING.flatXmap(
            str -> Optional.ofNullable(get(str)).map(DataResult::success)
                    .orElseGet(() -> DataResult.error(() -> "invalid TagPrefix: " + str)),
            prefix -> DataResult.success(prefix.name));

    public static TagPrefix get(String name) {
        return PREFIXES.get(name);
    }

    public boolean isEmpty() {
        return this == NULL_PREFIX;
    }

    public static final TagPrefix NULL_PREFIX = new TagPrefix("null");

    public static class Conditions {

        public static final Predicate<Material> hasToolProperty = mat -> mat.hasProperty(PropertyKey.TOOL);
        // public static final Predicate<Material> hasNoCraftingToolProperty = hasToolProperty.and(mat ->
        // !mat.getProperty(PropertyKey.TOOL).isIgnoreCraftingTools());
        public static final Predicate<Material> hasOreProperty = mat -> mat.hasProperty(PropertyKey.ORE);
        public static final Predicate<Material> hasGemProperty = mat -> mat.hasProperty(PropertyKey.GEM);
        public static final Predicate<Material> hasDustProperty = mat -> mat.hasProperty(PropertyKey.DUST);
        public static final Predicate<Material> hasIngotProperty = mat -> mat.hasProperty(PropertyKey.INGOT);
        public static final Predicate<Material> hasBlastProperty = mat -> mat.hasProperty(PropertyKey.BLAST);
    }

    public record OreType(Supplier<BlockState> stoneType, Supplier<Material> material,
                          Supplier<BlockBehaviour.Properties> template, ResourceLocation baseModelLocation,
                          boolean isDoubleDrops, boolean isSand, boolean shouldDropAsItem) {}

    public record BlockProperties(UnaryOperator<BlockBehaviour.Properties> properties) {}

    @Getter
    public final String name;
    @Getter
    @Setter
    private String idPattern;

    protected final List<TagType> tags = new ArrayList<>();
    @Setter
    @Getter
    public String langValue;

    @Getter
    @Setter
    private long materialAmount = -1;

    @Setter
    @Getter
    private boolean unificationEnabled;
    @Setter
    @Getter
    private boolean generateRecycling = false;
    @Setter
    private boolean generateItem;
    @Setter
    private boolean generateBlock;
    @Getter
    private BlockProperties blockProperties = new BlockProperties(UnaryOperator.identity());

    @Getter
    @Setter
    private @Nullable Predicate<Material> generationCondition;

    @Nullable
    @Getter
    @Setter
    private MaterialIconType materialIconType;

    @Setter
    private Supplier<Table<TagPrefix, Material, ? extends Supplier<? extends ItemLike>>> itemTable;

    @Nullable
    @Getter
    @Setter
    private BiConsumer<Material, Consumer<Component>> tooltip;

    private final Map<Material, Supplier<? extends ItemLike>[]> ignoredMaterials = new HashMap<>();
    private final Object2FloatMap<Material> materialAmounts = new Object2FloatOpenHashMap<>();

    @Getter
    @Setter
    private int maxStackSize = 64;

    @Getter
    private final List<MaterialStack> secondaryMaterials = new ArrayList<>();

    @Getter
    protected final Set<TagKey<Block>> miningToolTag = new HashSet<>();

    public TagPrefix(String name) {
        this.name = name;
        String lowerCaseUnder = FormattingUtil.toLowerCaseUnder(name);
        this.idPattern = "%s_" + lowerCaseUnder;
        this.langValue = "%s " + FormattingUtil.toEnglishName(lowerCaseUnder);
        PREFIXES.put(name, this);
    }

    public static TagPrefix oreTagPrefix(String name, TagKey<Block> miningToolTag) {
        return new TagPrefix(name)
                .defaultTagPath("ores/%s")
                .prefixOnlyTagPath("ores_in_ground/%s")
                .unformattedTagPath("ores")
                .materialIconType(TcMaterialIconTypes.ore)
                .miningToolTag(miningToolTag)
                .unificationEnabled(true)
                .generationCondition(hasOreProperty);
    }

    public void addSecondaryMaterial(MaterialStack secondaryMaterial) {
        Preconditions.checkNotNull(secondaryMaterial, "secondaryMaterial");
        secondaryMaterials.add(secondaryMaterial);
    }

    public TagPrefix registerOre(Supplier<BlockState> stoneType, Supplier<Material> material,
                                 BlockBehaviour.Properties properties, ResourceLocation baseModelLocation) {
        return registerOre(stoneType, material, properties, baseModelLocation, false);
    }

    public TagPrefix registerOre(Supplier<BlockState> stoneType, Supplier<Material> material,
                                 BlockBehaviour.Properties properties, ResourceLocation baseModelLocation,
                                 boolean doubleDrops) {
        return registerOre(stoneType, material, properties, baseModelLocation, doubleDrops, false, false);
    }

    public TagPrefix registerOre(Supplier<BlockState> stoneType, Supplier<Material> material,
                                 BlockBehaviour.Properties properties, ResourceLocation baseModelLocation,
                                 boolean doubleDrops, boolean isSand, boolean shouldDropAsItem) {
        return registerOre(stoneType, material, () -> properties, baseModelLocation, doubleDrops, isSand,
                shouldDropAsItem);
    }

    public TagPrefix registerOre(Supplier<BlockState> stoneType, Supplier<Material> material,
                                 Supplier<BlockBehaviour.Properties> properties, ResourceLocation baseModelLocation,
                                 boolean doubleDrops, boolean isSand, boolean shouldDropAsItem) {
        ORES.put(this,
                new OreType(stoneType, material, properties, baseModelLocation, doubleDrops, isSand, shouldDropAsItem));
        return this;
    }

    public TagPrefix defaultTagPath(String path) {
        return this.defaultTagPath(path, false);
    }

    public TagPrefix defaultTagPath(String path, boolean isVanilla) {
        this.tags.add(TagType.withDefaultFormatter(path, isVanilla));
        return this;
    }

    public TagPrefix prefixTagPath(String path) {
        this.tags.add(TagType.withPrefixFormatter(path));
        return this;
    }

    public TagPrefix prefixOnlyTagPath(String path) {
        this.tags.add(TagType.withPrefixOnlyFormatter(path));
        return this;
    }

    public TagPrefix unformattedTagPath(String path) {
        return unformattedTagPath(path, false);
    }

    public TagPrefix unformattedTagPath(String path, boolean isVanilla) {
        this.tags.add(TagType.withNoFormatter(path, isVanilla));
        return this;
    }

    public TagPrefix customTagPath(String path, BiFunction<TagPrefix, Material, TagKey<Item>> formatter) {
        this.tags.add(TagType.withCustomFormatter(path, formatter));
        return this;
    }

    public TagPrefix customTagPredicate(String path, boolean isVanilla, Predicate<Material> materialPredicate) {
        this.tags.add(TagType.withCustomFilter(path, isVanilla, materialPredicate));
        return this;
    }

    public TagPrefix miningToolTag(TagKey<Block> tag) {
        this.miningToolTag.add(tag);
        return this;
    }

    public TagPrefix blockProperties(UnaryOperator<BlockBehaviour.Properties> properties) {
        this.blockProperties = new BlockProperties(properties);
        return this;
    }

    public TagPrefix blockProperties(BlockProperties properties) {
        this.blockProperties = properties;
        return this;
    }

    public TagPrefix enableRecycling() {
        this.generateRecycling = true;
        return this;
    }

    public long getMaterialAmount(@NotNull Material material) {
        if (material.isNull() || !isAmountModified(material)) {
            return this.materialAmount;
        }
        return (long) (TcAPI.M * materialAmounts.getFloat(material));
    }

    public static TagPrefix getPrefix(String prefixName) {
        return getPrefix(prefixName, null);
    }

    public static TagPrefix getPrefix(String prefixName, @Nullable TagPrefix replacement) {
        return PREFIXES.getOrDefault(prefixName, replacement);
    }

    @Unmodifiable
    public List<TagKey<Item>> getItemParentTags() {
        return tags.stream()
                .filter(TagType::isParentTag)
                .map(type -> type.getTag(this, TcMaterials.NULL))
                .toList();
    }

    @Unmodifiable
    public List<TagKey<Item>> getItemTags(@NotNull Material mat) {
        return tags.stream()
                .filter(type -> !type.isParentTag())
                .map(type -> type.getTag(this, mat))
                .filter(Objects::nonNull)
                .toList();
    }

    @Unmodifiable
    public List<TagKey<Item>> getAllItemTags(@NotNull Material mat) {
        return tags.stream()
                .map(type -> type.getTag(this, mat))
                .filter(Objects::nonNull)
                .toList();
    }

    @Unmodifiable
    public List<TagKey<Block>> getBlockTags(@NotNull Material mat) {
        return tags.stream()
                .filter(type -> !type.isParentTag())
                .map(type -> type.getTag(this, mat))
                .map(itemTagKey -> TagKey.create(Registries.BLOCK, itemTagKey.location()))
                .toList();
    }

    @Unmodifiable
    public List<TagKey<Block>> getAllBlockTags(@NotNull Material mat) {
        return tags.stream()
                .map(type -> type.getTag(this, mat))
                .map(itemTagKey -> TagKey.create(Registries.BLOCK, itemTagKey.location()))
                .toList();
    }

    public boolean hasItemTable() {
        return itemTable != null;
    }

    @SuppressWarnings("unchecked")
    public Supplier<ItemLike> getItemFromTable(Material material) {
        return (Supplier<ItemLike>) itemTable.get().get(this, material);
    }

    public boolean doGenerateItem() {
        return generateItem;
    }

    public boolean doGenerateItem(Material material) {
        return generateItem && !isIgnored(material) &&
                (generationCondition == null || generationCondition.test(material)) ||
                (hasItemTable() && this.itemTable.get() != null && getItemFromTable(material) != null);
    }

    public boolean doGenerateBlock() {
        return generateBlock;
    }

    public boolean doGenerateBlock(Material material) {
        return generateBlock && !isIgnored(material) &&
                (generationCondition == null || generationCondition.test(material)) ||
                hasItemTable() && this.itemTable.get() != null && getItemFromTable(material) != null;
    }

    public String getUnlocalizedName() {
        return "tagprefix." + FormattingUtil.toLowerCaseUnderscore(name);
    }

    public MutableComponent getLocalizedName(Material material) {
        return Component.translatable(getUnlocalizedName(material), material.getLocalizedName());
    }

    public String getUnlocalizedName(Material material) {
        String formattedPrefix = FormattingUtil.toLowerCaseUnderscore(this.name);
        String matSpecificKey = String.format("item.%s.%s", material.getModid(),
                this.idPattern.formatted(material.getName()));
        if (LocalizationUtils.exist(matSpecificKey)) {
            return matSpecificKey;
        }
        if (material.hasProperty(PropertyKey.POLYMER)) {
            String localizationKey = String.format("tagprefix.polymer.%s", formattedPrefix);
            // Not every polymer tagprefix prefix gets a special name
            if (LocalizationUtils.exist(localizationKey)) {
                return localizationKey;
            }
        }

        return getUnlocalizedName();
    }

    public boolean isIgnored(Material material) {
        return ignoredMaterials.containsKey(material);
    }

    @SafeVarargs
    public final void setIgnored(Material material, Supplier<? extends ItemLike>... items) {
        ignoredMaterials.put(material, items);
        if (items.length > 0) {
            ItemMaterialData.registerMaterialEntries(Arrays.asList(items), this, material);
        }
    }

    @SuppressWarnings("unchecked")
    public void setIgnored(Material material, ItemLike... items) {
        // go through setIgnoredBlock to wrap if this is a block prefix
        if (this.doGenerateBlock()) {
            this.setIgnoredBlock(material,
                    Arrays.stream(items).filter(Block.class::isInstance).map(Block.class::cast).toArray(Block[]::new));
        } else {
            this.setIgnored(material,
                    Arrays.stream(items).map(item -> (Supplier<ItemLike>) () -> item).toArray(Supplier[]::new));
        }
    }

    @SuppressWarnings("unchecked")
    public void setIgnoredBlock(Material material, Block... items) {
        this.setIgnored(material, Arrays.stream(items).map(block -> CacheMemoizer.memoizeBlockSupplier(() -> block))
                .toArray(Supplier[]::new));
    }

    @SuppressWarnings("unchecked")
    public void setIgnored(Material material) {
        this.ignoredMaterials.put(material, new Supplier[0]);
    }

    public void removeIgnored(Material material) {
        ignoredMaterials.remove(material);
    }

    public Map<Material, Supplier<? extends ItemLike>[]> getIgnored() {
        return new HashMap<>(ignoredMaterials);
    }

    public boolean isAmountModified(Material material) {
        return materialAmounts.containsKey(material);
    }

    public void modifyMaterialAmount(@NotNull Material material, float amount) {
        materialAmounts.put(material, amount);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        TagPrefix tagPrefix = (TagPrefix) o;
        return name.equals(tagPrefix.name);
    }

    @Override
    public int hashCode() {
        return name.hashCode();
    }

    public static Collection<TagPrefix> values() {
        return PREFIXES.values();
    }

    @Override
    public String toString() {
        return name;
    }
}
