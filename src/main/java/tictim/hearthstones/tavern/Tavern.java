package tictim.hearthstones.tavern;

import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.common.util.Constants;
import net.minecraftforge.fml.common.registry.ForgeRegistries;

import javax.annotation.Nullable;

public interface Tavern{
	TavernPos pos();
	BlockPos blockPos();

	TavernType type();
	Owner owner();
	AccessModifier access();

	@Nullable String name();

	boolean isMissing();

	@Nullable IBlockState skin();

	default boolean hasAccessPermission(EntityPlayer player){
		return access().hasAccessPermission(player, owner());
	}
	default boolean hasModifyPermission(EntityPlayer player){
		return access().hasModifyPermission(player, owner());
	}
	default Accessibility getAccessibility(EntityPlayer player){
		return hasModifyPermission(player) ?
				owner().hasOwner()&&owner().isOwnerOrOp(player) ? Accessibility.MODIFIABLE : Accessibility.PARTIALLY_MODIFIABLE :
				Accessibility.READ_ONLY;
	}

	default TavernRecord toRecord(){
		return new TavernRecord(this);
	}
	default Tavern withMissingSet(boolean missing){
		return isMissing()==missing ? this : new TavernRecord(this, missing);
	}

	static IBlockState getDefaultSkin(){
		return Blocks.LOG.getDefaultState();
	}

	@SuppressWarnings("deprecation") @Nullable static IBlockState readSkin(NBTTagCompound tag){
		if(tag.hasKey("skin", Constants.NBT.TAG_STRING)){
			Block block = ForgeRegistries.BLOCKS.getValue(new ResourceLocation(tag.getString("skin")));
			if(block!=null&&block!=Blocks.AIR){
				try{
					return block.getStateFromMeta(Byte.toUnsignedInt(tag.getByte("skinMeta")));
				}catch(RuntimeException ex){ // nonexistent property etc.
					return block.getDefaultState();
				}
			}
		}
		return null;
	}

	static void writeSkin(NBTTagCompound tag, @Nullable IBlockState skin){
		if(skin==null||skin.getBlock().getRegistryName()==null) return;
		tag.setString("skin", skin.getBlock().getRegistryName().toString());
		byte meta = (byte)skin.getBlock().getMetaFromState(skin);
		if(meta!=0) tag.setByte("skinMeta", meta);
	}
}