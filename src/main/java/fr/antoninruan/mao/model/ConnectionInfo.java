package fr.antoninruan.mao.model;

public class ConnectionInfo {

    private final String host;
    private final String name;
    private final double scale;

    public ConnectionInfo(String host, String name, double scale) {
        this.host = host;
        this.name = name;
        this.scale = scale;
    }

    public String getHost() {
        return host;
    }

    public String getName() {
        return name;
    }

    public double getScale() {
        return scale;
    }
}
