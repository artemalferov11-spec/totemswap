package dev.phantom.visuals.mixin;

import dev.phantom.visuals.module.FreeLook;
import dev.phantom.visuals.module.ModuleManager;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.entity.Entity;
import net.minecraft.util.math.MathHelper;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(Entity.class)
public abstract class EntityMixin {

    @Inject(method = "isGlowing", at = @At("HEAD"), cancellable = true)
    private void phantom$glowInvisible(CallbackInfoReturnable<Boolean> cir) {
        Entity self = (Entity) (Object) this;
        if (!self.getWorld().isClient) return;
        if (ModuleManager.INVISIBLES.matches(self)) {
            cir.setReturnValue(true);
        }
    }

    // FreeLook: мышь крутит камеру, а не игрока
    @Inject(method = "changeLookDirection", at = @At("HEAD"), cancellable = true)
    private void phantom$freeLookMouse(double dx, double dy, CallbackInfo ci) {
        if (FreeLook.active && (Object) this instanceof ClientPlayerEntity) {
            FreeLook.yaw += (float) dx * 0.15f;
            FreeLook.pitch = MathHelper.clamp(FreeLook.pitch + (float) dy * 0.15f, -90.0f, 90.0f);
            ci.cancel();
        }
    }
}
