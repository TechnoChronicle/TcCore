package net.technochronicle.tccore.api.material.info;

import net.technochronicle.tccore.api.material.Material;
import net.technochronicle.tccore.api.material.property.PropertyKey;

import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * 材料标志
 * 用于批量生成数数据包内容
 */
public class MaterialFlags {

    private final Set<MaterialFlag> flags = new HashSet<>();

    public MaterialFlags addFlags(MaterialFlag... flags) {
        this.flags.addAll(Arrays.asList(flags));
        return this;
    }

    public void verify(Material material) {
        flags.addAll(flags.stream()
                .map(f -> f.verifyFlag(material))
                .flatMap(Collection::stream)
                .collect(Collectors.toSet()));
    }

    public boolean hasFlag(MaterialFlag flag) {
        return flags.contains(flag);
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        flags.forEach(f -> sb.append(f.toString()).append("\n"));
        return sb.toString();
    }

    /////////////////
    // GENERIC //
    /////////////////

    /**
     * 添加到材料以完全禁用其统一化处理
     */
    public static final MaterialFlag DISABLE_MATERIAL_RECIPES = new MaterialFlag.Builder("disable_material_recipes")
            .build();

    /**
     * 启用电解机分解配方生成
     */
    public static final MaterialFlag DECOMPOSITION_BY_ELECTROLYZING = new MaterialFlag.Builder(
            "decomposition_by_electrolyzing").build();

    /**
     * 启用离心机分解配方生成
     */
    public static final MaterialFlag DECOMPOSITION_BY_CENTRIFUGING = new MaterialFlag.Builder(
            "decomposition_by_centrifuging").build();

    /**
     * 禁用此材料的分解配方生成
     */
    public static final MaterialFlag DISABLE_DECOMPOSITION = new MaterialFlag.Builder("disable_decomposition").build();

    /**
     * 添加到材料，表示其为某种爆炸物
     */
    public static final MaterialFlag EXPLOSIVE = new MaterialFlag.Builder("explosive").build();

    /**
     * 添加到材料，表示其为某种易燃物
     */
    public static final MaterialFlag FLAMMABLE = new MaterialFlag.Builder("flammable").build();

    /**
     * 添加到材料，表示其为某种粘性物
     */
    public static final MaterialFlag STICKY = new MaterialFlag.Builder("sticky").build();

    /**
     * 添加到材料，表示其为某种磷光物
     */
    public static final MaterialFlag PHOSPHORESCENT = new MaterialFlag.Builder("phosphorescent").build();

    //////////////////
    // 粉尘 //
    //////////////////

    /**
     * 为此材料生成板
     * 如果是粉尘材料，将生成粉尘压缩机配方制作板
     * 如果是金属材料，将生成弯曲机配方
     * 如果找到方块，还会生成切割机配方
     */
    public static final MaterialFlag GENERATE_PLATE = new MaterialFlag.Builder("generate_plate")
            .requireProps(PropertyKey.DUST)
            .build();

    public static final MaterialFlag GENERATE_DENSE = new MaterialFlag.Builder("generate_dense")
            .requireFlags(GENERATE_PLATE)
            .requireProps(PropertyKey.DUST)
            .build();

    public static final MaterialFlag GENERATE_ROD = new MaterialFlag.Builder("generate_rod")
            .requireProps(PropertyKey.DUST)
            .build();

    public static final MaterialFlag GENERATE_BOLT_SCREW = new MaterialFlag.Builder("generate_bolt_screw")
            .requireFlags(GENERATE_ROD)
            .requireProps(PropertyKey.DUST)
            .build();

    public static final MaterialFlag GENERATE_FRAME = new MaterialFlag.Builder("generate_frame")
            .requireFlags(GENERATE_ROD)
            .requireProps(PropertyKey.DUST)
            .build();

    public static final MaterialFlag GENERATE_GEAR = new MaterialFlag.Builder("generate_gear")
            .requireFlags(GENERATE_PLATE, GENERATE_ROD)
            .requireProps(PropertyKey.DUST)
            .build();

    public static final MaterialFlag GENERATE_LONG_ROD = new MaterialFlag.Builder("generate_long_rod")
            .requireFlags(GENERATE_ROD)
            .requireProps(PropertyKey.DUST)
            .build();

    public static final MaterialFlag FORCE_GENERATE_BLOCK = new MaterialFlag.Builder("force_generate_block")
            .requireProps(PropertyKey.DUST)
            .build();

    /**
     * 这将阻止材料创建粉尘与方块之间的无序合成配方，
     * 同时阻止通过SHAPE_EXTRUDING/MOLD_BLOCK的挤出和合金冶炼配方。
     */
    public static final MaterialFlag EXCLUDE_BLOCK_CRAFTING_RECIPES = new MaterialFlag.Builder(
            "exclude_block_crafting_recipes")
            .requireProps(PropertyKey.DUST)
            .build();

    /**
     * 排除板材压缩机配方
     */
    public static final MaterialFlag EXCLUDE_PLATE_COMPRESSOR_RECIPE = new MaterialFlag.Builder(
            "exclude_plate_compressor_recipe")
            .requireFlags(GENERATE_PLATE)
            .requireProps(PropertyKey.DUST)
            .build();

    /**
     * 这将阻止材料创建粉尘与方块之间的无序合成配方。
     */
    public static final MaterialFlag EXCLUDE_BLOCK_CRAFTING_BY_HAND_RECIPES = new MaterialFlag.Builder(
            "exclude_block_crafting_by_hand_recipes")
            .requireProps(PropertyKey.DUST)
            .build();

    /**
     * 添加到材料表示可用研钵研磨
     */
    public static final MaterialFlag MORTAR_GRINDABLE = new MaterialFlag.Builder("mortar_grindable")
            .requireProps(PropertyKey.DUST)
            .build();

    /**
     * 添加到材料表示除粉碎或熔炼外无法通过其他方式加工。用于涂层材料。
     */
    public static final MaterialFlag NO_WORKING = new MaterialFlag.Builder("no_working")
            .requireProps(PropertyKey.DUST)
            .build();

    /**
     * 添加到材料表示无法进行常规金属加工，因为无法弯曲。
     */
    public static final MaterialFlag NO_SMASHING = new MaterialFlag.Builder("no_smashing")
            .requireProps(PropertyKey.DUST)
            .build();

    /**
     * 添加到材料表示无法熔炼
     */
    public static final MaterialFlag NO_SMELTING = new MaterialFlag.Builder("no_smelting")
            .requireProps(PropertyKey.DUST)
            .build();

    /**
     * 添加到材料表示无法从矿石中熔炼
     */
    public static final MaterialFlag NO_ORE_SMELTING = new MaterialFlag.Builder("no_ore_smelting")
            .requireProps(PropertyKey.DUST)
            .build();

    /**
     * 添加到材料以禁用创建矿石处理标签页
     */
    public static final MaterialFlag NO_ORE_PROCESSING_TAB = new MaterialFlag.Builder("no_ore_processing_tab")
            .requireProps(PropertyKey.ORE)
            .build();

    /**
     * 将此添加到您的材料中，如果您希望其矿石方解石在高炉中加热以获得更多产出。
     * 已经列出的材料有：铁、黄铁矿、生铁、熟铁。
     */
    public static final MaterialFlag BLAST_FURNACE_CALCITE_DOUBLE = new MaterialFlag.Builder(
            "blast_furnace_calcite_double")
            .requireProps(PropertyKey.DUST)
            .build();

    /**
     * 高炉方解石三重产量
     */
    public static final MaterialFlag BLAST_FURNACE_CALCITE_TRIPLE = new MaterialFlag.Builder(
            "blast_furnace_calcite_triple")
            .requireProps(PropertyKey.DUST)
            .build();
    // GCYM
    /**
     * 用于禁用合金高炉配方的生成
     */
    public static final MaterialFlag DISABLE_ALLOY_BLAST = new MaterialFlag.Builder("disable_alloy_blast")
            .requireProps(PropertyKey.BLAST, PropertyKey.FLUID)
            .build();

    /**
     * 用于禁用与合金高炉相关的所有内容
     */
    public static final MaterialFlag DISABLE_ALLOY_PROPERTY = new MaterialFlag.Builder("disable_alloy_property")
            .requireProps(PropertyKey.BLAST, PropertyKey.FLUID)
            .requireFlags(DISABLE_ALLOY_BLAST)
            .build();

    /////////////////
    // FLUID //
    /////////////////

    public static final MaterialFlag SOLDER_MATERIAL = new MaterialFlag.Builder("solder_material")
            .requireProps(PropertyKey.FLUID)
            .build();

    public static final MaterialFlag SOLDER_MATERIAL_BAD = new MaterialFlag.Builder("solder_material_bad")
            .requireProps(PropertyKey.FLUID)
            .build();

    public static final MaterialFlag SOLDER_MATERIAL_GOOD = new MaterialFlag.Builder("solder_material_good")
            .requireProps(PropertyKey.FLUID)
            .build();

    /////////////////
    // INGOT //
    /////////////////

    public static final MaterialFlag GENERATE_FOIL = new MaterialFlag.Builder("generate_foil")
            .requireFlags(GENERATE_PLATE)
            .requireProps(PropertyKey.INGOT)
            .build();

    public static final MaterialFlag GENERATE_RING = new MaterialFlag.Builder("generate_ring")
            .requireFlags(GENERATE_ROD)
            .requireProps(PropertyKey.INGOT)
            .build();

    public static final MaterialFlag GENERATE_SPRING = new MaterialFlag.Builder("generate_spring")
            .requireFlags(GENERATE_LONG_ROD)
            .requireProps(PropertyKey.INGOT)
            .build();

    public static final MaterialFlag GENERATE_SPRING_SMALL = new MaterialFlag.Builder("generate_spring_small")
            .requireFlags(GENERATE_ROD)
            .requireProps(PropertyKey.INGOT)
            .build();

    public static final MaterialFlag GENERATE_SMALL_GEAR = new MaterialFlag.Builder("generate_small_gear")
            .requireFlags(GENERATE_PLATE, GENERATE_ROD)
            .requireProps(PropertyKey.INGOT)
            .build();

    public static final MaterialFlag GENERATE_FINE_WIRE = new MaterialFlag.Builder("generate_fine_wire")
            .requireFlags(GENERATE_FOIL)
            .requireProps(PropertyKey.INGOT)
            .build();

    public static final MaterialFlag GENERATE_ROTOR = new MaterialFlag.Builder("generate_rotor")
            .requireFlags(GENERATE_BOLT_SCREW, GENERATE_RING, GENERATE_PLATE)
            .requireProps(PropertyKey.INGOT)
            .build();

    public static final MaterialFlag GENERATE_ROUND = new MaterialFlag.Builder("generate_round")
            .requireProps(PropertyKey.INGOT)
            .build();

    /**
     * 将此添加到您的材料中，如果它是另一种材料的磁化形式。
     */
    public static final MaterialFlag IS_MAGNETIC = new MaterialFlag.Builder("is_magnetic")
            .requireProps(PropertyKey.INGOT)
            .build();

    /////////////////
    // 宝石 //
    /////////////////

    /**
     * 表示此材料可以结晶。
     */
    public static final MaterialFlag CRYSTALLIZABLE = new MaterialFlag.Builder("crystallizable")
            .requireProps(PropertyKey.GEM)
            .build();
    public static final MaterialFlag GENERATE_LENS = new MaterialFlag.Builder("generate_lens")
            .requireFlags(GENERATE_PLATE)
            .requireProps(PropertyKey.GEM)
            .build();

    /////////////////
    // ORE //
    /////////////////

    public static final MaterialFlag HIGH_SIFTER_OUTPUT = new MaterialFlag.Builder("high_sifter_output")
            .requireProps(PropertyKey.GEM, PropertyKey.ORE)
            .build();
}
