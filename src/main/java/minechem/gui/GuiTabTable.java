package minechem.gui;

import minechem.Minechem;
import net.minecraft.client.gui.Gui;
import net.minecraft.util.IIcon;
import net.minecraft.util.ResourceLocation;

public class GuiTabTable extends GuiTab
{
    public static IIcon helpIcon;

    public GuiTabTable(Gui gui)
    {
        super(gui);

        this.overlayColor = 0x2F7DAA;
    }

    @Override
    public void draw(int x, int y)
    {
        drawBackground(x, y);
        if (!isFullyOpened())
        {
            drawIcon(x + 2, y + 3);
            return;
        }

    }

    @Override
    public String getTooltip()
    {

        return "Table Of Elements";
    }

    @Override
    public ResourceLocation getIcon()
    {
        return Minechem.ICON_HELP;
    }

}
