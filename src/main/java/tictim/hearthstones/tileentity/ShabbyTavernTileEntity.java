package tictim.hearthstones.tileentity;

import net.minecraft.item.ItemStack;
import tictim.hearthstones.contents.ModItems;
import tictim.hearthstones.contents.ModTileEntities;
import tictim.hearthstones.utils.HearthingContext;
import tictim.hearthstones.utils.TavernType;

public class ShabbyTavernTileEntity extends BaseTavernTileEntity{
	public ShabbyTavernTileEntity(){
		super(ModTileEntities.SHABBY_TAVERN.get());
	}

	@Override public TavernType tavernType(){
		return TavernType.SHABBY;
	}
	@Override public boolean canBeUpgraded(){
		return true;
	}
	@Override public ItemStack createUpgradeItem(){
		return new ItemStack(ModItems.TATTERED_TAVERNCLOTH.get());
	}

	@Override public boolean canTeleportTo(HearthingContext ctx){
		return super.canTeleportTo(ctx)&&ctx.getStack().getItem()==ModItems.HEARTHING_PLANKS.get();
	}
}
