package dev.array21.formulafixer;

import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;

import dev.array21.formulafixer.commands.CommandManager;
import dev.array21.formulafixer.config.ConfigManager;
import dev.array21.formulafixer.config.ConfigManifest;

public class FormulaFixer {
	
	private static final Logger LOGGER = LogManager.getLogger(FormulaFixer.class);
	private ConfigManifest config;
	private CommandManager commandManager;
    
	public static void main(String[] args) {
        new FormulaFixer().init(args);
    }
    
    public ConfigManifest getConfig() {
    	return this.config;
    }
    
    public CommandManager getCommandManager() {
    	return this.commandManager;
    }
    
    private void init(String[] argsStr) {
    	LOGGER.info("Starting FormulaFixer");
    	LOGGER.info("Parsing arguments");
    	
    	HashMap<Argument, String> args = this.parseArguments(argsStr);
    	checkArguments(args);
    	
    	LOGGER.info("Reading configuration file");
    	this.config = new ConfigManager().read(args.get(Argument.CONFIG_FILE));
    	
    	LOGGER.info("Setting up JDA");
    	JdaHandler jdaHandler = new JdaHandler(this);
    	jdaHandler.load(args.get(Argument.TOKEN));
    	
    	LOGGER.info("Setting up commands");
    	this.commandManager = new CommandManager(this);
    	this.commandManager.loadDefault();
    	
    	LOGGER.info("Registering commands");
    	jdaHandler.registerCommands(this.commandManager.getCommands());
    }
    
    private void checkArguments(HashMap<Argument, String> args) {
    	List<Argument> requiredArgs = Arrays.asList(
    			Argument.CONFIG_FILE, 
    			Argument.TOKEN);
    	
    	if(!args.keySet().containsAll(requiredArgs)) {
    		Set<Argument> argCopy = new HashSet<>(requiredArgs);
    		argCopy.removeAll(args.keySet());
    		
    		argCopy.forEach(a -> {
    			LOGGER.error(String.format("Missing required argument '%s'", a.getCliName()));
    		});
    		
    		LOGGER.error("Invalid argument configuration, exiting");
    		System.exit(1);
    	}
    }
    
    private HashMap<Argument, String> parseArguments(String[] args) {
    	HashMap<Argument, String> result = new HashMap<>();
    	
    	for(int i = 0; i < args.length; i++) {
    		String arg = args[i];
    		switch(arg) {
    		case "--config-file":
    			checkArgLen(args, i, "--token");  
    			result.put(Argument.CONFIG_FILE, args[++i]);
    		
    			break;
    		case "--token":
    			checkArgLen(args, i, "--token");    		
    			result.put(Argument.TOKEN, args[++i]);
    			break;
    		}
    	}
    	
    	return result;
    }
    
    private void checkArgLen(String[] args, int i, String name) {
		if(i == args.length -1) {
			LOGGER.error(String.format("Expected a value for argument '%s', but got EOL", name));
			System.exit(1);
		}
    }
}