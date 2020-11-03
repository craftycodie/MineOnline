package gg.codie.mineonline.discord;

public class MinotarAvatarProvider implements IAvatarProvider {
    @Override
    public String getAvatarURL(String username) {
        return "https://minotar.net/avatar/" + username + "/100.png";
    }
}
