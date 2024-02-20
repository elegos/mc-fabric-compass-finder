package name.giacomofurlan.compassfinder.mixin;

import java.util.ArrayList;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import name.giacomofurlan.compassfinder.CompassFinder;
import name.giacomofurlan.compassfinder.NeedleOption;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.Mouse;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.text.Text;

@Mixin(Mouse.class)
public class MouseMixin {
    private static final String COMPASS_TR_KEY = "item.minecraft.compass";
    private static final ArrayList<NeedleOption> needleOptions = new ArrayList<>(){{
        add(NeedleOption.SPAWN_POINT);
        add(NeedleOption.ORE_COAL);
        add(NeedleOption.ORE_COPPER);
        add(NeedleOption.ORE_IRON);
        add(NeedleOption.ORE_GOLD);
        add(NeedleOption.ORE_LAPIS_LAZULI);
        add(NeedleOption.ORE_REDSTONE);
        add(NeedleOption.ORE_DIAMOND);
        add(NeedleOption.ORE_EMERALD);
        add(NeedleOption.ORE_NETHER_QUARTZ);
        add(NeedleOption.ORE_ANCIENT_DEBRIS);
    }};
    private static final int numOptions = 12;

    @Inject(method = "onMouseScroll", cancellable = true, at = @At(value = "FIELD"))
    private void onMouseScroll(long window, double horizontal, double vertical, CallbackInfo ci) {
        ClientPlayerEntity player = MinecraftClient.getInstance().player;
        if (player == null) {
            return;
        }

        ItemStack currentItem = player.getInventory().getMainHandStack();
        Item compass = currentItem.getItem();

        if (compass.getTranslationKey().equals(COMPASS_TR_KEY) && CompassFinder.isCtrlDown()) {
            int index = Math.floorMod(
                Math.round(needleOptions.indexOf(CompassFinder.getNeedleOption()) + Math.floor(vertical) * -1),
                numOptions -1
            );

            NeedleOption nextOption = needleOptions.get(index);
            player.sendMessage(Text.literal(String.format("Compass - pointing to: %s", nextOption.label)));
            CompassFinder.setNeedleOption(nextOption);
            CompassFinder.searchAndSetCompassNbt(currentItem);


            ci.cancel();
        }
    }
}
