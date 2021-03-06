package minechem.fluid;

import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.registry.GameRegistry;
import minechem.item.element.ElementEnum;
import minechem.item.molecule.MoleculeEnum;
import minechem.utils.Reference;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraftforge.client.event.TextureStitchEvent;

import java.util.HashMap;

public class FluidHelper
{

    public static HashMap<MoleculeEnum, FluidChemical> molecule = new HashMap();
    public static HashMap<ElementEnum, FluidElement> elements = new HashMap();

    public static HashMap<FluidChemical, FluidBlockChemical> moleculeBlocks = new HashMap();
    public static HashMap<FluidElement, FluidBlockElement> elementsBlocks = new HashMap();

    public static void registerFluids()
    {
        for (MoleculeEnum moleculeToCreate : MoleculeEnum.values())
        {
            molecule.put(moleculeToCreate, new FluidChemical(moleculeToCreate));
        }
        for (ElementEnum moleculeToCreate : ElementEnum.values())
        {
            elements.put(moleculeToCreate, new FluidElement(moleculeToCreate));
        }
    }


    public static void registerFluidBlock()
    {
        for (FluidElement fluid : elements.values())
        {
            elementsBlocks.put(fluid, new FluidBlockElement(fluid));
            GameRegistry.registerBlock(elementsBlocks.get(fluid), fluid.getUnlocalizedName());
        }
        for (FluidChemical fluid : molecule.values())
        {
            moleculeBlocks.put(fluid, new FluidBlockChemical(fluid));
            GameRegistry.registerBlock(moleculeBlocks.get(fluid), fluid.getUnlocalizedName());
        }
    }

    @SubscribeEvent
    public void onStitch(TextureStitchEvent.Pre event)
    {
        if(event.map.getTextureType() == 0)
        {
            IIconRegister ir = event.map;
            for (FluidElement fluidElement : elements.values())
            {
                fluidElement.setIcons(ir.registerIcon(Reference.TEXTURE_MOD_ID + "fluid_still"), ir.registerIcon(Reference.TEXTURE_MOD_ID + "fluid_flow"));
            }
            for (FluidChemical fluidChemical : molecule.values())
            {
                fluidChemical.setIcons(ir.registerIcon(Reference.TEXTURE_MOD_ID + "fluid_still"), ir.registerIcon(Reference.TEXTURE_MOD_ID + "fluid_flow"));
            }
        }
    }
}
