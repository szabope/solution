package com.prospect;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.util.Properties;

public class DomainConfig {

    private final Logger logger = LoggerFactory.getLogger(DomainConfig.class);

    private final String domain;
    private final int httpPort;
    private final int httpsPort;
    private final int checkInterval;
    private final int timeoutMs;

    public DomainConfig(Properties props) {
        domain = props.getProperty("domain");
        httpPort = Integer.parseInt(props.getProperty("http_port"));
        httpsPort = Integer.parseInt(props.getProperty("https_port"));
        checkInterval = Integer.parseInt(props.getProperty("check_interval"));
        timeoutMs = Integer.parseInt(props.getProperty("timeout_ms"));
        checkConfig();
    }

    private void checkConfig() {
        assert domain != null;
    }

    public void check() {
        try {
            InetAddress inet = InetAddress.getByName(domain);
            String ip = inet.getHostAddress();
            boolean httpReachable = isReachable(ip, httpPort);
            boolean httpsReachable = isReachable(ip, httpsPort);
            report(domain, ip, stringify(httpReachable), stringify(httpsReachable));
        } catch (IOException e) {
            logger.error("Host cannot be resolved: {}", domain, e);
        }
    }

    private void report(String domain, String ip, String httpPing, String httpsPing) {
        String line = String.format("%20s %18s HTTP: %s | HTTPS: %s", domain, ip, httpPing, httpsPing);
        logger.info(line);
    }

    private String stringify(boolean value) {
        return value ? "UP" : "DOWN!";
    }

    private boolean isReachable(String ip, int port) {
        try (Socket socket = new Socket()) {
            socket.connect(new InetSocketAddress(ip, port), timeoutMs);
            return true;
        } catch (IOException e) {
            return false;
        }
    }

    public long getInterval() {
        return checkInterval;
    }
}
