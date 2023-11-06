package marsh.town.brb.mixins.unlockrecipes;

import marsh.town.brb.interfaces.unlockrecipes.IMixinRecipeManager;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.crafting.RecipeManager;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;

import java.util.HashSet;
import java.util.Set;

@Mixin(RecipeManager.class)
public class RecipeManagerMixin implements IMixinRecipeManager {

    // map for keeping track of unlocked recipes
    // we want this to be an instance variable to avoid any funny business
    @Unique
    private final Set<ResourceLocation> serverUnlockedRecipes = new HashSet<>();

    @Override
    public Set<ResourceLocation> _$getServerUnlockedRecipes() {
        return serverUnlockedRecipes;
    }

}
