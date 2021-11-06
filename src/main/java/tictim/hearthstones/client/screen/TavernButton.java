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
import net.minecraftforge.fmlclient.gui.GuiUtils;
import tictim.hearthstones.client.Rendering;
import tictim.hearthstones.hearthstone.HearthingGemHearthstone;
import tictim.hearthstones.net.ModNet;
import tictim.hearthstones.net.TavernMemoryOperationMsg;
import tictim.hearthstones.tavern.Tavern;
import tictim.hearthstones.tavern.TavernTextFormat;
import tictim.hearthstones.tavern.TavernType;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

final class TavernButton extends Button{
	private static final List<Component> howto1 = Collections.singletonList(new TranslatableComponent("info.hearthstones.screen.howtouse.1"));
	private static final List<Component> howto2 = Arrays.asList(
			new TranslatableComponent("info.hearthstones.screen.howtouse.1"),
			new TranslatableComponent("info.hearthstones.screen.howtouse.2"));

	public static final int BASE_WIDTH = 179;
	public static final int BASE_HEIGHT = 20;
	public static final int WIDTH = BASE_WIDTH*2;
	public static final int HEIGHT = BASE_HEIGHT*2;

	private final HearthstoneScreen screen;
	private final Tavern tavern;
	private final boolean isHome;
	private final boolean isFromGlobal;
	private final Map<TavernProperty, PropertyWidget> properties = new HashMap<>();

	public TavernButton(HearthstoneScreen screen, int x, int y, Tavern tavern, boolean isHome, boolean isFromGlobal){
		super(x, y, WIDTH, HEIGHT, TextComponent.EMPTY, button -> {});
		this.screen = screen;
		this.tavern = tavern;
		this.isHome = isHome;
		this.isFromGlobal = isFromGlobal;

		int properties = 1;
		TavernProperty[] values = TavernProperty.values();
		for(int i1 = values.length-1; i1>=0; i1--){
			TavernProperty property = values[i1];
			if(property.matches(this))
				this.properties.put(property, new PropertyWidget(this.x+WIDTH-8-16*(properties++), this.y+HEIGHT-22, property));
		}
	}

	@Override public void renderButton(PoseStack pose, int mouseX, int mouseY, float partialTicks){
		if(!this.visible) return;
		pose.pushPose();
		RenderSystem.setShaderColor(1, 1, 1, 1);
		RenderSystem.enableBlend();
		RenderSystem.blendFunc(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA);
		pose.translate(x, y, 0);
		Rendering.renderTavernUIBase(pose, tavern.type(), tavern.pos().equals(screen.playerMemory.getSelectedPos()));
		pose.translate(6*2, 2, 0);
		Rendering.renderTavernAccess(pose, tavern.access());
		pose.popPose();

		for(PropertyWidget p : properties.values()){
			RenderSystem.setShaderTexture(0, HearthstoneScreen.ICONS);
			blit(pose, p.x, p.y, 7*2*p.property.ordinal(), 0, 7*2, 7*2);
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
				ModNet.CHANNEL.sendToServer(new TavernMemoryOperationMsg(tavern.pos(), TavernMemoryOperationMsg.SELECT));
				screen.playerMemory.select(tavern.pos());
			}else if(!isFromGlobal){
				ModNet.CHANNEL.sendToServer(new TavernMemoryOperationMsg(tavern.pos(), TavernMemoryOperationMsg.DELETE));
				screen.playerMemory.delete(tavern.pos());
				screen.refreshTavernButtons();
			}
			return true;
		}
		return false;
	}

	@Override protected boolean isValidClickButton(int button){
		return button==0||button==1;
	}

	public void renderToolTip(PoseStack pose, int mouseX, int mouseY){
		if(isHovered()){
			for(PropertyWidget p : this.properties.values()){
				if(p.x<=mouseX&&p.x+14>mouseX&&p.y<=mouseY&&p.y+14>mouseY){
					GuiUtils.drawHoveringText(pose, p.property.getTooltip(), mouseX, mouseY, screen.width, screen.height, -1, Minecraft.getInstance().font);
					return;
				}
			}
			GuiUtils.drawHoveringText(pose, isFromGlobal ? howto1 : howto2, mouseX, mouseY, screen.width, screen.height, -1, Minecraft.getInstance().font);
		}
	}

	private record PropertyWidget(int x, int y, TavernProperty property){}

	private enum TavernProperty{
		MISSING, HOME, GLOBAL, SHABBY, TOO_FAR;

		private final List<Component> tooltip = Collections.singletonList(new TranslatableComponent("info.hearthstones.screen.property."+name().toLowerCase()));

		public boolean matches(TavernButton button){
			return switch(this){
				case MISSING -> button.tavern.isMissing();
				case HOME -> button.isHome;
				case GLOBAL -> button.isFromGlobal;
				case SHABBY -> button.tavern.type()==TavernType.SHABBY;
				case TOO_FAR -> button.screen.hearthingGem&&HearthingGemHearthstone.isTooFar(Objects.requireNonNull(Minecraft.getInstance().player), button.tavern.pos());
			};
		}

		public List<Component> getTooltip(){
			return tooltip;
		}
	}
}
