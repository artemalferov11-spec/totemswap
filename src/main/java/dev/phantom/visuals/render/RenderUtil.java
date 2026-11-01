package dev.phantom.visuals.render;

import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.render.LightmapTextureManager;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.Text;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;

public final class RenderUtil {
    private RenderUtil() {}

    public static VertexConsumer lines(RenderCtx rc) {
        return rc.consumers.getBuffer(RenderLayer.getLines());
    }

    /** Линия в мировых координатах (камера учитывается внутри). */
    public static void line(RenderCtx rc, VertexConsumer vc, Vec3d a, Vec3d b, int argb) {
        MatrixStack.Entry e = rc.matrices.peek();
        float ax = (float) (a.x - rc.cam.x), ay = (float) (a.y - rc.cam.y), az = (float) (a.z - rc.cam.z);
        float bx = (float) (b.x - rc.cam.x), by = (float) (b.y - rc.cam.y), bz = (float) (b.z - rc.cam.z);
        float dx = bx - ax, dy = by - ay, dz = bz - az;
        float len = MathHelper.sqrt(dx * dx + dy * dy + dz * dz);
        if (len < 1.0E-6f) return;
        dx /= len;
        dy /= len;
        dz /= len;
        int al = (argb >>> 24) & 255, r = (argb >> 16) & 255, g = (argb >> 8) & 255, bl = argb & 255;
        vc.vertex(e, ax, ay, az).color(r, g, bl, al).normal(e, dx, dy, dz);
        vc.vertex(e, bx, by, bz).color(r, g, bl, al).normal(e, dx, dy, dz);
    }

    public static void box(RenderCtx rc, VertexConsumer vc, Box b, int argb) {
        Vec3d[] c = new Vec3d[8];
        int i = 0;
        for (int x = 0; x < 2; x++)
            for (int y = 0; y < 2; y++)
                for (int z = 0; z < 2; z++)
                    c[i++] = new Vec3d(x == 0 ? b.minX : b.maxX, y == 0 ? b.minY : b.maxY, z == 0 ? b.minZ : b.maxZ);
        for (int a = 0; a < 8; a++) {
            for (int bit = 0; bit < 3; bit++) {
                int o = a ^ (1 << bit);
                if (o > a) line(rc, vc, c[a], c[o], argb);
            }
        }
    }

    public static void cross(RenderCtx rc, VertexConsumer vc, Vec3d p, double s, int argb) {
        line(rc, vc, p.add(-s, 0, 0), p.add(s, 0, 0), argb);
        line(rc, vc, p.add(0, -s, 0), p.add(0, s, 0), argb);
        line(rc, vc, p.add(0, 0, -s), p.add(0, 0, s), argb);
    }

    /** Текст, повёрнутый к камере, виден сквозь стены. */
    public static void text(RenderCtx rc, Text text, Vec3d pos, float scale, int color, int background) {
        MatrixStack m = rc.matrices;
        m.push();
        m.translate(pos.x - rc.cam.x, pos.y - rc.cam.y, pos.z - rc.cam.z);
        m.multiply(rc.camera.getRotation());
        m.scale(scale, -scale, scale);
        TextRenderer tr = rc.mc.textRenderer;
        float w = tr.getWidth(text);
        tr.draw(text, -w / 2f, 0f, color, false, m.peek().getPositionMatrix(), rc.consumers,
                TextRenderer.TextLayerType.SEE_THROUGH, background, LightmapTextureManager.MAX_LIGHT_COORDINATE);
        m.pop();
    }
}
