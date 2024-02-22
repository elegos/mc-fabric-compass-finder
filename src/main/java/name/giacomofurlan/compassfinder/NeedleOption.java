package name.giacomofurlan.compassfinder;

import java.util.ArrayList;

import net.minecraft.block.Block;
import net.minecraft.block.Blocks;

public enum NeedleOption {
    SPAWN_POINT("Spawn point", null),
    ORE_COAL("Coal", new ArrayList<>(){{ add(Blocks.COAL_ORE); add(Blocks.DEEPSLATE_COAL_ORE); }}, "block.minecraft.coal_ore"),
    ORE_COPPER("Copper", new ArrayList<>(){{ add(Blocks.COPPER_ORE); add(Blocks.DEEPSLATE_COPPER_ORE); }}, "block.minecraft.copper_ore"),
    ORE_IRON("Iron", new ArrayList<>(){{ add(Blocks.IRON_ORE); add(Blocks.DEEPSLATE_IRON_ORE); }}, "block.minecraft.iron_ore"),
    ORE_GOLD("Gold", new ArrayList<>(){{ add(Blocks.GOLD_ORE); add(Blocks.DEEPSLATE_GOLD_ORE); add(Blocks.NETHER_GOLD_ORE); }}, "block.minecraft.gold_ore"),
    ORE_LAPIS_LAZULI("Lapis Lazuli", new ArrayList<>(){{ add(Blocks.DEEPSLATE_LAPIS_ORE); add(Blocks.LAPIS_ORE); }}, "block.minecraft.lapis_ore"),
    ORE_REDSTONE("Redstone", new ArrayList<>(){{ add(Blocks.REDSTONE_ORE); add(Blocks.DEEPSLATE_REDSTONE_ORE); }}, "block.minecraft.redstone_ore"),
    ORE_DIAMOND("Diamond", new ArrayList<>(){{ add(Blocks.DIAMOND_ORE); add(Blocks.DEEPSLATE_DIAMOND_ORE); }}, "block.minecraft.diamond_ore"),
    ORE_EMERALD("Emerald", new ArrayList<>(){{ add(Blocks.EMERALD_ORE); add(Blocks.DEEPSLATE_EMERALD_ORE); }}, "block.minecraft.emerald_ore"),
    ORE_NETHER_QUARTZ("Nether Quartz", new ArrayList<>(){{ add(Blocks.NETHER_QUARTZ_ORE); }}, "block.minecraft.nether_quartz_ore"),
    ORE_ANCIENT_DEBRIS("Ancient Debris", new ArrayList<>(){{ add(Blocks.ANCIENT_DEBRIS); }}, "block.minecraft.ancient_debris");

    public final String label;
    public final ArrayList<Block> blocks;
    public final String translationKey;

    public static NeedleOption fromTranslationKey(String key) {
        for (NeedleOption option : NeedleOption.values()) {
            if (option.translationKey == null) {
                continue;
            }

            if (option.translationKey.equals(key)) {
                return option;
            }
        }

        return null;
    }

    private NeedleOption(String label, ArrayList<Block> blocks) {
        this.label = label;
        this.blocks = blocks;
        this.translationKey = null;
    }

    private NeedleOption(String label, ArrayList<Block> blocks, String translationKey) {
        this.label = label;
        this.blocks = blocks;
        this.translationKey = translationKey;
    }
}
