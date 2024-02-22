package name.giacomofurlan.compassfinder.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import name.giacomofurlan.compassfinder.CompassFinder;
import net.minecraft.item.CompassItem;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;

@Mixin(CompassItem.class)
public class CompassMixin {
    @Inject(method = "hasLodestone", cancellable = true, at = @At(value = "RETURN"))
    private static void hasLodestone(ItemStack stack, CallbackInfoReturnable<Boolean> cir) {
        NbtCompound nbt = stack.getNbt();
        if (nbt != null && nbt.contains(CompassFinder.MODDED_COMPASS_ORE_KEY)) {
            cir.setReturnValue(true);
        }
    }
}
