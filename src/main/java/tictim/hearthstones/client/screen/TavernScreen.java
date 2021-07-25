package tictim.hearthstones.client.screen;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.gui.widget.TextFieldWidget;
import net.minecraft.client.gui.widget.button.Button;
import net.minecraft.client.util.InputMappings;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.StringUtils;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraftforge.client.event.GuiScreenEvent.BackgroundDrawnEvent;
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

import static tictim.hearthstones.Hearthstones.MODID;

public class TavernScreen extends AbstractScreen{
	public static final ResourceLocation ACCESS_PUBLIC = new ResourceLocation(MODID, "textures/screen/access/public.png");
	public static final ResourceLocation ACCESS_PROTECTED = new ResourceLocation(MODID, "textures/screen/access/protected.png");
	public static final ResourceLocation ACCESS_TEAM = new ResourceLocation(MODID, "textures/screen/access/team.png");
	public static final ResourceLocation ACCESS_PRIVATE = new ResourceLocation(MODID, "textures/screen/access/private.png");

	private static final ITextComponent NO_NAME = new TranslationTextComponent("info.hearthstones.tavern.noName");

	static{
		NO_NAME.getStyle().withColor(TextFormatting.GRAY).withItalic(true);
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

		this.originalName = name!=null ? name.getContents() : "";
		this.originalAccessModifier = owner.getAccessModifier();
	}

	@Override
	protected void onInit(){
		minecraft.keyboardHandler.setSendRepeatsToGui(true);
		nameField = this.addButton(new TextFieldWidget(font, getLeft()+24*2, getTop()+7*2, 139*2, 4*2, StringTextComponent.EMPTY));
		nameField.setValue(originalName);
		nameField.setMaxLength(100);
		nameField.setBordered(false);
		nameField.setTextColorUneditable(14737632); // To match with enabled text color
		nameField.setEditable(access.isModifiable());
		addButton(new AccessibilityButton(getLeft()+5*2, getTop()));
		addButton(new SetHomeButton(getLeft()+166*2, getTop()+8*2));
	}
	@Override
	protected void onResize(){
		this.xSize = this.width-16;
		this.ySize = this.height-16;
	}

	@Override
	public void render(MatrixStack matrixStack, int mouseX, int mouseY, float partialTicks){
		this.renderBackground(matrixStack);
		super.render(matrixStack, mouseX, mouseY, partialTicks);
	}

	@Override
	protected void drawGuiContainerBackgroundLayer(MatrixStack matrixStack, float partialTicks, int mouseX, int mouseY){
		matrixStack.pushPose();
		matrixStack.translate(6, 6, 0);
		TavernRenderHelper.renderTavernUIBase(matrixStack, type, false);
		matrixStack.popPose();
	}

	@Override
	protected void drawGuiContainerForegroundLayer(MatrixStack matrixStack, int mouseX, int mouseY){
		if(!nameField.isFocused()&&StringUtils.isNullOrEmpty(nameField.getValue())) drawString(matrixStack, font, NO_NAME.getString(), 24*2, 7*2+1, 0xFFFFFF);
		drawString(matrixStack, font, TavernSign.formatOwner(owner), 24*2, 13*2-1, 0xFFFFFF);
	}

	@Override
	public void renderBackground(MatrixStack matrixStack, int tint){
		if(this.minecraft.level!=null){
			this.fillGradient(matrixStack, 0, 0, this.width, this.height, 0x90101010, 0xA0101010);
			MinecraftForge.EVENT_BUS.post(new BackgroundDrawnEvent(this, matrixStack));
		}else this.renderDirtBackground(tint);
	}

	@Override
	public boolean keyPressed(int keyCode, int scanCode, int p_keyPressed_3_){
		if(super.keyPressed(keyCode, scanCode, p_keyPressed_3_)) return true;
		InputMappings.Input input = InputMappings.getKey(keyCode, scanCode);
		if(keyCode==256||(this.nameField.isFocused() ?
				input.getValue()==GLFW.GLFW_KEY_ENTER :
				this.minecraft.options.keyInventory.isActiveAndMatches(input))){
			removed();
			return true;
		}else return false;
	}

	@Override
	public void removed(){
		this.minecraft.keyboardHandler.setSendRepeatsToGui(false);
		if(access.isModifiable()){
			String name = this.nameField.getValue();
			if(!originalName.equals(name)||originalAccessModifier!=owner.getAccessModifier()){
				ModNet.CHANNEL.sendToServer(new UpdateTavern(pos, name.isEmpty() ? null : new StringTextComponent(name), owner.getAccessModifier()));
			}
		}
		if(setHome) ModNet.CHANNEL.sendToServer(new TavernMemoryOperation(pos, TavernMemoryOperation.SET_HOME));
		super.removed();
	}

	private class AccessibilityButton extends Button{
		public AccessibilityButton(int x, int y){
			super(x, y, 32, 32, StringTextComponent.EMPTY, button -> {});
		}

		@Override
		public void renderButton(MatrixStack matrixStack, int mouseX, int mouseY, float partialTicks){
			if(this.visible){
				matrixStack.pushPose();
				matrixStack.translate(x, y, 0);
				//noinspection deprecation
				RenderSystem.color4f(1, 1, 1, 1);
				RenderSystem.enableBlend();
				RenderSystem.blendFunc(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA);
				TavernRenderHelper.renderAccess(matrixStack, owner.getAccessModifier());
				matrixStack.popPose();
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
		public void renderToolTip(MatrixStack matrixStack, int mouseX, int mouseY){
			if(isHovered()) renderTooltip(matrixStack, owner.getAccessModifier().toTextComponent(), mouseX, mouseY);
		}
	}

	private static final ITextComponent HOME_TOOLTIP = new TranslationTextComponent("info.hearthstones.screen.property.home");
	private static final ITextComponent SET_HOME_TOOLTIP = new TranslationTextComponent("info.hearthstones.screen.set_home");

	private class SetHomeButton extends Button{

		private boolean isPressed;

		public SetHomeButton(int x, int y){
			super(x, y, 7*2, 7*2, StringTextComponent.EMPTY, button -> {});
		}

		@Override
		public void renderButton(MatrixStack matrixStack, int mouseX, int mouseY, float partialTicks){
			if(this.visible){
				minecraft.getTextureManager().bind(HearthstoneScreen.ICONS);
				//noinspection deprecation
				RenderSystem.color4f(1, 1, 1, 1);
				this.blit(matrixStack, x, y, getTextureX(), getTextureY(), 7*2, 7*2);
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
		public void renderToolTip(MatrixStack matrixStack, int mouseX, int mouseY){
			if(isHovered()) renderTooltip(matrixStack, isHome ? HOME_TOOLTIP : SET_HOME_TOOLTIP, mouseX, mouseY);
		}
	}
}
