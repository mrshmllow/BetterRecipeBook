package marsh.town.brb.mixins.instantcraft;

import marsh.town.brb.BetterRecipeBook;
import marsh.town.brb.mixins.accessors.RecipeBookPageAccessor;
import marsh.town.brb.util.BRBTextures;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.components.StateSwitchingButton;
import net.minecraft.client.gui.screens.recipebook.RecipeBookComponent;
import net.minecraft.client.gui.screens.recipebook.RecipeBookPage;
import net.minecraft.client.gui.screens.recipebook.RecipeButton;
import net.minecraft.client.gui.screens.recipebook.RecipeCollection;
import net.minecraft.network.chat.Component;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

import java.util.List;

@Mixin(RecipeBookComponent.class)
public abstract class RecipeBookComponentMixin {

    @Shadow protected Minecraft minecraft;

    @Shadow public abstract boolean isVisible();

    @Shadow private int height;
    @Shadow private int width;
    @Shadow private int xOffset;

    @Shadow @Final private RecipeBookPage recipeBookPage;

    @Unique protected StateSwitchingButton _$instantCraftButton;
    @Unique private static final Component TOGGLE_INSTANT_CRAFT_ON_TEXT;
    @Unique private static final Component TOGGLE_INSTANT_CRAFT_OFF_TEXT;

    @Inject(method = "initVisuals", at = @At("RETURN"))
    public void reset(CallbackInfo ci) {
        if (!BetterRecipeBook.config.instantCraft.showButton) {
            return;
        }

        int i = (this.width - 147) / 2 - this.xOffset;
        int j = (this.height - 166) / 2;

        this._$instantCraftButton = new StateSwitchingButton(i + 110, j + 137, 26, 16 + 2, BetterRecipeBook.instantCraftingManager.on);
        this._$instantCraftButton.initTextureValues(BRBTextures.RECIPE_BOOK_INSTANT_CRAFT_BUTTON_SPRITES);
    }

    @Inject(method = "render", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/screens/recipebook/RecipeBookPage;render(Lnet/minecraft/client/gui/GuiGraphics;IIIIF)V"))
    public void render(GuiGraphics gui, int mouseX, int mouseY, float delta, CallbackInfo ci) {
        if (!BetterRecipeBook.config.instantCraft.showButton) {
            return;
        }

        this._$instantCraftButton.render(gui, mouseX, mouseY, delta);
    }

    @Inject(method = "mouseClicked", at = @At("HEAD"), cancellable = true)
    public void mouseClicked(double mouseX, double mouseY, int button, CallbackInfoReturnable<Boolean> cir) {
        if (this.isVisible() && BetterRecipeBook.config.instantCraft.showButton) {
            if (this._$instantCraftButton.mouseClicked(mouseX, mouseY, button)) {
                boolean bl = BetterRecipeBook.instantCraftingManager.toggleOn();
                this._$instantCraftButton.setStateTriggered(bl);
                cir.setReturnValue(true);
            }
        }
    }

    @Inject(method = "renderTooltip", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/screens/recipebook/RecipeBookComponent;renderGhostRecipeTooltip(Lnet/minecraft/client/gui/GuiGraphics;IIII)V"))
    public void drawTooltip(GuiGraphics gui, int x, int y, int mouseX, int mouseY, CallbackInfo ci) {
        if (!BetterRecipeBook.config.instantCraft.showButton) {
            return;
        }

        if (this._$instantCraftButton.isHoveredOrFocused()) {
            Component text = this._$instantCraftButton.isStateTriggered() ? TOGGLE_INSTANT_CRAFT_ON_TEXT : TOGGLE_INSTANT_CRAFT_OFF_TEXT;
            if (this.minecraft.screen != null) {
                gui.renderComponentTooltip(minecraft.font, List.of(text), mouseX, mouseY);
            }
        }
    }

    @Inject(method = "updateCollections", locals = LocalCapture.CAPTURE_FAILHARD, at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/screens/recipebook/RecipeBookPage;updateCollections(Ljava/util/List;Z)V"))
    private void updateCollections_Invoke(boolean bl, CallbackInfo ci, List<RecipeCollection> list, List<RecipeCollection> list2) {
        if (BetterRecipeBook.instantCraftingManager.on && this.recipeBookPage != null) {
            List<RecipeButton> buttons = ((RecipeBookPageAccessor) recipeBookPage).getButtons();
            RecipeButton btn = buttons.stream().filter(AbstractWidget::isHovered).findAny().orElse(null);
            if (btn != null) {
                RecipeCollection hoveredCollection = btn.getCollection();
                int idx = ((RecipeBookPageAccessor) recipeBookPage).getCollections().indexOf(hoveredCollection);
                if (idx != -1 && idx < list2.size()) {
                    BetterRecipeBook.currentHoveredRecipeCollection = hoveredCollection;
                    list2.remove(hoveredCollection);
                    list2.add(idx, hoveredCollection);
                }
            }
        }
    }

    static {
        TOGGLE_INSTANT_CRAFT_ON_TEXT = Component.translatable("brb.gui.instantCraft.on");
        TOGGLE_INSTANT_CRAFT_OFF_TEXT = Component.translatable("brb.gui.instantCraft.off");
    }
}
