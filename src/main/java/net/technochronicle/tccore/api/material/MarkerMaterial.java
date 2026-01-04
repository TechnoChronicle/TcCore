package net.technochronicle.tccore.api.material;

import net.minecraft.resources.ResourceLocation;

/**
 * 标记材料是一种用于通用目的的材料类型，例如材料重新注册和在配方中使用。
 * 标记材料不能用于生成任何元物品。
 * 标记材料仅可用于将其他材料标记（重新注册）为与其相等，然后在配方或获取物品时使用。
 * 标记材料不会出现在材料注册表中，也不能用于持久化。
 */
public final class MarkerMaterial extends Material {

    private final ResourceLocation resourceLocation;

    public MarkerMaterial(ResourceLocation resourceLocation) {
        super(resourceLocation);
        this.resourceLocation = resourceLocation;
        // TODO
        // OreDictUnifier.registerMarkerMaterial(this);
    }

    @Override
    public void verifyMaterial() {}

    @Override
    // since we're not registered, return overriden name
    public String toString() {
        return resourceLocation.toString();
    }
}
