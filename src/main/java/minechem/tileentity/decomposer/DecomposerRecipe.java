package minechem.tileentity.decomposer;

import java.util.ArrayList;
import java.util.Hashtable;
import java.util.Map;
import java.util.Random;
import minechem.Minechem;
import minechem.Settings;
import minechem.potion.PotionChemical;
import minechem.utils.Compare;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.oredict.OreDictionary;

public class DecomposerRecipe
{

	//public static ArrayList<DecomposerRecipe> recipes = new ArrayList<DecomposerRecipe>();
	public static Map<String, DecomposerRecipe> recipes = new Hashtable<String, DecomposerRecipe>();

	ItemStack input;
	//public ArrayList<PotionChemical> output = new ArrayList<PotionChemical>();
	public Map<PotionChemical, PotionChemical> output = new Hashtable<PotionChemical, PotionChemical>();

	//TODO:Add blacklist support for fluids
	public static DecomposerRecipe add(DecomposerRecipe recipe)
	{
		if (recipe.input != null)
		{
			for (int i = 0; i < Settings.DecomposerBlacklist.length; i++)
			{
				if (recipe.input.hasDisplayName())
				{
					if (recipe.input.getDisplayName() != null && Settings.DecomposerBlacklist[i] != null)
					{
						if (Compare.stringSieve(recipe.input.getDisplayName()).compareTo(Compare.stringSieve(Settings.DecomposerBlacklist[i])) == 0)
						{
							if (Settings.DebugMode)
							{
								Minechem.LOGGER.info("Decomposer recipe for '" + Settings.DecomposerBlacklist[i] + "' has been blacklisted");
							}
							return null;
						}
					}
				}
			}
			recipes.put(getKey(recipe.input), recipe);
		} else if (((DecomposerFluidRecipe) recipe).inputFluid != null)
		{
			recipes.put(getKey(((DecomposerFluidRecipe) recipe).inputFluid), recipe);
		}

		//recipes.add(recipe);
		return recipe;
	}

	public static DecomposerRecipe get(String string)
	{
		return recipes.get(string);
	}

	public static DecomposerRecipe remove(DecomposerRecipe recipe)
	{
		return remove(getKey(recipe.input));
	}

	public static DecomposerRecipe remove(String string)
	{
		if (recipes.containsKey(string))
		{
			return recipes.remove(string);
		}
		return null;
	}

	public static String getKey(ItemStack itemStack)
	{
        ItemStack result = itemStack.copy();
        result.stackSize = 1;
        return result.toString();
	}

	public static String getKey(FluidStack fluidStack)
	{
        FluidStack result = fluidStack.copy();
        result.amount = 1;
        return result.toString();
	}

	public static DecomposerRecipe get(ItemStack item)
	{
		//ArrayList<ItemStack> oreDict= OreDictionary.getOres(OreDictionary.getOreID(item));
		String key = getKey(item);
		if (key!=null) return get(key);
		return null;
	}

	public static DecomposerRecipe get(FluidStack item)
	{
		String key = getKey(item);
		if (key!=null) return get(key);
		return null;
	}

	public static void removeRecipeSafely(String item)
	{
		for (ItemStack i : OreDictionary.getOres(item))
		{
			DecomposerRecipe.remove(new DecomposerRecipe(new ItemStack(i.getItem(), 1, i.getItemDamage()), new PotionChemical[]
			{
			}));
		}
	}

	public static void createAndAddRecipeSafely(String item, int amount, PotionChemical... chemicals)
	{
		for (ItemStack i : OreDictionary.getOres(item))
		{
			DecomposerRecipe.add(new DecomposerRecipe(new ItemStack(i.getItem(), amount, i.getItemDamage()), chemicals));
		}
	}

	public DecomposerRecipe(ItemStack input, PotionChemical... chemicals)
	{
		this(chemicals);
		this.input = input;
	}

	public DecomposerRecipe(ItemStack input, ArrayList<PotionChemical> chemicals)
	{
		this.input = input;
		for (PotionChemical potionChemical : chemicals)
		{
			PotionChemical current = this.output.put(getPotionKey(potionChemical), potionChemical);
			if (current != null)
			{
				current.amount += potionChemical.amount;
				this.output.put(getPotionKey(potionChemical), potionChemical);
			}
		}
	}

	public DecomposerRecipe(PotionChemical... chemicals)
	{
		addChemicals(chemicals);
	}

	public void addChemicals(PotionChemical... chemicals)
	{
		for (PotionChemical potionChemical : chemicals)
		{
			//this.output.add(potionChemical);
			PotionChemical current = this.output.put(getPotionKey(potionChemical), potionChemical);
			if (current != null)
			{
				current.amount += potionChemical.amount;
				this.output.put(getPotionKey(potionChemical), potionChemical);
			}
		}
	}

	public ItemStack getInput()
	{
		return this.input;
	}

	public ArrayList<PotionChemical> getOutput()
	{
		//return this.output;
		ArrayList<PotionChemical> result = new ArrayList<PotionChemical>();
		result.addAll(this.output.values());
		return result;
	}

	public static PotionChemical getPotionKey(PotionChemical potion)
	{
		PotionChemical key = potion.copy();
		key.amount = 1;
		return key;
	}

	public ArrayList<PotionChemical> getOutputRaw()
	{
		//return this.output;
		ArrayList<PotionChemical> result = new ArrayList<PotionChemical>();
		result.addAll(this.output.values());
		return result;
	}

	public ArrayList<PotionChemical> getPartialOutputRaw(int f)
	{
		ArrayList<PotionChemical> raw = getOutput();
		ArrayList<PotionChemical> result = new ArrayList<PotionChemical>();
		if (raw != null)
		{
			for (PotionChemical chem : raw)
			{
				try
				{
					if (chem != null)
					{
						PotionChemical reduced = chem.copy();
						if (reduced != null)
						{
							reduced.amount = (int) Math.floor(chem.amount / f);
							Random rand = new Random();
							if (reduced.amount == 0 && rand.nextFloat() > (chem.amount / f))
							{
								reduced.amount = 1;
							}
							result.add(reduced);
						}
					}

				} catch (Exception e)
				{
					// something has gone wrong
					// but we do not know quite why
					// debug code goes here
				}

			}
		}

		return result;
	}

	public boolean isNull()
	{
		return this.output == null;
	}
}
