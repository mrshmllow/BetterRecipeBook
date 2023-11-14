package marsh.town.brb.util;

import net.minecraft.resources.ResourceLocation;

public class BRBTextures {

    public static final ResourceLocation RECIPE_BOOK_BACKGROUND_TEXTURE = new ResourceLocation("textures/gui/recipe_book.png");

    public static final ResourceLocation RECIPE_BOOK_BUTTON_SLOT_CRAFTABLE_SPRITE = new ResourceLocation("recipe_book/slot_craftable");
    public static final ResourceLocation RECIPE_BOOK_BUTTON_SLOT_UNCRAFTABLE_SPRITE = new ResourceLocation("recipe_book/slot_uncraftable");
    public static final ResourceLocation RECIPE_BOOK_PIN_SPRITE = new ResourceLocation("brb:recipe_book/pin");
    public static final ResourceLocation RECIPE_BOOK_OVERLAY_PIN_SPRITE = new ResourceLocation("brb:recipe_book/overlay_pin");

    public static final WidgetSprites RECIPE_BOOK_FILTER_BUTTON_SPRITES = new WidgetSprites(
            new ResourceLocation("recipe_book/filter_enabled"),
            new ResourceLocation("recipe_book/filter_disabled"),
            new ResourceLocation("recipe_book/filter_enabled_highlighted"),
            new ResourceLocation("recipe_book/filter_disabled_highlighted")
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

    public static final WidgetSprites RECIPE_BOOK_TAB_SPRITES = new WidgetSprites(
            new ResourceLocation("recipe_book/tab"),
            new ResourceLocation("recipe_book/tab_selected")
    );

    public static WidgetSprites RECIPE_BOOK_CRAFTING_OVERLAY_SPRITE = new WidgetSprites(
            new ResourceLocation("brb:recipe_book/crafting_overlay"),
            new ResourceLocation("brb:recipe_book/crafting_overlay_disabled"),
            new ResourceLocation("brb:recipe_book/crafting_overlay_highlighted"),
            new ResourceLocation("brb:recipe_book/crafting_overlay_disabled_highlighted")
    );

    public static WidgetSprites RECIPE_BOOK_PLAIN_OVERLAY_SPRITE = new WidgetSprites(
            new ResourceLocation("brb:recipe_book/plain_overlay"),
            new ResourceLocation("brb:recipe_book/plain_overlay_disabled"),
            new ResourceLocation("brb:recipe_book/plain_overlay_highlighted"),
            new ResourceLocation("brb:recipe_book/plain_overlay_disabled_highlighted")
    );

    public static final WidgetSprites SETTINGS_BUTTON_SPRITES = new WidgetSprites(
            new ResourceLocation("brb:recipe_book/button_settings"),
            new ResourceLocation("brb:recipe_book/button_settings_highlighted")
    );

    public static final WidgetSprites RECIPE_BOOK_INSTANT_CRAFT_BUTTON_SPRITES = new WidgetSprites(
            new ResourceLocation("brb:recipe_book/button_instantcraft"),
            new ResourceLocation("brb:recipe_book/button_instantcraft_disabled"),
            new ResourceLocation("brb:recipe_book/button_instantcraft_highlighted"),
            new ResourceLocation("brb:recipe_book/button_instantcraft_disabled_highlighted")
    );

}
