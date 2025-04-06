package gg.kite.core;

import gg.kite.core.command.CommandManager;
import gg.kite.core.config.Config;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.entities.Activity;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.Commands;
import net.dv8tion.jda.api.requests.GatewayIntent;

/**
 * The Main class initializes a Discord bot using the JDA library.
 * It reads configuration settings for the bot token and activity,
 * sets up gateway intents, and registers event listeners.
 * The bot provides a slash command for broadcasting messages to all
 * online users in a guild.
 */
public class Main {
    public static void main(String[] args) throws InterruptedException {
        final String token = Config.get("bot.token");
        final String activity = Config.get("bot.activity");

        JDA jda = JDABuilder.createDefault(token)
                .enableIntents(
                        GatewayIntent.GUILD_MEMBERS,
                        GatewayIntent.MESSAGE_CONTENT
                )
                .setActivity(Activity.playing(activity))
                .addEventListeners(new CommandManager())
                .build();

        jda.awaitReady();

        jda.upsertCommand(
                Commands.slash("broadcast", "Broadcast a message to all online users")
                        .addOption(
                                OptionType.STRING,
                                "message",
                                "Enter the message to broadcast",
                                true
        )).queue();

        System.out.println("Bot ready!");
    }
}