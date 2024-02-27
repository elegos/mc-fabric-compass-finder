package name.giacomofurlan.compassfinder.commands;

import static net.fabricmc.fabric.api.client.command.v2.ClientCommandManager.argument;
import static net.fabricmc.fabric.api.client.command.v2.ClientCommandManager.literal;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;

import name.giacomofurlan.compassfinder.NeedleOption;
import name.giacomofurlan.compassfinder.services.CompassManager;
import net.fabricmc.fabric.api.client.command.v2.FabricClientCommandSource;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.text.Text;
import net.minecraft.util.math.BlockPos;

public class LodestoneRegisterCommand {
    public static void register(CommandDispatcher<FabricClientCommandSource> dispatcher) {
        dispatcher.register(
            literal("compassfinder")
                .then(literal("lodestone")
                    .then(argument("x", IntegerArgumentType.integer())
                    .then(argument("z", IntegerArgumentType.integer())
                    .executes(LodestoneRegisterCommand::exec)
                )))
        );

        // alias
        dispatcher.register(
            literal("compassfinder")
                .then(literal("ls")
                    .then(argument("x", IntegerArgumentType.integer())
                    .then(argument("z", IntegerArgumentType.integer())
                    .executes(LodestoneRegisterCommand::exec)
                )))
        );
    }

    static public int exec(CommandContext<FabricClientCommandSource> context) throws CommandSyntaxException {
        MinecraftClient client = MinecraftClient.getInstance();
        ClientPlayerEntity player = client.player;
        if (player == null) {
            return 0;
        }

        ItemStack currentStack = player.getInventory().getMainHandStack();
        Item compass = currentStack.getItem();

        if (!compass.getTranslationKey().equals(CompassManager.COMPASS_TR_KEY)) {
            return 0;
        }

        Integer x = IntegerArgumentType.getInteger(context, "x");
        Integer z = IntegerArgumentType.getInteger(context, "z");

        CompassManager.setCompassOre(currentStack, NeedleOption.LODESTONE_MODE);
        CompassManager.updateCompassPos(player, NeedleOption.LODESTONE_MODE, BlockPos.ofFloored(x, 0f, z), currentStack);

        context.getSource().sendFeedback(Text.literal("Compass pointing to [%s, %s]".formatted(x, z)));

        return 1;
    }
}
