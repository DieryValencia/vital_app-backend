# 🚂 Guía de Deployment en Railway

## Requisitos Previos

- Cuenta en Railway (https://railway.app)
- Cuenta en GitHub
- PostgreSQL Database creado en Railway
- Proyecto conectado a GitHub

## 🔧 Pasos para Desplegar en Railway

### 1. Conectar Repositorio GitHub

1. Ve a https://railway.app/dashboard
2. Crea nuevo proyecto (New Project)
3. Selecciona "Deploy from GitHub"
4. Autoriza GitHub y selecciona tu repositorio
5. Selecciona la rama `main`

### 2. Crear Base de Datos PostgreSQL

1. En Railway → New → Database
2. Selecciona PostgreSQL
3. Confirma la creación
4. La variable `DATABASE_URL` se asignará automáticamente

### 3. Configurar Variables de Entorno

Ve a Railway → Project → Variables y configura:

```
SPRING_PROFILES_ACTIVE=prod
JWT_SECRET=<generar una clave segura de 256 bits>
OPENAI_API_KEY=<tu API key de OpenAI de https://platform.openai.com/api-keys>
CORS_ORIGINS=https://tu-frontend.vercel.app,https://tu-dominio.com
HIBERNATE_DDL_AUTO=validate
SHOW_SQL=false
```

**Generador de JWT_SECRET seguro:**
```bash
# En terminal, ejecuta:
openssl rand -base64 32
```

O usa este comando en PowerShell:
```powershell
[Convert]::ToBase64String($(New-Object byte[] 32 | ForEach-Object {Get-Random -Min 0 -Max 256}))
```

### 4. Configurar Base de Datos Automáticamente

Railway agregará la variable `DATABASE_URL` automáticamente cuando crees PostgreSQL. Esta se inyectará en `application.properties` a través de:
```properties
spring.datasource.url=${DATABASE_URL:jdbc:postgresql://localhost:5432/vitalapp}
```

### 5. Ejecutar Deploy

1. Haz push a la rama `main` de GitHub
2. Railway detectará el cambio automáticamente
3. Iniciará el build:
   - Lee `system.properties` (Java 21)
   - Ejecuta `mvn clean package`
   - Inicia la aplicación

4. Monitorea el deploy en Railway → Deployments

### 6. Verificar que Funciona

Una vez desplegado, accede a:
- **Health Check:** https://tu-app.railway.app/actuator/health
- **Swagger UI:** https://tu-app.railway.app/swagger-ui.html (solo en dev)

## 📋 Variables de Entorno Completas

| Variable | Descripción | Ejemplo |
|----------|-------------|---------|
| `SPRING_PROFILES_ACTIVE` | Perfil activo (prod/dev) | `prod` |
| `DATABASE_URL` | URL de PostgreSQL (auto Railway) | `postgresql://user:pass@host:5432/db` |
| `JWT_SECRET` | Clave para firmar tokens JWT | `base64_encoded_32_bytes` |
| `OPENAI_API_KEY` | API key de OpenAI | `sk-...` |
| `CORS_ORIGINS` | Orígenes permitidos | `https://app.ejemplo.com,https://admin.ejemplo.com` |
| `PORT` | Puerto (asignado por Railway) | `8080` (automático) |
| `HIBERNATE_DDL_AUTO` | Estrategia de DDL | `validate` |
| `SHOW_SQL` | Mostrar SQL en logs | `false` |

## 🔐 Seguridad

- ✅ No incluyas secretos en `application.properties` (usa variables de entorno)
- ✅ JWT_SECRET debe tener mínimo 256 bits
- ✅ OPENAI_API_KEY nunca en el repo (solo en variables Railway)
- ✅ HTTPS se habilita automáticamente en Railway
- ✅ CORS está configurado dinámicamente desde CORS_ORIGINS

## 🐛 Solución de Problemas

### Error: "OpenAI API key is invalid"
**Causa:** La variable OPENAI_API_KEY no está configurada o es inválida
**Solución:** Verifica que esté correctamente asignada en Railway → Variables

### Error: "Database connection failed"
**Causa:** DATABASE_URL no está configurada
**Solución:** Crea una PostgreSQL Database en Railway, se asignará automáticamente

### Error: "Authentication failed for JWT"
**Causa:** JWT_SECRET no coincide entre compilaciones
**Solución:** Usa la misma JWT_SECRET en Railway

### Error de CORS desde frontend
**Causa:** Tu dominio no está en CORS_ORIGINS
**Solución:** Actualiza la variable con tu nuevo dominio

## 📈 Monitoreo

Railway proporciona:
- Logs en tiempo real
- Métricas de CPU y memoria
- Health checks automáticos
- Reinicio automático si falla

## 📞 URLs Útiles

- **Dashboard Railway:** https://railway.app/dashboard
- **Documentación Railway:** https://docs.railway.app
- **OpenAI API Keys:** https://platform.openai.com/api-keys
- **Generador JWT:** https://jwt.io

## ✅ Checklist de Deploy

- [ ] GitHub conectado a Railway
- [ ] PostgreSQL creada en Railway
- [ ] Variables de entorno configuradas:
  - [ ] SPRING_PROFILES_ACTIVE=prod
  - [ ] JWT_SECRET configurado
  - [ ] OPENAI_API_KEY configurado
  - [ ] CORS_ORIGINS con tu dominio
- [ ] Push a rama main
- [ ] Deployment exitoso (/actuator/health retorna 200)
- [ ] Frontend puede conectar al backend (sin error 401/403)

## 🎉 ¡Deployment Completado!

Tu backend ahora está corriendo en Railway y listo para recibir requests del frontend.
