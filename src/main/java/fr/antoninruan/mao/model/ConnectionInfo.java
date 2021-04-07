package fr.antoninruan.mao.model;

public class ConnectionInfo {

    private String host;
    private String name;

    public ConnectionInfo(String host, String name) {
        this.host = host;
        this.name = name;
    }

    public String getHost() {
        return host;
    }

    public String getName() {
        return name;
    }
}
