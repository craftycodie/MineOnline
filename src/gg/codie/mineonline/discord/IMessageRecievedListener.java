package gg.codie.mineonline.discord;

import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

public interface IMessageRecievedListener {

    void onMessageRecieved(MessageReceivedEvent message);

}
