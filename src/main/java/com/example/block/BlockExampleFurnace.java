/*
 * copyright 2016-2018 ueyudiud
 */
package com.example.block;

import nebula.common.block.BlockSingleTE;
import net.minecraft.block.material.Material;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.registry.GameRegistry;

/**
 * @author ueyudiud
 */
public class BlockExampleFurnace extends BlockSingleTE
{
	public BlockExampleFurnace()
	{
		super("funrnace", Material.ROCK);
	}
	
	@Override
	public void postInitalizedBlocks()
	{
		super.postInitalizedBlocks();
		GameRegistry.registerTileEntity(TEExampleFurnace.class, "ExampleFurnace");
	}
	
	@Override
	public TileEntity createNewTileEntity(World worldIn, int meta)
	{
		return new TEExampleFurnace();
	}
}
