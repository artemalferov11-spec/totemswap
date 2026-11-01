package dev.phantom.visuals;

import dev.phantom.visuals.gui.ClickGuiScreen;
import dev.phantom.visuals.module.Feature;
import dev.phantom.visuals.module.ModuleManager;
import dev.phantom.visuals.render.RenderCtx;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientLifecycleEvents;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.fabricmc.fabric.api.client.rendering.v1.WorldRenderContext;
import net.fabricmc.fabric.api.client.rendering.v1.WorldRenderEvents;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.util.InputUtil;
import net.minecraft.client.util.math.MatrixStack;
import org.lwjgl.glfw.GLFW;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class PhantomVisuals implements ClientModInitializer {
    public static final String MOD_ID = "phantomvisuals";
    public static final String NAME = "PhantomVisuals";
    public static final Logger LOGGER = LoggerFactory.getLogger(NAME);

    private static KeyBinding guiKey;
    private static KeyBinding swapKey;

    @Override
    public void onInitializeClient() {
        String version = FabricLoader.getInstance().getModContainer(MOD_ID)
                .map(c -> c.getMetadata().getVersion().getFriendlyString()).orElse("?");
        LOGGER.info("==========================================");
        LOGGER.info("{} v{} - loading...", NAME, version);
        LOGGER.info("Features: Predictions, ItemPhysic, Custom Particles & Trails, Nametags, ClickGUI");

        Config.load();

        guiKey = KeyBindingHelper.registerKeyBinding(new KeyBinding(
                "key.phantomvisuals.gui", InputUtil.Type.KEYSYM, GLFW.GLFW_KEY_RIGHT_SHIFT, "key.category.phantomvisuals"));
        swapKey = KeyBindingHelper.registerKeyBinding(new KeyBinding(
                "key.phantomvisuals.swap", InputUtil.Type.KEYSYM, GLFW.GLFW_KEY_X, "key.category.phantomvisuals"));

        ClientTickEvents.END_CLIENT_TICK.register(PhantomVisuals::tick);
        WorldRenderEvents.AFTER_TRANSLUCENT.register(PhantomVisuals::render);
        ClientLifecycleEvents.CLIENT_STOPPING.register(client -> Config.save());

        LOGGER.info("{} loaded: {} features. Press RIGHT SHIFT in game to open ClickGUI.", NAME, ModuleManager.ALL.size());
        LOGGER.info("==========================================");
    }

    private static void tick(MinecraftClient mc) {
        if (mc.player == null || mc.world == null) return;

        while (guiKey.wasPressed()) {
            if (mc.currentScreen == null) ClickGuiScreen.open();
        }
        while (swapKey.wasPressed()) {
            ModuleManager.TOTEM_SWAP.run(mc);
        }
        for (Feature f : ModuleManager.ALL) {
            if (f.isEnabled()) f.onTick(mc);
        }
    }

    private static void render(WorldRenderContext ctx) {
        MinecraftClient mc = MinecraftClient.getInstance();
        if (mc.world == null || mc.player == null) return;

        MatrixStack matrices = ctx.matrixStack();
        VertexConsumerProvider consumers = ctx.consumers();
        if (matrices == null || consumers == null) return;

        RenderCtx rc = new RenderCtx(mc, matrices, consumers, ctx.camera(), mc.getRenderTickCounter().getTickDelta(true));
        for (Feature f : ModuleManager.ALL) {
            if (f.isEnabled()) f.onWorldRender(rc);
        }
        if (consumers instanceof VertexConsumerProvider.Immediate immediate) {
            immediate.draw();
        }
    }
}
