package name.giacomofurlan.compassfinder.services;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Optional;

import name.giacomofurlan.compassfinder.CompassFinder;
import name.giacomofurlan.compassfinder.NeedleOption;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.resource.language.I18n;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.CompassItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtElement;
import net.minecraft.nbt.NbtHelper;
import net.minecraft.nbt.NbtOps;
import net.minecraft.text.Text;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class CompassManager {
    public static final String COMPASS_TR_KEY = "item.minecraft.compass";
    public static final String LODESTONE_COMPASS_TR_KEY = "item.minecraft.lodestone_compass";
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

    protected static HashMap<Integer, NbtCompound> customNbtCache = new HashMap<>();

    public static void updateCompassPos(ClientPlayerEntity player, NeedleOption ore, BlockPos pos) {
        updateCompassPos(player, ore, pos, null);
    }

    public static void updateCompassPos(ClientPlayerEntity player, NeedleOption ore, BlockPos pos, ItemStack stack) {
        if (player == null) {
            return;
        }

        Optional<NbtElement> instanceNbt = World.CODEC.encodeStart(NbtOps.INSTANCE, player.getWorld().getRegistryKey())
            .resultOrPartial(CompassFinder.LOGGER::error);

        if (stack != null) {
            NbtCompound nbt = stack != null ? stack.getOrCreateNbt() : null;

            if (pos != null) {
                nbt.put(CompassItem.LODESTONE_POS_KEY, NbtHelper.fromBlockPos(pos));
                instanceNbt.ifPresent(nbtElement -> nbt.put(CompassItem.LODESTONE_DIMENSION_KEY, (NbtElement)nbtElement));
            } else {
                nbt.remove(CompassItem.LODESTONE_POS_KEY);
            }

            return;
        }

        PlayerInventory inventory = player.getInventory();
        for (int slot = 0; slot < inventory.size(); slot++) {
            stack = inventory.getStack(slot);
            NbtCompound nbt = stack != null ? stack.getNbt() : null;
            if (nbt == null || !nbt.contains(CompassFinder.MODDED_COMPASS_ORE_KEY) || nbt.getString(CompassFinder.MODDED_COMPASS_ORE_KEY) != ore.translationKey) {
                continue;
            }
            if (pos != null) {
                nbt.put(CompassItem.LODESTONE_POS_KEY, NbtHelper.fromBlockPos(pos));
            } else {
                nbt.remove(CompassItem.LODESTONE_POS_KEY);
            }
            instanceNbt.ifPresent(nbtElement -> nbt.put(CompassItem.LODESTONE_DIMENSION_KEY, (NbtElement)nbtElement));
        }
    }

    public static void setCompassOre(ItemStack stack, NeedleOption ore) {
        if (stack == null) {
            return;
        }
        NbtCompound nbt = stack.getOrCreateNbt();

        MinecraftClient client = MinecraftClient.getInstance();
        ClientPlayerEntity player = client.player;
        int slot = player.getInventory().getSlotWithStack(stack) + 1;

        if (ore.equals(NeedleOption.SPAWN_POINT)) {
            nbt.remove(CompassItem.LODESTONE_TRACKED_KEY);
            nbt.remove(CompassItem.LODESTONE_POS_KEY);
            nbt.remove(CompassItem.LODESTONE_DIMENSION_KEY);

            nbt.remove(CompassFinder.MODDED_COMPASS_ORE_KEY);
            stack.removeCustomName();

            updateNbtCache(slot, null);

            return;
        }

        nbt.putBoolean(CompassItem.LODESTONE_TRACKED_KEY, true);
        nbt.putString(CompassFinder.MODDED_COMPASS_ORE_KEY, ore.translationKey);
        if (ore == NeedleOption.LODESTONE_MODE) {
            stack.removeCustomName();
        } else {
            stack.setCustomName(
                Text.of(I18n.translate(stack.getItem().getTranslationKey()) + " (" + I18n.translate(ore.translationKey) + ")")
            );
        }

        CompassManager.updateNbtCache(slot, nbt);
    }

    /**
     * Change the main hand's compass pointing ore.
     * @param player
     * @param listDirectionForward true for forward, backwards otherwise
     * @return true if it changed, false otherwise
     */
    public static Boolean changeCompassPointing(ClientPlayerEntity player, Boolean listDirectionForward) {
        Integer direction = listDirectionForward ? 1 : -1;
        Integer numOptions = needleOptions.size();

        ItemStack currentStack = player.getInventory().getMainHandStack();
        Item compass = currentStack.getItem();

        if (!compass.getTranslationKey().equals(COMPASS_TR_KEY) || !Screen.hasControlDown()) {
            return false;
        }

        NbtCompound stackNbt = currentStack.getNbt();
        NeedleOption currentOption = stackNbt != null && stackNbt.contains(CompassFinder.MODDED_COMPASS_ORE_KEY)
            ? NeedleOption.fromTranslationKey(stackNbt.getString(CompassFinder.MODDED_COMPASS_ORE_KEY))
            : NeedleOption.SPAWN_POINT;

        int index = Math.floorMod(
            Math.round(needleOptions.indexOf(currentOption) + direction),
            numOptions
        );

        NeedleOption nextOption = needleOptions.get(index);
        player.sendMessage(Text.literal(String.format(
            "Compass - pointing to: %s",
            nextOption.translationKey != null ? I18n.translate(nextOption.translationKey) : nextOption.label
        )));

        setCompassOre(currentStack, nextOption);
        updateCompassPos(player, nextOption, CompassFinder.getNearestBlockPos(nextOption.blocks), currentStack);

        return true;
    }

    public static void updateNbtCache(int slot, NbtCompound nbt) {
        if (nbt == null && customNbtCache.containsKey(slot)) {
            customNbtCache.remove(slot);
        }

        customNbtCache.put(slot, nbt);
    }

    public static NbtCompound getCachedNbt(int slot) {
        if (customNbtCache.containsKey(slot)) {
            return customNbtCache.get(slot);
        }

        return null;
    }

    public static void removeNbtCache(BlockPos pos) {
        if (pos == null) {
            return;
        }

        customNbtCache.entrySet().removeIf(
            entry -> entry.getValue().contains(CompassItem.LODESTONE_POS_KEY)
                && NbtHelper.toBlockPos(entry.getValue().getCompound(CompassItem.LODESTONE_POS_KEY)).withY(0).equals(pos.withY(0))
        );
    }
}
