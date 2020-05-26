package gg.codie.mineonline.api;

import java.util.Date;

public class MineOnlineAccount {
    public final String user;
    public final String email;
    public final boolean premium;
    public final Date createdAt;

    public MineOnlineAccount(String user, String email, boolean premium, Date createdAt) {
        this.user = user;
        this.email = email;
        this.premium = premium;
        this.createdAt = createdAt;
    }
}
