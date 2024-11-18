package name.giacomofurlan.compassfinder.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import name.giacomofurlan.compassfinder.components.CompassFinderComponentTypes;
import net.minecraft.item.CompassItem;
import net.minecraft.item.ItemStack;

@Mixin(CompassItem.class)
public class CompassMixin {
    @Inject(method = "hasLodestone", cancellable = true, at = @At(value = "RETURN"))
    private static void hasLodestone(ItemStack stack, CallbackInfoReturnable<Boolean> cir) {
        if (stack.contains(CompassFinderComponentTypes.ORE_TYPE)) {
            cir.setReturnValue(true);
        }
    }
}
