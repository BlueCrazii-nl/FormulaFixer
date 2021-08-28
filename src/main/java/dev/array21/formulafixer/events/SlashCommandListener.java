package dev.array21.formulafixer.events;

import dev.array21.formulafixer.FormulaFixer;
import net.dv8tion.jda.api.events.interaction.SlashCommandEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

public class SlashCommandListener extends ListenerAdapter {

	private FormulaFixer bot;
	
	public SlashCommandListener(FormulaFixer bot) {
		this.bot = bot;
	}
	
	@Override
	public void onSlashCommand(SlashCommandEvent event) {
		this.bot.getCommandManager().fireCommand(event);
	}
}
