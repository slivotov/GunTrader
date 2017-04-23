package ru.forceofshit.config.internal;

public class OpskinHeaders {
    private String cookiesHeaderValue;
    private String xcsrHeaderValue;
    private String steamId;

    public OpskinHeaders(String cookiesHeaderValue, String xcsrHeaderValue, String steamId) {
        this.cookiesHeaderValue = cookiesHeaderValue;
        this.xcsrHeaderValue = xcsrHeaderValue;
        this.steamId = steamId;
    }

    public String getCookiesHeaderValue() {
        return cookiesHeaderValue;
    }

    public String getXcsrHeaderValue() {
        return xcsrHeaderValue;
    }

    public String getSteamId() {
        return steamId;
    }
}
