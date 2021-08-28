package dev.array21.formulafixer.annotations;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

@Retention(RetentionPolicy.RUNTIME)
public @interface CommandOptions {
	public CommandOption[] value();
}
