package tictim.hearthstones.client;

import net.minecraft.block.BlockHorizontal;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.client.renderer.RenderItem;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.block.model.IBakedModel;
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraftforge.client.ForgeHooksClient;
import net.minecraftforge.client.MinecraftForgeClient;
import net.minecraftforge.client.event.TextureStitchEvent;
import net.minecraftforge.client.model.IModel;
import net.minecraftforge.client.model.ModelLoaderRegistry;
import net.minecraftforge.common.ForgeModContainer;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.relauncher.Side;
import tictim.hearthstones.Hearthstones;
import tictim.hearthstones.client.model.TavernBlockModelCache;
import tictim.hearthstones.client.model.TavernModelCache;
import tictim.hearthstones.config.ModCfg;
import tictim.hearthstones.contents.tileentity.TavernTile;
import tictim.hearthstones.tavern.TavernType;

import java.util.Collection;
import java.util.EnumMap;
import java.util.Map;

import static tictim.hearthstones.Hearthstones.MODID;

@Mod.EventBusSubscriber(modid = MODID, value = Side.CLIENT)
public final class TavernRenderer{
	private TavernRenderer(){}

	private static TavernBlockModelCache blockModelCache;
	private static TavernModelCache itemModelCache;

	@SubscribeEvent
	public static void loadSprites(TextureStitchEvent.Pre event){
		if(ModCfg.logModelWarnings)
			Hearthstones.LOGGER.info("Stitching textureModel sprites");

		TextureMap textureMap = Minecraft.getMinecraft().getTextureMapBlocks();

		Map<TavernType, IModel> taverncloth = new EnumMap<>(TavernType.class);
		for(TavernType tavernType : TavernType.values()){
			IModel model = ModelLoaderRegistry.getModelOrLogError(new ResourceLocation(MODID, tavernType==TavernType.NORMAL ? "block/tavern" : "block/"+tavernType.getName()+"_tavern"), "Couldn't read model data.");
			registerTexture(textureMap, model.getTextures());
			taverncloth.put(tavernType, model);
		}

		IModel body = ModelLoaderRegistry.getModelOrLogError(new ResourceLocation(MODID, "block/tavern_body"), "Couldn't read model data.");
		registerTexture(textureMap, body.getTextures());

		blockModelCache = new TavernBlockModelCache(taverncloth, body, DefaultVertexFormats.BLOCK);
		itemModelCache = new TavernModelCache(taverncloth, body, DefaultVertexFormats.ITEM, EnumFacing.NORTH);

		if(ModCfg.logModelWarnings)
			Hearthstones.LOGGER.info("Stitching textureModel sprites end");
	}

	private static void registerTexture(TextureMap textureMap, Collection<ResourceLocation> textures){
		for(ResourceLocation t : textures)
			if(!TextureMap.LOCATION_MISSING_TEXTURE.equals(t))
				textureMap.registerSprite(t);
	}

	public static void renderBlock(TavernTile te, TextureManager textureManager, int destroyStage, float alpha){
		boolean renderDestroyStage = destroyStage>=0;
		Tessellator tessellator = Tessellator.getInstance();
		BufferBuilder buffer = tessellator.getBuffer();
		BlockPos pos = te.getPos();
		IBlockAccess world = MinecraftForgeClient.getRegionRenderCache(te.getWorld(), pos);
		IBlockState state = world.getBlockState(pos);
		RenderHelper.disableStandardItemLighting();
		GlStateManager.shadeModel(!renderDestroyStage&&!Minecraft.isAmbientOcclusionEnabled() ? 7424 : 7425);
		EnumFacing facing = state.getPropertyKeys().contains(BlockHorizontal.FACING) ? state.getValue(BlockHorizontal.FACING) : EnumFacing.NORTH;
		IBakedModel model = blockModelCache.getDestroyStageModel(destroyStage, facing, te);
		textureManager.bindTexture(TextureMap.LOCATION_BLOCKS_TEXTURE);
		if(renderDestroyStage){
			textureManager.getTexture(TextureMap.LOCATION_BLOCKS_TEXTURE).setBlurMipmap(false, false);
		}else{
			GlStateManager.blendFunc(770, 771);
			GlStateManager.enableBlend();
		}

		GlStateManager.color(1.0F, 1.0F, 1.0F, alpha);
		buffer.begin(7, DefaultVertexFormats.BLOCK);
		if(renderDestroyStage) buffer.noColor();

		buffer.setTranslation(-pos.getX(), -pos.getY(), -pos.getZ());
		Minecraft.getMinecraft().getBlockRendererDispatcher().getBlockModelRenderer().renderModel(world, model, state, pos, buffer, renderDestroyStage);
		buffer.setTranslation(0.0D, 0.0D, 0.0D);
		tessellator.draw();
		GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
		if(renderDestroyStage){
			textureManager.getTexture(TextureMap.LOCATION_BLOCKS_TEXTURE).restoreLastBlurMipmap();
		}
	}

	public static void renderItem(TavernType type, IBlockState skin, ItemStack stack, TextureManager textureManager){
		textureManager.bindTexture(TextureMap.LOCATION_BLOCKS_TEXTURE);
		IBakedModel model = itemModelCache.getModel(type, skin);
		RenderItem renderItem = Minecraft.getMinecraft().getRenderItem();
		if(ForgeModContainer.allowEmissiveItems){
			ForgeHooksClient.renderLitItem(renderItem, model, -1, stack);
		}else{
			Tessellator tessellator = Tessellator.getInstance();
			BufferBuilder buffer = tessellator.getBuffer();
			buffer.begin(7, DefaultVertexFormats.ITEM);

			for(EnumFacing facing : EnumFacing.values())
				renderItem.renderQuads(buffer, model.getQuads(null, facing, 0), -1, stack);

			renderItem.renderQuads(buffer, model.getQuads(null, null, 0), -1, stack);
			tessellator.draw();
		}
	}
}
