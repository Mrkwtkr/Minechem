package minechem.item.element;

import java.util.EnumMap;
import java.util.List;

import minechem.MinechemItemsRegistration;
import minechem.Minechem;
import minechem.fluid.FluidHelper;
import minechem.fluid.IMinechemFluid;
import minechem.item.polytool.PolytoolHelper;
import minechem.radiation.RadiationEnum;
import minechem.radiation.RadiationInfo;
import minechem.utils.Constants;
import minechem.utils.EnumColor;
import minechem.utils.MinechemHelper;
import minechem.utils.Reference;
import net.minecraft.block.Block;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.DamageSource;
import net.minecraft.util.IIcon;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.util.StatCollector;
import net.minecraft.world.World;

import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.IFluidBlock;
import net.minecraftforge.fluids.IFluidContainerItem;
import org.lwjgl.input.Keyboard;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class ElementItem extends Item implements IFluidContainerItem
{

    private final static ElementEnum[] elements = ElementEnum.values();
    private final EnumMap<ElementClassificationEnum, Integer> classificationIndexes = new EnumMap<ElementClassificationEnum, Integer>(ElementClassificationEnum.class);
    public final IIcon liquid[] = new IIcon[7], gas[] = new IIcon[7];
    public IIcon solid;

    public ElementItem()
    {
        setCreativeTab(Minechem.CREATIVE_TAB_ELEMENTS);
        setUnlocalizedName("itemElement");
        setHasSubtypes(true);
        classificationIndexes.put(ElementClassificationEnum.nonmetal, 0);
        classificationIndexes.put(ElementClassificationEnum.halogen, 1);
        classificationIndexes.put(ElementClassificationEnum.inertGas, 2);
        classificationIndexes.put(ElementClassificationEnum.semimetallic, 3);
        classificationIndexes.put(ElementClassificationEnum.otherMetal, 4);
        classificationIndexes.put(ElementClassificationEnum.alkaliMetal, 5);
        classificationIndexes.put(ElementClassificationEnum.alkalineEarthMetal, 6);
        classificationIndexes.put(ElementClassificationEnum.transitionMetal, 7);
        classificationIndexes.put(ElementClassificationEnum.lanthanide, 8);
        classificationIndexes.put(ElementClassificationEnum.actinide, 9);
        classificationIndexes.put(ElementClassificationEnum.gas, 1);
        classificationIndexes.put(ElementClassificationEnum.solid, 17);
        classificationIndexes.put(ElementClassificationEnum.liquid, 33);
    }

    public static String getShortName(ItemStack itemstack)
    {
        int atomicNumber = itemstack.getItemDamage();
        return atomicNumber < ElementEnum.heaviestMass ? elements[atomicNumber].name() : MinechemHelper.getLocalString("element.empty");
    }

    public static String getLongName(ItemStack itemstack)
    {
        int atomicNumber = itemstack.getItemDamage();
        return atomicNumber < ElementEnum.heaviestMass ? elements[atomicNumber].descriptiveName() : MinechemHelper.getLocalString("element.empty");
    }

    public static String getClassification(ItemStack itemstack)
    {
        int atomicNumber = itemstack.getItemDamage();
        return atomicNumber < ElementEnum.heaviestMass ? elements[atomicNumber].classification().descriptiveName() : MinechemHelper.getLocalString("element.empty");
    }

    public static String getRoomState(ItemStack itemstack)
    {
        int atomicNumber = itemstack.getItemDamage();
        return atomicNumber < ElementEnum.heaviestMass ? elements[atomicNumber].roomState().descriptiveName() : MinechemHelper.getLocalString("element.empty");
    }

    public static RadiationEnum getRadioactivity(ItemStack itemstack)
    {
        int atomicNumber = itemstack.getItemDamage();
        return atomicNumber < ElementEnum.heaviestMass ? elements[atomicNumber].radioactivity() : RadiationEnum.stable;
    }

    public static ElementEnum getElement(ItemStack itemstack)
    {
        return itemstack.getItemDamage() < ElementEnum.heaviestMass ? ElementEnum.elements[itemstack.getItemDamage()] : null;
    }

    public static void attackEntityWithRadiationDamage(ItemStack itemstack, int damage, Entity entity)
    {
        entity.attackEntityFrom(DamageSource.generic, damage);
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void registerIcons(IIconRegister ir)
    {
        itemIcon = ir.registerIcon(Reference.FILLED_TESTTUBE_TEX);
        gas[0] = ir.registerIcon(Reference.ELEMENT_GAS1_TEX);
        gas[1] = ir.registerIcon(Reference.ELEMENT_GAS2_TEX);
        gas[2] = ir.registerIcon(Reference.ELEMENT_GAS3_TEX);
        gas[3] = ir.registerIcon(Reference.ELEMENT_GAS4_TEX);
        gas[4] = ir.registerIcon(Reference.ELEMENT_GAS5_TEX);
        gas[5] = ir.registerIcon(Reference.ELEMENT_GAS6_TEX);
        gas[6] = ir.registerIcon(Reference.ELEMENT_GAS7_TEX);
        liquid[0] = ir.registerIcon(Reference.ELEMENT_LIQUID1_TEX);
        liquid[1] = ir.registerIcon(Reference.ELEMENT_LIQUID2_TEX);
        liquid[2] = ir.registerIcon(Reference.ELEMENT_LIQUID3_TEX);
        liquid[3] = ir.registerIcon(Reference.ELEMENT_LIQUID4_TEX);
        liquid[4] = ir.registerIcon(Reference.ELEMENT_LIQUID5_TEX);
        liquid[5] = ir.registerIcon(Reference.ELEMENT_LIQUID6_TEX);
        liquid[6] = ir.registerIcon(Reference.ELEMENT_LIQUID7_TEX);
        solid = ir.registerIcon(Reference.ELEMENT_SOLID_TEX);
    }

    @Override
    public String getUnlocalizedName(ItemStack itemStack)
    {
        return "minechem.itemElement." + getShortName(itemStack);
    }

    @Override
    public String getItemStackDisplayName(ItemStack itemStack)
    {
        return getLongName(itemStack);
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void addInformation(ItemStack itemstack, EntityPlayer player, List list, boolean bool)
    {
        if (itemstack.getItemDamage() == ElementEnum.heaviestMass)
        {
            return;
        }

        list.add(Constants.TEXT_MODIFIER + "9" + getShortName(itemstack) + " (" + (itemstack.getItemDamage() + 1) + ")");

        String radioactivityColor;
        String timeLeft = getRadioactiveLife(itemstack);
        if (!timeLeft.equals(""))
        {
            timeLeft = "(" + timeLeft + ")";
        }
        RadiationEnum radioactivity = getRadioactivity(itemstack);
        switch (radioactivity)
        {
            case stable:
                radioactivityColor = Constants.TEXT_MODIFIER + "7";
                break;
            case hardlyRadioactive:
                radioactivityColor = Constants.TEXT_MODIFIER + "a";
                break;
            case slightlyRadioactive:
                radioactivityColor = Constants.TEXT_MODIFIER + "2";
                break;
            case radioactive:
                radioactivityColor = Constants.TEXT_MODIFIER + "e";
                break;
            case highlyRadioactive:
                radioactivityColor = Constants.TEXT_MODIFIER + "6";
                break;
            case extremelyRadioactive:
                radioactivityColor = Constants.TEXT_MODIFIER + "4";
                break;
            default:
                radioactivityColor = "";
                break;
        }

        String radioactiveName = MinechemHelper.getLocalString("element.property." + radioactivity.name());
        list.add(radioactivityColor + radioactiveName + " " + timeLeft);
        list.add(getClassification(itemstack));
        list.add(getRoomState(itemstack));

        if (PolytoolHelper.getTypeFromElement(ElementItem.getElement(itemstack), 1) != null)
        {
            // Polytool Detail
            if (Keyboard.isKeyDown(Keyboard.KEY_LSHIFT) || Keyboard.isKeyDown(Keyboard.KEY_RSHIFT))
            {
                String polytoolDesc = PolytoolHelper.getTypeFromElement(ElementItem.getElement(itemstack), 1).getDescription();
                String localizedDesc=StatCollector.translateToLocal("polytool.description."+ElementItem.getShortName(itemstack));
              	 
                 if(!StatCollector.canTranslate("polytool.description."+ElementItem.getShortName(itemstack)))
                 {
                	 localizedDesc = polytoolDesc;
                 }
                 
                list.add(EnumColor.AQUA + localizedDesc);

            } else
            {
                list.add(EnumColor.DARK_GREEN + MinechemHelper.getLocalString("polytool.information"));
            }
        }

    }

    private String getRadioactiveLife(ItemStack itemstack)
    {
        String timeLeft = "";
        if (getRadioactivity(itemstack) != RadiationEnum.stable && itemstack.getTagCompound() != null)
        {
            NBTTagCompound tagCompound = itemstack.getTagCompound();
            long life = tagCompound.getLong("life");
            String hourabbr = MinechemHelper.getLocalString("minechem.hour.abbr");
            String minabbr = MinechemHelper.getLocalString("minechem.min.abbr");
            String secabbr = MinechemHelper.getLocalString("minechem.sec.abbr");
            
            if (life < Constants.TICKS_PER_DAY)
			{
				int hrs = (int) (life/Constants.TICKS_PER_HOUR);
				life = life - (Constants.TICKS_PER_HOUR * hrs);
				if (hrs>0)
				{
					if(hourabbr.isEmpty() || hourabbr == "minechem.hour.abbr")
						timeLeft = hrs + "hr ";
					else
						timeLeft = hrs + hourabbr + " ";
				}
			}
			if (life < Constants.TICKS_PER_HOUR)
			{
				int mins = (int) (life/Constants.TICKS_PER_MINUTE);
				life = life - (Constants.TICKS_PER_MINUTE * mins);
				if (mins>0)
				{
					if(minabbr.isEmpty() || minabbr == "minechem.min.abbr")
						timeLeft = timeLeft + mins + "m ";
					else
						timeLeft = timeLeft + mins + minabbr + " ";
				}
			}
			if (life < Constants.TICKS_PER_MINUTE)
			{
				int secs = (int) (life/Constants.TICKS_PER_SECOND);
				life = life - (Constants.TICKS_PER_SECOND * secs);
				if (secs>0)
				{
					if(secabbr.isEmpty() || secabbr == "minechem.sec.abbr")
						timeLeft = timeLeft + secs + "s";
					else
						timeLeft = timeLeft + secs + secabbr;
				}
			}
      }
        return timeLeft;
    }

    @Override
    public int getMetadata(int par1)
    {
        return par1;
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void getSubItems(Item item, CreativeTabs creativeTabs, List list)
    {
        list.add(new ItemStack(item, 1, ElementEnum.heaviestMass));
        for (ElementEnum element : ElementEnum.values())
        {
            list.add(new ItemStack(item, 1, element.ordinal()));
        }
    }

    public static Object createStackOf(ElementEnum element, int amount)
    {
        return new ItemStack(MinechemItemsRegistration.element, amount, element.ordinal());
    }

    public static RadiationInfo getRadiationInfo(ItemStack element, World world)
    {
        RadiationEnum radioactivity = getRadioactivity(element);
        if (radioactivity == RadiationEnum.stable)
        {
            return new RadiationInfo(element, radioactivity);
        } else
        {
            NBTTagCompound stackTag = element.getTagCompound();
            if (stackTag == null)
            {
                return initiateRadioactivity(element, world);
            } else
            {
                int dimensionID = stackTag.getInteger("dimensionID");
                long lastUpdate = stackTag.getLong("lastUpdate");
                long life = stackTag.getLong("life");
                RadiationInfo info = new RadiationInfo(element, life, lastUpdate, dimensionID, radioactivity);
                return info;
            }
        }
    }

    public static RadiationInfo initiateRadioactivity(ItemStack element, World world)
    {
        RadiationEnum radioactivity = getRadioactivity(element);
        int dimensionID = world.provider.dimensionId;
        long lastUpdate = world.getTotalWorldTime();
        long life = radioactivity.getLife();
        RadiationInfo info = new RadiationInfo(element, life, lastUpdate, dimensionID, radioactivity);
        setRadiationInfo(info, element);
        return info;
    }

    public static void setRadiationInfo(RadiationInfo radiationInfo, ItemStack element)
    {
        NBTTagCompound stackTag = element.getTagCompound();
        if (stackTag == null)
        {
            stackTag = new NBTTagCompound();
        }
        stackTag.setLong("lastUpdate", radiationInfo.lastRadiationUpdate);
        stackTag.setLong("life", radiationInfo.radiationLife);
        stackTag.setInteger("dimensionID", radiationInfo.dimensionID);
        element.setTagCompound(stackTag);
    }

    public static RadiationInfo decay(ItemStack element, World world)
    {
        int atomicMass = element.getItemDamage();
        element.setItemDamage(atomicMass - 1);
        return initiateRadioactivity(element, world);
    }

    @Override
    public FluidStack getFluid(ItemStack container)
    {
        if (container.getItemDamage() != ElementEnum.heaviestMass)
        {
            return new FluidStack(FluidHelper.elements.get(elements[container.getItemDamage()]), getCapacity(container));
        }
        return null;
    }

    @Override
    public int getCapacity(ItemStack container)
    {
        if (container.getItemDamage() != ElementEnum.heaviestMass)
        {
            return 1000;
        }
        return 0;
    }

    @Override
    public int fill(ItemStack container, FluidStack resource, boolean doFill)
    {
        return 0;
    }

    @Override
    public FluidStack drain(ItemStack container, int maxDrain, boolean doDrain)
    {
        if (container.getItemDamage() != ElementEnum.heaviestMass)
        {
            FluidStack fluidStack = getFluid(container);
            if (container.stackSize > 0)
            {
                --container.stackSize;
                return fluidStack;
            }
        }
        return null;
    }

    @Override
    public ItemStack onItemRightClick(ItemStack itemStack, World world, EntityPlayer player)
    {
        boolean flag = itemStack.getItemDamage() == ElementEnum.heaviestMass;
        MovingObjectPosition movingObjectPosition = this.getMovingObjectPositionFromPlayer(world, player, flag);
        if (movingObjectPosition == null)
        {
            return itemStack;
        }

        if (movingObjectPosition.typeOfHit == MovingObjectPosition.MovingObjectType.BLOCK)
        {
            int blockX = movingObjectPosition.blockX;
            int blockY = movingObjectPosition.blockY;
            int blockZ = movingObjectPosition.blockZ;

            Block block = world.getBlock(blockX, blockY,blockZ);
            int meta = world.getBlockMetadata(blockX, blockY, blockZ);

            if(flag)
            {
                if (block instanceof IFluidBlock && meta == 0)
                {
                    if (((IFluidBlock)block).getFluid() instanceof IMinechemFluid)
                    {
                        world.setBlockToAir(blockX, blockY, blockZ);
                        return fillTube(itemStack, player, ((IMinechemFluid) ((IFluidBlock) block).getFluid()).getOutputStack());
                    }
                }
            }
            else
            {
                if (movingObjectPosition.sideHit == 0)
                {
                    --blockY;
                }

                if (movingObjectPosition.sideHit == 1)
                {
                    ++blockY;
                }

                if (movingObjectPosition.sideHit == 2)
                {
                    --blockZ;
                }

                if (movingObjectPosition.sideHit == 3)
                {
                    ++blockZ;
                }

                if (movingObjectPosition.sideHit == 4)
                {
                    --blockX;
                }

                if (movingObjectPosition.sideHit == 5)
                {
                    ++blockX;
                }

                if (!player.canPlayerEdit(blockX, blockY,blockZ, movingObjectPosition.sideHit, itemStack))
                {
                    return itemStack;
                }

                return emptyTube(itemStack, player, world, blockX, blockY, blockZ);
            }
        }

        return itemStack;
    }

    private ItemStack fillTube(ItemStack itemStack, EntityPlayer player, ItemStack block)
    {
        if (player.capabilities.isCreativeMode)
        {
            return itemStack;
        }
        else if (--itemStack.stackSize <= 0)
        {
            return block;
        }
        else
        {
            if (!player.inventory.addItemStackToInventory(block))
            {
                player.dropPlayerItemWithRandomChoice(block, false);
            }
        }
        return itemStack;
    }

    private ItemStack emptyTube(ItemStack itemStack, EntityPlayer player, World world, int x, int y, int z)
    {
        if (world.isAirBlock(x, y, z))
        {
            Block block = FluidHelper.elementsBlocks.get(FluidHelper.elements.get(getElement(itemStack)));
            world.setBlock(x, y, z, block, 0, 3);
            if (player.capabilities.isCreativeMode)
            {
                return itemStack;
            } else if (--itemStack.stackSize <= 0)
            {
                return new ItemStack(MinechemItemsRegistration.element, 1, ElementEnum.heaviestMass);
            } else
            {
                if (!player.inventory.addItemStackToInventory(new ItemStack(MinechemItemsRegistration.element, 1, ElementEnum.heaviestMass)))
                {
                    player.dropPlayerItemWithRandomChoice(new ItemStack(MinechemItemsRegistration.element, 1, ElementEnum.heaviestMass), false);
                }
            }
        }
        return itemStack;
    }
}
