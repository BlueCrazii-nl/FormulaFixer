package dev.array21.formulafixer.commands.executors;

import java.awt.Color;
import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;

import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;

import com.jcraft.jsch.ChannelExec;
import com.jcraft.jsch.JSch;
import com.jcraft.jsch.JSchException;
import com.jcraft.jsch.Session;

import dev.array21.formulafixer.FormulaFixer;
import dev.array21.formulafixer.annotations.Command;
import dev.array21.formulafixer.annotations.CommandOption;
import dev.array21.formulafixer.commands.CommandExecutor;
import dev.array21.formulafixer.config.ConfigManifest.FixEntry;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.events.interaction.SlashCommandEvent;
import net.dv8tion.jda.api.interactions.commands.OptionMapping;
import net.dv8tion.jda.api.interactions.commands.OptionType;

@Command(name = "fix", description = "Fix a broken service")
@CommandOption(name = "service", description = "The name of the service to fix", type = OptionType.STRING, required = false)
public class FixExecutor implements CommandExecutor {

	private static final Logger LOGGER = LogManager.getLogger(FixExecutor.class);
	
	@Override
	public void execute(FormulaFixer bot, SlashCommandEvent event) {
		OptionMapping serviceOption = event.getOption("service");
		if(serviceOption != null) {
			String fixName = serviceOption.getAsString();
			bot.getConfig().getFixes().forEach(f -> {
				if(f.allowedUsers != null && f.allowedUsers.length > 0 && !f.getAllowedUsers().contains(event.getUser().getIdLong())) {
					EmbedBuilder eb = new EmbedBuilder()
							.setColor(Color.RED)
							.setTitle("FormulaFixer Fix Report")
							.setDescription("You are not allowed to fix this service, ask someone else.");
					
					event.replyEmbeds(eb.build()).queue();
				}
				
				if(f.name.equalsIgnoreCase(fixName)) {
					String output = this.fix(f);
					EmbedBuilder eb = new EmbedBuilder()
							.setColor(output != null ? Color.GREEN : Color.RED)
							.setTitle("FormulaFixer Fix Report")
							.setDescription(output != null ? "The service was fixed!" : "Unfortunately, something went wrong.");
					
					LOGGER.info(output);
					
					event.replyEmbeds(eb.build()).queue();
				}
			});
		} else {
			EmbedBuilder eb = new EmbedBuilder()
					.setColor(Color.GRAY)
					.setTitle("FormulaFixer");
			
			bot.getConfig().getFixes().forEach(f -> {
				eb.addField(f.name, f.sshHost, false);
			});
			
			event.replyEmbeds(eb.build()).queue();
		}
	}
	
	private String fix(FixEntry fixEntry) {
		final JSch jsch = new JSch();
		final File sshKey = new File(fixEntry.sshPrivateKeyPath);
		if(!sshKey.exists()) {
			LOGGER.error(String.format("The private SSH key in FixEntry '%s' at path '%s' does not exist.", fixEntry.name, fixEntry.sshPrivateKeyPath));
			return null;
		}
		
		try {
			if(fixEntry.sshPrivateKeyPassphrase != null) {
				jsch.addIdentity(fixEntry.sshPrivateKeyPath, fixEntry.sshPrivateKeyPassphrase);
			} else {
				jsch.addIdentity(fixEntry.sshPrivateKeyPath);
			}
		} catch(JSchException e) {
			LOGGER.error("Failed to add Identity to SSH client", e);
			return null;
		}
		
		Session session;
		try {
			session = jsch.getSession(fixEntry.sshUser, fixEntry.sshHost, fixEntry.getSshPort());
		} catch (JSchException e) {
			LOGGER.error(String.format("Unable to establish session for FixEntry '%s'", fixEntry.name), e);
			return null;
		}
		
        session.setConfig("StrictHostKeyChecking", "no");
        try {
			session.connect();
		} catch (JSchException e) {
			LOGGER.error(String.format("Unable to connect session for FixEntry '%s'", fixEntry.name), e);
			return null;
		}
        
        ChannelExec execChannel;
        try {
        	execChannel = (ChannelExec) session.openChannel("exec");
        } catch(JSchException e) {
        	LOGGER.error(String.format("Unable to create 'exec' channel for FixEntry '%s'", fixEntry.name), e);
        	return null;
        }
        
        execChannel.setCommand(fixEntry.command);
        ByteArrayOutputStream output = new ByteArrayOutputStream();
        execChannel.setOutputStream(output);
        try {
			execChannel.connect();
		} catch (JSchException e) {
			LOGGER.error(String.format("Unable to connect ExecChannel for FixEntry '%s'", fixEntry.name), e);
			return null;
		}
        
        String result = new String(output.toByteArray());
        
    	execChannel.disconnect();
    	session.disconnect();
    	
    	return result;
	}
}
