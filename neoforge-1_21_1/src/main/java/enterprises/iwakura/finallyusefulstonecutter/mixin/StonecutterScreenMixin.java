package enterprises.iwakura.finallyusefulstonecutter.mixin;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import enterprises.iwakura.finallyusefulstonecutter.StonecutterScreenDuck;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.gui.screens.inventory.StonecutterScreen;
import net.minecraft.client.multiplayer.MultiPlayerGameMode;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.StonecutterMenu;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.item.crafting.StonecutterRecipe;

@Mixin(StonecutterScreen.class)
public abstract class StonecutterScreenMixin implements StonecutterScreenDuck {

    @Shadow
    private int startIndex;

    @Shadow
    private float scrollOffs;

    @Unique
    private static final Map<Item, ResourceLocation> finallyusefulstonecutter$lastRecipeByInput = new HashMap<>();

    @Unique
    private boolean finallyusefulstonecutter$pendingAutoSelect;

    @Unique
    private Item finallyusefulstonecutter$pendingAutoSelectItem;

    @Unique
    private ResourceLocation finallyusefulstonecutter$pendingAutoSelectRecipe;

    @Unique
    private List<Integer> finallyusefulstonecutter$visibleIndices(StonecutterMenu menu) {
        String filter = this.finallyusefulstonecutter$getFilter().toLowerCase();
        List<RecipeHolder<StonecutterRecipe>> all = menu.getRecipes();
        List<Integer> visible = new ArrayList<>();
        for (int i = 0; i < all.size(); i++) {
            if (filter.isEmpty() || finallyusefulstonecutter$matches(all.get(i).value(), filter)) {
                visible.add(i);
            }
        }
        return visible;
    }

    @Unique
    private boolean finallyusefulstonecutter$matches(StonecutterRecipe recipe, String filter) {
        ItemStack resultItem = recipe.getResultItem(Minecraft.getInstance().level.registryAccess());
        String name = resultItem.getItem().getName(resultItem).getString().toLowerCase();
        return name.contains(filter);
    }

    @Redirect(
        method = {"renderButtons", "renderRecipes", "renderTooltip", "getOffscreenRows", "isScrollBarActive"},
        at = @At(value = "INVOKE", target = "Lnet/minecraft/world/inventory/StonecutterMenu;getNumRecipes()I")
    )
    private int finallyusefulstonecutter$redirectNumRecipes(StonecutterMenu menu) {
        return finallyusefulstonecutter$visibleIndices(menu).size();
    }

    @Redirect(
        method = {"renderRecipes", "renderTooltip"},
        at = @At(value = "INVOKE", target = "Lnet/minecraft/world/inventory/StonecutterMenu;getRecipes()Ljava/util/List;")
    )
    private List<RecipeHolder<StonecutterRecipe>> finallyusefulstonecutter$redirectGetRecipes(StonecutterMenu menu) {
        List<RecipeHolder<StonecutterRecipe>> all = menu.getRecipes();
        List<Integer> visible = finallyusefulstonecutter$visibleIndices(menu);
        List<RecipeHolder<StonecutterRecipe>> out = new ArrayList<>(visible.size());

        for (int index : visible) {
            out.add(all.get(index));
        }

        return out;
    }

    @Redirect(
        method = "renderButtons",
        at = @At(value = "INVOKE", target = "Lnet/minecraft/world/inventory/StonecutterMenu;getSelectedRecipeIndex()I")
    )
    private int finallyusefulstonecutter$redirectSelectedIndex(StonecutterMenu menu) {
        int realSelected = menu.getSelectedRecipeIndex();
        return finallyusefulstonecutter$visibleIndices(menu).indexOf(realSelected);
    }

    @Inject(method = "mouseClicked", at = @At("HEAD"))
    private void finallyusefulstonecutter$unfocusSearchBoxIfClickedElsewhere(double mouseX, double mouseY, int button, CallbackInfoReturnable<Boolean> cir) {
        EditBox searchBox = this.finallyusefulstonecutter$getSearchBox();
        if (searchBox != null && searchBox.isFocused() && !searchBox.isMouseOver(mouseX, mouseY)) {
            searchBox.setFocused(false);
        }
    }

    @Redirect(
        method = "mouseClicked",
        at = @At(value = "INVOKE", target = "Lnet/minecraft/world/inventory/StonecutterMenu;clickMenuButton(Lnet/minecraft/world/entity/player/Player;I)Z")
    )
    private boolean finallyusefulstonecutter$redirectClick(StonecutterMenu menu, Player player, int displayIndex) {
        List<Integer> visible = finallyusefulstonecutter$visibleIndices(menu);
        if (displayIndex < 0 || displayIndex >= visible.size()) {
            return false;
        }
        int realIndex = visible.get(displayIndex);
        boolean success = menu.clickMenuButton(player, realIndex);
        if (success) {
            finallyusefulstonecutter$rememberSelection(menu, realIndex);
        }
        return success;
    }

    @Unique
    private void finallyusefulstonecutter$rememberSelection(StonecutterMenu menu, int realIndex) {
        ItemStack input = menu.getSlot(StonecutterMenu.INPUT_SLOT).getItem();
        if (input.isEmpty()) {
            return;
        }
        RecipeHolder<StonecutterRecipe> recipe = menu.getRecipes().get(realIndex);
        finallyusefulstonecutter$lastRecipeByInput.put(input.getItem(), recipe.id());
    }

    @Inject(method = "containerChanged", at = @At("TAIL"))
    private void finallyusefulstonecutter$onContainerChanged(CallbackInfo ci) {
        StonecutterMenu menu = (StonecutterMenu) ((AbstractContainerScreenAccessor) this).finallyusefulstonecutter$getMenu();
        ItemStack input = menu.getSlot(StonecutterMenu.INPUT_SLOT).getItem();
        if (input.isEmpty() || menu.getSelectedRecipeIndex() >= 0) {
            finallyusefulstonecutter$pendingAutoSelect = false;
            return;
        }
        Item inputItem = input.getItem();
        ResourceLocation rememberedId = finallyusefulstonecutter$lastRecipeByInput.get(inputItem);
        if (rememberedId == null) {
            finallyusefulstonecutter$pendingAutoSelect = false;
            return;
        }

        // next tick auto select, don't race the server
        finallyusefulstonecutter$pendingAutoSelect = true;
        finallyusefulstonecutter$pendingAutoSelectItem = inputItem;
        finallyusefulstonecutter$pendingAutoSelectRecipe = rememberedId;
    }

    @Override
    public void finallyusefulstonecutter$resetScroll() {
        this.startIndex = 0;
        this.scrollOffs = 0.0F;
    }

    @Override
    public void finallyusefulstonecutter$tickPendingAutoSelect() {
        if (!finallyusefulstonecutter$pendingAutoSelect) {
            return;
        }

        finallyusefulstonecutter$pendingAutoSelect = false;

        StonecutterMenu menu = (StonecutterMenu) ((AbstractContainerScreenAccessor) this).finallyusefulstonecutter$getMenu();
        ItemStack currentInput = menu.getSlot(StonecutterMenu.INPUT_SLOT).getItem();
        if (currentInput.isEmpty() || !currentInput.is(finallyusefulstonecutter$pendingAutoSelectItem) || menu.getSelectedRecipeIndex() >= 0) {
            return;
        }
        List<RecipeHolder<StonecutterRecipe>> recipes = menu.getRecipes();
        for (int i = 0; i < recipes.size(); i++) {
            if (recipes.get(i).id().equals(finallyusefulstonecutter$pendingAutoSelectRecipe)) {
                menu.clickMenuButton(Minecraft.getInstance().player, i);
                Minecraft.getInstance().gameMode.handleInventoryButtonClick(menu.containerId, i);
                break;
            }
        }
    }

    @Redirect(
        method = "mouseClicked",
        at = @At(value = "INVOKE", target = "Lnet/minecraft/client/multiplayer/MultiPlayerGameMode;handleInventoryButtonClick(II)V")
    )
    private void finallyusefulstonecutter$redirectHandleButtonClick(MultiPlayerGameMode gameMode, int containerId, int displayIndex) {
        StonecutterMenu menu = (StonecutterMenu) ((AbstractContainerScreenAccessor) this).finallyusefulstonecutter$getMenu();
        List<Integer> visible = finallyusefulstonecutter$visibleIndices(menu);
        int realIndex = (displayIndex >= 0 && displayIndex < visible.size()) ? visible.get(displayIndex) : displayIndex;
        gameMode.handleInventoryButtonClick(containerId, realIndex);
    }
}