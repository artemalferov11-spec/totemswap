package dev.phantom.visuals.module;

import dev.phantom.visuals.setting.BoolSetting;
import dev.phantom.visuals.setting.ModeSetting;
import dev.phantom.visuals.setting.NumberSetting;
import dev.phantom.visuals.util.ParticleStyles;
import java.util.concurrent.ThreadLocalRandom;
import net.minecraft.class_1297;
import net.minecraft.class_1657;
import net.minecraft.class_1676;
import net.minecraft.class_2394;
import net.minecraft.class_310;

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
    public void onTick(class_310 mc) {
        if (mc.field_1687 == null || mc.field_1724 == null || mc.method_1493()) return;
        class_2394 effect = ParticleStyles.get(style.get());
        ThreadLocalRandom r = ThreadLocalRandom.current();

        for (class_1297 e : mc.field_1687.method_18112()) {
            double dx = e.method_23317() - e.field_6014;
            double dy = e.method_23318() - e.field_6036;
            double dz = e.method_23321() - e.field_5969;
            double moved = dx * dx + dy * dy + dz * dz;
            if (moved < 0.0004) continue;

            double yOff;
            if (e == mc.field_1724) {
                if (!self.get()) continue;
                yOff = 0.1;
            } else if (e instanceof class_1657) {
                if (!others.get()) continue;
                yOff = 0.1;
            } else if (e instanceof class_1676) {
                if (!projectiles.get()) continue;
                yOff = 0.0;
            } else {
                continue;
            }

            for (int i = 0; i < density.getI(); i++) {
                mc.field_1687.method_8406(effect,
                        e.method_23317() + (r.nextDouble() - 0.5) * 0.25,
                        e.method_23318() + yOff + r.nextDouble() * 0.15,
                        e.method_23321() + (r.nextDouble() - 0.5) * 0.25,
                        (r.nextDouble() - 0.5) * 0.02,
                        r.nextDouble() * 0.02,
                        (r.nextDouble() - 0.5) * 0.02);
            }
        }
    }
}
