package tictim.hearthstones.net;

import tictim.hearthstones.data.TavernPos;

public class TavernMemoryOperation{
	public static final byte SELECT = 1;
	public static final byte DELETE = 2;
	public static final byte SET_HOME = 3;

	public final TavernPos pos;
	public final byte operation;

	public TavernMemoryOperation(TavernPos pos, byte operation){
		this.pos = pos;
		this.operation = operation;
	}
}
