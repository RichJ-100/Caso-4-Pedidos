FROM mcr.microsoft.com/devcontainers/java:1-21

# Quita el repositorio de Yarn (clave GPG vencida) que rompe apt-get update
RUN rm -f /etc/apt/sources.list.d/yarn.list /etc/apt/sources.list.d/*yarn*