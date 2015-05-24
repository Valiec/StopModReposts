package com.kpabr.stopmodreposts;

import java.io.File;
import java.io.IOException;
import java.net.UnknownHostException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.OpenOption;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.ArrayList;
import java.util.List;

import net.minecraft.client.Minecraft;
import net.minecraft.command.CommandBase;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommand;
import net.minecraft.command.ICommandSender;
import net.minecraft.crash.CrashReport;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.Item;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.ChatComponentText;

public class IgnoreCommand extends CommandBase
{

    public String getCommandName()
    {
        return "smrignore";
    }

    public String getCommandUsage(ICommandSender par1ICommandSender)
    {
        return "/smrignore";
    }

    // JAVADOC METHOD $$ func_82362_a
    public int getRequiredPermissionLevel()
    {
        return 0;
    }
    public void processCommand(ICommandSender par1ICommandSender, String[] par2ArrayOfStr)
    {
    	String modid = par2ArrayOfStr[0];
    	StopModReposts.warningIDs.remove(modid);
    	File listingDir = new File("./stopmodreposts");
    	File listingFile = new File("./stopmodreposts/stopmodreposts.txt");
    	Path listingPath = listingFile.toPath();
        Charset fileCharset = Charset.forName("UTF-8");
        ArrayList<String> lines = new ArrayList<String>();
        lines.add(modid+"	"+StopModReposts.getJarID(modid));
        if(!listingFile.exists())
        {
        	try {
        		listingDir.mkdir();
				listingFile.createNewFile();
			} catch (IOException e) {
				e.printStackTrace();
				//do nothing, attempting to write will print error message
			}
        }
        try {
			Files.write(listingPath, lines, fileCharset, StandardOpenOption.APPEND);
			Minecraft.getMinecraft().thePlayer.addChatMessage(new ChatComponentText("[StopModReposts] Mod "+modid+" successfully ignored."));
		} catch (IOException e) {
			e.printStackTrace();
			Minecraft.getMinecraft().thePlayer.addChatMessage(new ChatComponentText("[StopModReposts] Failed to write to ignore file."));
		}
    }
    @Override
    public int compareTo(Object o)
    {
        return 0;
    }
    
    // JAVADOC METHOD $$ func_71516_a
    protected String[] func_147209_d()
    {
        return MinecraftServer.getServer().getAllUsernames();
    }

    // JAVADOC METHOD $$ func_82358_a
    public boolean isUsernameIndex(String[] par1ArrayOfStr, int par2)
    {
        return par2 == 0;
    }
}