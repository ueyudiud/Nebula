/*
 * copyright 2016-2018 ueyudiud
 */
package nebula.common.block;

import nebula.common.LanguageManager;
import net.minecraft.block.Block;
import net.minecraft.block.SoundType;
import net.minecraft.block.state.IBlockState;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class ItemBlockBase extends ItemBlock
{
	public static boolean placeflag = false;
	
	public BlockBase block;
	
	public ItemBlockBase(BlockBase block)
	{
		super(block);
		this.block = block;
		this.hasSubtypes = true;
	}
	
	@Override
	public String getUnlocalizedName(ItemStack stack)
	{
		return this.block.getUnlocalizedName() + "@" + getDamage(stack);
	}
	
	@Override
	public String getItemStackDisplayName(ItemStack stack)
	{
		return LanguageManager.translateLocal(this.block.getTranslateNameForItemStack(stack));
	}
	
	@Override
	public int getMetadata(int damage)
	{
		return damage;
	}
	
	@Override
	public CreativeTabs[] getCreativeTabs()
	{
		return this.block.getCreativeTabs();
	}
	
	/**
	 * Called when a Block is right-clicked with this Item
	 */
	@Override
	public EnumActionResult onItemUse(ItemStack stack, EntityPlayer playerIn, World worldIn, BlockPos pos, EnumHand hand, EnumFacing facing, float hitX, float hitY, float hitZ)
	{
		IBlockState iblockstate = worldIn.getBlockState(pos);
		Block block = iblockstate.getBlock();
		
		/**
		 * The block will be offset if the target block is not replaceable.
		 */
		if (!block.isReplaceable(worldIn, pos) ||
				playerIn.isSneaking())
			// But will force to offset if player is sneaking.
		{
			pos = pos.offset(facing);
		}
		
		if (stack.stackSize > 0 // Minus size also can not use.
				&& playerIn.canPlayerEdit(pos, facing, stack) && worldIn.canBlockBePlaced(this.block, pos, false, facing, (Entity) null, stack))
		{
			if (placeBlockAt(stack, playerIn, worldIn, pos, facing, hitX, hitY, hitZ, null))
			{
				SoundType soundtype = this.block.getSoundType();
				worldIn.playSound(playerIn, pos, soundtype.getPlaceSound(), SoundCategory.BLOCKS, (soundtype.getVolume() + 1.0F) / 2.0F, soundtype.getPitch() * 0.8F);
				--stack.stackSize;
			}
			
			return EnumActionResult.SUCCESS;
		}
		else
		{
			return EnumActionResult.FAIL;
		}
	}
	
	@Override
	public boolean placeBlockAt(ItemStack stack, EntityPlayer player, World world, BlockPos pos, EnumFacing side, float hitX, float hitY, float hitZ, IBlockState unused)
	{
		placeflag = true;
		if (!this.block.onPlayerPlacing(world, pos, stack, player, side, hitX, hitY, hitZ))
		{
			placeflag = false;
			return false;
		}
		this.block.onBlockPlacedBy(world, pos, world.getBlockState(pos), player, stack);
		placeflag = false;
		return true;
	}
}
