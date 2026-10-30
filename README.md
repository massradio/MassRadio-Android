# MassRadio para Android

Aplicación de radio en vivo de MassRadio Honduras. Usa el logotipo original y el stream SSL `https://radiostreaming.pro/8430/stream`.

## Generar APK sin Android Studio

1. Subir **el contenido** de este ZIP al repositorio GitHub `massradio/MassRadio-Android`, conservando las carpetas.
2. Abrir **Actions** > **Compilar APK MassRadio** > **Run workflow**.
3. Esperar que termine con marca verde; abrir la ejecución y descargar el artefacto `MassRadio-Android-APK-prueba`.
4. Descomprimir y probar `app-debug.apk` en un teléfono Android.

**IMPORTANTE:** Es una compilación **debug**, adecuada solo para pruebas personales. **No distribuir públicamente** este APK. Antes de compartirlo desde la web, crear un APK **release firmado** con una clave propia conservada de forma segura, y probar audio en segundo plano, llamadas entrantes, interrupciones de red y diferentes teléfonos. Nunca subir claves ni contraseñas al repositorio público.

La reproducción se realiza mediante un servicio en primer plano con notificación persistente. La primera versión no incluye reconexión automática ni controles avanzados en pantalla de bloqueo.
