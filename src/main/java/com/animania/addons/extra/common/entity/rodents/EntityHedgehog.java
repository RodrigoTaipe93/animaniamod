package com.animania.addons.extra.common.entity.rodents;

import net.minecraft.world.World;

public class EntityHedgehog extends EntityHedgehogBase
{

	public EntityHedgehog(World worldIn)
	{
		super(worldIn);
		this.type = HedgehogType.NORMAL;
	}
	
	@Override
	public int getPrimaryEggColor()
	{
		return 10451558;
	}
	
	@Override
	public int getSecondaryEggColor()
	{
		return 14337943;
	}
	
}