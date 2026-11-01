package dev.phantom.visuals.mixin;

import dev.phantom.visuals.module.ModuleManager;
import net.minecraft.client.render.entity.ItemEntityRenderer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.RotationAxis;
import org.joml.Quaternionf;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(ItemEntityRenderer.class)
public abstract class ItemEntityRendererMixin {

    // 1) убираем покачивание вверх-вниз
    @Redirect(
            method = "render(Lnet/minecraft/client/render/entity/state/ItemEntityRenderState;Lnet/minecraft/client/util/math/MatrixStack;Lnet/minecraft/client/render/VertexConsumerProvider;I)V",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/util/math/MathHelper;sin(F)F"))
    private float phantom$noBob(float value) {
        return ModuleManager.ITEM_PHYSIC.isEnabled() ? 0f : MathHelper.sin(value);
    }

    // 2) убираем вращение: фиксированный угол у каждого предмета
    @Redirect(
            method = "render(Lnet/minecraft/client/render/entity/state/ItemEntityRenderState;Lnet/minecraft/client/util/math/MatrixStack;Lnet/minecraft/client/render/VertexConsumerProvider;I)V",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/ItemEntity;getRotation(FF)F"))
    private float phantom$noSpin(float age, float offset) {
        return ModuleManager.ITEM_PHYSIC.isEnabled() ? offset : age / 20.0f + offset;
    }

    // 3) кладём предмет плашмя на землю
    @Redirect(
            method = "render(Lnet/minecraft/client/render/entity/state/ItemEntityRenderState;Lnet/minecraft/client/util/math/MatrixStack;Lnet/minecraft/client/render/VertexConsumerProvider;I)V",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/client/util/math/MatrixStack;multiply(Lorg/joml/Quaternionf;)V"))
    private void phantom$lieFlat(MatrixStack matrices, Quaternionf rotation) {
        if (ModuleManager.ITEM_PHYSIC.isEnabled()) {
            matrices.translate(0.0, -ModuleManager.ITEM_PHYSIC.drop.get(), 0.0);
            matrices.multiply(rotation);
            matrices.multiply(RotationAxis.POSITIVE_X.rotationDegrees(90.0f));
        } else {
            matrices.multiply(rotation);
        }
    }
}
