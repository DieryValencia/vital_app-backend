# 🚂 SOLUCIÓN: Error "No se puede acceder al archivo jar" en Railway

## ✅ PROBLEMA RESUELTO

El error ocurría porque:
- **Causa:** El nombre del JAR generado no coincidía con el esperado por el `Procfile`
- **Antes:** `vital_app-backend-0.0.1-SNAPSHOT.jar`
- **Ahora:** `vital_app-backend.jar` ✓

## 🔧 CAMBIOS REALIZADOS

### 1. **pom.xml** - Agregar `finalName`
```xml
<build>
  <plugins>
    <!-- ... plugins ... -->
  </plugins>
  <finalName>vital_app-backend</finalName>  <!-- ← AGREGADO -->
</build>
```

### 2. **Procfile** - Actualizar ruta del JAR
```
web: java -Dserver.port=$PORT -Dspring.profiles.active=prod -jar target/vital_app-backend.jar
```

### 3. **Verificación Local**
```
✅ mvn clean package -DskipTests 
✅ JAR generado: target/vital_app-backend.jar (78 MB)
✅ Build completado exitosamente
```

## 🚀 PRÓXIMOS PASOS EN RAILWAY

1. **Ir a Railway Dashboard**
   - Tu Project → Deployments
   - Ver el estado del nuevo build

2. **El build deberá:**
   - ✅ Detectar Java 21 (desde `system.properties`)
   - ✅ Ejecutar `mvn clean package`
   - ✅ Generar `target/vital_app-backend.jar` 
   - ✅ Ejecutar con `Procfile`

3. **Verificar que está corriendo:**
   ```bash
   curl https://tu-app.railway.app/actuator/health
   ```
   
   Debería responder:
   ```json
   {
     "status": "UP"
   }
   ```

## 📋 CHECKLIST ANTES DE RAILWAY

- [x] JAR se genera correctamente localmente
- [x] system.properties especifica Java 21
- [x] Procfile tiene ruta correcta del JAR
- [x] application.properties usa variables de entorno
- [x] application-prod.properties configurado
- [x] .env local para desarrollo

## 🔑 VARIABLES DE ENTORNO REQUERIDAS EN RAILWAY

Antes de deployar, configura estas variables en Railway:

```
SPRING_PROFILES_ACTIVE=prod
JWT_SECRET=<generar_con_openssl_rand_-base64_32>
OPENAI_API_KEY=<tu_clave_de_openai>
CORS_ORIGINS=<tu_dominio_vercel>
DATABASE_URL=<automático_de_railway>
```

## ⚡ SI FALLA DE NUEVO EN RAILWAY

### Opción 1: Ver logs de Railway
- Dashboard → Deployments → Click en el deployment fallido
- Logs → Ver qué salió mal
- Copiar el error completo

### Opción 2: Buildear localmente con Docker
```bash
# Simulará el build de Railway localmente
docker run --rm -v "$(pwd)":/workspace -w /workspace maven:3.9-eclipse-temurin-21 mvn clean package -DskipTests
```

### Opción 3: Forzar rebuild en Railway
- Dashboard → Settings → Rebuild
- Click "Redeploy"

## 📚 ARCHIVOS RELACIONADOS

- `pom.xml` - Define el nombre del JAR
- `Procfile` - Cómo ejecutar la aplicación
- `system.properties` - Versión de Java
- `application.properties` - Variables de entorno
- `application-prod.properties` - Configuración de producción

## ✅ ESTADO ACTUAL

| Componente | Estado |
|-----------|--------|
| JAR generation | ✅ Funciona |
| Local build | ✅ Exitoso |
| Railway config | ✅ Actualizado |
| GitHub | ✅ Pusheado |
| Ready for deploy | ✅ Sí |

---

**Ahora Railway debería encontrar y ejecutar el JAR correctamente. 🚀**
