package tictim.hearthstones.block;

import net.minecraft.block.BlockState;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.util.NonNullList;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.IBlockReader;
import tictim.hearthstones.config.ModCfg;
import tictim.hearthstones.tileentity.BaseTavernTileEntity;
import tictim.hearthstones.tileentity.ShabbyTavernTileEntity;

import javax.annotation.Nullable;
import java.util.List;

public class ShabbyTavernBlock extends BaseTavernBlock{
	@Override public BaseTavernTileEntity createTileEntity(BlockState state, IBlockReader world){
		return new ShabbyTavernTileEntity();
	}

	@Override protected void addTipInformation(ItemStack stack, @Nullable IBlockReader worldIn, List<ITextComponent> tooltip, ITooltipFlag flagIn){
		super.addTipInformation(stack, worldIn, tooltip, flagIn);
		tooltip.add(new TranslationTextComponent("info.hearthstones.tavern.shabby.tooltip"));
	}

	@Override public void fillItemGroup(ItemGroup group, NonNullList<ItemStack> items){
		if(!ModCfg.easyMode()) items.add(new ItemStack(this));
	}
}
