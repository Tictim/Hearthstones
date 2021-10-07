package tictim.hearthstones.client.screen;

import com.mojang.blaze3d.platform.InputConstants;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.chat.NarratorChatListener;
import net.minecraft.client.resources.language.I18n;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.level.Level;
import tictim.hearthstones.Hearthstones;
import tictim.hearthstones.capability.PlayerTavernMemory;
import tictim.hearthstones.capability.TavernMemory;
import tictim.hearthstones.tavern.Tavern;
import tictim.hearthstones.tavern.TavernRecord;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;

public class HearthstoneScreen extends AbstractScreen{
	public static final ResourceLocation ICONS = new ResourceLocation(Hearthstones.MODID, "textures/screen/icons.png");

	public final PlayerTavernMemory memory;
	public final boolean hearthingGem;

	private int yOffset = 0;
	private double yOffsetFloat = 0;
	private double yOffsetDest = 0;
	private int screenY;

	private boolean updateButtons = false;

	private final Comparator<TavernRecord> tavernComparator;
	private final List<TavernButton> tavernButtons = new ArrayList<>();

	public HearthstoneScreen(boolean hearthingGem){
		super(NarratorChatListener.NO_TITLE);
		this.memory = TavernMemory.expectFromPlayer(Objects.requireNonNull(Minecraft.getInstance().player));
		this.hearthingGem = hearthingGem;

		this.tavernComparator = (o1, o2) -> {
			int i;
			// home
			Tavern homeTavern = memory.getHomeTavern();
			i = Boolean.compare(homeTavern==o2, homeTavern==o1);
			if(i!=0) return i;
			ResourceLocation d1 = o1.pos().dim;
			ResourceLocation d2 = o2.pos().dim;
			//noinspection ConstantConditions
			ResourceKey<Level> dim = minecraft.level.dimension();
			if(d1==d2&&dim.location().equals(d1)){// #3(Optional) distance
				//noinspection ConstantConditions
				double s1 = minecraft.player.distanceToSqr(o1.blockPos().getX()+0.5, o1.blockPos().getY()+0.5, o1.blockPos().getZ()+0.5);
				double s2 = minecraft.player.distanceToSqr(o2.blockPos().getX()+0.5, o2.blockPos().getY()+0.5, o2.blockPos().getZ()+0.5);
				i = Double.compare(s1, s2);
				if(i!=0) return i;
			}

			// missing
			i = Boolean.compare(o1.isMissing(), o2.isMissing());
			if(i!=0) return i;
			// dimension
			i = o1.pos().dim.compareTo(o2.pos().dim);
			if(i!=0) return i;
			// name
			if((o1.name()==null)!=(o2.name()==null)) return o1.name()==null ? -1 : 1;
			else if(o1.name()!=null){
				i = o1.name().getContents().compareTo(o2.name().getContents());
				if(i!=0) return i;
			}
			// static position
			return o1.blockPos().compareTo(o2.blockPos());
		};
	}

	public void markForUpdate(){
		this.updateButtons = true;
	}

	@Override protected void onInit(){
		createTavernButtons();
	}
	@Override protected void onResize(){
		this.xSize = this.width;
		this.ySize = this.height;
	}

	public void createTavernButtons(){
		updateButtons = false;
		for(TavernButton tavernButton : tavernButtons) removeWidget(tavernButton);
		tavernButtons.clear();

		screenY = 0;

		memory.taverns().values().stream()
				.sorted(tavernComparator)
				.forEachOrdered(it -> createButton(it, false));
		TavernMemory.expectClientGlobal().taverns().values().stream()
				.filter(it -> !memory.has(it.pos()))
				.sorted(tavernComparator)
				.forEachOrdered(it -> createButton(it, true));
	}

	private void createButton(Tavern tavern, boolean isFromGlobal){
		tavernButtons.add(addRenderableWidget(new TavernButton(this,
				getLeft(),
				getTop()+screenY,
				tavern,
				memory.getHomePos()!=null&&memory.getHomePos().equals(tavern.pos()),
				isFromGlobal)));
		screenY += TavernButton.HEIGHT+(7*2)+6;
	}

	@Override public void render(PoseStack pose, int mouseX, int mouseY, float partialTicks){
		if(updateButtons) createTavernButtons();

		int mouseY2 = mouseY+yOffset;
		this.renderBackground(pose);
		pose.pushPose();
		pose.translate(0, -yOffset, 0);
		super.render(pose, mouseX, mouseY2, partialTicks);
		pose.popPose();
		super.drawTooltip(pose, mouseX, mouseY);
		this.yOffsetFloat = Mth.lerp(0.4, yOffsetFloat, this.yOffsetDest = Mth.clamp(yOffsetDest, 0, Math.max(0, screenY-width/2)));
		this.yOffset = (int)Math.round(yOffsetFloat);
	}

	@Override protected void renderLabels(PoseStack pose, int mouseX, int mouseY){
		if(this.tavernButtons.isEmpty()) drawCenteredString(pose, font, I18n.get("info.hearthstones.screen.empty"), this.xSize/2, this.ySize/2-5, 0xFFFFFF);
	}

	@Override protected void drawTooltip(PoseStack pose, int mouseX, int mouseY){}

	@Override public boolean mouseClicked(double mouseX, double mouseY, int button){
		return super.mouseClicked(mouseX, mouseY+yOffset, button);
	}
	@Override public boolean mouseReleased(double mouseX, double mouseY, int button){
		return super.mouseReleased(mouseX, mouseY+yOffset, button);
	}
	@Override public boolean mouseDragged(double mouseX, double mouseY, int mouseButton, double dragX, double dragY){
		return super.mouseDragged(mouseX, mouseY+yOffset, mouseButton, dragX, dragY);
	}
	@Override public boolean mouseScrolled(double mouseX, double mouseY, double scroll){
		this.yOffsetDest -= scroll*40;
		return true;
	}

	@Override public boolean keyPressed(int keyCode, int scanCode, int modifier){
		if(super.keyPressed(keyCode, scanCode, modifier)) return true;
		InputConstants.Key mouseKey = InputConstants.getKey(keyCode, scanCode);
		//noinspection ConstantConditions
		if(this.minecraft.options.keyInventory.getKey().equals(mouseKey)){
			onClose();
			return true;
		}else return false;
	}
}
