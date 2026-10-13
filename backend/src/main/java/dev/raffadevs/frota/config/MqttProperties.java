package dev.raffadevs.frota.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "app.mqtt")
public class MqttProperties {

    private boolean habilitado = true;
    private String brokerUrl;
    private String topicoPosicoes;
    private String clientIdPrefix;

    public boolean isHabilitado() {
        return habilitado;
    }

    public void setHabilitado(boolean habilitado) {
        this.habilitado = habilitado;
    }

    public String getBrokerUrl() {
        return brokerUrl;
    }

    public void setBrokerUrl(String brokerUrl) {
        this.brokerUrl = brokerUrl;
    }

    public String getTopicoPosicoes() {
        return topicoPosicoes;
    }

    public void setTopicoPosicoes(String topicoPosicoes) {
        this.topicoPosicoes = topicoPosicoes;
    }

    public String getClientIdPrefix() {
        return clientIdPrefix;
    }

    public void setClientIdPrefix(String clientIdPrefix) {
        this.clientIdPrefix = clientIdPrefix;
    }
}
