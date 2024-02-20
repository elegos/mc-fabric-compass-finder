package name.giacomofurlan.compassfinder;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.util.math.BlockPos;

public class InventoryHelper {
    public static void updateCompasses() {
        ClientPlayerEntity player = MinecraftClient.getInstance().player;
        if (player == null) {
            return;
        }

        PlayerInventory inventory = player.getInventory();
        BlockPos nearestPos = CompassFinder.getNearestBlockPos();

        for (int slot = 0; slot < inventory.size(); slot++) {
            ItemStack stack = inventory.getStack(slot);
            NbtCompound nbt = stack.getNbt();
            if (nbt == null || !nbt.contains(CompassFinder.MODDED_COMPASS_KEY)) {
                continue;
            }

            CompassFinder.setCompassNbt(stack, nearestPos);
        }
    }
}
