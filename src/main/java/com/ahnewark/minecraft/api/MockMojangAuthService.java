package com.ahnewark.minecraft.api;

import com.ahnewark.mineonline.utils.LastLogin;
import org.json.JSONObject;

import java.io.IOException;

public class MockMojangAuthService extends MojangAuthService {
    public JSONObject authenticate(String username, String password, String clientToken) throws IOException {
        JSONObject res = new JSONObject();
        res.put("accessToken", "mockAccessToken");
        JSONObject selectedProfile = new JSONObject();
        selectedProfile.put("id", "mockUUID");
        selectedProfile.put("name", username);
        res.put("selectedProfile", selectedProfile);
        return res;
    }

    public JSONObject refresh(String accessToken, String clientToken) throws IOException {
        return authenticate(LastLogin.readLastLogin().username, "", "");
    }
}
