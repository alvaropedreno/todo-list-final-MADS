# Documentación práctica 2 

La práctica incluye todas las funcionalidades, tanto obligatorias como opcionales.

## Nuevas clases y métodos implementados.

- authetication/ManageferUserSession
   - **boolean isAdmin()**: Devuelve true si el usuario logeado es administrador.

- UsuarioService
  - **List<UsuarioData> allUsuarios()**: Devuelve una lista con todos los usuarios registrados.
  - **boolean existeAdmin()**: Devuelve true en el caso de que exista un usuario registrado como administrador.
  - **UsuarioData toggleBloqueo(usuarioID)**: Cambia el estado de un usuario registrado entre bloqueado/desbloqueado. Cada vez que se llame a la funcion tomará el estado actual del usuario para cambiarlo (true/false)

- model/Usuario
  - Añadidos atributos `Boolean admin = false` y `Boolean bloqueado = false` junto a sus respectivos getters y setters.

- dto/UsuarioData
  - Añadidos atributos `Boolean admin = false` y `Boolean bloqueado = false` junto a sus respectivos getters y setters.

- dto/RegistroData
  - Añadido atributo `Boolean admin = false` junto a su respectivos getter y setter.

- controller/HomeController
  - @GetMapping("/about") modificado de manera que la barra de menu cambie si el usuario está logeado o no.

- controller/UsuarioController
  - **comprobarUsuarioAdmin()**: Devuelve una excepcion en caso de que el usuario logeado no sea administrador.
  - **listadoUsuarios(model)**: Devuelve la vista `Lista de usuarios (/registrados)` 
  - **detalleUsuario(id, model)**: Devuelve la vista `Detalle de usuario (/registrados/{id})` para el usuario elegido de la lista.
  - **toggleBloqueo(id)**: Cambia el estado del usuario elegido de la lista.

- controller/exception/UsuarioNoAdminException
  - Nueva excepcion para usuarios que no sean administradores.

## Plantillas thyemeleaf.

- **fragments**: Se ha añadido un fragmento con un navbar de bootstrap para que sea común a todas las vistas en las que se incorpore. Se le pasará un `usuarioLoggeado` que será el usuario que ha iniciado sesión, el cual tendrá enlace a su lista de tareas y un desplegable con acceso a su perfil (por desarrollar) y cerrar sesión. En caso de que no se haya iniciado sesión aparecerán enlaces a login y registro.

```
<div th:fragment="navbar (usuarioLoggeado)">
    <nav class="navbar navbar-expand-lg navbar-light" style="background-color: #e48aeb;">
        <a class="navbar-brand" href="/about">ToDoList</a>
        <button class="navbar-toggler" type="button" data-toggle="collapse" data-target="#navbarSupportedContent" aria-controls="navbarSupportedContent" aria-expanded="false" aria-label="Toggle navigation">
            <span class="navbar-toggler-icon"></span>
        </button>

        <div class="collapse navbar-collapse" id="navbarSupportedContent">
            <ul class="navbar-nav mr-auto">
                <li class="nav-item" th:if="${usuarioLoggeado}">
                    <a class="nav-link" th:href="@{/usuarios/{id}/tareas(id=${usuarioLoggeado.id})}">Tareas</a>
                </li>
            </ul>
            <ul class="navbar-nav ml-auto">
                <li class="nav-item dropdown" th:if="${usuarioLoggeado}">
                    <a class="nav-link dropdown-toggle" href="#" role="button" data-toggle="dropdown" aria-expanded="false">
                        [[${usuarioLoggeado.nombre}]]
                    </a>
                    <div class="dropdown-menu dropdown-menu-right">
                        <a class="dropdown-item" href="#">Cuenta</a>
                        <a class="dropdown-item" href="/logout">Cerrar sesion [[${usuarioLoggeado.nombre}]]</a>
                    </div>
                </li>
                <li class="nav-item" th:unless="${usuarioLoggeado}">
                    <a class="nav-link" href="/login">Login</a>
                </li>
                <li class="nav-item" th:unless="${usuarioLoggeado}">
                    <a class="nav-link" href="/registro">Registro</a>
                </li>
            </ul>
        </div>
    </nav>
</div>
```
- **formRegistro**: Se ha añadido una casilla para que un usuario pueda registrarse como administrador. En el momento que exista un administrador en el sistema esta casilla no estará disponible.

```
<div class="form-group form-check" th:if="${!hayAdmin}">
   <input type="checkbox" class="form-check-input" name="adminCheck" id="adminCheck"
         th:field="*{admin}"/>
   <label for="adminCheck">Administrador</label>
</div>
```

- **listaUsuarios**: Similar a listaTareas, muestra el id y email de todos los usuarios registrados. Cada usuario tendrá su botón para ver más detalles y cambiar su estado entre bloqueado y desbloqueado. Al tener una única función el botón cambiará de color y texto según el estado del usuario.
```
<td th:if="${!usuario.bloqueado}">
   <a class="btn btn-danger btn-xs" th:href="@{/toggleBloqueo/{id}(id=${usuario.id})}"/>Bloquear</a>
</td>
<td th:if="${usuario.bloqueado}">
   <a class="btn btn-success btn-xs" th:href="@{/toggleBloqueo/{id}(id=${usuario.id})}"/>Desbloquear</a>
</td>
```
- **detalleUsuario**: Muestra todos los datos de un usuario (id, nombre, email, fecha de nacimiento).

## Tests implementados.

- controller/AcercaDeWebTest
  - **listaUsuariosAdmin**: Añade 2 usuarios a la base de datos, siendo uno de estos administrador para poder acceder a la pagina protegida `/registrados`. El test comprueba que aparece el correo de ambos usuarios.
  - **getDetallesDevuelveDetalles**: Comprueba que al acceder a la pagina de detalles de un usuario se muestran todos sus datos. El usuario tiene que ser administrador ya que las paginas `/registrados` y `/registrador/{id}` estan protegidas.
  - **formularioApareceAdmin**: Comprueba que cuando no existe ningun administrador aparece la opcion en el registro.
  - **formularioNoApareceAdmin**: Comprueba que cuando existe ningun administrador no aparece la opcion en el registro.
  - **servicioLoginUsuarioAdminOK**: Comprueba que al iniciar sesión como administrador se redirige a la página `/registrados`
  - **servicioLoginUsuarioBlocked**: Comprueba que al intentar iniciar sesión con un usuario bloqueado aparece un mensaje de alerta.
  - **getNavbarUsuarioNoLoggeado**: Comprueba que el navbar muestra las opciones correctas para un usuario no registrado.
  - **getNavbarUsuarioLoggeado**: Comprueba que el navbar muestra las opciones correctas para un usuario registrado.
- service/UsuarioServiceTest
  - **servicioConsultaAdmin**: Comprueba que existe un usuario administrador tras su registro.