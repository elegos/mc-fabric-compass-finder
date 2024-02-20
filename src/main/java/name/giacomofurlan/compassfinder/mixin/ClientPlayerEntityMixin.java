package name.giacomofurlan.compassfinder.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import name.giacomofurlan.compassfinder.CompassFinder;
import name.giacomofurlan.compassfinder.InventoryHelper;
import name.giacomofurlan.compassfinder.NeedleOption;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.util.math.BlockPos;

@Mixin(ClientPlayerEntity.class)
public class ClientPlayerEntityMixin {
    private BlockPos playerPos = null;

    @Inject(method = "tick", at = @At(value = "TAIL"))
    private void tick(CallbackInfo ci) {
        if (CompassFinder.getNeedleOption() == NeedleOption.SPAWN_POINT) {
            return;
        }

        ClientPlayerEntity player = MinecraftClient.getInstance().player;
        BlockPos currentPos = player.getBlockPos();

        if (playerPos != null && currentPos.getSquaredDistance(playerPos) < CompassFinder.SEARCH_RADIUS) {
            return;
        }

        InventoryHelper.updateCompasses();
        playerPos = currentPos;
    }
}
