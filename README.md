# Don't Waste It

**Don't Waste It** es una aplicación móvil desarrollada en Android que ayuda a los usuarios a gestionar los alimentos que compran,
evitar caducidades y reducir el desperdicio alimentario doméstico. Con una interfaz intuitiva, notificaciones inteligentes y estadísticas personalizadas,
la app busca fomentar hábitos de consumo más sostenibles.

## Funcionalidades principales

-  Registro de productos manual o por código de barras (con escáner integrado)
-  Consulta automática de información nutricional y visual desde Open Food Facts
-  Notificaciones de productos próximos a caducar
-  Estadísticas mensuales sobre consumo y desperdicio de alimentos
-  Indicadores visuales por cercanía de caducidad (verde/amarillo/rojo)
-  Gestión de categorías personalizadas
-  Seguimiento de productos consumidos
-  Comparación con el desperdicio medio español (4.4%)
-  Pantalla de bienvenida con explicación del propósito de la app

---

##  Tecnologías y librerías

- **Lenguaje:** Kotlin
- **Framework:** Android SDK
- **Persistencia local:** Room (SQLite)
- **Interfaz:** Material Components
- **Cámara + ML:** ML Kit + CameraX
- **Imágenes:** Glide
- **APIs:** Retrofit + Moshi
- **Estadísticas:** MPAndroidChart
- **Notificaciones:** WorkManager
- **Gestión de estados:** ViewModel + LiveData + Coroutines + Flow

---

## Permisos requeridos

- Cámara: Para escanear códigos de barras
- Notificaciones: Para avisar sobre productos próximos a caducar
- Internet: Para conectarse a la API Open Food Facts y poder obtener informacion de los alimentos escaneados
---

## Instalación

1. Clona este repositorio:

```bash

 git clone https://github.com/tuusuario/dont-waste-it.git

```
2. Abre el proyecto en Android Studio
3. Ejecuta el proyecto en un emulador o dispositivo físico (preferiblemente Android 11 o superior).
