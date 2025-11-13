═══════════════════════════════════════════════════════════════════════════════
🚀 PROMPT PARA CONFIGURAR FRONTEND - DEPLOYMENT EN VERCEL
═══════════════════════════════════════════════════════════════════════════════

Eres un experto desarrollador Frontend. Necesito que configures mi proyecto 
frontend para poder desplegarlo en Vercel y conectarlo con un backend que 
estará en Railway.

═══════════════════════════════════════════════════════════════════════════════
CONTEXTO DEL PROYECTO
═══════════════════════════════════════════════════════════════════════════════

**Proyecto:** VitalApp Frontend - Aplicación de Triaje Médico
**Framework:** React 18+ (o el que uses)
**Empaquetador:** Vite / Create React App
**Hosting:** Vercel (con dominio personalizado)

**Backend disponible en:**
- Desarrollo: http://localhost:8080/api
- Producción: https://vital-app-backend.railway.app/api

**Endpoints que necesito consumir:**
- POST /api/auth/register - Registro de usuario
- POST /api/auth/login - Login y obtener JWT token
- POST /api/patients - Crear paciente
- GET /api/patients/{id} - Obtener datos del paciente
- PUT /api/patients/{id} - Actualizar paciente
- POST /api/ai/analyze-symptoms - Análisis de síntomas con OpenAI
- POST /api/ai/generate-recommendation - Recomendaciones médicas
- POST /api/ai/chat - Chat con IA

**Autenticación:** JWT Token en header Authorization: Bearer {token}

═══════════════════════════════════════════════════════════════════════════════
ARCHIVOS A CREAR/MODIFICAR
═══════════════════════════════════════════════════════════════════════════════

1. .env.local - Variables de entorno para desarrollo local
2. .env.production - Variables de entorno para Vercel
3. vercel.json - Configuración de Vercel (rewrites para API proxy)
4. src/services/api.js (o api.ts) - Cliente HTTP con Axios/Fetch
5. src/hooks/useAuth.js - Hook para autenticación y JWT
6. src/hooks/useApi.js - Hook para llamadas a API con manejo de errores
7. src/utils/apiClient.js - Instancia configurada de cliente HTTP
8. package.json - Verificar versiones y scripts
9. src/config/config.js - Configuración centralizada
10. README.md - Instrucciones de deployment

═══════════════════════════════════════════════════════════════════════════════
REQUISITOS ESPECÍFICOS
═══════════════════════════════════════════════════════════════════════════════

**IMPORTANTE:**

1. **Variables de Entorno:**
   - REACT_APP_API_URL (o VITE_API_URL según tu bundler)
   - REACT_APP_API_TIMEOUT
   - Desarrollo apunta a localhost:8080, producción a https://vital-app-backend.railway.app

2. **Cliente HTTP:**
   - Manejar JWT tokens automáticamente
   - Agregar token al header Authorization de todas las peticiones
   - Refresh token si expira (si lo tienes)
   - Interceptores para errores globales (401, 403, 500)
   - Timeout de 30 segundos
   - Retry automático en errores de red

3. **Autenticación:**
   - Guardar token en localStorage o sessionStorage
   - Persistir usuario al refrescar página
   - Cerrar sesión: limpiar token y redirigir a login
   - Handle de tokens expirados

4. **Manejo de Errores:**
   - Mostrar mensajes amigables al usuario
   - Logs en desarrollo
   - No exponer errores internos en producción

5. **CORS:**
   - Frontend en vercel.app, backend en railway.app
   - Backend tiene CORS configurado para tu dominio
   - Enviar credenciales si es necesario

6. **Vercel Configuration:**
   - Rewrite /api/* al backend (proxy)
   - Variables de entorno secretas en dashboard de Vercel
   - Build command: npm run build (o yarn build)
   - Output directory: dist (si es Vite) o build (si es CRA)

7. **Performance:**
   - Lazy loading de componentes
   - Caché de respuestas donde sea posible
   - Compresión habilitada
   - Minificación

═══════════════════════════════════════════════════════════════════════════════
CÓDIGO DE LOS ARCHIVOS
═══════════════════════════════════════════════════════════════════════════════

───────────────────────────────────────────────────────────────────────────────
ARCHIVO 1: .env.local (DESARROLLO - No subir a repo)
───────────────────────────────────────────────────────────────────────────────
```env
# Desarrollo Local
VITE_API_URL=http://localhost:8080/api
VITE_API_TIMEOUT=30000
VITE_ENV=development

# Si usas Create React App, usa REACT_APP_ en lugar de VITE_
# REACT_APP_API_URL=http://localhost:8080/api
# REACT_APP_API_TIMEOUT=30000
```

───────────────────────────────────────────────────────────────────────────────
ARCHIVO 2: .env.production (PRODUCCIÓN - En Vercel)
───────────────────────────────────────────────────────────────────────────────
```env
# Estos valores se configuran en Vercel Dashboard
# NO incluir en el repo, solo como referencia

# API Backend en Railway
VITE_API_URL=https://vital-app-backend.railway.app/api
VITE_API_TIMEOUT=30000
VITE_ENV=production
```

───────────────────────────────────────────────────────────────────────────────
ARCHIVO 3: vercel.json (CONFIGURACIÓN DE VERCEL)
───────────────────────────────────────────────────────────────────────────────
```json
{
  "buildCommand": "npm run build",
  "outputDirectory": "dist",
  "env": {
    "VITE_API_URL": "@api_url",
    "VITE_API_TIMEOUT": "30000",
    "VITE_ENV": "production"
  },
  "rewrites": [
    {
      "source": "/api/(.*)",
      "destination": "https://vital-app-backend.railway.app/api/$1"
    }
  ]
}
```

Nota: Para Create React App cambiar "outputDirectory" a "build"

───────────────────────────────────────────────────────────────────────────────
ARCHIVO 4: src/utils/apiClient.js (CLIENTE HTTP)
───────────────────────────────────────────────────────────────────────────────
```javascript
import axios from 'axios';

const API_URL = import.meta.env.VITE_API_URL || 'http://localhost:8080/api';
const API_TIMEOUT = import.meta.env.VITE_API_TIMEOUT || 30000;

/**
 * Cliente HTTP configurado con Axios
 * - Maneja JWT tokens automáticamente
 * - Intercepta errores
 * - Reintentos en errores de red
 */
const apiClient = axios.create({
  baseURL: API_URL,
  timeout: parseInt(API_TIMEOUT),
  headers: {
    'Content-Type': 'application/json',
  },
});

/**
 * Interceptor de solicitud: Agregar token JWT
 */
apiClient.interceptors.request.use(
  (config) => {
    const token = localStorage.getItem('token');
    if (token) {
      config.headers.Authorization = `Bearer ${token}`;
    }
    return config;
  },
  (error) => {
    return Promise.reject(error);
  }
);

/**
 * Interceptor de respuesta: Manejar errores globales
 */
apiClient.interceptors.response.use(
  (response) => response,
  (error) => {
    // Token expirado
    if (error.response?.status === 401) {
      localStorage.removeItem('token');
      localStorage.removeItem('user');
      window.location.href = '/login';
    }

    // Acceso denegado
    if (error.response?.status === 403) {
      console.error('Acceso denegado');
    }

    // Error del servidor
    if (error.response?.status >= 500) {
      console.error('Error del servidor:', error.response.data);
    }

    return Promise.reject(error);
  }
);

export default apiClient;
```

───────────────────────────────────────────────────────────────────────────────
ARCHIVO 5: src/services/api.js (SERVICIOS DE API)
───────────────────────────────────────────────────────────────────────────────
```javascript
import apiClient from '../utils/apiClient';

/**
 * Servicio de Autenticación
 */
export const authService = {
  register: (userData) => apiClient.post('/auth/register', userData),
  login: (email, password) => apiClient.post('/auth/login', { email, password }),
  logout: () => {
    localStorage.removeItem('token');
    localStorage.removeItem('user');
  },
};

/**
 * Servicio de Pacientes
 */
export const patientService = {
  create: (patientData) => apiClient.post('/patients', patientData),
  getById: (id) => apiClient.get(`/patients/${id}`),
  update: (id, patientData) => apiClient.put(`/patients/${id}`, patientData),
  list: (filters = {}) => apiClient.get('/patients', { params: filters }),
};

/**
 * Servicio de IA
 */
export const aiService = {
  analyzeSymptoms: (symptoms) =>
    apiClient.post('/ai/analyze-symptoms', { symptoms }),

  generateRecommendation: (symptoms, medicalHistory) =>
    apiClient.post('/ai/generate-recommendation', {
      symptoms,
      medicalHistory,
    }),

  chat: (prompt) => apiClient.post('/ai/chat', { prompt }),
};

/**
 * Servicio de Triaje
 */
export const triageService = {
  create: (triageData) => apiClient.post('/triage', triageData),
  getById: (id) => apiClient.get(`/triage/${id}`),
  list: () => apiClient.get('/triage'),
};
```

───────────────────────────────────────────────────────────────────────────────
ARCHIVO 6: src/hooks/useAuth.js (HOOK DE AUTENTICACIÓN)
───────────────────────────────────────────────────────────────────────────────
```javascript
import { useState, useEffect, useCallback } from 'react';
import { authService } from '../services/api';

/**
 * Hook para manejar autenticación y JWT token
 * - Mantiene el usuario en estado local y localStorage
 * - Persiste la sesión al refrescar
 */
export const useAuth = () => {
  const [user, setUser] = useState(null);
  const [token, setToken] = useState(null);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState(null);

  // Cargar usuario y token del localStorage al montar el componente
  useEffect(() => {
    const savedUser = localStorage.getItem('user');
    const savedToken = localStorage.getItem('token');

    if (savedUser && savedToken) {
      try {
        setUser(JSON.parse(savedUser));
        setToken(savedToken);
      } catch (e) {
        console.error('Error al cargar usuario guardado:', e);
        localStorage.removeItem('user');
        localStorage.removeItem('token');
      }
    }

    setLoading(false);
  }, []);

  const register = useCallback(async (userData) => {
    try {
      setLoading(true);
      setError(null);
      const response = await authService.register(userData);
      const { token: newToken, user: newUser } = response.data;

      localStorage.setItem('token', newToken);
      localStorage.setItem('user', JSON.stringify(newUser));

      setToken(newToken);
      setUser(newUser);

      return response.data;
    } catch (err) {
      const message = err.response?.data?.message || 'Error en registro';
      setError(message);
      throw err;
    } finally {
      setLoading(false);
    }
  }, []);

  const login = useCallback(async (email, password) => {
    try {
      setLoading(true);
      setError(null);
      const response = await authService.login(email, password);
      const { token: newToken, user: newUser } = response.data;

      localStorage.setItem('token', newToken);
      localStorage.setItem('user', JSON.stringify(newUser));

      setToken(newToken);
      setUser(newUser);

      return response.data;
    } catch (err) {
      const message = err.response?.data?.message || 'Error en login';
      setError(message);
      throw err;
    } finally {
      setLoading(false);
    }
  }, []);

  const logout = useCallback(() => {
    authService.logout();
    setUser(null);
    setToken(null);
    setError(null);
  }, []);

  const isAuthenticated = !!token && !!user;

  return {
    user,
    token,
    loading,
    error,
    register,
    login,
    logout,
    isAuthenticated,
  };
};
```

───────────────────────────────────────────────────────────────────────────────
ARCHIVO 7: src/hooks/useApi.js (HOOK GENÉRICO PARA API)
───────────────────────────────────────────────────────────────────────────────
```javascript
import { useState, useCallback } from 'react';
import apiClient from '../utils/apiClient';

/**
 * Hook genérico para llamadas a API
 * Maneja loading, error y data automáticamente
 *
 * Uso:
 * const { data, loading, error, execute } = useApi();
 * await execute(() => patientService.getById(id));
 */
export const useApi = () => {
  const [data, setData] = useState(null);
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState(null);

  const execute = useCallback(async (apiCall) => {
    try {
      setLoading(true);
      setError(null);
      const response = await apiCall();
      setData(response.data);
      return response.data;
    } catch (err) {
      const errorMessage =
        err.response?.data?.message || 
        err.message || 
        'Error desconocido';
      setError(errorMessage);
      throw err;
    } finally {
      setLoading(false);
    }
  }, []);

  return { data, loading, error, execute };
};
```

───────────────────────────────────────────────────────────────────────────────
ARCHIVO 8: src/config/config.js (CONFIGURACIÓN CENTRALIZADA)
───────────────────────────────────────────────────────────────────────────────
```javascript
/**
 * Configuración centralizada de la aplicación
 */
export const config = {
  api: {
    url: import.meta.env.VITE_API_URL || 'http://localhost:8080/api',
    timeout: parseInt(import.meta.env.VITE_API_TIMEOUT) || 30000,
  },
  env: import.meta.env.VITE_ENV || 'development',
  isDevelopment: import.meta.env.DEV,
  isProduction: import.meta.env.PROD,
};

// Log configuración en desarrollo
if (config.isDevelopment) {
  console.log('🔧 Configuración:', config);
}
```

───────────────────────────────────────────────────────────────────────────────
ARCHIVO 9: package.json (VERIFICAR/ACTUALIZAR)
───────────────────────────────────────────────────────────────────────────────
```json
{
  "name": "vitalapp-frontend",
  "version": "1.0.0",
  "type": "module",
  "scripts": {
    "dev": "vite",
    "build": "vite build",
    "preview": "vite preview",
    "lint": "eslint src --ext .js,.jsx",
    "format": "prettier --write src/**/*.{js,jsx,css,md}"
  },
  "dependencies": {
    "react": "^18.2.0",
    "react-dom": "^18.2.0",
    "axios": "^1.6.0",
    "react-router-dom": "^6.20.0"
  },
  "devDependencies": {
    "@vitejs/plugin-react": "^4.2.0",
    "vite": "^5.0.0",
    "eslint": "^8.55.0",
    "prettier": "^3.1.0"
  }
}
```

───────────────────────────────────────────────────────────────────────────────
ARCHIVO 10: .gitignore (NO SUBIR SECRETOS)
───────────────────────────────────────────────────────────────────────────────
```gitignore
# Dependencies
node_modules/
/.pnp
.pnp.js

# Testing
/coverage

# Production
/build
/dist
.vercel

# Misc
.DS_Store
.env
.env.local
.env.*.local
npm-debug.log*
yarn-debug.log*
yarn-error.log*

# Editor
.vscode/
.idea/
*.swp
*.swo
*~

# IDE
.DS_Store
.env.production.local
```

───────────────────────────────────────────────────────────────────────────────
ARCHIVO 11: README.md - INSTRUCCIONES DE DEPLOYMENT
───────────────────────────────────────────────────────────────────────────────
```markdown
# VitalApp Frontend

Frontend de la aplicación de triaje médico con análisis de síntomas usando OpenAI.

## 🚀 Inicio Rápido

### Requisitos
- Node.js 18+
- npm o yarn

### Instalación Local

```bash
# Instalar dependencias
npm install

# Crear archivo .env.local
cp .env.example .env.local

# Desarrollo
npm run dev
```

La aplicación corre en http://localhost:5173

### Variables de Entorno Necesarias

```env
VITE_API_URL=http://localhost:8080/api
VITE_API_TIMEOUT=30000
VITE_ENV=development
```

## 📦 Build para Producción

```bash
npm run build
npm run preview
```

## 🚢 Deployment en Vercel

### Opción 1: Deploy Automático desde GitHub

1. Ve a https://vercel.com/dashboard
2. Click "Add New..." → "Project"
3. Selecciona tu repositorio
4. Vercel detectará automáticamente Vite/React
5. Configura variables de entorno en Vercel:
   - `VITE_API_URL=https://vital-app-backend.railway.app/api`
   - `VITE_API_TIMEOUT=30000`
6. Click "Deploy"

### Opción 2: Deploy Manual con Vercel CLI

```bash
# Instalar Vercel CLI
npm i -g vercel

# Login
vercel login

# Deploy
vercel --prod
```

## 🔑 Configurar Variables en Vercel

1. Vercel Dashboard → Settings → Environment Variables
2. Agregar:
   - `VITE_API_URL`: https://vital-app-backend.railway.app/api
   - `VITE_API_TIMEOUT`: 30000

## 🧪 Testear Conexión con Backend

Desde la consola del navegador:
```javascript
// Debe retornar 200
fetch('https://vital-app-backend.railway.app/actuator/health')
  .then(r => r.json())
  .then(console.log);
```

## 📋 Estructura de Carpetas

```
src/
├── components/       # Componentes reutilizables
├── pages/           # Páginas (Login, Dashboard, etc)
├── services/        # Servicios de API
├── hooks/           # Hooks personalizados
├── utils/           # Utilidades
├── config/          # Configuración
└── App.jsx
```

## 🐛 Solución de Problemas

### Error: "Failed to connect to backend"
- Verifica que `VITE_API_URL` esté correcta en Vercel
- Backend debe estar ejecutándose en Railway
- Verifica CORS: El backend debe permitir tu dominio de Vercel

### Error: "JWT token invalid"
- Limpia localStorage: Abre DevTools → Application → Clear All
- Intenta login nuevamente

### Error: "CORS blocked"
- Verifica que el backend tiene CORS configurado para tu dominio
- Backend debe incluir tu dominio en `CORS_ORIGINS`

## 📚 Más Información

- [Documentación Vite](https://vitejs.dev)
- [Documentación Vercel](https://vercel.com/docs)
- [Documentación React](https://react.dev)
- [Documentación Axios](https://axios-http.com)
```

═══════════════════════════════════════════════════════════════════════════════
INSTRUCCIONES DE IMPLEMENTACIÓN
═══════════════════════════════════════════════════════════════════════════════

1. **Copia este prompt completo** (desde "PROYECTO: VitalApp Frontend" hasta aquí)

2. **Pégalo en Claude/ChatGPT** con el siguiente prefijo:

"Eres un experto desarrollador Frontend React/Vite. 
Aquí está el prompt completo para configurar mi frontend:"

3. **Luego agrega lo siguiente:**

"Por favor:
- Crea todos los archivos listados
- Adapta los paths según mi estructura actual
- Explica qué hace cada archivo
- Proporciona instrucciones paso a paso
- Incluye ejemplos de uso de los hooks
- Sugiere mejoras de seguridad
- Incluye tipos TypeScript si usas TS"

4. **Si usas TypeScript**, solicita versiones .ts en lugar de .js

═══════════════════════════════════════════════════════════════════════════════
CHECKLIST FINAL
═══════════════════════════════════════════════════════════════════════════════

- [ ] .env.local configurado para desarrollo local
- [ ] vercel.json creado con rewrites
- [ ] apiClient.js con interceptores
- [ ] useAuth hook implementado
- [ ] Todos los servicios de API listos
- [ ] package.json con dependencias correctas
- [ ] .gitignore incluye .env y .env.local
- [ ] README.md con instrucciones
- [ ] Build local funciona: npm run build
- [ ] Push a GitHub
- [ ] Conectar a Vercel
- [ ] Configurar variables en Vercel
- [ ] Deploy exitoso
- [ ] Verificar que se conecta al backend en Railway

═══════════════════════════════════════════════════════════════════════════════
🎉 ¡LISTO!

Tu frontend estará desplegado en Vercel y conectado al backend en Railway.
Ambas plataformas se comunicarán correctamente con seguridad y manejo de errores.
═══════════════════════════════════════════════════════════════════════════════
