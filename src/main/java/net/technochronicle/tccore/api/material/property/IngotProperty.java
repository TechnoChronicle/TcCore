package net.technochronicle.tccore.api.material.property;

import net.technochronicle.tccore.api.material.Material;
import net.technochronicle.tccore.data.material.TcMaterials;

import lombok.Getter;
import lombok.Setter;
import org.jetbrains.annotations.NotNull;

/**
 * 锭类材料信息
 * 等待重新设定
 */
public class IngotProperty implements IMaterialProperty {

    /**
     * 指定此材料部件加热时转变为何种材料
     */
    @Getter
    @Setter
    @NotNull
    private Material smeltingInto = TcMaterials.NULL;

    /**
     * 指定此材料部件在电弧炉中加热时转变为何种材料
     */
    @Getter
    @Setter
    @NotNull
    private Material arcSmeltingInto = TcMaterials.NULL;

    /**
     * 指定此材料破碎后得到何种材料。
     * <p>
     * 默认值：此材料本身。
     */
    @Getter
    @Setter
    @NotNull
    private Material macerateInto = TcMaterials.NULL;

    /**
     * 此材料磁化后获得的材料
     */
    @Getter
    @Setter
    @NotNull
    private Material magneticMaterial = TcMaterials.NULL;

    @Override
    public void verifyProperty(MaterialProperties properties) {
        // 确保材料具有DUST属性
        properties.ensureSet(PropertyKey.DUST, true);

        // 检查材料不能同时具有INGOT和GEM属性
        if (properties.hasProperty(PropertyKey.GEM)) {
            throw new IllegalStateException(
                    "材料 " + properties.getMaterial() +
                            " 同时具有Ingot和Gem属性，这是不允许的！");
        }

        // 设置默认值：如果未指定加热转变材料，则默认为材料本身
        if (smeltingInto.isNull()) smeltingInto = properties.getMaterial();
        else smeltingInto.getProperties().ensureSet(PropertyKey.INGOT, true);

        // 设置默认值：如果未指定电弧炉加热转变材料，则默认为材料本身
        if (arcSmeltingInto.isNull()) arcSmeltingInto = properties.getMaterial();
        else arcSmeltingInto.getProperties().ensureSet(PropertyKey.INGOT, true);

        // 设置默认值：如果未指定破碎后材料，则默认为材料本身
        if (macerateInto.isNull()) macerateInto = properties.getMaterial();
        else macerateInto.getProperties().ensureSet(PropertyKey.INGOT, true);

        // 如果指定了磁化材料，确保其具有INGOT属性
        if (!magneticMaterial.isNull())
            magneticMaterial.getProperties().ensureSet(PropertyKey.INGOT, true);
    }
}
