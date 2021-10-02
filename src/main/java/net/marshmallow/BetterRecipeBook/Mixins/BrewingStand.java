package net.marshmallow.BetterRecipeBook.Mixins;

import net.marshmallow.BetterRecipeBook.BrewingStand.BrewingStandRecipeBookWidget;
import net.minecraft.client.gui.screen.ingame.BrewingStandScreen;
import net.minecraft.client.gui.screen.ingame.HandledScreen;
import net.minecraft.client.gui.widget.TexturedButtonWidget;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.screen.BrewingStandScreenHandler;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyArg;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(BrewingStandScreen.class)
public abstract class BrewingStand extends HandledScreen<BrewingStandScreenHandler> {
    private final BrewingStandRecipeBookWidget recipeBook = new BrewingStandRecipeBookWidget();
    private static final Identifier RECIPE_BUTTON_TEXTURE = new Identifier("textures/gui/recipe_button.png");
    @Final
    @Shadow
    private static int[] BUBBLE_PROGRESS;
    @Final
    @Shadow
    private static Identifier TEXTURE;
    private boolean open;
    private boolean mouseDown;
    private boolean narrow;

    public BrewingStand(BrewingStandScreenHandler handler, PlayerInventory inventory, Text title) {
        super(handler, inventory, title);
    }

    @Inject(method = "init", at = @At("RETURN"))
    protected void init(CallbackInfo ci) {
        this.narrow = this.width < 379;
        this.recipeBook.initialize(this.width, this.height, this.client, narrow, this.handler);
        this.open = true;
        this.x = this.recipeBook.findLeftEdge(this.width, this.backgroundWidth);
        this.addDrawableChild(new TexturedButtonWidget(this.x + 104, this.height / 2 - 22, 20, 18, 0, 0, 19, RECIPE_BUTTON_TEXTURE, (button) -> {
            this.recipeBook.toggleOpen();
            this.x = this.recipeBook.findLeftEdge(this.width, this.backgroundWidth);
            ((TexturedButtonWidget)button).setPos(this.x + 104, this.height / 2 - 22);
            this.mouseDown = true;
        }));
        this.addSelectableChild(this.recipeBook);
        this.setInitialFocus(this.recipeBook);
    }

    /**
     * @author
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
        // this.mouseX = (float)mouseX;
        // this.mouseY = (float)mouseY;
    }

    @ModifyArg(method = "drawBackground", index = 1, at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/screen/ingame/BrewingStandScreen;drawTexture(Lnet/minecraft/client/util/math/MatrixStack;IIIIII)V"))
    public int drawBackground(int i) {
        if (this.recipeBook.isOpen()) {
            return (this.width - this.backgroundWidth) / 2 + 77;
        } else {
            return i;
        }
    }
}
