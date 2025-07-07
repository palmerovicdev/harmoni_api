# Harmoni API

![Java](https://img.shields.io/badge/Java-17-orange)
![Spring Boot](https://img.shields.io/badge/Spring%20Boot-3.4.5-brightgreen)
![PostgreSQL](https://img.shields.io/badge/PostgreSQL-Database-blue)
![FFmpeg](https://img.shields.io/badge/FFmpeg-Video%20Processing-red)

## 📋 Descripción

**Harmoni API** es una aplicación backend desarrollada en Spring Boot que permite el seguimiento y análisis de emociones a través del procesamiento de contenido multimedia (video y audio). La aplicación está diseñada para ayudar a los usuarios a monitorear sus estados emocionales vinculados a diferentes actividades.

## 🚀 Características Principales

### 🔐 Autenticación y Usuarios
- **Registro de usuarios** con validación de email y nombre
- **Autenticación JWT** para sesiones seguras
- **Gestión de perfiles** con información personal (edad, género, avatar)
- **Validación de datos** en tiempo real
- **Eliminación de cuentas** con confirmación

### 📊 Gestión de Actividades
- **Creación y gestión de actividades** personalizadas
- **Organización visual** con colores personalizados
- **Eliminación de actividades** no deseadas
- **Seguimiento temporal** de creación y modificación

### 🎭 Análisis de Emociones
- **Procesamiento de videos** para extracción de audio
- **Análisis emocional** a través de API externa especializada
- **Detección de múltiples emociones:**
  - Felicidad (HAPPY)
  - Tristeza (SAD)
  - Enojo (ANGRY)
  - Miedo (FEARFUL)
  - Sorpresa (SURPRISED)
  - Disgusto (DISGUSTED)
  - Neutral (NEUTRAL)
  - Otros (OTHER)

### 🎥 Procesamiento Multimedia
- **Extracción de audio** de archivos de video usando FFmpeg
- **Segmentación inteligente** de contenido multimedia
- **Soporte para múltiples formatos** de video
- **Procesamiento en tiempo real** con manejo de archivos temporales

### ⚙️ Configuraciones Personalizadas
- **Ajustes de usuario** personalizables
- **Configuración de preferencias** de análisis
- **Gestión de configuraciones** por usuario

## 🛠️ Tecnologías Utilizadas

### Backend
- **Java 17** - Lenguaje de programación principal
- **Spring Boot 3.4.5** - Framework principal
- **Spring Security** - Autenticación y autorización
- **Spring Data JPA** - Persistencia de datos
- **Spring WebFlux** - Programación reactiva
- **Hibernate** - ORM para base de datos

### Base de Datos
- **PostgreSQL** - Base de datos principal
- **Hibernate Types** - Tipos de datos avanzados

### Procesamiento Multimedia
- **FFmpeg** - Procesamiento de video y audio
- **JavaCV** - Wrapper de OpenCV para Java
- **Bytedeco** - Bindings para librerías nativas
- **MP4Parser** - Análisis de archivos MP4

### Seguridad
- **JWT (JSON Web Tokens)** - Autenticación stateless
- **BCrypt** - Encriptación de contraseñas

### Desarrollo
- **Lombok** - Reducción de código boilerplate
- **Maven** - Gestión de dependencias
- **Spring Boot DevTools** - Desarrollo ágil

## 🏗️ Arquitectura del Sistema

```
harmoni_api/
├── controller/          # Controladores REST
│   ├── HomeController      # Actividades y emociones
│   ├── UserController      # Autenticación
│   └── MyProfileController # Perfil de usuario
├── service/            # Lógica de negocio
├── repository/         # Acceso a datos
├── model/             # Modelos de datos
│   ├── entity/           # Entidades JPA
│   ├── request/          # DTOs de petición
│   ├── response/         # DTOs de respuesta
│   └── enums/           # Enumeraciones
├── helper/            # Servicios auxiliares
│   ├── VideoProcessingService # Procesamiento multimedia
│   ├── EmotionApiClient      # Cliente API externa
│   └── VideoSegmenter       # Segmentación de video
├── security/          # Configuración de seguridad
└── core/             # Manejo de excepciones y utilidades
```

## 📦 Instalación y Configuración

### Prerrequisitos
- **Java 17** o superior
- **Maven 3.6+**
- **PostgreSQL 12+**
- **FFmpeg** instalado en el sistema

### 1. Clonar el Repositorio
```bash
git clone <tu-repositorio>
cd harmoni_api
```

### 2. Configurar Base de Datos
```sql
-- Crear base de datos
CREATE DATABASE harmoni;

-- Crear usuario (opcional)
CREATE USER palmerodev WITH PASSWORD 'victor';
GRANT ALL PRIVILEGES ON DATABASE harmoni TO palmerodev;
```

### 3. Configurar Variables de Entorno
Edita el archivo `src/main/resources/application.properties`:

```properties
# Configuración del servidor
server.port=8081
server.address=0.0.0.0

# Configuración de base de datos
spring.datasource.url=jdbc:postgresql://localhost:5432/harmoni
spring.datasource.username=palmerodev
spring.datasource.password=victor

# Configuración JWT
jwt.secret=tu-clave-secreta-super-segura

# API externa de emociones
emotion.api.base-url=http://localhost:8000

# Configuración de archivos
spring.servlet.multipart.max-file-size=200MB
spring.servlet.multipart.max-request-size=200MB
```

### 4. Instalar FFmpeg
```bash
# macOS
brew install ffmpeg

# Ubuntu/Debian
sudo apt update
sudo apt install ffmpeg

# Windows
# Descargar desde https://ffmpeg.org/download.html
```

### 5. Ejecutar la Aplicación
```bash
# Compilar y ejecutar
mvn clean install
mvn spring-boot:run

# O usando el wrapper
./mvnw spring-boot:run
```

## 🔌 API Externa de Emociones

La aplicación requiere una API externa para el análisis de emociones que debe estar corriendo en `http://localhost:8000`. Esta API debe aceptar archivos de audio y devolver análisis emocionales en formato JSON.

### Formato de Respuesta Esperado
```json
{
  "predictions": [
    {
      "label": "HAPPY",
      "probability": 0.85
    }
  ],
  "success": "true"
}
```

## 📚 Endpoints de la API

### 🔐 Autenticación (`/auth`)
- `POST /auth/signUp` - Registro de usuarios
- `POST /auth/signIn` - Inicio de sesión

### 🏠 Actividades (`/home`)
- `GET /home/activities` - Obtener actividades del usuario
- `POST /home/createActivity` - Crear nueva actividad
- `DELETE /home/deleteActivity/{id}` - Eliminar actividad
- `POST /home/trackEmotion` - Procesar video/audio para análisis emocional
- `GET /home/emotionsByActivity/{activityId}` - Obtener emociones por actividad
- `GET /home/emotions` - Obtener todas las emociones del usuario

### 👤 Perfil (`/myProfile`)
- `GET /myProfile/getUserProfile` - Obtener perfil del usuario
- `POST /myProfile/update` - Actualizar perfil
- `DELETE /myProfile/delete` - Eliminar cuenta
- `POST /myProfile/saveSettings` - Guardar configuraciones
- `GET /myProfile/getSettingsForUser` - Obtener configuraciones
- `GET /myProfile/validateEmail/{email}` - Validar email
- `GET /myProfile/validateName/{name}` - Validar nombre

## 📊 Modelos de Datos

### Usuario (UserInfo)
```java
{
  "id": Long,
  "name": String,
  "email": String,
  "gender": String,
  "age": Integer,
  "avatar": String,
  "role": Role,
  "createdAt": Timestamp,
  "updatedAt": Timestamp
}
```

### Actividad (ActivityEntity)
```java
{
  "id": Long,
  "name": String,
  "color": String,
  "userInfo": UserInfo,
  "createdAt": Timestamp,
  "updatedAt": Timestamp
}
```

### Seguimiento de Emociones (EmotionTrackEntity)
```java
{
  "id": Long,
  "percentage": Double,
  "emotionTrackType": EmotionTrackType,
  "emotionType": EmotionType,
  "userInfo": UserInfo,
  "activity": ActivityEntity,
  "createdAt": Timestamp,
  "updatedAt": Timestamp
}
```

## 🔄 Flujo de Procesamiento de Emociones

1. **Subida de Video**: El usuario sube un archivo de video
2. **Extracción de Audio**: FFmpeg extrae el audio en formato WAV
3. **Segmentación**: El audio se divide en segmentos procesables
4. **Análisis Externo**: Los segmentos se envían a la API externa
5. **Procesamiento de Resultados**: Se interpretan las respuestas
6. **Almacenamiento**: Se guardan los resultados en la base de datos
7. **Limpieza**: Se eliminan archivos temporales

## 🛡️ Seguridad

- **Autenticación JWT** para todas las rutas protegidas
- **Encriptación de contraseñas** con BCrypt
- **Validación de entrada** en todos los endpoints
- **Manejo de errores** centralizado
- **Filtros de seguridad** personalizados

## 📁 Gestión de Archivos

Los archivos temporales se almacenan en `temp_service_files/` durante el procesamiento y se limpian automáticamente después del análisis.

## 🔧 Configuración de Desarrollo

### Logging
```properties
logging.level.root=debug
logging.level.org.springframework=debug
logging.level.org.hibernate.SQL=DEBUG
```

### Perfil de Desarrollo
```properties
spring.jpa.hibernate.ddl-auto=update
spring.main.allow-circular-references=true
```

## 🚀 Despliegue

### Compilar para Producción
```bash
mvn clean package -DskipTests
```

### Ejecutar JAR
```bash
java -jar target/harmoni_api-0.0.1-SNAPSHOT.jar
```

### Variables de Entorno de Producción
```bash
export SPRING_DATASOURCE_URL=jdbc:postgresql://prod-db:5432/harmoni
export SPRING_DATASOURCE_USERNAME=prod_user
export SPRING_DATASOURCE_PASSWORD=prod_password
export JWT_SECRET=production-secret-key
export EMOTION_API_BASE_URL=https://emotion-api.example.com
```

## 🤝 Contribución

1. Fork el proyecto
2. Crea una rama para tu feature (`git checkout -b feature/AmazingFeature`)
3. Commit tus cambios (`git commit -m 'Add some AmazingFeature'`)
4. Push a la rama (`git push origin feature/AmazingFeature`)
5. Abre un Pull Request

## 📄 Licencia

Este proyecto está licenciado bajo la [MIT License](LICENSE).

## 📞 Contacto

**Desarrollador**: PalmeroDev
**Email**: contact@palmerodev.com
**Proyecto**: Harmoni API - Sistema de Seguimiento de Emociones

---

⚡ **Powered by Spring Boot & FFmpeg** - Análisis de emociones en tiempo real
