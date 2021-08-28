package dev.array21.formulafixer.annotations;

import java.lang.annotation.Repeatable;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

import net.dv8tion.jda.api.interactions.commands.OptionType;

@Retention(RetentionPolicy.RUNTIME)
@Repeatable(CommandOptions.class)
public @interface CommandOption {
	public String name();
	public String description();
	public OptionType type();
	public boolean required();
}
