package dev.phantom.visuals.module;

import dev.phantom.visuals.setting.ModeSetting;
import dev.phantom.visuals.setting.NumberSetting;
import dev.phantom.visuals.util.ParticleStyles;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.particle.ParticleEffect;

import java.util.concurrent.ThreadLocalRandom;

public class WorldParticles extends Feature {
    private final ModeSetting style = add(new ModeSetting("Style", "Snowflake", ParticleStyles.NAMES));
    private final ModeSetting motion = add(new ModeSetting("Motion", "Falling", "Falling", "Rising", "Drifting"));
    private final NumberSetting amount = add(new NumberSetting("Amount", 1, 30, 1, 6));
    private final NumberSetting radius = add(new NumberSetting("Radius", 3, 32, 1, 14));

    public WorldParticles() {
        super("World Particles", "Custom ambient particles around you", Category.PARTICLES, false);
    }

    @Override
    public void onTick(MinecraftClient mc) {
        ClientPlayerEntity p = mc.player;
        if (p == null || mc.world == null || mc.isPaused()) return;
        ThreadLocalRandom r = ThreadLocalRandom.current();
        ParticleEffect effect = ParticleStyles.get(style.get());
        double rad = radius.get();

        for (int i = 0; i < amount.getI(); i++) {
            double x = p.getX() + (r.nextDouble() * 2 - 1) * rad;
            double z = p.getZ() + (r.nextDouble() * 2 - 1) * rad;
            double y = p.getEyeY() + (r.nextDouble() * 2 - 0.6) * rad * 0.5;
            double vx = (r.nextDouble() - 0.5) * 0.02;
            double vz = (r.nextDouble() - 0.5) * 0.02;
            double vy;
            switch (motion.get()) {
                case "Rising":
                    vy = 0.02 + r.nextDouble() * 0.03;
                    break;
                case "Drifting":
                    vy = (r.nextDouble() - 0.5) * 0.02;
                    break;
                default:
                    vy = -0.03 - r.nextDouble() * 0.03;
            }
            mc.world.addParticle(effect, x, y, z, vx, vy, vz);
        }
    }
}
