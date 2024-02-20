package name.giacomofurlan.compassfinder;

import java.util.ArrayList;

import net.minecraft.block.Block;
import net.minecraft.block.Blocks;

public enum NeedleOption {
    SPAWN_POINT("Spawn point", null),
    ORE_COAL("Coal", new ArrayList<>(){{ add(Blocks.COAL_ORE); add(Blocks.DEEPSLATE_COAL_ORE); }}),
    ORE_COPPER("Copper", new ArrayList<>(){{ add(Blocks.COPPER_ORE); add(Blocks.DEEPSLATE_COPPER_ORE); }}),
    ORE_IRON("Iron", new ArrayList<>(){{ add(Blocks.IRON_ORE); add(Blocks.DEEPSLATE_IRON_ORE); }}),
    ORE_GOLD("Gold", new ArrayList<>(){{ add(Blocks.GOLD_ORE); add(Blocks.DEEPSLATE_GOLD_ORE); add(Blocks.NETHER_GOLD_ORE); }}),
    ORE_LAPIS_LAZULI("Lapis Lazuli", new ArrayList<>(){{ add(Blocks.DEEPSLATE_LAPIS_ORE); add(Blocks.LAPIS_ORE); }}),
    ORE_REDSTONE("Redstone", new ArrayList<>(){{ add(Blocks.REDSTONE_ORE); add(Blocks.DEEPSLATE_REDSTONE_ORE); }}),
    ORE_DIAMOND("Diamond", new ArrayList<>(){{ add(Blocks.DIAMOND_ORE); add(Blocks.DEEPSLATE_DIAMOND_ORE); }}),
    ORE_EMERALD("Emerald", new ArrayList<>(){{ add(Blocks.EMERALD_ORE); add(Blocks.DEEPSLATE_EMERALD_ORE); }}),
    ORE_NETHER_QUARTZ("Nether Quartz", new ArrayList<>(){{ add(Blocks.NETHER_QUARTZ_ORE); }}),
    ORE_ANCIENT_DEBRIS("Ancient Debris", new ArrayList<>(){{ add(Blocks.ANCIENT_DEBRIS); }});

    public final String label;
    public final ArrayList<Block> blocks;

    private NeedleOption(String label, ArrayList<Block> blocks) {
        this.label = label;
        this.blocks = blocks;
    }
}
