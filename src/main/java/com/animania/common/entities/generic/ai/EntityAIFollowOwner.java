package com.animania.common.entities.generic.ai;

import com.animania.common.entities.rodents.EntityFerretBase;
import com.animania.common.entities.rodents.EntityHamster;
import com.animania.common.entities.rodents.EntityHedgehogBase;
import com.animania.config.AnimaniaConfig;

import net.minecraft.block.state.BlockFaceShape;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.ai.EntityAIBase;
import net.minecraft.entity.passive.EntityTameable;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.pathfinding.PathNavigate;
import net.minecraft.pathfinding.PathNavigateFlying;
import net.minecraft.pathfinding.PathNavigateGround;
import net.minecraft.pathfinding.PathNodeType;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;

public class EntityAIFollowOwner extends EntityAIBase
{
	private final EntityTameable tameable;
	private EntityLivingBase owner;
	World world;
	private final double followSpeed;
	private final PathNavigate petPathfinder;
	private int timeToRecalcPath;
	float maxDist;
	float minDist;
	private float oldWaterCost;

	public EntityAIFollowOwner(EntityTameable tameableIn, double followSpeedIn, float minDistIn, float maxDistIn)
	{
		this.tameable = tameableIn;
		this.world = tameableIn.world;
		this.followSpeed = followSpeedIn;
		this.petPathfinder = tameableIn.getNavigator();
		this.minDist = minDistIn;
		this.maxDist = maxDistIn;
		this.setMutexBits(3);

		if (!(tameableIn.getNavigator() instanceof PathNavigateGround) && !(tameableIn.getNavigator() instanceof PathNavigateFlying))
		{
			throw new IllegalArgumentException("Unsupported mob type for FollowOwnerGoal");
		}
	}

	public boolean shouldExecute()
	{

		if (this.tameable instanceof EntityHedgehogBase)
		{
			EntityHedgehogBase entity = (EntityHedgehogBase) this.tameable;
			if (entity.getSleeping())
			{
				return false;
			}
		}

		if (this.tameable instanceof EntityFerretBase)
		{
			EntityFerretBase entity = (EntityFerretBase) this.tameable;
			if (entity.getSleeping())
			{
				return false;
			}
		}

		if (this.tameable instanceof EntityHamster)
		{
			EntityHamster entity = (EntityHamster) this.tameable;
			if (entity.getSleeping())
			{
				return false;
			}
		}

		EntityLivingBase entitylivingbase = this.tameable.getOwner();

		if (entitylivingbase == null)
		{
			return false;
		}
		else if (entitylivingbase instanceof EntityPlayer && ((EntityPlayer)entitylivingbase).isSpectator())
		{
			return false;
		}
		else if (this.tameable.isSitting())
		{
			return false;
		}
		else if (this.tameable.getDistanceSq(entitylivingbase) < (double)(this.minDist * this.minDist))
		{
			return false;
		}
		else
		{
			this.owner = entitylivingbase;
			return true;
		}
	}

	public boolean shouldContinueExecuting()
	{
		return !this.petPathfinder.noPath() && this.tameable.getDistanceSq(this.owner) > (double)(this.maxDist * this.maxDist) && !this.tameable.isSitting();
	}

	public void startExecuting()
	{
		this.timeToRecalcPath = 0;
		this.oldWaterCost = this.tameable.getPathPriority(PathNodeType.WATER);
		this.tameable.setPathPriority(PathNodeType.WATER, 0.0F);
	}

	public void resetTask()
	{
		this.owner = null;
		this.petPathfinder.clearPath();
		this.tameable.setPathPriority(PathNodeType.WATER, this.oldWaterCost);
	}

	public void updateTask()
	{
		this.tameable.getLookHelper().setLookPositionWithEntity(this.owner, 10.0F, (float)this.tameable.getVerticalFaceSpeed());

		if (!this.tameable.isSitting())
		{
			if (--this.timeToRecalcPath <= 0)
			{
				this.timeToRecalcPath = 10;

				if (!this.petPathfinder.tryMoveToEntityLiving(this.owner, this.followSpeed))
				{
					if (!this.tameable.getLeashed() && !this.tameable.isRiding())
					{

						if (this.tameable.getDistanceSq(this.owner) >= 144.0D && AnimaniaConfig.gameRules.tamedAnimalsTeleport)
						{
							int i = MathHelper.floor(this.owner.posX) - 2;
							int j = MathHelper.floor(this.owner.posZ) - 2;
							int k = MathHelper.floor(this.owner.getEntityBoundingBox().minY);

							for (int l = 0; l <= 4; ++l)
							{
								for (int i1 = 0; i1 <= 4; ++i1)
								{
									if ((l < 1 || i1 < 1 || l > 3 || i1 > 3) && this.isTeleportFriendlyBlock(i, j, k, l, i1))
									{
										this.tameable.setLocationAndAngles((double)((float)(i + l) + 0.5F), (double)k, (double)((float)(j + i1) + 0.5F), this.tameable.rotationYaw, this.tameable.rotationPitch);
										this.petPathfinder.clearPath();
										return;
									}
								}
							}
						}

					}
				}
			}
		}
	}

	protected boolean isTeleportFriendlyBlock(int x, int p_192381_2_, int y, int p_192381_4_, int p_192381_5_)
	{
		BlockPos blockpos = new BlockPos(x + p_192381_4_, y - 1, p_192381_2_ + p_192381_5_);
		IBlockState iblockstate = this.world.getBlockState(blockpos);
		return iblockstate.getBlockFaceShape(this.world, blockpos, EnumFacing.DOWN) == BlockFaceShape.SOLID && iblockstate.canEntitySpawn(this.tameable) && this.world.isAirBlock(blockpos.up()) && this.world.isAirBlock(blockpos.up(2));
	}
}