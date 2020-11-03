package gg.codie.mineonline.server;
import gg.codie.mineonline.discord.MessageRecievedListener;

import club.minnced.discord.webhook.send.WebhookMessageBuilder;
import club.minnced.discord.webhook.WebhookClientBuilder;
import club.minnced.discord.webhook.WebhookClient;

import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.entities.MessageChannel;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.JDA;

public class MinecraftServerDiscordChatBridge extends ListenerAdapter {

    WebhookMessageBuilder webhookMessage;
    WebhookClientBuilder builder;
    MessageChannel channel;
    String saneMessage;
    String saneName;
    WebhookClient client;
    public String message = "";
    StringBuilder sb = new StringBuilder();
    String Webhook = "";
    String Channel = "";
    String Token = "";
    String[] classicColor = {"", "", "", "", "", ""};
    Character[] colorTokens = {'f', 'e', 'c', '9', 'b', 'd'};
    JDA jda;

    public MinecraftServerDiscordChatBridge(String colorChar, String discordChannel, String discordToken, String webhookUrl, MessageRecievedListener msgEvent) {

        Token = discordToken;
        Channel = discordChannel;
        Webhook = webhookUrl;

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
                        if(event.getChannel().getId().equals(Channel) && !event.isWebhookMessage() && !event.getAuthor().getId().equals(event.getJDA().getSelfUser().getId()) && !event.getMessage().getContentStripped().isEmpty()) { // stop listening to yourself

                            message = event.getAuthor().getName()+">"+event.getMessage().getContentStripped();

                            for (int i=0; i<colorTokens.length; i++) // If we're in classic, set up some color code variables to use
                            {
                                if(colorChar == "ÿ&") {
                                    classicColor[i] = " &" + colorTokens[i];
                                } else {
                                    classicColor[i] = "";
                                }
                            }

                            message = message.replace("\n","") // Make emojis pretty
                                    .replace("\uD83D\uDE42", classicColor[1]+":smile:"+classicColor[0])
                                    .replace("\uD83D\uDE04", classicColor[1]+":smile:"+classicColor[0])
                                    .replace("❤️", classicColor[2]+":heart:"+classicColor[0])
                                    .replace("\uD83C\uDFB5", classicColor[3]+":musical_note:"+classicColor[0])
                                    .replace("♂️", classicColor[4]+":male_sign:"+classicColor[0])
                                    .replace("♀️", classicColor[5]+":female_sign:"+classicColor[0]);


                            sb.delete(0, sb.length());  // Loop through the characters and make sure there isn't anything naughty in there
                            for (int i = 0; i < message.length(); i++){
                                char c = message.charAt(i);
                                if ((int) c > 32 && (int) c < 128 || c == ' '){
                                    sb.append(c);
                                }
                            }

                            saneName = sb.toString().split(">")[0];

                            if (!event.getMessage().getContentStripped().startsWith("\n")) { // Check for things like empty codeblocks
                                saneMessage = sb.toString().split(">")[1];
                            } else {
                                saneMessage = "Posted Wrongly!";
                            }

                            if (saneMessage.endsWith("&f")) { // Prevent a crash in classic where if the message ends with this all connected clients crash lmao
                                saneMessage = saneMessage.substring(0, saneMessage.length() - 2);
                            }

                            if(saneMessage.length() < 256) // Truncate messages that are overly long - TODO: split messages into multiple 30 char messages for classic
                                message = ("say " + colorChar + "9" + saneName + ": " + colorChar + "f" + saneMessage);
                            else
                                message = ("say " + colorChar + "9" + saneName + ": " + colorChar + "f" + saneMessage.substring(0, 256));
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

