package marsh.town.brb.mixins.accessors;

import net.minecraft.stats.RecipeBookSettings;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(RecipeBookSettings.TypeSettings.class)
public interface BookSettingsTypeSettingsAccessor {
    @Accessor("filtering")
    boolean isFiltering();

    @Accessor("filtering")
    void setFiltering(boolean value);

    @Accessor("open")
    boolean isOpen();

    @Accessor("open")
    void setOpen(boolean value);
}
