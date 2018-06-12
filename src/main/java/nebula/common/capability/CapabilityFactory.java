/*
 * copyright 2016-2018 ueyudiud
 */
package nebula.common.capability;

import nebula.common.nbt.INBTSelfReaderAndWriter;
import net.minecraft.nbt.NBTBase;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.Capability.IStorage;

/**
 * @author ueyudiud
 */
public class CapabilityFactory
{
	private static final IStorage<INBTSelfReaderAndWriter> STORAGE = new IStorage<INBTSelfReaderAndWriter>()
	{
		@Override
		public NBTBase writeNBT(Capability<INBTSelfReaderAndWriter> capability, INBTSelfReaderAndWriter instance,
				EnumFacing side)
		{
			return instance.writeTo();
		}
		
		@Override
		public void readNBT(Capability<INBTSelfReaderAndWriter> capability, INBTSelfReaderAndWriter instance,
				EnumFacing side, NBTBase nbt)
		{
			instance.readFrom(nbt);
		}
	};
	
	public static <T extends INBTSelfReaderAndWriter<?>> IStorage<T> storage()
	{
		return (IStorage) STORAGE;
	}
}
