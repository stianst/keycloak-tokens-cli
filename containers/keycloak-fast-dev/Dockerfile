FROM quay.io/keycloak/keycloak:23.0.6 as builder

ENV KC_CACHE=local

RUN /opt/keycloak/bin/kc.sh build
RUN /opt/keycloak/bin/kc.sh export --file /tmp/export.json

FROM quay.io/keycloak/keycloak:23.0.6
COPY --from=builder /opt/keycloak/ /opt/keycloak/

ENV KEYCLOAK_ADMIN=admin
ENV KEYCLOAK_ADMIN_PASSWORD=admin

ENV KC_HTTP_ENABLED=true
ENV KC_HOSTNAME_STRICT=false
#ENV KC_LOG_LEVEL=WARN

ENTRYPOINT ["/opt/keycloak/bin/kc.sh"]
