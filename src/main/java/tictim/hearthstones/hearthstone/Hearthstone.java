package tictim.hearthstones.hearthstone;

import tictim.hearthstones.tavern.Tavern;

import javax.annotation.Nullable;

public interface Hearthstone{
	int getMaxDamage();

	@Nullable Tavern previewWarp(WarpContext context);
	@Nullable WarpSetup setupWarp(WarpContext context);

	@FunctionalInterface
	interface WarpSetup{
		void warp();
	}
}
