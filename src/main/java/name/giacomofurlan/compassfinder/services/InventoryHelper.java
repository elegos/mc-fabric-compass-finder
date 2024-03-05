package name.giacomofurlan.compassfinder.services;

import java.util.HashMap;
import java.util.Optional;

import name.giacomofurlan.compassfinder.CompassFinder;
import name.giacomofurlan.compassfinder.NeedleOption;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.CompassItem;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtElement;
import net.minecraft.nbt.NbtHelper;
import net.minecraft.nbt.NbtOps;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.math.Vec3i;
import net.minecraft.world.World;

public class InventoryHelper {
    public static void updateHotbarDistances() {
        MinecraftClient client = MinecraftClient.getInstance();
        ClientPlayerEntity player = client.player;
        Optional<NbtElement> instanceNbt = World.CODEC.encodeStart(NbtOps.INSTANCE, player.getWorld().getRegistryKey())
            .resultOrPartial(CompassFinder.LOGGER::error);

        if (player == null || instanceNbt.isEmpty()) {
            return;
        }

        PlayerInventory inventory = player.getInventory();
        for (int slot = 0; slot < PlayerInventory.getHotbarSize(); slot++) {
            ItemStack stack = inventory.getStack(slot);
            NbtCompound nbt = stack.getNbt();

            if (!stack.getItem().getTranslationKey().equals(CompassManager.COMPASS_TR_KEY)) {
                continue;
            }

            Integer stackCount = 1;

            if (
                nbt != null
                && nbt.contains(CompassFinder.MODDED_COMPASS_ORE_KEY)
                && nbt.contains(CompassItem.LODESTONE_POS_KEY)
                && nbt.getString(CompassItem.LODESTONE_DIMENSION_KEY).equals(instanceNbt.get().asString())
            ) {
                BlockPos targetPos = NbtHelper.toBlockPos(nbt.getCompound(CompassItem.LODESTONE_POS_KEY));
                Vec3d playerPos = player.getPos();

                // Lodestone mode doesn't care about height
                if (nbt.getString(CompassFinder.MODDED_COMPASS_ORE_KEY) == NeedleOption.LODESTONE_MODE.translationKey) {
                    playerPos = new Vec3d(playerPos.getX(), targetPos.getY(), playerPos.getZ());
                }
                stackCount = (int) targetPos.getManhattanDistance(new Vec3i((int) playerPos.getX(), (int) playerPos.getY(), (int) playerPos.getZ()));
            }

            stack.setCount(stackCount > 0 ? stackCount : 1);
        }
    }

    public static void updateCompasses() {
        updateCompasses(null);
    }

    public static void updateCompasses(BlockPos brokenPos) {
        MinecraftClient client = MinecraftClient.getInstance();
        ClientPlayerEntity player = client.player;
        if (player == null) {
            return;
        }

        PlayerInventory inventory = player.getInventory();
        HashMap<String, BlockPos> nearestPos = new HashMap<>();
        CompassManager.removeNbtCache(brokenPos);

        for (int slot = 0; slot < inventory.size(); slot++) {
            ItemStack stack = inventory.getStack(slot);
            NbtCompound nbt = stack.getOrCreateNbt();
            NbtCompound cachedNbt = CompassManager.getCachedNbt(slot + 1);

            if (nbt.getSize() == 0 && cachedNbt != null) {
                nbt.copyFrom(cachedNbt);
            }
            
            if (nbt != null && nbt.contains(CompassFinder.MODDED_COMPASS_ORE_KEY)) {
                String translationKey = nbt.getString(CompassFinder.MODDED_COMPASS_ORE_KEY);
                NeedleOption option = NeedleOption.fromTranslationKey(translationKey);
                if (option == NeedleOption.LODESTONE_MODE) {
                    continue;
                }

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
