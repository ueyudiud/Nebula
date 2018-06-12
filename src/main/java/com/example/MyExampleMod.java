/*
 * copyright 2016-2018 ueyudiud
 */
package com.example;

import com.example.block.BlockExampleFurnace;

import net.minecraft.creativetab.CreativeTabs;
import net.minecraftforge.fml.common.Mod.EventHandler;
import net.minecraftforge.fml.common.Mod.Instance;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;

/**
 * @author ueyudiud
 */
//@Mod(modid = MyExampleMod.MODID, name = MyExampleMod.NAME, version = MyExampleMod.VERSION)
public class MyExampleMod
{
	public static final String MODID = "my_example_mod";
	public static final String NAME = "MyExampleMod";
	public static final String VERSION = "0.1.0";
	
	@Instance(MODID)
	public MyExampleMod instance;
	
	@EventHandler
	public void load(FMLPreInitializationEvent event)
	{
		new BlockExampleFurnace().setHardness(1.0F).setCreativeTab(CreativeTabs.DECORATIONS);
	}
}
