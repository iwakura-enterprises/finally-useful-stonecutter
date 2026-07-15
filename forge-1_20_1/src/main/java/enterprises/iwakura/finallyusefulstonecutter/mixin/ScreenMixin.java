package enterprises.iwakura.finallyusefulstonecutter.mixin;

import org.lwjgl.glfw.GLFW;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import enterprises.iwakura.finallyusefulstonecutter.StonecutterScreenDuck;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.inventory.StonecutterScreen;

@Mixin(Screen.class)
public abstract class ScreenMixin {

    @Inject(method = "keyPressed", at = @At("HEAD"), cancellable = true)
    private void finallyusefulstonecutter$keyPressed(int keyCode, int scanCode, int modifiers, CallbackInfoReturnable<Boolean> cir) {
        if ((Object) this instanceof StonecutterScreen screen) {
            EditBox searchBox = ((StonecutterScreenDuck) screen).finallyusefulstonecutter$getSearchBox();
            if (searchBox != null && searchBox.isFocused()) {
                if (keyCode == GLFW.GLFW_KEY_ESCAPE) {
                    return;
                }
                searchBox.keyPressed(keyCode, scanCode, modifiers);
                cir.setReturnValue(true);
            }
        }
    }
}