package net.technochronicle.tccore.data.material;

import net.technochronicle.tclib.TcUtil;

import net.technochronicle.tccore.TcCore;
import net.technochronicle.tccore.api.TcAPI;
import net.technochronicle.tccore.api.material.MarkerMaterial;
import net.technochronicle.tccore.api.material.MarkerMaterials;
import net.technochronicle.tccore.api.material.Material;
import net.technochronicle.tccore.api.material.info.MaterialFlag;
import net.technochronicle.tccore.api.material.stack.MaterialStack;
import net.technochronicle.tccore.api.tag.TagPrefix;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.block.Blocks;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static net.technochronicle.tccore.api.material.info.MaterialFlags.*;
import static net.technochronicle.tccore.api.tag.TagPrefix.ORES;
import static net.technochronicle.tccore.data.tagprefix.TcTagPrefixes.*;

public class TcMaterials {

    /// 化学颜料
    public static Material[] CHEMICAL_DYES;

    public static void init() {
        MarkerMaterials.register();

        CHEMICAL_DYES = new Material[] {
                DyeWhite, DyeOrange,
                DyeMagenta, DyeLightBlue,
                DyeYellow, DyeLime,
                DyePink, DyeGray,
                DyeLightGray, DyeCyan,
                DyePurple, DyeBlue,
                DyeBrown, DyeGreen,
                DyeRed, DyeBlack
        };

        gem.setIgnored(Diamond, Items.DIAMOND);
        gem.setIgnored(Emerald, Items.EMERALD);
        gem.setIgnored(Lapis, Items.LAPIS_LAZULI);
        gem.setIgnored(NetherQuartz, Items.QUARTZ);
        gem.setIgnored(Coal, Items.COAL);
        gem.setIgnored(Amethyst, Items.AMETHYST_SHARD);
        gem.setIgnored(EchoShard, Items.ECHO_SHARD);
        excludeAllGems(Charcoal, Items.CHARCOAL);
        excludeAllGems(Flint, Items.FLINT);
        excludeAllGems(EnderPearl, Items.ENDER_PEARL);
        excludeAllGems(EnderEye, Items.ENDER_EYE);
        excludeAllGems(NetherStar, Items.NETHER_STAR);

        dust.setIgnored(Redstone, Items.REDSTONE);
        dust.setIgnored(Glowstone, Items.GLOWSTONE_DUST);
        dust.setIgnored(Gunpowder, Items.GUNPOWDER);
        dust.setIgnored(Sugar, Items.SUGAR);
        dust.setIgnored(Bone, Items.BONE_MEAL);
        dust.setIgnored(Blaze, Items.BLAZE_POWDER);

        rod.setIgnored(Wood, Items.STICK);
        rod.setIgnored(Bone, Items.BONE);
        rod.setIgnored(Blaze, Items.BLAZE_ROD);
        rod.setIgnored(Paper);

        ingot.setIgnored(Iron, Items.IRON_INGOT);
        ingot.setIgnored(Gold, Items.GOLD_INGOT);
        ingot.setIgnored(Copper, Items.COPPER_INGOT);
        ingot.setIgnored(Netherite, Items.NETHERITE_INGOT);
        ingot.setIgnored(Brick, Items.BRICK);
        ingot.setIgnored(Wax, Items.HONEYCOMB);

        nugget.setIgnored(Gold, Items.GOLD_NUGGET);
        nugget.setIgnored(Iron, Items.IRON_NUGGET);

        plate.setIgnored(Paper, Items.PAPER);

        block.setIgnored(Iron, Blocks.IRON_BLOCK);
        block.setIgnored(Gold, Blocks.GOLD_BLOCK);
        block.setIgnored(Copper, Blocks.COPPER_BLOCK);
        block.setIgnored(Netherite, Items.NETHERITE_BLOCK);
        block.setIgnored(Lapis, Blocks.LAPIS_BLOCK);
        block.setIgnored(Emerald, Blocks.EMERALD_BLOCK);
        block.setIgnored(Redstone, Blocks.REDSTONE_BLOCK);
        block.setIgnored(Diamond, Blocks.DIAMOND_BLOCK);
        block.setIgnored(Coal, Blocks.COAL_BLOCK);
        block.setIgnored(Amethyst, Blocks.AMETHYST_BLOCK);
        block.setIgnored(Glass, Blocks.GLASS);
        block.setIgnored(Glowstone, Blocks.GLOWSTONE);
        block.setIgnored(Wood);
        block.setIgnored(TreatedWood);
        block.setIgnored(Clay, Blocks.CLAY);
        block.setIgnored(Brick, Blocks.BRICKS);
        block.setIgnored(Bone, Blocks.BONE_BLOCK);
        block.setIgnored(NetherQuartz, Blocks.QUARTZ_BLOCK);
        block.setIgnored(Ice, Blocks.ICE);
        block.setIgnored(Concrete, Blocks.WHITE_CONCRETE, Blocks.ORANGE_CONCRETE, Blocks.MAGENTA_CONCRETE,
                Blocks.LIGHT_BLUE_CONCRETE, Blocks.YELLOW_CONCRETE, Blocks.LIME_CONCRETE,
                Blocks.PINK_CONCRETE, Blocks.GRAY_CONCRETE, Blocks.LIGHT_GRAY_CONCRETE, Blocks.CYAN_CONCRETE,
                Blocks.PURPLE_CONCRETE, Blocks.BLUE_CONCRETE,
                Blocks.BROWN_CONCRETE, Blocks.GREEN_CONCRETE, Blocks.RED_CONCRETE, Blocks.BLACK_CONCRETE);
        block.setIgnored(Blaze);
        block.setIgnored(Wax, Blocks.HONEYCOMB_BLOCK);

        rock.setIgnored(Granite, Blocks.GRANITE);
        rock.setIgnored(Granite, Blocks.POLISHED_GRANITE);
        rock.setIgnored(Andesite, Blocks.ANDESITE);
        rock.setIgnored(Andesite, Blocks.POLISHED_ANDESITE);
        rock.setIgnored(Diorite, Blocks.DIORITE);
        rock.setIgnored(Diorite, Blocks.POLISHED_DIORITE);
        rock.setIgnored(Stone, Blocks.STONE);
        rock.setIgnored(Calcite, Blocks.CALCITE);
        rock.setIgnored(Netherrack, Blocks.NETHERRACK);
        rock.setIgnored(Obsidian, Blocks.OBSIDIAN);
        rock.setIgnored(Endstone, Blocks.END_STONE);
        rock.setIgnored(Deepslate, Blocks.DEEPSLATE);
        rock.setIgnored(Basalt, Blocks.BASALT);
        rock.setIgnored(Blackstone, Blocks.BLACKSTONE);
        block.setIgnored(Sculk, Blocks.SCULK);

        for (TagPrefix prefix : ORES.keySet()) {
            TagPrefix.OreType oreType = ORES.get(prefix);
            if (oreType.shouldDropAsItem() && oreType.material() != null) {
                prefix.addSecondaryMaterial(new MaterialStack(oreType.material().get(), dust.materialAmount()));
            }
        }

        dye.setIgnored(DyeBlack, Items.BLACK_DYE);
        dye.setIgnored(DyeRed, Items.RED_DYE);
        dye.setIgnored(DyeGreen, Items.GREEN_DYE);
        dye.setIgnored(DyeBrown, Items.BROWN_DYE);
        dye.setIgnored(DyeBlue, Items.BLUE_DYE);
        dye.setIgnored(DyePurple, Items.PURPLE_DYE);
        dye.setIgnored(DyeCyan, Items.CYAN_DYE);
        dye.setIgnored(DyeLightGray, Items.LIGHT_GRAY_DYE);
        dye.setIgnored(DyeGray, Items.GRAY_DYE);
        dye.setIgnored(DyePink, Items.PINK_DYE);
        dye.setIgnored(DyeLime, Items.LIME_DYE);
        dye.setIgnored(DyeYellow, Items.YELLOW_DYE);
        dye.setIgnored(DyeLightBlue, Items.LIGHT_BLUE_DYE);
        dye.setIgnored(DyeMagenta, Items.MAGENTA_DYE);
        dye.setIgnored(DyeOrange, Items.ORANGE_DYE);
        dye.setIgnored(DyeWhite, Items.WHITE_DYE);

        // register vanilla materials

        rawOre.setIgnored(Gold, Items.RAW_GOLD);
        rawOre.setIgnored(Iron, Items.RAW_IRON);
        rawOre.setIgnored(Copper, Items.RAW_COPPER);
        rawOreBlock.setIgnored(Gold, Blocks.RAW_GOLD_BLOCK);
        rawOreBlock.setIgnored(Iron, Blocks.RAW_IRON_BLOCK);
        rawOreBlock.setIgnored(Copper, Blocks.RAW_COPPER_BLOCK);

        block.modifyMaterialAmount(Amethyst, 4);
        block.modifyMaterialAmount(EchoShard, 4);
        block.modifyMaterialAmount(Glowstone, 4);
        block.modifyMaterialAmount(NetherQuartz, 4);
        block.modifyMaterialAmount(CertusQuartz, 4);
        block.modifyMaterialAmount(Brick, 4);
        block.modifyMaterialAmount(Clay, 4);

        block.modifyMaterialAmount(Concrete, 1);
        block.modifyMaterialAmount(Glass, 1);
        block.modifyMaterialAmount(Ice, 1);
        block.modifyMaterialAmount(Obsidian, 1);
        block.modifyMaterialAmount(Sculk, 1);
        block.modifyMaterialAmount(Wax, 4);

        rod.modifyMaterialAmount(Blaze, 4);
        rod.modifyMaterialAmount(Bone, 5);
    }

    @NotNull
    public static Material get(String name) {
        var mat = TcAPI.materialManager.getMaterial(ResourceLocation.parse(name));
        // mat could be null here due to the registrate grabbing a material that isn't in the map
        if (mat == null) {
            TcCore.LOGGER.warn("{} is not a known Material", name);
            return TcMaterials.NULL;
        }
        return mat;
    }

    // region MISC
    private static void excludeAllGems(Material material, ItemLike... items) {
        gem.setIgnored(material, items);
        excludeAllGemsButNormal(material);
    }

    private static void excludeAllGemsButNormal(Material material) {}

    public static final List<MaterialFlag> STD_METAL = new ArrayList<>();
    public static final List<MaterialFlag> EXT_METAL = new ArrayList<>();
    public static final List<MaterialFlag> EXT2_METAL = new ArrayList<>();

    static {
        STD_METAL.add(GENERATE_PLATE);

        EXT_METAL.addAll(STD_METAL);
        EXT_METAL.add(GENERATE_ROD);

        EXT2_METAL.addAll(EXT_METAL);
        EXT2_METAL.addAll(Arrays.asList(GENERATE_LONG_ROD, GENERATE_BOLT_SCREW));
    }
    // endregion

    public static final MarkerMaterial NULL = new MarkerMaterial(TcUtil.id("null"));

    // region 元素周期表材料
    public static Material Actinium;
    public static Material Aluminium;
    public static Material Americium;
    public static Material Antimony;
    public static Material Argon;
    public static Material Arsenic;
    public static Material Astatine;
    public static Material Barium;
    public static Material Berkelium;
    public static Material Beryllium;
    public static Material Bismuth;
    public static Material Bohrium;
    public static Material Boron;
    public static Material Bromine;
    public static Material Caesium;
    public static Material Calcium;
    public static Material Californium;
    public static Material Carbon;
    public static Material Cadmium;
    public static Material Cerium;
    public static Material Chlorine;
    public static Material Chromium;
    public static Material Cobalt;
    public static Material Copernicium;
    public static Material Copper;
    public static Material Curium;
    public static Material Darmstadtium;
    public static Material Deuterium;
    public static Material Dubnium;
    public static Material Dysprosium;
    public static Material Einsteinium;
    public static Material Erbium;
    public static Material Europium;
    public static Material Fermium;
    public static Material Flerovium;
    public static Material Fluorine;
    public static Material Francium;
    public static Material Gadolinium;
    public static Material Gallium;
    public static Material Germanium;
    public static Material Gold;
    public static Material Hafnium;
    public static Material Hassium;
    public static Material Holmium;
    public static Material Hydrogen;
    public static Material Helium;
    public static Material Helium3;
    public static Material Indium;
    public static Material Iodine;
    public static Material Iridium;
    public static Material Iron;
    public static Material Krypton;
    public static Material Lanthanum;
    public static Material Lawrencium;
    public static Material Lead;
    public static Material Lithium;
    public static Material Livermorium;
    public static Material Lutetium;
    public static Material Magnesium;
    public static Material Mendelevium;
    public static Material Manganese;
    public static Material Meitnerium;
    public static Material Mercury;
    public static Material Molybdenum;
    public static Material Moscovium;
    public static Material Neodymium;
    public static Material Neon;
    public static Material Neptunium;
    public static Material Nickel;
    public static Material Nihonium;
    public static Material Niobium;
    public static Material Nitrogen;
    public static Material Nobelium;
    public static Material Oganesson;
    public static Material Osmium;
    public static Material Oxygen;
    public static Material Palladium;
    public static Material Phosphorus;
    public static Material Polonium;
    public static Material Platinum;
    public static Material Plutonium239;
    public static Material Plutonium241;
    public static Material Potassium;
    public static Material Praseodymium;
    public static Material Promethium;
    public static Material Protactinium;
    public static Material Radon;
    public static Material Radium;
    public static Material Rhenium;
    public static Material Rhodium;
    public static Material Roentgenium;
    public static Material Rubidium;
    public static Material Ruthenium;
    public static Material Rutherfordium;
    public static Material Samarium;
    public static Material Scandium;
    public static Material Seaborgium;
    public static Material Selenium;
    public static Material Silicon;
    public static Material Silver;
    public static Material Sodium;
    public static Material Strontium;
    public static Material Sulfur;
    public static Material Tantalum;
    public static Material Technetium;
    public static Material Tellurium;
    public static Material Tennessine;
    public static Material Terbium;
    public static Material Thorium;
    public static Material Thallium;
    public static Material Thulium;
    public static Material Tin;
    public static Material Titanium;
    public static Material Tritium;
    public static Material Tungsten;
    public static Material Uranium238;
    public static Material Uranium235;
    public static Material Vanadium;
    public static Material Xenon;
    public static Material Ytterbium;
    public static Material Yttrium;
    public static Material Zinc;
    public static Material Zirconium;
    // endregion
    // region 颜料材料
    public static Material DyeBlack;
    public static Material DyeRed;
    public static Material DyeGreen;
    public static Material DyeBrown;
    public static Material DyeBlue;
    public static Material DyePurple;
    public static Material DyeCyan;
    public static Material DyeLightGray;
    public static Material DyeGray;
    public static Material DyePink;
    public static Material DyeLime;
    public static Material DyeYellow;
    public static Material DyeLightBlue;
    public static Material DyeMagenta;
    public static Material DyeOrange;
    public static Material DyeWhite;
    // endregion
    // region 石料
    /// 石头
    public static Material Stone;
    /// 花岗岩
    public static Material Granite;
    /// 闪长岩
    public static Material Diorite;
    /// 安山岩
    public static Material Andesite;
    /// 深板岩
    public static Material Deepslate;
    /// 凝灰岩
    public static Material Tuff;
    /// 沙子
    public static Material SiliconDioxide;
    /// 沙砾
    public static Material Flint;
    /// 玄武岩
    public static Material Basalt;
    /// 下界岩
    public static Material Netherrack;
    /// 黑石
    public static Material Blackstone;
    /// 末地石
    public static Material Endstone;
    // endregion
    // TODO:原版材料
    public static Material Water;
    public static Material Lava;
    public static Material Milk;
    /// 黑曜石
    public static Material Obsidian;
    /// 冰
    public static Material Ice;
    /// 玻璃
    public static Material Glass;
    /// 混凝土
    public static Material Concrete;
    /// 粘土
    public static Material Clay;
    /// 红砖
    public static Material Brick;
    /// 方解石
    public static Material Calcite;
    // region 矿物
    /// 下界合金
    public static Material Netherite;
    /// 红石
    public static Material Redstone;
    /// 钻石
    public static Material Diamond;
    /// 煤
    public static Material Coal;
    /// 绿宝石
    public static Material Emerald;
    /// 青金石
    public static Material Lapis;
    /// 下界石英
    public static Material NetherQuartz;
    /// 塞特斯石英
    public static Material CertusQuartz;
    /// 荧石
    public static Material Glowstone;
    /// 紫水晶
    public static Material Amethyst;
    // endregion
    // region 杂物
    /// 下界之心
    public static Material NetherStar;
    /// 末影之眼
    public static Material EnderEye;
    /// 末影珍珠
    public static Material EnderPearl;
    /// 木头
    public static Material Wood;
    /// 去皮木头
    public static Material TreatedWood;
    /// 木炭
    public static Material Charcoal;
    /// 糖
    public static Material Sugar;
    /// 纸
    public static Material Paper;
    /// 火药
    public static Material Gunpowder;
    /// 回响碎片
    public static Material EchoShard;
    /// 骨
    public static Material Bone;
    /// 烈焰(烈焰粉,烈焰棒)
    public static Material Blaze;
    /// 蜡(蜜脾)
    public static Material Wax;
    /// 幽匿
    public static Material Sculk;
    // endregion
    // 空气
    /// 幽匿
    public static Material Air;
    public static Material NetherAir;
    public static Material EnderAir;
    public static Material LiquidAir;
    public static Material LiquidNetherAir;
    public static Material LiquidEnderAir;
}
