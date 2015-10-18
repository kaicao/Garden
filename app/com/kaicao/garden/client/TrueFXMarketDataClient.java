package com.kaicao.garden.client;

import com.google.common.base.Joiner;
import com.google.common.collect.Sets;
import sun.misc.BASE64Encoder;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.Response;
import java.util.Set;
import java.util.UUID;

/**
 * Created by kaicao on 28/09/15.
 * Usage refer to https://www.truefx.com/dev/data/TrueFX_MarketDataWebAPI_DeveloperGuide.pdf
 */
public class TrueFXMarketDataClient {

    private static final String BASE_URL = "http://webrates.truefx.com/rates/connect.html";
    private static final BASE64Encoder BASE_64_ENCODER = new BASE64Encoder();

    private static final Client CLIENT = ClientBuilder.newClient();
    private static String SESSION;

    public static void main(String[] args) throws Exception {
        String user = "ck870711";
        String pass = "19870711";
        String sessionQualifier = BASE_64_ENCODER.encode(UUID.randomUUID().toString().getBytes());
        String session = login(user, pass, sessionQualifier, Sets.newHashSet("EUR/USD"), "csv", true);
        String result = fetch(session);
    }

    private static String login(String username, String password, String sessionQualifier,
                                Set<String> currencyPairs, String format, boolean snapshotUpdate) throws Exception {
        if (SESSION == null) {
            WebTarget target = CLIENT.target(BASE_URL)
                    .queryParam("u", username)
                    .queryParam("p", password)
                    .queryParam("q", sessionQualifier)
                    .queryParam("c", String.join(",", currencyPairs))
                    .queryParam("f", format)
                    .queryParam("s", snapshotUpdate ? "y" : "n");
            Response response = target.request().get();
            assert response.getStatus() == 200;
            SESSION = response.readEntity(String.class).trim();
        }
        return SESSION;
    }

    private static String fetch(String session) throws Exception {
        WebTarget target = CLIENT.target(BASE_URL)
                .queryParam("id", session.trim());
        Response response = target.request().get();
        assert response.getStatus() == 200;
        String result = response.readEntity(String.class);
        return result.trim();
    }
}
