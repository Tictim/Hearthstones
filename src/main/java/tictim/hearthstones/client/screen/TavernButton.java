package tictim.hearthstones.client.screen;

import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.components.Button;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.resources.ResourceLocation;
import tictim.hearthstones.Hearthstones;
import tictim.hearthstones.client.Rendering;
import tictim.hearthstones.tavern.Tavern;
import tictim.hearthstones.tavern.TavernTextFormat;

import java.util.Collections;
import java.util.EnumSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

public final class TavernButton extends Button{
	public static final ResourceLocation ICONS = new ResourceLocation(Hearthstones.MODID, "textures/screen/icons.png");

	private static final Component HELP_SELECT = new TranslatableComponent("info.hearthstones.screen.help.select");
	private static final Component HELP_REMOVE = new TranslatableComponent("info.hearthstones.screen.help.remove");

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

	public TavernButton(TavernMemoryScreen screen, Tavern tavern){
		super(0, 0, WIDTH, HEIGHT, TextComponent.EMPTY, button -> {});
		this.screen = screen;
		this.tavern = tavern;
	}

	@Override public void renderButton(PoseStack pose, int mouseX, int mouseY, float partialTicks){
		if(!this.visible) return;
		pose.pushPose();
		RenderSystem.setShaderColor(1, 1, 1, 1);
		RenderSystem.enableBlend();
		RenderSystem.blendFunc(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA);
		pose.translate(x, y, 0);
		Rendering.renderTavernUIBase(pose, tavern.type(), selected);
		pose.translate(6*2, 2, 0);
		Rendering.renderTavernAccess(pose, tavern.access());
		pose.popPose();

		int i = 1;
		for(TavernProperty p : properties){
			RenderSystem.setShaderTexture(0, ICONS);
			blit(pose, getPropertyWidgetX(i++), getPropertyWidgetY(), 7*2*p.ordinal(), 0, 7*2, 7*2);
		}

		Font font = Minecraft.getInstance().font;
		//noinspection ConstantConditions
		font.drawShadow(pose, TavernTextFormat.nameAndDistance(tavern, Minecraft.getInstance().player), x+25*2, y+9*2-1, 0xFFFFFF);
		Component ownerText = TavernTextFormat.owner(tavern);
		int ownerWidth = font.width(ownerText);
		font.drawShadow(pose, ownerText, x+25*2, y+14*2-1, 0xFFFFFF);
		font.draw(pose, TavernTextFormat.position(tavern), x+25*2+ownerWidth+font.width(" "), y+14*2-1, 0xFFFFFF);
	}


	@Override public boolean mouseClicked(double mouseX, double mouseY, int button){
		if(this.active&&this.visible&&this.isValidClickButton(button)&&this.clicked(mouseX, mouseY)){
			this.playDownSound(Minecraft.getInstance().getSoundManager());
			if(button==0){
				if(canSelect) screen.select(tavern);
				else return false;
			}else{
				if(canDelete) screen.askForDeletion(tavern);
				else return false;
			}
			return true;
		}
		return false;
	}

	@Override protected boolean isValidClickButton(int button){
		return button==0||button==1;
	}

	@Override public void renderToolTip(PoseStack pose, int mouseX, int mouseY){
		if(!isHoveredOrFocused()) return;
		int i = 1;
		for(TavernProperty p : this.properties){
			int px = getPropertyWidgetX(i);
			int py = getPropertyWidgetY();
			if(px<=mouseX&&px+14>mouseX&&py<=mouseY&&py+14>mouseY){
				screen.renderTooltip(pose, p.getTooltip(), Optional.empty(), mouseX, mouseY);
				return;
			}
			i++;
		}
		screen.renderTooltip(pose, canDelete ?
				canSelect ? List.of(HELP_SELECT, HELP_REMOVE) : List.of(HELP_REMOVE) :
				canSelect ? List.of(HELP_SELECT) : List.of(),
				Optional.empty(), mouseX, mouseY);
	}

	private int getPropertyWidgetX(int index){
		return this.x+WIDTH-8-16*(index);
	}
	private int getPropertyWidgetY(){
		return this.y+HEIGHT-22;
	}

	public enum TavernProperty{
		MISSING, HOME, GLOBAL, SHABBY, TOO_FAR;

		private final List<Component> tooltip = Collections.singletonList(new TranslatableComponent("info.hearthstones.screen.property."+name().toLowerCase()));

		public List<Component> getTooltip(){
			return tooltip;
		}
	}
}
