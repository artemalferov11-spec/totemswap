package dev.phantom.visuals.module;

import dev.phantom.visuals.setting.BoolSetting;
import net.minecraft.client.MinecraftClient;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.decoration.ArmorStandEntity;
import net.minecraft.entity.player.PlayerEntity;

public class Invisibles extends Feature {
    private final BoolSetting players = add(new BoolSetting("Players", true));
    private final BoolSetting mobs = add(new BoolSetting("Mobs", true));

    public Invisibles() {
        super("Invisibles", "Outline for invisible players and mobs", Category.VISUALS, false);
    }

    /** Используется миксином Entity#isGlowing. */
    public boolean matches(Entity e) {
        if (!isEnabled()) return false;
        if (!(e instanceof LivingEntity) || e instanceof ArmorStandEntity) return false;
        if (e == MinecraftClient.getInstance().player) return false;
        if (!e.isInvisible()) return false;
        return e instanceof PlayerEntity ? players.get() : mobs.get();
    }
}
