package dev.array21.formulafixer.commands;

import java.io.IOException;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;

import com.google.common.reflect.ClassPath;

import dev.array21.formulafixer.FormulaFixer;
import dev.array21.formulafixer.annotations.Command;
import net.dv8tion.jda.api.events.interaction.SlashCommandEvent;

public class CommandManager {

	private static final Logger LOGGER = LogManager.getLogger(CommandManager.class);
	private List<CommandDescriptor> commands;
	private FormulaFixer bot;
	
	public CommandManager(FormulaFixer bot) {
		this.bot = bot;
	}
	
	public void fireCommand(SlashCommandEvent event) {
		this.commands.forEach(c -> {
			if(c.name().equalsIgnoreCase(event.getName())) {
				c.execute(this.bot, event);
			}
		});
	}
	
	public List<CommandDescriptor> getCommands() {
		return this.commands;
	}
	
	public void loadDefault() {
		List<CommandDescriptor> cds = new ArrayList<>();
		ClassPath cp;
		try {
			cp = ClassPath.from(this.getClass().getClassLoader());
		} catch(IOException e) {
			LOGGER.error("Failed to create ClassPath. Exiting", e);
			System.exit(1);
			return;
		}
		
		cp.getTopLevelClasses("dev.array21.formulafixer.commands.executors").forEach(ci -> {
			Class<?> clazz; 
			try {
				clazz = Class.forName(ci.getName());
			} catch(ClassNotFoundException e) {
				LOGGER.error(String.format("Class '%s' could not be loaded, it is not on the classpath.", ci.getName()), e);
				return;
			}
			
			if(!clazz.isAnnotationPresent(Command.class)) {
				return;
			}
			
			Constructor<?> constructor;
			try {
				constructor = clazz.getDeclaredConstructor();
			} catch(NoSuchMethodException e) {
				LOGGER.error(String.format("Command executor '%s' does not provide a constructor: <init>()", clazz.getName()), e);
				return;
			}
			
			if(!Arrays.asList(clazz.getInterfaces()).contains(CommandExecutor.class)) {
				LOGGER.error(String.format("Command executor '%s' does not implement '%s'", clazz.getName(), CommandExecutor.class.getName()));
				return;
			}
			
			CommandExecutor executor;
			try {
				executor = (CommandExecutor) constructor.newInstance();
			} catch(InstantiationException | IllegalAccessException | InvocationTargetException e) {
				LOGGER.error(String.format("Failed to instantiate executor '%s'", clazz.getName()), e);
				return;
			}
			
			Command commandAnnotation = clazz.getAnnotation(Command.class);
			
			List<CommandOption> options = new ArrayList<>();
			for(dev.array21.formulafixer.annotations.CommandOption option : clazz.getAnnotationsByType(dev.array21.formulafixer.annotations.CommandOption.class)) {
				options.add(new CommandOption(option.name(), option.description(), option.type(), option.required()));
			}
			
			CommandDescriptor descriptor = new CommandDescriptor(commandAnnotation.name().toLowerCase(), commandAnnotation.description(), executor, options);
			cds.add(descriptor);
		});
		
		this.commands = cds;
	}
}
