package marsh.town.brb.mixins;

import marsh.town.brb.BetterRecipeBook;
import marsh.town.brb.smithingtable.SmithingRecipeBookComponent;
import marsh.town.brb.util.BRBTextures;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.ImageButton;
import net.minecraft.client.gui.screens.inventory.ItemCombinerScreen;
import net.minecraft.client.gui.screens.inventory.SmithingScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.ClickType;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.inventory.SmithingMenu;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

@Mixin(SmithingScreen.class)
public abstract class SmithingScreenMixin extends ItemCombinerScreen<SmithingMenu> {
    @Shadow protected abstract void renderOnboardingTooltips(GuiGraphics arg, int i, int j);

    @Unique
    public final SmithingRecipeBookComponent _$recipeBookComponent = new SmithingRecipeBookComponent();
    @Unique private boolean _$widthNarrow;

    public SmithingScreenMixin(SmithingMenu itemCombinerMenu, Inventory inventory, Component component, ResourceLocation resourceLocation) {
        super(itemCombinerMenu, inventory, component, resourceLocation);
    }

    @Inject(method = "subInit", at = @At("RETURN"))
    void init(CallbackInfo ci) {
        if (BetterRecipeBook.config.enableBook) {
            this._$widthNarrow = this.width < 379;
            this._$recipeBookComponent.initialize(this.width, this.height, _$widthNarrow, this.menu);

            if (!BetterRecipeBook.config.keepCentered) {
                this.leftPos = this._$recipeBookComponent.findLeftEdge(this.width, this.imageWidth);
            }

            // NOTE : width and height are both 0
            this.addRenderableWidget(new ImageButton(this.leftPos + 145, this.height / 2 - 70, 20, 18, BRBTextures.RECIPE_BOOK_BUTTON_SPRITES, (button) -> {
                this._$recipeBookComponent.toggleOpen();
                BetterRecipeBook.rememberedSmithingOpen = this._$recipeBookComponent.isOpen();
                if (!BetterRecipeBook.config.keepCentered) {
                    this.leftPos = this._$recipeBookComponent.findLeftEdge(this.width, this.imageWidth);
                }
                button.setPosition(this.leftPos + 145, this.height / 2 - 70);
            }));

            this.addWidget(this._$recipeBookComponent);
        }
    }

    @Override
    protected void slotClicked(Slot slot, int x, int y, ClickType clickType) {
        // clear ghost recipe if an empty ingredient slot is clicked with no items
        if (slot != null && slot.index < 4 && menu.getCarried().isEmpty() && menu.slots.get(slot.index).getItem().isEmpty()) {
//            _$recipeBookComponent.ghostRecipe.clear();
        }

        super.slotClicked(slot, x, y, clickType);

        // clear ghostRecipe if ingredients match
//        BrewableResult result = _$recipeBookComponent.recipesArea.getLastClickedRecipe();
//        if (slot != null && slot.index < 4 && result != null && _$recipeBookComponent.currentTab != null && result.hasMaterials(_$recipeBookComponent.currentTab.getGroup(), menu.slots.subList(0, 4))) {
//            _$recipeBookComponent.ghostRecipe.clear();
//        }
    }

    @Override
    protected boolean hasClickedOutside(double d, double e, int i, int j, int k) {
        boolean bl = d < (double)i || e < (double)j || d >= (double)(i + this.imageWidth) || e >= (double)(j + this.imageHeight);
        return this._$recipeBookComponent.hasClickedOutside(d, e, this.leftPos, this.topPos, this.imageWidth, this.imageHeight, k) && bl;
    }

    @Inject(method = "render", at = @At("HEAD"), locals = LocalCapture.CAPTURE_FAILSOFT)
    public void render(GuiGraphics guiGraphics, int i, int j, float f, CallbackInfo ci) {
        if (this._$recipeBookComponent.isOpen()) {
            this._$recipeBookComponent.render(guiGraphics, i, j, f);
            this._$recipeBookComponent.drawTooltip(guiGraphics, this.leftPos, this.topPos, i, j);
        }
    }

    @Unique
    public void recipesUpdated() {
        _$recipeBookComponent.recipesUpdated();
    }
}
