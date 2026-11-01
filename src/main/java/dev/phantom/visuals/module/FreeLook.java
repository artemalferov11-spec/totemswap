package dev.phantom.visuals.module;

import dev.phantom.visuals.setting.BoolSetting;
import dev.phantom.visuals.setting.ModeSetting;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.option.Perspective;

public class FreeLook extends Feature {
    /** Состояние читают миксины камеры и мыши. */
    public static boolean active;
    public static float yaw;
    public static float pitch;

    private final ModeSetting mode = add(new ModeSetting("Mode", "Hold", "Hold", "Toggle"));
    private final BoolSetting thirdPerson = add(new BoolSetting("Third person", true));

    private boolean toggled;
    private Perspective saved;

    public FreeLook() {
        super("FreeLook", "Look around without turning your character", Category.VISUALS, false);
    }

    /** Вызывается каждый тик с клавишей FreeLook. */
    public void tickKey(MinecraftClient mc, KeyBinding key) {
        boolean want;
        if (!isEnabled() || mc.currentScreen != null || mc.player == null) {
            while (key.wasPressed()) { /* сбрасываем нажатия */ }
            toggled = false;
            want = false;
        } else if (mode.is("Hold")) {
            while (key.wasPressed()) { /* сбрасываем нажатия */ }
            want = key.isPressed();
        } else {
            while (key.wasPressed()) toggled = !toggled;
            want = toggled;
        }

        if (want && !active) start(mc);
        else if (!want && active) stop(mc);
    }

    private void start(MinecraftClient mc) {
        active = true;
        yaw = mc.player.getYaw();
        pitch = mc.player.getPitch();
        if (thirdPerson.get()) {
            saved = mc.options.getPerspective();
            mc.options.setPerspective(Perspective.THIRD_PERSON_BACK);
        }
    }

    private void stop(MinecraftClient mc) {
        active = false;
        if (saved != null) {
            mc.options.setPerspective(saved);
            saved = null;
        }
    }

    @Override
    public void onDisable() {
        if (active) stop(MinecraftClient.getInstance());
        toggled = false;
    }
}
