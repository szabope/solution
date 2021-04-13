# 📟 Uptime Monitor

Prospect integrates with several third-party APIs and services.

This application is an **uptime monitor** to monitor our third-party API integrations.

## Configuration
Number of threads: application.properties

Under folder 'domain' you may add or remove configurations. These configurations will be picked up automatically on startup.

Domain configuration example (all fields are mandatory):
```
domain=gmail.com
http_port=80
https_port=443
check_interval=5
timeout_ms=1000
```