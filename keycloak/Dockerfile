# ---- build the provider
FROM maven:3.9-eclipse-temurin-21 AS build
WORKDIR /src
COPY keycloak-his-auth/ ./keycloak-his-auth/
WORKDIR /src/keycloak-his-auth
RUN mvn -q -DskipTests package

# ---- Keycloak with the provider
FROM quay.io/keycloak/keycloak:26.3.3
COPY --from=build /src/keycloak-his-auth/target/keycloak-his-auth-*.jar /opt/keycloak/providers/
RUN /opt/keycloak/bin/kc.sh build
# default command can be start-dev for local; compose will override envs/command
