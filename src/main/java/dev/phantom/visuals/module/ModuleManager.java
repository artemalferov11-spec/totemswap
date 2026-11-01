package dev.phantom.visuals.module;

import java.util.ArrayList;
import java.util.List;

public final class ModuleManager {
    private ModuleManager() {}

    public static final Nametags NAMETAGS = new Nametags();
    public static final Predictions PREDICTIONS = new Predictions();
    public static final ItemPhysic ITEM_PHYSIC = new ItemPhysic();
    public static final Invisibles INVISIBLES = new Invisibles();
    public static final WorldParticles WORLD_PARTICLES = new WorldParticles();
    public static final Trails TRAILS = new Trails();
    public static final DamageEffects DAMAGE = new DamageEffects();
    public static final TotemSwap TOTEM_SWAP = new TotemSwap();
    public static final AutoSprint AUTO_SPRINT = new AutoSprint();
    public static final ThemeFeature THEME = new ThemeFeature();

    public static final List<Feature> ALL = List.of(
            NAMETAGS, PREDICTIONS, ITEM_PHYSIC, INVISIBLES,
            WORLD_PARTICLES, TRAILS, DAMAGE,
            TOTEM_SWAP, AUTO_SPRINT,
            THEME
    );

    static {
        // Включено по умолчанию
        NAMETAGS.setEnabled(true);
        PREDICTIONS.setEnabled(true);
        ITEM_PHYSIC.setEnabled(true);
        DAMAGE.setEnabled(true);
        TOTEM_SWAP.setEnabled(true);
    }

    public static List<Feature> of(Category category) {
        List<Feature> list = new ArrayList<>();
        for (Feature f : ALL) {
            if (f.category == category) list.add(f);
        }
        return list;
    }
}
