package net.technochronicle.tccore.api.material.property;

/**
 * 木材属性
 */
public class WoodProperty implements IMaterialProperty {

    @Override
    public void verifyProperty(MaterialProperties properties) {
        properties.ensureSet(PropertyKey.DUST, true);
    }
}
