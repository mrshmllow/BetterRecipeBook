package marsh.town.brb.smithingtable;

import com.google.common.collect.Lists;
import com.mojang.blaze3d.systems.RenderSystem;
import marsh.town.brb.BetterRecipeBook;
import marsh.town.brb.api.BRBBookSettings;
import marsh.town.brb.recipe.BRBSmithingRecipe;
import marsh.town.brb.util.BRBTextures;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.components.Renderable;
import net.minecraft.client.gui.components.events.GuiEventListener;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.core.RegistryAccess;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import org.jetbrains.annotations.Nullable;

import java.util.Collections;
import java.util.List;

public class SmithingOverlayRecipeComponent implements Renderable, GuiEventListener {
    private final List<OverlayRecipeButton> recipeButtons = Lists.newArrayList();
    private BRBSmithingRecipe lastRecipeClicked;
    private SmithingRecipeCollection collection;
    private boolean isVisible;
    private static final ResourceLocation OVERLAY_RECIPE_SPRITE = ResourceLocation.withDefaultNamespace("recipe_book/overlay_recipe");
    float time;
    private int y;
    private int x;

    public void init(SmithingRecipeCollection recipeCollection, int x, int y, RegistryAccess registryAccess) {
        this.collection = recipeCollection;

        List<BRBSmithingRecipe> lockedRecipes = recipeCollection.getDisplayRecipes(true);
        List<BRBSmithingRecipe> unlockedRecipes = BRBBookSettings.isFiltering(BetterRecipeBook.SMITHING) ? Collections.emptyList() : recipeCollection.getDisplayRecipes(false);
        int lockedRecipeCount = lockedRecipes.size();
        int totalRecipeCount = lockedRecipeCount + unlockedRecipes.size();
        int columns = totalRecipeCount <= 16 ? 4 : 5;
        // screw it hardcode these values not like they are adding more armour anytime soon
        // trimming tools, anyone?
        this.x = x + 7;
        this.y = y + 26;

        this.isVisible = true;
        this.recipeButtons.clear();

        for (int index = 0; index < totalRecipeCount; ++index) {
            boolean isCraftable = index < lockedRecipeCount;
            BRBSmithingRecipe recipeHolder = isCraftable ? lockedRecipes.get(index) : unlockedRecipes.get(index - lockedRecipeCount);
            int buttonX = this.x + 4 + 25 * (index % columns);
            int buttonY = this.y + 5 + 25 * (index / columns);
            this.recipeButtons.add(new OverlayRecipeButton(buttonX, buttonY, recipeHolder, isCraftable, registryAccess));
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
    public BRBSmithingRecipe getLastRecipeClicked() {
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
        final BRBSmithingRecipe recipe;
        private final boolean isCraftable;
        private RegistryAccess registryAccess;

        public OverlayRecipeButton(int i, int j, BRBSmithingRecipe smithableResult, boolean isCraftable, RegistryAccess registryAccess) {
            super(i, j, 200, 20, CommonComponents.EMPTY);
            this.width = 24;
            this.height = 24;
            this.recipe = smithableResult;
            this.isCraftable = isCraftable;
            this.registryAccess = registryAccess;
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
            // Currently, the trim does not use the category passed at all. Only brewing uses the category. soooo its
            // fineeeee to not use the current category. I hate OOP.
            guiGraphics.renderFakeItem(recipe.getResult(registryAccess, BetterRecipeBook.SMITHING_SEARCH), getX() + offset, getY() + offset);

            guiGraphics.pose().popPose();
        }
    }
}
