package marsh.town.brb.util;

import net.minecraft.client.gui.components.WidgetSprites;
import net.minecraft.resources.ResourceLocation;

public class BRBTextures {

    public static final ResourceLocation RECIPE_BOOK_BACKGROUND_TEXTURE = new ResourceLocation("textures/gui/recipe_book.png");

    public static final ResourceLocation RECIPE_BOOK_PIN_TEXTURE = new ResourceLocation("brb:textures/gui/sprites/recipe_book/pin.png");

    // TODO break up into sprites
    public static final ResourceLocation RECIPE_BUTTON_ALT_BLANK_TEXTURE = new ResourceLocation("brb:textures/gui/alt_button_blank.png");

    public static final WidgetSprites RECIPE_BOOK_FILTER_BUTTON_SPRITES = new WidgetSprites(
            new ResourceLocation("recipe_book/filter_disabled"),
            new ResourceLocation("recipe_book/filter_disabled_highlighted"),
            new ResourceLocation("recipe_book/filter_enabled"),
            new ResourceLocation("recipe_book/filter_enabled_highlighted")
    );

    public static final WidgetSprites RECIPE_BOOK_PAGE_FORWARD_SPRITES = new WidgetSprites(
            new ResourceLocation("recipe_book/page_forward"),
            new ResourceLocation("recipe_book/page_forward_highlighted")
    );

    public static final WidgetSprites RECIPE_BOOK_PAGE_BACKWARD_SPRITES = new WidgetSprites(
            new ResourceLocation("recipe_book/page_backward"),
            new ResourceLocation("recipe_book/page_backward_highlighted")
    );

    public static final WidgetSprites RECIPE_BOOK_BUTTON_SPRITES = new WidgetSprites(
            new ResourceLocation("recipe_book/button"),
            new ResourceLocation("recipe_book/button_highlighted")
    );

    public static final WidgetSprites SETTINGS_BUTTON_SPRITES = new WidgetSprites(
            new ResourceLocation("brb:recipe_book/button_settings"),
            new ResourceLocation("brb:recipe_book/button_settings_highlighted")
    );

    public static final WidgetSprites RECIPE_BOOK_INSTANT_CRAFT_BUTTON_SPRITES = new WidgetSprites(
            new ResourceLocation("brb:recipe_book/button_instantcraft"),
            new ResourceLocation("brb:recipe_book/button_instantcraft_enabled"),
            new ResourceLocation("brb:recipe_book/button_instantcraft_highlighted"),
            new ResourceLocation("brb:recipe_book/button_instantcraft_enabled_highlighted")
    );

}
