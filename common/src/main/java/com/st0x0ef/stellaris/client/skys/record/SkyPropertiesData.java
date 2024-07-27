package com.st0x0ef.stellaris.client.skys.record;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.mojang.serialization.JsonOps;
import com.st0x0ef.stellaris.Stellaris;
import com.st0x0ef.stellaris.client.skys.renderer.SkyRenderer;
import dev.architectury.platform.Platform;
import dev.architectury.utils.Env;
import io.github.amerebagatelle.mods.nuit.api.NuitApi;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.server.packs.resources.SimpleJsonResourceReloadListener;
import net.minecraft.util.GsonHelper;
import net.minecraft.util.profiling.ProfilerFiller;
import net.minecraft.world.level.Level;

import java.util.HashMap;
import java.util.Map;

public class SkyPropertiesData extends SimpleJsonResourceReloadListener {

    public static final Map<ResourceKey<Level>, SkyRenderer> SKY_PROPERTIES = new HashMap<>();

    public SkyPropertiesData() {
        super(Stellaris.GSON, "renderer/sky");
    }

    @Override
    protected void apply(Map<ResourceLocation, JsonElement> object, ResourceManager resourceManager, ProfilerFiller profiler) {
        if (Platform.getEnvironment() == Env.CLIENT) {
            NuitApi.getInstance().clearSkyboxes();

            object.forEach((key, value) -> {
                JsonObject json = GsonHelper.convertToJsonObject(value, "sky_renderer");
                SkyProperties skyProperties = SkyProperties.CODEC.parse(JsonOps.INSTANCE, json).getOrThrow();
                SkyRenderer skyRenderer = new SkyRenderer(skyProperties);

                SKY_PROPERTIES.put(skyProperties.id(), skyRenderer);

                //Register the Skybox with Nuit
                NuitApi.getInstance().addPermanentSkybox(new ResourceLocation(Stellaris.MODID, skyProperties.id().location().getPath()), skyRenderer);
                Stellaris.LOG.error("Registering a skybox for {}", skyProperties.id());
            });

        }
    }

    public static SkyRenderer getSkyRenderersById(ResourceKey<Level> id) {
        SkyRenderer skyRenderer = SKY_PROPERTIES.get(id);
        if (skyRenderer == null) {
            Stellaris.LOG.warn("SkyProperty not found for ID: {}", id);
        }
        return skyRenderer;
    }
}
