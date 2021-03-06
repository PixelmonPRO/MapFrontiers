package games.alejandrocoria.mapfrontiers.client.gui;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;

import org.apache.commons.lang3.StringUtils;
import org.lwjgl.input.Keyboard;
import org.lwjgl.opengl.GL11;

import games.alejandrocoria.mapfrontiers.MapFrontiers;
import games.alejandrocoria.mapfrontiers.common.ConfigData;
import journeymap.client.ui.UIManager;
import journeymap.client.ui.minimap.MiniMap;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.client.config.GuiUtils;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@ParametersAreNonnullByDefault
@SideOnly(Side.CLIENT)
public class GuiHUDSettings extends GuiScreen implements TextBox.TextBoxResponder {
    private static final int guiTextureSize = 512;

    private ResourceLocation guiTexture;
    private GuiFrontierSettings parent;
    private GuiOptionButton buttonSlot1;
    private GuiOptionButton buttonSlot2;
    private GuiOptionButton buttonSlot3;
    private TextBox textBannerSize;
    private GuiOptionButton buttonAnchor;
    private TextBox textPositionX;
    private TextBox textPositionY;
    private GuiOptionButton buttonAutoAdjustAnchor;
    private GuiOptionButton buttonSnapToBorder;
    private GuiSettingsButton buttonDone;
    private List<GuiSimpleLabel> labels;
    private Map<GuiSimpleLabel, List<String>> labelTooltips;
    private int id = 0;
    private MiniMap minimap;
    private GuiHUD guiHUD;
    private int anchorLineColor = GuiColors.SETTINGS_ANCHOR_LIGHT;
    private int anchorLineColorTick = 0;
    private ConfigData.Point positionHUD = new ConfigData.Point();
    private ConfigData.Point grabOffset = new ConfigData.Point();
    private boolean grabbed = false;

    public GuiHUDSettings(GuiFrontierSettings parent) {
        guiTexture = new ResourceLocation(MapFrontiers.MODID + ":textures/gui/gui.png");
        labels = new ArrayList<GuiSimpleLabel>();
        labelTooltips = new HashMap<GuiSimpleLabel, List<String>>();
        this.parent = parent;
        guiHUD = GuiHUD.asPreview();
        guiHUD.configUpdated();
        updatePosition();
    }

    @Override
    public void initGui() {
        buttonSlot1 = new GuiOptionButton(++id, mc.fontRenderer, width / 2 - 108, height / 2 - 32, 50);
        buttonSlot1.addOption(ConfigData.HUDSlot.None.name());
        buttonSlot1.addOption(ConfigData.HUDSlot.Name.name());
        buttonSlot1.addOption(ConfigData.HUDSlot.Owner.name());
        buttonSlot1.addOption(ConfigData.HUDSlot.Banner.name());
        buttonSlot1.setSelected(ConfigData.hud.slot1.ordinal());

        buttonSlot2 = new GuiOptionButton(++id, mc.fontRenderer, width / 2 - 108, height / 2 - 16, 50);
        buttonSlot2.addOption(ConfigData.HUDSlot.None.name());
        buttonSlot2.addOption(ConfigData.HUDSlot.Name.name());
        buttonSlot2.addOption(ConfigData.HUDSlot.Owner.name());
        buttonSlot2.addOption(ConfigData.HUDSlot.Banner.name());
        buttonSlot2.setSelected(ConfigData.hud.slot2.ordinal());

        buttonSlot3 = new GuiOptionButton(++id, mc.fontRenderer, width / 2 - 108, height / 2, 50);
        buttonSlot3.addOption(ConfigData.HUDSlot.None.name());
        buttonSlot3.addOption(ConfigData.HUDSlot.Name.name());
        buttonSlot3.addOption(ConfigData.HUDSlot.Owner.name());
        buttonSlot3.addOption(ConfigData.HUDSlot.Banner.name());
        buttonSlot3.setSelected(ConfigData.hud.slot3.ordinal());

        textBannerSize = new TextBox(++id, mc.fontRenderer, width / 2 - 108, height / 2 + 16, 50, "");
        textBannerSize.setText(String.valueOf(ConfigData.hud.bannerSize));
        textBannerSize.setMaxStringLength(1);
        textBannerSize.setResponder(this);
        textBannerSize.setCentered(false);
        textBannerSize.setColor(GuiColors.SETTINGS_TEXT, GuiColors.SETTINGS_TEXT_HIGHLIGHT);
        textBannerSize.setFrame(true);

        buttonAnchor = new GuiOptionButton(++id, mc.fontRenderer, width / 2 + 70, height / 2 - 32, 100);
        buttonAnchor.addOption(ConfigData.HUDAnchor.ScreenTop.name());
        buttonAnchor.addOption(ConfigData.HUDAnchor.ScreenTopRight.name());
        buttonAnchor.addOption(ConfigData.HUDAnchor.ScreenRight.name());
        buttonAnchor.addOption(ConfigData.HUDAnchor.ScreenBottomRight.name());
        buttonAnchor.addOption(ConfigData.HUDAnchor.ScreenBottom.name());
        buttonAnchor.addOption(ConfigData.HUDAnchor.ScreenBottomLeft.name());
        buttonAnchor.addOption(ConfigData.HUDAnchor.ScreenLeft.name());
        buttonAnchor.addOption(ConfigData.HUDAnchor.ScreenTopLeft.name());
        buttonAnchor.addOption(ConfigData.HUDAnchor.Minimap.name());
        buttonAnchor.addOption(ConfigData.HUDAnchor.MinimapHorizontal.name());
        buttonAnchor.addOption(ConfigData.HUDAnchor.MinimapVertical.name());
        buttonAnchor.setSelected(ConfigData.hud.anchor.ordinal());

        textPositionX = new TextBox(++id, mc.fontRenderer, width / 2 + 70, height / 2 - 16, 44, "");
        textPositionX.setText(String.valueOf(ConfigData.hud.position.x));
        textPositionX.setMaxStringLength(5);
        textPositionX.setResponder(this);
        textPositionX.setCentered(false);
        textPositionX.setColor(GuiColors.SETTINGS_TEXT, GuiColors.SETTINGS_TEXT_HIGHLIGHT);
        textPositionX.setFrame(true);

        textPositionY = new TextBox(++id, mc.fontRenderer, width / 2 + 125, height / 2 - 16, 45, "");
        textPositionY.setText(String.valueOf(ConfigData.hud.position.y));
        textPositionY.setMaxStringLength(5);
        textPositionY.setResponder(this);
        textPositionY.setCentered(false);
        textPositionY.setColor(GuiColors.SETTINGS_TEXT, GuiColors.SETTINGS_TEXT_HIGHLIGHT);
        textPositionY.setFrame(true);

        buttonAutoAdjustAnchor = new GuiOptionButton(++id, mc.fontRenderer, width / 2 + 70, height / 2, 100);
        buttonAutoAdjustAnchor.addOption("true");
        buttonAutoAdjustAnchor.addOption("false");
        buttonAutoAdjustAnchor.setSelected(ConfigData.hud.autoAdjustAnchor ? 0 : 1);

        buttonSnapToBorder = new GuiOptionButton(++id, mc.fontRenderer, width / 2 + 70, height / 2 + 16, 100);
        buttonSnapToBorder.addOption("true");
        buttonSnapToBorder.addOption("false");
        buttonSnapToBorder.setSelected(ConfigData.hud.snapToBorder ? 0 : 1);

        buttonDone = new GuiSettingsButton(++id, mc.fontRenderer, width / 2 - 50, height / 2 + 36, 100, "Done");

        buttonList.add(buttonSlot1);
        buttonList.add(buttonSlot2);
        buttonList.add(buttonSlot3);
        buttonList.add(buttonAnchor);
        buttonList.add(buttonAutoAdjustAnchor);
        buttonList.add(buttonSnapToBorder);
        buttonList.add(buttonDone);

        if (UIManager.INSTANCE.isMiniMapEnabled()) {
            minimap = UIManager.INSTANCE.getMiniMap();
        }

        resetLabels();
    }

    @Override
    public void updateScreen() {
        ++anchorLineColorTick;

        if (anchorLineColorTick >= 3) {
            anchorLineColorTick = 0;
            if (anchorLineColor == GuiColors.SETTINGS_ANCHOR_LIGHT) {
                anchorLineColor = GuiColors.SETTINGS_ANCHOR_DARK;
            } else {
                anchorLineColor = GuiColors.SETTINGS_ANCHOR_LIGHT;
            }
        }
    }

    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        if (minimap != null) {
            GlStateManager.matrixMode(GL11.GL_MODELVIEW);
            GlStateManager.pushMatrix();
            GlStateManager.matrixMode(GL11.GL_PROJECTION);
            GlStateManager.pushMatrix();
            minimap.drawMap(true);
            GlStateManager.matrixMode(GL11.GL_MODELVIEW);
            GlStateManager.popMatrix();
            GlStateManager.matrixMode(GL11.GL_PROJECTION);
            GlStateManager.popMatrix();
        }

        guiHUD.draw();
        drawAnchor();

        Gui.drawRect(width / 2 - 178, height / 2 - 40, width / 2 + 178, height / 2 + 60, GuiColors.SETTINGS_BG);

        textBannerSize.drawTextBox(mouseX, mouseY);
        textPositionX.drawTextBox(mouseX, mouseY);
        textPositionY.drawTextBox(mouseX, mouseY);

        for (GuiSimpleLabel label : labels) {
            label.drawLabel(mc, mouseX, mouseY);
        }

        super.drawScreen(mouseX, mouseY, partialTicks);

        for (GuiSimpleLabel label : labels) {
            if (label.isMouseOver()) {
                List<String> tooltip = labelTooltips.get(label);
                if (tooltip == null) {
                    continue;
                }

                GuiUtils.drawHoveringText(tooltip, mouseX, mouseY, width, height, 300, fontRenderer);
            }
        }
    }

    private void drawAnchor() {
        ScaledResolution scaledresolution = new ScaledResolution(mc);
        int factor = scaledresolution.getScaleFactor();
        GlStateManager.pushMatrix();
        GlStateManager.scale(1.0 / factor, 1.0 / factor, 1.0);

        int directionX = 0;
        int directionY = 0;
        int length = 25;
        ConfigData.Point anchor = ConfigData.getHUDAnchor(ConfigData.hud.anchor);

        if (anchor.x < mc.displayWidth / 2) {
            directionX = 1;
        } else if (anchor.x > mc.displayWidth / 2) {
            directionX = -1;
            --anchor.x;
        }

        if (anchor.y < mc.displayHeight / 2) {
            directionY = 1;
        } else if (anchor.y > mc.displayHeight / 2) {
            directionY = -1;
            --anchor.y;
        }

        if (ConfigData.hud.anchor == ConfigData.HUDAnchor.Minimap) {
            directionX = -directionX;
            directionY = -directionY;

            if (directionX == 1) {
                ++anchor.x;
            }
            if (directionY == 1) {
                ++anchor.y;
            }
        }

        if (directionX == 0) {
            drawHorizontalLine(anchor.x - length, anchor.x + length, anchor.y, anchorLineColor);
        } else {
            drawHorizontalLine(anchor.x, anchor.x + length * directionX, anchor.y, anchorLineColor);
        }

        if (directionY == 0) {
            drawVerticalLine(anchor.x, anchor.y - length, anchor.y + length, anchorLineColor);
        } else {
            drawVerticalLine(anchor.x, anchor.y, anchor.y + length * directionY, anchorLineColor);
        }

        GlStateManager.popMatrix();
    }

    @Override
    protected void mouseClicked(int x, int y, int btn) throws IOException {
        if (btn == 0) {
            boolean textClicked = false;
            textClicked |= textBannerSize.mouseClicked(x, y, btn);
            textClicked |= textPositionX.mouseClicked(x, y, btn);
            textClicked |= textPositionY.mouseClicked(x, y, btn);

            ScaledResolution scaledresolution = new ScaledResolution(mc);
            int factor = scaledresolution.getScaleFactor();
            int xScaled = x * factor;
            int yScaled = y * factor;

            if (!textClicked && guiHUD.isInside(xScaled, yScaled)) {
                grabbed = true;
                grabOffset.x = xScaled - positionHUD.x;
                grabOffset.y = yScaled - positionHUD.y;
            }
        }

        super.mouseClicked(x, y, btn);
    }

    @Override
    protected void mouseReleased(int x, int y, int state) {
        super.mouseReleased(x, y, state);
        grabbed = false;
    }

    @Override
    protected void mouseClickMove(int x, int y, int btn, long timeSinceLastClick) {
        super.mouseClickMove(x, y, btn, timeSinceLastClick);
        if (grabbed) {
            ScaledResolution scaledresolution = new ScaledResolution(mc);
            int factor = scaledresolution.getScaleFactor();
            x *= factor;
            y *= factor;

            positionHUD.x = x - grabOffset.x;
            positionHUD.y = y - grabOffset.y;

            ConfigData.Point anchorPoint = ConfigData.getHUDAnchor(ConfigData.hud.anchor);
            ConfigData.Point originPoint = ConfigData.getHUDOrigin(ConfigData.hud.anchor, guiHUD.getWidth(), guiHUD.getHeight());

            if (ConfigData.hud.autoAdjustAnchor) {
                ConfigData.HUDAnchor closestAnchor = null;
                int closestDistance = 99999;

                for (ConfigData.HUDAnchor anchor : ConfigData.HUDAnchor.values()) {
                    if ((anchor == ConfigData.HUDAnchor.Minimap || anchor == ConfigData.HUDAnchor.MinimapHorizontal
                            || anchor == ConfigData.HUDAnchor.MinimapVertical) && minimap == null) {
                        continue;
                    }

                    ConfigData.Point anchorP = ConfigData.getHUDAnchor(anchor);
                    ConfigData.Point originP = ConfigData.getHUDOrigin(anchor, guiHUD.getWidth(), guiHUD.getHeight());

                    int distance = Math.abs(anchorP.x - positionHUD.x - originP.x)
                            + Math.abs(anchorP.y - positionHUD.y - originP.y);
                    if (distance < closestDistance) {
                        closestDistance = distance;
                        closestAnchor = anchor;
                    }
                }
                if (closestAnchor != null && closestAnchor != ConfigData.hud.anchor) {
                    ConfigData.hud.anchor = closestAnchor;
                    anchorPoint = ConfigData.getHUDAnchor(ConfigData.hud.anchor);
                    originPoint = ConfigData.getHUDOrigin(ConfigData.hud.anchor, guiHUD.getWidth(), guiHUD.getHeight());
                    buttonAnchor.setSelected(ConfigData.hud.anchor.ordinal());
                }
            }

            ConfigData.Point snapOffset = new ConfigData.Point();
            if (ConfigData.hud.snapToBorder) {
                snapOffset.x = 16;
                snapOffset.y = 16;
                for (ConfigData.HUDAnchor anchor : ConfigData.HUDAnchor.values()) {
                    if (anchor == ConfigData.HUDAnchor.MinimapHorizontal || anchor == ConfigData.HUDAnchor.MinimapVertical) {
                        continue;
                    }

                    ConfigData.Point anchorP = ConfigData.getHUDAnchor(anchor);
                    ConfigData.Point originP = ConfigData.getHUDOrigin(anchor, guiHUD.getWidth(), guiHUD.getHeight());
                    int offsetX = positionHUD.x - anchorP.x + originP.x;
                    int offsetY = positionHUD.y - anchorP.y + originP.y;

                    if (anchor == ConfigData.HUDAnchor.Minimap) {
                        if (minimap == null) {
                            continue;
                        }

                        if (anchorP.x < mc.displayWidth / 2 && offsetX >= 16) {
                            continue;
                        } else if (anchorP.x > mc.displayWidth / 2 && offsetX <= -16) {
                            continue;
                        }

                        if (anchorP.y < mc.displayHeight / 2 && offsetY >= 16) {
                            continue;
                        } else if (anchorP.y > mc.displayHeight / 2 && offsetY <= -16) {
                            continue;
                        }
                    }

                    if (Math.abs(offsetX) < Math.abs(snapOffset.x)) {
                        snapOffset.x = offsetX;
                    }
                    if (Math.abs(offsetY) < Math.abs(snapOffset.y)) {
                        snapOffset.y = offsetY;
                    }
                }

                if (snapOffset.x == 16) {
                    snapOffset.x = 0;
                }
                if (snapOffset.y == 9999) {
                    snapOffset.y = 0;
                }
            }

            ConfigData.hud.position.x = positionHUD.x - anchorPoint.x + originPoint.x - snapOffset.x;
            ConfigData.hud.position.y = positionHUD.y - anchorPoint.y + originPoint.y - snapOffset.y;

            guiHUD.configUpdated();

            textPositionX.setText(String.valueOf(ConfigData.hud.position.x));
            textPositionY.setText(String.valueOf(ConfigData.hud.position.y));
            textPositionX.setColor(GuiColors.SETTINGS_TEXT, GuiColors.SETTINGS_TEXT_HIGHLIGHT);
            textPositionY.setColor(GuiColors.SETTINGS_TEXT, GuiColors.SETTINGS_TEXT_HIGHLIGHT);
        }
    }

    @Override
    protected void keyTyped(char typedChar, int keyCode) throws IOException {
        if (keyCode == Keyboard.KEY_ESCAPE) {
            Minecraft.getMinecraft().displayGuiScreen(parent);
        } else {
            super.keyTyped(typedChar, keyCode);
            textBannerSize.textboxKeyTyped(typedChar, keyCode);
            textPositionX.textboxKeyTyped(typedChar, keyCode);
            textPositionY.textboxKeyTyped(typedChar, keyCode);
        }
    }

    @Override
    protected void actionPerformed(GuiButton button) {
        if (button == buttonSlot1) {
            updateSlotsValidity();
            if (buttonSlot1.getColor() == GuiColors.SETTINGS_TEXT
                    || buttonSlot1.getColor() == GuiColors.SETTINGS_TEXT_HIGHLIGHT) {
                ConfigData.hud.slot1 = ConfigData.HUDSlot.values()[buttonSlot1.getSelected()];
                guiHUD.configUpdated();
                updatePosition();
            }
        } else if (button == buttonSlot2) {
            updateSlotsValidity();
            if (buttonSlot2.getColor() == GuiColors.SETTINGS_TEXT
                    || buttonSlot2.getColor() == GuiColors.SETTINGS_TEXT_HIGHLIGHT) {
                ConfigData.hud.slot2 = ConfigData.HUDSlot.values()[buttonSlot2.getSelected()];
                guiHUD.configUpdated();
                updatePosition();
            }
        } else if (button == buttonSlot3) {
            updateSlotsValidity();
            if (buttonSlot3.getColor() == GuiColors.SETTINGS_TEXT
                    || buttonSlot3.getColor() == GuiColors.SETTINGS_TEXT_HIGHLIGHT) {
                ConfigData.hud.slot3 = ConfigData.HUDSlot.values()[buttonSlot3.getSelected()];
                guiHUD.configUpdated();
                updatePosition();
            }
        } else if (button == buttonAnchor) {
            ConfigData.hud.anchor = ConfigData.HUDAnchor.values()[buttonAnchor.getSelected()];
            guiHUD.configUpdated();
            updatePosition();
        } else if (button == buttonAutoAdjustAnchor) {
            ConfigData.hud.autoAdjustAnchor = buttonAutoAdjustAnchor.getSelected() == 0;
            guiHUD.configUpdated();
        } else if (button == buttonSnapToBorder) {
            ConfigData.hud.snapToBorder = buttonSnapToBorder.getSelected() == 0;
            guiHUD.configUpdated();
        } else if (button == buttonDone) {
            Minecraft.getMinecraft().displayGuiScreen(parent);
        }
    }

    private void updateSlotsValidity() {
        ConfigData.HUDSlot slot1 = ConfigData.HUDSlot.values()[buttonSlot1.getSelected()];
        ConfigData.HUDSlot slot2 = ConfigData.HUDSlot.values()[buttonSlot2.getSelected()];
        ConfigData.HUDSlot slot3 = ConfigData.HUDSlot.values()[buttonSlot3.getSelected()];

        buttonSlot1.setColor(GuiColors.SETTINGS_TEXT, GuiColors.SETTINGS_TEXT_HIGHLIGHT);
        buttonSlot2.setColor(GuiColors.SETTINGS_TEXT, GuiColors.SETTINGS_TEXT_HIGHLIGHT);
        buttonSlot3.setColor(GuiColors.SETTINGS_TEXT, GuiColors.SETTINGS_TEXT_HIGHLIGHT);

        if (slot1 != ConfigData.HUDSlot.None && slot1 == slot2) {
            buttonSlot1.setColor(GuiColors.SETTINGS_TEXT_ERROR, GuiColors.SETTINGS_TEXT_ERROR_HIGHLIGHT);
            buttonSlot2.setColor(GuiColors.SETTINGS_TEXT_ERROR, GuiColors.SETTINGS_TEXT_ERROR_HIGHLIGHT);
        }

        if (slot1 != ConfigData.HUDSlot.None && slot1 == slot3) {
            buttonSlot1.setColor(GuiColors.SETTINGS_TEXT_ERROR, GuiColors.SETTINGS_TEXT_ERROR_HIGHLIGHT);
            buttonSlot3.setColor(GuiColors.SETTINGS_TEXT_ERROR, GuiColors.SETTINGS_TEXT_ERROR_HIGHLIGHT);
        }

        if (slot2 != ConfigData.HUDSlot.None && slot2 == slot3) {
            buttonSlot2.setColor(GuiColors.SETTINGS_TEXT_ERROR, GuiColors.SETTINGS_TEXT_ERROR_HIGHLIGHT);
            buttonSlot3.setColor(GuiColors.SETTINGS_TEXT_ERROR, GuiColors.SETTINGS_TEXT_ERROR_HIGHLIGHT);
        }
    }

    @Override
    public void onGuiClosed() {
        MapFrontiers.proxy.configUpdated();
    }

    @Override
    public boolean doesGuiPauseGame() {
        return true;
    }

    private void resetLabels() {
        labels.clear();

        addLabelWithTooltip(new GuiSimpleLabel(fontRenderer, width / 2 - 170, height / 2 - 30, GuiSimpleLabel.Align.Left, "slot1",
                GuiColors.SETTINGS_TEXT), ConfigData.getTooltip("hud.slot1"));
        addLabelWithTooltip(new GuiSimpleLabel(fontRenderer, width / 2 - 170, height / 2 - 14, GuiSimpleLabel.Align.Left, "slot2",
                GuiColors.SETTINGS_TEXT), ConfigData.getTooltip("hud.slot2"));
        addLabelWithTooltip(new GuiSimpleLabel(fontRenderer, width / 2 - 170, height / 2 + 2, GuiSimpleLabel.Align.Left, "slot3",
                GuiColors.SETTINGS_TEXT), ConfigData.getTooltip("hud.slot3"));
        addLabelWithTooltip(new GuiSimpleLabel(fontRenderer, width / 2 - 170, height / 2 + 18, GuiSimpleLabel.Align.Left,
                "bannerSize", GuiColors.SETTINGS_TEXT), ConfigData.getTooltip("hud.bannerSize"));
        addLabelWithTooltip(new GuiSimpleLabel(fontRenderer, width / 2 - 30, height / 2 - 30, GuiSimpleLabel.Align.Left, "anchor",
                GuiColors.SETTINGS_TEXT), ConfigData.getTooltip("hud.anchor"));
        List<String> positionTooltip = ConfigData.getTooltip("hud.position");
        positionTooltip.add(GuiColors.DEFAULT_CONFIG + "Default: [0 x 0]");
        addLabelWithTooltip(new GuiSimpleLabel(fontRenderer, width / 2 - 30, height / 2 - 14, GuiSimpleLabel.Align.Left,
                "position", GuiColors.SETTINGS_TEXT), positionTooltip);
        labels.add(new GuiSimpleLabel(fontRenderer, width / 2 + 120, height / 2 - 14, GuiSimpleLabel.Align.Center, "x",
                GuiColors.SETTINGS_TEXT_DARK));
        addLabelWithTooltip(new GuiSimpleLabel(fontRenderer, width / 2 - 30, height / 2 + 2, GuiSimpleLabel.Align.Left,
                "autoAdjustAnchor", GuiColors.SETTINGS_TEXT), ConfigData.getTooltip("hud.autoAdjustAnchor"));
        addLabelWithTooltip(new GuiSimpleLabel(fontRenderer, width / 2 - 30, height / 2 + 18, GuiSimpleLabel.Align.Left,
                "snapToBorder", GuiColors.SETTINGS_TEXT), ConfigData.getTooltip("hud.snapToBorder"));
    }

    private void addLabelWithTooltip(GuiSimpleLabel label, @Nullable List<String> tooltip) {
        labels.add(label);
        if (tooltip != null) {
            labelTooltips.put(label, tooltip);
        }
    }

    @Override
    public void updatedValue(int id, String value) {
    }

    @Override
    public void lostFocus(int id, String value) {
        if (textBannerSize.getId() == id) {
            if (StringUtils.isBlank(value)) {
                textBannerSize.setColor(GuiColors.SETTINGS_TEXT, GuiColors.SETTINGS_TEXT_HIGHLIGHT);
                textBannerSize.setText(ConfigData.getDefault("hud.bannerSize"));
                ConfigData.hud.bannerSize = Integer.parseInt(textBannerSize.getText());
                guiHUD.configUpdated();
                updatePosition();
            } else {
                try {
                    textBannerSize.setColor(GuiColors.SETTINGS_TEXT_ERROR, GuiColors.SETTINGS_TEXT_ERROR_HIGHLIGHT);
                    Integer size = Integer.valueOf(value);
                    if (ConfigData.isInRange("hud.bannerSize", size)) {
                        textBannerSize.setColor(GuiColors.SETTINGS_TEXT, GuiColors.SETTINGS_TEXT_HIGHLIGHT);
                        ConfigData.hud.bannerSize = size;
                        guiHUD.configUpdated();
                        updatePosition();
                    }
                } catch (Exception e) {
                }
            }
        } else if (textPositionX.getId() == id) {
            if (StringUtils.isBlank(value)) {
                textPositionX.setColor(GuiColors.SETTINGS_TEXT, GuiColors.SETTINGS_TEXT_HIGHLIGHT);
                textPositionX.setText(ConfigData.getDefault("hud.position.x"));
                ConfigData.hud.position.x = Integer.parseInt(textPositionX.getText());
                guiHUD.configUpdated();
                updatePosition();
            } else {
                try {
                    textPositionX.setColor(GuiColors.SETTINGS_TEXT_ERROR, GuiColors.SETTINGS_TEXT_ERROR_HIGHLIGHT);
                    Integer x = Integer.valueOf(value);
                    ConfigData.hud.position.x = x;
                    guiHUD.configUpdated();
                    textPositionX.setColor(GuiColors.SETTINGS_TEXT, GuiColors.SETTINGS_TEXT_HIGHLIGHT);
                } catch (Exception e) {
                }
            }
        } else if (textPositionY.getId() == id) {
            if (StringUtils.isBlank(value)) {
                textPositionY.setColor(GuiColors.SETTINGS_TEXT, GuiColors.SETTINGS_TEXT_HIGHLIGHT);
                textPositionY.setText(ConfigData.getDefault("hud.position.y"));
                ConfigData.hud.position.y = Integer.parseInt(textPositionY.getText());
                guiHUD.configUpdated();
                updatePosition();
            } else {
                try {
                    textPositionY.setColor(GuiColors.SETTINGS_TEXT_ERROR, GuiColors.SETTINGS_TEXT_ERROR_HIGHLIGHT);
                    Integer y = Integer.valueOf(value);
                    ConfigData.hud.position.y = y;
                    guiHUD.configUpdated();
                    textPositionY.setColor(GuiColors.SETTINGS_TEXT, GuiColors.SETTINGS_TEXT_HIGHLIGHT);
                } catch (Exception e) {
                }
            }
        }
    }

    private void updatePosition() {
        ConfigData.Point anchorPoint = ConfigData.getHUDAnchor(ConfigData.hud.anchor);
        ConfigData.Point originPoint = ConfigData.getHUDOrigin(ConfigData.hud.anchor, guiHUD.getWidth(), guiHUD.getHeight());
        positionHUD.x = ConfigData.hud.position.x + anchorPoint.x - originPoint.x;
        positionHUD.y = ConfigData.hud.position.y + anchorPoint.y - originPoint.y;
    }
}
