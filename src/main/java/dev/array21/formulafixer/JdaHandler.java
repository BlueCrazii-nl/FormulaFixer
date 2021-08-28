package dev.array21.formulafixer;

import java.util.List;

import javax.security.auth.login.LoginException;

import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;

import dev.array21.formulafixer.commands.CommandDescriptor;
import dev.array21.formulafixer.events.SlashCommandListener;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.entities.Activity;
import net.dv8tion.jda.api.interactions.commands.build.CommandData;

public class JdaHandler {

	private static final Logger LOGGER = LogManager.getLogger(JdaHandler.class);
	private FormulaFixer bot;
	private JDA jda;
	
	public JdaHandler(FormulaFixer bot) {
		this.bot = bot;
	}
	
	public void load(String token) {
		JDABuilder builder = JDABuilder.createDefault(token)
				.setActivity(Activity.playing("Fixing Formula 1"))
				.addEventListeners(new SlashCommandListener(this.bot));
		
		try {
			this.jda = builder.build();
		} catch(LoginException e) {
			LOGGER.error("Failed to log-in to Discord. Exiting", e);
			System.exit(1);
		}
		
		try {
			this.jda.awaitReady();
		} catch(InterruptedException e) {
			LOGGER.error("Got interrupted while waiting for JDA to be ready. Exiting", e);
			System.exit(1);
		}
	}
	
	public JDA getJda() {
		return this.jda;
	}
	
	public void registerCommands(List<CommandDescriptor> commands) {
		commands.forEach(c -> {
			CommandData cd = new CommandData(c.name(), c.description());
			c.options().forEach(o -> {
				cd.addOption(o.type(), o.name(), o.description(), o.required());
			});
			
			this.jda.getGuilds().forEach(g -> {
				LOGGER.info(g.getName());
				g.upsertCommand(cd).queue();
			});
			LOGGER.debug(String.format("Registered command '%s'", c.name().toLowerCase()));
		});
	}
}
