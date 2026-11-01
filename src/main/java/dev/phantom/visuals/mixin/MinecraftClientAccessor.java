package dev.phantom.visuals.mixin;

import net.minecraft.client.MinecraftClient;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(MinecraftClient.class)
public interface MinecraftClientAccessor {
    @Accessor("itemUseCooldown")
    int phantom$getItemUseCooldown();

    @Accessor("itemUseCooldown")
    void phantom$setItemUseCooldown(int value);
}
