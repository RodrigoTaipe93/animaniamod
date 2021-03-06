package com.animania.common.entities.generic.ai;

import java.util.Collections;
import java.util.Set;

import com.animania.api.interfaces.IFoodEating;
import com.animania.api.interfaces.ISleeping;
import com.animania.common.helper.AnimaniaHelper;
import com.google.common.collect.Sets;

import net.minecraft.entity.EntityCreature;
import net.minecraft.entity.ai.EntityAITempt;
import net.minecraft.entity.passive.EntityTameable;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;

public class GenericAITempt<T extends EntityCreature & ISleeping & IFoodEating> extends EntityAITempt
{
	/** The entity using this AI that is tempted by the player. */
	private final T temptedEntity;

	private Set<ItemStack> temptSet;

	public GenericAITempt(T temptedEntityIn, double speedIn, ItemStack temptItemIn, boolean scaredByPlayerMovementIn)
	{
		super(temptedEntityIn, speedIn, Items.AIR, scaredByPlayerMovementIn);
		this.temptedEntity = temptedEntityIn;
		this.temptSet = Sets.newHashSet(temptItemIn.copy());
	}

	public GenericAITempt(T temptedEntityIn, double speedIn, boolean scaredByPlayerMovementIn, Set<ItemStack> temptItemIn)
	{
		super(temptedEntityIn, speedIn, scaredByPlayerMovementIn, Collections.EMPTY_SET);
		this.temptedEntity = temptedEntityIn;

		this.temptSet = Sets.newHashSet();
		for (ItemStack s : temptItemIn)
			temptSet.add(s.copy());
	}

	@Override
	protected boolean isTempting(ItemStack stack)
	{
		return AnimaniaHelper.containsItemStack(temptSet, stack);
	}

	/**
	 * Returns whether the EntityAIBase should begin execution.
	 */
	@Override
	public boolean shouldExecute()
	{
		if (temptedEntity.getSleeping())
			return false;

		if (temptedEntity instanceof EntityTameable && ((EntityTameable) temptedEntity).isSitting())
			return false;

		return super.shouldExecute();
	}

	@Override
	public void updateTask()
	{
		if (!temptedEntity.getInteracted())
			temptedEntity.setInteracted(true);

		super.updateTask();
	}
}