
### Escuela Colombiana de Ingeniería
### Arquitecturas de Software - ARSW
## Ejercicio Introducción al paralelismo - Hilos - Caso BlackListSearch

### Dependencias:
####   Lecturas:
*  [Threads in Java](http://beginnersbook.com/2013/03/java-threads/)  (Hasta 'Ending Threads')
*  [Threads vs Processes]( http://cs-fundamentals.com/tech-interview/java/differences-between-thread-and-process-in-java.php)

### Descripción
  Este ejercicio contiene una introducción a la programación con hilos en Java, además de la aplicación a un caso concreto.
  

**Parte I - Introducción a Hilos en Java**

1. De acuerdo con lo revisado en las lecturas, complete las clases CountThread, para que las mismas definan el ciclo de vida de un hilo que imprima por pantalla los números entre A y B.
2. Complete el método __main__ de la clase CountMainThreads para que:
	1. Cree 3 hilos de tipo CountThread, asignándole al primero el intervalo [0..99], al segundo [99..199], y al tercero [200..299].
	2. Inicie los tres hilos con 'start()'.
	3. Ejecute y revise la salida por pantalla. 
	4. Cambie el incio con 'start()' por 'run()'. Cómo cambia la salida?, por qué?.

**Parte II - Ejercicio Black List Search**


Para un software de vigilancia automática de seguridad informática se está desarrollando un componente encargado de validar las direcciones IP en varios miles de listas negras (de host maliciosos) conocidas, y reportar aquellas que existan en al menos cinco de dichas listas. 

Dicho componente está diseñado de acuerdo con el siguiente diagrama, donde:

- HostBlackListsDataSourceFacade es una clase que ofrece una 'fachada' para realizar consultas en cualquiera de las N listas negras registradas (método 'isInBlacklistServer'), y que permite también hacer un reporte a una base de datos local de cuando una dirección IP se considera peligrosa. Esta clase NO ES MODIFICABLE, pero se sabe que es 'Thread-Safe'.

- HostBlackListsValidator es una clase que ofrece el método 'checkHost', el cual, a través de la clase 'HostBlackListDataSourceFacade', valida en cada una de las listas negras un host determinado. En dicho método está considerada la política de que al encontrarse un HOST en al menos cinco listas negras, el mismo será registrado como 'no confiable', o como 'confiable' en caso contrario. Adicionalmente, retornará la lista de los números de las 'listas negras' en donde se encontró registrado el HOST.

![](img/Model.png)

Al usarse el módulo, la evidencia de que se hizo el registro como 'confiable' o 'no confiable' se dá por lo mensajes de LOGs:

INFO: HOST 205.24.34.55 Reported as trustworthy

INFO: HOST 205.24.34.55 Reported as NOT trustworthy


Al programa de prueba provisto (Main), le toma sólo algunos segundos análizar y reportar la dirección provista (200.24.34.55), ya que la misma está registrada más de cinco veces en los primeros servidores, por lo que no requiere recorrerlos todos. Sin embargo, hacer la búsqueda en casos donde NO hay reportes, o donde los mismos están dispersos en las miles de listas negras, toma bastante tiempo.

Éste, como cualquier método de búsqueda, puede verse como un problema [vergonzosamente paralelo](https://en.wikipedia.org/wiki/Embarrassingly_parallel), ya que no existen dependencias entre una partición del problema y otra.

Para 'refactorizar' este código, y hacer que explote la capacidad multi-núcleo de la CPU del equipo, realice lo siguiente:

1. Cree una clase de tipo Thread que represente el ciclo de vida de un hilo que haga la búsqueda de un segmento del conjunto de servidores disponibles. Agregue a dicha clase un método que permita 'preguntarle' a las instancias del mismo (los hilos) cuantas ocurrencias de servidores maliciosos ha encontrado o encontró.

2. Agregue al método 'checkHost' un parámetro entero N, correspondiente al número de hilos entre los que se va a realizar la búsqueda (recuerde tener en cuenta si N es par o impar!). Modifique el código de este método para que divida el espacio de búsqueda entre las N partes indicadas, y paralelice la búsqueda a través de N hilos. Haga que dicha función espere hasta que los N hilos terminen de resolver su respectivo sub-problema, agregue las ocurrencias encontradas por cada hilo a la lista que retorna el método, y entonces calcule (sumando el total de ocurrencuas encontradas por cada hilo) si el número de ocurrencias es mayor o igual a _BLACK_LIST_ALARM_COUNT_. Si se da este caso, al final se DEBE reportar el host como confiable o no confiable, y mostrar el listado con los números de las listas negras respectivas. Para lograr este comportamiento de 'espera' revise el método [join](https://docs.oracle.com/javase/tutorial/essential/concurrency/join.html) del API de concurrencia de Java. Tenga también en cuenta:

	* Dentro del método checkHost Se debe mantener el LOG que informa, antes de retornar el resultado, el número de listas negras revisadas VS. el número de listas negras total (línea 60). Se debe garantizar que dicha información sea verídica bajo el nuevo esquema de procesamiento en paralelo planteado.

	* Se sabe que el HOST 202.24.34.55 está reportado en listas negras de una forma más dispersa, y que el host 212.24.24.55 NO está en ninguna lista negra.


**Parte II.I Para discutir la próxima clase (NO para implementar aún)**

La estrategia de paralelismo antes implementada es ineficiente en ciertos casos, pues la búsqueda se sigue realizando aún cuando los N hilos (en su conjunto) ya hayan encontrado el número mínimo de ocurrencias requeridas para reportar al servidor como malicioso. Cómo se podría modificar la implementación para minimizar el número de consultas en estos casos?, qué elemento nuevo traería esto al problema?

---

**Parte III - Evaluación de Desempeño**

*A partir de lo anterior, implemente la siguiente secuencia de experimentos para realizar las validación de direcciones IP dispersas (por ejemplo 202.24.34.55), tomando los tiempos de ejecución de los mismos (asegúrese de hacerlos en la misma máquina):*

Se implementa en el método main ,de manera general, el siguiente código cambiando el número de hilos según se solicite:

<img width="958" height="213" alt="image" src="https://github.com/user-attachments/assets/c7981998-8c4a-4dd1-ae1e-f22c497b1e8c" />

1. Un solo hilo.
    Se obtienen los siguientes resultados:

    <img width="1166" height="199" alt="image" src="https://github.com/user-attachments/assets/c530ea3b-d9f3-420a-b009-e38d4de93828" />

    **Tiempo de ejecución en mm:ss.mmm:** 02:09.352


2. Tantos hilos como núcleos de procesamiento (haga que el programa determine esto haciendo uso del [API Runtime](https://docs.oracle.com/javase/7/docs/api/java/lang/Runtime.html)).
   Se obtienen los siguientes resultados:

   <img width="1167" height="194" alt="image" src="https://github.com/user-attachments/assets/d628b7e3-106c-46ea-9f73-6ff2db1ffd2d" />

   **Tiempo de ejecución en mm:ss.mmm:** 00:16.037


3. Tantos hilos como el doble de núcleos de procesamiento.
   Se obtienen los siguientes resultados:

   <img width="1169" height="206" alt="image" src="https://github.com/user-attachments/assets/6cf42e4e-1109-46f1-a4e8-6fd8ecc6ad2e" />

   **Tiempo de ejecución en mm:ss.mmm:** 00:07.951


4. 50 hilos.
   Se obtienen los siguientes resultados:

   <img width="1172" height="197" alt="image" src="https://github.com/user-attachments/assets/cb028526-66aa-41ee-9498-52263c0a0629" />

   **Tiempo de ejecución en mm:ss.mmm:** 00:02.465



5. 100 hilos.
   Se obtienen los siguientes resultados:

   <img width="1171" height="203" alt="image" src="https://github.com/user-attachments/assets/0e56c038-d6f3-4209-a6b3-53cd46a4031d" />

   **Tiempo de ejecución en mm:ss.mmm:** 00:01.195


<img width="1600" height="703" alt="image" src="https://github.com/user-attachments/assets/6596aec0-2f6a-4b23-9afd-a2dbe9ec1acc" />

En general el uso del CPU estuvo entre 0,1% y 2,3%, siendo los valores menores en los últimos experimentos. Usó de memoria aproximadamente 32 MB ya que pasó de 5 MB a 37 MB.


*Con lo anterior, y con los tiempos de ejecución dados, haga una gráfica de tiempo de solución vs. número de hilos. Analice y plantee hipótesis con su compañero para las siguientes preguntas (puede tener en cuenta lo reportado por jVisualVM):*


<img width="600" height="353" alt="image" src="https://github.com/user-attachments/assets/79a91e30-9108-43eb-a62d-4b41e683b6e6" />


---

**Parte IV - Ejercicio Black List Search**

*1. Según la [ley de Amdahls](https://www.pugetsystems.com/labs/articles/Estimating-CPU-Performance-using-Amdahls-Law-619/#WhatisAmdahlsLaw?):
	![](img/ahmdahls.png),
    donde _S(n)_ es el mejoramiento teórico del desempeño, _P_ la fracción paralelizable del algoritmo, y _n_ el número de hilos, a mayor _n_, mayor debería ser dicha mejora. ¿Por qué el mejor desempeño no se logra con los 500 hilos?, ¿Cómo se compara este desempeño cuando se usan 200?.*

El mejor desempeño no se logra con 500 hilos ya que de acuerdo con la ley de Amdahl el mejoramiento teórico del desempeño depende de la fracción paralelizable del algoritmo y
tal como se ve en la gráfica de comparación entre el número de hilos y el tiempo de ejecución, al ir aumentando el número de hilos este tiende hacia un mismo valo, además en la práctica
hay costos extra como la creación y el cambio de contexto entre hilos, la competencia por memoria y recursos compartidos. Es por esto que a mayor número de hilos la ganancia
adicional será cada vez menor, y por ende habrá mas ganancia con 200 hilos que con 500.

*2. ¿Cómo se comporta la solución usando tantos hilos de procesamiento como núcleos comparado con el resultado de usar el doble de éste?.*

Cuando se utilizan tantos hilos como núcleos la solución aprovecha de manera eficiente el hardware disponible, ya que cada hilo se puede ejecutar en un núcleo sin necesidad de tener que
competir con otros, esto reduce la sobrecarga y daría como resultado un desempeño cercano al óptimo. En cambio, al usar el doble de hilos que núcleos, los hilos tendrán que compartir los mismos
recursos físicos y se recurrirá al cambio de contexto, lo que limita la ganancia. El rendimiento no mejora de forma significativa e incluso puede llegar a ser inferior al caso inicial de un solo hilo por núcleo.

*3. De acuerdo con lo anterior, si para este problema en lugar de 100 hilos en una sola CPU se pudiera usar 1 hilo en cada una de 100 máquinas hipotéticas ¿La ley de Amdahls se aplicaría mejor?. Si en lugar de esto se usaran c hilos en 100/c máquinas distribuidas (siendo c es el número de núcleos de dichas máquinas) ¿Se mejoraría?. Explique su respuesta.*

Si en lugar de usar 100 hilos en una sola CPU se usara 1 hilo en cada una de 100 máquinas, en teoría la Ley de Amdahl se aplicaría mejor porque cada hilo correría en un núcleo físico
distinto las dificultades de manejar muchos hilos en una sola CPU.
Si se realizara en un sistema distribuido aparecería los costos de la comunicación y la coordinación entre máquinas, que actúa como parte secuencial y que puede reducir el beneficio y si
se usan c hilos en 100/c máquinas, se disminuye la comunicación entre nodos pero aumenta la competencia local por CPU y memoria, por lo que el desempeño dependeria del balance
entre el costo de coordinación y la contención de recursos.


