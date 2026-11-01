package dev.phantom.visuals.module;

import dev.phantom.visuals.render.RenderCtx;
import dev.phantom.visuals.render.RenderUtil;
import dev.phantom.visuals.setting.BoolSetting;
import dev.phantom.visuals.setting.NumberSetting;
import net.minecraft.client.MinecraftClient;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.decoration.ArmorStandEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;
import net.minecraft.util.math.Vec3d;

import java.util.Locale;

public class Nametags extends Feature {
    private final NumberSetting scale = add(new NumberSetting("Scale", 0.5, 3.0, 0.1, 1.0));
    private final BoolSetting players = add(new BoolSetting("Players", true));
    private final BoolSetting mobs = add(new BoolSetting("Mobs", true));
    private final BoolSetting health = add(new BoolSetting("Health", true));
    private final BoolSetting distance = add(new BoolSetting("Distance", true));
    private final BoolSetting background = add(new BoolSetting("Background", true));

    public Nametags() {
        super("Nametags", "Nametags through walls with health and distance", Category.VISUALS, false);
    }

    /** Рисуем ли мы свой тег для этого существа (и прячем ли ванильный). */
    public boolean shouldRender(Entity e) {
        if (!isEnabled()) return false;
        if (!(e instanceof LivingEntity) || e instanceof ArmorStandEntity) return false;
        MinecraftClient mc = MinecraftClient.getInstance();
        if (e == mc.player) return !mc.options.getPerspective().isFirstPerson();
        return e instanceof PlayerEntity ? players.get() : mobs.get();
    }

    @Override
    public void onWorldRender(RenderCtx rc) {
        for (Entity e : rc.mc.world.getEntities()) {
            if (!shouldRender(e)) continue;
            LivingEntity le = (LivingEntity) e;
            Vec3d p = e.getLerpedPos(rc.delta);
            double dist = rc.cam.distanceTo(p);
            if (dist > 128) continue;

            MutableText t = Text.empty().append(le.getDisplayName());
            if (health.get()) {
                float hp = le.getHealth();
                float ratio = hp / Math.max(1f, le.getMaxHealth());
                int col = ratio > 0.66f ? 0x55FF55 : ratio > 0.33f ? 0xFFFF55 : 0xFF5555;
                t.append(Text.literal(String.format(Locale.ROOT, " %.1f", hp)).styled(s -> s.withColor(col)));
            }
            if (distance.get()) {
                t.append(Text.literal(" " + (int) dist + "m").styled(s -> s.withColor(0xAAAAAA)));
            }

            float s = (float) Math.max(0.025, dist * 0.0028) * scale.getF();
            Vec3d pos = p.add(0, e.getHeight() + 0.55 + s * 4, 0);
            RenderUtil.text(rc, t, pos, s, 0xFFFFFFFF, background.get() ? 0x80000000 : 0);
        }
    }
}
