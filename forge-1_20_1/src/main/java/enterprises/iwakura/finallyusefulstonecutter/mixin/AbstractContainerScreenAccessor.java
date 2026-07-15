package enterprises.iwakura.finallyusefulstonecutter.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.world.inventory.AbstractContainerMenu;

@Mixin(AbstractContainerScreen.class)
public interface AbstractContainerScreenAccessor {

    @Accessor("menu")
    AbstractContainerMenu finallyusefulstonecutter$getMenu();

    @Accessor("leftPos")
    int finallyusefulstonecutter$getLeftPos();

    @Accessor("topPos")
    int finallyusefulstonecutter$getTopPos();
}