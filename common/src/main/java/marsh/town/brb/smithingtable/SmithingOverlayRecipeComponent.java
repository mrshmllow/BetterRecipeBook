package marsh.town.brb.smithingtable;

import com.google.common.collect.Lists;
import com.mojang.blaze3d.systems.RenderSystem;
import marsh.town.brb.BetterRecipeBook;
import marsh.town.brb.util.BRBTextures;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.components.Renderable;
import net.minecraft.client.gui.components.events.GuiEventListener;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import org.jetbrains.annotations.Nullable;

import java.util.Collections;
import java.util.List;

public class SmithingOverlayRecipeComponent implements Renderable, GuiEventListener {
    private final List<OverlayRecipeButton> recipeButtons = Lists.newArrayList();
    private SmithableResult lastRecipeClicked;
    private SmithingRecipeCollection collection;
    private boolean isVisible;
    private static final ResourceLocation OVERLAY_RECIPE_SPRITE = new ResourceLocation("recipe_book/overlay_recipe");
    float time;
    private int y;
    private int x;

    public void init(SmithingRecipeCollection recipeCollection, int x, int y, int screenX, int screenY, float recipeButtonWidth) {
        this.collection = recipeCollection;

        boolean isFiltering = BetterRecipeBook.rememberedSmithableToggle;
        List<SmithableResult> lockedRecipes = recipeCollection.getDisplayRecipes(true);
        List<SmithableResult> unlockedRecipes = isFiltering ? Collections.emptyList() : recipeCollection.getDisplayRecipes(false);
        int lockedRecipeCount = lockedRecipes.size();
        int totalRecipeCount = lockedRecipeCount + unlockedRecipes.size();
        int columns = totalRecipeCount <= 16 ? 4 : 5;
        int rows = (int) Math.ceil((float) totalRecipeCount / (float) columns);
        this.x = x;
        this.y = y;
        float rightEdgeX = (float) (this.x + Math.min(totalRecipeCount, columns) * 25);
        float visibleRightEdgeX = (float) (screenX + 50);
        if (rightEdgeX > visibleRightEdgeX) {
            this.x = (int) ((float) this.x - recipeButtonWidth * (float) ((int) ((rightEdgeX - visibleRightEdgeX) / recipeButtonWidth)));
        }

        float bottomEdgeY = (float) (this.y + rows * 25);
        float visibleBottomEdgeY = (float) (screenY + 50);
        if (bottomEdgeY > visibleBottomEdgeY) {
            this.y = (int) ((float) this.y - recipeButtonWidth * (float) Mth.ceil((bottomEdgeY - visibleBottomEdgeY) / recipeButtonWidth));
        }

        float currentTopY = (float) this.y;
        float visibleTopY = (float) (screenY - 100);
        if (currentTopY < visibleTopY) {
            this.y = (int) ((float) this.y - recipeButtonWidth * (float) Mth.ceil((currentTopY - visibleTopY) / recipeButtonWidth));
        }

        this.isVisible = true;
        this.recipeButtons.clear();

        for (int index = 0; index < totalRecipeCount; ++index) {
            boolean isCraftable = index < lockedRecipeCount;
            SmithableResult recipeHolder = isCraftable ? lockedRecipes.get(index) : unlockedRecipes.get(index - lockedRecipeCount);
            int buttonX = this.x + 4 + 25 * (index % columns);
            int buttonY = this.y + 5 + 25 * (index / columns);
            this.recipeButtons.add(new OverlayRecipeButton(buttonX, buttonY, recipeHolder, isCraftable));
        }

        this.lastRecipeClicked = null;
    }

    public boolean mouseClicked(double d, double e, int i) {
        if (i != 0) {
            return false;
        }
        for (OverlayRecipeButton overlayRecipeButton : this.recipeButtons) {
            if (!overlayRecipeButton.mouseClicked(d, e, i)) continue;
            this.lastRecipeClicked = overlayRecipeButton.recipe;
            return true;
        }
        return false;
    }

    public boolean isMouseOver(double d, double e) {
        return false;
    }

    @Nullable
    public SmithableResult getLastRecipeClicked() {
        return this.lastRecipeClicked;
    }

    public SmithingRecipeCollection getRecipeCollection() {
        return this.collection;
    }

    public boolean isVisible() {
        return this.isVisible;
    }

    public void setVisible(boolean b) {
        this.isVisible = b;
    }

    public void setFocused(boolean bl) {
    }

    public boolean isFocused() {
        return false;
    }

    @Override
    public void render(GuiGraphics guiGraphics, int i, int j, float f) {
        if (this.isVisible) {
            this.time += f;
            RenderSystem.enableBlend();
            guiGraphics.pose().pushPose();
            guiGraphics.pose().translate(0.0F, 0.0F, 1000.0F);
            int k = this.recipeButtons.size() <= 16 ? 4 : 5;
            int l = Math.min(this.recipeButtons.size(), k);
            int m = Mth.ceil((float) this.recipeButtons.size() / (float) k);
            guiGraphics.blitSprite(OVERLAY_RECIPE_SPRITE, this.x, this.y, l * 25 + 8, m * 25 + 8);
            RenderSystem.disableBlend();

            for (OverlayRecipeButton overlayRecipeButton : this.recipeButtons) {
                overlayRecipeButton.render(guiGraphics, i, j, f);
            }

            guiGraphics.pose().popPose();
        }
    }

    public static class OverlayRecipeButton extends AbstractWidget {
        final SmithableResult recipe;
        private final boolean isCraftable;

        public OverlayRecipeButton(int i, int j, SmithableResult smithableResult, boolean isCraftable) {
            super(i, j, 200, 20, CommonComponents.EMPTY);
            this.width = 24;
            this.height = 24;
            this.recipe = smithableResult;
            this.isCraftable = isCraftable;
        }

        public void updateWidgetNarration(NarrationElementOutput narrationElementOutput) {
            this.defaultButtonNarrationText(narrationElementOutput);
        }

        public void renderWidget(GuiGraphics guiGraphics, int i, int j, float f) {
            ResourceLocation resourceLocation;

            resourceLocation = BRBTextures.RECIPE_BOOK_PLAIN_OVERLAY_SPRITE.get(this.isCraftable, isHoveredOrFocused());

            guiGraphics.blitSprite(resourceLocation, this.getX(), this.getY(), this.width, this.height);
            guiGraphics.pose().pushPose();
//            guiGraphics.pose().translate(this.getX() + 2, this.getY() + 2, 150.0);

            int offset = 4;
            guiGraphics.renderFakeItem(recipe.result, getX() + offset, getY() + offset);

            guiGraphics.pose().popPose();
        }
    }
}
