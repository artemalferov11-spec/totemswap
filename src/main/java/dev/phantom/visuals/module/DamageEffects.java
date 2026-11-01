package dev.phantom.visuals.module;

import dev.phantom.visuals.render.RenderCtx;
import dev.phantom.visuals.render.RenderUtil;
import dev.phantom.visuals.setting.BoolSetting;
import dev.phantom.visuals.setting.ModeSetting;
import dev.phantom.visuals.setting.NumberSetting;
import dev.phantom.visuals.util.ParticleStyles;
import net.minecraft.client.MinecraftClient;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.decoration.ArmorStandEntity;
import net.minecraft.particle.ParticleEffect;
import net.minecraft.text.Text;
import net.minecraft.util.math.Vec3d;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.concurrent.ThreadLocalRandom;

public class DamageEffects extends Feature {
    private final ModeSetting style = add(new ModeSetting("Style", "Crit", ParticleStyles.NAMES));
    private final NumberSetting amount = add(new NumberSetting("Particles", 4, 80, 2, 24));
    private final BoolSetting numbers = add(new BoolSetting("Damage numbers", true));
    private final NumberSetting numberSize = add(new NumberSetting("Number size", 0.5, 2.5, 0.1, 1.0));

    private Map<Integer, Float> lastHealth = new HashMap<>();
    private final List<Floater> floaters = new ArrayList<>();

    private static final class Floater {
        final Vec3d pos;
        final String text;
        int age;

        Floater(Vec3d pos, String text) {
            this.pos = pos;
            this.text = text;
        }
    }

    public DamageEffects() {
        super("Damage Particles", "Particle burst and floating numbers on damage", Category.PARTICLES, false);
    }

    @Override
    public void onDisable() {
        lastHealth.clear();
        floaters.clear();
    }

    @Override
    public void onTick(MinecraftClient mc) {
        if (mc.world == null || mc.player == null) return;

        Iterator<Floater> it = floaters.iterator();
        while (it.hasNext()) {
            Floater f = it.next();
            if (++f.age > 30) it.remove();
        }

        ParticleEffect effect = ParticleStyles.get(style.get());
        ThreadLocalRandom r = ThreadLocalRandom.current();
        Map<Integer, Float> next = new HashMap<>();

        for (Entity e : mc.world.getEntities()) {
            if (!(e instanceof LivingEntity le) || e instanceof ArmorStandEntity) continue;
            float hp = le.getHealth();
            Float prev = lastHealth.get(le.getId());
            next.put(le.getId(), hp);
            if (prev == null || hp >= prev - 0.01f) continue;
            if (le.squaredDistanceTo(mc.player) > 64 * 64) continue;

            float dmg = prev - hp;
            Vec3d c = le.getPos().add(0, le.getHeight() * 0.6, 0);
            for (int i = 0; i < amount.getI(); i++) {
                double vx = r.nextDouble() * 2 - 1;
                double vy = r.nextDouble() * 2 - 0.6;
                double vz = r.nextDouble() * 2 - 1;
                mc.world.addParticle(effect, c.x, c.y, c.z, vx * 0.25, vy * 0.25 + 0.05, vz * 0.25);
            }
            if (numbers.get()) {
                floaters.add(new Floater(c.add((r.nextDouble() - 0.5) * 0.5, 0.3, (r.nextDouble() - 0.5) * 0.5),
                        String.format(Locale.ROOT, "-%.1f", dmg)));
            }
        }
        lastHealth = next;
    }

    @Override
    public void onWorldRender(RenderCtx rc) {
        for (Floater f : floaters) {
            float t = (f.age + rc.delta) / 30f;
            if (t >= 1f) continue;
            int alpha = Math.max(8, (int) (255 * (1f - t * t)));
            int color = (alpha << 24) | 0xFF5555;
            Vec3d p = f.pos.add(0, t * 1.1, 0);
            double dist = rc.cam.distanceTo(p);
            float s = (float) Math.max(0.03, dist * 0.0035) * numberSize.getF();
            RenderUtil.text(rc, Text.literal(f.text), p, s, color, 0);
        }
    }
}
