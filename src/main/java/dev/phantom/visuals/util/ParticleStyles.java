package dev.phantom.visuals.util;

import net.minecraft.particle.ParticleEffect;
import net.minecraft.particle.ParticleTypes;

public final class ParticleStyles {
    private ParticleStyles() {}

    public static final String[] NAMES = {
            "Flame", "Soul Fire", "End Rod", "Snowflake", "Heart", "Enchant",
            "Electric", "Glow", "Firework", "Crit", "Dragon", "Totem", "Note"
    };

    public static ParticleEffect get(String name) {
        return switch (name) {
            case "Flame" -> ParticleTypes.FLAME;
            case "Soul Fire" -> ParticleTypes.SOUL_FIRE_FLAME;
            case "Snowflake" -> ParticleTypes.SNOWFLAKE;
            case "Heart" -> ParticleTypes.HEART;
            case "Enchant" -> ParticleTypes.ENCHANT;
            case "Electric" -> ParticleTypes.ELECTRIC_SPARK;
            case "Glow" -> ParticleTypes.GLOW;
            case "Firework" -> ParticleTypes.FIREWORK;
            case "Crit" -> ParticleTypes.CRIT;
            case "Dragon" -> ParticleTypes.DRAGON_BREATH;
            case "Totem" -> ParticleTypes.TOTEM_OF_UNDYING;
            case "Note" -> ParticleTypes.NOTE;
            default -> ParticleTypes.END_ROD;
        };
    }
}
