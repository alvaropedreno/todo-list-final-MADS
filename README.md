> ⚠️ **Nota importante:**
> 
> Este reopositorio es una copia pública del original, que anteriormente era privado y contenía información adicional como tableros de *Projects* (Kanban), *Issues* y *Pull Requests*.
> 
> Debido al cambio de visibilidad, parte de ese contenido colaborativo se ha perdido y no se ve reflejado en esta versión.
> 
> Sin embargo, el código fuente y la estructura del proyecto permanecen intactos.

# Aplicación inicial ToDoList

Aplicación ToDoList de la asignatura [MADS](https://cvnet.cpd.ua.es/Guia-Docente/GuiaDocente/Index?wlengua=es&wcodasi=34037&scaca=2024-25) usando Spring Boot y plantillas Thymeleaf.

## Enlace Trello

https://trello.com/b/X5CwclxH/todolist-mads

## Enlace Dock

https://hub.docker.com/repository/docker/mersuarezc/mads-todolist/general




## Requisitos

Necesitas tener instalado en tu sistema:

- Java 8

## Ejecución

Puedes ejecutar la aplicación usando el _goal_ `run` del _plugin_ Maven 
de Spring Boot:

```
$ ./mvnw spring-boot:run 
```   

También puedes generar un `jar` y ejecutarlo:

```
$ ./mvnw package
$ java -jar target/mads-todolist-inicial-0.0.1-SNAPSHOT.jar 
```

Una vez lanzada la aplicación puedes abrir un navegador y probar la página de inicio:

- [http://localhost:8080/login](http://localhost:8080/login)
