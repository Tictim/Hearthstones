package tictim.hearthstones.client.gui;

import io.netty.util.internal.StringUtil;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiTextField;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraft.util.text.TextFormatting;
import net.minecraftforge.client.event.GuiScreenEvent.ActionPerformedEvent;
import net.minecraftforge.common.MinecraftForge;
import org.lwjgl.input.Keyboard;
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
import java.io.IOException;
import java.util.Collections;

import static tictim.hearthstones.Hearthstones.MODID;

public class TavernScreen extends AbstractScreen{
	public static final ResourceLocation ACCESS_PUBLIC = new ResourceLocation(MODID, "textures/screen/access/public.png");
	public static final ResourceLocation ACCESS_PROTECTED = new ResourceLocation(MODID, "textures/screen/access/protected.png");
	public static final ResourceLocation ACCESS_TEAM = new ResourceLocation(MODID, "textures/screen/access/team.png");
	public static final ResourceLocation ACCESS_PRIVATE = new ResourceLocation(MODID, "textures/screen/access/private.png");

	private static final ITextComponent NO_NAME = new TextComponentTranslation("info.hearthstones.tavern.no_name");

	static{
		NO_NAME.getStyle().setColor(TextFormatting.GRAY).setItalic(true);
	}

	private final TavernPos pos;
	private final TavernType type;
	private final Accessibility accessibility;
	private final Owner owner;
	private AccessModifier accessModifier;
	private final boolean isHome;

	private final String originalName;
	private final AccessModifier originalAccess;

	private GuiTextField nameField;
	private boolean setHome;

	public TavernScreen(TavernPos pos, TavernType type, @Nullable String name, Accessibility accessibility, Owner owner, AccessModifier accessModifier, boolean isHome){
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
		Keyboard.enableRepeatEvents(true);
		nameField = new GuiTextField(0, fontRenderer, getLeft()+24*2, getTop()+7*2, 139*2, 4*2);
		nameField.setMaxStringLength(50);
		nameField.setText(originalName);
		nameField.setEnableBackgroundDrawing(false);
		nameField.setDisabledTextColour(0xe0e0e0); // To match with enabled text color
		nameField.setEnabled(accessibility.isModifiable());
		addButton(new AccessibilityButton(1, getLeft()+5*2, getTop())).enabled = accessibility.isAccessibilityModifiable();
		addButton(new SetHomeButton(2, getLeft()+166*2, getTop()+8*2));
	}

	@Override
	protected void onResize(){
		this.xSize = this.width-16;
		this.ySize = this.height-16;
	}

	@Override public void drawScreen(int mouseX, int mouseY, float partialTicks){
		GlStateManager.disableDepth();
		this.drawDefaultBackground();
		GlStateManager.pushMatrix();
		GlStateManager.translate(6, 6, 0);
		Rendering.renderTavernUIBase(type, false);
		GlStateManager.popMatrix();
		super.drawScreen(mouseX, mouseY, partialTicks);

		if(!nameField.isFocused()&&StringUtil.isNullOrEmpty(nameField.getText()))
			drawString(fontRenderer, NO_NAME.getFormattedText(), getLeft()+24*2, getTop()+7*2+1, 0xFFFFFF);
		drawString(fontRenderer, TavernTextFormat.formatOwner(owner), getLeft()+24*2, getTop()+13*2-1, 0xFFFFFF);

		drawTooltip(mouseX, mouseY);
		GlStateManager.enableDepth();
	}

	@Override public void drawWorldBackground(int tint){
		if(this.mc.world!=null){
			this.drawGradientRect(0, 0, this.width, this.height, 0x90101010, 0xA0101010);
		}else super.drawWorldBackground(tint);
	}

	@Override protected void keyTyped(char typedChar, int keyCode) throws IOException{
		if(!this.nameField.textboxKeyTyped(typedChar, keyCode)){
			if(keyCode==1||mc.gameSettings.keyBindInventory.isActiveAndMatches(keyCode)){
				this.mc.displayGuiScreen(null);
				if(this.mc.currentScreen==null)
					this.mc.setIngameFocus();
			}else super.keyTyped(typedChar, keyCode);
		}
	}

	private int selectedButtonMouse;

	@Override protected void mouseClicked(int mouseX, int mouseY, int mouseButton) throws IOException{
		this.selectedButtonMouse = mouseButton;
		if(mouseButton==1){
			for(int i = 0; i<this.buttonList.size(); ++i){
				GuiButton btn = this.buttonList.get(i);

				if(btn instanceof TavernButton&&((TavernButton)btn).rightMousePressed(mc, mouseX, mouseY)){
					ActionPerformedEvent.Pre event = new ActionPerformedEvent.Pre(this, btn, this.buttonList);
					if(MinecraftForge.EVENT_BUS.post(event))
						break;
					btn = event.getButton();
					this.selectedButton = btn;
					btn.playPressSound(this.mc.getSoundHandler());
					this.actionPerformed(btn);
					if(this.equals(this.mc.currentScreen))
						MinecraftForge.EVENT_BUS.post(new ActionPerformedEvent.Post(this, event.getButton(), this.buttonList));
				}
			}
		}else{
			super.mouseClicked(mouseX, mouseY, mouseButton);
		}
	}

	@Override protected void mouseReleased(int mouseX, int mouseY, int state){
		if(this.selectedButton instanceof TavernButton&&state==this.selectedButtonMouse){
			if(state==0){
				this.selectedButton.mouseReleased(mouseX, mouseY);
			}else if(state==1){
				((TavernButton)this.selectedButton).rightMouseReleased(mouseX, mouseY);
			}
			this.selectedButton = null;
		}else super.mouseReleased(mouseX, mouseY, state);
	}

	@Override public void onGuiClosed(){
		Keyboard.enableRepeatEvents(false);
		if(accessibility.isModifiable()){
			String name = this.nameField.getText();
			if(!originalName.equals(name)||originalAccess!=accessModifier){
				ModNet.CHANNEL.sendToServer(new UpdateTavernMsg(pos, name.isEmpty() ? null : name, accessModifier));
			}
		}
		if(setHome) ModNet.CHANNEL.sendToServer(new TavernMemoryOperationMsg(pos, TavernMemoryOperationMsg.SET_HOME));
		super.onGuiClosed();
	}

	private class AccessibilityButton extends GuiButton implements TooltipComponent{
		public AccessibilityButton(int id, int x, int y){
			super(id, x, y, 32, 32, "");
		}

		@Override public void drawButton(Minecraft mc, int mouseX, int mouseY, float partialTicks){
			if(this.visible){
				this.hovered = mouseX>=this.x&&mouseY>=this.y&&mouseX<this.x+this.width&&mouseY<this.y+this.height;

				GlStateManager.pushMatrix();
				GlStateManager.translate(x, y, 0);
				GlStateManager.color(1, 1, 1, 1);
				GlStateManager.enableBlend();
				GlStateManager.blendFunc(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA);
				Rendering.renderTavernAccess(accessModifier);
				GlStateManager.popMatrix();
			}
		}

		@Override public boolean mousePressed(Minecraft mc, int mouseX, int mouseY){
			return super.mousePressed(mc, mouseX, mouseY)&&accessibility.isAccessibilityModifiable();
		}
		@Override public void mouseReleased(int mouseX, int mouseY){
			if(mouseX<this.x||mouseY<this.y||mouseX>=this.x+this.width||mouseY>=this.y+this.height) return;
			if(accessibility.isAccessibilityModifiable()){
				AccessModifier[] values = AccessModifier.values();
				accessModifier = (values[(accessModifier.ordinal()+1)%values.length]);
			}
		}

		@Override public void renderTooltip(int mouseX, int mouseY){
			if(hovered) drawHoveringText(fontRenderer.listFormattedStringToWidth(accessModifier.text().getFormattedText(), TavernScreen.this.width*2/3), mouseX, mouseY);
		}
	}

	private static final ITextComponent HOME_TOOLTIP = new TextComponentTranslation("info.hearthstones.screen.property.home");
	private static final ITextComponent SET_HOME_TOOLTIP = new TextComponentTranslation("info.hearthstones.screen.set_home");

	private class SetHomeButton extends GuiButton implements TooltipComponent{
		private boolean isPressed;

		public SetHomeButton(int id, int x, int y){
			super(id, x, y, 7*2, 7*2, "");
		}

		@Override public void drawButton(Minecraft mc, int mouseX, int mouseY, float partialTicks){
			if(this.visible){
				this.hovered = mouseX>=this.x&&mouseY>=this.y&&mouseX<this.x+this.width&&mouseY<this.y+this.height;

				mc.renderEngine.bindTexture(TavernButton.ICONS);
				GlStateManager.color(1, 1, 1, 1);
				this.drawTexturedModalRect(x, y, getTextureX(), getTextureY(), 7*2, 7*2);
			}
		}

		@Override public boolean mousePressed(Minecraft mc, int mouseX, int mouseY){
			boolean b = !isHome&&super.mousePressed(mc, mouseX, mouseY);
			if(b) isPressed = true;
			return b;
		}

		@Override public void mouseReleased(int mouseX, int mouseY){
			if(mouseX<this.x||mouseY<this.y||mouseX>=this.x+this.width||mouseY>=this.y+this.height) return;
			setHome = !setHome;
		}

		private int getTextureY(){
			return isHome||setHome ? 21*2 : 14*2;
		}

		private int getTextureX(){
			return this.enabled ? !isHome&&this.hovered ? isPressed ? 14*2 : 7*2 : 0 : 14*2;
		}

		@Override public void renderTooltip(int mouseX, int mouseY){
			if(hovered) drawHoveringText(Collections.singletonList((isHome ? HOME_TOOLTIP : SET_HOME_TOOLTIP).getFormattedText()), mouseX, mouseY);
		}
	}
}
