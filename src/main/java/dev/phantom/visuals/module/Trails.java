package dev.phantom.visuals.module;

import dev.phantom.visuals.setting.BoolSetting;
import dev.phantom.visuals.setting.ModeSetting;
import dev.phantom.visuals.setting.NumberSetting;
import dev.phantom.visuals.util.ParticleStyles;
import net.minecraft.client.MinecraftClient;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.projectile.ProjectileEntity;
import net.minecraft.particle.ParticleEffect;

import java.util.concurrent.ThreadLocalRandom;

public class Trails extends Feature {
    private final ModeSetting style = add(new ModeSetting("Style", "End Rod", ParticleStyles.NAMES));
    private final BoolSetting self = add(new BoolSetting("Self", true));
    private final BoolSetting others = add(new BoolSetting("Other players", false));
    private final BoolSetting projectiles = add(new BoolSetting("Projectiles", true));
    private final NumberSetting density = add(new NumberSetting("Density", 1, 8, 1, 2));

    public Trails() {
        super("Trails", "Particle trails behind players and projectiles", Category.PARTICLES, false);
    }

    @Override
    public void onTick(MinecraftClient mc) {
        if (mc.world == null || mc.player == null || mc.isPaused()) return;
        ParticleEffect effect = ParticleStyles.get(style.get());
        ThreadLocalRandom r = ThreadLocalRandom.current();

        for (Entity e : mc.world.getEntities()) {
            double dx = e.getX() - e.prevX;
            double dy = e.getY() - e.prevY;
            double dz = e.getZ() - e.prevZ;
            double moved = dx * dx + dy * dy + dz * dz;
            if (moved < 0.0004) continue;

            double yOff;
            if (e == mc.player) {
                if (!self.get()) continue;
                yOff = 0.1;
            } else if (e instanceof PlayerEntity) {
                if (!others.get()) continue;
                yOff = 0.1;
            } else if (e instanceof ProjectileEntity) {
                if (!projectiles.get()) continue;
                yOff = 0.0;
            } else {
                continue;
            }

            for (int i = 0; i < density.getI(); i++) {
                mc.world.addParticle(effect,
                        e.getX() + (r.nextDouble() - 0.5) * 0.25,
                        e.getY() + yOff + r.nextDouble() * 0.15,
                        e.getZ() + (r.nextDouble() - 0.5) * 0.25,
                        (r.nextDouble() - 0.5) * 0.02,
                        r.nextDouble() * 0.02,
                        (r.nextDouble() - 0.5) * 0.02);
            }
        }
    }
}
