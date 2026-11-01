package dev.phantom.visuals.module;

import dev.phantom.visuals.render.RenderCtx;
import dev.phantom.visuals.setting.Setting;
import net.minecraft.client.MinecraftClient;

import java.util.ArrayList;
import java.util.List;

public abstract class Feature {
    public final String name;
    public final String description;
    public final Category category;
    public final List<Setting> settings = new ArrayList<>();
    private final boolean permanent;
    private boolean enabled;

    protected Feature(String name, String description, Category category, boolean permanent) {
        this.name = name;
        this.description = description;
        this.category = category;
        this.permanent = permanent;
        this.enabled = permanent;
    }

    public boolean isEnabled() {
        return enabled;
    }

    public boolean isPermanent() {
        return permanent;
    }

    public void setEnabled(boolean value) {
        if (permanent || enabled == value) return;
        enabled = value;
        if (value) onEnable();
        else onDisable();
    }

    public void toggle() {
        setEnabled(!enabled);
    }

    protected <T extends Setting> T add(T setting) {
        settings.add(setting);
        return setting;
    }

    public void onEnable() {}

    public void onDisable() {}

    public void onTick(MinecraftClient mc) {}

    public void onWorldRender(RenderCtx rc) {}
}
