package net.technochronicle.tccore.api.item;

import net.technochronicle.tccore.api.item.component.IItemComponent;

import net.minecraft.world.item.Item;

import lombok.Getter;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class ComponentItem extends Item implements IComponentItem {

    protected int burnTime = -1;

    @Getter
    protected List<IItemComponent> components;

    public ComponentItem(Properties properties) {
        super(properties);
        components = new ArrayList<>();
    }

    public void attachComponents(IItemComponent component) {
        this.components.add(component);
        component.onAttached(this);
    }

    public void attachComponents(IItemComponent... components) {
        this.components.addAll(Arrays.asList(components));
        for (IItemComponent component : components) {
            component.onAttached(this);
        }
    }

    public int getBurnTime() {
        return burnTime;
    }

    public void burnTime(int burnTime) {
        this.burnTime = burnTime;
    }
}
