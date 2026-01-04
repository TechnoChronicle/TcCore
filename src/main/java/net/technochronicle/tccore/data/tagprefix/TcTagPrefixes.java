package net.technochronicle.tccore.data.tagprefix;

import net.technochronicle.tccore.api.TcAPI;
import net.technochronicle.tccore.api.addon.AddonFinder;
import net.technochronicle.tccore.api.addon.ITcAddon;
import net.technochronicle.tccore.api.material.info.MaterialFlags;
import net.technochronicle.tccore.api.material.property.PropertyKey;
import net.technochronicle.tccore.api.tag.TagPrefix;
import net.technochronicle.tccore.data.material.TcMaterialIconTypes;
import net.technochronicle.tccore.data.material.TcMaterials;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.properties.NoteBlockInstrument;
import net.minecraft.world.level.material.MapColor;

import static net.technochronicle.tccore.api.tag.TagPrefix.*;
import static net.technochronicle.tccore.api.tag.TagPrefix.Conditions.*;

public class TcTagPrefixes {

    public static void init() {
        AddonFinder.getAddonList().forEach(ITcAddon::registerTagPrefixes);
    }

    public static final TagPrefix NULL_PREFIX = TagPrefix.NULL_PREFIX;

    /// 非变种矿石
    public static final TagPrefix ore = oreTagPrefix("stone", BlockTags.MINEABLE_WITH_PICKAXE)
            .langValue("%s Ore")
            .registerOre(
                    Blocks.STONE::defaultBlockState, () -> TcMaterials.Stone, BlockBehaviour.Properties.of()
                            .mapColor(MapColor.STONE).requiresCorrectToolForDrops().strength(3.0F, 3.0F),
                    ResourceLocation.withDefaultNamespace("block/stone"), false, false, true);

    /// 花岗岩矿石
    public static final TagPrefix oreGranite = oreTagPrefix("granite", BlockTags.MINEABLE_WITH_PICKAXE)
            .langValue("Granite %s Ore")
            .registerOre(
                    Blocks.GRANITE::defaultBlockState, () -> TcMaterials.Granite, BlockBehaviour.Properties.of()
                            .mapColor(MapColor.DIRT).requiresCorrectToolForDrops().strength(3.0F, 3.0F),
                    ResourceLocation.withDefaultNamespace("block/granite"));

    /// 闪长岩矿石
    public static final TagPrefix oreDiorite = oreTagPrefix("diorite", BlockTags.MINEABLE_WITH_PICKAXE)
            .langValue("Diorite %s Ore")
            .registerOre(
                    Blocks.DIORITE::defaultBlockState, () -> TcMaterials.Diorite, BlockBehaviour.Properties.of()
                            .mapColor(MapColor.QUARTZ).requiresCorrectToolForDrops().strength(3.0F, 3.0F),
                    ResourceLocation.withDefaultNamespace("block/diorite"));

    /// 安山岩矿石
    public static final TagPrefix oreAndesite = oreTagPrefix("andesite", BlockTags.MINEABLE_WITH_PICKAXE)
            .langValue("Andesite %s Ore")
            .registerOre(
                    Blocks.ANDESITE::defaultBlockState, () -> TcMaterials.Andesite, BlockBehaviour.Properties.of()
                            .mapColor(MapColor.DIRT).requiresCorrectToolForDrops().strength(3.0F, 3.0F),
                    ResourceLocation.withDefaultNamespace("block/andesite"));
    /// 深板岩矿石
    public static final TagPrefix oreDeepslate = oreTagPrefix("deepslate", BlockTags.MINEABLE_WITH_PICKAXE)
            .langValue("Deepslate %s Ore")
            .registerOre(
                    Blocks.DEEPSLATE::defaultBlockState, () -> TcMaterials.Deepslate, BlockBehaviour.Properties.of()
                            .mapColor(MapColor.DEEPSLATE).requiresCorrectToolForDrops().strength(4.5F, 3.0F)
                            .sound(SoundType.DEEPSLATE),
                    ResourceLocation.withDefaultNamespace("block/deepslate"), false, false, true);

    /// 凝灰岩矿石
    public static final TagPrefix oreTuff = oreTagPrefix("tuff", BlockTags.MINEABLE_WITH_PICKAXE)
            .langValue("Tuff %s Ore")
            .registerOre(
                    Blocks.TUFF::defaultBlockState, () -> TcMaterials.Tuff, BlockBehaviour.Properties.of()
                            .mapColor(MapColor.TERRACOTTA_GRAY).requiresCorrectToolForDrops().strength(3.0F, 3.0F)
                            .sound(SoundType.TUFF),
                    ResourceLocation.withDefaultNamespace("block/tuff"));

    /// 沙子矿石
    public static final TagPrefix oreSand = oreTagPrefix("sand", BlockTags.MINEABLE_WITH_SHOVEL)
            .langValue("Sand %s Ore")
            .registerOre(Blocks.SAND::defaultBlockState, () -> TcMaterials.SiliconDioxide,
                    BlockBehaviour.Properties.of().mapColor(MapColor.SAND).instrument(NoteBlockInstrument.SNARE)
                            .strength(0.5F).sound(SoundType.SAND),
                    ResourceLocation.withDefaultNamespace("block/sand"), false, true, false);

    /// 红沙矿石
    public static final TagPrefix oreRedSand = oreTagPrefix("redSand", BlockTags.MINEABLE_WITH_SHOVEL)
            .langValue("Red Sand %s Ore")
            .registerOre(Blocks.RED_SAND::defaultBlockState, () -> TcMaterials.SiliconDioxide,
                    BlockBehaviour.Properties.of().mapColor(MapColor.COLOR_ORANGE).instrument(NoteBlockInstrument.SNARE)
                            .strength(0.5F).sound(SoundType.SAND),
                    ResourceLocation.withDefaultNamespace("block/red_sand"), false, true, false);

    /// 沙砾矿石
    public static final TagPrefix oreGravel = oreTagPrefix("gravel", BlockTags.MINEABLE_WITH_SHOVEL)
            .langValue("Gravel %s Ore")
            .registerOre(Blocks.GRAVEL::defaultBlockState, () -> TcMaterials.Flint,
                    BlockBehaviour.Properties.of().mapColor(MapColor.STONE).instrument(NoteBlockInstrument.SNARE)
                            .strength(0.6F).sound(SoundType.GRAVEL),
                    ResourceLocation.withDefaultNamespace("block/gravel"), false, true, false);

    /// 玄武岩矿石
    public static final TagPrefix oreBasalt = oreTagPrefix("basalt", BlockTags.MINEABLE_WITH_PICKAXE)
            .langValue("Basalt %s Ore")
            .registerOre(Blocks.BASALT::defaultBlockState, () -> TcMaterials.Basalt,
                    BlockBehaviour.Properties.of().mapColor(MapColor.COLOR_BLACK)
                            .instrument(NoteBlockInstrument.BASEDRUM).requiresCorrectToolForDrops().strength(2.5F, 4.2F)
                            .sound(SoundType.BASALT),
                    ResourceLocation.withDefaultNamespace("block/basalt"), true);

    /// 下界岩矿石
    public static final TagPrefix oreNetherrack = oreTagPrefix("netherrack", BlockTags.MINEABLE_WITH_PICKAXE)
            .langValue("Nether %s Ore")
            .registerOre(Blocks.NETHERRACK::defaultBlockState, () -> TcMaterials.Netherrack,
                    BlockBehaviour.Properties.of().mapColor(MapColor.NETHER).instrument(NoteBlockInstrument.BASEDRUM)
                            .requiresCorrectToolForDrops().strength(3.0F, 3.0F).sound(SoundType.NETHER_ORE),
                    ResourceLocation.withDefaultNamespace("block/netherrack"), true, false, true);

    /// 黑石矿石
    public static final TagPrefix oreBlackstone = oreTagPrefix("blackstone", BlockTags.MINEABLE_WITH_PICKAXE)
            .langValue("Blackstone %s Ore")
            .registerOre(Blocks.BLACKSTONE::defaultBlockState, () -> TcMaterials.Blackstone,
                    BlockBehaviour.Properties.of().mapColor(MapColor.COLOR_BLACK)
                            .instrument(NoteBlockInstrument.BASEDRUM).requiresCorrectToolForDrops()
                            .strength(3.0F, 3.0F),
                    ResourceLocation.withDefaultNamespace("block/blackstone"), true, false, false);
    /// 末地石矿石
    public static final TagPrefix oreEndstone = oreTagPrefix("endstone", BlockTags.MINEABLE_WITH_PICKAXE)
            .langValue("End %s Ore")
            .registerOre(Blocks.END_STONE::defaultBlockState, () -> TcMaterials.Endstone,
                    BlockBehaviour.Properties.of().mapColor(MapColor.SAND).instrument(NoteBlockInstrument.BASEDRUM)
                            .requiresCorrectToolForDrops().strength(4.5F, 9.0F),
                    ResourceLocation.withDefaultNamespace("block/end_stone"), true, false, true);
    /// 粗矿
    public static final TagPrefix rawOre = new TagPrefix("raw")
            .idPattern("raw_%s")
            .defaultTagPath("raw_materials/%s")
            .unformattedTagPath("raw_materials")
            .langValue("Raw %s")
            .materialIconType(TcMaterialIconTypes.rawOre)
            .unificationEnabled(true)
            .generateItem(true)
            .generationCondition(hasOreProperty);
    /// 粗矿块
    public static final TagPrefix rawOreBlock = new TagPrefix("rawOreBlock")
            .idPattern("raw_%s_block")
            .defaultTagPath("storage_blocks/raw_%s")
            .unformattedTagPath("storage_blocks")
            .langValue("Block of Raw %s")
            .materialIconType(TcMaterialIconTypes.rawOreBlock)
            .miningToolTag(BlockTags.MINEABLE_WITH_PICKAXE)
            .unificationEnabled(true)
            .generateBlock(true)
            .generationCondition(hasOreProperty);

    /// 锭
    public static final TagPrefix ingot = new TagPrefix("ingot")
            .defaultTagPath("ingots/%s")
            .unformattedTagPath("ingots")
            .materialAmount(TcAPI.M)
            .materialIconType(TcMaterialIconTypes.ingot)
            .unificationEnabled(true)
            .enableRecycling()
            .generateItem(true)
            .generationCondition(hasIngotProperty);

    /// 宝石
    public static final TagPrefix gem = new TagPrefix("gem")
            .defaultTagPath("gems/%s")
            .unformattedTagPath("gems")
            .langValue("%s")
            .materialAmount(TcAPI.M)
            .materialIconType(TcMaterialIconTypes.gem)
            .unificationEnabled(true)
            .enableRecycling()
            .generateItem(true)
            .generationCondition(hasGemProperty);

    /// 粉末
    public static final TagPrefix dust = new TagPrefix("dust")
            .defaultTagPath("dusts/%s")
            .unformattedTagPath("dusts")
            .materialAmount(TcAPI.M)
            .materialIconType(TcMaterialIconTypes.dust)
            .unificationEnabled(true)
            .enableRecycling()
            .generateItem(true)
            .generationCondition(hasDustProperty);

    /// 粒
    public static final TagPrefix nugget = new TagPrefix("nugget")
            .defaultTagPath("nuggets/%s")
            .unformattedTagPath("nuggets")
            .materialAmount(TcAPI.M / 9)
            .materialIconType(TcMaterialIconTypes.nugget)
            .unificationEnabled(true)
            .enableRecycling()
            .generateItem(true)
            .generationCondition(hasIngotProperty);

    /// 板
    public static final TagPrefix plate = new TagPrefix("plate")
            .defaultTagPath("plates/%s")
            .unformattedTagPath("plates")
            .materialAmount(TcAPI.M)
            .materialIconType(TcMaterialIconTypes.plate)
            .unificationEnabled(true)
            .enableRecycling()
            .generateItem(true)
            .generationCondition(mat -> mat.hasFlag(MaterialFlags.GENERATE_PLATE));

    public static final TagPrefix lens = new TagPrefix("lens")
            .defaultTagPath("lenses/%s")
            .unformattedTagPath("lenses")
            .materialAmount((TcAPI.M * 3) / 4)
            .materialIconType(TcMaterialIconTypes.lens)
            .unificationEnabled(true)
            .enableRecycling()
            .generateItem(true)
            .generationCondition(mat -> mat.hasFlag(MaterialFlags.GENERATE_LENS));
    /// 长杆
    public static final TagPrefix rodLong = new TagPrefix("longRod")
            .idPattern("long_%s_rod")
            .defaultTagPath("rods/long/%s")
            .unformattedTagPath("rods/long")
            .langValue("Long %s Rod")
            .materialAmount(TcAPI.M)
            .materialIconType(TcMaterialIconTypes.rodLong)
            .unificationEnabled(true)
            .enableRecycling()
            .generateItem(true)
            .generationCondition(mat -> mat.hasFlag(MaterialFlags.GENERATE_LONG_ROD));

    /// 杆
    public static final TagPrefix rod = new TagPrefix("rod")
            .defaultTagPath("rods/%s")
            .unformattedTagPath("rods")
            .langValue("%s Rod")
            .materialAmount(TcAPI.M / 2)
            .materialIconType(TcMaterialIconTypes.rod)
            .unificationEnabled(true)
            .enableRecycling()
            .generateItem(true)
            .generationCondition(mat -> mat.hasFlag(MaterialFlags.GENERATE_ROD));

    /// 颜料
    public static final TagPrefix dye = new TagPrefix("dye")
            .defaultTagPath("dyes/%s")
            .unformattedTagPath("dyes")
            .materialAmount(-1);

    /// 块
    public static final TagPrefix block = new TagPrefix("block")
            .defaultTagPath("storage_blocks/%s")
            .unformattedTagPath("storage_blocks")
            .langValue("Block of %s")
            .materialAmount(TcAPI.M * 9)
            .materialIconType(TcMaterialIconTypes.block)
            .miningToolTag(BlockTags.MINEABLE_WITH_PICKAXE)
            .generateBlock(true)
            .generationCondition(material -> material.hasProperty(PropertyKey.INGOT) ||
                    material.hasProperty(PropertyKey.GEM) || material.hasFlag(MaterialFlags.FORCE_GENERATE_BLOCK))
            .unificationEnabled(true)
            .enableRecycling();

    /// 原木
    public static final TagPrefix log = new TagPrefix("log")
            .unformattedTagPath("logs", true);
    /// 木板
    public static final TagPrefix planks = new TagPrefix("planks")
            .unformattedTagPath("planks", true);
    /// 半砖
    public static final TagPrefix slab = new TagPrefix("slab")
            .unformattedTagPath("slabs", true);
    /// 楼梯
    public static final TagPrefix stairs = new TagPrefix("stairs")
            .unformattedTagPath("stairs", true);
    /// 栅栏
    public static final TagPrefix fence = new TagPrefix("fence")
            .unformattedTagPath("fences");
    /// 栅栏门
    public static final TagPrefix fenceGate = new TagPrefix("fenceGate")
            .unformattedTagPath("fence_gates");
    /// 门
    public static final TagPrefix door = new TagPrefix("door")
            .unformattedTagPath("doors", true);

    /// 岩石
    public static final TagPrefix rock = new TagPrefix("rock")
            .defaultTagPath("%s")
            .langValue("%s")
            .miningToolTag(BlockTags.MINEABLE_WITH_PICKAXE)
            .unificationEnabled(false)
            .generateBlock(true) // generate a block but not really, for TagPrefix#setIgnoredBlock
            .generationCondition((material) -> false);
}
