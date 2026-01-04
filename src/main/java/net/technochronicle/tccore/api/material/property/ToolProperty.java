package net.technochronicle.tccore.api.material.property;

/**
 * 工具属性
 */
public class ToolProperty implements IMaterialProperty {

    @Override
    public void verifyProperty(MaterialProperties properties) {
        if (properties.hasProperty(PropertyKey.WOOD)) {
            return;
        }
        if (properties.hasProperty(PropertyKey.GEM)) {
            return;
        }
        properties.ensureSet(PropertyKey.INGOT, true);
    }

    public static class Builder {}
}
