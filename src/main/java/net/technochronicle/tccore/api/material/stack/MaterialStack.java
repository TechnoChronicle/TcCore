package net.technochronicle.tccore.api.material.stack;

import net.technochronicle.tclib.utils.FormattingUtil;

import net.technochronicle.tccore.api.material.Material;
import net.technochronicle.tccore.data.material.TcMaterials;

import org.jetbrains.annotations.NotNull;

import java.util.Map;
import java.util.WeakHashMap;

/**
 * 表示具有特定数量的不可变材料堆栈。
 * <p>
 * {@code MaterialStack} 将 {@link Material} 与数量配对。
 * 通常用于配方、库存和材料处理操作。
 * 该类是一个 {@code record}，因此基于其组件自动提供 {@code equals()}、{@code hashCode()} 和 {@code toString()} 的实现。
 * </p>
 * <p>
 * 此类通过 {@link #EMPTY} 提供静态空实例，并支持从字符串表示解析，且为了提高性能而进行了缓存。
 * </p>
 *
 * @param material 此堆栈中包含的材料；永不为 null
 * @param amount   材料的数量；可能为零或正数
 * @see Material
 * @see TcMaterials
 * @see #EMPTY
 */
public record MaterialStack(@NotNull Material material, long amount) {

    /**
     * 空材料堆栈实例。
     * <p>
     * 此堆栈使用 {@link TcMaterials#NULL} 作为其材料，数量为 0。
     * 建议使用此常量而不是创建新的空堆栈。
     * </p>
     */
    public static final MaterialStack EMPTY = new MaterialStack(TcMaterials.NULL, 0);

    /**
     * 已解析材料堆栈的缓存，以避免冗余解析。
     * <p>
     * 使用 {@link WeakHashMap} 允许在解析的字符串在其他地方不再使用时对缓存条目进行垃圾回收。
     * </p>
     */
    private static final Map<String, MaterialStack> PARSE_CACHE = new WeakHashMap<>();

    /**
     * 创建此材料堆栈的副本。
     * <p>
     * 由于 {@code MaterialStack} 是不可变的，如果堆栈为空（由 {@link #isEmpty()} 定义），则此方法返回相同实例，
     * 否则创建具有相同材料和数量的新实例。
     * </p>
     *
     * @return 此材料堆栈的副本；如果为空，则可能是相同实例
     */
    public MaterialStack copy() {
        if (isEmpty()) return EMPTY;
        return new MaterialStack(material, amount);
    }

    /**
     * 将字符串表示解析为 {@code MaterialStack}。
     * <p>
     * 字符串格式可以是以下之一：
     * <ul>
     * <li>{@code "MaterialName"} - 单个该材料</li>
     * <li>{@code "Nx MaterialName"} - N 个该材料（例如，"3x Iron"）</li>
     * </ul>
     * 解析器对材料名称区分大小写，并期望有一个可选的计数前缀，用空格分隔。字符串周围的空格会被修剪。
     * </p>
     * <p>
     * 结果缓存在弱缓存中，以提高重复解析相同字符串时的性能。
     * </p>
     *
     * @param str 要解析的字符串；可能为 null 或空
     * @return 解析后的材料堆栈；永不为 null，但如果找不到材料，则可能为 {@link #EMPTY}
     * @throws NumberFormatException 如果计数前缀不是有效的整数
     * @see TcMaterials#get(String)
     * @see #toString()
     */
    public static MaterialStack fromString(CharSequence str) {
        String trimmed = str.toString().trim();
        String copy = trimmed;

        var cached = PARSE_CACHE.get(trimmed);

        if (cached != null) {
            return cached;
        }

        var count = 1;
        var spaceIndex = copy.indexOf(' ');

        if (spaceIndex >= 2 && copy.indexOf('x') == spaceIndex - 1) {
            count = Integer.parseInt(copy.substring(0, spaceIndex - 1));
            copy = copy.substring(spaceIndex + 1);
        }

        cached = new MaterialStack(TcMaterials.get(copy), count);
        PARSE_CACHE.put(trimmed, cached);
        return cached;
    }

    /**
     * 检查此材料堆栈是否为空。
     * <p>
     * 堆栈在以下情况下被视为空：
     * <ul>
     * <li>材料为 {@link TcMaterials#NULL}，或</li>
     * <li>数量小于 1</li>
     * </ul>
     * </p>
     *
     * @return 如果此堆栈为空则为 {@code true}，否则为 {@code false}
     */
    public boolean isEmpty() {
        return this.material == TcMaterials.NULL || this.amount < 1;
    }

    /**
     * 返回此材料堆栈的字符串表示。
     * <p>
     * 格式取决于材料的属性：
     * <ul>
     * <li>如果材料没有化学式或化学式为空：{@code "?"}</li>
     * <li>如果材料有多个成分：{@code "(formula)"}</li>
     * <li>否则：化学式</li>
     * </ul>
     * 如果数量大于 1，则使用 {@link FormattingUtil#toSmallDownNumbers(String)} 以下标格式附加数量。
     * </p>
     * <p>
     * 如果堆栈 {@link #isEmpty()}，则返回空字符串。
     * </p>
     *
     * @return 此材料堆栈的格式化字符串表示
     * @see Material#getChemicalFormula()
     * @see Material#getMaterialComponents()
     * @see FormattingUtil#toSmallDownNumbers(String)
     */
    @Override
    public @NotNull String toString() {
        String string = "";
        if (this.isEmpty()) return "";
        if (material.getChemicalFormula() == null || material.getChemicalFormula().isEmpty()) {
            string += "?";
        } else if (material.getMaterialComponents().size() > 1) {
            string += '(' + material.getChemicalFormula() + ')';
        } else {
            string += material.getChemicalFormula();
        }
        if (amount > 1) {
            string += FormattingUtil.toSmallDownNumbers(Long.toString(amount));
        }
        return string;
    }
}
