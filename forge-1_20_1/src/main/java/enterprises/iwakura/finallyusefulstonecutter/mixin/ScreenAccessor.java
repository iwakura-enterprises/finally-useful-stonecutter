package enterprises.iwakura.finallyusefulstonecutter.mixin;

import java.util.List;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

import net.minecraft.client.gui.components.events.GuiEventListener;
import net.minecraft.client.gui.screens.Screen;

@Mixin(Screen.class)
public interface ScreenAccessor {

    // Registering the search box directly into this list is more robust than invoking
    // Screen#addWidget (that Invoker failed to resolve in some heavily mixin-modded
    // instances) — "children" is a core, stable field that Screen's default input-routing
    // (charTyped/keyPressed/mouseClicked) already reads from.
    @Accessor("children")
    List<GuiEventListener> finallyusefulstonecutter$getChildren();
}