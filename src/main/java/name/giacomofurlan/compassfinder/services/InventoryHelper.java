package name.giacomofurlan.compassfinder.services;

import java.util.HashMap;
import java.util.Optional;

import name.giacomofurlan.compassfinder.CompassFinder;
import name.giacomofurlan.compassfinder.NeedleOption;
import name.giacomofurlan.compassfinder.components.CompassFinderComponentTypes;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.component.type.LodestoneTrackerComponent;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtElement;
import net.minecraft.nbt.NbtOps;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.GlobalPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.math.Vec3i;
import net.minecraft.world.World;

public class InventoryHelper {
    public static void updateHotbarDistances() {
        MinecraftClient client = MinecraftClient.getInstance();
        ClientPlayerEntity player = client.player;
        if (player == null) {
            return;
        }

        Optional<NbtElement> instanceNbt = World.CODEC.encodeStart(NbtOps.INSTANCE, player.getWorld().getRegistryKey())
            .resultOrPartial(CompassFinder.LOGGER::error);

        if (player == null || instanceNbt.isEmpty()) {
            return;
        }

        PlayerInventory inventory = player.getInventory();
        for (int slot = 0; slot < PlayerInventory.getHotbarSize(); slot++) {
            ItemStack stack = inventory.getStack(slot);

            if (!stack.getItem().getTranslationKey().equals(CompassManager.COMPASS_TR_KEY)) {
                continue;
            }

            Integer stackCount = 1;
            LodestoneTrackerComponent ltc = stack.get(DataComponentTypes.LODESTONE_TRACKER);
            GlobalPos lodestoneGlobalPos = ltc != null && ltc.target().isPresent() ? ltc.target().get() : null;
            String oreType = stack.contains(CompassFinderComponentTypes.ORE_TYPE) ? stack.get(CompassFinderComponentTypes.ORE_TYPE) : null;

            if (
                oreType != null
                && ltc != null
                && lodestoneGlobalPos != null
                && lodestoneGlobalPos.dimension().equals(player.getWorld().getRegistryKey())
            ) {
                BlockPos targetPos = lodestoneGlobalPos.pos();
                Vec3d playerPos = player.getPos();

                // Lodestone mode doesn't care about height
                if (oreType == NeedleOption.LODESTONE_MODE.translationKey) {
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

        for (int slot = 0; slot < inventory.size(); slot++) {
            ItemStack stack = inventory.getStack(slot);
            String oreType = stack.contains(CompassFinderComponentTypes.ORE_TYPE) ? stack.get(CompassFinderComponentTypes.ORE_TYPE) : null;

            if (oreType != null && !oreType.equals("")) {
                NeedleOption option = NeedleOption.fromTranslationKey(oreType);
                if (option == NeedleOption.LODESTONE_MODE) {
                    continue;
                }

                BlockPos pos = nearestPos.getOrDefault(oreType, null);
                if (pos == null && option != null) {
                    pos = CompassFinder.getNearestBlockPos(option.blocks);
                    nearestPos.put(oreType, pos);
                }
                CompassManager.updateCompassPos(player, option, pos, stack);
            }
        }
    }
}
