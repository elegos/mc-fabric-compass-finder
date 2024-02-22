package name.giacomofurlan.compassfinder;

import java.util.HashMap;

import name.giacomofurlan.compassfinder.services.CompassManager;
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
        HashMap<String, BlockPos> nearestPos = new HashMap<>();

        for (int slot = 0; slot < inventory.size(); slot++) {
            ItemStack stack = inventory.getStack(slot);
            NbtCompound nbt = stack.getNbt();
            if (nbt != null && nbt.contains(CompassFinder.MODDED_COMPASS_ORE_KEY)) {
                String translationKey = nbt.getString(CompassFinder.MODDED_COMPASS_ORE_KEY);
                NeedleOption option = NeedleOption.fromTranslationKey(translationKey);
                BlockPos pos = nearestPos.getOrDefault(translationKey, null);
                if (pos == null && option != null) {
                    pos = CompassFinder.getNearestBlockPos(option.blocks);
                    nearestPos.put(translationKey, pos);
                }
                CompassManager.updateCompassPos(player, option, pos, stack);
            }
        }
    }
}
