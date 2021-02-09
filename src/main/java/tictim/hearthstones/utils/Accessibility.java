package tictim.hearthstones.utils;

public enum Accessibility{
	MODIFIABLE,
	PARTIALLY_MODIFIABLE,
	READ_ONLY;

	public boolean isModifiable(){
		return this!=READ_ONLY;
	}
	public boolean isAccessibilityModifiable(){
		return this==MODIFIABLE;
	}

	public int getMeta(){
		switch(this){
			case MODIFIABLE:
				return 0;
			case PARTIALLY_MODIFIABLE:
				return 1;
			case READ_ONLY:
				return -1;
			default:
				throw new IllegalStateException(name());
		}
	}

	public static Accessibility fromMeta(int meta){
		switch(meta){
			case 0:
				return MODIFIABLE;
			case 1:
				return PARTIALLY_MODIFIABLE;
			default:
				return READ_ONLY;
		}
	}
}
