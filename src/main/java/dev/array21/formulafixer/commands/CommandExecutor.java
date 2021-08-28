package dev.array21.formulafixer.commands;

import dev.array21.formulafixer.FormulaFixer;
import net.dv8tion.jda.api.events.interaction.SlashCommandEvent;

public interface CommandExecutor {

	public void execute(FormulaFixer bot, SlashCommandEvent event);
	
}
