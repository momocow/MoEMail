package me.momocow.moemail.client.gui;

import me.momocow.mobasic.client.gui.MoGuiScreen;
import me.momocow.moemail.client.MailNotificationHandler;
import me.momocow.moemail.reference.Reference;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.resources.I18n;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.MinecraftForge;

public class GuiMailNotification extends Gui
{
	private static final String unlocalizedName = Reference.MOD_ID + ".GuiMailNotification";
	private static final ResourceLocation NOTIFICATION_BG = new ResourceLocation("textures/gui/achievement/achievement_background.png");
	private static final ResourceLocation MAIL_ICON = new ResourceLocation(Reference.MOD_ID, "textures/gui/mail.png");
	private Minecraft mc;
	private MailNotificationHandler handler;
	
	private String notificationMsg;
	private String tooltip;
	
	private int mailCount = 0;
	private int width;
	private int height;
	private long notificationTime = 0L;
	private long appearancePeriod = 5000L;
	
	public GuiMailNotification(Minecraft mc)
    {
		this(mc, 1);
    }
	
	public GuiMailNotification(Minecraft mc, int mailCountIn)
    {
        this.mc = mc;
        this.mailCount = mailCountIn;
        this.notificationTime = Minecraft.getSystemTime();
        this.notificationMsg = I18n.format(getUnlocalizedName() + ".notification", this.mailCount);
        this.tooltip = "(" + I18n.format(getUnlocalizedName() + ".tooltip") + ")";
    }
	
	public static String getUnlocalizedName()
	{
		return "gui." + unlocalizedName;
	}
	
	public void updateScreen()
	{
		if(Minecraft.getSystemTime() - this.notificationTime <= this.appearancePeriod)
		{
			this.updateAchievementWindow();
		}
		else if(this.handler != null) //animate finishes
		{
			MinecraftForge.EVENT_BUS.unregister(handler);
		}
	}
	
	public void setHandler(MailNotificationHandler handler)
	{
		this.handler = handler;
	}
	
	private void updateAchievementWindowScale()
    {
        GlStateManager.viewport(0, 0, this.mc.displayWidth, this.mc.displayHeight);
        GlStateManager.matrixMode(5889);
        GlStateManager.loadIdentity();
        GlStateManager.matrixMode(5888);
        GlStateManager.loadIdentity();
        this.width = this.mc.displayWidth;
        this.height = this.mc.displayHeight;
        ScaledResolution scaledresolution = new ScaledResolution(this.mc);
        this.width = scaledresolution.getScaledWidth();
        this.height = scaledresolution.getScaledHeight();
        GlStateManager.clear(256);
        GlStateManager.matrixMode(5889);
        GlStateManager.loadIdentity();
        GlStateManager.ortho(0.0D, (double)this.width, (double)this.height, 0.0D, 1000.0D, 3000.0D);
        GlStateManager.matrixMode(5888);
        GlStateManager.loadIdentity();
        GlStateManager.translate(0.0F, 0.0F, -2000.0F);
    }

    public void updateAchievementWindow()
    {
        if (mailCount > 0 && this.notificationTime != 0L && Minecraft.getMinecraft().thePlayer != null)
        {
            double d0 = (double)(Minecraft.getSystemTime() - this.notificationTime) / 3000.0D;

            if (d0 > 0.5D)
            {
                d0 = 0.5D;
            }

            this.updateAchievementWindowScale();
            GlStateManager.disableDepth();
            GlStateManager.depthMask(false);
            double d1 = d0 * 2.0D;

            if (d1 > 1.0D)
            {
                d1 = 2.0D - d1;
            }

            d1 = d1 * 4.0D;
            d1 = 1.0D - d1;

            if (d1 < 0.0D)
            {
                d1 = 0.0D;
            }

            d1 = d1 * d1;
            d1 = d1 * d1;
            int i = this.width - 160;
            int j = 0 - (int)(d1 * 36.0D);
            GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
            GlStateManager.enableTexture2D();
            this.mc.getTextureManager().bindTexture(NOTIFICATION_BG);
            GlStateManager.disableLighting();
            this.drawTexturedModalRect(i, j, 96, 202, 160, 32);

            this.mc.fontRendererObj.drawString(this.notificationMsg, i + 30, j + 8, -1);
            this.mc.fontRendererObj.drawString(this.tooltip, i + 30, j + 18, this.mc.fontRendererObj.getColorCode('e'));

            MoGuiScreen.drawPartialScaleTexturedRect(MAIL_ICON, i + 8, j + 8, this.zLevel, 0, 0, 64, 64, 64, 64, 16, 16);
            
            GlStateManager.depthMask(true);
            GlStateManager.enableDepth();
        }
    }
}
