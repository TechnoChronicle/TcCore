package net.technochronicle.tccore.api.material.info;

import net.technochronicle.tccore.TcCore;
import net.technochronicle.tccore.api.material.Material;
import net.technochronicle.tccore.api.material.property.PropertyKey;

import it.unimi.dsi.fastutil.objects.ObjectOpenHashSet;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

/**
 * 表示定义材料特殊特性或行为的材料标志。
 * 标志可以依赖于其他标志，并要求特定的材料属性。
 * <p>
 * 标志在全局注册表中注册，可以通过名称检索。
 * 通常使用 {@link Builder} 构建器模式进行构建。
 * </p>
 *
 * @see Material
 * @see PropertyKey
 * @see Builder
 */
public class MaterialFlag {

    /**
     * 所有材料标志的全局注册表。
     */
    private static final Set<MaterialFlag> FLAG_REGISTRY = new HashSet<>();

    /**
     * 此标志的唯一名称。
     */
    private final String name;

    /**
     * 应用此标志时必须存在的标志集合。
     */
    private final Set<MaterialFlag> requiredFlags;

    /**
     * 应用此标志时材料必须具有的属性集合。
     */
    private final Set<PropertyKey<?>> requiredProperties;

    /**
     * 使用指定参数构造新的 MaterialFlag。
     *
     * @param name               标志的唯一名称
     * @param requiredFlags      存在此标志时所需的依赖标志
     * @param requiredProperties 存在此标志时所需的属性
     */
    private MaterialFlag(String name, Set<MaterialFlag> requiredFlags, Set<PropertyKey<?>> requiredProperties) {
        this.name = name;
        this.requiredFlags = requiredFlags;
        this.requiredProperties = requiredProperties;
        FLAG_REGISTRY.add(this);
    }

    /**
     * 验证材料是否满足此标志的所有要求。
     * <p>
     * 检查所需属性，并递归验证所有依赖标志。
     * 对于任何缺失的要求，记录警告信息。
     * </p>
     *
     * @param material 要验证的材料
     * @return 包含此标志及其所有已通过材料验证的传递性依赖的集合
     * @throws NullPointerException 如果材料为 null
     */
    protected Set<MaterialFlag> verifyFlag(Material material) {
        requiredProperties.forEach(key -> {
            if (!material.hasProperty(key)) {
                TcCore.LOGGER.warn("材料 {} 不具有标志 {} 所需的属性 {}！",
                        material.getUnlocalizedName(), this.name, key.toString());
            }
        });

        Set<MaterialFlag> thisAndDependencies = new HashSet<>(requiredFlags);
        requiredFlags.stream()
                .map(f -> f.verifyFlag(material))
                .forEach(thisAndDependencies::addAll);

        return thisAndDependencies;
    }

    /**
     * 返回此标志的名称。
     *
     * @return 标志名称
     */
    @Override
    public String toString() {
        return this.name;
    }

    /**
     * 通过名称从注册表中检索标志。
     * <p>
     * 搜索不区分大小写。
     * </p>
     *
     * @param name 要查找的标志名称
     * @return 找到的标志，如果不存在该名称的标志则返回 {@code null}
     */
    public static MaterialFlag getByName(String name) {
        return FLAG_REGISTRY.stream().filter(f -> f.toString().equalsIgnoreCase(name)).findFirst().orElse(null);
    }

    /**
     * 将此标志与另一对象进行相等性比较。
     * <p>
     * 如果两个标志具有相同的名称，则认为它们相等。
     * </p>
     *
     * @param o 要比较的对象
     * @return 如果对象相等则返回 {@code true}，否则返回 {@code false}
     */
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        MaterialFlag that = (MaterialFlag) o;
        return name.equals(that.name);
    }

    /**
     * 根据名称返回此标志的哈希码值。
     *
     * @return 此标志的哈希码值
     */
    @Override
    public int hashCode() {
        return name.hashCode();
    }

    /**
     * 用于通过流畅API创建 {@link MaterialFlag} 实例的构建器。
     * <p>
     * 此构建器允许在构造最终不可变的 MaterialFlag 实例之前指定所需的标志和属性。
     * </p>
     *
     * @see MaterialFlag
     */
    public static class Builder {

        /**
         * 正在构建的标志的名称。
         */
        final String name;

        /**
         * 正在构建的标志所需的依赖标志。
         */
        final Set<MaterialFlag> requiredFlags = new ObjectOpenHashSet<>();

        /**
         * 正在构建的标志所需的属性。
         */
        final Set<PropertyKey<?>> requiredProperties = new ObjectOpenHashSet<>();

        /**
         * 为具有指定名称的标志创建一个新的构建器。
         *
         * @param name 要构建的标志的名称
         * @throws NullPointerException 如果名称为 null
         */
        public Builder(String name) {
            this.name = name;
        }

        /**
         * 添加应用此标志时必须存在的依赖标志。
         * <p>
         * 应用此标志时，材料必须具有所有指定的依赖标志。
         * </p>
         *
         * @param flags 所需的依赖标志
         * @return 此构建器以支持方法链式调用
         * @throws NullPointerException 如果 flags 或任何元素为 null
         */
        public Builder requireFlags(MaterialFlag... flags) {
            requiredFlags.addAll(Arrays.asList(flags));
            return this;
        }

        /**
         * 添加应用此标志时材料必须具有的属性。
         * <p>
         * 应用此标志时，材料必须具有所有指定的属性。
         * </p>
         *
         * @param propertyKeys 所需的属性
         * @return 此构建器以支持方法链式调用
         * @throws NullPointerException 如果 propertyKeys 或任何元素为 null
         */
        public Builder requireProps(PropertyKey<?>... propertyKeys) {
            requiredProperties.addAll(Arrays.asList(propertyKeys));
            return this;
        }

        /**
         * 使用此构建器中指定的配置构造并注册一个新的 {@link MaterialFlag}。
         * <p>
         * 一旦构建完成，标志将自动添加到全局注册表中，并且无法再修改。
         * </p>
         *
         * @return 新创建的 MaterialFlag
         * @throws IllegalStateException 如果已存在具有相同名称的标志
         */
        public MaterialFlag build() {
            return new MaterialFlag(name, requiredFlags, requiredProperties);
        }
    }
}
