package me.momocow.moemail.command;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.UUID;

import com.mojang.authlib.GameProfile;

import me.momocow.mobasic.command.MoCommand;
import me.momocow.mobasic.proxy.Server;
import me.momocow.moemail.reference.Reference;
import me.momocow.moemail.server.MailPool;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraftforge.common.UsernameCache;

public class CommandSendMail extends MoCommand
{
	private List<String> alias = new ArrayList<String>();
	
	public CommandSendMail()
	{
		this.alias.add("mail");
		this.alias.add("em");
		this.setUnlocalizedName(Reference.MOD_ID + ".SendMail");
	}
	
	@Override
	public String getCommandName() 
	{
		return "email";
	}

	@Override
	public List<String> getCommandAliases() 
	{
		return this.alias;
	}

	// email <Receiver> <Title|*> <Message...>
	@Override
	public void execute(MinecraftServer server, ICommandSender sender, String[] args) throws CommandException 
	{
		if(args.length < 3)
		{
			throw new CommandException(this.getCommandUsage(sender), new Object[0]);
		}
		else
		{
			GameProfile profile = Server.getPlayerProfile(args[0]);
			UUID receiverID = (profile == null)? null: profile.getId();
			
			String title = args[1];
			if(title.equals("*") || title.isEmpty())
			{
				title = this.getUnlocalizedName() + ".defaultTitle";
			}
			
			String message = "";
			for(int i = 2; i< args.length; i++)
			{
				message += args[i] + " ";
			}
			message = message.trim();
			
			if(receiverID == null)
			{
				throw new CommandException(this.getUnlocalizedName() + ".receiverNotFound", new Object[0]);
			}
			else
			{
				MailPool.instance().send(receiverID, sender.getCommandSenderEntity().getUniqueID(), sender.getName(), title, message);
				sender.addChatMessage(new TextComponentTranslation(this.getUnlocalizedName() + ".mailsent"));
			}
		}
	}
	
	@Override
	public int getRequiredPermissionLevel() {
		return 0;
	}

	@Override
	public boolean checkPermission(MinecraftServer server, ICommandSender sender) 
	{
		//default permission level for MoCommand is 0
		return sender.canCommandSenderUseCommand(this.getRequiredPermissionLevel(), this.getCommandName());
	}
	
	@Override
	public List<String> getTabCompletionOptions(MinecraftServer server, ICommandSender sender, String[] args,
			BlockPos pos) 
	{
		List<String> candidates = new ArrayList<String>();
		
		if(args.length == 1)
		{
			Collection<String> players = UsernameCache.getMap().values();
			candidates = getListOfStringsMatchingLastWord(args, players);
		}
		else if(args.length == 2)
		{
			candidates.add("*");
			candidates.add("\"\"");
		}
		
		return candidates;
	}

	@Override
	public boolean isUsernameIndex(String[] args, int index) 
	{
		return index == 1;
	}
}
