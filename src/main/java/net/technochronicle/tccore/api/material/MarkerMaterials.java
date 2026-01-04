package net.technochronicle.tccore.api.material;

import net.technochronicle.tclib.TcUtil;

import net.minecraft.world.item.DyeColor;

import com.google.common.collect.HashBiMap;

public class MarkerMaterials {

    @SuppressWarnings("ResultOfMethodCallIgnored")
    public static void register() {
        Color.Colorless.toString();
        Empty.toString();
    }

    /**
     * 无分类的标记材料
     */
    public static final MarkerMaterial Empty = new MarkerMaterial(TcUtil.id("empty"));

    /**
     * 颜色材料
     */
    public static class Color {

        /**
         * 只能通过直接指定使用
         * 表示 TagPrefix 上没有颜色
         * 通常作为颜色前缀的默认值
         */
        public static final MarkerMaterial Colorless = new MarkerMaterial(TcUtil.id("colorless"));

        public static final MarkerMaterial White = new MarkerMaterial(TcUtil.id("white"));
        public static final MarkerMaterial Orange = new MarkerMaterial(TcUtil.id("orange"));
        public static final MarkerMaterial Magenta = new MarkerMaterial(TcUtil.id("magenta"));
        public static final MarkerMaterial LightBlue = new MarkerMaterial(TcUtil.id("light_blue"));
        public static final MarkerMaterial Yellow = new MarkerMaterial(TcUtil.id("yellow"));
        public static final MarkerMaterial Lime = new MarkerMaterial(TcUtil.id("lime"));
        public static final MarkerMaterial Pink = new MarkerMaterial(TcUtil.id("pink"));
        public static final MarkerMaterial Gray = new MarkerMaterial(TcUtil.id("gray"));
        public static final MarkerMaterial LightGray = new MarkerMaterial(TcUtil.id("light_gray"));
        public static final MarkerMaterial Cyan = new MarkerMaterial(TcUtil.id("cyan"));
        public static final MarkerMaterial Purple = new MarkerMaterial(TcUtil.id("purple"));
        public static final MarkerMaterial Blue = new MarkerMaterial(TcUtil.id("blue"));
        public static final MarkerMaterial Brown = new MarkerMaterial(TcUtil.id("brown"));
        public static final MarkerMaterial Green = new MarkerMaterial(TcUtil.id("green"));
        public static final MarkerMaterial Red = new MarkerMaterial(TcUtil.id("red"));
        public static final MarkerMaterial Black = new MarkerMaterial(TcUtil.id("black"));
        /**
         * 包含所有可能颜色值的数组（不包含无色！）
         */
        public static final MarkerMaterial[] VALUES = new MarkerMaterial[] {
                White, Orange, Magenta, LightBlue, Yellow, Lime, Pink, Gray, LightGray, Cyan, Purple, Blue, Brown,
                Green, Red, Black
        };

        /**
         * 通过颜色名称获取颜色材料
         * 名称格式与 DyeColor 相同
         */
        public static MarkerMaterial valueOf(String string) {
            for (MarkerMaterial color : VALUES) {
                if (color.getName().equals(string)) {
                    return color;
                }
            }
            return null;
        }

        /**
         * 包含 MC DyeColor 与颜色标记材料之间的关联映射
         */
        public static final HashBiMap<DyeColor, MarkerMaterial> COLORS = HashBiMap.create();

        static {
            for (var color : DyeColor.values()) {
                COLORS.put(color, Color.valueOf(color.getName()));
            }
        }
    }
}
