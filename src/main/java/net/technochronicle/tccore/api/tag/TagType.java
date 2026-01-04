package net.technochronicle.tccore.api.tag;

import net.technochronicle.tclib.utils.FormattingUtil;

import net.technochronicle.tccore.api.material.Material;

import net.minecraft.Util;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;

import lombok.Getter;
import org.jetbrains.annotations.NotNull;

import java.util.function.BiFunction;
import java.util.function.Predicate;

public class TagType {

    private final String tagPath;
    @Getter
    private boolean isParentTag = false;
    // this is now memoized because creating tagprefix keys interns them and that's slow
    private BiFunction<TagPrefix, Material, TagKey<Item>> formatter;
    private Predicate<Material> filter;

    private TagType(String tagPath) {
        this.tagPath = tagPath;
    }

    // formatter:off
    /**
     * 使用指定的路径创建一个标签，采用"默认"格式化器，意味着
     * 路径中包含1个"%s"格式化字符，用于材料名称。
     */
    public static @NotNull TagType withDefaultFormatter(String tagPath, boolean isVanilla) {
        TagType type = new TagType(tagPath);
        type.formatter = Util.memoize((prefix, mat) -> TagUtil.createItemTag(type.tagPath.formatted(mat.getName()), isVanilla));
        return type;
    }

    /**
     * 使用指定的路径创建一个标签，采用"默认"格式化器，意味着
     * 路径中包含2个"%s"格式化字符，第一个是前缀名称，
     * 第二个是材料名称。
     */
    public static @NotNull TagType withPrefixFormatter(String tagPath) {
        TagType type = new TagType(tagPath);
        type.formatter = Util.memoize((prefix, mat) -> TagUtil.createItemTag(type.tagPath.formatted(FormattingUtil.toLowerCaseUnderscore(prefix.name), mat.getName())));
        return type;
    }

    /**
     * 使用指定的路径创建一个标签，采用"默认"格式化器，意味着
     * 路径中包含1个"%s"格式化字符，用于前缀名称。
     */
    public static @NotNull TagType withPrefixOnlyFormatter(String tagPath) {
        TagType type = new TagType(tagPath);
        type.formatter = Util.memoize((prefix, mat) -> TagUtil.createItemTag(type.tagPath.formatted(FormattingUtil.toLowerCaseUnderscore(prefix.name))));
        type.isParentTag = true;
        return type;
    }

    public static @NotNull TagType withNoFormatter(String tagPath, boolean isVanilla) {
        TagType type = new TagType(tagPath);
        type.formatter = Util.memoize((prefix, material) -> TagUtil.createItemTag(type.tagPath, isVanilla));
        type.isParentTag = true;
        return type;
    }

    public static @NotNull TagType withCustomFormatter(String tagPath, BiFunction<TagPrefix, Material, TagKey<Item>> formatter) {
        TagType type = new TagType(tagPath);
        type.formatter = Util.memoize(formatter);
        return type;
    }

    public static @NotNull TagType withCustomFilter(String tagPath, boolean isVanilla, Predicate<Material> filter) {
        TagType type = new TagType(tagPath);
        type.filter = filter;
        type.formatter = Util.memoize((prefix, material) -> TagUtil.createItemTag(type.tagPath, isVanilla));
        return type;
    }
    // spotless:on

    public TagKey<Item> getTag(TagPrefix prefix, @NotNull Material material) {
        if (filter != null && !material.isNull() && !filter.test(material)) return null;
        return formatter.apply(prefix, material);
    }
}
