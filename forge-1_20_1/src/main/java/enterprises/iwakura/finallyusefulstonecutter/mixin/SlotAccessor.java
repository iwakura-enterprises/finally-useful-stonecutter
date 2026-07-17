package enterprises.iwakura.finallyusefulstonecutter.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.gen.Accessor;

import net.minecraft.world.inventory.Slot;

@Mixin(Slot.class)
public interface SlotAccessor {

    @Mutable
    @Accessor("x")
    void finallyusefulstonecutter$setX(int x);

    @Mutable
    @Accessor("y")
    void finallyusefulstonecutter$setY(int y);
}