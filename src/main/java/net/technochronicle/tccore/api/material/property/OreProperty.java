package net.technochronicle.tccore.api.material.property;

import net.technochronicle.tccore.api.material.Material;
import net.technochronicle.tccore.data.material.TcMaterials;

import net.minecraft.util.Mth;

import com.mojang.datafixers.util.Pair;
import lombok.Getter;
import lombok.Setter;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

/**
 * 矿处属性
 */
public class OreProperty implements IMaterialProperty {

    /**
     * 矿物副产品列表。
     * <p>
     * 默认值：无，即仅包含此属性对应的材料。
     */
    @Getter
    private final List<Material> oreByProducts = new ArrayList<>();

    /**
     * 破碎过程中破碎矿石产出量的倍数。
     * <p>
     * 默认值：1（无倍数）。
     */
    @Getter
    @Setter
    private int oreMultiplier;

    /**
     * 破碎过程中副产品产出量的倍数。
     * <p>
     * 默认值：1（无倍数）。
     */
    @Getter
    @Setter
    private int byProductMultiplier;

    /**
     * 矿物方块是否使用发光纹理。
     * <p>
     * 默认值：false。
     */
    @Getter
    @Setter
    private boolean emissive;

    /**
     * 此矿物直接冶炼得到的结果材料。
     * <p>
     * 该材料必须具有粉尘属性。
     * 默认值：无。
     */
    @Getter
    @Setter
    @NotNull
    private Material directSmeltResult = TcMaterials.NULL;

    /**
     * 此矿物应在此材料中进行洗涤以获得额外产出。
     * <p>
     * 该材料必须具有流体属性。
     * 默认值：无。
     */
    @Setter
    @NotNull
    private Material washedIn = TcMaterials.NULL;

    /**
     * 在化学浴中洗涤此矿物所需的材料量。
     * <p>
     * 默认值：100 mb
     */
    private int washedAmount = 100;

    /**
     * 在电磁分离过程中，此矿物将被分离为此材料和此字段指定的材料。
     * 限制为2种材料。
     * <p>
     * 材料必须具有粉尘属性。
     * 默认值：无。
     */
    @Getter
    private final List<Material> separatedInto = new ArrayList<>();

    /**
     * 构造方法
     *
     * @param oreMultiplier       矿石产出倍数
     * @param byProductMultiplier 副产品产出倍数
     */
    public OreProperty(int oreMultiplier, int byProductMultiplier) {
        this.oreMultiplier = oreMultiplier;
        this.byProductMultiplier = byProductMultiplier;
        this.emissive = false;
    }

    /**
     * 构造方法
     *
     * @param oreMultiplier       矿石产出倍数
     * @param byProductMultiplier 副产品产出倍数
     * @param emissive            是否发光
     */
    public OreProperty(int oreMultiplier, int byProductMultiplier, boolean emissive) {
        this.oreMultiplier = oreMultiplier;
        this.byProductMultiplier = byProductMultiplier;
        this.emissive = emissive;
    }

    /**
     * 默认值构造方法。
     */
    public OreProperty() {
        this(1, 1);
    }

    /**
     * 设置洗涤材料和用量
     *
     * @param m            洗涤材料
     * @param washedAmount 用量
     */
    public void setWashedIn(Material m, int washedAmount) {
        this.washedIn = m;
        this.washedAmount = washedAmount;
    }

    /**
     * 获取洗涤材料和用量
     *
     * @return 洗涤材料和用量的配对
     */
    public Pair<Material, Integer> getWashedIn() {
        return Pair.of(this.washedIn, this.washedAmount);
    }

    /**
     * 设置分离产物
     *
     * @param materials 分离得到的材料
     */
    public void setSeparatedInto(Material... materials) {
        this.separatedInto.addAll(Arrays.asList(materials));
    }

    /**
     * 设置矿物副产品
     *
     * @param materials 用作副产品的材料
     */
    public void setOreByProducts(@NotNull Material @NotNull... materials) {
        setOreByProducts(Arrays.asList(materials));
    }

    /**
     * 设置矿物副产品
     *
     * @param materials 用作副材料的集合
     */
    public void setOreByProducts(@NotNull Collection<@NotNull Material> materials) {
        this.oreByProducts.clear();
        this.oreByProducts.addAll(materials);
    }

    /**
     * 添加矿物副产品
     *
     * @param materials 要添加为副产品的材料
     */
    public void addOreByProducts(@NotNull Material @NotNull... materials) {
        this.oreByProducts.addAll(Arrays.asList(materials));
    }

    @NotNull
    public final Material getOreByProduct(int index) {
        if (this.oreByProducts.isEmpty()) return TcMaterials.NULL;
        return this.oreByProducts.get(Mth.clamp(index, 0, this.oreByProducts.size() - 1));
    }

    @NotNull
    public final Material getOreByProduct(int index, @NotNull Material fallback) {
        Material material = getOreByProduct(index);
        return !material.isNull() ? material : fallback;
    }

    @Override
    public void verifyProperty(MaterialProperties properties) {
        properties.ensureSet(PropertyKey.DUST, true);

        if (!directSmeltResult.isNull())
            directSmeltResult.getProperties().ensureSet(PropertyKey.DUST, true);
        if (!washedIn.isNull())
            washedIn.getProperties().ensureSet(PropertyKey.FLUID, true);
        separatedInto.forEach(m -> m.getProperties().ensureSet(PropertyKey.DUST, true));
        oreByProducts.forEach(m -> m.getProperties().ensureSet(PropertyKey.DUST, true));
    }
}
