package dev.phantom.visuals.module;

import dev.phantom.visuals.mixin.MinecraftClientAccessor;
import dev.phantom.visuals.setting.BoolSetting;
import dev.phantom.visuals.setting.NumberSetting;
import net.minecraft.client.MinecraftClient;
import net.minecraft.item.BlockItem;

public class FastPlace extends Feature {
    private final BoolSetting blocksOnly = add(new BoolSetting("Blocks only", true));
    private final NumberSetting delay = add(new NumberSetting("Delay (ticks)", 0, 4, 1, 0));

    public FastPlace() {
        super("FastPlace", "Removes the delay between placing blocks", Category.UTILITY, false);
    }

    @Override
    public void onTick(MinecraftClient mc) {
        if (mc.player == null) return;
        if (blocksOnly.get()
                && !(mc.player.getMainHandStack().getItem() instanceof BlockItem)
                && !(mc.player.getOffHandStack().getItem() instanceof BlockItem)) {
            return;
        }
        MinecraftClientAccessor acc = (MinecraftClientAccessor) (Object) mc;
        if (acc.phantom$getItemUseCooldown() > delay.getI()) {
            acc.phantom$setItemUseCooldown(delay.getI());
        }
    }
}
