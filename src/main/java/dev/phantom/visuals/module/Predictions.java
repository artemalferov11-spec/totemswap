package dev.phantom.visuals.module;

import dev.phantom.visuals.render.RenderCtx;
import dev.phantom.visuals.render.RenderUtil;
import dev.phantom.visuals.setting.BoolSetting;
import dev.phantom.visuals.setting.ModeSetting;
import dev.phantom.visuals.setting.NumberSetting;
import dev.phantom.visuals.util.Theme;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.CrossbowItem;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.RaycastContext;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class Predictions extends Feature {
    private final BoolSetting landing = add(new BoolSetting("Landing marker", true));
    private final BoolSetting hits = add(new BoolSetting("Highlight hits", true));
    private final NumberSetting steps = add(new NumberSetting("Max steps", 40, 300, 10, 160));
    private final ModeSetting color = add(new ModeSetting("Color", "Accent", "Accent", "Rainbow", "White"));

    public Predictions() {
        super("Predictions", "Visual trajectory for projectiles", Category.VISUALS, false);
    }

    /** {скорость, гравитация, добавка к pitch} или null, если ничего не метаем. */
    private double[] physics(ClientPlayerEntity p) {
        if (p.isUsingItem()) {
            ItemStack a = p.getActiveItem();
            if (a.isOf(Items.BOW)) {
                float f = (72000 - p.getItemUseTimeLeft()) / 20f;
                f = (f * f + f * 2f) / 3f;
                if (f < 0.1f) return null;
                if (f > 1f) f = 1f;
                return new double[]{f * 3.0, 0.05, 0};
            }
            if (a.isOf(Items.TRIDENT)) return new double[]{2.5, 0.05, 0};
        }
        ItemStack[] hands = {p.getMainHandStack(), p.getOffHandStack()};
        for (ItemStack s : hands) {
            if (s.isOf(Items.CROSSBOW) && CrossbowItem.isCharged(s)) return new double[]{3.15, 0.05, 0};
            if (s.isOf(Items.ENDER_PEARL) || s.isOf(Items.SNOWBALL) || s.isOf(Items.EGG)) return new double[]{1.5, 0.03, 0};
            if (s.isOf(Items.SPLASH_POTION) || s.isOf(Items.LINGERING_POTION)) return new double[]{0.5, 0.05, -20};
            if (s.isOf(Items.EXPERIENCE_BOTTLE)) return new double[]{0.7, 0.07, -20};
        }
        return null;
    }

    private int col(float t) {
        switch (color.get()) {
            case "Rainbow":
                return 0xFF000000 | MathHelper.hsvToRgb((t * 0.8f + (System.currentTimeMillis() % 4000L) / 4000f) % 1f, 0.7f, 1f);
            case "White":
                return 0xFFFFFFFF;
            default:
                return Theme.accent(t * 0.5f);
        }
    }

    @Override
    public void onWorldRender(RenderCtx rc) {
        ClientPlayerEntity p = rc.mc.player;
        ClientWorld world = rc.mc.world;
        double[] ph = physics(p);
        if (ph == null) return;

        double speed = ph[0], gravity = ph[1], roll = ph[2];
        double rad = Math.PI / 180.0;
        float yaw = p.getYaw(rc.delta), pitch = p.getPitch(rc.delta);
        double dx = -Math.sin(yaw * rad) * Math.cos(pitch * rad);
        double dy = -Math.sin((pitch + roll) * rad);
        double dz = Math.cos(yaw * rad) * Math.cos(pitch * rad);
        Vec3d vel = new Vec3d(dx, dy, dz).normalize().multiply(speed);
        Vec3d pv = p.getVelocity();
        vel = vel.add(pv.x, p.isOnGround() ? 0 : pv.y, pv.z);
        Vec3d pos = p.getCameraPosVec(rc.delta).subtract(0, 0.1, 0);

        List<Entity> candidates = new ArrayList<>();
        if (hits.get()) {
            candidates = world.getOtherEntities(p, new Box(pos, pos).expand(110),
                    e -> e instanceof LivingEntity && e.isAlive() && !e.isSpectator());
        }

        List<Vec3d> pts = new ArrayList<>();
        pts.add(pos);
        Vec3d hitPos = null;
        Entity hitEntity = null;

        for (int i = 0; i < steps.getI(); i++) {
            Vec3d next = pos.add(vel);
            BlockHitResult br = world.raycast(new RaycastContext(pos, next,
                    RaycastContext.ShapeType.COLLIDER, RaycastContext.FluidHandling.NONE, p));
            boolean blockHit = br.getType() != HitResult.Type.MISS;
            Vec3d end = blockHit ? br.getPos() : next;

            Entity he = null;
            Vec3d hp = null;
            double best = Double.MAX_VALUE;
            for (Entity e : candidates) {
                Optional<Vec3d> o = e.getBoundingBox().expand(0.3).raycast(pos, end);
                if (o.isPresent()) {
                    double d = pos.squaredDistanceTo(o.get());
                    if (d < best) {
                        best = d;
                        he = e;
                        hp = o.get();
                    }
                }
            }
            if (he != null) {
                pts.add(hp);
                hitEntity = he;
                hitPos = hp;
                break;
            }
            pts.add(end);
            if (blockHit) {
                hitPos = end;
                break;
            }
            pos = next;
            vel = vel.multiply(0.99).add(0, -gravity, 0);
            if (pos.y < world.getBottomY() - 8) break;
        }

        VertexConsumer vc = RenderUtil.lines(rc);
        int n = pts.size();
        for (int i = 1; i < n; i++) {
            RenderUtil.line(rc, vc, pts.get(i - 1), pts.get(i), col((float) i / n));
        }
        if (hitEntity != null) {
            RenderUtil.box(rc, vc, hitEntity.getBoundingBox(), 0xFFFF4B4B);
        } else if (landing.get() && hitPos != null) {
            int c = col(1f);
            RenderUtil.box(rc, vc, new Box(hitPos, hitPos).expand(0.12), c);
            RenderUtil.cross(rc, vc, hitPos, 0.3, c);
        }
    }
}
