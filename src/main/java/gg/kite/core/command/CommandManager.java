package gg.kite.core.command;

import gg.kite.core.config.Config;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.utils.messages.MessageCreateData;

import java.awt.*;

/**
 * The CommandManager class handles slash commands using the JDA library.
 * It specifically processes the "broadcast" command, allowing administrators
 * to send broadcast messages to all non-bot users within a guild. This class
 * extends ListenerAdapter to utilize event listener functionality.
 *
 * Responsibilities:
 * - Validates user permissions to prevent unauthorized command usage.
 * - Compiles and sends an embedded broadcast message to all users within a guild.
 * - Provides feedback to the command issuer regarding the success or failure of the action.
 *
 * Behavior:
 * - If the user lacks administrator privileges or the guild context is missing,
 *   appropriate error responses are provided.
 * - Retrieves configuration values from the Config class for building the broadcast embed.
 * - Ensures bots are excluded from receiving the broadcast message.
 * - Implements error handling for member loading and private channel messaging operations.
 */
public class CommandManager extends ListenerAdapter {

    @Override
    public void onSlashCommandInteraction(SlashCommandInteractionEvent event) {

        if (event.getName().equals("broadcast")) {

            Member member = event.getMember();
            if (member == null || !member.hasPermission(Permission.ADMINISTRATOR)) {
                event.reply("You do not have permission to use this command!").setEphemeral(true).queue();
                return;
            }

            String messageContent = event.getOption("message").getAsString();

            EmbedBuilder embed = new EmbedBuilder();
            embed.setTitle(Config.get("embed.title"));
            embed.setThumbnail(Config.get("embed.thumbnail"));
            embed.setDescription(messageContent);
            embed.setColor(Color.decode(Config.get("embed.color")));

            embed.setFooter(
                    Config.get("embed.footer") + " " + event.getUser().getName(),
                    event.getUser().getEffectiveAvatarUrl()
            );

            event.reply("Broadcasting your message...").setEphemeral(true).queue();

            Guild guild = event.getGuild();
            if (guild == null) {
                event.getHook().sendMessage("This command can only be used in a server!").queue();
                return;
            }

            guild.loadMembers().onSuccess(members -> {
                for (Member guildMember : members) {
                    if (guildMember.getUser().isBot()) continue;

                    guildMember.getUser().openPrivateChannel().queue(privateChannel -> {
                        privateChannel.sendMessage(MessageCreateData.fromEmbeds(embed.build())).queue(
                        );
                    });
                }
            }).onError(error -> {
                event.getHook().sendMessage("Failed to load members: " + error.getMessage()).queue();
            });
        }
    }
}
