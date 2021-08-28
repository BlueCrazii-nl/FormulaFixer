package dev.array21.formulafixer.commands;

import java.util.List;

import dev.array21.formulafixer.FormulaFixer;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.events.interaction.SlashCommandEvent;

public record CommandDescriptor(String name, String description, CommandExecutor executor, List<CommandOption> options) {

	public void execute(FormulaFixer bot, SlashCommandEvent event) {
		this.executor.execute(bot, event);
	}
	
	public void register(JDA jda) {
		jda.upsertCommand(this.name.toLowerCase(), this.description);
	}
}
