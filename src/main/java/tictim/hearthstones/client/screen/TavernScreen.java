package tictim.hearthstones.client.screen;

import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.platform.InputConstants;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.ChatFormatting;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.StringUtil;
import org.lwjgl.glfw.GLFW;
import tictim.hearthstones.client.Rendering;
import tictim.hearthstones.net.ModNet;
import tictim.hearthstones.net.TavernMemoryOperationMsg;
import tictim.hearthstones.net.UpdateTavernMsg;
import tictim.hearthstones.tavern.AccessModifier;
import tictim.hearthstones.tavern.Accessibility;
import tictim.hearthstones.tavern.Owner;
import tictim.hearthstones.tavern.TavernPos;
import tictim.hearthstones.tavern.TavernTextFormat;
import tictim.hearthstones.tavern.TavernType;

import javax.annotation.Nullable;

import static tictim.hearthstones.Hearthstones.MODID;

public class TavernScreen extends AbstractScreen{
	public static final ResourceLocation ACCESS_PUBLIC = new ResourceLocation(MODID, "textures/screen/access/public.png");
	public static final ResourceLocation ACCESS_PROTECTED = new ResourceLocation(MODID, "textures/screen/access/protected.png");
	public static final ResourceLocation ACCESS_TEAM = new ResourceLocation(MODID, "textures/screen/access/team.png");
	public static final ResourceLocation ACCESS_PRIVATE = new ResourceLocation(MODID, "textures/screen/access/private.png");

	private static final Component NO_NAME = Component.translatable("info.hearthstones.tavern.no_name");

	static{
		NO_NAME.getStyle().withColor(ChatFormatting.GRAY).withItalic(true);
	}

	private final TavernPos pos;
	private final TavernType type;
	private final Accessibility accessibility;
	private final Owner owner;
	private AccessModifier accessModifier;
	private final boolean isHome;

	private final String originalName;
	private final AccessModifier originalAccess;

	private EditBox nameField;
	private boolean setHome;

	public TavernScreen(TavernPos pos, TavernType type, @Nullable String name, Accessibility accessibility, Owner owner, AccessModifier accessModifier, boolean isHome){
		super(name!=null ? Component.literal(name) : Component.translatable("info.hearthstones.tavern.no_name"));
		this.pos = pos;
		this.type = type;
		this.accessibility = accessibility;
		this.owner = owner;
		this.isHome = isHome;

		this.originalName = name!=null ? name : "";
		this.originalAccess = this.accessModifier = accessModifier;
	}

	@Override
	protected void onInit(){
		//noinspection ConstantConditions
		minecraft.keyboardHandler.setSendRepeatsToGui(true);
		nameField = this.addRenderableWidget(new EditBox(font, getLeft()+24*2, getTop()+7*2, 139*2, 4*2, Component.empty()));
		nameField.setMaxLength(50);
		nameField.setValue(originalName);
		nameField.setBordered(false);
		nameField.setTextColorUneditable(0xe0e0e0); // To match with enabled text color
		nameField.setEditable(accessibility.isModifiable());
		addRenderableWidget(new AccessibilityButton(getLeft()+5*2, getTop()));
		addRenderableWidget(new SetHomeButton(getLeft()+166*2, getTop()+8*2));
	}

	@Override
	protected void onResize(){
		this.xSize = this.width-16;
		this.ySize = this.height-16;
	}

	@Override
	public void render(PoseStack pose, int mouseX, int mouseY, float partialTicks){
		RenderSystem.disableDepthTest();
		this.renderBackground(pose);
		pose.pushPose();
		pose.translate(6, 6, 0);
		Rendering.renderTavernUIBase(pose, type, false);
		pose.popPose();
		super.render(pose, mouseX, mouseY, partialTicks);

		if(!nameField.isFocused()&&StringUtil.isNullOrEmpty(nameField.getValue()))
			drawString(pose, font, NO_NAME.getString(), getLeft()+24*2, getTop()+7*2+1, 0xFFFFFF);
		drawString(pose, font, TavernTextFormat.formatOwner(owner), getLeft()+24*2, getTop()+13*2-1, 0xFFFFFF);

		drawTooltip(pose, mouseX, mouseY);
		RenderSystem.enableDepthTest();
	}

	@Override
	public void renderBackground(PoseStack matrixStack, int tint){
		//noinspection ConstantConditions
		if(this.minecraft.level!=null){
			this.fillGradient(matrixStack, 0, 0, this.width, this.height, 0x90101010, 0xA0101010);
		}else this.renderDirtBackground(tint);
	}

	@Override
	public boolean keyPressed(int keyCode, int scanCode, int modifier){
		if(super.keyPressed(keyCode, scanCode, modifier)) return true;
		InputConstants.Key input = InputConstants.getKey(keyCode, scanCode);
		//noinspection ConstantConditions
		if(this.nameField.isFocused() ?
				input.getValue()==GLFW.GLFW_KEY_ENTER :
				this.minecraft.options.keyInventory.getKey().equals(input)){
			onClose();
			return true;
		}else return false;
	}

	@Override public void removed(){
		//noinspection ConstantConditions
		this.minecraft.keyboardHandler.setSendRepeatsToGui(false);
		if(accessibility.isModifiable()){
			String name = this.nameField.getValue();
			if(!originalName.equals(name)||originalAccess!=accessModifier){
				ModNet.CHANNEL.sendToServer(new UpdateTavernMsg(pos, name.isEmpty() ? null : name, accessModifier));
			}
		}
		if(setHome) ModNet.CHANNEL.sendToServer(new TavernMemoryOperationMsg(pos, TavernMemoryOperationMsg.SET_HOME));
		super.removed();
	}

	private class AccessibilityButton extends Button{
		public AccessibilityButton(int x, int y){
			super(x, y, 32, 32, Component.empty(), button -> {});
		}

		@Override
		public void renderButton(PoseStack matrixStack, int mouseX, int mouseY, float partialTicks){
			if(this.visible){
				matrixStack.pushPose();
				matrixStack.translate(x, y, 0);
				RenderSystem.setShaderColor(1, 1, 1, 1);
				RenderSystem.enableBlend();
				RenderSystem.blendFunc(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA);
				Rendering.renderTavernAccess(matrixStack, accessModifier);
				matrixStack.popPose();
			}
		}

		@Override
		protected boolean isValidClickButton(int button){
			return super.isValidClickButton(button)&&accessibility.isAccessibilityModifiable();
		}

		@Override
		public void onClick(double mouseX, double mouseY){
			if(accessibility.isAccessibilityModifiable()){
				AccessModifier[] values = AccessModifier.values();
				accessModifier = (values[(accessModifier.ordinal()+1)%values.length]);
			}
		}

		@Override
		public void renderToolTip(PoseStack matrixStack, int mouseX, int mouseY){
			if(isHoveredOrFocused()) renderTooltip(matrixStack, font.split(accessModifier.text(), TavernScreen.this.width*2/3), mouseX, mouseY);
		}
	}

	private static final Component HOME_TOOLTIP = Component.translatable("info.hearthstones.screen.property.home");
	private static final Component SET_HOME_TOOLTIP = Component.translatable("info.hearthstones.screen.set_home");

	private class SetHomeButton extends Button{
		private boolean isPressed;

		public SetHomeButton(int x, int y){
			super(x, y, 7*2, 7*2, Component.empty(), button -> {});
		}

		@Override
		public void renderButton(PoseStack pose, int mouseX, int mouseY, float partialTicks){
			if(this.visible){
				RenderSystem.setShaderTexture(0, TavernButton.ICONS);
				RenderSystem.setShaderColor(1, 1, 1, 1);
				this.blit(pose, x, y, getTextureX(), getTextureY(), 7*2, 7*2);
			}
		}

		@Override
		protected boolean isValidClickButton(int btn){
			return super.isValidClickButton(btn)&&!isHome;
		}

		@Override
		public void onRelease(double mouseX, double mouseY){
			isPressed = false;
		}

		@Override
		public void onClick(double mouseX, double mouseY){
			setHome = !setHome;
		}

		private int getTextureY(){
			return isHome||setHome ? 21*2 : 14*2;
		}

		private int getTextureX(){
			return this.active ? !isHome&&this.isHoveredOrFocused() ? isPressed ? 14*2 : 7*2 : 0 : 14*2;
		}

		@Override
		public void renderToolTip(PoseStack matrixStack, int mouseX, int mouseY){
			if(isHoveredOrFocused()) renderTooltip(matrixStack, isHome ? HOME_TOOLTIP : SET_HOME_TOOLTIP, mouseX, mouseY);
		}
	}
}
