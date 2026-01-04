package net.technochronicle.tccore.api.attribute;

import net.minecraft.core.Holder;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EquipmentSlotGroup;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.item.component.ItemAttributeModifiers;

import com.google.common.collect.Multimap;
import lombok.Getter;

import java.util.List;

/**
 * 就是三个数 给attribute用的 分别表示加 混合乘区 独立乘区
 */
public record TriNum(double v1, double v2, double v3) {

    public TriNum() {
        this(0, 0, 1);
    }

    public TriNum add1(double value) {
        return new TriNum(v1 + value, v2, v3);
    }

    public TriNum add2(double value) {
        return new TriNum(v1, v2 + value, v3);
    }

    public TriNum add3(double value) {
        return new TriNum(v1, v2, v3 * (1 + value));
    }

    public TriNum add(double v1, double v2, double v3) {
        return new TriNum(this.v1 + v1, this.v2 + v2, this.v3 * (1 + v3));
    }

    public void createAttributeModifier(Holder<Attribute> attribute, ResourceLocation id, Multimap<Holder<Attribute>, AttributeModifier> map) {
        if (this.v1 != 0) {
            map.put(attribute, new AttributeModifier(id.withSuffix("/stage1"), v1, AttributeModifier.Operation.ADD_VALUE));
        }
        if (this.v2 != 0) {
            map.put(attribute, new AttributeModifier(id.withSuffix("/stage2"), v2, AttributeModifier.Operation.ADD_MULTIPLIED_BASE));
        }
        if (this.v3 != 1) {
            map.put(attribute, new AttributeModifier(id.withSuffix("/stage3"), v3 - 1, AttributeModifier.Operation.ADD_MULTIPLIED_TOTAL));
        }
    }

    public void createItemAttributeModifier(Holder<Attribute> attribute, EquipmentSlotGroup group, ResourceLocation id, List<ItemAttributeModifiers.Entry> list) {
        if (this.v1 != 0) {
            list.add(new ItemAttributeModifiers.Entry(attribute, new AttributeModifier(id.withSuffix("/stage1"), v1, AttributeModifier.Operation.ADD_VALUE), group));
        }
        if (this.v2 != 0) {
            list.add(new ItemAttributeModifiers.Entry(attribute, new AttributeModifier(id.withSuffix("/stage2"), v2, AttributeModifier.Operation.ADD_MULTIPLIED_BASE), group));
        }
        if (this.v3 != 1) {
            list.add(new ItemAttributeModifiers.Entry(attribute, new AttributeModifier(id.withSuffix("/stage3"), v3 - 1, AttributeModifier.Operation.ADD_MULTIPLIED_TOTAL), group));
        }
    }

    public static class Mutable {

        @Getter
        public double v1 = 0, v2 = 0, v3 = 1;

        public Mutable add1(double value) {
            v1 += value;
            return this;
        }

        public Mutable add2(double value) {
            v2 += value;
            return this;
        }

        public Mutable add3(double value) {
            v3 *= (1 + value);
            return this;
        }

        public Mutable add(AttributeModifier.Operation op, double value) {
            return switch (op) {
                case ADD_VALUE -> add1(value);
                case ADD_MULTIPLIED_BASE -> add2(value);
                case ADD_MULTIPLIED_TOTAL -> add3(value);
            };
        }

        public Mutable add(double v1, double v2, double v3) {
            this.v1 += v1;
            this.v2 += v2;
            this.v3 *= (1 + v3);
            return this;
        }

        public Mutable add(TriNum triNum) {
            return add(triNum.v1, triNum.v2, triNum.v3);
        }

        public TriNum build() {
            return new TriNum(this.v1, this.v2, this.v3);
        }
    }
}
