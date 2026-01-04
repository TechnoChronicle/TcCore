package net.technochronicle.tccore.api.material;

import net.technochronicle.tccore.TcCore;
import net.technochronicle.tccore.api.TcAPI;
import net.technochronicle.tccore.api.fluid.store.FluidStorageKey;
import net.technochronicle.tccore.api.material.property.FluidProperty;
import net.technochronicle.tccore.api.material.property.PropertyKey;
import net.technochronicle.tccore.api.material.stack.ItemMaterialInfo;
import net.technochronicle.tccore.api.material.stack.MaterialEntry;
import net.technochronicle.tccore.api.material.stack.MaterialStack;
import net.technochronicle.tccore.api.tag.TagPrefix;
import net.technochronicle.tccore.api.tag.TagUtil;
import net.technochronicle.tccore.data.material.TcMaterials;
import net.technochronicle.tccore.data.tagprefix.TcTagPrefixes;

import net.minecraft.core.Holder;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.Fluid;

import com.mojang.datafixers.util.Pair;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.annotations.Unmodifiable;

import java.util.*;
import java.util.function.Supplier;
import java.util.stream.Collectors;

import static net.technochronicle.tccore.api.TcAPI.M;
import static net.technochronicle.tccore.api.material.ItemMaterialData.*;

/**
 * 化学助手类，提供材料系统与游戏物品之间的转换和查询功能。
 * <p>
 * 该类作为材料系统的核心工具类，实现了以下主要功能：
 * <ol>
 * <li>材料和物品之间的双向转换</li>
 * <li>标签前缀系统的查询和解析</li>
 * <li>材料物品的获取和创建</li>
 * <li>材料系统的缓存优化</li>
 * </ol>
 * 该类使用多种缓存机制提升性能，包括LazyInit和静态映射表。
 * </p>
 *
 * @see Material
 * @see TagPrefix
 * @see MaterialEntry
 * @see MaterialStack
 */
public class ChemicalHelper {

    /**
     * 从各种对象类型中获取材料堆栈。
     * <p>
     * 支持的输入类型包括：
     * <ul>
     * <li>{@link MaterialStack} - 直接返回</li>
     * <li>{@link MaterialEntry} - 转换为材料堆栈</li>
     * <li>{@link ItemStack} - 从物品获取材料信息</li>
     * <li>{@link ItemLike} - 从物品获取材料信息</li>
     * <li>{@link Ingredient} - 尝试从成分的第一个匹配物品获取</li>
     * </ul>
     * </p>
     *
     * @param object 待转换的对象
     * @return 对应的材料堆栈，如果无法转换则返回 {@link MaterialStack#EMPTY}
     */
    public static MaterialStack getMaterialStack(@Nullable Object object) {
        if (object instanceof MaterialStack materialStack) {
            return materialStack;
        } else if (object instanceof MaterialEntry entry) {
            return getMaterialStack(entry);
        } else if (object instanceof ItemStack itemStack) {
            return getMaterialStack(itemStack);
        } else if (object instanceof ItemLike item) {
            return getMaterialStack(item);
        } else if (object instanceof Ingredient ing) {
            for (var stack : ing.getValues()) {
                var ms = getMaterialStack(stack.getItems());
                if (!ms.isEmpty()) return ms;
            }
        }
        return MaterialStack.EMPTY;
    }

    /**
     * 从物品堆栈中获取材料堆栈。
     * <p>
     * 如果物品堆栈为空，则返回空材料堆栈。该方法会调用 {@link #getMaterialStack(ItemLike)} 进行具体处理。
     * </p>
     *
     * @param itemStack 物品堆栈
     * @return 对应的材料堆栈
     * @throws NullPointerException 如果 {@code itemStack} 为 null
     */
    public static MaterialStack getMaterialStack(@NotNull ItemStack itemStack) {
        if (itemStack.isEmpty()) return MaterialStack.EMPTY;
        return getMaterialStack(itemStack.getItem());
    }

    /**
     * 从材料条目中获取材料堆栈。
     * <p>
     * 通过材料条目中的标签前缀和材料，计算材料数量并创建材料堆栈。
     * 如果材料为空（{@link Material#isNull()}），则返回空堆栈。
     * </p>
     *
     * @param entry 材料条目
     * @return 对应的材料堆栈
     * @throws NullPointerException 如果 {@code entry} 为 null
     */
    public static MaterialStack getMaterialStack(@NotNull MaterialEntry entry) {
        Material entryMaterial = entry.material();
        if (!entryMaterial.isNull()) {
            return new MaterialStack(entryMaterial, entry.tagPrefix().getMaterialAmount(entryMaterial));
        }
        return MaterialStack.EMPTY;
    }

    /**
     * 从物品中获取材料堆栈。
     * <p>
     * 首先尝试获取物品的材料条目，如果成功则转换为材料堆栈。
     * 否则从物品材料信息缓存中查找。
     * 如果都没有找到，则记录错误并返回空堆栈。
     * </p>
     *
     * @param itemLike 物品
     * @return 对应的材料堆栈
     */
    public static MaterialStack getMaterialStack(ItemLike itemLike) {
        var entry = getMaterialEntry(itemLike);
        if (!entry.isEmpty()) {
            Material entryMaterial = entry.material();
            return new MaterialStack(entryMaterial, entry.tagPrefix().getMaterialAmount(entryMaterial));
        }
        ItemMaterialInfo info = ITEM_MATERIAL_INFO.get(itemLike);
        if (info == null) return MaterialStack.EMPTY;
        if (info.getMaterial().isEmpty()) {
            TcCore.LOGGER.error("ItemMaterialInfo for {} is empty!", itemLike);
            return MaterialStack.EMPTY;
        }
        return info.getMaterial();
    }

    /**
     * 获取流体对应的材料。
     * <p>
     * 该方法使用LazyInit策略，首次调用时会遍历所有材料并建立流体到材料的映射关系。
     * 查找规则：
     * <ol>
     * <li>材料必须具有 {@link PropertyKey#FLUID} 属性</li>
     * <li>通过 {@link FluidStorageKey} 获取材料对应的流体</li>
     * <li>根据流体创建对应的标签并检查是否存在</li>
     * <li>建立流体到材料的映射关系</li>
     * </ol>
     * 后续调用直接使用缓存的结果。
     * </p>
     *
     * @param fluid 流体
     * @return 对应的材料，如果未找到则返回 {@link TcMaterials#NULL}
     */
    public static Material getMaterial(Fluid fluid) {
        if (FLUID_MATERIAL.isEmpty()) {
            Set<TagKey<Fluid>> allFluidTags = BuiltInRegistries.FLUID.getTagNames().collect(Collectors.toSet());
            for (final Material material : TcAPI.materialManager) {
                if (material.hasProperty(PropertyKey.FLUID)) {
                    FluidProperty property = material.getProperty(PropertyKey.FLUID);
                    FluidStorageKey.allKeys().stream()
                            .map(property::get)
                            .filter(Objects::nonNull)
                            .map(f -> Pair.of(f, TagUtil.createFluidTag(BuiltInRegistries.FLUID.getKey(f).getPath())))
                            .filter(pair -> allFluidTags.contains(pair.getSecond()))
                            .forEach(pair -> {
                                allFluidTags.remove(pair.getSecond());
                                FLUID_MATERIAL.put(pair.getFirst(), material);
                            });
                }
            }
        }
        return FLUID_MATERIAL.getOrDefault(fluid, TcMaterials.NULL);
    }

    /**
     * 获取物品对应的标签前缀。
     * <p>
     * 通过物品的材料条目获取其标签前缀。
     * 如果未找到对应的材料条目，则返回 {@link TagPrefix#NULL_PREFIX}。
     * </p>
     *
     * @param itemLike 物品
     * @return 对应的标签前缀
     */
    public static TagPrefix getPrefix(ItemLike itemLike) {
        MaterialEntry entry = getMaterialEntry(itemLike);
        if (!entry.isEmpty()) return entry.tagPrefix();
        return TagPrefix.NULL_PREFIX;
    }

    /**
     * 根据材料和材料数量获取粉末物品堆栈。
     * <p>
     * 获取条件：
     * <ol>
     * <li>材料必须具有 {@link PropertyKey#DUST} 属性</li>
     * <li>材料数量大于0</li>
     * <li>材料数量是 {@link TcAPI#M} 的整数倍，或者大于等于 {@code M * 16}</li>
     * </ol>
     * 如果满足条件，则创建 {@link TcTagPrefixes#dust} 前缀的物品。
     * </p>
     *
     * @param material       材料
     * @param materialAmount 材料数量（以mB为单位）
     * @return 粉末物品堆栈，如果不满足条件则返回 {@link ItemStack#EMPTY}
     * @throws NullPointerException 如果 {@code material} 为 null
     */
    public static ItemStack getDust(@NotNull Material material, long materialAmount) {
        if (!material.hasProperty(PropertyKey.DUST) || materialAmount <= 0) {
            return ItemStack.EMPTY;
        }
        if (materialAmount % M == 0 || materialAmount >= M * 16) {
            return get(TcTagPrefixes.dust, material, (int) (materialAmount / M));
        }
        return ItemStack.EMPTY;
    }

    /**
     * 从材料堆栈获取粉末物品堆栈。
     * <p>
     * 调用 {@link #getDust(Material, long)} 方法进行转换。
     * </p>
     *
     * @param materialStack 材料堆栈
     * @return 粉末物品堆栈
     * @throws NullPointerException 如果 {@code materialStack} 为 null
     */
    public static ItemStack getDust(@NotNull MaterialStack materialStack) {
        return getDust(materialStack.material(), materialStack.amount());
    }

    /**
     * 根据材料和材料数量获取锭物品堆栈。
     * <p>
     * 获取逻辑：
     * <ol>
     * <li>检查材料是否具有 {@link PropertyKey#INGOT} 属性且数量大于0</li>
     * <li>如果数量是 {@code M * 9} 的整数倍，返回方块形式</li>
     * <li>如果数量是 {@code M} 的整数倍或大于等于 {@code M * 16}，返回锭形式</li>
     * <li>如果 {@code 数量 * 9 >= M}，返回粒形式</li>
     * </ol>
     * 材料数量与物品数量的对应关系遵循1锭=9粒=1/9方块的规则。
     * </p>
     *
     * @param material       材料
     * @param materialAmount 材料数量（以mB为单位）
     * @return 锭/粒/方块物品堆栈，如果不满足条件则返回 {@link ItemStack#EMPTY}
     * @throws NullPointerException 如果 {@code material} 为 null
     */
    public static ItemStack getIngot(@NotNull Material material, long materialAmount) {
        if (!material.hasProperty(PropertyKey.INGOT) || materialAmount <= 0)
            return ItemStack.EMPTY;
        if (materialAmount % (M * 9) == 0)
            return get(TcTagPrefixes.block, material, (int) (materialAmount / (M * 9)));
        if (materialAmount % M == 0 || materialAmount >= M * 16)
            return get(TcTagPrefixes.ingot, material, (int) (materialAmount / M));
        else if ((materialAmount * 9) >= M)
            return get(TcTagPrefixes.nugget, material, (int) ((materialAmount * 9) / M));
        return ItemStack.EMPTY;
    }

    /**
     * 获取锭或粉末物品堆栈。
     * <p>
     * 优先尝试获取锭物品堆栈，如果失败则尝试获取粉末物品堆栈。
     * 如果两者都不存在，则返回 {@link ItemStack#EMPTY}。
     * </p>
     *
     * @param material       材料
     * @param materialAmount 材料数量
     * @return 锭或粉末物品堆栈
     */
    public static ItemStack getIngotOrDust(Material material, long materialAmount) {
        ItemStack ingotStack = getIngot(material, materialAmount);
        if (ingotStack != ItemStack.EMPTY) return ingotStack;
        return getDust(material, materialAmount);
    }

    /**
     * 从材料堆栈获取锭或粉末物品堆栈。
     *
     * @param materialStack 材料堆栈
     * @return 锭或粉末物品堆栈
     * @throws NullPointerException 如果 {@code materialStack} 为 null
     */
    public static ItemStack getIngotOrDust(@NotNull MaterialStack materialStack) {
        return getIngotOrDust(materialStack.material(), materialStack.amount());
    }

    /**
     * 从材料堆栈获取宝石物品堆栈。
     * <p>
     * 获取条件：
     * <ol>
     * <li>材料必须具有 {@link PropertyKey#GEM} 属性</li>
     * <li>材料未被 {@link TcTagPrefixes#gem} 前缀忽略</li>
     * <li>材料数量等于宝石前缀的标准材料数量</li>
     * </ol>
     * 如果满足宝石条件，返回宝石物品；否则返回粉末物品。
     * </p>
     *
     * @param materialStack 材料堆栈
     * @return 宝石或粉末物品堆栈
     */
    public static ItemStack getGem(@NotNull MaterialStack materialStack) {
        if (materialStack.material().hasProperty(PropertyKey.GEM) &&
                !TcTagPrefixes.gem.isIgnored(materialStack.material()) &&
                materialStack.amount() == TcTagPrefixes.gem.getMaterialAmount(materialStack.material())) {
            return get(TcTagPrefixes.gem, materialStack.material(), (int) (materialStack.amount() / M));
        }
        return getDust(materialStack);
    }

    /**
     * 获取物品对应的材料条目。
     * <p>
     * 该方法使用多级缓存策略：
     * <ol>
     * <li>首先检查已收集的缓存（{@link ItemMaterialData#ITEM_MATERIAL_ENTRY_COLLECTED}）</li>
     * <li>如果缓存为空，解析所有延迟条目并填充缓存</li>
     * <li>如果仍未找到，通过物品标签猜测材料条目</li>
     * <li>检查标签是否为父标签，避免错误匹配</li>
     * </ol>
     * 性能优化：避免多次调用 {@link ItemLike#asItem()}，对结果进行缓存。
     * </p>
     *
     * @param itemLike 物品
     * @return 对应的材料条目，如果未找到则返回 {@link MaterialEntry#NULL_ENTRY}
     * @throws NullPointerException 如果 {@code itemLike} 为 null
     */
    public static MaterialEntry getMaterialEntry(@NotNull ItemLike itemLike) {
        // asItem 调用较慢，避免多次调用
        var itemKey = itemLike.asItem();
        var materialEntry = ITEM_MATERIAL_ENTRY_COLLECTED.get(itemKey);

        if (materialEntry == null) {
            // 一次性解析所有延迟供应商，避免每次请求时的 O(n) 查找性能
            for (var entry : ITEM_MATERIAL_ENTRY) {
                ITEM_MATERIAL_ENTRY_COLLECTED.put(entry.getFirst().get().asItem(), entry.getSecond());
            }
            ITEM_MATERIAL_ENTRY.clear();

            // 如果没有预注册的条目，根据物品标签猜测条目
            materialEntry = ITEM_MATERIAL_ENTRY_COLLECTED.computeIfAbsent(itemKey, item -> {
                for (TagKey<Item> itemTag : item.asItem().builtInRegistryHolder().tags().toList()) {
                    MaterialEntry materialEntry1 = getMaterialEntry(itemTag);
                    // 检查是否为空标记且不是父标签
                    if (!materialEntry1.isEmpty() &&
                            materialEntry1.tagPrefix().getItemParentTags().stream().noneMatch(itemTag::equals)) {
                        return materialEntry1;
                    }
                }
                return MaterialEntry.NULL_ENTRY;
            });
        }
        return materialEntry;
    }

    /**
     * 获取物品标签对应的材料条目。
     * <p>
     * 使用LazyInit策略，首次调用时会遍历所有标签前缀和材料，
     * 建立标签到材料条目的完整映射。
     * 优化策略：从标签集合中移除已处理的标签，减少后续迭代时间。
     * </p>
     *
     * @param tag 物品标签
     * @return 对应的材料条目，如果未找到则返回 {@link MaterialEntry#NULL_ENTRY}
     */
    public static MaterialEntry getMaterialEntry(TagKey<Item> tag) {
        if (TAG_MATERIAL_ENTRY.isEmpty()) {
            // 如果映射为空，解析所有可能的标签到其值，以节省后续查找时间
            Set<TagKey<Item>> allItemTags = BuiltInRegistries.ITEM.getTagNames().collect(Collectors.toSet());
            for (TagPrefix prefix : TagPrefix.values()) {
                for (Material material : TcAPI.materialManager) {
                    prefix.getItemTags(material).stream()
                            .filter(allItemTags::contains)
                            .forEach(tagKey -> {
                                // 移除标签以使下一次迭代更快
                                allItemTags.remove(tagKey);
                                TAG_MATERIAL_ENTRY.put(tagKey, new MaterialEntry(prefix, material));
                            });
                }
            }
        }
        return TAG_MATERIAL_ENTRY.getOrDefault(tag, MaterialEntry.NULL_ENTRY);
    }

    /**
     * 获取材料条目对应的所有物品。
     * <p>
     * 查找逻辑：
     * <ol>
     * <li>检查缓存映射（{@link ItemMaterialData#MATERIAL_ENTRY_ITEM_MAP}）</li>
     * <li>通过标签系统查找注册物品</li>
     * <li>如果未找到且前缀有物品表且应生成物品，则从物品表获取</li>
     * </ol>
     * 返回的物品列表是不可修改的，且通过延迟供应商实现惰性加载。
     * </p>
     *
     * @param materialEntry 材料条目
     * @return 对应的物品列表，如果未找到则返回空列表
     * @throws NullPointerException 如果 {@code materialEntry} 为 null
     */
    public static List<ItemLike> getItems(@NotNull MaterialEntry materialEntry) {
        if (materialEntry.material().isNull()) return new ArrayList<>();
        return MATERIAL_ENTRY_ITEM_MAP.computeIfAbsent(materialEntry, entry -> {
            var items = new ArrayList<Supplier<? extends ItemLike>>();
            for (TagKey<Item> tag : getTags(entry.tagPrefix(), entry.material())) {
                for (Holder<Item> itemHolder : BuiltInRegistries.ITEM.getTagOrEmpty(tag)) {
                    items.add(itemHolder::value);
                }
            }
            TagPrefix prefix = entry.tagPrefix();
            if (items.isEmpty() && prefix.hasItemTable() && prefix.doGenerateItem(entry.material())) {
                return List.of(prefix.getItemFromTable(entry.material()));
            }
            return items;
        }).stream().map(Supplier::get).collect(Collectors.toList());
    }

    /**
     * 获取材料条目对应的物品堆栈。
     * <p>
     * 使用 {@link #getItems(MaterialEntry)} 获取物品列表，然后创建指定数量的物品堆栈。
     * 如果物品列表为空，返回 {@link ItemStack#EMPTY}。
     * </p>
     *
     * @param materialEntry 材料条目
     * @param size          堆栈大小
     * @return 对应的物品堆栈
     */
    public static ItemStack get(MaterialEntry materialEntry, int size) {
        var list = getItems(materialEntry);
        if (list.isEmpty()) return ItemStack.EMPTY;
        var stack = list.getFirst().asItem().getDefaultInstance();
        stack.setCount(size);
        return stack;
    }

    /**
     * 获取标签前缀和材料对应的物品堆栈。
     * <p>
     * 便捷方法，创建材料条目后调用 {@link #get(MaterialEntry, int)}。
     * </p>
     *
     * @param orePrefix 标签前缀
     * @param material  材料
     * @param stackSize 堆栈大小
     * @return 对应的物品堆栈
     */
    public static ItemStack get(TagPrefix orePrefix, Material material, int stackSize) {
        return get(new MaterialEntry(orePrefix, material), stackSize);
    }

    /**
     * 获取标签前缀和材料对应的单个物品堆栈。
     *
     * @param orePrefix 标签前缀
     * @param material  材料
     * @return 对应的物品堆栈（数量为1）
     */
    public static ItemStack get(TagPrefix orePrefix, Material material) {
        return get(orePrefix, material, 1);
    }

    /**
     * 获取材料条目对应的所有方块。
     * <p>
     * 通过标签系统查找与材料条目相关的所有方块。
     * 结果会被缓存以提高后续查询性能。
     * </p>
     *
     * @param materialEntry 材料条目
     * @return 对应的方块列表，如果未找到则返回空列表
     * @throws NullPointerException 如果 {@code materialEntry} 为 null
     */
    public static List<Block> getBlocks(@NotNull MaterialEntry materialEntry) {
        if (materialEntry.isEmpty()) return Collections.emptyList();
        return MATERIAL_ENTRY_BLOCK_MAP.computeIfAbsent(materialEntry, entry -> {
            var blocks = new ArrayList<Supplier<? extends Block>>();
            for (var tag : getTags(materialEntry.tagPrefix(), entry.material())) {
                var blockTag = TagKey.create(Registries.BLOCK, tag.location());
                for (Holder<Block> itemHolder : BuiltInRegistries.BLOCK.getTagOrEmpty(blockTag)) {
                    blocks.add(itemHolder::value);
                }
            }
            return blocks;
        }).stream().map(Supplier::get).collect(Collectors.toList());
    }

    /**
     * 获取材料条目对应的第一个方块。
     * <p>
     * 使用 {@link #getBlocks(MaterialEntry)} 获取方块列表，返回第一个元素。
     * 主要用于需要单个方块的场景。
     * </p>
     *
     * @param materialEntry 材料条目
     * @return 对应的方块，如果未找到则返回 null
     */
    @Nullable
    public static Block getBlock(MaterialEntry materialEntry) {
        var list = getBlocks(materialEntry);
        if (list.isEmpty()) return null;
        return list.getFirst();
    }

    /**
     * 获取标签前缀和材料对应的第一个方块。
     *
     * @param orePrefix 标签前缀
     * @param material  材料
     * @return 对应的方块
     */
    @Nullable
    public static Block getBlock(TagPrefix orePrefix, Material material) {
        return getBlock(new MaterialEntry(orePrefix, material));
    }

    /**
     * 获取标签前缀和材料对应的方块标签。
     * <p>
     * 返回该组合的第一个方块标签，用于标签查询和匹配。
     * </p>
     *
     * @param orePrefix 标签前缀
     * @param material  材料
     * @return 对应的方块标签，如果不存在则返回 null
     * @throws NullPointerException 如果参数为 null
     */
    @Nullable
    public static TagKey<Block> getBlockTag(@NotNull TagPrefix orePrefix, @NotNull Material material) {
        var tags = orePrefix.getBlockTags(material);
        if (!tags.isEmpty()) {
            return tags.getFirst();
        }
        return null;
    }

    /**
     * 获取标签前缀和材料对应的物品标签。
     *
     * @param orePrefix 标签前缀
     * @param material  材料
     * @return 对应的物品标签，如果不存在则返回 null
     * @throws NullPointerException 如果参数为 null
     */
    @Nullable
    public static TagKey<Item> getTag(@NotNull TagPrefix orePrefix, @NotNull Material material) {
        var tags = orePrefix.getItemTags(material);
        if (!tags.isEmpty()) {
            return tags.getFirst();
        }
        return null;
    }

    /**
     * 获取标签前缀和材料对应的所有物品标签。
     * <p>
     * 返回不可修改的标签列表，确保调用者不会意外修改内部数据结构。
     * </p>
     *
     * @param orePrefix 标签前缀
     * @param material  材料
     * @return 物品标签的不可修改列表
     * @throws NullPointerException 如果参数为 null
     */
    public static @Unmodifiable List<TagKey<Item>> getTags(@NotNull TagPrefix orePrefix, @NotNull Material material) {
        return orePrefix.getItemTags(material);
    }

    /**
     * 获取所有物品及其材料信息的配对列表。
     * <p>
     * 用于调试、数据导出或需要完整物品材料信息的场景。
     * 返回的列表包含所有已注册物品及其对应的材料信息。
     * </p>
     *
     * @return 物品堆栈和材料信息的配对列表
     */
    public static @NotNull List<Pair<ItemStack, ItemMaterialInfo>> getAllItemInfos() {
        List<Pair<ItemStack, ItemMaterialInfo>> f = new ArrayList<>();
        for (var entry : ITEM_MATERIAL_INFO.entrySet()) {
            f.add(Pair.of(new ItemStack(entry.getKey().asItem()), entry.getValue()));
        }
        return f;
    }

    /**
     * 根据方块状态获取对应的矿石前缀。
     * <p>
     * 通过反向查找矿石注册表，找到与给定方块状态匹配的矿石前缀。
     * 主要用于矿石识别和自动化处理。
     * </p>
     *
     * @param state 方块状态
     * @return 对应的矿石前缀，如果未找到则返回空 Optional
     */
    public static @NotNull Optional<TagPrefix> getOrePrefix(BlockState state) {
        return ORES_INVERSE.entrySet().stream()
                .filter(entry -> entry.getKey().get().equals(state))
                .map(Map.Entry::getValue)
                .findFirst();
    }
}
