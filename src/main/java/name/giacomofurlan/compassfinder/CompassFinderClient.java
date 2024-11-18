package name.giacomofurlan.compassfinder;

import java.util.Optional;

import com.mojang.brigadier.CommandDispatcher;

import name.giacomofurlan.compassfinder.commands.LodestoneRegisterCommand;
import name.giacomofurlan.compassfinder.components.CompassFinderComponentTypes;
import name.giacomofurlan.compassfinder.services.InventoryHelper;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.command.v2.ClientCommandRegistrationCallback;
import net.fabricmc.fabric.api.client.command.v2.FabricClientCommandSource;
import net.fabricmc.fabric.api.client.rendering.v1.HudRenderCallback;
import net.fabricmc.fabric.api.event.client.player.ClientPlayerBlockBreakEvents;
import net.minecraft.block.BlockState;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.render.RenderTickCounter;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.command.CommandRegistryAccess;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.component.type.LodestoneTrackerComponent;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.GlobalPos;

public class CompassFinderClient implements ClientModInitializer {

    @Override
    public void onInitializeClient() {
        ClientPlayerBlockBreakEvents.AFTER.register((ClientWorld world, ClientPlayerEntity player, BlockPos pos, BlockState state) -> {
            this.onClientPlayerBlockBreakEventsCallback(world, player, pos, state);
        });

        HudRenderCallback.EVENT.register(new HudRenderCallback() {
            @Override
            public void onHudRender(DrawContext drawContext, RenderTickCounter tickCounter) {
                onHudRenderCallback(drawContext, tickCounter);
            }
        });

        ClientCommandRegistrationCallback.EVENT.register(
            (CommandDispatcher<FabricClientCommandSource> dispatcher, CommandRegistryAccess registryAccess) -> {
                LodestoneRegisterCommand.register(dispatcher);
            }
        );
    }

    public void onClientPlayerBlockBreakEventsCallback(ClientWorld world, ClientPlayerEntity player, BlockPos pos, BlockState state) {
        PlayerInventory inventory = player.getInventory();

        for (int slot = 0; slot < inventory.size(); slot++) {
            ItemStack stack = inventory.getStack(slot);
            Boolean moddedCompass = stack.contains(CompassFinderComponentTypes.ORE_TYPE);
            LodestoneTrackerComponent lodestoneComp = stack.contains(DataComponentTypes.LODESTONE_TRACKER)
                ? stack.get(DataComponentTypes.LODESTONE_TRACKER)
                : null;

            Optional<GlobalPos> lodestoneTarget = lodestoneComp != null ? lodestoneComp.target() : Optional.empty();

            if (moddedCompass && lodestoneTarget.isPresent() && lodestoneTarget.get().pos().equals(pos)) {
                InventoryHelper.updateCompasses();

                break;
            }
        }
    }

    public static void onHudRenderCallback(DrawContext drawContext, RenderTickCounter tickCounter) {
        InventoryHelper.updateHotbarDistances();
    }
}
