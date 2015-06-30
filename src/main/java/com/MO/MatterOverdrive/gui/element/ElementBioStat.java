package com.MO.MatterOverdrive.gui.element;

import cofh.lib.render.RenderHelper;
import com.MO.MatterOverdrive.MatterOverdrive;
import com.MO.MatterOverdrive.Reference;
import com.MO.MatterOverdrive.api.inventory.IBionicStat;
import com.MO.MatterOverdrive.entity.AndroidPlayer;
import com.MO.MatterOverdrive.gui.MOGuiBase;
import com.MO.MatterOverdrive.network.packet.server.PacketUnlockBioticStat;
import com.MO.MatterOverdrive.proxy.ClientProxy;
import com.MO.MatterOverdrive.util.RenderUtils;
import net.minecraft.util.IIcon;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.util.ForgeDirection;
import static org.lwjgl.opengl.GL11.*;

import java.util.List;

/**
 * Created by Simeon on 5/27/2015.
 */
public class ElementBioStat extends MOElementButton {
    IBionicStat stat;
    AndroidPlayer player;
    int level;
    ForgeDirection direction;

    public ElementBioStat(MOGuiBase gui, int posX, int posY,IBionicStat stat,int level,AndroidPlayer player,ForgeDirection direction)
    {
        super(gui,gui, posX, posY,stat.getUnlocalizedName(),0,0,0,0, 22, 22,"");
        texture = ElementSlot.getTexture("holo");
        texW = 22;
        texH = 22;
        this.stat = stat;
        this.player = player;
        this.level = level;
        this.direction = direction;
    }

    @Override
    public boolean isEnabled() {

        if (stat.canBeUnlocked(player,level))
        {
            if (player.getUnlockedLevel(stat) < stat.maxLevel())
            {
                return true;
            }
        }
        return false;
    }


    protected void ApplyColor()
    {
        if (stat.canBeUnlocked(player,level) || player.isUnlocked(stat,level))
        {
            if (level <= 0)
            {
                RenderUtils.applyColorWithMultipy(Reference.COLOR_HOLO,0.5f);
            }
            else
            {
                RenderUtils.applyColor(Reference.COLOR_HOLO);
            }
        }
        else
        {
            RenderUtils.applyColorWithMultipy(Reference.COLOR_HOLO_RED, 0.5f);
        }
    }

    protected void ResetColor()
    {
        glColor3f(1, 1, 1);
    }

    @Override
    public void addTooltip(List<String> list)
    {
       stat.onTooltip(player, level, list, gui.getMouseX(), gui.getMouseY());
    }

    @Override
    public void onAction(int mouseX, int mouseY,int mouseButton)
    {
        if (super.intersectsWith(mouseX,mouseY))
        {
            if (stat.canBeUnlocked(player,level+1) && level < stat.maxLevel())
            {
                gui.playSound(Reference.MOD_ID + ":" + "gui.biotic_stat_unlock",1,1);
                MatterOverdrive.packetPipeline.sendToServer(new PacketUnlockBioticStat(stat.getUnlocalizedName(),++level));
            }
        }
        super.onAction(mouseX, mouseY, mouseButton);
    }

    @Override
    public void drawTexturedModalRect(int var1, int var2, int var3, int var4, int var5, int var6)
    {
        ApplyColor();
        this.gui.drawSizedTexturedModalRect(var1, var2, var3, var4, var5, var6, (float) this.texW, (float) this.texH);
    }

    @Override
    public void drawBackground(int mouseX, int mouseY, float gameTicks)
    {
        glEnable(GL_BLEND);
        ApplyColor();
        super.drawBackground(mouseX, mouseY, gameTicks);
        drawIcon(stat.getIcon(level), posX + 3, posY + 3);
        if (direction != ForgeDirection.UNKNOWN)
        {
            glPushMatrix();
            glTranslated(posX, posY, 0);
            glTranslated(sizeX / 2, sizeY / 2, 0);
            glTranslated(direction.offsetX * (sizeX*0.75),-direction.offsetY * (sizeY*0.75),0);
            if (direction == ForgeDirection.EAST)
            {
                glRotated(90, 0, 0, 1);
            }else if (direction == ForgeDirection.WEST)
            {
                glRotated(-90,0,0,1);
            }
            else if (direction == ForgeDirection.DOWN)
            {
                glRotated(180,0,0,1);
            }
            glTranslated(-3.5,-3.5,0);
            ClientProxy.holoIcons.renderIcon("up_arrow", 0, 0);
            glPopMatrix();
        }
        if (stat.maxLevel() > 1 && level > 0)
        {
            getFontRenderer().drawString(Integer.toString(level),posX + 16,posY + 16,0xFFFFFF);
        }
        ResetColor();
        glDisable(GL_BLEND);
    }

    public void drawIcon(IIcon icon,int x,int y)
    {
        if(icon != null)
        {
            glEnable(GL_BLEND);

            glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA);
            ClientProxy.holoIcons.bindSheet();
            RenderHelper.renderIcon(x, y, 0, icon, 16, 16);
            glDisable(GL_BLEND);
        }
    }

    public IBionicStat getStat()
    {
        return stat;
    }
}
