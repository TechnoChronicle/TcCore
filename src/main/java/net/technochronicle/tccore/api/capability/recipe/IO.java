package net.technochronicle.tccore.api.capability.recipe;

import lombok.Getter;

/**
 * The capability can be input or output or both
 */
public enum IO {

    IN("tclib.io.import", "import"),
    OUT("tclib.io.export", "export"),
    BOTH("tclib.io.both", "both"),
    NONE("tclib.io.none", "none");

    @Getter
    public final String tooltip;

    IO(String tooltip, String textureName) {
        this.tooltip = tooltip;
    }

    public boolean support(IO io) {
        if (io == this) return true;
        if (io == NONE) return false;
        return this == BOTH;
    }
}
