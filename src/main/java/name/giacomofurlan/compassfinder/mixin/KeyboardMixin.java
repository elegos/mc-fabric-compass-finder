package name.giacomofurlan.compassfinder.mixin;

import org.lwjgl.glfw.GLFW;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import name.giacomofurlan.compassfinder.services.CompassManager;
import net.minecraft.client.Keyboard;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayerEntity;

@Mixin(Keyboard.class)
public class KeyboardMixin {
    // Alternate arrow up/down shortcut
    @Inject(method = "onKey", cancellable = true, at = @At(value = "FIELD"))
    private void onKey(long window, int key, int scancode, int action, int modifiers, CallbackInfo ci) {
        Boolean forward = null;
        if (key == GLFW.GLFW_KEY_UP) {
            forward = false;
        } else if (key == GLFW.GLFW_KEY_DOWN) {
            forward = true;
        }

        MinecraftClient client = MinecraftClient.getInstance();
        ClientPlayerEntity player = client.player;
        if (player == null || forward == null || action == 0) {
            return;
        }

        if (CompassManager.changeCompassPointing(player, forward)) {
            ci.cancel();
        }
    }
}
