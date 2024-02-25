package name.giacomofurlan.compassfinder;

import java.util.HashMap;
import java.util.Optional;

import name.giacomofurlan.compassfinder.services.CompassManager;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.CompassItem;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtElement;
import net.minecraft.nbt.NbtHelper;
import net.minecraft.nbt.NbtOps;
import net.minecraft.text.Text;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class InventoryHelper {
    public static void updateHotbarDistances(DrawContext drawContext, float tickDelta) {
        MinecraftClient client = MinecraftClient.getInstance();
        ClientPlayerEntity player = client.player;
        Optional<NbtElement> instanceNbt = World.CODEC.encodeStart(NbtOps.INSTANCE, player.getWorld().getRegistryKey())
            .resultOrPartial(CompassFinder.LOGGER::error);

        if (drawContext == null || player == null || instanceNbt.isEmpty()) {
            return;
        }

        PlayerInventory inventory = player.getInventory();
        for (int slot = 0; slot < PlayerInventory.getHotbarSize(); slot++) {
            ItemStack stack = inventory.getStack(slot);
            NbtCompound nbt = stack.getNbt();

            if (
                nbt == null
                || !nbt.contains(CompassFinder.MODDED_COMPASS_ORE_KEY)
                || !nbt.contains(CompassItem.LODESTONE_POS_KEY)
                || !nbt.getString(CompassItem.LODESTONE_DIMENSION_KEY).equals(instanceNbt.get().asString())
            ) {
                continue;
            }

            Integer distance = (int) NbtHelper.toBlockPos(nbt.getCompound(CompassItem.LODESTONE_POS_KEY)).getSquaredDistance(player.getPos());

            drawCustomTextOnHotbar(drawContext, stack);
            stack.setCount(distance);
        }
    }

    public static void updateCompasses() {
        MinecraftClient client = MinecraftClient.getInstance();
        ClientPlayerEntity player = client.player;
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

    private static void drawCustomTextOnHotbar(DrawContext drawContext, ItemStack itemStack) {
        MinecraftClient client = MinecraftClient.getInstance();
        TextRenderer textRenderer = client.textRenderer;

        int screenWidth = client.getWindow().getScaledWidth();
        int screenHeight = client.getWindow().getScaledHeight();

        int x = screenWidth / 2 - 91;
        int y = screenHeight - 22;

        drawContext.drawText(textRenderer, Text.of("99"), x, y, y, false);
    }
}
