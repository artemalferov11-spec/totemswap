package dev.phantom.visuals.module;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayerEntity;

public class AutoSprint extends Feature {
    public AutoSprint() {
        super("AutoSprint", "Sprint automatically while moving forward", Category.UTILITY, false);
    }

    @Override
    public void onTick(MinecraftClient mc) {
        ClientPlayerEntity p = mc.player;
        if (p == null) return;
        if (mc.options.forwardKey.isPressed()
                && !p.isSneaking()
                && !p.isUsingItem()
                && !p.horizontalCollision
                && p.getHungerManager().getFoodLevel() > 6) {
            p.setSprinting(true);
        }
    }
}
