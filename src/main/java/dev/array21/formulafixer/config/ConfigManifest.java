package dev.array21.formulafixer.config;

import java.util.Arrays;
import java.util.List;

public class ConfigManifest {

	private FixEntry[] fixes;
	
	public List<FixEntry> getFixes() {
		return Arrays.asList(this.fixes);
	}
	
	public class FixEntry {
		public String name;
		public String sshHost;
		public Integer sshPort;
		public String sshUser;
		public String sshPrivateKeyPath;
		public String sshPrivateKeyPassphrase;
		public String command;
		public Long[] allowedUsers;
		
		public List<Long> getAllowedUsers() {
			return Arrays.asList(this.allowedUsers);
		}
		
		public int getSshPort() {
			return this.sshPort != null ? this.sshPort : 22;
		}
	}
}
