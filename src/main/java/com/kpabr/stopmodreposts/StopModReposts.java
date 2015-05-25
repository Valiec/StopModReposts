package com.kpabr.stopmodreposts;

import java.io.BufferedReader;
import java.io.File;
import java.nio.file.Path;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.net.UnknownHostException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;

import net.minecraft.event.ClickEvent;
import net.minecraft.init.Blocks;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.ChatStyle;
import net.minecraftforge.client.ClientCommandHandler;
import net.minecraftforge.common.MinecraftForge;
import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.Loader;
import cpw.mods.fml.common.Mod;
import cpw.mods.fml.common.Mod.EventHandler;
import cpw.mods.fml.common.ModContainer;
import cpw.mods.fml.common.event.FMLInitializationEvent;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.gameevent.PlayerEvent;
import cpw.mods.fml.common.versioning.ArtifactVersion;

@Mod(modid = StopModReposts.MODID, version = StopModReposts.VERSION, name=StopModReposts.NAME)
public class StopModReposts
{
    public static final String MODID = "stopmodreposts";
    public static final String VERSION = "1.1.0";
    public static final String NAME = "StopModReposts";
    static int versionID = 2; //Used by version checker!
    protected static ArrayList<String> warningIDs = new ArrayList<String>();
    
    static StopVersionChecker versionChecker = new StopVersionChecker();
    public static StopModReposts instance;
   
    @EventHandler
    public void init(FMLInitializationEvent event)
    {
    	FMLCommonHandler.instance().bus().register(this);
        MinecraftForge.EVENT_BUS.register(this);
        
        FMLCommonHandler.instance().bus().register(versionChecker);
        MinecraftForge.EVENT_BUS.register(versionChecker);
        
        ClientCommandHandler.instance.registerCommand(new IgnoreCommand());
        
        ClientCommandHandler.instance.registerCommand(new VersionCommand());
        
        for(ModContainer mod : Loader.instance().getActiveModList())
        {
        	for(ArtifactVersion modLoaded: mod.getRequirements())
        	{
        		if(modLoaded.getLabel().equals("stopmodreposts"))
        		{
        			validateMod(mod.getModId());
        		}
        	}
        }
    }
    
    public static void validateMod(String modid)
    {
    	boolean valid = isAuthorized(modid);
    	if(!valid)
    	{
    		warningIDs.add(modid);
    	}
    }
    
    private static boolean isAuthorized(String modid)
    {
    	ModContainer mod = Loader.instance().getIndexedModList().get(modid);
    	if(mod != null)
    	{
	    	String id = mod.getMetadata().description.split("///StopModReposts///")[1];
	    	
	    	try 
	    	{
				return isIDAuthorized(modid, id);
			} 
	    	catch (UnknownHostException e)
	    	{
				return true;
			} 
	    	catch (IOException e)
	    	{
				return true;
			}
    	}
    	else
    	{
    		return true;
    	}
    }
    public static String getJarID(String modid)
    {
    	ModContainer mod = Loader.instance().getIndexedModList().get(modid);
    	if(mod != null)
    	{
	    	return mod.getMetadata().description.split("///StopModReposts///")[1];	    	
	    	
    	}
    	else
    	{
    		return null;
    	}
    }
    private static boolean isIDAuthorized(String modid, String id) throws UnknownHostException, IOException
    {
        URL check = new URL("http://www.kpabr.com/mcmods/stopmodreposts/"+modid+"/"+id);
        URLConnection read = check.openConnection();
        BufferedReader reader = new BufferedReader(new InputStreamReader(read.getInputStream()));
        String isValidStr = reader.readLine();
        boolean isValid = Boolean.parseBoolean(isValidStr);
        Path listingPath = new File("./stopmodreposts/stopmodreposts.txt").toPath();
        Charset fileCharset = Charset.forName("UTF-8");
        List<String> lines = new ArrayList<String>();
        try
        {
        	lines = Files.readAllLines(listingPath, fileCharset);
        }
        catch(IOException e)
        {
        	//do nothing
        }
        if(lines.contains(modid+"	"+id))
        {
        	return true; //in ignore file, so don't return that it is not valid
        }
        return isValid;
    }
    
    @SubscribeEvent
    public void onPlayerLogin(PlayerEvent.PlayerLoggedInEvent event) throws UnknownHostException, IOException
    {
    	if(warningIDs.size() != 0)
    	{
	    	boolean comma = false;
	    	String messageline1 = "[StopModReposts] The following mods were likely downloaded from sites that were not given permission to distribute them:";
	    	String messageline2 = "";
	        for(String mod : warningIDs)
	        {
	        	if(!comma)
	        	{
	        		messageline2 = messageline2+mod;	
	        		comma = true;
	        	}
	        	else
	        	{
	        	messageline2 = messageline2+", "+mod;
	        	}
	        }
	        ClickEvent linkmcf = new ClickEvent(ClickEvent.Action.OPEN_URL, "http://www.minecraftforum.net");
	        ClickEvent linksmr = new ClickEvent(ClickEvent.Action.OPEN_URL, "http://www.stopmodreposts.org");
	        ChatStyle stylemcf = new ChatStyle().setChatClickEvent(linkmcf);
	        ChatStyle stylesmr = new ChatStyle().setChatClickEvent(linksmr);
	    	String messageline3 = "By distributing these mods, these sites (not you) likely violate the licenses of the mods and are harmful to the mod authors. Also, these kinds of sites are known to put malware in their downloads along with the mods. Please help modders and protect yourself from malware by downloading these mods from their official sites where they were published by their developers. To prevent this message from reappearing for a certain mod, type /smrignore followed by the modid of that mod."; 
	    	String messageline4 = "A place very likely to have an official download is the Minecraft Forums at http://www.minecraftforum.net.";
	    	String messageline5 = "To learn more, go to http://www.stopmodreposts.org.";
	    	String messageline6 = "If this message does not appear to start with \"[StopModReposts]\", press T to see the whole message.";
	    	event.player.addChatMessage(new ChatComponentText(messageline1));
	    	event.player.addChatMessage(new ChatComponentText(messageline2));
	    	event.player.addChatMessage(new ChatComponentText(messageline3));
	    	event.player.addChatComponentMessage(new ChatComponentText(messageline4).setChatStyle(stylemcf));
	    	event.player.addChatComponentMessage(new ChatComponentText(messageline5).setChatStyle(stylesmr));
	    	event.player.addChatMessage(new ChatComponentText(messageline6));
    	}
	    	 
    }
}
