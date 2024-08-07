package marsh.town.brb.util;

import marsh.town.brb.BetterRecipeBook;
import net.minecraft.client.gui.components.WidgetSprites;
import net.minecraft.resources.ResourceLocation;

public class BRBTextures {

    private static final String NS = BetterRecipeBook.MOD_ID;

    public static final ResourceLocation RECIPE_BOOK_BACKGROUND_TEXTURE = ResourceLocation.withDefaultNamespace("textures/gui/recipe_book.png");

    public static final ResourceLocation RECIPE_BOOK_BUTTON_SLOT_CRAFTABLE_SPRITE = ResourceLocation.withDefaultNamespace("recipe_book/slot_craftable");
    public static final ResourceLocation RECIPE_BOOK_BUTTON_SLOT_UNCRAFTABLE_SPRITE = ResourceLocation.withDefaultNamespace("recipe_book/slot_uncraftable");
    public static final ResourceLocation RECIPE_BOOK_PIN_SPRITE = ResourceLocation.fromNamespaceAndPath(NS, "recipe_book/pin");
    public static final ResourceLocation RECIPE_BOOK_OVERLAY_PIN_SPRITE = ResourceLocation.fromNamespaceAndPath(NS, "recipe_book/overlay_pin");
    
    public static final WidgetSprites RECIPE_BOOK_FILTER_BUTTON_SPRITES = new WidgetSprites(
            ResourceLocation.withDefaultNamespace("recipe_book/filter_enabled"),
            ResourceLocation.withDefaultNamespace("recipe_book/filter_disabled"),
            ResourceLocation.withDefaultNamespace("recipe_book/filter_enabled_highlighted"),
            ResourceLocation.withDefaultNamespace("recipe_book/filter_disabled_highlighted")
    );

    public static final WidgetSprites RECIPE_BOOK_PAGE_FORWARD_SPRITES = new WidgetSprites(
            ResourceLocation.withDefaultNamespace("recipe_book/page_forward"),
            ResourceLocation.withDefaultNamespace("recipe_book/page_forward_highlighted")
    );

    public static final WidgetSprites RECIPE_BOOK_PAGE_BACKWARD_SPRITES = new WidgetSprites(
            ResourceLocation.withDefaultNamespace("recipe_book/page_backward"),
            ResourceLocation.withDefaultNamespace("recipe_book/page_backward_highlighted")
    );

    public static final WidgetSprites RECIPE_BOOK_BUTTON_SPRITES = new WidgetSprites(
            ResourceLocation.withDefaultNamespace("recipe_book/button"),
            ResourceLocation.withDefaultNamespace("recipe_book/button_highlighted")
    );

    public static final WidgetSprites RECIPE_BOOK_TAB_SPRITES = new WidgetSprites(
            ResourceLocation.withDefaultNamespace("recipe_book/tab"),
            ResourceLocation.withDefaultNamespace("recipe_book/tab_selected")
    );

    public static WidgetSprites RECIPE_BOOK_CRAFTING_OVERLAY_SPRITE = new WidgetSprites(
            ResourceLocation.fromNamespaceAndPath(NS, "recipe_book/crafting_overlay"),
            ResourceLocation.fromNamespaceAndPath(NS, "recipe_book/crafting_overlay_disabled"),
            ResourceLocation.fromNamespaceAndPath(NS, "recipe_book/crafting_overlay_highlighted"),
            ResourceLocation.fromNamespaceAndPath(NS, "recipe_book/crafting_overlay_disabled_highlighted")
    );

    public static WidgetSprites RECIPE_BOOK_PLAIN_OVERLAY_SPRITE = new WidgetSprites(
            ResourceLocation.fromNamespaceAndPath(NS, "recipe_book/plain_overlay"),
            ResourceLocation.fromNamespaceAndPath(NS, "recipe_book/plain_overlay_disabled"),
            ResourceLocation.fromNamespaceAndPath(NS, "recipe_book/plain_overlay_highlighted"),
            ResourceLocation.fromNamespaceAndPath(NS, "recipe_book/plain_overlay_disabled_highlighted")
    );

    public static final WidgetSprites SETTINGS_BUTTON_SPRITES = new WidgetSprites(
            ResourceLocation.fromNamespaceAndPath(NS, "recipe_book/button_settings"),
            ResourceLocation.fromNamespaceAndPath(NS, "recipe_book/button_settings_highlighted")
    );

    public static final WidgetSprites RECIPE_BOOK_INSTANT_CRAFT_BUTTON_SPRITES = new WidgetSprites(
            ResourceLocation.fromNamespaceAndPath(NS, "recipe_book/button_instantcraft"),
            ResourceLocation.fromNamespaceAndPath(NS, "recipe_book/button_instantcraft_disabled"),
            ResourceLocation.fromNamespaceAndPath(NS, "recipe_book/button_instantcraft_highlighted"),
            ResourceLocation.fromNamespaceAndPath(NS, "recipe_book/button_instantcraft_disabled_highlighted")
    );

}
