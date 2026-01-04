package net.technochronicle.tccore.api.material.property;

import net.technochronicle.tccore.api.material.info.MaterialFlags;

/**
 * 聚合物信息
 */
public class PolymerProperty implements IMaterialProperty {

    @Override
    public void verifyProperty(MaterialProperties properties) {
        properties.ensureSet(PropertyKey.DUST, true);
        properties.ensureSet(PropertyKey.INGOT, true);

        properties.getMaterial().addFlags(MaterialFlags.FLAMMABLE, MaterialFlags.NO_SMASHING,
                MaterialFlags.DISABLE_DECOMPOSITION);
    }
}
