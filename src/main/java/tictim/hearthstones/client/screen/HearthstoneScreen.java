package tictim.hearthstones.client.screen;

import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.platform.InputConstants;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.chat.NarratorChatListener;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.resources.language.I18n;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraftforge.fmlclient.gui.GuiUtils;
import tictim.hearthstones.Hearthstones;
import tictim.hearthstones.client.render.TavernRenderHelper;
import tictim.hearthstones.client.utils.TavernSign;
import tictim.hearthstones.config.ModCfg;
import tictim.hearthstones.contents.ModItems;
import tictim.hearthstones.data.GlobalTavernMemory;
import tictim.hearthstones.data.PlayerTavernMemory;
import tictim.hearthstones.data.TavernPos;
import tictim.hearthstones.data.TavernRecord;
import tictim.hearthstones.net.ModNet;
import tictim.hearthstones.net.TavernMemoryOperation;
import tictim.hearthstones.utils.TavernType;

import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeSet;
import java.util.function.Predicate;

public class HearthstoneScreen extends AbstractScreen{
	public static final ResourceLocation ICONS = new ResourceLocation(Hearthstones.MODID, "textures/screen/icons.png");

	private final PlayerTavernMemory memory;
	private int yOffset = 0;
	private double yOffsetFloat = 0;
	private double yOffsetDest = 0;
	private int heightCache;
	public boolean flagResetButtons = false;

	public HearthstoneScreen(PlayerTavernMemory cap){
		super(NarratorChatListener.NO_TITLE);
		this.memory = cap;
	}

	@Override
	protected void onInit(){
		resetTavernButtons();
	}
	@Override
	protected void onResize(){
		this.xSize = this.width;
		this.ySize = this.height;
	}

	@SuppressWarnings("ConstantConditions")
	public void resetTavernButtons(){
		this.renderables.removeIf(b -> b instanceof TavernButton||b instanceof TavernButton.PropertyButton);
		this.children().removeIf(b -> b instanceof TavernButton||b instanceof TavernButton.PropertyButton);
		ResourceKey<Level> dim = minecraft.level.dimension();
		TreeSet<TavernRecord> set = new TreeSet<>((e1, e2) -> {
			int i;
			// home
			TavernRecord homeTavern = memory.getHomeTavern();
			i = Boolean.compare(homeTavern==e2, homeTavern==e1);
			if(i!=0) return i;
			ResourceLocation d1 = e1.getDimensionType();
			ResourceLocation d2 = e2.getDimensionType();
			if(d1==d2&&dim.location().equals(d1)){// #3(Optional) distance
				double s1 = minecraft.player.distanceToSqr(e1.getPos().getX()+0.5, e1.getPos().getY()+0.5, e1.getPos().getZ()+0.5);
				double s2 = minecraft.player.distanceToSqr(e2.getPos().getX()+0.5, e2.getPos().getY()+0.5, e2.getPos().getZ()+0.5);
				i = Double.compare(s1, s2);
				if(i!=0) return i;
			}
			return e1.compareTo(e2);
		});
		set.addAll(memory.view().values());
		heightCache = 0;
		createButtons(set, false);
		set.clear();
		for(TavernRecord e : GlobalTavernMemory.get().memories()) if(!memory.has(e.getTavernPos())) set.add(e);
		createButtons(set, true);
	}

	private void createButtons(Collection<TavernRecord> taverns, boolean isFromGlobal){
		for(TavernRecord e : taverns){
			TavernButton button = this.addWidget(new TavernButton(getLeft(), getTop()+heightCache, e, isFromGlobal));
			button.generatePropertyButtons();
			heightCache += TavernButton.HEIGHT+(7*2)+6;
		}
	}

	@Override
	public void render(PoseStack pose, int mouseX, int mouseY, float partialTicks){
		if(flagResetButtons) resetTavernButtons();

		int mouseY2 = mouseY+yOffset;
		this.renderBackground(pose);
		pose.pushPose();
		pose.translate(0, -yOffset, 0);
		super.render(pose, mouseX, mouseY2, partialTicks);
		pose.popPose();
		super.drawTooltip(pose, mouseX, mouseY);
		this.yOffsetFloat = Mth.lerp(0.4, yOffsetFloat, this.yOffsetDest = Mth.clamp(yOffsetDest, 0, Math.max(0, heightCache-width/2)));
		this.yOffset = (int)Math.round(yOffsetFloat);
	}

	@Override
	protected void renderLabels(PoseStack pose, int mouseX, int mouseY){
		if(this.renderables.isEmpty()) drawCenteredString(pose, font, I18n.get("info.hearthstones.screen.empty"), this.xSize/2, this.ySize/2-5, 0xFFFFFF);
	}

	@Override
	protected void drawTooltip(PoseStack pose, int mouseX, int mouseY){}

	@Override
	public boolean mouseClicked(double mouseX, double mouseY, int button){
		return super.mouseClicked(mouseX, mouseY+yOffset, button);
	}
	@Override
	public boolean mouseReleased(double mouseX, double mouseY, int button){
		return super.mouseReleased(mouseX, mouseY+yOffset, button);
	}
	@Override
	public boolean mouseDragged(double mouseX, double mouseY, int mouseButton, double dragX, double dragY){
		return super.mouseDragged(mouseX, mouseY+yOffset, mouseButton, dragX, dragY);
	}
	@Override
	public boolean mouseScrolled(double mouseX, double mouseY, double scroll){
		this.yOffsetDest -= scroll*40;
		return true;
	}

	@Override
	public boolean keyPressed(int keyCode, int scanCode, int modifier){
		if(super.keyPressed(keyCode, scanCode, modifier)) return true;
		InputConstants.Key mouseKey = InputConstants.getKey(keyCode, scanCode);
		//noinspection ConstantConditions
		if(this.minecraft.options.keyInventory.getKey().equals(mouseKey)){
			onClose();
			return true;
		}else return false;
	}

	private static final List<Component> howto1 = Collections.singletonList(new TranslatableComponent("info.hearthstones.screen.howtouse.1"));
	private static final List<Component> howto2 = Arrays.asList(
			new TranslatableComponent("info.hearthstones.screen.howtouse.1"),
			new TranslatableComponent("info.hearthstones.screen.howtouse.2"));

	private class TavernButton extends Button{
		public static final int BASE_WIDTH = 179;
		public static final int BASE_HEIGHT = 20;
		public static final int WIDTH = BASE_WIDTH*2;
		public static final int HEIGHT = BASE_HEIGHT*2;

		private final TavernRecord tavern;
		private final TavernSign sign;
		private final boolean isFromGlobal;
		private final Map<TavernProperty, PropertyButton> properties = new HashMap<>();

		private TavernButton(int x, int y, TavernRecord tavern, boolean isFromGlobal){
			super(x, y, WIDTH, HEIGHT, TextComponent.EMPTY, button -> {});
			this.tavern = tavern;
			this.sign = TavernSign.of(tavern);
			this.isFromGlobal = isFromGlobal;
		}

		private HearthstoneScreen screen(){
			return HearthstoneScreen.this;
		}

		@Override
		public void renderButton(PoseStack matrixStack, int mouseX, int mouseY, float partialTicks){
			if(this.visible){
				matrixStack.pushPose();
				RenderSystem.setShaderColor(1, 1, 1, 1);
				RenderSystem.enableBlend();
				RenderSystem.blendFunc(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA);
				matrixStack.translate(x, y, 0);
				TavernRenderHelper.renderTavernUIBase(matrixStack, tavern.getTavernType(), memory.getSelectedTavern()==tavern);
				matrixStack.translate(6*2, 2, 0);
				TavernRenderHelper.renderAccess(matrixStack, tavern.getOwner().getAccessModifier());
				matrixStack.popPose();

				font.drawShadow(matrixStack, sign.nameAndDistance(), x+25*2, y+9*2-1, 0xFFFFFF);
				String ownerText = sign.owner();
				int ownerWidth = font.width(ownerText);
				font.drawShadow(matrixStack, sign.owner(), x+25*2, y+14*2-1, 0xFFFFFF);
				font.draw(matrixStack, sign.position(), x+25*2+ownerWidth+font.width(" "), y+14*2-1, 0xFFFFFF);
			}
		}


		@Override
		public boolean mouseClicked(double mouseX, double mouseY, int button){
			if(this.active&&this.visible&&this.isValidClickButton(button)&&this.clicked(mouseX, mouseY)){
				this.playDownSound(Minecraft.getInstance().getSoundManager());
				execute(button==0 ? TavernMemoryOperation.SELECT : TavernMemoryOperation.DELETE);
				return true;
			}
			return false;
		}

		@Override
		protected boolean isValidClickButton(int button){
			return button==0||button==1;
		}

		private void execute(byte operation){
			TavernPos pos = tavern.getTavernPos();
			ModNet.CHANNEL.sendToServer(new TavernMemoryOperation(pos, operation));
			switch(operation){
				case TavernMemoryOperation.SELECT -> memory.select(pos);
				case TavernMemoryOperation.DELETE -> memory.delete(pos);
			}
		}


		public void renderToolTip(PoseStack matrixStack, int mouseX, int mouseY){
			if(isHovered()){
				for(PropertyButton propertyButton : this.properties.values()) if(propertyButton.isHovered()) return;
				GuiUtils.drawHoveringText(matrixStack, isFromGlobal ? howto1 : howto2, mouseX, mouseY, HearthstoneScreen.this.width, HearthstoneScreen.this.height, -1, font);
			}
		}

		public void generatePropertyButtons(){
			int i = 1;
			TavernProperty[] values = TavernProperty.values();
			for(int i1 = values.length-1; i1>=0; i1--){
				TavernProperty property = values[i1];
				if(property.matches(this))
					this.properties.put(property, addWidget(new PropertyButton(this.x+WIDTH-8-16*(i++), this.y+HEIGHT-22, property)));
			}
		}

		private class PropertyButton extends Button{
			private final TavernProperty property;

			public PropertyButton(int widthIn, int heightIn, TavernProperty property){
				super(widthIn, heightIn, 14, 14, TextComponent.EMPTY, e -> {});
				this.property = property;
			}

			@Override
			protected boolean isValidClickButton(int p_isValidClickButton_1_){
				return false;
			}

			@Override
			public void renderButton(PoseStack matrixStack, int p_renderButton_1_, int p_renderButton_2_, float p_renderButton_3_){
				RenderSystem.setShaderTexture(0, HearthstoneScreen.ICONS);
				blit(matrixStack, this.x, this.y, 7*2*property.ordinal(), 0, 7*2, 7*2);
			}

			@Override
			public void renderToolTip(PoseStack matrixStack, int mouseX, int mouseY){
				if(isHovered) GuiUtils.drawHoveringText(matrixStack, property.getTooltip(), mouseX, mouseY, HearthstoneScreen.this.width, HearthstoneScreen.this.height, -1, font);
			}
		}
	}

	private enum TavernProperty{
		MISSING(button -> button.tavern.isMissing()),
		HOME(button -> button.screen().memory.getHomeTavern()==button.tavern),
		GLOBAL(button -> button.isFromGlobal),
		SHABBY(button -> button.tavern.getTavernType()==TavernType.SHABBY),
		TOO_FAR(button -> {
			Player player = Minecraft.getInstance().player;
			return (player.getItemInHand(InteractionHand.MAIN_HAND).getItem()==ModItems.HEARTHING_GEM.get()||player.getItemInHand(InteractionHand.OFF_HAND).getItem()==ModItems.HEARTHING_GEM.get())&&
					(!button.tavern.getDimensionType().equals(player.level.dimension().location())||
							Math.sqrt(player.distanceToSqr(button.tavern.getPos().getX(), button.tavern.getPos().getY(), button.tavern.getPos().getZ()))>ModCfg.hearthingGem.travelDistanceThreshold());
		});

		private final Predicate<TavernButton> matches;
		private final List<Component> tooltip = Collections.singletonList(
				new TranslatableComponent("info.hearthstones.screen.property."+name().toLowerCase()));

		TavernProperty(Predicate<TavernButton> matches){
			this.matches = matches;
		}

		private boolean matches(TavernButton button){
			return matches.test(button);
		}

		public List<Component> getTooltip(){
			return tooltip;
		}
	}
}
