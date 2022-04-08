package tictim.hearthstones;

import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityManager;
import net.minecraftforge.common.capabilities.CapabilityToken;
import tictim.hearthstones.contents.item.hearthstone.HearthstoneItem;
import tictim.hearthstones.tavern.TavernBinderData;
import tictim.hearthstones.tavern.TavernMemories;

public final class Caps{
	private Caps(){}

	public static final Capability<TavernMemories> TAVERN_MEMORIES = CapabilityManager.get(new CapabilityToken<>(){});
	public static final Capability<HearthstoneItem.Data> HEARTHSTONE_DATA = CapabilityManager.get(new CapabilityToken<>(){});
	public static final Capability<TavernBinderData> BINDER_DATA = CapabilityManager.get(new CapabilityToken<>(){});
}
