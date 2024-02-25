package name.giacomofurlan.compassfinder;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.rendering.v1.HudRenderCallback;
import net.fabricmc.fabric.api.event.client.player.ClientPlayerBlockBreakEvents;
import net.minecraft.block.BlockState;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.CompassItem;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtHelper;
import net.minecraft.util.math.BlockPos;

public class CompassFinderClient implements ClientModInitializer {

    @Override
    public void onInitializeClient() {
        ClientPlayerBlockBreakEvents.AFTER.register((ClientWorld world, ClientPlayerEntity player, BlockPos pos, BlockState state) -> {
            this.onClientPlayerBlockBreakEventsCallback(world, player, pos, state);
        });

        HudRenderCallback.EVENT.register((DrawContext drawContext, float tickDelta) -> {
            onHudRenderCallback(drawContext, tickDelta);
        });
    }

    public void onClientPlayerBlockBreakEventsCallback(ClientWorld world, ClientPlayerEntity player, BlockPos pos, BlockState state) {
        PlayerInventory inventory = player.getInventory();

        for (int slot = 0; slot < inventory.size(); slot++) {
            ItemStack stack = inventory.getStack(slot);
            NbtCompound nbt = stack.getNbt();
            Boolean moddedCompass = nbt != null && nbt.contains(CompassFinder.MODDED_COMPASS_ORE_KEY);
            BlockPos lodestoneBlockPos = nbt != null && nbt.contains(CompassItem.LODESTONE_DIMENSION_KEY)
                ? NbtHelper.toBlockPos(nbt.getCompound(CompassItem.LODESTONE_POS_KEY))
                : null;
            
            if (moddedCompass && lodestoneBlockPos.equals(pos)) {
                InventoryHelper.updateCompasses();

                break;
            }
        }
    }

    public static void onHudRenderCallback(DrawContext drawContext, float tickDelta) {
        InventoryHelper.updateHotbarDistances();
    }
}
