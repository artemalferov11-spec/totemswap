package dev.phantom.visuals.mixin;

import dev.phantom.visuals.module.FreeLook;
import net.minecraft.client.render.Camera;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArgs;
import org.spongepowered.asm.mixin.injection.invoke.arg.Args;

@Mixin(Camera.class)
public abstract class CameraMixin {

    // Во время FreeLook камера смотрит в свою сторону, а не туда, куда повёрнут игрок
    @ModifyArgs(
            method = "update(Lnet/minecraft/world/BlockView;Lnet/minecraft/entity/Entity;ZZF)V",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/client/render/Camera;setRotation(FF)V"))
    private void phantom$freeLook(Args args) {
        if (FreeLook.active) {
            args.set(0, FreeLook.yaw);
            args.set(1, FreeLook.pitch);
        }
    }
}
