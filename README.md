# CABITO - Mascota Virtual (JavaFX)

## Requisitos
- JDK 21
- Apache Maven 3.9 o superior
- JavaFX se descarga automáticamente mediante Maven.

## Ejecutar
Desde la carpeta del proyecto:

```bash
mvn clean javafx:run
```

La primera ejecución requiere conexión a internet para descargar dependencias Maven.
La partida se guarda en `cabito-partida.properties` dentro de la carpeta desde la que se ejecuta Maven.

## Controles
- Alimentar: cuesta 5 monedas.
- Dormir: recupera energía y un poco de salud.
- Jugar: aumenta felicidad, experiencia y monedas.
- Bañar: recupera higiene.
- Entrenar: consume energía y entrega experiencia y monedas.
- Tienda: permite comprar un snack por 10 monedas.
- Guardar/Cargar: persiste las estadísticas, monedas, nivel y experiencia.

La imagen suministrada está en `src/main/resources/images/cabito.png`.
