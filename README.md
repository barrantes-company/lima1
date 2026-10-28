# lima1

Widgets de reloj para Android. La app es la pantalla de configuración: elige paleta e instala cada pieza en el inicio o, si el teléfono lo permite, en el bloqueo.

## Widgets

| Pieza | Inicio | Bloqueo |
|---|---|---|
| Editorial | Hora grande, fecha, sol y °C | Igual, centrado |
| Instrumento | Hora, avance del día y grados | Línea del día a lo ancho |
| Señal | Monograma muy tenue | Hora sola, marca casi apagada |
| Cartel | Hora en dos líneas y °C grande | Hora y grados abajo |

Paletas, por diseño: **Lima**, **Oro**, **Ámbar**. La hora la marca el reloj del sistema. La temperatura sale de la ubicación del teléfono o, si no hay permiso, de la red, en grados Celsius.

## Instalar

1. Descarga `lima1-widgets.apk` del release.
2. Ábrelo en el celular y acepta instalar desde esta fuente.
3. Abre **lima1**, elige la paleta y pulsa **Instalar**.
4. Si el sistema no fija el widget solo, mantén pulsado el inicio, entra a Widgets y busca lima1.

El bloqueo depende del fabricante. Android no lo muestra en todos los equipos.

## Compilar

Hace falta el SDK de Android (platform 35 y build-tools 35).

```
echo "sdk.dir=$ANDROID_HOME" > local.properties
./gradlew assembleDebug
```

El APK queda en `app/build/outputs/apk/debug/`.
