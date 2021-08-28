package dev.array21.formulafixer.commands;

import net.dv8tion.jda.api.interactions.commands.OptionType;

public record CommandOption(String name, String description, OptionType type, boolean required) {}