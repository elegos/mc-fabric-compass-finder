package name.giacomofurlan.compassfinder.components;

import java.util.function.UnaryOperator;

import com.mojang.serialization.Codec;

import name.giacomofurlan.compassfinder.CompassFinder;

import net.minecraft.component.ComponentType;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.util.Identifier;

public class CompassFinderComponentTypes {
    public static final ComponentType<String> ORE_TYPE = register("ore_type", builder -> builder.codec(Codec.STRING));

    private static <T>ComponentType<T> register(String name, UnaryOperator<ComponentType.Builder<T>> buildOperator) {
        return Registry.register(
            Registries.DATA_COMPONENT_TYPE,
            Identifier.of(CompassFinder.MOD_ID, name),
            buildOperator.apply(ComponentType.<T>builder()).build()
        );
    }

    public static void registerDataComponentTypes() {
        CompassFinder.LOGGER.info("Registering data component types for " + CompassFinder.MOD_ID);
    }
}
