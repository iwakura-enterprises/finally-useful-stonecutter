package enterprises.iwakura.finallyusefulstonecutter.mixin;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.objectweb.asm.Opcodes;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Constant;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyConstant;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import enterprises.iwakura.finallyusefulstonecutter.StonecutterScreenDuck;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.inventory.StonecutterScreen;
import net.minecraft.client.multiplayer.MultiPlayerGameMode;
import net.minecraft.client.resources.sounds.SimpleSoundInstance;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.StonecutterMenu;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.item.crafting.StonecutterRecipe;

@Mixin(StonecutterScreen.class)
public abstract class StonecutterScreenMixin implements StonecutterScreenDuck {

    @Unique
    private static final ResourceLocation finallyusefulstonecutter$BG_LOCATION = ResourceLocation.fromNamespaceAndPath("finally_useful_stonecutter", "textures/gui/container/stonecutter.png");

    @Unique
    private static final int finallyusefulstonecutter$IMAGE_WIDTH = 207;
    @Unique
    private static final int finallyusefulstonecutter$IMAGE_HEIGHT = 205;

    // 8 * 5 cells, 16x18, 128x92 recipe frame, 30, 21
    @Unique
    private static final int finallyusefulstonecutter$GRID_X = 30;
    @Unique
    private static final int finallyusefulstonecutter$GRID_Y = 21;
    @Unique
    private static final int finallyusefulstonecutter$GRID_COLUMNS = 8;
    @Unique
    private static final int finallyusefulstonecutter$GRID_ROWS = 5;
    @Unique
    private static final int finallyusefulstonecutter$CELL_WIDTH = 16;
    @Unique
    private static final int finallyusefulstonecutter$CELL_HEIGHT = 18;
    @Unique
    private static final int finallyusefulstonecutter$VISIBLE_SLOTS = finallyusefulstonecutter$GRID_COLUMNS * finallyusefulstonecutter$GRID_ROWS;

    @Unique
    private static final int finallyusefulstonecutter$SCROLLBAR_X = 161;
    @Unique
    private static final int finallyusefulstonecutter$SCROLLBAR_Y = 21;
    @Unique
    private static final int finallyusefulstonecutter$SCROLLBAR_WIDTH = 12;
    @Unique
    private static final int finallyusefulstonecutter$SCROLLBAR_HEIGHT = 92;
    @Unique
    private static final int finallyusefulstonecutter$SCROLLBAR_THUMB_HEIGHT = 15;

    @Unique
    private static final int finallyusefulstonecutter$BUTTON_U = 207;
    @Unique
    private static final int finallyusefulstonecutter$BUTTON_IDLE_V = 0;
    @Unique
    private static final int finallyusefulstonecutter$BUTTON_SELECTED_V = 18;
    @Unique
    private static final int finallyusefulstonecutter$BUTTON_HOVER_U = 223;
    @Unique
    private static final int finallyusefulstonecutter$BUTTON_HOVER_V = 0;
    @Unique
    private static final int finallyusefulstonecutter$SCROLLBAR_THUMB_U = 207;
    @Unique
    private static final int finallyusefulstonecutter$SCROLLBAR_THUMB_V = 36;
    @Unique
    private static final int finallyusefulstonecutter$SCROLLBAR_THUMB_DISABLED_U = 219;
    @Unique
    private static final int finallyusefulstonecutter$SCROLLBAR_THUMB_DISABLED_V = 36;

    @Unique
    private static final int finallyusefulstonecutter$INPUT_SLOT_X = 8;
    @Unique
    private static final int finallyusefulstonecutter$INPUT_SLOT_Y = 52;
    @Unique
    private static final int finallyusefulstonecutter$RESULT_SLOT_X = 181;
    @Unique
    private static final int finallyusefulstonecutter$RESULT_SLOT_Y = 52;
    @Unique
    private static final int finallyusefulstonecutter$INVENTORY_X = 23;
    @Unique
    private static final int finallyusefulstonecutter$INVENTORY_Y = 123;
    @Unique
    private static final int finallyusefulstonecutter$HOTBAR_Y = 181;
    @Unique
    private static final int finallyusefulstonecutter$SLOT_SIZE = 18;
    @Unique
    private static final int finallyusefulstonecutter$INVENTORY_LIST_START = 2;
    @Unique
    private static final int finallyusefulstonecutter$INVENTORY_ROWS = 3;
    @Unique
    private static final int finallyusefulstonecutter$INVENTORY_COLUMNS = 9;
    @Unique
    private static final int HOTBAR_LIST_START = finallyusefulstonecutter$INVENTORY_LIST_START + finallyusefulstonecutter$INVENTORY_ROWS * finallyusefulstonecutter$INVENTORY_COLUMNS;
    @Unique
    private static final Map<Item, ResourceLocation> finallyusefulstonecutter$lastRecipeByInput = new HashMap<>();

    @Shadow
    private int startIndex;
    @Shadow
    private float scrollOffs;
    @Shadow
    private boolean scrolling;
    @Shadow
    private boolean displayRecipes;

    @Unique
    private boolean finallyusefulstonecutter$pendingAutoSelect;
    @Unique
    private Item finallyusefulstonecutter$pendingAutoSelectItem;
    @Unique
    private ResourceLocation finallyusefulstonecutter$pendingAutoSelectRecipe;

    @Shadow
    protected int getOffscreenRows() {
        throw new IllegalStateException("nu uh");
    }

    @Shadow
    private boolean isScrollBarActive() {
        throw new IllegalStateException("nu uh");
    }

    @Inject(method = "<init>", at = @At("TAIL"))
    private void finallyusefulstonecutter$setImageSize(
        StonecutterMenu menu,
        Inventory inventory,
        Component title,
        CallbackInfo ci
    ) {
        AbstractContainerScreenAccessor accessor = (AbstractContainerScreenAccessor) this;
        accessor.finallyusefulstonecutter$setImageWidth(finallyusefulstonecutter$IMAGE_WIDTH);
        accessor.finallyusefulstonecutter$setImageHeight(finallyusefulstonecutter$IMAGE_HEIGHT);
        accessor.finallyusefulstonecutter$setTitleLabelY(accessor.finallyusefulstonecutter$getTitleLabelY() + 3);

        finallyusefulstonecutter$repositionSlot(menu, StonecutterMenu.INPUT_SLOT, finallyusefulstonecutter$INPUT_SLOT_X, finallyusefulstonecutter$INPUT_SLOT_Y);
        finallyusefulstonecutter$repositionSlot(menu, StonecutterMenu.RESULT_SLOT, finallyusefulstonecutter$RESULT_SLOT_X, finallyusefulstonecutter$RESULT_SLOT_Y);

        // gotta move the inventory slots lower to the right
        for (int row = 0; row < finallyusefulstonecutter$INVENTORY_ROWS; row++) {
            for (int col = 0; col < finallyusefulstonecutter$INVENTORY_COLUMNS; col++) {
                int listIndex = finallyusefulstonecutter$INVENTORY_LIST_START + row * finallyusefulstonecutter$INVENTORY_COLUMNS
                    + col;
                finallyusefulstonecutter$repositionSlot(menu, listIndex,
                    finallyusefulstonecutter$INVENTORY_X + col * finallyusefulstonecutter$SLOT_SIZE, finallyusefulstonecutter$INVENTORY_Y + row * finallyusefulstonecutter$SLOT_SIZE);
            }
        }
        for (int col = 0; col < finallyusefulstonecutter$INVENTORY_COLUMNS; col++) {
            int listIndex = HOTBAR_LIST_START + col;
            finallyusefulstonecutter$repositionSlot(menu, listIndex, finallyusefulstonecutter$INVENTORY_X + col * finallyusefulstonecutter$SLOT_SIZE, finallyusefulstonecutter$HOTBAR_Y);
        }
    }

    @Unique
    private void finallyusefulstonecutter$repositionSlot(StonecutterMenu menu, int listIndex, int x, int y) {
        SlotAccessor slot = (SlotAccessor) menu.getSlot(listIndex);
        slot.finallyusefulstonecutter$setX(x);
        slot.finallyusefulstonecutter$setY(y);
    }

    @Unique
    private StonecutterMenu finallyusefulstonecutter$menu() {
        return (StonecutterMenu) ((AbstractContainerScreenAccessor) this).finallyusefulstonecutter$getMenu();
    }

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

    @Inject(method = "renderBg", at = @At("HEAD"), cancellable = true)
    private void finallyusefulstonecutter$renderBg(
        GuiGraphics guiGraphics,
        float partialTick,
        int mouseX,
        int mouseY,
        CallbackInfo ci
    ) {
        ci.cancel();

        AbstractContainerScreenAccessor accessor = (AbstractContainerScreenAccessor) this;
        int left = accessor.finallyusefulstonecutter$getLeftPos();
        int top = accessor.finallyusefulstonecutter$getTopPos();

        guiGraphics.blit(finallyusefulstonecutter$BG_LOCATION, left, top, 0, 0, finallyusefulstonecutter$IMAGE_WIDTH, finallyusefulstonecutter$IMAGE_HEIGHT);

        boolean scrollActive = this.isScrollBarActive();
        int travel = finallyusefulstonecutter$SCROLLBAR_HEIGHT - finallyusefulstonecutter$SCROLLBAR_THUMB_HEIGHT;
        int thumbY = top + finallyusefulstonecutter$SCROLLBAR_Y + (int) (travel * this.scrollOffs);
        int thumbX = left + finallyusefulstonecutter$SCROLLBAR_X;
        int thumbU = scrollActive ? finallyusefulstonecutter$SCROLLBAR_THUMB_U : finallyusefulstonecutter$SCROLLBAR_THUMB_DISABLED_U;
        int thumbV = scrollActive ? finallyusefulstonecutter$SCROLLBAR_THUMB_V : finallyusefulstonecutter$SCROLLBAR_THUMB_DISABLED_V;
        guiGraphics.blit(finallyusefulstonecutter$BG_LOCATION, thumbX, thumbY, thumbU, thumbV, finallyusefulstonecutter$SCROLLBAR_WIDTH, finallyusefulstonecutter$SCROLLBAR_THUMB_HEIGHT);

        finallyusefulstonecutter$renderButtonsNew(guiGraphics, mouseX, mouseY, left, top);
        finallyusefulstonecutter$renderRecipesNew(guiGraphics, left, top);
    }

    @Unique
    private void finallyusefulstonecutter$renderButtonsNew(
        GuiGraphics guiGraphics,
        int mouseX,
        int mouseY,
        int left,
        int top
    ) {
        if (!this.displayRecipes) {
            return;
        }

        StonecutterMenu menu = finallyusefulstonecutter$menu();
        List<Integer> visible = finallyusefulstonecutter$visibleIndices(menu);
        int selectedReal = menu.getSelectedRecipeIndex();
        int end = Math.min(this.startIndex + finallyusefulstonecutter$VISIBLE_SLOTS, visible.size());

        for (int i = this.startIndex; i < end; i++) {
            int slot = i - this.startIndex;
            int col = slot % finallyusefulstonecutter$GRID_COLUMNS;
            int row = slot / finallyusefulstonecutter$GRID_COLUMNS;
            int x = left + finallyusefulstonecutter$GRID_X + col * finallyusefulstonecutter$CELL_WIDTH;
            int y = top + finallyusefulstonecutter$GRID_Y + row * finallyusefulstonecutter$CELL_HEIGHT;
            int realIndex = visible.get(i);

            int u = finallyusefulstonecutter$BUTTON_U;
            int v = finallyusefulstonecutter$BUTTON_IDLE_V;
            if (realIndex == selectedReal) {
                v = finallyusefulstonecutter$BUTTON_SELECTED_V;
            } else if (mouseX >= x && mouseY >= y && mouseX < x + finallyusefulstonecutter$CELL_WIDTH && mouseY < y + finallyusefulstonecutter$CELL_HEIGHT) {
                u = finallyusefulstonecutter$BUTTON_HOVER_U;
                v = finallyusefulstonecutter$BUTTON_HOVER_V;
            }
            guiGraphics.blit(finallyusefulstonecutter$BG_LOCATION, x, y, u, v, finallyusefulstonecutter$CELL_WIDTH, finallyusefulstonecutter$CELL_HEIGHT);
        }
    }

    @Unique
    private void finallyusefulstonecutter$renderRecipesNew(GuiGraphics guiGraphics, int left, int top) {
        if (!this.displayRecipes) {
            return;
        }

        var menu = finallyusefulstonecutter$menu();
        List<Integer> visible = finallyusefulstonecutter$visibleIndices(menu);
        List<RecipeHolder<StonecutterRecipe>> all = menu.getRecipes();
        int end = Math.min(this.startIndex + finallyusefulstonecutter$VISIBLE_SLOTS, visible.size());

        for (int i = this.startIndex; i < end; i++) {
            int slot = i - this.startIndex;
            int col = slot % finallyusefulstonecutter$GRID_COLUMNS;
            int row = slot / finallyusefulstonecutter$GRID_COLUMNS;
            int x = left + finallyusefulstonecutter$GRID_X + col * finallyusefulstonecutter$CELL_WIDTH;
            int y = top + finallyusefulstonecutter$GRID_Y + row * finallyusefulstonecutter$CELL_HEIGHT + 1;
            int realIndex = visible.get(i);

            guiGraphics.renderItem(all.get(realIndex).value().getResultItem(Minecraft.getInstance().level.registryAccess()), x, y);
        }
    }

    @Redirect(
        method = {"mouseClicked", "renderTooltip"},
        at = @At(value = "FIELD", target = "Lnet/minecraft/client/gui/screens/inventory/StonecutterScreen;displayRecipes:Z", opcode = Opcodes.GETFIELD)
    )
    private boolean finallyusefulstonecutter$suppressStaleGeometry(StonecutterScreen self) {
        // prevent stack overflow
        return false;
    }

    @Inject(method = "renderTooltip", at = @At("TAIL"))
    private void finallyusefulstonecutter$renderTooltip(
        GuiGraphics guiGraphics,
        int mouseX,
        int mouseY,
        CallbackInfo ci
    ) {
        if (!this.displayRecipes) {
            return;
        }

        AbstractContainerScreenAccessor accessor = (AbstractContainerScreenAccessor) this;
        int left = accessor.finallyusefulstonecutter$getLeftPos();
        int top = accessor.finallyusefulstonecutter$getTopPos();
        StonecutterMenu menu = finallyusefulstonecutter$menu();
        List<Integer> visible = finallyusefulstonecutter$visibleIndices(menu);
        List<RecipeHolder<StonecutterRecipe>> all = menu.getRecipes();
        int end = Math.min(this.startIndex + finallyusefulstonecutter$VISIBLE_SLOTS, visible.size());

        for (int i = this.startIndex; i < end; i++) {
            int slot = i - this.startIndex;
            int col = slot % finallyusefulstonecutter$GRID_COLUMNS;
            int row = slot / finallyusefulstonecutter$GRID_COLUMNS;
            int x = left + finallyusefulstonecutter$GRID_X + col * finallyusefulstonecutter$CELL_WIDTH;
            int y = top + finallyusefulstonecutter$GRID_Y + row * finallyusefulstonecutter$CELL_HEIGHT;

            if (mouseX >= x && mouseX < x + finallyusefulstonecutter$CELL_WIDTH && mouseY >= y && mouseY < y + finallyusefulstonecutter$CELL_HEIGHT) {
                int realIndex = visible.get(i);
                guiGraphics.renderTooltip(Minecraft.getInstance().font, all.get(realIndex).value().getResultItem(Minecraft.getInstance().level.registryAccess()), mouseX, mouseY);
                break;
            }
        }
    }

    @Inject(method = "mouseClicked", at = @At("HEAD"))
    private void finallyusefulstonecutter$unfocusSearchBoxIfClickedElsewhere(
        double mouseX,
        double mouseY,
        int button,
        CallbackInfoReturnable<Boolean> cir
    ) {
        EditBox searchBox = this.finallyusefulstonecutter$getSearchBox();
        if (searchBox != null && searchBox.isFocused() && !searchBox.isMouseOver(mouseX, mouseY)) {
            searchBox.setFocused(false);
        }
    }

    @Inject(method = "mouseClicked", at = @At("TAIL"), cancellable = true)
    private void finallyusefulstonecutter$mouseClicked(
        double mouseX,
        double mouseY,
        int button,
        CallbackInfoReturnable<Boolean> cir
    ) {
        if (!this.displayRecipes) {
            return;
        }

        AbstractContainerScreenAccessor accessor = (AbstractContainerScreenAccessor) this;
        int left = accessor.finallyusefulstonecutter$getLeftPos();
        int top = accessor.finallyusefulstonecutter$getTopPos();
        StonecutterMenu menu = finallyusefulstonecutter$menu();
        List<Integer> visible = finallyusefulstonecutter$visibleIndices(menu);
        int end = Math.min(this.startIndex + finallyusefulstonecutter$VISIBLE_SLOTS, visible.size());

        for (int i = this.startIndex; i < end; i++) {
            int slot = i - this.startIndex;
            int col = slot % finallyusefulstonecutter$GRID_COLUMNS;
            int row = slot / finallyusefulstonecutter$GRID_COLUMNS;
            double x = mouseX - (left + finallyusefulstonecutter$GRID_X + col * finallyusefulstonecutter$CELL_WIDTH);
            double y = mouseY - (top + finallyusefulstonecutter$GRID_Y + row * finallyusefulstonecutter$CELL_HEIGHT);

            if (x >= 0.0D && y >= 0.0D && x < finallyusefulstonecutter$CELL_WIDTH && y < finallyusefulstonecutter$CELL_HEIGHT) {
                int realIndex = visible.get(i);
                if (menu.clickMenuButton(Minecraft.getInstance().player, realIndex)) {
                    finallyusefulstonecutter$rememberSelection(menu, realIndex);
                    Minecraft.getInstance().getSoundManager()
                        .play(SimpleSoundInstance.forUI(SoundEvents.UI_STONECUTTER_SELECT_RECIPE, 1.0F));
                    Minecraft.getInstance().gameMode.handleInventoryButtonClick(menu.containerId, realIndex);
                    cir.setReturnValue(true);
                    return;
                }
                break;
            }
        }

        int scrollbarX = left + finallyusefulstonecutter$SCROLLBAR_X;
        int scrollbarY = top + finallyusefulstonecutter$SCROLLBAR_Y;
        if (mouseX >= scrollbarX && mouseX < scrollbarX + finallyusefulstonecutter$SCROLLBAR_WIDTH && mouseY >= scrollbarY
            && mouseY < scrollbarY + finallyusefulstonecutter$SCROLLBAR_HEIGHT) {
            this.scrolling = true;
        }
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

    @ModifyConstant(method = "mouseDragged", constant = @Constant(intValue = 14))
    private int finallyusefulstonecutter$mouseDraggedTrackTop(int original) {
        return finallyusefulstonecutter$SCROLLBAR_Y;
    }

    @ModifyConstant(method = "mouseDragged", constant = @Constant(intValue = 54))
    private int finallyusefulstonecutter$mouseDraggedTrackHeight(int original) {
        return finallyusefulstonecutter$SCROLLBAR_HEIGHT;
    }

    @ModifyConstant(method = "mouseDragged", constant = @Constant(intValue = 4))
    private int finallyusefulstonecutter$mouseDraggedColumns(int original) {
        return finallyusefulstonecutter$GRID_COLUMNS;
    }

    @Inject(method = "mouseScrolled", at = @At("HEAD"), cancellable = true)
    private void finallyusefulstonecutter$mouseScrolled(
        double mouseX,
        double mouseY,
        double scrollX,
        double scrollY,
        CallbackInfoReturnable<Boolean> cir
    ) {
        if (this.isScrollBarActive()) {
            int rows = this.getOffscreenRows();
            float f = (float) scrollY / (float) rows;
            this.scrollOffs = Mth.clamp(this.scrollOffs - f, 0.0F, 1.0F);
            this.startIndex = (int) ((double) (this.scrollOffs * (float) rows) + 0.5D) * finallyusefulstonecutter$GRID_COLUMNS;
        }
        cir.setReturnValue(true);
    }

    @Inject(method = "isScrollBarActive", at = @At("HEAD"), cancellable = true)
    private void finallyusefulstonecutter$isScrollBarActiveInject(CallbackInfoReturnable<Boolean> cir) {
        boolean active = this.displayRecipes
            && finallyusefulstonecutter$visibleIndices(finallyusefulstonecutter$menu()).size() > finallyusefulstonecutter$VISIBLE_SLOTS;
        cir.setReturnValue(active);
    }

    @Inject(method = "getOffscreenRows", at = @At("HEAD"), cancellable = true)
    private void finallyusefulstonecutter$getOffscreenRowsInject(CallbackInfoReturnable<Integer> cir) {
        int count = finallyusefulstonecutter$visibleIndices(finallyusefulstonecutter$menu()).size();
        cir.setReturnValue((count + finallyusefulstonecutter$GRID_COLUMNS - 1) / finallyusefulstonecutter$GRID_COLUMNS - finallyusefulstonecutter$GRID_ROWS);
    }

    @Inject(method = "containerChanged", at = @At("TAIL"))
    private void finallyusefulstonecutter$onContainerChanged(CallbackInfo ci) {
        StonecutterMenu menu = finallyusefulstonecutter$menu();
        ItemStack input = menu.getSlot(StonecutterMenu.INPUT_SLOT).getItem();
        if (input.isEmpty()) {
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

        StonecutterMenu menu = finallyusefulstonecutter$menu();
        ItemStack currentInput = menu.getSlot(StonecutterMenu.INPUT_SLOT).getItem();
        if (currentInput.isEmpty() || !currentInput.is(finallyusefulstonecutter$pendingAutoSelectItem)) {
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
}