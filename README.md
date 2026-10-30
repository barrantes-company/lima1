# lima1

Widgets de reloj para la pantalla de inicio de Android. La app configura la paleta e instala cada pieza.

## Widgets

| Pieza | Qué muestra |
|---|---|
| Editorial | Hora grande, fecha, sol y °C |
| Instrumento | Hora, avance del día y grados |
| Señal | Monograma tenue, hora y °C |
| Cartel | Hora en dos líneas y °C grande |

Paletas, por diseño: **Lima**, **Oro**, **Ámbar**. La hora la marca el reloj del sistema. La temperatura sale de la ubicación del teléfono o, si no hay permiso, de la red, en grados Celsius.

No hay widgets de bloqueo.

## Instalar

1. Descarga `lima1-widgets.apk` del release.
2. Ábrelo en el celular y acepta instalar desde esta fuente.
3. Abre **lima1**, elige la paleta y pulsa **Instalar**.
4. Si el sistema no fija el widget solo, mantén pulsado el inicio, entra a Widgets y busca lima1.

## Compilar

Hace falta el SDK de Android (platform 35 y build-tools 35).

```
echo "sdk.dir=$ANDROID_HOME" > local.properties
./gradlew assembleDebug
```

El APK queda en `app/build/outputs/apk/debug/`.
