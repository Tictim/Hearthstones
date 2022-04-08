package tictim.hearthstones.client;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiComponent;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.util.Mth;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraftforge.client.gui.ForgeIngameGui;
import net.minecraftforge.client.gui.GuiUtils;
import net.minecraftforge.client.gui.IIngameOverlay;
import tictim.hearthstones.client.screen.TavernButton;
import tictim.hearthstones.contents.ModItems;
import tictim.hearthstones.contents.blockentity.BinderLecternBlockEntity;
import tictim.hearthstones.contents.item.TavernBinderItem;
import tictim.hearthstones.contents.item.TavernWaypointItem;
import tictim.hearthstones.contents.item.hearthstone.HearthstoneItem;
import tictim.hearthstones.hearthstone.CompanionHearthstone;
import tictim.hearthstones.tavern.Tavern;
import tictim.hearthstones.tavern.TavernBinderData;
import tictim.hearthstones.tavern.TavernPos;
import tictim.hearthstones.tavern.TavernRecord;
import tictim.hearthstones.tavern.TavernTextFormat;

import javax.annotation.Nullable;
import java.util.Set;

public class HearthstoneOverlay implements IIngameOverlay{
	/**
	 * Yeah I know this looks bad, stfu
	 */
	@Nullable public static TavernPos homePos;

	@Override public void render(ForgeIngameGui gui, PoseStack pose, float partialTick, int width, int height){
		if(Minecraft.getInstance().screen!=null) return;
		Player p = Minecraft.getInstance().player;
		if(p==null||!p.isAlive()) return;

		drawTavernSign(pose, p);

		// held item overlay
		if(p.isUsingItem()&&p.getUsedItemHand()==InteractionHand.OFF_HAND){ // Prioritize active hand, or main hand if there's no active hand
			if(drawOverlayForItem(pose, p, width, height, InteractionHand.OFF_HAND)) return;
			if(drawOverlayForItem(pose, p, width, height, InteractionHand.MAIN_HAND)) return;
		}else{
			if(drawOverlayForItem(pose, p, width, height, InteractionHand.MAIN_HAND)) return;
			if(drawOverlayForItem(pose, p, width, height, InteractionHand.OFF_HAND)) return;
		}
		// block overlay
		drawBinderLecternOverlay(pose, p, width, height);
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

		TavernPos homePos = HearthstoneOverlay.homePos;
		if(homePos!=null&&homePos.equals(tavern.pos())){
			RenderSystem.setShaderTexture(0, TavernButton.ICONS);
			GuiComponent.blit(pose, 167*2, 9*2, 7*2, 0, 7*2, 7*2, 256, 256);
		}
		Font font = Minecraft.getInstance().font;
		font.drawShadow(pose, TavernTextFormat.name(tavern), 25*2, 8*2+1, 0xFFFFFF);
		font.drawShadow(pose, TavernTextFormat.owner(tavern), 25*2, 14*2-1, 0xFFFFFF);
		pose.popPose();
		RenderSystem.enableDepthTest();
	}

	/**
	 * Tries to draw corresponding overlay for an item the player is holding.
	 */
	private static boolean drawOverlayForItem(PoseStack pose, Player player, int width, int height, InteractionHand hand){
		ItemStack stack = player.getItemInHand(hand);
		if(stack.isEmpty()) return false;
		if(stack.getItem() instanceof HearthstoneItem item){
			drawHearthstoneOverlay(pose, player, width, height, stack, item, hand);
			return true;
		}else if(stack.getItem()==ModItems.WAYPOINT_BINDER.get()||stack.getItem()==ModItems.INFINITE_WAYPOINT_BINDER.get()){
			TavernBinderData data = TavernBinderItem.data(stack);
			if(data!=null) drawBinderOverlay(pose, player, width, height, data.getWaypoints(), data.getEmptyWaypoints(), data.isInfiniteWaypoints());
			return true;
		}else if(stack.getItem()==ModItems.WAYPOINT.get()){
			drawWaypointOverlay(pose, player, width, height, stack);
			return true;
		}
		return false;
	}

	@SuppressWarnings("IntegerDivisionInFloatingPointContext")
	private static void drawHearthstoneOverlay(PoseStack pose, Player player, int width, int height, ItemStack stack, HearthstoneItem hearthstoneItem, InteractionHand hand){
		Minecraft mc = Minecraft.getInstance();
		int left = width/2, top = height/2;

		HearthstoneItem.Data data = HearthstoneItem.data(stack);
		if(data!=null) data.read();

		if(data==null||data.destination==null){
			drawHelpString(pose, left, top-33,
					stack.getItem()==ModItems.HEARTHING_PLANKS.get() ?
							"info.hearthstones.hearthing_planks.help" :
							"info.hearthstones.hearthstone.help");
		}else{
			drawTavernOverlay(pose, player, left, top-55, data.destination,
					"info.hearthstones.hearthstone.dest");
			if(hearthstoneItem.getHearthstone() instanceof CompanionHearthstone ch){
				Set<Entity> entities = ch.getWarpTargets(player);
				if(!entities.isEmpty()){
					TranslatableComponent text = new TranslatableComponent("info.hearthstones.companion_hearthstone.companions");
					for(Entity entity : entities) text.append("\n").append(entity.getDisplayName());

					String[] strs = text.getString().split("\n");
					for(int i = 0; i<strs.length; i++)
						mc.font.drawShadow(pose, strs[i],
								width-8-mc.font.width(strs[i]),
								top-(strs.length*mc.font.lineHeight)/2+i*mc.font.lineHeight,
								0xF0F0F0);
				}
			}
		}

		if(player.isUsingItem()&&player.getUsedItemHand()==hand){
			double ratio = Mth.clamp((double)player.getUseItemRemainingTicks()/player.getUseItem().getUseDuration(), 0, 1);
			drawCentered(pose, new TranslatableComponent("info.hearthstones.hearthstone.hearthing"), left, top+22);
			GuiUtils.drawGradientRect(pose.last().pose(), -90, left-32, top+33, left+32, top+43, 0xFF5f5f5f, 0xFF5f5f5f);
			GuiUtils.drawGradientRect(pose.last().pose(), -90, left-32, top+33, left+32-(int)(ratio*64), top+43, 0xFF02ccfc, 0xFF02ccfc);
		}
	}

	@Nullable private static ItemStack waypointForRender;

	private static void drawBinderOverlay(PoseStack pose, Player player, int width, int height, int waypoints, int emptyWaypoints, boolean infiniteWaypoints){
		Minecraft mc = Minecraft.getInstance();
		int left = width/2, top = height/2;

		int midpoint = top-33-18;

		Component waypointText = infiniteWaypoints ?
				new TranslatableComponent("info.hearthstones.binder.overlay.waypoints.infinite",
						formatWaypointCount(waypoints)) :
				new TranslatableComponent("info.hearthstones.binder.overlay.waypoints",
						formatWaypointCount(waypoints), formatWaypointCount(emptyWaypoints));

		int length = 16+4+mc.font.width(waypointText);

		if(waypointForRender==null) waypointForRender = new ItemStack(ModItems.WAYPOINT.get());
		mc.getItemRenderer().renderAndDecorateItem(player, waypointForRender, left-length/2, midpoint-8, 0);

		//noinspection IntegerDivisionInFloatingPointContext
		mc.font.drawShadow(pose, waypointText, left-length/2+20, midpoint-mc.font.lineHeight/2, 0xFFFFFFFF);

		drawHelpString(pose, left, top-33,
				"info.hearthstones.binder.overlay.help.0",
				"info.hearthstones.binder.overlay.help.1");
	}

	private static Component formatWaypointCount(int waypoints){
		if(waypoints<=0) return new TextComponent(waypoints+"").withStyle(ChatFormatting.RED);
		else if(waypoints>=1000) return new TextComponent("1,000+");
		else return new TextComponent(waypoints+"");
	}

	private static void drawWaypointOverlay(PoseStack pose, Player player, int width, int height, ItemStack stack){
		TavernRecord tavern = TavernWaypointItem.getTavern(stack);
		if(tavern!=null)
			drawTavernOverlay(pose, player, width/2, height/2-55, tavern,
					"info.hearthstones.waypoint.overlay.saved");

		HitResult ray = Minecraft.getInstance().hitResult;
		boolean lookingAtBinderLectern = ray!=null&&ray.getType()==HitResult.Type.BLOCK&&
				player.level.getBlockEntity(((BlockHitResult)ray).getBlockPos()) instanceof BinderLecternBlockEntity;

		drawHelpString(pose, width/2, height/2+15, lookingAtBinderLectern ?
				"info.hearthstones.waypoint.overlay.help.lectern" :
				tavern!=null ?
						"info.hearthstones.waypoint.overlay.help.read" :
						"info.hearthstones.waypoint.overlay.help.bind");
	}

	/**
	 * height=51 if {@code contextStringKey} is present, height=40 if didn't
	 */
	public static void drawTavernOverlay(PoseStack pose, @Nullable Player player, int xCenter, int yStart, Tavern tavern, @Nullable String contextStringKey){
		Minecraft mc = Minecraft.getInstance();
		// actually it's nullable
		//noinspection ConstantConditions
		mc.getItemRenderer().renderAndDecorateItem(player, tavern.type().stackForRender(), xCenter-8, yStart, 0);

		if(contextStringKey!=null){
			drawCentered(pose, new TranslatableComponent(contextStringKey), xCenter, yStart+19);
			yStart += 11;
		}
		//noinspection ConstantConditions
		drawCentered(pose, player==null ? TavernTextFormat.name(tavern) : TavernTextFormat.nameAndDistance(tavern, player), xCenter, yStart+19);
		Component ownerText = TavernTextFormat.owner(tavern);
		Component positionText = TavernTextFormat.position(tavern);
		int positionWidth = mc.font.width(positionText);
		int widthSum = mc.font.width(ownerText)+mc.font.width(" ")+positionWidth;
		//noinspection IntegerDivisionInFloatingPointContext
		mc.font.drawShadow(pose, ownerText, xCenter-widthSum/2, yStart+30, 0xF0F0F0);
		//noinspection IntegerDivisionInFloatingPointContext
		mc.font.draw(pose, positionText, xCenter+widthSum/2-positionWidth, yStart+30, 0xF0F0F0);
	}

	private static void drawBinderLecternOverlay(PoseStack pose, Player player, int width, int height){
		HitResult ray = Minecraft.getInstance().hitResult;
		if(ray==null||ray.getType()!=HitResult.Type.BLOCK) return;
		if(!(player.level.getBlockEntity(((BlockHitResult)ray).getBlockPos()) instanceof BinderLecternBlockEntity binderLectern)) return;

		drawBinderOverlay(pose, player, width, height,
				binderLectern.getWaypointsSync(),
				binderLectern.getEmptyWaypointsSync(),
				binderLectern.isInfiniteWaypointsSync()
		);
	}

	private static void drawHelpString(PoseStack pose, int xCenter, int yStart, String... keys){
		for(String key : keys){
			drawCentered(pose, new TranslatableComponent(key), xCenter, yStart);
			yStart += Minecraft.getInstance().font.lineHeight;
		}
	}

	private static void drawCentered(PoseStack pose, Component str, float x, float y){
		Font font = Minecraft.getInstance().font;
		//noinspection IntegerDivisionInFloatingPointContext
		font.drawShadow(pose, str, x-font.width(str)/2, y, 0xF0F0F0);
	}
}
