package marsh.town.brb.util;


import net.minecraft.resources.ResourceLocation;

public record WidgetSprites(ResourceLocation enabled, ResourceLocation disabled, ResourceLocation enabledFocused,
                            ResourceLocation disabledFocused) {
    public WidgetSprites(ResourceLocation var1, ResourceLocation var2) {
        this(var1, var1, var2, var2);
    }

    public ResourceLocation get(boolean enabled, boolean focused) {
        if (enabled) {
            return focused ? this.enabledFocused : this.enabled;
        } else {
            return focused ? this.disabledFocused : this.disabled;
        }
    }
}
