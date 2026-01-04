package net.technochronicle.tccore.api.material.property;

public class PropertyKey<T extends IMaterialProperty> {

    /**
     * 材料基本属性
     */
    public static final PropertyKey<DustProperty> DUST = new PropertyKey<>("dust", DustProperty.class);
    /**
     * 锭属性
     */
    public static final PropertyKey<IngotProperty> INGOT = new PropertyKey<>("ingot", IngotProperty.class);
    /**
     * 宝石属性
     */
    public static final PropertyKey<GemProperty> GEM = new PropertyKey<>("gem", GemProperty.class);
    /**
     * 流体属性
     */
    public static final PropertyKey<FluidProperty> FLUID = new PropertyKey<>("fluid", FluidProperty.class);
    /**
     * 熔炼属性
     */
    public static final PropertyKey<BlastProperty> BLAST = new PropertyKey<>("blast", BlastProperty.class);
    /**
     * 聚合物属性
     */
    public static final PropertyKey<PolymerProperty> POLYMER = new PropertyKey<>("polymer", PolymerProperty.class);
    /**
     * 工具属性
     */
    public static final PropertyKey<ToolProperty> TOOL = new PropertyKey<>("tool", ToolProperty.class);
    /**
     * 木材属性
     */
    public static final PropertyKey<WoodProperty> WOOD = new PropertyKey<>("wood", WoodProperty.class);
    /**
     * 矿石属性
     */
    public static final PropertyKey<OreProperty> ORE = new PropertyKey<>("ore", OreProperty.class);
    /**
     * 空属性，用于允许无属性的材料，同时不保留基础类型强制约束
     */
    public static final PropertyKey<EmptyProperty> EMPTY = new PropertyKey<>("empty", EmptyProperty.class);

    private final String key;
    private final Class<T> type;

    public PropertyKey(String key, Class<T> type) {
        this.key = key;
        this.type = type;
    }

    protected String getKey() {
        return key;
    }

    protected T constructDefault() {
        try {
            return type.newInstance();
        } catch (Exception e) {
            return null;
        }
    }

    public T cast(IMaterialProperty property) {
        return this.type.cast(property);
    }

    @Override
    public boolean equals(Object o) {
        if (o instanceof PropertyKey) {
            return ((PropertyKey<?>) o).getKey().equals(key);
        }
        return false;
    }

    @Override
    public int hashCode() {
        return key.hashCode();
    }

    @Override
    public String toString() {
        return key;
    }

    /**
     * 空属性 不执行任何操作
     */
    public static class EmptyProperty implements IMaterialProperty {

        private EmptyProperty() {}

        @Override
        public void verifyProperty(MaterialProperties properties) {
            // no-op
        }
    }
}
