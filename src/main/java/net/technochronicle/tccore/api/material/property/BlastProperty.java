package net.technochronicle.tccore.api.material.property;

import lombok.Getter;
import lombok.Setter;

/**
 * 材料物理性质,熔炼信息
 * 待重新设定
 */
public class BlastProperty implements IMaterialProperty {

    /**
     * 此材料的高炉冶炼温度。
     * 如果低于1000K，将同时添加原始高炉配方。
     * 如果高于1750K，将同时添加热锭及其真空冷冻机配方。
     * <p>
     * 如果具有此属性的材料有流体形态，且流体温度为默认值，
     * 则其温度将被设置为此值。
     */
    @Getter
    private int blastTemperature;

    /**
     * EBF（电力高炉）配方的持续时间，覆盖默认行为。
     * <p>
     * 默认值：-1，表示持续时间将为：material.getAverageMass() * blastTemperature / 50
     */
    @Setter
    @Getter
    private int durationOverride = -1;

    /**
     * EBF（电力高炉）配方的EU/t（能耗），覆盖默认行为。
     * <p>
     * 默认值：-1，表示EU/t将为120。
     */
    @Setter
    @Getter
    private int EUtOverride = -1;

    /**
     * 真空冷冻机配方的持续时间，覆盖默认行为。
     * <p>
     * 默认值：-1，表示持续时间将为：material.getMass() * 3
     */
    @Setter
    @Getter
    private int vacuumDurationOverride = -1;

    /**
     * 真空冷冻机配方（如果需要）的EU/t（能耗），覆盖默认行为。
     * <p>
     * 默认值：-1，表示EU/t将为120。
     */
    @Setter
    @Getter
    private int vacuumEUtOverride = -1;

    public BlastProperty(int blastTemperature) {
        this.blastTemperature = blastTemperature;
    }

    public BlastProperty(int blastTemperature, int eutOverride, int durationOverride,
                         int vacuumEUtOverride, int vacuumDurationOverride) {
        this.blastTemperature = blastTemperature;
        this.EUtOverride = eutOverride;
        this.durationOverride = durationOverride;
        this.vacuumEUtOverride = vacuumEUtOverride;
        this.vacuumDurationOverride = vacuumDurationOverride;
    }

    /**
     * Default property constructor.
     */
    public BlastProperty() {
        this(0);
    }

    public void setBlastTemperature(int blastTemp) {
        if (blastTemp <= 0) throw new IllegalArgumentException("Blast Temperature must be greater than zero!");
        this.blastTemperature = blastTemp;
    }

    @Override
    public void verifyProperty(MaterialProperties properties) {
        properties.ensureSet(PropertyKey.INGOT, true);
    }

    public static class Builder {

        private int temp;
        private int eutOverride = -1;
        private int durationOverride = -1;
        private int vacuumEUtOverride = -1;
        private int vacuumDurationOverride = -1;

        public Builder() {}

        public Builder temp(int temperature) {
            this.temp = temperature;
            return this;
        }

        public Builder blastStats(int eutOverride) {
            this.eutOverride = eutOverride;
            return this;
        }

        public Builder blastStats(int eutOverride, int durationOverride) {
            this.eutOverride = eutOverride;
            this.durationOverride = durationOverride;
            return this;
        }

        public Builder vacuumStats(int eutOverride) {
            this.vacuumEUtOverride = eutOverride;
            return this;
        }

        public Builder vacuumStats(int eutOverride, int durationOverride) {
            this.vacuumEUtOverride = eutOverride;
            this.vacuumDurationOverride = durationOverride;
            return this;
        }

        public BlastProperty build() {
            return new BlastProperty(temp, eutOverride, durationOverride, vacuumEUtOverride,
                    vacuumDurationOverride);
        }
    }
}
