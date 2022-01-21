package tictim.hearthstones.client;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiComponent;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.util.Mth;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.client.gui.GuiUtils;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import tictim.hearthstones.Hearthstones;
import tictim.hearthstones.client.screen.HearthstoneScreen;
import tictim.hearthstones.contents.ModItems;
import tictim.hearthstones.contents.item.hearthstone.HearthstoneItem;
import tictim.hearthstones.hearthstone.CompanionHearthstone;
import tictim.hearthstones.tavern.Tavern;
import tictim.hearthstones.tavern.TavernPos;
import tictim.hearthstones.tavern.TavernTextFormat;

import javax.annotation.Nullable;
import java.util.Set;

@Mod.EventBusSubscriber(modid = Hearthstones.MODID, value = Dist.CLIENT)
public final class OverlayRenderEventHandler{
	private OverlayRenderEventHandler(){}

	/**
	 * Yeah I know this looks bad, stfu
	 */
	@Nullable public static TavernPos homePos;

	@SubscribeEvent
	public static void renderEvent(RenderGameOverlayEvent.Post event){
		if(event.getType()!=RenderGameOverlayEvent.ElementType.ALL||Minecraft.getInstance().screen!=null) return;
		Player p = Minecraft.getInstance().player;
		if(p==null||!p.isAlive()) return;
		if(p.getMainHandItem().getItem() instanceof HearthstoneItem)
			drawHearthstoneUI(event, p, p.getMainHandItem(), InteractionHand.MAIN_HAND);
		else if(p.getOffhandItem().getItem() instanceof HearthstoneItem)
			drawHearthstoneUI(event, p, p.getMainHandItem(), InteractionHand.OFF_HAND);
		drawTavernSign(event.getMatrixStack(), p);
	}

	@SuppressWarnings("IntegerDivisionInFloatingPointContext")
	private static void drawHearthstoneUI(RenderGameOverlayEvent.Post event, Player player, ItemStack stack, InteractionHand hand){
		Minecraft mc = Minecraft.getInstance();
		int width = event.getWindow().getGuiScaledWidth(), height = event.getWindow().getGuiScaledHeight();
		int left = width/2, top = height/2;
		HearthstoneItem.Data data = HearthstoneItem.data(stack);

		PoseStack pose = event.getMatrixStack();
		if(data!=null){
			data.read();
			if(data.destination!=null){
				mc.getItemRenderer().renderAndDecorateItem(player, data.destination.type().stackForRender(), left-8, top-55, 0);

				drawCentered(pose, new TranslatableComponent("info.hearthstones.hearthstone.dest"), left, top-36);
				drawCentered(pose, TavernTextFormat.nameAndDistance(data.destination, player), left, top-25);
				Component ownerText = TavernTextFormat.owner(data.destination);
				Component positionText = TavernTextFormat.position(data.destination);
				int positionWidth = mc.font.width(positionText);
				int widthSum = mc.font.width(ownerText)+mc.font.width(" ")+positionWidth;
				mc.font.drawShadow(pose, ownerText, left-widthSum/2, top-14, 0xF0F0F0);
				mc.font.draw(pose, positionText, left+widthSum/2-positionWidth, top-14, 0xF0F0F0);

				if(player.isUsingItem()&&player.getUsedItemHand()==hand){
					double ratio = Mth.clamp((double)player.getUseItemRemainingTicks()/player.getUseItem().getUseDuration(), 0, 1);
					drawCentered(pose, new TranslatableComponent("info.hearthstones.hearthstone.hearthing"), left, top+22);
					GuiUtils.drawGradientRect(pose.last().pose(), -90, left-32, top+33, left+32, top+43, 0xFF5f5f5f, 0xFF5f5f5f);
					GuiUtils.drawGradientRect(pose.last().pose(), -90, left-32, top+33, left+32-(int)(ratio*64), top+43, 0xFF02ccfc, 0xFF02ccfc);
				}
				if(stack.getItem()==ModItems.COMPANION_HEARTHSTONE.get()){
					Set<Entity> entities = CompanionHearthstone.getWarpTargets(player);
					if(!entities.isEmpty()){
						TranslatableComponent text = new TranslatableComponent("info.hearthstones.companion_hearthstone.companions");
						for(Entity entity : entities) text.append("\n").append(entity.getDisplayName());

						String[] strs = text.getString().split("\n");
						for(int i = 0; i<strs.length; i++)
							mc.font.drawShadow(pose, strs[i], width-8-mc.font.width(strs[i]), top-(strs.length*mc.font.lineHeight)/2+i*mc.font.lineHeight, 0xF0F0F0);
					}
				}
				return;
			}
		}
		drawCentered(pose, new TranslatableComponent(stack.getItem()==ModItems.HEARTHING_PLANKS.get() ?
				"info.hearthstones.hearthing_planks.help" :
				"info.hearthstones.hearthstone.help"), left, top-33);
	}

	private static void drawTavernSign(PoseStack pose, Player player){
		HitResult ray = Minecraft.getInstance().hitResult;
		if(ray==null||ray.getType()!=HitResult.Type.BLOCK) return;
		if(!(player.level.getBlockEntity(((BlockHitResult)ray).getBlockPos()) instanceof Tavern tavern)) return;
		pose.pushPose();
		RenderSystem.disableDepthTest();
		RenderSystem.setShaderColor(1, 1, 1, 1);
		pose.translate(6, 6, 0);
		pose.pushPose();
		Rendering.renderTavernUIBase(pose, tavern.type(), false);
		pose.translate(2+10, 2, 0);
		Rendering.renderTavernAccess(pose, tavern.access());
		pose.popPose();

		TavernPos homePos = OverlayRenderEventHandler.homePos;
		if(homePos!=null&&homePos.equals(tavern.pos())){
			RenderSystem.setShaderTexture(0, HearthstoneScreen.ICONS);
			GuiComponent.blit(pose, 167*2, 9*2, 7*2, 0, 7*2, 7*2, 256, 256);
		}
		Font font = Minecraft.getInstance().font;
		font.drawShadow(pose, TavernTextFormat.name(tavern), 25*2, 8*2+1, 0xFFFFFF);
		font.drawShadow(pose, TavernTextFormat.owner(tavern), 25*2, 14*2-1, 0xFFFFFF);
		pose.popPose();
		RenderSystem.enableDepthTest();
	}

	private static void drawCentered(PoseStack matrixStack, Component str, float x, float y){
		Font font = Minecraft.getInstance().font;
		//noinspection IntegerDivisionInFloatingPointContext
		font.drawShadow(matrixStack, str, x-font.width(str)/2, y, 0xF0F0F0);
	}
}
