package com.animania.addons.farm.client.render.sheep;

import org.lwjgl.opengl.GL11;

import com.animania.addons.farm.client.model.sheep.ModelDorsetEwe;
import com.animania.addons.farm.common.entity.sheep.EntityAnimaniaSheep;
import com.animania.addons.farm.common.entity.sheep.SheepDorset.EntityEweDorset;
import com.animania.client.render.layer.LayerBlinking;

import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.entity.Render;
import net.minecraft.client.renderer.entity.RenderLiving;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.entity.passive.EntitySheep;
import net.minecraft.item.EnumDyeColor;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.client.registry.IRenderFactory;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class RenderEweDorset<T extends EntityEweDorset> extends RenderLiving<T>
{
	public static final Factory FACTORY = new Factory();
	private static final String modid = "animania", SheepBaseDir = "textures/entity/sheep/";

	private static final ResourceLocation[] SHEEP_TEXTURES = new ResourceLocation[] { new ResourceLocation(RenderEweDorset.modid, RenderEweDorset.SheepBaseDir + "sheep_dorset_" + "white_ewe.png"), new ResourceLocation(RenderEweDorset.modid, RenderEweDorset.SheepBaseDir + "sheep_dorset_" + "brown_ewe.png") };

	private static final ResourceLocation SHEEP_TEXTURE_BLINK = new ResourceLocation("animania:textures/entity/sheep/sheep_blink.png");
	private static final ResourceLocation[] SHEEP_TEXTURES_SHEARED = new ResourceLocation[] { new ResourceLocation(RenderEweDorset.modid, RenderEweDorset.SheepBaseDir + "sheep_dorset_" + "white_ewe_sheared.png"), new ResourceLocation(RenderEweDorset.modid, RenderEweDorset.SheepBaseDir + "sheep_dorset_" + "brown_ewe_sheared.png") };

	private static int[] EYE_COLORS = new int[]{0xEDEDED, 0x1D1D1D};
	
	private LayerBlinking blinking;
	
	public RenderEweDorset(RenderManager rm)
	{
		super(rm, new ModelDorsetEwe(), 0.5F);
		this.addLayer(blinking = new LayerBlinking(this, SHEEP_TEXTURE_BLINK, 0));
	}

	protected void preRenderScale(EntityEweDorset entity, float f)
	{
		GL11.glScalef(0.58F, 0.58F, 0.58F);
		GL11.glTranslatef(0f, 0f, -0.5f);

		EntityAnimaniaSheep entitySheep = (EntityAnimaniaSheep) entity;
		if (entitySheep.getSleeping())
		{
			this.shadowSize = 0;
			float sleepTimer = entitySheep.getSleepTimer();
			if (sleepTimer > -0.55F)
			{
				sleepTimer = sleepTimer - 0.01F;
			}
			entity.setSleepTimer(sleepTimer);

			GlStateManager.translate(-0.25F, entity.height - 1.05F - sleepTimer, -0.25F);
			GlStateManager.rotate(6.0F, 0.0F, 0.0F, 1.0F);
		}
		else
		{
			this.shadowSize = 0.5F;
			entitySheep.setSleeping(false);
			entitySheep.setSleepTimer(0F);
		}
	}

	@Override
	protected ResourceLocation getEntityTexture(T entity)
	{
		if (entity.posX == -1 && entity.posY == -1 && entity.posZ == -1)
		{
			return SHEEP_TEXTURES[0];
		}

		if (!entity.getSheared())
		{
			return this.SHEEP_TEXTURES[entity.getColorNumber()];
		}
		else
		{
			return this.SHEEP_TEXTURES_SHEARED[entity.getColorNumber()];
		}
	}

	@Override
	protected void preRenderCallback(T entityliving, float f)
	{
		this.preRenderScale(entityliving, f);
		this.blinking.setColors(EYE_COLORS[entityliving.getColorNumber()], EYE_COLORS[entityliving.getColorNumber()]);
		if (entityliving.hasCustomName() && "jeb_".equals(entityliving.getCustomNameTag()) && entityliving.isDyeable())
		{
			int i1 = 25;
			int i = entityliving.ticksExisted / 25 + entityliving.getEntityId();
			int j = EnumDyeColor.values().length;
			int k = i % j;
			int l = (i + 1) % j;
			float q = ((float) (entityliving.ticksExisted % 25) + f) / 25.0F;
			float[] afloat1 = EntitySheep.getDyeRgb(EnumDyeColor.byMetadata(k));
			float[] afloat2 = EntitySheep.getDyeRgb(EnumDyeColor.byMetadata(l));
			((ModelDorsetEwe) this.mainModel).setWoolColor(afloat1[0] * (1.0F - q) + afloat2[0] * q, afloat1[1] * (1.0F - q) + afloat2[1] * q, afloat1[2] * (1.0F - q) + afloat2[2] * q);
		}
		else
		{
			float[] rgb = entityliving.getDyeColor().getColorComponentValues();
			((ModelDorsetEwe) this.mainModel).setWoolColor(rgb[0], rgb[1], rgb[2]);
		}
	}

	static class Factory<T extends EntityEweDorset> implements IRenderFactory<T>
	{
		@Override
		public Render<? super T> createRenderFor(RenderManager manager)
		{
			return new RenderEweDorset(manager);
		}

	}
}
