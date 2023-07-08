package co.neeve.nae2.item.patternmultiplier.client;

import appeng.client.gui.AEBaseGui;
import appeng.client.gui.widgets.ITooltip;
import appeng.core.localization.ButtonToolTips;
import appeng.core.localization.GuiText;
import co.neeve.nae2.NAE2;
import co.neeve.nae2.Tags;
import co.neeve.nae2.item.patternmultiplier.ObjPatternMultiplier;
import co.neeve.nae2.item.patternmultiplier.container.ContainerPatternMultiplier;
import co.neeve.nae2.item.patternmultiplier.net.PatternMultiplierPacket;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.resources.I18n;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;

@SideOnly(Side.CLIENT)
public class GuiPatternMultiplier extends AEBaseGui {
    // GUI texture
    private final ResourceLocation loc = new ResourceLocation(Tags.MODID, "textures/gui/pattern_multiplier.png");
    private final ObjPatternMultiplier te;

    // Constructor
    public GuiPatternMultiplier(InventoryPlayer inventoryPlayer, ObjPatternMultiplier te) {
        super(new ContainerPatternMultiplier(inventoryPlayer, te));
        this.ySize = 189 + 18;
        this.xSize = 211;
        this.te = te;
    }

    // Handles button actions
    @Override
    protected void actionPerformed(@NotNull GuiButton btn) throws IOException {
        super.actionPerformed(btn);
        // Check which button was pressed and send corresponding packet
        NAE2.network.sendToServer(new PatternMultiplierPacket(btn.id));
    }

    // Initializes the GUI
    @Override
    public void initGui() {
        super.initGui();

        // Calculate start position for buttons
        int start = 7 + this.guiLeft;

        // Add buttons to the GUI
        this.buttonList.add(new TooltipButton(0, start, this.guiTop + 76 + 18, "*2",
                ButtonToolTips.MultiplyByTwo, ButtonToolTips.MultiplyByTwoDesc));
        this.buttonList.add(new TooltipButton(1, start + 23, this.guiTop + 76 + 18, "*3",
                ButtonToolTips.MultiplyByThree, ButtonToolTips.MultiplyByThreeDesc));
        this.buttonList.add(new TooltipButton(2, start + 46, this.guiTop + 76 + 18, "+1",
                ButtonToolTips.IncreaseByOne, ButtonToolTips.IncreaseByOneDesc));

        this.buttonList.add(new TooltipButton(3, start, this.guiTop + 76 + 18, "/2",
                ButtonToolTips.DivideByTwo, ButtonToolTips.DivideByTwoDesc));
        this.buttonList.add(new TooltipButton(4, start + 23, this.guiTop + 76 + 18, "/3",
                ButtonToolTips.DivideByThree, ButtonToolTips.DivideByThreeDesc));
        this.buttonList.add(new TooltipButton(5, start + 46, this.guiTop + 76 + 18, "-1",
                ButtonToolTips.DecreaseByOne, ButtonToolTips.DecreaseByOneDesc));

        GuiButton unencode;
        this.buttonList.add(unencode = new TooltipButton(6, start + 176 - 60 - 15, this.guiTop + 76 + 18,
                "nae2.pattern_multiplier.unencode",
                "nae2.pattern_multiplier.unencode",
                "nae2.pattern_multiplier.unencode.desc"));
        unencode.width = 60;

    }

    // Draws the screen
    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        super.drawScreen(mouseX, mouseY, partialTicks);

        boolean shiftDown = isShiftKeyDown();
        this.buttonList.subList(0, 3).forEach(but -> but.visible = !shiftDown);
        this.buttonList.subList(3, 6).forEach(but -> but.visible = shiftDown);
    }

    // Draws the foreground
    public void drawFG(int offsetX, int offsetY, int mouseX, int mouseY) {
        StringBuilder sb = new StringBuilder().append(I18n.format("item.nae2.pattern_multiplier.name"));
        if (this.te.isBoundToInterface()) {
            sb.append(" (");
            sb.append(GuiText.Interface.getLocal());
            sb.append(")");
        }
        this.fontRenderer.drawString(sb.toString(), 8, 6, 4210752);

        this.fontRenderer.drawString(GuiText.inventory.getLocal(), 8, this.ySize - 96 + 3, 4210752);
    }

    // Draws the background
    public void drawBG(int offsetX, int offsetY, int mouseX, int mouseY) {
        this.mc.getTextureManager().bindTexture(loc);
        this.drawTexturedModalRect(offsetX, offsetY, 0, 0, 177, this.ySize);

        // Draw pattern rows.
        int installedCapacityUpgrades = this.te.getInstalledCapacityUpgrades();
        for (int u = 0; u < installedCapacityUpgrades; u++) {
            this.drawTexturedModalRect(offsetX + 8, offsetY + 37 + u * 18, 8, 19, 18 * 9 - 1, 18 - 1);
        }

        // Draw the upgrade inventory depending on the size.
        int upgradeInventorySize = this.te.getUpgradeInventory().getSlots();
        if (upgradeInventorySize > 0) {
            this.drawTexturedModalRect(offsetX + 180, offsetY, 180, 0, 32, 32);
            for (int u = 1; u < upgradeInventorySize; u++) {
                this.drawTexturedModalRect(offsetX + 180, offsetY + 8 + u * 18 - 1, 180, 7, 32, 30);
            }

        }

        // Draw the network tool if present.
        if (this.hasToolbox()) {
            this.drawTexturedModalRect(offsetX + 178, offsetY + this.ySize - 90, 178, this.ySize - 90, 68, 68);
        }
    }

    protected boolean hasToolbox() {
        return ((ContainerPatternMultiplier) this.inventorySlots).hasToolbox();
    }

    public static class TooltipButton extends GuiButton implements ITooltip {
        private final String title;
        private final String hint;


        public TooltipButton(int buttonId, int x, int y, String buttonText, ButtonToolTips title, ButtonToolTips hint) {
            this(buttonId, x, y, buttonText, title.getUnlocalized(), hint.getUnlocalized());
        }

        public TooltipButton(int buttonId, int x, int y, String buttonText, String title, String hint) {
            super(buttonId, x, y, I18n.format(buttonText));
            this.width = 18;
            this.height = 18;
            this.title = title;
            this.hint = hint;
        }

        @Override
        public String getMessage() {
            return I18n.format(this.title) + "\n" + I18n.format(this.hint);
        }

        @Override
        public int xPos() {
            return this.x;
        }

        @Override
        public int yPos() {
            return this.y;
        }

        @Override
        public int getWidth() {
            return this.width;
        }

        @Override
        public int getHeight() {
            return this.height;
        }

        @Override
        public boolean isVisible() {
            return this.visible;
        }
    }
}
