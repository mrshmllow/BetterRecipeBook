package marsh.town.brb.mixins;

import marsh.town.brb.BetterRecipeBook;
import marsh.town.brb.smithingtable.SmithingGhostRecipe;
import marsh.town.brb.smithingtable.SmithingRecipeBookComponent;
import marsh.town.brb.util.BRBTextures;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.ImageButton;
import net.minecraft.client.gui.screens.inventory.CyclingSlotBackground;
import net.minecraft.client.gui.screens.inventory.ItemCombinerScreen;
import net.minecraft.client.gui.screens.inventory.SmithingScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ClickType;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.inventory.SmithingMenu;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(SmithingScreen.class)
public abstract class SmithingScreenMixin extends ItemCombinerScreen<SmithingMenu> {
    @Shadow protected abstract void updateArmorStandPreview(ItemStack itemStack);

    @Unique
    public final SmithingRecipeBookComponent _$recipeBookComponent = new SmithingRecipeBookComponent();
    @Unique
    private boolean _$widthNarrow;

    public SmithingScreenMixin(SmithingMenu itemCombinerMenu, Inventory inventory, Component component, ResourceLocation resourceLocation) {
        super(itemCombinerMenu, inventory, component, resourceLocation);
    }

    @Inject(method = "subInit", at = @At("RETURN"))
    void init(CallbackInfo ci) {
        if (BetterRecipeBook.config.enableBook) {
            this._$widthNarrow = this.width < 379;
            this._$recipeBookComponent.initialize(this.width, this.height, this.minecraft, _$widthNarrow, this.menu, s -> {
                if (s.getRecipe() != null) this.updateArmorStandPreview(s.getCurrentResult(Minecraft.getInstance().getConnection().registryAccess()));
            });

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
        boolean bl = d < (double) i || e < (double) j || d >= (double) (i + this.imageWidth) || e >= (double) (j + this.imageHeight);
        return this._$recipeBookComponent.hasClickedOutside(d, e, this.leftPos, this.topPos, this.imageWidth, this.imageHeight, k) && bl;
    }

    public void ghostRecipeChanged(SmithingGhostRecipe ghostRecipe) {

    }

    @Inject(method = "render", at = @At("RETURN"))
    public void render(GuiGraphics guiGraphics, int i, int j, float f, CallbackInfo ci) {
        if (this._$recipeBookComponent.isOpen()) {
            this._$recipeBookComponent.render(guiGraphics, i, j, f);
            this._$recipeBookComponent.renderGhostRecipe(guiGraphics, this.leftPos, this.topPos, false, f);
            this._$recipeBookComponent.drawTooltip(guiGraphics, this.leftPos, this.topPos, i, j);
        }
    }

//    @Inject(method = "renderBg", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/screens/inventory/CyclingSlotBackground;render(Lnet/minecraft/world/inventory/AbstractContainerMenu;Lnet/minecraft/client/gui/GuiGraphics;FII)V"), cancellable = true)
//    public void renderBg(GuiGraphics guiGraphics, float f, int i, int j, CallbackInfo ci) {
//        if (_$recipeBookComponent.isShowingGhostRecipe()) {
//            ci.cancel();
//        }
//    }

    @Redirect(method = "renderBg", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/screens/inventory/CyclingSlotBackground;render(Lnet/minecraft/world/inventory/AbstractContainerMenu;Lnet/minecraft/client/gui/GuiGraphics;FII)V"))
    public void renderBg(CyclingSlotBackground instance, AbstractContainerMenu slot, GuiGraphics bl, float g, int k, int arg) {
        if (!_$recipeBookComponent.isShowingGhostRecipe()) {
            instance.render(this.menu, bl, g, this.leftPos, this.topPos);
        }

        // pass, cancel render of onboarding tip slots if there is a ghost recipe
    }

    @Inject(method = "renderOnboardingTooltips", at = @At(value = "HEAD"), cancellable = true)
    public void renderOnboardingTooltips(GuiGraphics guiGraphics, int i, int j, CallbackInfo ci) {
        if (_$recipeBookComponent.isShowingGhostRecipe()) {
            ci.cancel();
        }
    }

    @Inject(method = "slotChanged", at = @At(value = "HEAD"))
    public void slotChanged(AbstractContainerMenu abstractContainerMenu, int i, ItemStack itemStack, CallbackInfo ci) {
        if (i == SmithingMenu.BASE_SLOT || i == SmithingMenu.ADDITIONAL_SLOT || i == SmithingMenu.TEMPLATE_SLOT || i == SmithingMenu.RESULT_SLOT) {
            _$recipeBookComponent.ghostRecipe.clear();
        }
    }

    @Unique
    public void recipesUpdated() {
        _$recipeBookComponent.recipesUpdated();
    }
}
