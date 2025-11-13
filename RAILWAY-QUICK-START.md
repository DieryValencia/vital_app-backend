# 🚀 GUÍA RÁPIDA: DESPLEGAR EN RAILWAY (5 minutos)

## Paso 1️⃣ : Preparar Variables de Entorno

Necesitas estos valores listos:

```
JWT_SECRET = [Genera con: openssl rand -base64 32]
OPENAI_API_KEY = [De https://platform.openai.com/api-keys]
CORS_ORIGINS = [Tu dominio del frontend, ej: https://app.ejemplo.com]
```

## Paso 2️⃣: Conectar a Railway

1. Ve a https://railway.app/dashboard
2. Click en **"New Project"**
3. Selecciona **"Deploy from GitHub repo"**
4. Autoriza y elige tu repo
5. Selecciona rama **main**

## Paso 3️⃣: Crear Base de Datos

1. En Railway Dashboard → Click **"New"**
2. Selecciona **"Database" → "PostgreSQL"**
3. Confirma creación
4. La variable `DATABASE_URL` se asigna automáticamente ✅

## Paso 4️⃣: Configurar Variables

En Railway → Tu Proyecto → Variables → Agregar:

```
SPRING_PROFILES_ACTIVE=prod
JWT_SECRET=<tu_valor_generado>
OPENAI_API_KEY=<tu_api_key>
CORS_ORIGINS=<tu_dominio>
HIBERNATE_DDL_AUTO=validate
SHOW_SQL=false
```

⚠️ **DATABASE_URL se configura automáticamente de PostgreSQL**

## Paso 5️⃣: Deploy Automático

1. Haz push a `main` en GitHub
2. Railway detecta el cambio automáticamente
3. Inicia build y deploy
4. ✅ Listo en 2-3 minutos

## Verificar que Funciona

Accede a: `https://tu-app.railway.app/actuator/health`

Deberías ver:
```json
{
  "status": "UP",
  "components": {
    "db": {
      "status": "UP"
    }
  }
}
```

## Soluciones Rápidas

| Problema | Solución |
|----------|----------|
| "Invalid API key" | Verifica OPENAI_API_KEY en Railway Variables |
| "Database connection failed" | Asegúrate de crear PostgreSQL en Railway |
| "CORS error en frontend" | Agrega tu dominio a CORS_ORIGINS |
| "Build failed" | Revisa logs en Railway → Deployments |

## 🎉 ¡Listo!

Tu backend corre en Railway. Ahora conéctalo desde el frontend a:
```
https://tu-app.railway.app/api
```

---

**Más detalles:** Ver `README-DEPLOYMENT.md`
