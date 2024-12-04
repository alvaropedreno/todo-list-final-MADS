# Aplicación inicial ToDoList

Aplicación ToDoList de la asignatura [MADS](https://cvnet.cpd.ua.es/Guia-Docente/GuiaDocente/Index?wlengua=es&wcodasi=34037&scaca=2024-25) usando Spring Boot y plantillas Thymeleaf.

## Enlace Trello

[https://trello.com/b/NgfNsLTa/todolist-mads](https://trello.com/invite/b/6750309ac3f1f3bbec48dd54/ATTIb8289daf3f97dbf0d76fff01102186452BECBD99/todolist-mads)

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
