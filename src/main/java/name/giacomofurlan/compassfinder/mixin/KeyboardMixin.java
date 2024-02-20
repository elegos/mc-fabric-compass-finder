package name.giacomofurlan.compassfinder.mixin;

import org.lwjgl.glfw.GLFW;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import name.giacomofurlan.compassfinder.CompassFinder;
import net.minecraft.client.Keyboard;

@Mixin(Keyboard.class)
public class KeyboardMixin {
    @Inject(method = "onKey", at = @At(value = "FIELD"))
    private void onKey(long window, int key, int scancode, int action, int modifiers, CallbackInfo ci)
    {
        if (key == GLFW.GLFW_KEY_LEFT_CONTROL || key == GLFW.GLFW_KEY_RIGHT_CONTROL) {
            CompassFinder.setCtrlState(action > 0);
        }
    }
}
