package minechem;

import cpw.mods.fml.common.registry.GameRegistry;
import minechem.item.ItemAtomicManipulator;
import minechem.item.OpticalMicroscopeLens;
import minechem.item.blueprint.ItemBlueprint;
import minechem.item.chemistjournal.ChemistJournalItem;
import minechem.item.element.ElementEnum;
import minechem.item.element.ElementItem;
import minechem.item.fusionstar.FusionStarItem;
import minechem.item.molecule.MoleculeEnum;
import minechem.item.molecule.MoleculeItem;
import minechem.item.polytool.PolytoolItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraftforge.oredict.OreDictionary;

public class MinechemItemsRegistration
{
    public static ElementItem element;
    public static MoleculeItem molecule;
    public static OpticalMicroscopeLens lens;
    public static ItemAtomicManipulator atomicManipulator;
    public static FusionStarItem fusionStar;
    public static ItemBlueprint blueprint;
    public static ChemistJournalItem journal;
    public static ItemStack convexLens;
    public static ItemStack concaveLens;
    public static ItemStack projectorLens;
    public static ItemStack microscopeLens;
    public static ItemStack minechempills;
    public static Item polytool;

    public static void init()
    {
        element = new ElementItem();
        GameRegistry.registerItem(element, Minechem.ID + "Element");
        
        molecule = new MoleculeItem();
        GameRegistry.registerItem(molecule, Minechem.ID + "Molecule");
        
        lens = new OpticalMicroscopeLens();
        concaveLens = new ItemStack(lens, 1, 0);
        convexLens = new ItemStack(lens, 1, 1);
        microscopeLens = new ItemStack(lens, 1, 2);
        projectorLens = new ItemStack(lens, 1, 3);
        GameRegistry.registerItem(lens, Minechem.ID + "OpticalMicroscopeLens");
        
        atomicManipulator = new ItemAtomicManipulator();
        GameRegistry.registerItem(atomicManipulator, Minechem.ID + "AtomicManipulator");
        
        fusionStar = new FusionStarItem();
        GameRegistry.registerItem(fusionStar, Minechem.ID + "FusionStar");
        
        blueprint = new ItemBlueprint();
        GameRegistry.registerItem(blueprint, Minechem.ID + "Blueprint");
        
        journal = new ChemistJournalItem();
        GameRegistry.registerItem(journal, Minechem.ID + "Journal");
        
        polytool = new PolytoolItem();
        GameRegistry.registerItem(polytool, Minechem.ID + "Polytool");

    }

    public static void registerToOreDictionary()
    {
        for (ElementEnum element : ElementEnum.values())
        {
            OreDictionary.registerOre("element_" + element.name(), new ItemStack(MinechemItemsRegistration.element, 1, element.ordinal()));
        }
        OreDictionary.registerOre("dustSalpeter", new ItemStack(MinechemItemsRegistration.molecule, 1, MoleculeEnum.potassiumNitrate.ordinal()));
        OreDictionary.registerOre("dustSalt", new ItemStack(MinechemItemsRegistration.molecule, 1, MoleculeEnum.salt.ordinal()));
    }
}