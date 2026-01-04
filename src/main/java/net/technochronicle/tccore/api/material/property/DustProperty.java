package net.technochronicle.tccore.api.material.property;

import lombok.Getter;

/**
 * 材料的基本性质 如挖掘等级,燃烧时间
 */
public class DustProperty implements IMaterialProperty {

    /**
     * 采集此材料方块所需的工具等级。
     * <p>
     * 默认值：2（铁制工具级别）。
     */
    @Getter
    private int harvestLevel;

    /**
     * 此材料作为熔炉燃料时的燃烧时间。
     * 零或负值表示此材料不能用作燃料。
     * <p>
     * 默认值：0。
     */
    @Getter
    private int burnTime;

    /**
     * 构造方法
     *
     * @param harvestLevel 挖掘等级
     * @param burnTime     燃烧时间
     */
    public DustProperty(int harvestLevel, int burnTime) {
        this.harvestLevel = harvestLevel;
        this.burnTime = burnTime;
    }

    /**
     * 默认属性构造方法。
     */
    public DustProperty() {
        this(2, 0);
    }

    public void setHarvestLevel(int harvestLevel) {
        if (harvestLevel <= 0) throw new IllegalArgumentException("Harvest Level must be greater than zero!");
        this.harvestLevel = harvestLevel;
    }

    public void setBurnTime(int burnTime) {
        if (burnTime < 0) throw new IllegalArgumentException("Burn Time cannot be negative!");
        this.burnTime = burnTime;
    }

    @Override
    public void verifyProperty(MaterialProperties properties) {}
}
