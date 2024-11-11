# Documentacion practica 3

La práctica incluye todas las funcionalidades, tanto obligatorias como opcionales.

## 1. Pantalla de la base de datos PostgreSQL

**Capturas de pantalla de bbdd**

## 2. Endpoints definidos para las acciones

### /equipos

Se muestra una lista con todos los equipos y aparece una opcion para ver los usuarios que componen ese grupo. Como administrador, se pueden modificar y borrar los equipos.

#### Clases y métodos

* EquipoService.java/findAllOrdenadoPorNombre(): Leemos todos los equipos de la base de datos ordenados por nombre y los devolvemos en forma de lista.
* EquipoController.java/listadoEquipos(): Llamamos a la funcion findAllOrdenadoPorNombre y devolvemos la plantilla rellenada con los datos de la lista.

#### Plantillas thymeleaf

* listaEquipos.html: En esta plantilla se muestra la lista de equipos y las opciones para editarlos, borrarlos y mostrar los usuarios que los componen. Además, antes de eliminar un equipo, se mostrará un mensaje para que el usuario confirme la opción.


#### Tests
