package name.giacomofurlan.compassfinder;

import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import net.fabricmc.api.ModInitializer;
import net.minecraft.block.Block;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.item.CompassItem;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtElement;
import net.minecraft.nbt.NbtHelper;
import net.minecraft.nbt.NbtOps;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class CompassFinder implements ModInitializer {
	public static final String MOD_ID = "gf-compass-finder";
    public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);
	public static final String MODDED_COMPASS_KEY = "CompassFinderModdedCompass";

	public static final int SEARCH_RADIUS = 20;

	private static Boolean ctrlState;
	private static NeedleOption needleOption;

	private static BlockPos needlePointingPos;

	@Override
	public void onInitialize() {
		ctrlState = false;
		needleOption = NeedleOption.SPAWN_POINT;
	}

	public static BlockPos getNearestBlockPos() {
		BlockPos nearestPos = getNearestBlockPos(needleOption.blocks);
		CompassFinder.LOGGER.info("[" + needleOption.label + "] found pos: " + (nearestPos == null ? "NULL" : nearestPos.toString()));

		return nearestPos;
	}

	public static BlockPos getNearestBlockPos(List<Block> blockTypes) {
		if (blockTypes == null || blockTypes.isEmpty()) {
			return null;
		}

		MinecraftClient client = MinecraftClient.getInstance();
		ClientPlayerEntity player = client.player;
		ClientWorld world = client.world;

		double pX = Math.floor(player.getX());
		double pY = Math.floor(player.getY());
		double pZ = Math.floor(player.getZ());

		BlockPos pos = null;

		double x, y, z;
		for (int radius = 1; radius < SEARCH_RADIUS && pos == null; radius++) {
			for (x = pX - radius; x <= pX + radius && pos == null; x++) {
				for (z = pZ - radius; z <= pZ + radius && pos == null; z++) {
					Iterator<Double> yVals = Arrays.asList(pY, pY - 1, pY + 1, pY -2, pY + 2, pY + 3).iterator();
					while (yVals.hasNext()) {
						y = yVals.next();
						BlockPos candidate = BlockPos.ofFloored(x, y, z);
						if (blockTypes.contains(world.getBlockState(candidate).getBlock())) {
							pos = candidate;
						}
					}

				}
			}
		}

		return pos;
	}

	public static void searchAndSetCompassNbt(ItemStack stack) {
		if (needleOption == NeedleOption.SPAWN_POINT) {
			return;
		}

		BlockPos nearestPos = getNearestBlockPos(needleOption.blocks);
		LOGGER.info("[" + needleOption.label + "] found pos: " + (nearestPos == null ? "NULL" : nearestPos.toString()));

		setCompassNbt(stack, getNearestBlockPos(needleOption.blocks));
	}

	public static void setCompassNbt(ItemStack stack, BlockPos nearestPos) {
		if (needleOption == NeedleOption.SPAWN_POINT) {
			return;
		}
		
		MinecraftClient client = MinecraftClient.getInstance();
		ClientWorld world = client.world;

		needlePointingPos = nearestPos;

		if (stack.getNbt() == null) {
			stack.setNbt(new NbtCompound());
		}
		NbtCompound nbt = stack.getNbt();
		nbt.putBoolean(MODDED_COMPASS_KEY, true);

		if (nearestPos == null) {
			nbt.putBoolean(CompassItem.LODESTONE_TRACKED_KEY, false);
			nbt.remove(CompassItem.LODESTONE_POS_KEY);
			nbt.remove(CompassItem.LODESTONE_DIMENSION_KEY);
		} else {
			nbt.put(CompassItem.LODESTONE_POS_KEY, NbtHelper.fromBlockPos(nearestPos));
			nbt.putBoolean(CompassItem.LODESTONE_TRACKED_KEY, true);
			World.CODEC.encodeStart(NbtOps.INSTANCE, world.getRegistryKey())
				.resultOrPartial(LOGGER::error)
				.ifPresent(nbtElement -> nbt.put(CompassItem.LODESTONE_DIMENSION_KEY, (NbtElement)nbtElement)
			);
		}
	}

	public static Boolean isCtrlDown() {
		return ctrlState;
	}

	public static void setCtrlState(Boolean ctrlState) {
		CompassFinder.ctrlState = ctrlState;
	}

	public static NeedleOption getNeedleOption() {
		return needleOption;
	}

	public static void setNeedleOption(NeedleOption needleOption) {
		CompassFinder.needleOption = needleOption;
	}

	public static BlockPos getNeedlePointingPos() {
		return needlePointingPos;
	}
}