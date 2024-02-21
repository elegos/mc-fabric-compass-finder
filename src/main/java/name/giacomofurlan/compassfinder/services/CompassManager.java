package name.giacomofurlan.compassfinder.services;

import java.util.ArrayList;

import name.giacomofurlan.compassfinder.CompassFinder;
import name.giacomofurlan.compassfinder.NeedleOption;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.resource.language.I18n;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.text.Text;

public class CompassManager {
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

    /**
     * Change the main hand's compass pointing ore.
     * @param player
     * @param listDirectionForward true for forward, backwards otherwise
     * @return true if it changed, false otherwise
     */
    public static Boolean changeCompassPointing(ClientPlayerEntity player, Boolean listDirectionForward) {
        Integer direction = listDirectionForward ? 1 : -1;
        Integer numOptions = needleOptions.size();

        ItemStack currentItem = player.getInventory().getMainHandStack();
        Item compass = currentItem.getItem();

        if (!compass.getTranslationKey().equals(COMPASS_TR_KEY) || !Screen.hasControlDown()) {
            return false;
        }

        int index = Math.floorMod(
            Math.round(needleOptions.indexOf(CompassFinder.getNeedleOption()) + direction),
            numOptions
        );

        NeedleOption nextOption = needleOptions.get(index);
        player.sendMessage(Text.literal(String.format(
            "Compass - pointing to: %s",
            nextOption.translationKey != null ? I18n.translate(nextOption.translationKey) : nextOption.label
        )));
        CompassFinder.setNeedleOption(nextOption);
        CompassFinder.searchAndSetCompassNbt(currentItem);

        return true;
    }
}
