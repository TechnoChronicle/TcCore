package net.technochronicle.tccore.api.tag;

import net.technochronicle.tclib.TcUtil;

import net.minecraft.core.Registry;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.material.Fluid;

import org.jetbrains.annotations.NotNull;

public class TagUtil {

    /**
     * 在 {@code c} 或 {@code minecraft} 命名空间下创建标签
     *
     * @param vanilla 是否使用原版命名空间替代通用命名空间
     * @return 标签 {@code #c:path} 或 {@code #minecraft:path}
     */
    public static <T> @NotNull TagKey<T> createTag(ResourceKey<? extends Registry<T>> registryKey, String path,
                                                   boolean vanilla) {
        if (vanilla) return TagKey.create(registryKey, ResourceLocation.withDefaultNamespace(path));
        return TagKey.create(registryKey, ResourceLocation.fromNamespaceAndPath("c", path));
    }

    /**
     * 在 {@code tccore} 命名空间下创建标签
     *
     * @return {@code #tccore:path}
     */
    public static <T> @NotNull TagKey<T> createModTag(ResourceKey<? extends Registry<T>> registryKey, String path) {
        return TagKey.create(registryKey, TcUtil.id(path));
    }

    /**
     * 在 {@code c} 命名空间下创建方块标签
     *
     * @return 方块标签 {@code #c:path}
     */
    public static @NotNull TagKey<Block> createBlockTag(String path) {
        return createTag(Registries.BLOCK, path, false);
    }

    /**
     * 在 {@code c} 或 {@code minecraft} 命名空间下创建方块标签
     *
     * @param vanilla 是否使用原版命名空间替代通用命名空间
     * @return 方块标签 {@code #c:path} 或 {@code #minecraft:path}
     */
    public static @NotNull TagKey<Block> createBlockTag(String path, boolean vanilla) {
        return createTag(Registries.BLOCK, path, vanilla);
    }

    /**
     * 在 {@code tccore} 命名空间下创建方块标签
     *
     * @return 方块标签 {@code #tccore:path}
     */
    public static @NotNull TagKey<Block> createModBlockTag(String path) {
        return createModTag(Registries.BLOCK, path);
    }

    /**
     * 在 {@code c} 命名空间下创建物品标签
     *
     * @return 物品标签 {@code #c:path}
     */
    public static @NotNull TagKey<Item> createItemTag(String path) {
        return createTag(Registries.ITEM, path, false);
    }

    /**
     * 在 {@code c} 或 {@code minecraft} 命名空间下创建物品标签
     *
     * @param vanilla 是否使用原版命名空间替代通用命名空间
     * @return 物品标签 {@code #c:path} 或 {@code #minecraft:path}
     */
    public static @NotNull TagKey<Item> createItemTag(String path, boolean vanilla) {
        return createTag(Registries.ITEM, path, vanilla);
    }

    /**
     * 在 {@code tccore} 命名空间下创建物品标签
     *
     * @return 物品标签 {@code #tccore:path}
     */
    public static @NotNull TagKey<Item> createModItemTag(String path) {
        return createModTag(Registries.ITEM, path);
    }

    /**
     * 在 {@code c} 命名空间下创建流体标签
     *
     * @return 流体标签 {@code #c:path}
     */
    public static @NotNull TagKey<Fluid> createFluidTag(String path) {
        return createTag(Registries.FLUID, path, false);
    }
}
