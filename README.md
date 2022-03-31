# GlobalReto

# Introducción
Es un app que permita al usuario consultar los lugares cercanos utilizando Yelp API. 
La primera pantalla mostrará un buscador donde el usuario podrá ingresar un parámetro de búsqueda (“pizza”, “tacos”, etc…), los resultados se mostrarán en una lista.
Al dar click en algún ítem de los resultados, se abre una segunda pantalla de detalles.

# Consideraciones
La versión mínima de Android es la 19 y la máxima es la 32 (la última disponible)
Contiene el archivo google_maps_api.xml con la llave para la API de Google Maps.
Y en la clase de Networking la llave para la API de Yelp.

# Indicaciones
En la primera pantalla se muestra un buscador que cuando el usuario empieza a ingresar un parámetro, se muestra una segunda pantalla para mostrar sugerencias de búsqueda (estas se proporcionan a través de la API de Yelp). Cuando el usuario selecciona una sugerencia ésta se introduce al buscador automáticamente, actualizando así las sugerencias.

Cuando el usuario presiona el botón de buscar (en el teclado del celular), estando en el buscador, se regresará a la primera pantalla, pero ahora con una lista con los resultados obtenidos de la búsqueda. Al seleccionar un resultado de la lista se mostrará una tercera pantalla, en ésta se muestran más datos a detalle del resultado, junto a un mini mapa. El mapa muestra con un marcador el lugar del resultado seleccionado, y puede ver también la posición actual del usuario con otro marcador.

Si se está en la primera pantalla, puede presionar el botón de la parte inferior derecha, para cambiar al modo mapa. En el modo mapa los resultados de las búsquedas muestran como marcadores en el mapa en lugar de una lista. Al seleccionar un marcador se muestra una etiqueta con el nombre del lugar. Si se selecciona la etiqueta, se cambiara a la tercera pantalla, que es la pantalla de detalles.


# Comentarios
Debido a que personalmente no tenia conocimientos previo de View Binding, Data Binding y del uso de Navigation Component, el desarrollo de la app fue más tardado de lo normal, pero cumpliendo con la fecha limite de entrega.
A pesar de esto, si se implemento la arquitectura de MVVM y el uso de Navigation Component junto a Safe Args
