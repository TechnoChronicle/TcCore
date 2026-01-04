package net.technochronicle.tccore.api.item.component;

import net.technochronicle.tccore.api.item.IComponentItem;

import net.minecraft.world.item.Item;

/**
 * 描述可附加到{@link IComponentItem}的通用组件
 * 一个项目可以连接多个组件
 */
public interface IItemComponent {

    default void onAttached(Item item) {}
}
