package dev.phantom.visuals.module;

import dev.phantom.visuals.render.RenderCtx;
import dev.phantom.visuals.render.RenderUtil;
import dev.phantom.visuals.setting.NumberSetting;
import dev.phantom.visuals.util.Theme;
import java.util.ArrayList;
import java.util.List;
import net.minecraft.class_243;
import net.minecraft.class_310;
import net.minecraft.class_4588;
import net.minecraft.class_746;

/** Расходящиеся круги под ногами в момент прыжка. */
public class JumpCircles extends Feature {
    private final NumberSetting radius = add(new NumberSetting("Radius", 0.5, 4.0, 0.1, 1.6));
    private final NumberSetting duration = add(new NumberSetting("Duration (ms)", 300, 3000, 100, 900));

    private static final class Circle {
        final double x, y, z;
        final long start;
        Circle(double x, double y, double z, long start) {
            this.x = x; this.y = y; this.z = z; this.start = start;
        }
    }

    private final List<Circle> circles = new ArrayList<>();
    private boolean wasOnGround;

    public JumpCircles() {
        super("JumpCircles", "Expanding circles under your feet when you jump", Category.VISUALS, false);
    }

    @Override
    public void onDisable() {
        circles.clear();
    }

    @Override
    public void onTick(class_310 mc) {
        class_746 p = mc.field_1724;
        if (p == null) return;
        boolean ground = p.method_24828();
        if (wasOnGround && !ground && p.method_18798().field_1351 > 0.0) {
            circles.add(new Circle(p.method_23317(), p.method_23318() + 0.02, p.method_23321(), System.currentTimeMillis()));
        }
        wasOnGround = ground;
    }

    private void ring(RenderCtx rc, class_4588 vc, Circle c, double r, int color) {
        int seg = 48;
        class_243 prev = null;
        for (int i = 0; i <= seg; i++) {
            double a = i * (Math.PI * 2.0 / seg);
            class_243 cur = new class_243(c.x + Math.cos(a) * r, c.y, c.z + Math.sin(a) * r);
            if (prev != null) RenderUtil.line(rc, vc, prev, cur, color);
            prev = cur;
        }
    }

    @Override
    public void onWorldRender(RenderCtx rc) {
        if (circles.isEmpty()) return;
        long now = System.currentTimeMillis();
        long dur = (long) duration.get();
        class_4588 vc = RenderUtil.lines(rc);
        circles.removeIf(c -> now - c.start > dur);
        for (Circle c : circles) {
            float t = (now - c.start) / (float) dur;           // 0..1
            float ease = 1f - (1f - t) * (1f - t);              // быстро расходится, потом замедляется
            int alpha = (int) (255 * (1f - t));
            double r = radius.get() * ease;
            ring(rc, vc, c, r, Theme.withAlpha(Theme.accent(0f), alpha));
            ring(rc, vc, c, r * 0.72, Theme.withAlpha(Theme.accent(0.25f), alpha));
        }
    }
}
