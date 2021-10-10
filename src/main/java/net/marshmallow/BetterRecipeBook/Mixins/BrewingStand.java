package net.marshmallow.BetterRecipeBook.Mixins;

import net.marshmallow.BetterRecipeBook.BetterRecipeBook;
import net.marshmallow.BetterRecipeBook.BrewingStand.BrewingStandRecipeBookWidget;
import net.minecraft.client.gui.screen.ingame.BrewingStandScreen;
import net.minecraft.client.gui.screen.ingame.HandledScreen;
import net.minecraft.client.gui.widget.TexturedButtonWidget;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.screen.BrewingStandScreenHandler;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyArg;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(BrewingStandScreen.class)
public abstract class BrewingStand extends HandledScreen<BrewingStandScreenHandler> {
    private final BrewingStandRecipeBookWidget recipeBook = new BrewingStandRecipeBookWidget();
    private static final Identifier RECIPE_BUTTON_TEXTURE = new Identifier("textures/gui/recipe_button.png");
    private boolean narrow;

    public BrewingStand(BrewingStandScreenHandler handler, PlayerInventory inventory, Text title) {
        super(handler, inventory, title);
    }

    @Inject(method = "init", at = @At("RETURN"))
    protected void init(CallbackInfo ci) {
        if (BetterRecipeBook.config.enableBook) {
            this.narrow = this.width < 379;
            assert this.client != null;
            this.recipeBook.initialize(this.width, this.height, this.client, narrow, this.handler);

            if (!BetterRecipeBook.config.keepCentered) {
                this.x = this.recipeBook.findLeftEdge(this.width, this.backgroundWidth);
            }

            this.addDrawableChild(new TexturedButtonWidget(this.x + 135, this.height / 2 - 50, 20, 18, 0, 0, 19, RECIPE_BUTTON_TEXTURE, (button) -> {
                this.recipeBook.toggleOpen();
                BetterRecipeBook.rememberedBrewingOpen = this.recipeBook.isOpen();
                if (!BetterRecipeBook.config.keepCentered) {
                    this.x = this.recipeBook.findLeftEdge(this.width, this.backgroundWidth);
                }
                ((TexturedButtonWidget)button).setPos(this.x + 135, this.height / 2 - 50);
            }));

            this.addSelectableChild(this.recipeBook);
            this.setInitialFocus(this.recipeBook);
        }
    }

    /**
     * @author marshmallow
     */
    @Overwrite
    public void render(MatrixStack matrices, int mouseX, int mouseY, float delta) {
        // this.drawStatusEffects = !this.recipeBook.isOpen();
        this.renderBackground(matrices);
        if (this.recipeBook.isOpen() && this.narrow) {
            this.drawBackground(matrices, delta, mouseX, mouseY);
            super.render(matrices, mouseX, mouseY, delta);
            this.drawMouseoverTooltip(matrices, mouseX, mouseY);
        } else {
            this.recipeBook.render(matrices, mouseX, mouseY, delta);
            super.render(matrices, mouseX, mouseY, delta);
            this.recipeBook.drawGhostSlots(matrices, this.x, this.y, false, delta);
        }

        this.drawMouseoverTooltip(matrices, mouseX, mouseY);
        this.recipeBook.drawTooltip(matrices, this.x, this.y, mouseX, mouseY);
    }

    @ModifyArg(
            method = "drawBackground",
            index = 1,
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/client/gui/screen/ingame/BrewingStandScreen;drawTexture(Lnet/minecraft/client/util/math/MatrixStack;IIIIII)V"
            )
    )
    public int drawBackground(int i) {
        if (this.recipeBook.isOpen() && !BetterRecipeBook.config.keepCentered) {
            return i + 77;
        } else {
            return i;
        }
    }
}
