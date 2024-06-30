package marsh.town.brb.mixins;

import marsh.town.brb.BetterRecipeBook;
import marsh.town.brb.brewingstand.BrewingRecipeBookComponent;
import marsh.town.brb.util.BRBTextures;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.ImageButton;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.gui.screens.inventory.BrewingStandScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.BrewingStandMenu;
import net.minecraft.world.inventory.ClickType;
import net.minecraft.world.inventory.Slot;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(BrewingStandScreen.class)
public abstract class BrewingStandScreenMixin extends AbstractContainerScreen<BrewingStandMenu> {

    @Unique
    public final BrewingRecipeBookComponent _$recipeBookComponent = new BrewingRecipeBookComponent();
    @Unique
    private boolean _$widthNarrow;

    public BrewingStandScreenMixin(BrewingStandMenu handler, Inventory inventory, Component title) {
        super(handler, inventory, title);
    }

    @Inject(method = "init", at = @At("RETURN"))
    protected void init(CallbackInfo ci) {
        if (BetterRecipeBook.config.enableBook) {
            this._$widthNarrow = this.width < 379;
            assert this.minecraft != null;
            this._$recipeBookComponent.init(this.width, this.height, this.minecraft, _$widthNarrow, this.menu, Minecraft.getInstance().getConnection().registryAccess());

            if (!BetterRecipeBook.config.keepCentered) {
                this.leftPos = this._$recipeBookComponent.findLeftEdge(this.width, this.imageWidth);
            }

            this.addRenderableWidget(new ImageButton(this.leftPos + 135, this.height / 2 - 50, 20, 18, BRBTextures.RECIPE_BOOK_BUTTON_SPRITES, (button) -> {
                this._$recipeBookComponent.toggleVisibility();
                if (!BetterRecipeBook.config.keepCentered) {
                    this.leftPos = this._$recipeBookComponent.findLeftEdge(this.width, this.imageWidth);
                }
                button.setPosition(this.leftPos + 135, this.height / 2 - 50);
            }));

            this.addWidget(this._$recipeBookComponent);
        }
    }

    @Override
    public boolean keyPressed(int i, int j, int k) {
        if (_$recipeBookComponent.keyPressed(i, j, k)) {
            return true;
        }
        return super.keyPressed(i, j, k);
    }

    @Override
    public boolean keyReleased(int i, int j, int k) {
        if (_$recipeBookComponent.keyReleased(i, j, k)) {
            return true;
        }
        return super.keyReleased(i, j, k);
    }

    @Override
    public boolean charTyped(char c, int i) {
        if (_$recipeBookComponent.charTyped(c, i)) {
            return true;
        }
        return super.charTyped(c, i);
    }

    @Override
    protected void slotClicked(Slot slot, int x, int y, ClickType clickType) {
        // clear ghost recipe if an empty ingredient slot is clicked with no items
        if (slot != null && slot.index < 4 && menu.slots.get(slot.index).getItem().isEmpty()) {
            _$recipeBookComponent.ghostRecipe.clear();
        }

        super.slotClicked(slot, x, y, clickType);
    }

    @Override
    protected boolean hasClickedOutside(double d, double e, int i, int j, int k) {
        boolean bl = d < (double) i || e < (double) j || d >= (double) (i + this.imageWidth) || e >= (double) (j + this.imageHeight);
        return this._$recipeBookComponent.hasClickedOutside(d, e, this.leftPos, this.topPos, this.imageWidth, this.imageHeight, k) && bl;
    }

    @Inject(method = "render", at = @At("RETURN"))
    public void render(GuiGraphics guiGraphics, int i, int j, float f, CallbackInfo ci) {
        if (this._$recipeBookComponent.isVisible()) {
            this._$recipeBookComponent.render(guiGraphics, i, j, f);
            this._$recipeBookComponent.renderGhostRecipe(guiGraphics, this.leftPos, this.topPos, false, f);
            this._$recipeBookComponent.drawTooltip(guiGraphics, this.leftPos, this.topPos, i, j);
        }
    }

    // fix brewing progress indicator offset when recipe book is open by modifying the width offset
    @ModifyVariable(
            method = "renderBg",
            index = 5,
            at = @At("STORE")
    )
    public int renderBg_width(int i) {
        if (this._$recipeBookComponent.isVisible() && !BetterRecipeBook.config.keepCentered) {
            return i + 77;
        } else {
            return i;
        }
    }
}
