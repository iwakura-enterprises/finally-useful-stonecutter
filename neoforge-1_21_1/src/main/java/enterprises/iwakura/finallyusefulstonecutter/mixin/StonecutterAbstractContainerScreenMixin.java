package enterprises.iwakura.finallyusefulstonecutter.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import enterprises.iwakura.finallyusefulstonecutter.StonecutterScreenDuck;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.gui.screens.inventory.StonecutterScreen;
import net.minecraft.network.chat.Component;

@Mixin(AbstractContainerScreen.class)
public abstract class StonecutterAbstractContainerScreenMixin implements StonecutterScreenDuck {

    @Unique
    private static final int finallyusefulstonecutter$xOffset = 90;
    @Unique
    private static final int finallyusefulstonecutter$yOffset = 0;
    @Unique
    private static String finallyusefulstonecutter$lastSearch = "";

    @Unique
    private EditBox finallyusefulstonecutter$searchBox;

    @Inject(method = "tick", at = @At("TAIL"))
    private void finallyusefulstonecutter$tick(CallbackInfo ci) {
        if ((Object) this instanceof StonecutterScreen) {
            this.finallyusefulstonecutter$tickPendingAutoSelect();
        }
    }

    @Inject(method = "render", at = @At("TAIL"), cancellable = true)
    public void render(
        GuiGraphics gui, int p_282517_, int p_282840_, float p_282389_, CallbackInfo ci
    ) {
        if ((Object) this instanceof StonecutterScreen) {
            if (finallyusefulstonecutter$searchBox != null) {
                finallyusefulstonecutter$searchBox.render(gui, p_282517_, p_282840_, p_282389_);
            }
        }
    }

    @Inject(method = "init", at = @At("TAIL"))
    private void finallyusefulstonecutter$onInit(CallbackInfo ci) {
        if ((Object) this instanceof StonecutterScreen) {
            int leftPos = ((AbstractContainerScreenAccessor) this).finallyusefulstonecutter$getLeftPos();
            int topPos = ((AbstractContainerScreenAccessor) this).finallyusefulstonecutter$getTopPos();

            if (finallyusefulstonecutter$searchBox == null) {
                finallyusefulstonecutter$searchBox = new EditBox(
                    Minecraft.getInstance().font,
                    leftPos + finallyusefulstonecutter$xOffset,
                    topPos + finallyusefulstonecutter$yOffset,
                    81, Minecraft.getInstance().font.lineHeight + 3,
                    Component.translatable("gui.recipebook.search_hint")
                );
                finallyusefulstonecutter$searchBox.setMaxLength(50);
                finallyusefulstonecutter$searchBox.setVisible(true);
                finallyusefulstonecutter$searchBox.setTextColor(16777215);
                finallyusefulstonecutter$searchBox.setHint(Component.translatable("gui.recipebook.search_hint")
                    .withStyle(ChatFormatting.ITALIC).withStyle(ChatFormatting.GRAY));
                finallyusefulstonecutter$searchBox.setResponder(text -> {
                    finallyusefulstonecutter$lastSearch = text;

                    if (!text.isBlank() || !finallyusefulstonecutter$lastSearch.isBlank()) {
                        // UX; don't reset scroll if user just clicked the search
                        this.finallyusefulstonecutter$resetScroll(); // snap back to the top
                    }
                });

                finallyusefulstonecutter$searchBox.setValue(finallyusefulstonecutter$lastSearch);
                ((ScreenAccessor) this).finallyusefulstonecutter$getChildren().add(finallyusefulstonecutter$searchBox);
            } else {
                finallyusefulstonecutter$searchBox.setX(leftPos + finallyusefulstonecutter$xOffset);
                finallyusefulstonecutter$searchBox.setY(topPos + finallyusefulstonecutter$yOffset);
            }
        }
    }

    @Override
    public EditBox finallyusefulstonecutter$getSearchBox() {
        return finallyusefulstonecutter$searchBox;
    }

    @Override
    public String finallyusefulstonecutter$getFilter() {
        return finallyusefulstonecutter$searchBox == null ? "" : finallyusefulstonecutter$searchBox.getValue();
    }
}