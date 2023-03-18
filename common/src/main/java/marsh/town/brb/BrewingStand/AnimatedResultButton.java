package marsh.town.brb.BrewingStand;

import com.google.common.collect.Lists;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import marsh.town.brb.BetterRecipeBook;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.narration.NarratedElementType;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.contents.LiteralContents;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.inventory.BrewingStandMenu;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.alchemy.PotionUtils;

import java.awt.*;
import java.util.List;

import static marsh.town.brb.BrewingStand.PlatformPotionUtil.getIngredient;

@Environment(EnvType.CLIENT)
public class AnimatedResultButton extends AbstractWidget {
    private float time;
    private static final ResourceLocation BACKGROUND_TEXTURE = new ResourceLocation("textures/gui/recipe_book.png");
    private Result potionRecipe;
    private RecipeBookGroup group;
    private BrewingStandMenu brewingStandScreenHandler;

    public AnimatedResultButton() {
        super(0, 0, 25, 25, CommonComponents.EMPTY);
    }

    public void showPotionRecipe(Result potionRecipe, RecipeBookGroup group, BrewingStandMenu brewingStandScreenHandler) {
        this.potionRecipe = potionRecipe;
        this.group = group;
        this.brewingStandScreenHandler = brewingStandScreenHandler;
    }

    public void renderWidget(PoseStack matrices, int mouseX, int mouseY, float delta) {
        if (!Screen.hasControlDown()) {
            this.time += delta;
        }

        Minecraft minecraftClient = Minecraft.getInstance();
        RenderSystem.setShader(GameRenderer::getPositionTexShader);

        int i;
        int j;

        if (BetterRecipeBook.config.enablePinning && BetterRecipeBook.pinnedRecipeManager.hasPotion(potionRecipe.recipe)) {
            RenderSystem.setShaderTexture(0, new ResourceLocation("brb:textures/gui/pinned.png"));
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

        PoseStack matrixStack = RenderSystem.getModelViewStack();
        this.blit(matrices, getX(), getY(), i, j, this.width, this.height);
        int k = 4;

        matrixStack.pushPose();
        matrixStack.mulPoseMatrix(matrices.last().pose()); // No idea what this does
        minecraftClient.getItemRenderer().renderAndDecorateItem(matrices, potionRecipe.ingredient, getX() + k, getY() + k); // Why do we do this twice?
        minecraftClient.getItemRenderer().renderGuiItemDecorations(matrices, Minecraft.getInstance().font, potionRecipe.ingredient, getX() + k, getY() + k); // ^
        RenderSystem.enableDepthTest();
        matrixStack.popPose();
        RenderSystem.applyModelViewMatrix();
        RenderSystem.disableDepthTest();
    }

    public Result getRecipe() {
        return potionRecipe;
    }

    public void setPos(int x, int y) {
        setX(x);
        setY(y);
    }

    public void updateWidgetNarration(NarrationElementOutput builder) {
        ItemStack inputStack = this.potionRecipe.inputAsItemStack(group);

        builder.add(NarratedElementType.TITLE, Component.translatable("narration.recipe", inputStack.getHoverName()));
        builder.add(NarratedElementType.USAGE, Component.translatable("narration.button.usage.hovered"));
    }

    public List<Component> getTooltip() {
        List<Component> list = Lists.newArrayList();

        list.add(potionRecipe.ingredient.getHoverName());
        PotionUtils.addPotionTooltip(potionRecipe.ingredient, list, 1);
        list.add(Component.literal(""));

        ChatFormatting colour = ChatFormatting.DARK_GRAY;
        if (potionRecipe.hasIngredient(brewingStandScreenHandler)) {
            colour = ChatFormatting.WHITE;
        }

        list.add(Component.literal(getIngredient(potionRecipe.recipe).getItems()[0].getHoverName().getString()).withStyle(colour));

        list.add(Component.literal("↓").withStyle(ChatFormatting.DARK_GRAY));

        ItemStack inputStack = this.potionRecipe.inputAsItemStack(group);

        if (!potionRecipe.hasInput(group, brewingStandScreenHandler)) {
            colour = ChatFormatting.DARK_GRAY;
        }

        list.add(Component.literal(inputStack.getHoverName().getString()).withStyle(colour));

        if (BetterRecipeBook.config.enablePinning) {
            if (BetterRecipeBook.pinnedRecipeManager.hasPotion(this.potionRecipe.recipe)) {
                list.add(Component.translatable("brb.gui.pin.remove"));
            } else {
                list.add(Component.translatable("brb.gui.pin.add"));
            }
        }

        return list;
    }
}
