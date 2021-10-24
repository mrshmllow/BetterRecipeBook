package net.marshmallow.BetterRecipeBook.BrewingStand;

import com.google.common.collect.Lists;
import com.mojang.blaze3d.systems.RenderSystem;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.marshmallow.BetterRecipeBook.BetterRecipeBook;
import net.marshmallow.BetterRecipeBook.Mixins.Accessors.BrewingRecipeRegistryRecipeAccessor;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.narration.NarrationMessageBuilder;
import net.minecraft.client.gui.screen.narration.NarrationPart;
import net.minecraft.client.gui.widget.ClickableWidget;
import net.minecraft.client.render.GameRenderer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionUtil;
import net.minecraft.screen.BrewingStandScreenHandler;
import net.minecraft.text.LiteralText;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.Formatting;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;

import java.util.List;

@Environment(EnvType.CLIENT)
public class BrewingAnimatedResultButton extends ClickableWidget {
    private float time;
    private static final Identifier BACKGROUND_TEXTURE = new Identifier("textures/gui/recipe_book.png");
    private BrewingResult potionRecipe;
    private BrewingRecipeBookGroup group;
    private BrewingStandScreenHandler brewingStandScreenHandler;

    public BrewingAnimatedResultButton() {
        super(0, 0, 25, 25, LiteralText.EMPTY);
    }

    public void showPotionRecipe(BrewingResult potionRecipe, BrewingRecipeBookGroup group, BrewingStandScreenHandler brewingStandScreenHandler) {
        this.potionRecipe = potionRecipe;
        this.group = group;
        this.brewingStandScreenHandler = brewingStandScreenHandler;
    }

    public void renderButton(MatrixStack matrices, int mouseX, int mouseY, float delta) {
        if (!Screen.hasControlDown()) {
            this.time += delta;
        }

        MinecraftClient minecraftClient = MinecraftClient.getInstance();
        RenderSystem.setShader(GameRenderer::getPositionTexShader);

        int i;
        int j;
        if (BetterRecipeBook.pinnedRecipeManager.hasPotion(potionRecipe.recipe)) {
            RenderSystem.setShaderTexture(0, new Identifier("betterrecipebook:textures/gui/pinned.png"));
            i = 25;
            j = 0;
        } else {
            RenderSystem.setShaderTexture(0, BACKGROUND_TEXTURE);
            i = 29 + 25;
            j = 206;
        }

        if (potionRecipe.hasMaterials(group, brewingStandScreenHandler)) {
            i -= 25;
        }

        MatrixStack matrixStack = RenderSystem.getModelViewStack();
        this.drawTexture(matrices, this.x, this.y, i, j, this.width, this.height);
        int k = 4;

        matrixStack.push();
        matrixStack.method_34425(matrices.peek().getModel().copy()); // No idea what this does
        minecraftClient.getItemRenderer().renderInGuiWithOverrides(potionRecipe.ingredient, this.x + k, this.y + k); // Why do we do this twice?
        minecraftClient.getItemRenderer().renderGuiItemOverlay(MinecraftClient.getInstance().textRenderer, potionRecipe.ingredient, this.x + k, this.y + k); // ^
        RenderSystem.enableDepthTest();
        matrixStack.pop();
        RenderSystem.applyModelViewMatrix();
        RenderSystem.disableDepthTest();
    }

    public BrewingResult getRecipe() {
        return potionRecipe;
    }

    public void setPos(int x, int y) {
        this.x = x;
        this.y = y;
    }

    public void appendNarrations(NarrationMessageBuilder builder) {
        Potion inputPotion = (Potion) ((BrewingRecipeRegistryRecipeAccessor<?>) this.potionRecipe.recipe).getInput();

        Identifier identifier = Registry.POTION.getId(inputPotion);
        ItemStack inputStack;
        if (group == BrewingRecipeBookGroup.BREWING_SPLASH_POTION) {
            inputStack = new ItemStack(Items.SPLASH_POTION);
        } else if (group == BrewingRecipeBookGroup.BREWING_LINGERING_POTION) {
            inputStack = new ItemStack(Items.LINGERING_POTION);
        } else {
            inputStack = new ItemStack(Items.POTION);
        }

        inputStack.getOrCreateNbt().putString("Potion", identifier.toString());

        builder.put(NarrationPart.TITLE, new TranslatableText("narration.recipe", inputStack.getName()));
        builder.put(NarrationPart.USAGE, new TranslatableText("narration.button.usage.hovered"));
    }

    public List<Text> getTooltip() {
        List<Text> list = Lists.newArrayList();

        list.add(potionRecipe.ingredient.getName());
        PotionUtil.buildTooltip(potionRecipe.ingredient, list, 1);
        list.add(new LiteralText(""));

        Formatting colour = Formatting.DARK_GRAY;
        if (getRecipe().hasIngredient(brewingStandScreenHandler)) {
            colour = Formatting.WHITE;
        }

        list.add(new LiteralText(((BrewingRecipeRegistryRecipeAccessor<?>) potionRecipe.recipe).getIngredient().getMatchingStacks()[0].getName().getString()).formatted(colour));

        list.add(new LiteralText("↓").formatted(Formatting.DARK_GRAY));

        Potion inputPotion = (Potion) ((BrewingRecipeRegistryRecipeAccessor<?>) this.potionRecipe.recipe).getInput();

        Identifier identifier = Registry.POTION.getId(inputPotion);
        ItemStack inputStack;
        if (group == BrewingRecipeBookGroup.BREWING_SPLASH_POTION) {
            inputStack = new ItemStack(Items.SPLASH_POTION);
        } else if (group == BrewingRecipeBookGroup.BREWING_LINGERING_POTION) {
            inputStack = new ItemStack(Items.LINGERING_POTION);
        } else {
            inputStack = new ItemStack(Items.POTION);
        }

        inputStack.getOrCreateNbt().putString("Potion", identifier.toString());

        if (!getRecipe().hasInput(group, brewingStandScreenHandler)) {
            colour = Formatting.DARK_GRAY;
        }

        list.add(new LiteralText(inputStack.getName().getString()).formatted(colour));

        if (BetterRecipeBook.pinnedRecipeManager.hasPotion(this.potionRecipe.recipe)) {
            list.add(new TranslatableText("betterrecipebook.gui.pin.remove"));
        } else {
            list.add(new TranslatableText("betterrecipebook.gui.pin.add"));
        }

        return list;
    }
}
