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
import net.minecraft.util.math.BlockPos;

public class CompassFinder implements ModInitializer {
	public static final String MOD_ID = "gf-compass-finder";
    public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

	@Deprecated
	public static final String MODDED_COMPASS_ORE_KEY = "CompassFinderModdedOre"; // Use CompassFinderComponentTypes.ORE_TYPE instead

	public static final int SEARCH_RADIUS = 20;

	@Override
	public void onInitialize() {
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
}