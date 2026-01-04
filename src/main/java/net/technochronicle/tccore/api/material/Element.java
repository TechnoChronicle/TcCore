package net.technochronicle.tccore.api.material;

import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

/**
 * 这是一种周期表元素，可用于确定材料的"属性"。
 */
@Accessors(fluent = true, chain = false)
public class Element {

    /**
     * 质子数
     */
    @Getter
    @Setter
    private long protons;
    /**
     * 中子数
     */
    @Getter
    @Setter
    private long neutrons;
    /**
     * 该元素的半衰期（秒）。稳定材料为-1。
     */
    @Getter
    @Setter
    private long halfLifeSeconds;
    /**
     * 表示该元素衰变为何种元素的字符串。以'&'字符分隔。
     */
    @Getter
    @Setter
    private String decayTo;
    /**
     * 元素名称
     */
    @Getter
    @Setter
    private String name;
    /**
     * 元素符号
     */
    @Getter
    @Setter
    private String symbol;
    /**
     * 该元素是否为同位素？
     */
    @Getter
    @Setter
    private boolean isIsotope;

    public long mass() {
        return protons + neutrons;
    }

    public Element(long protons, long neutrons, long halfLifeSeconds, String decayTo, String name, String symbol,
                   boolean isIsotope) {
        this.protons = protons;
        this.neutrons = neutrons;
        this.halfLifeSeconds = halfLifeSeconds;
        this.decayTo = decayTo;
        this.name = name;
        this.symbol = symbol;
        this.isIsotope = isIsotope;
    }
}
