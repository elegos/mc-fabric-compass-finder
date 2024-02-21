package name.giacomofurlan.compassfinder.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import name.giacomofurlan.compassfinder.services.CompassManager;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.Mouse;
import net.minecraft.client.network.ClientPlayerEntity;

@Mixin(Mouse.class)
public class MouseMixin {
    @Inject(method = "onMouseScroll", cancellable = true, at = @At(value = "FIELD"))
    private void onMouseScroll(long window, double horizontal, double vertical, CallbackInfo ci) {
        MinecraftClient client = MinecraftClient.getInstance();
        ClientPlayerEntity player = client.player;
        if (player == null) {
            return;
        }

        if (CompassManager.changeCompassPointing(player, vertical < 0)) {
            ci.cancel();
        }
    }
}
