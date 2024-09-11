# Guía de instalación

Este repositorio contiene el código fuente para una aplicación de detección de animales. A continuación, se proporciona una guía para la instalación y configuración del proyecto en un entorno local.

## Requisitos

- **Java 17** o superior
- **Docker**
- **Git**
- **Python 3.7** o superior
- **Maven** 
- **Node.js** 
- **MongoDB** 
- **MinIO**

## Guía de Instalación

### 1. Clonar el Repositorio

Clona el repositorio en tu máquina local usando el siguiente comando:

```bash
git clone https://github.com/hugoavalos01/TFG_UMA
```

### 2. Descargar YOLOv5
Clona el repositorio de YOLOv5 desde el siguiente link: https://github.com/ultralytics/yolov5

Una vez descargado, ubícalo dentro de la carpeta `TFGApp`

### 3. Crear un entorno virtual de Python
Para usar YOLOv5, es necesario crear un entorno virtual de Python e instalar las dependencias necesarias. Aquí están los pasos:
```bash
# Crear un entorno virtual
python -m venv yolov5-env

# Activar el entorno virtual
# En Windows
yolov5-env\Scripts\activate
# En macOS/Linux
source yolov5-env/bin/activate

# Instalar las dependencias de YOLOv5
pip install -r yolov5/requirements.txt
```

### 4. Configuración de Docker
Para usar MongoDB y MinIO, asegúrate de tener Docker instalado y en ejecución. Luego, ejecuta los siguientes comandos:
```bash
# Ejecutar MongoDB
docker run --name mongodb -d -p 27017:27017 mongo

# Ejecutar MinIO
docker run --name minio -d -p 9000:9000 -e MINIO_ROOT_USER=admin -e MINIO_ROOT_PASSWORD=admin123 minio/minio server /data
```

### 5. Configuración de las conexiones
#### 1. MongoDB
Abre el archivo `src/main/resources/application.properties` y configura las conexiones para MongoDB según los detalles de tu entorno:
```bash
# Configuración de MongoDB
spring.data.mongodb.host=localhost
spring.data.mongodb.port=27017
spring.data.mongodb.database=nombre_de_base_de_datos
```
#### 2. MinIO

Abre el archivo `TfgAppApplication.java` y configura las credenciales de MinIO:
```bash
// Ejemplo de configuración para MinIO
minioClient = MinioClient.builder()
                .endpoint("http://localhost:9000")
                .credentials("admin", "admin123")
                .build();
```


### 6. Instalar dependencias y ejecutar la aplicación
#### Backend
Instala las dependencias del backend utilizando Maven y ejecuta la aplicación.

#### Frontend
Navega a la carpeta `web` y usa el comando `npm install` para instalar las dependencias. Por último ejecútalo usando `npm run serve`.

### 7. Acceso a la aplicación.
Una vez que hayas ejecutado ambas partes, la aplicación estará disponible en:
- **Frontend:** `http://localhost:8081` (o el puerto configurado en `package.json`)
- **Backend:** `http://localhost:8080` (o el puerto configurado en `application.properties`)




