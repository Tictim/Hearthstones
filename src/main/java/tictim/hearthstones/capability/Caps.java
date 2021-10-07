package tictim.hearthstones.capability;

import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityManager;
import net.minecraftforge.common.capabilities.CapabilityToken;

public final class Caps{
	private Caps(){}

	public static final Capability<TavernMemory> TAVERN_MEMORY = CapabilityManager.get(new CapabilityToken<>(){});
}
