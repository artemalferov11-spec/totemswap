package dev.phantom.visuals.render;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.Camera;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.math.Vec3d;

/** Всё, что нужно модулям для рисования в мире. */
public final class RenderCtx {
    public final MinecraftClient mc;
    public final MatrixStack matrices;
    public final VertexConsumerProvider consumers;
    public final Camera camera;
    public final Vec3d cam;
    public final float delta;

    public RenderCtx(MinecraftClient mc, MatrixStack matrices, VertexConsumerProvider consumers, Camera camera, float delta) {
        this.mc = mc;
        this.matrices = matrices;
        this.consumers = consumers;
        this.camera = camera;
        this.cam = camera.getPos();
        this.delta = delta;
    }
}
