package tictim.hearthstones.contents.item;

import net.minecraft.ChatFormatting;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Block;

public class TavernItem extends BlockItem{
	public TavernItem(Block block, Properties properties){
		super(block, properties);
	}

	@Override public Component getName(ItemStack stack){
		if(!stack.hasCustomHoverName()){
			CompoundTag tag = stack.getTag();
			if(tag!=null&&tag.contains("BlockEntityTag", Tag.TAG_COMPOUND)){
				CompoundTag nbt = tag.getCompound("BlockEntityTag");
				if(nbt.contains("name", Tag.TAG_STRING)){
					return new TextComponent(nbt.getString("name")).withStyle(ChatFormatting.ITALIC);
				}
			}
		}
		return super.getName(stack);
	}
}
