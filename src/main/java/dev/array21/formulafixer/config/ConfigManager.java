package dev.array21.formulafixer.config;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.LinkedHashMap;

import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.yaml.snakeyaml.Yaml;

import com.google.gson.Gson;

public class ConfigManager {

	private static final Logger LOGGER = LogManager.getLogger(ConfigManager.class);
	
	public ConfigManifest read(String configFile) {
		File f = new File(configFile);
		if(!f.exists()) {
			LOGGER.error(String.format("Configuration file path '%s' does not exist. Exiting", f.getAbsolutePath()));
			System.exit(1);
		}
		
		final Yaml yaml = new Yaml();
		Object parsedYaml;
		try {
			 parsedYaml = yaml.load(new FileReader(f));
		} catch(FileNotFoundException e) {
			//Unreachable;
			return null;
		}
		
		final Gson gson = new Gson();
		String json = gson.toJson(parsedYaml, LinkedHashMap.class);
		return gson.fromJson(json, ConfigManifest.class);
	}
}
