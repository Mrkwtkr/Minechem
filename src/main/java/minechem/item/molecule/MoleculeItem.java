package minechem.item.molecule;

import java.util.ArrayList;
import java.util.List;

import minechem.Minechem;
import minechem.MinechemItemsRegistration;
import minechem.fluid.FluidHelper;
import minechem.item.element.ElementEnum;
import minechem.item.element.ElementItem;
import minechem.potion.PotionPharmacologyEffect;
import minechem.utils.MinechemHelper;
import minechem.utils.Reference;
import net.minecraft.block.Block;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.EnumAction;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.IIcon;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.world.World;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.IFluidContainerItem;

public class MoleculeItem extends Item implements IFluidContainerItem
{
    public IIcon render_pass1, render_pass2, filledMolecule;

    public MoleculeItem()
    {
        setCreativeTab(Minechem.CREATIVE_TAB_ELEMENTS);
        setHasSubtypes(true);
        setUnlocalizedName("itemMolecule");
    }


    @Override
    public String getItemStackDisplayName(ItemStack par1ItemStack)
    {
        int itemDamage = par1ItemStack.getItemDamage();
        return MoleculeEnum.getById(itemDamage).descriptiveName();
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void registerIcons(IIconRegister ir)
    {
        itemIcon = ir.registerIcon(Reference.FILLED_TESTTUBE_TEX);
        render_pass1 = ir.registerIcon(Reference.MOLECULE_PASS1_TEX);
        render_pass2 = ir.registerIcon(Reference.MOLECULE_PASS2_TEX);
        filledMolecule = ir.registerIcon(Reference.FILLED_MOLECULE_TEX);
    }

    public ArrayList<ItemStack> getElements(ItemStack itemstack)
    {
        MoleculeEnum molecule = MoleculeEnum.getById(itemstack.getItemDamage());
        return MinechemHelper.convertChemicalsIntoItemStacks(molecule.components());
    }

    @Override
    public String getUnlocalizedName(ItemStack par1ItemStack)
    {
        return getUnlocalizedName() + "." + getMolecule(par1ItemStack).name();
    }

    public String getFormula(ItemStack itemstack)
    {
        ArrayList<ItemStack> components = getElements(itemstack);
        String formula = "";
        for (ItemStack component : components)
        {
            if (component.getItem() instanceof ElementItem)
            {
                formula += ElementItem.getShortName(component);
                if (component.stackSize > 1)
                    formula += component.stackSize;
            }
            else if (component.getItem() instanceof MoleculeItem)
            {
                if (component.stackSize > 1)
                    formula += "(";
                formula += getFormula(component);
                if (component.stackSize > 1)
                    formula += ")" + component.stackSize;
            }
        }
        return formula;
    }

    public String getFormulaWithSubscript(ItemStack itemstack)
    {
        String formula = getFormula(itemstack);
        return subscriptNumbers(formula);
    }

    private static String subscriptNumbers(String string)
    {
        string = string.replace('0', '\u2080');
        string = string.replace('1', '\u2081');
        string = string.replace('2', '\u2082');
        string = string.replace('3', '\u2083');
        string = string.replace('4', '\u2084');
        string = string.replace('5', '\u2085');
        string = string.replace('6', '\u2086');
        string = string.replace('7', '\u2087');
        string = string.replace('8', '\u2088');
        string = string.replace('9', '\u2089');
        return string;
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void addInformation(ItemStack par1ItemStack, EntityPlayer par2EntityPlayer, List par3List, boolean par4)
    {
        par3List.add("\u00A79" + getFormulaWithSubscript(par1ItemStack));
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void getSubItems(Item item, CreativeTabs par2CreativeTabs, List par3List)
    {
        for (MoleculeEnum molecule : MoleculeEnum.molecules)
        {
            par3List.add(new ItemStack(item, 1, molecule.id()));
        }
    }

    public static MoleculeEnum getMolecule(ItemStack itemstack)
    {
        return MoleculeEnum.getById(itemstack.getItemDamage());
    }

    /** returns the action that specifies what animation to play when the items is being used */
    @Override
    public EnumAction getItemUseAction(ItemStack par1ItemStack)
    {
        return EnumAction.drink;
    }

    /** How long it takes to use or consume an item */
    @Override
    public int getMaxItemUseDuration(ItemStack par1ItemStack)
    {
        return 16;
    }

    @Override
    public ItemStack onEaten(ItemStack itemStack, World world, EntityPlayer entityPlayer)
    {

        --itemStack.stackSize;

        if (world.isRemote)

            return itemStack;

        MoleculeEnum molecule = getMolecule(itemStack);
        PotionPharmacologyEffect.triggerPlayerEffect(molecule, entityPlayer);
        world.playSoundAtEntity(entityPlayer, "random.burp", 0.5F, world.rand.nextFloat() * 0.1F + 0.9F); // Thanks mDiyo!
        return itemStack;
    }

    /** Returns True is the item is renderer in full 3D when hold. */
    @Override
    public boolean isFull3D()
    {
        return true;
    }

    @Override
    public FluidStack getFluid(ItemStack container)
    {
        return new FluidStack(FluidHelper.molecule.get(MoleculeEnum.getById(container.getItemDamage())), 100);
    }

    @Override
    public int getCapacity(ItemStack container)
    {
        return 100;
    }

    @Override
    public int fill(ItemStack container, FluidStack resource, boolean doFill)
    {
        return 0;
    }

    @Override
    public FluidStack drain(ItemStack container, int maxDrain, boolean doDrain)
    {
        return getFluid(container);
    }

    @Override
    public ItemStack onItemRightClick(ItemStack itemStack, World world, EntityPlayer player)
    {
        player.setItemInUse(itemStack, getMaxItemUseDuration(itemStack));

        MovingObjectPosition movingObjectPosition = this.getMovingObjectPositionFromPlayer(world, player, false);
        if (movingObjectPosition == null)
        {
            return itemStack;
        }

        if (movingObjectPosition.typeOfHit == MovingObjectPosition.MovingObjectType.BLOCK)
        {
            int blockX = movingObjectPosition.blockX;
            int blockY = movingObjectPosition.blockY;
            int blockZ = movingObjectPosition.blockZ;

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

        return itemStack;
    }

    private ItemStack fillTube(ItemStack itemStack, EntityPlayer player, int meta)
    {
        if (player.capabilities.isCreativeMode)
        {
            return itemStack;
        }
        else if (--itemStack.stackSize <= 0)
        {
            return new ItemStack(MinechemItemsRegistration.element, 1, meta);
        }
        else
        {
            if (!player.inventory.addItemStackToInventory(new ItemStack(MinechemItemsRegistration.element, 1, meta)))
            {
                player.dropPlayerItemWithRandomChoice(new ItemStack(MinechemItemsRegistration.element, 1, meta), false);
            }
        }
        return itemStack;
    }

    private ItemStack emptyTube(ItemStack itemStack, EntityPlayer player, World world, int x, int y, int z)
    {
        if (world.isAirBlock(x, y, z))
        {
            Block block = FluidHelper.moleculeBlocks.get(FluidHelper.molecule.get(getMolecule(itemStack)));
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
