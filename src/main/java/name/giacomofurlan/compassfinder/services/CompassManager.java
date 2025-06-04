package name.giacomofurlan.compassfinder.services;

import java.util.ArrayList;
import java.util.Optional;

import name.giacomofurlan.compassfinder.CompassFinder;
import name.giacomofurlan.compassfinder.NeedleOption;
import name.giacomofurlan.compassfinder.components.CompassFinderComponentTypes;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.resource.language.I18n;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.component.type.LodestoneTrackerComponent;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.text.Text;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.GlobalPos;

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

    public static void updateCompassPos(ClientPlayerEntity player, NeedleOption ore, BlockPos pos) {
        updateCompassPos(player, ore, pos, null);
    }

    public static void updateCompassPos(ClientPlayerEntity player, NeedleOption ore, BlockPos pos, ItemStack stack) {
        if (player == null) {
            return;
        }

        if (stack != null) {
            if (pos == null) {
                stack.remove(DataComponentTypes.LODESTONE_TRACKER);
            } else {
                stack.set(
                    DataComponentTypes.LODESTONE_TRACKER,
                    new LodestoneTrackerComponent(Optional.of(GlobalPos.create(player.getWorld().getRegistryKey(), pos)), true)
                );
            }

            return;
        }

        PlayerInventory inventory = player.getInventory();
        for (int slot = 0; slot < inventory.size(); slot++) {
            stack = inventory.getStack(slot);

            if (stack.contains(CompassFinderComponentTypes.ORE_TYPE) || !stack.get(CompassFinderComponentTypes.ORE_TYPE).equals(ore.translationKey)) {
                continue;
            }

            if (pos != null) {
                stack.set(
                    DataComponentTypes.LODESTONE_TRACKER,
                    new LodestoneTrackerComponent(Optional.of(GlobalPos.create(player.getWorld().getRegistryKey(), pos)), true)
                );
            } else {
                stack.remove(DataComponentTypes.LODESTONE_TRACKER);
            }
        }
    }

    public static void setCompassOre(ItemStack stack, NeedleOption ore) {
        if (stack == null) {
            return;
        }

        if (ore.equals(NeedleOption.SPAWN_POINT)) {
            if (stack.contains(CompassFinderComponentTypes.ORE_TYPE)) { stack.remove(CompassFinderComponentTypes.ORE_TYPE); }
            if (stack.contains(DataComponentTypes.LODESTONE_TRACKER)) { stack.remove(DataComponentTypes.LODESTONE_TRACKER); }

            if (stack.contains(DataComponentTypes.CUSTOM_NAME)) {
                stack.remove(DataComponentTypes.CUSTOM_NAME);
            }

            return;
        }

        Optional<GlobalPos> gPos = stack.contains(DataComponentTypes.LODESTONE_TRACKER) ? stack.get(DataComponentTypes.LODESTONE_TRACKER).target() : Optional.empty();
        stack.set(DataComponentTypes.LODESTONE_TRACKER, new LodestoneTrackerComponent(gPos, true));
        stack.set(CompassFinderComponentTypes.ORE_TYPE, ore.translationKey);

        if (ore == NeedleOption.LODESTONE_MODE && stack.contains(DataComponentTypes.CUSTOM_NAME)) {
            stack.remove(DataComponentTypes.CUSTOM_NAME);
        } else {
            stack.set(DataComponentTypes.CUSTOM_NAME, Text.of(I18n.translate(stack.getItem().getTranslationKey()) + " (" + I18n.translate(ore.translationKey) + ")"));
        }
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

        ItemStack currentStack = player.getMainHandStack();
        Item compass = currentStack.getItem();

        if (!compass.getTranslationKey().equals(COMPASS_TR_KEY) || !Screen.hasControlDown()) {
            return false;
        }

        NeedleOption currentOption = currentStack.contains(CompassFinderComponentTypes.ORE_TYPE)
            ? NeedleOption.fromTranslationKey(currentStack.get(CompassFinderComponentTypes.ORE_TYPE))
            : NeedleOption.SPAWN_POINT;

        int raw_index = needleOptions.indexOf(currentOption) + direction;
        int index = raw_index < 0 ? numOptions + raw_index : raw_index % numOptions;

        NeedleOption nextOption = needleOptions.get(index);
        player.sendMessage(Text.literal(String.format(
            "Compass - pointing to: %s",
            nextOption.translationKey != null ? I18n.translate(nextOption.translationKey) : nextOption.label
        )), true);

        setCompassOre(currentStack, nextOption);
        updateCompassPos(player, nextOption, CompassFinder.getNearestBlockPos(nextOption.blocks), currentStack);

        return true;
    }
}
