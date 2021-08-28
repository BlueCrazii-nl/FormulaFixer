package dev.array21.formulafixer;

public enum Argument {
	CONFIG_FILE("--config-file"),
	TOKEN("--token");
	
	private String cliName;
	private Argument(String cliName) {
		this.cliName = cliName;
	}
	
	public String getCliName() {
		return this.cliName;
	}
}
