package marsh.town.brb.Mixins;

import marsh.town.brb.BetterRecipeBook;
import marsh.town.brb.BrewingStand.BrewableResult;
import marsh.town.brb.BrewingStand.BrewingRecipeBookComponent;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.ImageButton;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.gui.screens.inventory.BrewingStandScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.BrewingStandMenu;
import net.minecraft.world.inventory.ClickType;
import net.minecraft.world.inventory.Slot;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyArg;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(BrewingStandScreen.class)
public abstract class BrewingStand extends AbstractContainerScreen<BrewingStandMenu> {
    public final BrewingRecipeBookComponent recipeBookComponent = new BrewingRecipeBookComponent();
    private static final ResourceLocation RECIPE_BUTTON_LOCATION = new ResourceLocation("textures/gui/recipe_button.png");
    private boolean widthNarrow;

    public BrewingStand(BrewingStandMenu handler, Inventory inventory, Component title) {
        super(handler, inventory, title);
    }

    @Inject(method = "init", at = @At("RETURN"))
    protected void init(CallbackInfo ci) {
        if (BetterRecipeBook.config.enableBook) {
            this.widthNarrow = this.width < 379;
            assert this.minecraft != null;
            this.recipeBookComponent.initialize(this.width, this.height, this.minecraft, widthNarrow, this.menu);

            if (!BetterRecipeBook.config.keepCentered) {
                this.leftPos = this.recipeBookComponent.findLeftEdge(this.width, this.imageWidth);
            }

            this.addRenderableWidget(new ImageButton(this.leftPos + 135, this.height / 2 - 50, 20, 18, 0, 0, 19, RECIPE_BUTTON_LOCATION, (button) -> {
                this.recipeBookComponent.toggleOpen();
                BetterRecipeBook.rememberedBrewingOpen = this.recipeBookComponent.isOpen();
                if (!BetterRecipeBook.config.keepCentered) {
                    this.leftPos = this.recipeBookComponent.findLeftEdge(this.width, this.imageWidth);
                }
                button.setPosition(this.leftPos + 135, this.height / 2 - 50);
            }));

            this.addWidget(this.recipeBookComponent);
            this.setInitialFocus(this.recipeBookComponent);
        }
    }

    @Override
    protected void slotClicked(Slot slot, int x, int y, ClickType clickType) {
        // clear ghost recipe if an empty ingredient slot is clicked with no items
        if (slot != null && slot.index < 4 && menu.getCarried().isEmpty() && menu.slots.get(slot.index).getItem().isEmpty()) {
            recipeBookComponent.ghostRecipe.clear();
        }

        super.slotClicked(slot, x, y, clickType);

        // clear ghostRecipe if ingredients match
        BrewableResult result = recipeBookComponent.recipesArea.getLastClickedRecipe();
        if (slot != null && slot.index < 4 && result != null && recipeBookComponent.currentTab != null && result.hasMaterials(recipeBookComponent.currentTab.getGroup(), menu.slots.subList(0, 4))) {
            recipeBookComponent.ghostRecipe.clear();
        }
    }

    /**
     * @author marshmallow
     */
    @Overwrite
    public void render(GuiGraphics gui, int mouseX, int mouseY, float delta) {
        // this.drawStatusEffects = !this.recipeBook.isOpen();
        this.renderBackground(gui);

        // clear ghost recipe if book is closed
        if (!recipeBookComponent.isOpen() && recipeBookComponent.ghostRecipe.size() > 0) {
            recipeBookComponent.ghostRecipe.clear();
        }

        if (this.recipeBookComponent.isOpen() && this.widthNarrow) {
            this.renderBg(gui, delta, mouseX, mouseY);
            super.render(gui, mouseX, mouseY, delta);
            this.renderTooltip(gui, mouseX, mouseY);
        } else {
            this.recipeBookComponent.render(gui, mouseX, mouseY, delta);
            super.render(gui, mouseX, mouseY, delta);
            this.recipeBookComponent.drawGhostSlots(gui, this.leftPos, this.topPos, false, delta);
        }

        this.renderTooltip(gui, mouseX, mouseY);
        this.recipeBookComponent.drawTooltip(gui, this.leftPos, this.topPos, mouseX, mouseY);
    }

    @ModifyArg(
            method = "renderBg",
            index = 1,
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/client/gui/GuiGraphics;blit(Lnet/minecraft/resources/ResourceLocation;IIIIII)V"
            )
    )
    public int drawBackground(int i) {
        if (this.recipeBookComponent.isOpen() && !BetterRecipeBook.config.keepCentered) {
            return i + 77;
        } else {
            return i;
        }
    }
}
