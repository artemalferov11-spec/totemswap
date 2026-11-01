package dev.phantom.visuals.module;

import dev.phantom.visuals.setting.BoolSetting;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.Items;
import net.minecraft.screen.slot.SlotActionType;
import net.minecraft.text.Text;

public class TotemSwap extends Feature {
    private final BoolSetting totemFallback = add(new BoolSetting("Totem <-> Totem", true));

    public TotemSwap() {
        super("TotemSwap", "Key X: swaps offhand totem <-> player head", Category.UTILITY, false);
    }

    /** Вызывается по нажатию клавиши. */
    public void run(MinecraftClient mc) {
        if (!isEnabled()) return;
        ClientPlayerEntity p = mc.player;
        if (p == null || mc.interactionManager == null) return;
        if (p.currentScreenHandler != p.playerScreenHandler) return;

        if (p.getOffHandStack().isOf(Items.TOTEM_OF_UNDYING)) {
            if (trySwap(mc, p, Items.PLAYER_HEAD)) return;
            if (totemFallback.get() && trySwap(mc, p, Items.TOTEM_OF_UNDYING)) return;
            p.sendMessage(Text.translatable("message.phantomvisuals.no_head"), true);
        } else {
            if (trySwap(mc, p, Items.TOTEM_OF_UNDYING)) return;
            p.sendMessage(Text.translatable("message.phantomvisuals.no_totem"), true);
        }
    }

    private boolean trySwap(MinecraftClient mc, ClientPlayerEntity p, Item target) {
        for (int i = 0; i < 36; i++) {
            if (p.getInventory().getStack(i).isOf(target)) {
                int slotId = i < 9 ? i + 36 : i;
                mc.interactionManager.clickSlot(p.playerScreenHandler.syncId, slotId, 40, SlotActionType.SWAP, p);
                return true;
            }
        }
        return false;
    }
}
