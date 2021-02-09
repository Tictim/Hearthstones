package tictim.hearthstones.client.screen;

import com.google.common.collect.ImmutableList;
import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.gui.widget.TextFieldWidget;
import net.minecraft.client.gui.widget.button.Button;
import net.minecraft.client.resources.I18n;
import net.minecraft.client.util.InputMappings;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.StringUtils;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraftforge.common.MinecraftForge;
import org.lwjgl.glfw.GLFW;
import tictim.hearthstones.client.render.TavernRenderHelper;
import tictim.hearthstones.client.utils.TavernSign;
import tictim.hearthstones.data.Owner;
import tictim.hearthstones.data.TavernPos;
import tictim.hearthstones.net.ModNet;
import tictim.hearthstones.net.TavernMemoryOperation;
import tictim.hearthstones.net.UpdateTavern;
import tictim.hearthstones.utils.AccessModifier;
import tictim.hearthstones.utils.Accessibility;
import tictim.hearthstones.utils.TavernType;

import javax.annotation.Nullable;
import java.util.Collections;

import static tictim.hearthstones.Hearthstones.MODID;

public class TavernScreen extends AbstractScreen{
	public static final ResourceLocation ACCESS_PUBLIC = new ResourceLocation(MODID, "textures/screen/access/public.png");
	public static final ResourceLocation ACCESS_PROTECTED = new ResourceLocation(MODID, "textures/screen/access/protected.png");
	public static final ResourceLocation ACCESS_TEAM = new ResourceLocation(MODID, "textures/screen/access/team.png");
	public static final ResourceLocation ACCESS_PRIVATE = new ResourceLocation(MODID, "textures/screen/access/private.png");

	private static final ITextComponent NO_NAME = new TranslationTextComponent("info.hearthstones.tavern.noName");

	static{
		NO_NAME.getStyle().setColor(TextFormatting.GRAY).setItalic(true);
	}

	private final TavernPos pos;
	private final TavernType type;
	private final Accessibility access;
	private final Owner owner;
	private final boolean isHome;

	private final String originalName;
	private final AccessModifier originalAccessModifier;

	private TextFieldWidget nameField;
	private boolean setHome;

	public TavernScreen(TavernPos pos, TavernType type, @Nullable ITextComponent name, Accessibility access, Owner owner, boolean isHome){
		super(name!=null ? name : new TranslationTextComponent("info.hearthstones.tavern.noName"));
		this.pos = pos;
		this.type = type;
		this.access = access;
		this.owner = owner;
		this.isHome = isHome;

		this.originalName = name!=null ? name.getUnformattedComponentText() : "";
		this.originalAccessModifier = owner.getAccessModifier();
	}

	@Override
	protected void onInit(){
		minecraft.keyboardListener.enableRepeatEvents(true);
		nameField = this.addButton(new TextFieldWidget(font, getLeft()+24*2, getTop()+7*2, 139*2, 4*2, ""));
		nameField.setText(originalName);
		nameField.setMaxStringLength(100);
		nameField.setEnableBackgroundDrawing(false);
		nameField.setDisabledTextColour(14737632); // To match with enabled text color
		nameField.setEnabled(access.isModifiable());
		addButton(new AccessibilityButton(getLeft()+5*2, getTop()));
		addButton(new SetHomeButton(getLeft()+166*2, getTop()+8*2));
	}
	@Override
	protected void onResize(){
		this.xSize = this.width-16;
		this.ySize = this.height-16;
	}

	@Override
	public void render(int mouseX, int mouseY, float partialTicks){
		this.renderBackground();
		super.render(mouseX, mouseY, partialTicks);
	}

	@Override
	protected void drawGuiContainerBackgroundLayer(float partialTicks, int mouseX, int mouseY){
		RenderSystem.pushMatrix();
		RenderSystem.translatef(6, 6, 0);
		TavernRenderHelper.renderTavernUIBase(type, false);
		RenderSystem.popMatrix();
	}

	@Override
	protected void drawGuiContainerForegroundLayer(int mouseX, int mouseY){
		//RenderSystem.pushMatrix();
		if(!nameField.isFocused()&&StringUtils.isNullOrEmpty(nameField.getText())) this.drawString(font, NO_NAME.getFormattedText(), 24*2, 7*2+1, 0xFFFFFF);
		this.drawString(font, TavernSign.formatOwner(owner), 24*2, 13*2-1, 0xFFFFFF);
		//RenderSystem.popMatrix();
	}

	@Override
	public void renderBackground(int tint){
		if(this.minecraft.world!=null){
			this.fillGradient(0, 0, this.width, this.height, 0x90101010, 0xA0101010);
			MinecraftForge.EVENT_BUS.post(new net.minecraftforge.client.event.GuiScreenEvent.BackgroundDrawnEvent(this));
		}else this.renderDirtBackground(tint);
	}

	@Override
	public boolean keyPressed(int keyCode, int scanCode, int p_keyPressed_3_){
		if(super.keyPressed(keyCode, scanCode, p_keyPressed_3_)) return true;
		InputMappings.Input input = InputMappings.getInputByCode(keyCode, scanCode);
		if(keyCode==256||(this.nameField.isFocused() ?
				input.getKeyCode()==GLFW.GLFW_KEY_ENTER :
				this.minecraft.gameSettings.keyBindInventory.isActiveAndMatches(input))){
			onClose();
			return true;
		}else return false;
	}

	@Override
	public void onClose(){
		this.minecraft.keyboardListener.enableRepeatEvents(false);
		if(access.isModifiable()){
			String name = this.nameField.getText();
			if(!originalName.equals(name)||originalAccessModifier!=owner.getAccessModifier()){
				ModNet.CHANNEL.sendToServer(new UpdateTavern(pos, name.isEmpty() ? null : new StringTextComponent(name), owner.getAccessModifier()));
			}
		}
		if(setHome) ModNet.CHANNEL.sendToServer(new TavernMemoryOperation(pos, TavernMemoryOperation.SET_HOME));
		super.onClose();
	}

	private class AccessibilityButton extends Button{
		public AccessibilityButton(int x, int y){
			super(x, y, 32, 32, "", button -> {});
		}

		@Override
		public void renderButton(int mouseX, int mouseY, float partialTicks){
			if(this.visible){
				RenderSystem.pushMatrix();
				RenderSystem.color4f(1, 1, 1, 1);
				RenderSystem.translated(x, y, 0);
				RenderSystem.enableBlend();
				RenderSystem.blendFunc(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA);
				TavernRenderHelper.renderAccess(owner.getAccessModifier());
				RenderSystem.popMatrix();
			}
		}

		@Override
		protected boolean isValidClickButton(int p_isValidClickButton_1_){
			return super.isValidClickButton(p_isValidClickButton_1_)&&access.isAccessibilityModifiable();
		}

		@Override
		public void onClick(double mouseX, double mouseY){
			if(access.isAccessibilityModifiable()){
				AccessModifier am = owner.getAccessModifier();
				AccessModifier[] values = AccessModifier.values();
				owner.setAccessModifier(values[(am.ordinal()+1)%values.length]);
			}
		}

		@Override
		public void renderToolTip(int mouseX, int mouseY){
			if(isHovered()) renderTooltip(ImmutableList.of(owner.getAccessModifier().localize()), mouseX, mouseY);
		}
	}

	private class SetHomeButton extends Button{
		private boolean isPressed;

		public SetHomeButton(int x, int y){
			super(x, y, 7*2, 7*2, "", button -> {});
		}

		@Override
		public void renderButton(int mouseX, int mouseY, float partialTicks){
			if(this.visible){
				minecraft.getTextureManager().bindTexture(HearthstoneScreen.ICONS);
				RenderSystem.color4f(1, 1, 1, 1);
				this.blit(x, y, getTextureX(), getTextureY(), 7*2, 7*2);
			}
		}

		@Override
		protected boolean isValidClickButton(int p_isValidClickButton_1_){
			return super.isValidClickButton(p_isValidClickButton_1_)&&!isHome;
		}

		@Override
		public void onRelease(double p_onRelease_1_, double p_onRelease_3_){
			isPressed = false;
		}

		@Override
		public void onClick(double p_onClick_1_, double p_onClick_3_){
			setHome = !setHome;
		}

		private int getTextureY(){
			return isHome||setHome ? 21*2 : 14*2;
		}

		private int getTextureX(){
			return this.active ? !isHome&&access.isAccessibilityModifiable()&&this.isHovered() ? isPressed ? 14*2 : 7*2 : 0 : 14*2;
		}

		@Override
		public void renderToolTip(int mouseX, int mouseY){
			if(isHovered()) renderTooltip(Collections.singletonList(I18n.format(isHome ? "info.hearthstones.screen.property.home" : "info.hearthstones.screen.set_home")), mouseX, mouseY);
		}
	}
}
