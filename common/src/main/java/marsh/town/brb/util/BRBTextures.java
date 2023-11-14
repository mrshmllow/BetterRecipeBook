package marsh.town.brb.util;

import net.minecraft.resources.ResourceLocation;

public class BRBTextures {

    public static final ResourceLocation RECIPE_BOOK_BACKGROUND_TEXTURE = new ResourceLocation("textures/gui/recipe_book.png");
    public static final ResourceLocation RECIPE_BUTTON_LOCATION = new ResourceLocation("textures/gui/recipe_button.png");

    public static final ResourceLocation RECIPE_BOOK_PIN_SPRITE = new ResourceLocation("brb:textures/gui/sprites/recipe_book/pin.png");
    public static final ResourceLocation RECIPE_BOOK_OVERLAY_PIN_SPRITE = new ResourceLocation("brb:textures/gui/sprites/recipe_book/overlay_pin.png");
    public static final ResourceLocation RECIPE_BOOK_SETTINGS_BUTTON = new ResourceLocation("brb:textures/gui/sprites/recipe_book/button_settings.png");
    public static final ResourceLocation RECIPE_BOOK_INSTANT_CRAFT_BUTTON_SPRITE = new ResourceLocation("brb:textures/gui/sprites/recipe_book/button_instantcraft.png");

    public static WidgetSprites RECIPE_BOOK_CRAFTING_OVERLAY_SPRITE = new WidgetSprites(
            new ResourceLocation("brb:textures/gui/sprites/recipe_book/crafting_overlay.png"),
            new ResourceLocation("brb:textures/gui/sprites/recipe_book/crafting_overlay_disabled.png"),
            new ResourceLocation("brb:textures/gui/sprites/recipe_book/crafting_overlay_highlighted.png"),
            new ResourceLocation("brb:textures/gui/sprites/recipe_book/crafting_overlay_disabled_highlighted.png")
    );

    public static WidgetSprites RECIPE_BOOK_PLAIN_OVERLAY_SPRITE = new WidgetSprites(
            new ResourceLocation("brb:textures/gui/sprites/recipe_book/plain_overlay.png"),
            new ResourceLocation("brb:textures/gui/sprites/recipe_book/plain_overlay_disabled.png"),
            new ResourceLocation("brb:textures/gui/sprites/recipe_book/plain_overlay_highlighted.png"),
            new ResourceLocation("brb:textures/gui/sprites/recipe_book/plain_overlay_disabled_highlighted.png")
    );

}
