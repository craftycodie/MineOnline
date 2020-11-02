package gg.codie.mineonline.discord;

import club.minnced.discord.webhook.send.WebhookMessageBuilder;
import club.minnced.discord.webhook.WebhookClientBuilder;
import club.minnced.discord.webhook.WebhookClient;

import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.entities.MessageChannel;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.JDA;

public class DiscordChatBridge extends ListenerAdapter {

    WebhookMessageBuilder webhookMessage;
    WebhookClientBuilder builder;
    MessageChannel channel;
    WebhookClient client;
    public String message = "";
    String Webhook = "";
    String Channel = "";
    String Token = "";
    String Vers = "";
    JDA jda;

    public DiscordChatBridge(String version, String discordChannel, String discordToken, String webhookUrl, MessageRecievedListener msgEvent) {

        Token = discordToken;
        Channel = discordChannel;
        Webhook = webhookUrl;

        if (version.startsWith("Classic")) // Jank color character switcher for old version, may fail on inf/indef
            Vers = "&";
        else
            Vers = "§";

        if (Webhook != null) {
            try {
                builder = new WebhookClientBuilder(Webhook);
                client = builder.build();
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }
            try {

                jda = JDABuilder.createDefault(Token)
                        .build();
                jda.awaitReady();
                System.out.println("Discord Bridge Started");
                jda.addEventListener(new ListenerAdapter() {
                    @Override
                    public void onMessageReceived(MessageReceivedEvent event) {
                        if(event.getChannel().getId().equals(Channel) && !event.isWebhookMessage() && !event.getAuthor().getId().equals(event.getJDA().getSelfUser().getId())) { // stop listening to yourself
                            if(event.getMessage().getContentStripped().length() < 256)
                                message = ("say " + event.getAuthor().getName() + ": " + Vers + "f" + event.getMessage().getContentStripped());
                            else
                                message = ("say " + event.getAuthor().getName() + ": " + Vers + "f" + event.getMessage().getContentStripped().substring(0, 256));
                            msgEvent.onMessageRecieved(message);
                        }
                    }
                });

            } catch (Exception ex) {
                ex.printStackTrace();
            }
    }

    public void sendDiscordMessage(String username, String message){
        if (Webhook != null && !username.equals("")){ // Webhook player messages to discord
            webhookMessage = new WebhookMessageBuilder();
            webhookMessage.setUsername(username);
            webhookMessage.setAvatarUrl("https://minotar.net/avatar/" + username + "/100.png");
            webhookMessage.setContent(message);
            client.send(webhookMessage.build());
        } else if (Token != null && !username.equals("")) { // Non-webhook player messages to discord
            try {
                long chan = Long.parseLong(Channel);
                channel = jda.getTextChannelById(chan);
                channel.sendMessage("**" + username + "**: " + message).queue();
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        } else if (username.equals("")){ // Non webhook system messages to discord
            try {
                long chan = Long.parseLong(Channel);
                channel = jda.getTextChannelById(chan);
                channel.sendMessage(message).queue();
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }
    }
}

