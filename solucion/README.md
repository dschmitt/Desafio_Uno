# Solución

Para la solución se implementó un programa en Java que aplica el nivel 2.

Para compilar el programa hay que ubicarse en la carpeta de la solución y ejecutar:

```
$ javac -d bin -cp lib/* src/Desafio1.java
```

Para ejecutar el programa:

```
$ java -cp bin;lib/* Desafio1 [archivo de entrada]
```

El archivo de entrada es opcional. Si no se ingresa, el programa tratará de obtener los datos de la dirección:
```
http://127.0.0.1:8080/periodos/api
```

Se agregan un par de archivos de ejemplo para los datos de entrada y salida.

## Resumen del código

El programa primero lee los datos de entrada de la forma que se explicó anteriormente. Por problemas de ambiente, no se pudo probar el servicio de Generador de Datos (si no funciona se puede probar con el archivo de entrada).

Después de obtener los datos en formato JSON se revisan las fechas de creacion, fin y las fechas recibidas que se guardan en una lista ordenada. Empezando a iterar en el intervalo de fechas, si se encuentra una fecha ya recibida se avanza en la iteración de la lista. Si es una fecha faltante, se agrega a otra lista.

Con la lista de fechas faltantes se crea el archivo de salida (no es JSON como lo dice el nivel 2).