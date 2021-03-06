package minechem.proxy;

import cpw.mods.fml.common.network.simpleimpl.MessageContext;
import minechem.tick.ScheduledTickHandler;
import minechem.utils.Reference;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;
import cpw.mods.fml.common.FMLCommonHandler;
import net.minecraft.server.MinecraftServer;

public class CommonProxy implements Reference
{
    public static int RENDER_ID;

    public void registerRenderers()
    {

    }

    public void registerTickHandlers() {
        FMLCommonHandler.instance().bus().register(new ScheduledTickHandler());
    }

    public World getClientWorld()
    {
        return null;
    }

    public void registerHooks()
    {
    }

    public EntityPlayer findEntityPlayerByName(String name)
    {

        EntityPlayer player;
        player = MinecraftServer.getServer().getConfigurationManager().func_152612_a(name);

        if (player != null)
        {
            return player;
        }

        return null;
    }

    public String getCurrentLanguage()
    {
        return null;
    }

    public void addName(Object obj, String s)
    {
    }

    public void addLocalization(String s1, String string)
    {
    }

    public String getItemDisplayName(ItemStack newStack)
    {
        return "";
    }

    public EntityPlayer getPlayer(MessageContext context)
    {
        return context.getServerHandler().playerEntity;
    }
}
