package com.st0x0ef.stellaris.client.screens;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.*;
import com.st0x0ef.stellaris.Stellaris;
import com.st0x0ef.stellaris.client.screens.components.ModifiedButton;
import com.st0x0ef.stellaris.client.screens.components.TexturedButton;
import com.st0x0ef.stellaris.client.screens.info.CelestialBody;
import com.st0x0ef.stellaris.client.screens.info.MoonInfo;
import com.st0x0ef.stellaris.client.screens.info.PlanetInfo;
import com.st0x0ef.stellaris.common.data.planets.StellarisData;
import com.st0x0ef.stellaris.common.menus.PlanetSelectionMenu;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.Util;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.ImageButton;
import net.minecraft.client.gui.components.WidgetSprites;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;
import org.lwjgl.glfw.GLFW;
import org.lwjgl.glfw.GLFWScrollCallback;
import org.lwjgl.opengl.GL11;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;


@Environment(EnvType.CLIENT)
public class PlanetSelectionScreen extends AbstractContainerScreen<PlanetSelectionMenu> {

    public static final ResourceLocation BACKGROUND_TEXTURE = new ResourceLocation(Stellaris.MODID, "textures/gui/planet_selection.png");
    public static final ResourceLocation SCROLLER_TEXTURE = new ResourceLocation(Stellaris.MODID,
            "textures/gui/util/scroller.png");

    public static final ResourceLocation SMALL_BUTTON_TEXTURE = new ResourceLocation(Stellaris.MODID,
            "textures/gui/util/buttons/small_button.png");
    public static final ResourceLocation BUTTON_TEXTURE = new ResourceLocation(Stellaris.MODID,
            "textures/gui/util/buttons/button.png");
    public static final ResourceLocation LARGE_BUTTON_TEXTURE = new ResourceLocation(Stellaris.MODID,
            "textures/gui/util/buttons/large_button.png");

    public static final ResourceLocation SMALL_MENU_LIST = new ResourceLocation(Stellaris.MODID,
            "textures/gui/util/planet_menu.png");
    public static final ResourceLocation LARGE_MENU_TEXTURE = new ResourceLocation(Stellaris.MODID,
            "textures/gui/util/large_planet_menu.png");

    public static final List<CelestialBody> STARS = new ArrayList<>();
    public static final List<PlanetInfo> PLANETS = new ArrayList<>();
    public static final List<MoonInfo> MOONS = new ArrayList<>();

//    private static final PlanetInfo EARTH = new PlanetInfo(new ResourceLocation(Stellaris.MODID, "textures/environment/solar_system/earth.png"),
//            "Earth", 100, 8000L, 10, 10, SUN);
//
//    private static final List<PlanetInfo> PLANETS = Arrays.asList(
//            new PlanetInfo(new ResourceLocation(Stellaris.MODID, "textures/environment/solar_system/mercury.png"),
//                    "Mercury", 38, 3000L, 5, 5, SUN),
//            new PlanetInfo(new ResourceLocation(Stellaris.MODID, "textures/environment/solar_system/venus.png"),
//                    "Venus", 68, 5000L, 9, 9, SUN),
//            EARTH,
//            new PlanetInfo(new ResourceLocation(Stellaris.MODID, "textures/environment/solar_system/mars.png"),
//                    "Mars", 140, 10000L, 9, 9, SUN)
//    );
//
//    private static final List<MoonInfo> MOONS = Arrays.asList(
//            new MoonInfo(new ResourceLocation(Stellaris.MODID, "textures/environment/solar_system/moon.png"),
//                    30, 1000L, 7, 7, EARTH),
//            new MoonInfo(new ResourceLocation(Stellaris.MODID, "textures/environment/solar_system/deimos.png"),
//                    20, 1000L, 4, 4, PLANETS.get(3)),
//            new MoonInfo(new ResourceLocation(Stellaris.MODID, "textures/environment/solar_system/phobos.png"),
//                    30, 1500L, 4, 4, PLANETS.get(3)
//            )
//    );


    private final ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);
    private static final long UPDATE_INTERVAL = 1L;

    private double offsetX = 0;
    private double offsetY = 0;

    private double lastMouseX;
    private double lastMouseY;
    private boolean dragging = false;

    private boolean isXPressed;

    private double zoomLevel = 1.0;
    private GLFWScrollCallback prevScrollCallback;

    public List<ModifiedButton> visibleButtons;

    public PlanetSelectionScreen(PlanetSelectionMenu abstractContainerMenu, Inventory inventory, Component component) {
        super(abstractContainerMenu, inventory, component);
        startUpdating();
        this.imageWidth = 1200;
        this.imageHeight = 1600;
        this.inventoryLabelY = this.imageHeight - 110;

    }

    @Override
    protected void init() {
        super.init();
        centerSun();
        long windowHandle = Minecraft.getInstance().getWindow().getWindow();
        prevScrollCallback = GLFW.glfwSetScrollCallback(windowHandle, this::onMouseScroll);
    }

    private void nothing() {
    }

    private void startUpdating() {
        scheduler.scheduleAtFixedRate(this::updatePlanets, 0, UPDATE_INTERVAL, TimeUnit.MILLISECONDS);
    }

    @Override
    public void render(GuiGraphics graphics, int mouseX, int mouseY, float partialTicks) {
        super.render(graphics, mouseX, mouseY, partialTicks);
        this.renderBackground(graphics, mouseX, mouseY, partialTicks);
        drawOrbits();
        renderBodiesAndPlanets(graphics);
        renderMoons(graphics);

        this.renderTooltip(graphics, mouseX, mouseY);
    }

    @Override
    protected void renderBg(GuiGraphics graphics, float var2, int var3, int var4) {
        RenderSystem.setShader(GameRenderer::getPositionTexShader);
        RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
        RenderSystem.setShaderTexture(0, BACKGROUND_TEXTURE);
        graphics.blit(BACKGROUND_TEXTURE, this.leftPos, this.topPos, 0, 0, this.imageWidth, this.imageHeight, this.imageWidth, this.imageHeight);

//        ScreenHelper.drawTexture(0, (this.height / 2) - 177 / 2, 215, 177, LARGE_MENU_TEXTURE, true);

        addSystemsButtons();
    }

    public void renderBodiesAndPlanets(GuiGraphics graphics) {
        Font font = Minecraft.getInstance().font;

        for (CelestialBody body : STARS) {
            float bodyX = (float) ((body.x + offsetX) * zoomLevel - (body.width / 2) * zoomLevel);
            float bodyY = (float) ((body.y + offsetY) * zoomLevel - (body.height / 2) * zoomLevel);

            int bodyWidth = (int) (body.width * zoomLevel);
            int bodyHeight = (int) (body.height * zoomLevel);

            graphics.blit(body.texture, (int) bodyX, (int) bodyY, 0, 0, bodyWidth, bodyHeight, bodyWidth, bodyHeight);

            int nameWidth = font.width(body.name);
            graphics.drawString(font, body.name, (int) (bodyX + bodyWidth / 2 - nameWidth / 2), (int) (bodyY + bodyHeight), 0xFFFFFF);
        }

        for (PlanetInfo planet : PLANETS) {
            CelestialBody orbitCenter = planet.orbitCenter;

            float orbitCenterX = (float) ((orbitCenter.x + offsetX) * zoomLevel);
            float orbitCenterY = (float) ((orbitCenter.y + offsetY) * zoomLevel);

            float planetX = (float) (orbitCenterX + planet.orbitRadius * zoomLevel * Math.cos(planet.currentAngle) - planet.width / 2 * zoomLevel);
            float planetY = (float) (orbitCenterY + planet.orbitRadius * zoomLevel * Math.sin(planet.currentAngle) - planet.height / 2 * zoomLevel);

            int planetWidth = (int) (planet.width * zoomLevel);
            int planetHeight = (int) (planet.height * zoomLevel);

            graphics.blit(planet.texture, (int) planetX, (int) planetY, 0, 0, planetWidth, planetHeight, planetWidth, planetHeight);

            int nameWidth = font.width(planet.name);
            graphics.drawString(font, planet.name, (int) (planetX + planetWidth / 2 - nameWidth / 2), (int) (planetY + planetHeight), 0xFFFFFF);

        }
    }

    public void renderMoons(GuiGraphics graphics) {
        for (MoonInfo moon : MOONS) {
            float moonX = (float) ((moon.x + offsetX) * zoomLevel - (moon.width / 2) * zoomLevel);
            float moonY = (float) ((moon.y + offsetY) * zoomLevel - (moon.height / 2) * zoomLevel);

            int moonWidth = (int) (moon.width * zoomLevel);
            int moonHeight = (int) (moon.height * zoomLevel);

            graphics.blit(moon.texture, (int) moonX, (int) moonY, 0, 0, moonWidth, moonHeight, moonWidth, moonHeight);
        }
    }

    @Override
    public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
        if (keyCode == GLFW.GLFW_KEY_X) {
            isXPressed = true;
        }
        return super.keyPressed(keyCode, scanCode, modifiers);
    }

    @Override
    public boolean keyReleased(int keyCode, int scanCode, int modifiers) {
        if (keyCode == GLFW.GLFW_KEY_X) {
            isXPressed = false;
        }
        return super.keyReleased(keyCode, scanCode, modifiers);
    }

    private void updatePlanets() {
        long time = Util.getMillis();
        if (isXPressed == false) {
            for (PlanetInfo planet : PLANETS) {
                planet.updateAngle(time);
                planet.updatePosition();

//                System.out.println(planet.name + ", " + planet.x + ", " + planet.y);
            }
            for (MoonInfo moon : MOONS) {
                moon.updateAngle(time);
                moon.updatePosition();

//                System.out.println(moon.name + ", " + moon.x + ", " + moon.y);
            }
        }
    }

    public void drawOrbits() {
        RenderSystem.enableBlend();
        RenderSystem.defaultBlendFunc();
        RenderSystem.setShader(GameRenderer::getPositionColorShader);

        GL11.glLineWidth(2.0F);

        BufferBuilder bufferBuilder = Tesselator.getInstance().getBuilder();
        bufferBuilder.begin(VertexFormat.Mode.DEBUG_LINES, DefaultVertexFormat.POSITION_COLOR);

        for (PlanetInfo planet : PLANETS) {
            CelestialBody orbitCenter = planet.orbitCenter;

            float orbitCenterX = (float) ((orbitCenter.x + offsetX) * zoomLevel);
            float orbitCenterY = (float) ((orbitCenter.y + offsetY) * zoomLevel);

            renderOrbits(bufferBuilder, orbitCenterX, orbitCenterY, planet.orbitRadius * zoomLevel, 75, orbitCenter.orbitColor, 1.0F);
        }

        for (MoonInfo moon : MOONS) {
            CelestialBody orbitCenter = moon.orbitCenter;

            float orbitCenterX = (float) ((orbitCenter.x + offsetX) * zoomLevel);
            float orbitCenterY = (float) ((orbitCenter.y + offsetY) * zoomLevel);

            renderOrbits(bufferBuilder, orbitCenterX, orbitCenterY, moon.orbitRadius * zoomLevel, 75, 0x888888, 0.5F);
        }

        Tesselator.getInstance().end();

        RenderSystem.disableBlend();
    }

    public static void renderOrbits(BufferBuilder bufferBuilder, double centerX, double centerY, double radius, int sides, int color, float alphaL) {
        float red = ((color >> 16) & 0xFF) / 255.0F;
        float green = ((color >> 8) & 0xFF) / 255.0F;
        float blue = (color & 0xFF) / 255.0F;

        double angleStep = 2.0 * Math.PI / sides;

        for (int i = 0; i < sides; i++) {
            double currentAngle = i * angleStep;
            double nextAngle = currentAngle + angleStep;

            double vertex1X = centerX + radius * Math.cos(currentAngle);
            double vertex1Y = centerY + radius * Math.sin(currentAngle);
            double vertex2X = centerX + radius * Math.cos(nextAngle);
            double vertex2Y = centerY + radius * Math.sin(nextAngle);

            bufferBuilder.vertex(vertex1X, vertex1Y, 0).color(red, green, blue, alphaL).endVertex();
            bufferBuilder.vertex(vertex2X, vertex2Y, 0).color(red, green, blue, alphaL).endVertex();
        }

    }

    private void centerSun() {
        float centerX = width / 2.0f;
        float centerY = height / 2.0f;
        findByNameStar("Sun").setPosition(centerX, centerY);
        offsetX = 0;
        offsetY = 0;
    }

    public static CelestialBody findByNameStar(String name) {
        for (CelestialBody body : PlanetSelectionScreen.STARS) {
            if (body.getName().equals(name)) {
                return body;
            }
        }
        return null;
    }

    private void centerOnBody(CelestialBody body) {
        zoomLevel = 1;
        offsetX = ((body.x - width / 2.0)) * -1;
        offsetY = ((body.y - height / 2.0)) * -1;
    }

    private void onMouseScroll(long window, double scrollX, double scrollY) {
        Minecraft.getInstance().player.getInventory().swapPaint((int) scrollY);

        double[] mouseX = new double[1];
        double[] mouseY = new double[1];
        double screenSize = Minecraft.getInstance().getWindow().getWidth();
        GLFW.glfwGetCursorPos(window, mouseX, mouseY);

        if (scrollY != 0) {
            if (mouseX[0] < screenSize / 5) {

            } else {
                zoomLevel += scrollY * 0.01;
                zoomLevel = Math.max(0.01, Math.min(zoomLevel, 2.0));
            }
        }
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        if (button == GLFW.GLFW_MOUSE_BUTTON_LEFT || button == GLFW.GLFW_MOUSE_BUTTON_RIGHT) {
            dragging = true;
            lastMouseX = mouseX;
            lastMouseY = mouseY;
        }
        return super.mouseClicked(mouseX, mouseY, button);
    }

    @Override
    public boolean mouseReleased(double mouseX, double mouseY, int button) {
        if (button == GLFW.GLFW_MOUSE_BUTTON_LEFT || button == GLFW.GLFW_MOUSE_BUTTON_RIGHT) {
            dragging = false;
        }
        return super.mouseReleased(mouseX, mouseY, button);
    }

    @Override
    public boolean mouseDragged(double mouseX, double mouseY, int button, double deltaX, double deltaY) {
        if (dragging) {
            offsetX += (mouseX - lastMouseX) / zoomLevel;
            offsetY += (mouseY - lastMouseY) / zoomLevel;
            lastMouseX = mouseX;
            lastMouseY = mouseY;
        }
        return super.mouseDragged(mouseX, mouseY, button, deltaX, deltaY);
    }

    @Override
    public PlanetSelectionMenu getMenu() {
        return this.menu;
    }


    public void addSystemsButtons() {
        AtomicInteger systemsHeight = new AtomicInteger();
        StellarisData.SYSTEMS.forEach((key, value) -> {
            List<String> buttonText = List.of("Go Back to the planet selection", "menu and try again.");
            this.addButton(this.width / 2 - 30 , this.height / 2 + 50, 0, 75, 25, false, null, buttonText,
                    BUTTON_TEXTURE, TexturedButton.ColorTypes.BLUE, Component.literal(key), (onPress) -> {
                        System.out.println("Go Back");
                        this.onClose();
                    });

            systemsHeight.addAndGet(35);
        });

    }

    public ModifiedButton addButton(int x, int y, int row, int width, int height, boolean rocketCondition,
                                    ModifiedButton.ButtonTypes type, List<String> list, ResourceLocation buttonTexture,
                                    ModifiedButton.ColorTypes colorType, Component title, Button.OnPress onPress) {
        return this.addRenderableWidget(new ModifiedButton(x, y, row, width, height, 0, 0, 0,
                rocketCondition, type, list, buttonTexture, colorType, width, height, onPress, title));
    }

}
