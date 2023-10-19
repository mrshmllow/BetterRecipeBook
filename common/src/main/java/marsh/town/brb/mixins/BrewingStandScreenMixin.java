package marsh.town.brb.mixins;

import marsh.town.brb.BetterRecipeBook;
import marsh.town.brb.brewingstand.BrewableResult;
import marsh.town.brb.brewingstand.BrewingRecipeBookComponent;
import marsh.town.brb.util.BRBTextures;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.ImageButton;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.gui.screens.inventory.BrewingStandScreen;
import net.minecraft.client.gui.screens.recipebook.RecipeUpdateListener;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.BrewingStandMenu;
import net.minecraft.world.inventory.ClickType;
import net.minecraft.world.inventory.Slot;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(BrewingStandScreen.class)
public abstract class BrewingStandScreenMixin extends AbstractContainerScreen<BrewingStandMenu> implements RecipeUpdateListener {

    @Unique public final BrewingRecipeBookComponent _$recipeBookComponent = new BrewingRecipeBookComponent();
    @Unique private boolean _$widthNarrow;

    public BrewingStandScreenMixin(BrewingStandMenu handler, Inventory inventory, Component title) {
        super(handler, inventory, title);
    }

    @Inject(method = "init", at = @At("RETURN"))
    protected void init(CallbackInfo ci) {
        if (BetterRecipeBook.config.enableBook) {
            this._$widthNarrow = this.width < 379;
            assert this.minecraft != null;
            this._$recipeBookComponent.initialize(this.width, this.height, this.minecraft, _$widthNarrow, this.menu);

            if (!BetterRecipeBook.config.keepCentered) {
                this.leftPos = this._$recipeBookComponent.findLeftEdge(this.width, this.imageWidth);
            }

            this.addRenderableWidget(new ImageButton(this.leftPos + 135, this.height / 2 - 50, 20, 18, BRBTextures.RECIPE_BOOK_BUTTON_SPRITES, (button) -> {
                this._$recipeBookComponent.toggleOpen();
                BetterRecipeBook.rememberedBrewingOpen = this._$recipeBookComponent.isOpen();
                if (!BetterRecipeBook.config.keepCentered) {
                    this.leftPos = this._$recipeBookComponent.findLeftEdge(this.width, this.imageWidth);
                }
                button.setPosition(this.leftPos + 135, this.height / 2 - 50);
            }));

            this.addWidget(this._$recipeBookComponent);
        }
    }

    @Override
    protected void slotClicked(Slot slot, int x, int y, ClickType clickType) {
        // clear ghost recipe if an empty ingredient slot is clicked with no items
        if (slot != null && slot.index < 4 && menu.getCarried().isEmpty() && menu.slots.get(slot.index).getItem().isEmpty()) {
            _$recipeBookComponent.ghostRecipe.clear();
        }

        super.slotClicked(slot, x, y, clickType);

        // clear ghostRecipe if ingredients match
        BrewableResult result = _$recipeBookComponent.recipesArea.getLastClickedRecipe();
        if (slot != null && slot.index < 4 && result != null && _$recipeBookComponent.currentTab != null && result.hasMaterials(_$recipeBookComponent.currentTab.getGroup(), menu.slots.subList(0, 4))) {
            _$recipeBookComponent.ghostRecipe.clear();
        }
    }

    @Override
    protected boolean hasClickedOutside(double d, double e, int i, int j, int k) {
        boolean bl = d < (double)i || e < (double)j || d >= (double)(i + this.imageWidth) || e >= (double)(j + this.imageHeight);
        return this._$recipeBookComponent.hasClickedOutside(d, e, this.leftPos, this.topPos, this.imageWidth, this.imageHeight, k) && bl;
    }

    /**
     * @author marshmallow
     */
    @Overwrite
    public void render(GuiGraphics gui, int mouseX, int mouseY, float delta) {
        this.renderBackground(gui, mouseX, mouseY, delta);

        // clear ghost recipe if book is closed
        if (!_$recipeBookComponent.isOpen() && _$recipeBookComponent.ghostRecipe.size() > 0) {
            _$recipeBookComponent.ghostRecipe.clear();
        }

        if (this._$recipeBookComponent.isOpen() && this._$widthNarrow) {
            this.renderBg(gui, delta, mouseX, mouseY);
            super.render(gui, mouseX, mouseY, delta);
            this.renderTooltip(gui, mouseX, mouseY);
        } else {
            this._$recipeBookComponent.render(gui, mouseX, mouseY, delta);
            super.render(gui, mouseX, mouseY, delta);
            this._$recipeBookComponent.drawGhostSlots(gui, this.leftPos, this.topPos, false, delta);
        }

        this.renderTooltip(gui, mouseX, mouseY);
        this._$recipeBookComponent.drawTooltip(gui, this.leftPos, this.topPos, mouseX, mouseY);
    }

    // fix brewing progress indicator offset when recipe book is open by modifying the width offset
    @ModifyVariable(
            method = "renderBg",
            index = 5,
            at = @At("STORE")
    )
    public int renderBg_width(int i) {
        if (this._$recipeBookComponent.isOpen() && !BetterRecipeBook.config.keepCentered) {
            return i + 77;
        } else {
            return i;
        }
    }

    @Override
    public BrewingRecipeBookComponent getRecipeBookComponent() {
        return _$recipeBookComponent;
    }

    @Override
    public void recipesUpdated() {
        _$recipeBookComponent.recipesUpdated();
    }

}
