package gg.codie.mineonline.discord;

import club.minnced.discord.webhook.WebhookClient;
import club.minnced.discord.webhook.WebhookClientBuilder;
import club.minnced.discord.webhook.send.WebhookMessageBuilder;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.entities.MessageChannel;
import net.dv8tion.jda.api.events.ShutdownEvent;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

import javax.annotation.Nonnull;
import javax.security.auth.login.LoginException;

public class DiscordChatBridge extends ListenerAdapter {

    WebhookMessageBuilder webhookMessage;
    WebhookClientBuilder builder;
    MessageChannel channel;
    WebhookClient client;
    final String webhook;
    final long channelID;
    final String token;
    final JDA jda;
    final IAvatarProvider avatarProvider;

    public DiscordChatBridge(IAvatarProvider avatarProvider, String discordChannel, String discordToken, String webhookUrl, IMessageRecievedListener msgEvent, IShutdownListener shutdownListener) throws NumberFormatException, InterruptedException, LoginException {
        token = discordToken;
        channelID = Long.parseLong(discordChannel);
        webhook = webhookUrl;

        this.avatarProvider = avatarProvider;

        if (webhook != null) {
            try {
                builder = new WebhookClientBuilder(webhook);
                client = builder.build();
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }
        jda = JDABuilder.createDefault(token)
                .build();
        jda.awaitReady();
        System.out.println("Discord Bridge Started");
        jda.addEventListener(new ListenerAdapter() {
            @Override
            public void onMessageReceived(MessageReceivedEvent event) {
            if (event.getChannel().getId().equals("" + channelID) && !event.isWebhookMessage() && !event.getAuthor().getId().equals(event.getJDA().getSelfUser().getId()) && !event.getMessage().getContentStripped().isEmpty()) { // stop listening to yourself
                msgEvent.onMessageRecieved(event);
            }
            }
        });
        jda.addEventListener(new ListenerAdapter() {
            @Override
            public void onShutdown(@Nonnull ShutdownEvent event) {
                shutdownListener.onShutdown();
                super.onShutdown(event);
            }
        });
    }

    public void sendDiscordMessage(String username, String message){
        if (webhook != null && !username.equals("")){ // webhook player messages to discord
            webhookMessage = new WebhookMessageBuilder();
            webhookMessage.setUsername(username);
            webhookMessage.setAvatarUrl(avatarProvider.getAvatarURL(username));
            webhookMessage.setContent(message);
            client.send(webhookMessage.build());
        } else if (token != null && !username.equals("")) { // Non-webhook player messages to discord
            try {
                channel = jda.getTextChannelById(channelID);
                channel.sendMessage("**" + username + "**: " + message).queue();
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        } else if (username.equals("")){ // Non webhook system messages to discord
            try {
                channel = jda.getTextChannelById(channelID);
                channel.sendMessage(message).queue();
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }
    }
}

