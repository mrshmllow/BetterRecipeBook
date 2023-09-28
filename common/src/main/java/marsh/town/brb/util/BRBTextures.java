package marsh.town.brb.util;

import net.minecraft.client.gui.components.WidgetSprites;
import net.minecraft.resources.ResourceLocation;

//TODO "brb:textures" are not rendering. Find out why.
public class BRBTextures {

    public static final ResourceLocation RECIPE_BOOK_BACKGROUND_TEXTURE = new ResourceLocation("textures/gui/recipe_book.png");

    public static final WidgetSprites SETTINGS_BUTTON_SPRITES = new WidgetSprites(
            new ResourceLocation("brb:textures/gui/sprites/button_settings.png"),
            new ResourceLocation("brb:textures/gui/sprites/button_settings_highlighted.png")
    );

    public static final WidgetSprites RECIPE_BOOK_FILTER_BUTTON_SPRITES = new WidgetSprites(
            new ResourceLocation("textures/gui/sprites/filter_disabled.png"),
            new ResourceLocation("textures/gui/sprites/filter_disabled_highlighted.png"),
            new ResourceLocation("textures/gui/sprites/filter_enabled.png"),
            new ResourceLocation("textures/gui/sprites/filter_enabled_highlighted.png")
    );

    public static final WidgetSprites RECIPE_BOOK_PAGE_FORWARD_SPRITES = new WidgetSprites(
            new ResourceLocation("textures/gui/sprites/recipe_book/page_forward.png"),
            new ResourceLocation("textures/gui/sprites/recipe_book/page_forward_highlighted.png")
    );

    public static final WidgetSprites RECIPE_BOOK_PAGE_BACKWARD_SPRITES = new WidgetSprites(
            new ResourceLocation("textures/gui/sprites/recipe_book/page_backward.png"),
            new ResourceLocation("textures/gui/sprites/recipe_book/page_backward_highlighted.png")
    );

    public static final WidgetSprites RECIPE_BOOK_BUTTON_SPRITES = new WidgetSprites(
            new ResourceLocation("textures/gui/sprites/recipe_book/button.png"),
            new ResourceLocation("textures/gui/sprites/recipe_book/button_highlighted.png")
    );

    public static final WidgetSprites RECIPE_BOOK_INSTANT_CRAFT_BUTTON_SPRITES = new WidgetSprites(
            new ResourceLocation("brb:textures/gui/sprites/button_instantcraft.png"),
            new ResourceLocation("brb:textures/gui/sprites/button_instantcraft_enabled.png"),
            new ResourceLocation("brb:textures/gui/sprites/button_instantcraft_highlighted.png"),
            new ResourceLocation("brb:textures/gui/sprites/button_instantcraft_enabled_highlighted.png")
    );

    public static final ResourceLocation RECIPE_BOOK_PIN_TEXTURE = new ResourceLocation("brb:textures/gui/sprites/pin.png");

    // TODO break up into sprites
    public static final ResourceLocation RECIPE_BUTTON_ALT_BLANK_TEXTURE = new ResourceLocation("brb:textures/gui/alt_button_blank.png");

}
