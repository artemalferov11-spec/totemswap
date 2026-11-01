package dev.phantom.visuals.mixin;

import dev.phantom.visuals.module.ModuleManager;
import net.minecraft.client.render.entity.LivingEntityRenderer;
import net.minecraft.entity.LivingEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(LivingEntityRenderer.class)
public abstract class LivingEntityRendererMixin {

    // Прячем ванильный тег, когда рисуем свой (иначе будет дубль)
    @Inject(method = "hasLabel(Lnet/minecraft/entity/LivingEntity;D)Z", at = @At("HEAD"), cancellable = true)
    private void phantom$hideVanillaLabel(LivingEntity entity, double distance, CallbackInfoReturnable<Boolean> cir) {
        if (ModuleManager.NAMETAGS.shouldRender(entity)) {
            cir.setReturnValue(false);
        }
    }
}
