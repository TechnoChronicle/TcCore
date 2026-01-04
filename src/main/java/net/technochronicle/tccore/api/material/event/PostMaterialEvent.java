package net.technochronicle.tccore.api.material.event;

import net.neoforged.bus.api.Event;
import net.neoforged.fml.event.IModBusEvent;

/**
 * 事件用于修改和对材料进行后处理
 * <br/>
 * 材料事件在MOD总线中触发，因为forge总线在所有模组加载完成之前不会激活。
 */

public class PostMaterialEvent extends Event implements IModBusEvent {

    public PostMaterialEvent() {
        super();
    }
}
