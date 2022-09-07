package tictim.hearthstones.client.gui;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.resources.I18n;
import net.minecraft.util.ResourceLocation;
import tictim.hearthstones.Hearthstones;
import tictim.hearthstones.client.Rendering;
import tictim.hearthstones.tavern.Tavern;
import tictim.hearthstones.tavern.TavernTextFormat;

import java.util.ArrayList;
import java.util.EnumSet;
import java.util.List;
import java.util.Set;

public final class TavernButton extends GuiButton implements AbstractScreen.TooltipComponent{
	public static final ResourceLocation ICONS = new ResourceLocation(Hearthstones.MODID, "textures/screen/icons.png");

	public static final int BASE_WIDTH = 179;
	public static final int BASE_HEIGHT = 20;
	public static final int WIDTH = BASE_WIDTH*2;
	public static final int HEIGHT = BASE_HEIGHT*2;

	public final Set<TavernProperty> properties = EnumSet.noneOf(TavernProperty.class);
	public final TavernMemoryScreen screen;
	public final Tavern tavern;

	public boolean selected;
	public boolean canSelect;
	public boolean canDelete;

	public TavernButton(int buttonId, TavernMemoryScreen screen, Tavern tavern){
		super(buttonId, 0, 0, WIDTH, HEIGHT, "");
		this.screen = screen;
		this.tavern = tavern;
	}

	@Override public void drawButton(Minecraft mc, int mouseX, int mouseY, float partialTicks){
		if(!this.visible) return;
		this.hovered = mouseX>=this.x&&mouseY>=this.y&&mouseX<this.x+this.width&&mouseY<this.y+this.height;

		GlStateManager.pushMatrix();
		GlStateManager.color(1, 1, 1, 1);
		GlStateManager.enableBlend();
		GlStateManager.blendFunc(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA);
		GlStateManager.translate(x, y, 0);
		Rendering.renderTavernUIBase(tavern.type(), selected);
		GlStateManager.translate(6*2, 2, 0);
		Rendering.renderTavernAccess(tavern.access());
		GlStateManager.popMatrix();

		int i = 1;
		for(TavernProperty p : properties){
			mc.getTextureManager().bindTexture(ICONS);
			drawTexturedModalRect(getPropertyWidgetX(i++), getPropertyWidgetY(), 7*2*p.ordinal(), 0, 7*2, 7*2);
		}

		FontRenderer font = mc.fontRenderer;
		font.drawStringWithShadow(TavernTextFormat.nameAndDistance(tavern, Minecraft.getMinecraft().player), x+25*2, y+9*2-1, 0xFFFFFF);
		String ownerText = TavernTextFormat.owner(tavern);
		int ownerWidth = font.getStringWidth(ownerText);
		font.drawStringWithShadow(ownerText, x+25*2, y+14*2-1, 0xFFFFFF);
		font.drawString(TavernTextFormat.position(tavern), x+25*2+ownerWidth+font.getStringWidth(" "), y+14*2-1, 0xFFFFFF);
	}

	@Override public boolean mousePressed(Minecraft mc, int mouseX, int mouseY){
		return super.mousePressed(mc, mouseX, mouseY)&&canSelect;
	}

	public boolean rightMousePressed(Minecraft mc, int mouseX, int mouseY){
		return super.mousePressed(mc, mouseX, mouseY)&&canDelete;
	}

	@Override public void mouseReleased(int mouseX, int mouseY){
		if(mouseX<this.x||mouseY<this.y||mouseX>=this.x+this.width||mouseY>=this.y+this.height) return;
		if(canSelect) screen.select(tavern);
	}
	public void rightMouseReleased(int mouseX, int mouseY){
		if(mouseX<this.x||mouseY<this.y||mouseX>=this.x+this.width||mouseY>=this.y+this.height) return;
		if(canDelete) screen.askForDeletion(tavern);
	}

	@Override public void renderTooltip(int mouseX, int mouseY){
		if(!hovered) return;
		int i = 1;
		for(TavernProperty p : this.properties){
			int px = getPropertyWidgetX(i);
			int py = getPropertyWidgetY()-screen.getYOffset();
			if(px<=mouseX&&px+14>mouseX&&py<=mouseY&&py+14>mouseY){
				screen.drawHoveringText(p.getTooltip(), mouseX, mouseY);
				return;
			}
			i++;
		}

		List<String> strs = new ArrayList<>();
		if(canDelete) strs.add(I18n.format("info.hearthstones.screen.help.remove"));
		if(canSelect) strs.add(I18n.format("info.hearthstones.screen.help.select"));
		screen.drawHoveringText(strs, mouseX, mouseY);
	}


	private int getPropertyWidgetX(int index){
		return this.x+WIDTH-8-16*(index);
	}
	private int getPropertyWidgetY(){
		return this.y+HEIGHT-22;
	}

	public enum TavernProperty{
		MISSING, HOME, GLOBAL, SHABBY, TOO_FAR;

		public String getTooltip(){
			return I18n.format("info.hearthstones.screen.property."+name().toLowerCase());
		}
	}
}
