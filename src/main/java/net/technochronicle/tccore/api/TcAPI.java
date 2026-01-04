package net.technochronicle.tccore.api;

import net.technochronicle.tclib.mixin.RegisterEventAccessor;

import net.technochronicle.tccore.api.material.registry.IMaterialRegistry;
import net.technochronicle.tccore.api.registry.TcRegistry;

import net.minecraft.util.RandomSource;
import net.neoforged.fml.ModLoader;
import net.neoforged.neoforge.registries.RegisterEvent;

import org.jetbrains.annotations.ApiStatus;

public class TcAPI {

    /**
     * <p/>
     * 这正好相当于一个正常物品的价值。
     * 这个常数可以被许多常用数字整除，例如
     * 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 12, 14, 15, 16, 18, 20, 21, 24, ... 64 或 81
     * 而不会丢失精度，因此被用作数量的单位。
     * 但它也足够小，可以与更大的数字相乘。
     * <p/>
     * 这用于确定前缀矿石中所含材料的数量。
     * 例如，Nugget = M / 9，因为它由 Ingot 的 1/9 组成。
     */
    public static final long M = 3628800;

    /**
     * 从 "FLUID_MATERIAL_UNIT" 重命名为 "L"
     * <p/>
     * 每个材料单位的流体量（质因数：3 * 3 * 2 * 2 * 2 * 2）
     */
    public static final int L = 144;

    public static final long CLIENT_TIME = 0;
    public static final RandomSource RNG = RandomSource.createThreadSafe();

    public static IMaterialRegistry materialManager;

    /**
     * 为特定的注册器发布注册事件。仅内部使用，不要尝试调用此方法。
     */
    @ApiStatus.Internal
    public static <T> void postRegisterEvent(TcRegistry<T> registry) {
        RegisterEvent registerEvent = RegisterEventAccessor.create(registry.key(), registry);
        ModLoader.postEventWrapContainerInModOrder(registerEvent);
    }
}
