package com.yul2ya.rxjava;

public class FakeTemplateApp {
    private String id;
    private String url;
    private String version;
    private String token;

    public FakeTemplateApp(String id, String url, String version, String token) {
        this.id = id;
        this.url = url;
        this.version = version;
        this.token = token;
    }

    public String getId() {
        return id;
    }

    public String getUrl() {
        return url;
    }

    public String getVersion() {
        return version;
    }

    public String getToken() {
        return token;
    }

    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof FakeTemplateApp)) return false;
        FakeTemplateApp object = (FakeTemplateApp) obj;
        return token != null && token.equals(object.getToken());
    }
}
