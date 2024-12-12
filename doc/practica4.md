# Documentación Técnica - Práctica 4

Autores:
- Victoria Morquio
- Alvaro Pedreño Rubio
- Mercedes Suarez Canga

## 1. Cambios introducidos en la aplicación

### 1.1 Campo descripción - Back-end

Se ha añadido el campo descripción a la entidad Tarea como parte del desarrollo del back-end. Este cambio permite almacenar una descripción detallada para cada tarea en la base de datos.

- *Cambios en el modelo*:  
  Se añadió un campo descripcion con tipo String en la clase Tarea.

- *Cambios en el servicio*:  
  Se actualizó el servicio para manejar el nuevo campo al crear o actualizar una tarea.
---

### 1.2 Campo descripción - Front-end

Posteriormente, se añadió soporte para el campo descripción en la interfaz de usuario, lo que permite que los usuarios puedan visualizar y editar la descripción de las tareas.
- *Cambios en el controlador*:  
  Los controladores ahora aceptan y procesan el campo descripcion en las operaciones correspondientes, asegurando que la API soporte este nuevo atributo.

- *Cambios en la vista*:  
  Se actualizó la interfaz de usuario para mostrar el campo descripcion al listar las tareas. Además, se añadió un formulario o componente para permitir a los usuarios editar la descripción de una tarea existente.

---

### 1.3 Funcionalidad de modificar descripción de las tareas

Se añadió la funcionalidad para permitir la modificación del campo descripción de las tareas. Esto facilita la actualización de información detallada en caso de cambios o ajustes necesarios.

- *Cambios en el servicio*:  
  Se añadió un método en la capa de servicio para actualizar la descripción de una tarea. Este método recibe el ID de la tarea y la nueva descripción, y persiste los cambios en la base de datos.

- *Cambios en el controlador*:  
  Se añadió un nuevo endpoint en el controlador que permite a los clientes enviar una solicitud para modificar la descripción de una tarea. Este endpoint utiliza el método PATCH y requiere el ID de la tarea y la nueva descripción como parámetros.

- *Cambios en la vista*:  
  Se agregó un formulario de edición en la interfaz de usuario que permite a los usuarios modificar la descripción de una tarea existente. Una vez realizada la modificación, el formulario envía la solicitud al backend.

---

## 2. Detalles del despliegue de producción

Para comprobar las diferencias entre los esquemas de datos ejecutamos el siguiente comando:

    diff sql/schema-1.3.0.sql sql/schema-1.2.0.sql

y creamos el script de migracion `schema-1.2.0-1.3.0.sql` que contiene los cambios de una version a otra:

```sql
ALTER TABLE public.tareas
ADD COLUMN descripcion character varying(255)
```

## 3. Enlace docker

Enlace al docker con la etiqueta 1.3.0

https://hub.docker.com/layers/mersuarezc/todolist-equipo-13/1.3.0/images/sha256-07e0a9b3a8fc888e3ce910ce6ee362102f07c9bb5e84dcaf5bc035dcc9f8fe63?context=explore

