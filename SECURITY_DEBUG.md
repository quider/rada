# Debugging Spring Security + OAuth2 Keycloak

## Konfiguracja

✅ **SecurityConfig.java** - Włączone:
- OAuth2 Resource Server z JWT
- Public endpoints: `/swagger-ui/**`, `/api-docs/**`, `/auth/**`
- Chroniące endpointy: wymagają JWT tokenu
- Debug logging: `org.springframework.security: DEBUG`

✅ **application.yml** - Dodane:
```yaml
spring.security.oauth2.resourceserver.jwt.issuer-uri: http://keycloak:8080/realms/master
spring.security.oauth2.resourceserver.jwt.jwk-set-uri: http://keycloak:8080/realms/master/protocol/openid-connect/certs
```

✅ **application-dev.yml** - Profil dla lokalnego Keycloak (localhost:8180)

✅ **AuthController** - Endpointy:
- `GET /auth/me` - info o zalogowanym użytkowniku
- `POST /auth/provision` - sync użytkownika z Keycloak

## Jak testować

### 1. Publicze endpointy (bez JWT)
```bash
# Swagger
curl http://localhost:8080/swagger-ui.html

# API docs
curl http://localhost:8080/api-docs
```

### 2. Chronione endpointy (wymagają JWT)
```bash
# Bez tokenu - 401 Unauthorized
curl http://localhost:8080/api/v1/announcements

# Z tokenem (przykład)
curl -H "Authorization: Bearer <JWT_TOKEN>" http://localhost:8080/api/v1/announcements
```

### 3. Logowanie przez Keycloak (nowe)

#### Uzyskanie JWT tokenu z Keycloak:
```bash
# Replace: USERNAME, PASSWORD, REALM
curl -X POST http://localhost:8180/realms/master/protocol/openid-connect/token \
  -H "Content-Type: application/x-www-form-urlencoded" \
  -d "client_id=admin-cli" \
  -d "grant_type=password" \
  -d "username=admin" \
  -d "password=admin" \
  -d "scope=openid"
```

#### Pobierz `access_token` i użyj go:
```bash
curl -H "Authorization: Bearer <ACCESS_TOKEN>" http://localhost:8080/auth/me
```

## Debug Info

### Logi Spring Security
Sprawdź w console logi z prefixem:
```
DEBUG org.springframework.security - ...
DEBUG org.springframework.security.oauth2 - ...
```

### Problemy i rozwiązania

**Problem**: Requesty nie docierają do kontrolera (HTTP 401)
- ✅ Sprawdź czy JWT token jest ważny
- ✅ Upewnij się że Keycloak zwraca prawidłową strukturę JWT
- ✅ Sprawdź czy `issuer-uri` jest dostępny

**Problem**: Keycloak nie jest dostępny
- ✅ Sprawdź czy kontener Keycloak działa: `docker ps | grep keycloak`
- ✅ Zmień profil na `dev` w application.yml aby używać `localhost:8180`

**Problem**: JWK Set URI niedostępny
- ✅ Sprawdź connectiv ość do Keycloak: `curl http://localhost:8180/realms/master/protocol/openid-connect/certs`
- ✅ Ustaw timeout jeśli sieć jest wolna

## Dalsze kroki

1. **Konfiguracja Keycloak**: Utwórz realm, client, user w Keycloak UI
2. **Sync użytkowników**: Zaimplementuj logikę tworzenia użytkownika przy provisioning
3. **Bezpieczeństwo**: Hash passwordów zamiast plaintext
4. **Role-based access**: Dodaj @PreAuthorize dla kontrolerów bazowanych na rolach
