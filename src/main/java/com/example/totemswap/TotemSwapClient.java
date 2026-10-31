package com.example.totemswap;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.util.InputUtil;
import net.minecraft.item.Item;
import net.minecraft.item.Items;
import net.minecraft.screen.slot.SlotActionType;
import net.minecraft.text.Text;
import org.lwjgl.glfw.GLFW;

public class TotemSwapClient implements ClientModInitializer {

    private static KeyBinding swapKey;

    @Override
    public void onInitializeClient() {
        swapKey = KeyBindingHelper.registerKeyBinding(new KeyBinding(
                "key.totemswap.swap",
                InputUtil.Type.KEYSYM,
                GLFW.GLFW_KEY_X,
                "key.category.totemswap"
        ));
        ClientTickEvents.END_CLIENT_TICK.register(this::onTick);
    }

    private void onTick(MinecraftClient client) {
        while (swapKey.wasPressed()) {
            ClientPlayerEntity player = client.player;
            if (player == null || client.interactionManager == null) continue;
            if (player.currentScreenHandler != player.playerScreenHandler) continue;
            swap(client, player);
        }
    }

    private void swap(MinecraftClient client, ClientPlayerEntity player) {
        boolean totemInHand = player.getOffHandStack().isOf(Items.TOTEM_OF_UNDYING);

        if (totemInHand) {
            // Тотем в руке -> сначала пробуем голову,
            // если головы нет -> меняем на другой тотем из инвентаря
            if (trySwap(client, player, Items.PLAYER_HEAD)) return;
            if (trySwap(client, player, Items.TOTEM_OF_UNDYING)) return;
            player.sendMessage(Text.translatable("message.totemswap.no_head"), true);
        } else {
            // В руке голова / пусто / что-то другое -> ставим тотем
            if (trySwap(client, player, Items.TOTEM_OF_UNDYING)) return;
            player.sendMessage(Text.translatable("message.totemswap.no_totem"), true);
        }
    }

    /** Меняет левую руку с первым найденным в инвентаре предметом target. */
    private boolean trySwap(MinecraftClient client, ClientPlayerEntity player, Item target) {
        // 0..8 — хотбар, 9..35 — основной инвентарь (левая рука сюда не входит)
        for (int i = 0; i < 36; i++) {
            if (player.getInventory().getStack(i).isOf(target)) {
                int slotId = i < 9 ? i + 36 : i;
                client.interactionManager.clickSlot(
                        player.playerScreenHandler.syncId,
                        slotId,
                        40, // 40 = левая рука
                        SlotActionType.SWAP,
                        player
                );
                return true;
            }
        }
        return false;
    }
}
