package madstodolist.controller;

import madstodolist.authentication.ManagerUserSession;
import madstodolist.dto.EquipoData;
import madstodolist.dto.UsuarioData;
import madstodolist.service.EquipoService;
import madstodolist.service.UsuarioService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.RequestBuilder;

import java.util.Arrays;
import java.util.List;

import static org.hamcrest.Matchers.*;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
public class EquiposWebTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired @MockBean
    private EquipoService equipoService;

    @Autowired @MockBean
    private UsuarioService usuarioService;

    @Autowired @MockBean
    private ManagerUserSession managerUserSession;

    @Test
    public void testMostrarEquipos() throws Exception {
        EquipoData equipo1 = new EquipoData();
        equipo1.setId(1L);
        equipo1.setNombre("Equipo 1");
        EquipoData equipo2 = new EquipoData();
        equipo2.setId(2L);
        equipo2.setNombre("Equipo 2");

        UsuarioData anaGarcia = new UsuarioData();
        anaGarcia.setNombre("Ana García");
        anaGarcia.setId(1L);
        anaGarcia.setAdmin(false);

        when(equipoService.findAllOrdenadoPorNombre()).thenReturn(Arrays.asList(equipo1, equipo2));
        when(managerUserSession.usuarioLogeado()).thenReturn(1L);
        when(usuarioService.findById(1L)).thenReturn(anaGarcia);
        when(managerUserSession.isAdmin()).thenReturn(false);

        this.mockMvc.perform(get("/equipos"))
                .andExpect(content().string(allOf(
                    containsString("Equipo 1"),
                    containsString("Equipo 2")
                )));
    }

    @Test
    public void testMostrarOpcionEquipo() throws Exception {
        // GIVEN
        // Creamos el objeto del usuario loggeado
        UsuarioData anaGarcia = new UsuarioData();
        anaGarcia.setNombre("Ana García");
        anaGarcia.setId(1L);

        // Simulamos que el usuario está loggeado
        when(managerUserSession.usuarioLogeado()).thenReturn(anaGarcia.getId());

        // Simulamos la búsqueda del usuario loggeado
        when(usuarioService.findById(anaGarcia.getId())).thenReturn(anaGarcia);

        // WHEN, THEN
        // Realizamos una petición GET a la página /about y comprobamos que aparece el nombre del usuario loggeado
        this.mockMvc.perform(get("/about"))
                .andExpect(status().isOk())
                .andExpect(content().string(allOf(
                        containsString("Tareas"),    // Aseguramos que aparece el enlace de tareas
                        containsString("Equipos"),   // Aseguramos que aparece el enlace de equipos
                        containsString("Ana García") // Aseguramos que aparece el nombre del usuario
                )));
    }

    @Test
    public void testGetMiembrosEquipo() throws Exception {
        EquipoData equipo1 = new EquipoData();
        equipo1.setId(1L);
        equipo1.setNombre("Equipo 1");

        UsuarioData anaGarcia = new UsuarioData();
        anaGarcia.setNombre("Ana García");
        anaGarcia.setEmail("ana@ua.es");
        anaGarcia.setId(1L);

        when(equipoService.recuperarEquipo(1L)).thenReturn(equipo1);
        when(equipoService.usuariosEquipo(1L)).thenReturn(Arrays.asList(anaGarcia));
        when(managerUserSession.usuarioLogeado()).thenReturn(1L);
        when(usuarioService.findById(1L)).thenReturn(anaGarcia);

        this.mockMvc.perform(get("/equipos/1/usuarios"))
                .andExpect(content().string(allOf(
                    containsString("Equipo 1"),
                    containsString("ana@ua.es")
                )));



    }

    @Test
    public void testAddUsuarioEquipo() throws Exception {
        EquipoData equipo1 = new EquipoData();
        equipo1.setId(1L);
        equipo1.setNombre("Equipo 1");

        UsuarioData anaGarcia = new UsuarioData();
        anaGarcia.setNombre("Ana García");
        anaGarcia.setEmail("ana@ua.es");
        anaGarcia.setId(1L);

        when(equipoService.recuperarEquipo(1L)).thenReturn(equipo1);
        when(equipoService.usuariosEquipo(1L)).thenReturn(Arrays.asList(anaGarcia));
        when(managerUserSession.usuarioLogeado()).thenReturn(1L);
        when(usuarioService.findById(1L)).thenReturn(anaGarcia);

        this.mockMvc.perform(get("/equipos/1/addUsuario/2"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/equipos/1/usuarios"));

        this.mockMvc.perform(get("/equipos/1/usuarios"))
                .andExpect(content().string(allOf(
                        containsString("ana@ua.es")
                )));



    }

    @Test
    public void testRemoveUsuarioEquipo() throws Exception {
        EquipoData equipo1 = new EquipoData();
        equipo1.setId(1L);
        equipo1.setNombre("Equipo 1");

        UsuarioData anaGarcia = new UsuarioData();
        anaGarcia.setNombre("Ana García");
        anaGarcia.setEmail("ana@ua.es");
        anaGarcia.setId(1L);

        when(equipoService.recuperarEquipo(1L)).thenReturn(equipo1);
        when(equipoService.usuariosEquipo(1L)).thenReturn(Arrays.asList(anaGarcia));
        when(managerUserSession.usuarioLogeado()).thenReturn(1L);
        when(usuarioService.findById(1L)).thenReturn(anaGarcia);

        this.mockMvc.perform(get("/equipos/1/addUsuario/1"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/equipos/1/usuarios"));

        when(equipoService.usuariosEquipo(1L)).thenReturn(Arrays.asList());

        this.mockMvc.perform(get("/equipos/1/deleteUsuario/1"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/equipos/1/usuarios"));

        this.mockMvc.perform(get("/equipos/1/usuarios"))
                .andExpect(content().string(not(containsString("ana@ua.es"))));


    }

    @Test
    public void testMostrarBotonAñadirUsuarioEquipo() throws Exception {
        EquipoData equipo1 = new EquipoData();
        equipo1.setId(1L);
        equipo1.setNombre("Equipo 1");

        UsuarioData anaGarcia = new UsuarioData();
        anaGarcia.setNombre("Ana García");
        anaGarcia.setEmail("ana@ua.es");
        anaGarcia.setId(1L);

        when(equipoService.recuperarEquipo(1L)).thenReturn(equipo1);
        when(managerUserSession.usuarioLogeado()).thenReturn(1L);
        when(usuarioService.findById(1L)).thenReturn(anaGarcia);

        this.mockMvc.perform(get("/equipos/1/usuarios"))
                .andExpect(content().string(allOf(
                        containsString("Añadirme")
                )));
    }

    @Test
    public void testMostrarBotonEliminarUsuarioEquipo() throws Exception {
        EquipoData equipo1 = new EquipoData();
        equipo1.setId(1L);
        equipo1.setNombre("Equipo 1");

        UsuarioData anaGarcia = new UsuarioData();
        anaGarcia.setNombre("Ana García");
        anaGarcia.setEmail("ana@ua.es");
        anaGarcia.setId(1L);

        when(equipoService.recuperarEquipo(1L)).thenReturn(equipo1);
        when(equipoService.usuariosEquipo(1L)).thenReturn(Arrays.asList(anaGarcia));
        when(managerUserSession.usuarioLogeado()).thenReturn(1L);
        when(usuarioService.findById(1L)).thenReturn(anaGarcia);

        this.mockMvc.perform(get("/equipos/1/addUsuario/2"));

        this.mockMvc.perform(get("/equipos/1/usuarios"))
                .andExpect(content().string(allOf(
                        containsString("Eliminarme")
                )));
    }

    @Test
    public void testMostrarFormNuevoEquipo() throws Exception {
        UsuarioData anaGarcia = new UsuarioData();
        anaGarcia.setNombre("Ana García");
        anaGarcia.setId(1L);

        when(managerUserSession.usuarioLogeado()).thenReturn(1L);
        when(usuarioService.findById(1L)).thenReturn(anaGarcia);

        this.mockMvc.perform(get("/equipos/nuevo"))
                .andExpect(content().string(
                    containsString("Nuevo equipo")
                ));
    }

    @Test
    public void testCrearEquipo() throws Exception {
        UsuarioData anaGarcia = new UsuarioData();
        anaGarcia.setNombre("Ana García");
        anaGarcia.setId(1L);

        EquipoData equipo1 = new EquipoData();
        equipo1.setId(1L);
        equipo1.setNombre("Equipo 1");


        when(managerUserSession.usuarioLogeado()).thenReturn(1L);
        when(usuarioService.findById(1L)).thenReturn(anaGarcia);

        RequestBuilder request = post("/equipos/nuevo")
                .param("nombre", "Equipo 1");

        this.mockMvc.perform(request)
                .andExpect(redirectedUrl("/equipos"));

        when(equipoService.findAllOrdenadoPorNombre()).thenReturn(Arrays.asList(equipo1));

        this.mockMvc.perform(get("/equipos"))
                .andExpect(content().string(
                    containsString("Equipo 1")
                ));
    }

    @Test
    public void testNoAdminNoEditaEquipo() throws Exception {
        UsuarioData anaGarcia = new UsuarioData();
        anaGarcia.setNombre("Ana García");
        anaGarcia.setId(1L);
        anaGarcia.setAdmin(false);

        EquipoData equipo1 = new EquipoData();
        equipo1.setId(1L);
        equipo1.setNombre("Equipo 1");

        when(managerUserSession.usuarioLogeado()).thenReturn(1L);
        when(usuarioService.findById(1L)).thenReturn(anaGarcia);
        when(equipoService.recuperarEquipo(1L)).thenReturn(equipo1);

        this.mockMvc.perform(get("/equipos/1/editar"))
                .andExpect(content().string(
                    not(containsString("Editar"))
                ));
    }

    @Test
    public void testEditarNombreEquipo() throws Exception {
        UsuarioData anaGarcia = new UsuarioData();
        anaGarcia.setNombre("Ana García");
        anaGarcia.setId(1L);
        anaGarcia.setAdmin(true);

        EquipoData equipo1 = new EquipoData();
        equipo1.setId(1L);
        equipo1.setNombre("Equipo 1");

        when(managerUserSession.usuarioLogeado()).thenReturn(anaGarcia.getId());
        when(usuarioService.findById(1L)).thenReturn(anaGarcia);
        when(equipoService.recuperarEquipo(1L)).thenReturn(equipo1);
        when(managerUserSession.isAdmin()).thenReturn(true);

        this.mockMvc.perform(post("/equipos/1/editar")
                .param("nombre", "Equipo 2"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/equipos"));

        equipo1.setNombre("Equipo 2");
        when(equipoService.findAllOrdenadoPorNombre()).thenReturn(Arrays.asList(equipo1));

        this.mockMvc.perform(get("/equipos"))
                .andExpect(content().string(
                    containsString("Equipo 2")
                ));
    }

    @Test
    public void testNoAdminNoEliminaEquipo() throws Exception {
        UsuarioData anaGarcia = new UsuarioData();
        anaGarcia.setNombre("Ana García");
        anaGarcia.setId(1L);
        anaGarcia.setAdmin(false);

        EquipoData equipo1 = new EquipoData();
        equipo1.setId(1L);
        equipo1.setNombre("Equipo 1");

        when(managerUserSession.usuarioLogeado()).thenReturn(1L);
        when(usuarioService.findById(1L)).thenReturn(anaGarcia);
        when(equipoService.recuperarEquipo(1L)).thenReturn(equipo1);

        this.mockMvc.perform(get("/equipos/1/editar"))
                .andExpect(content().string(
                        not(containsString("Eliminar"))
                ));
    }

    @Test
    public void testEliminarEquipo() throws Exception {
        UsuarioData anaGarcia = new UsuarioData();
        anaGarcia.setNombre("Ana García");
        anaGarcia.setId(1L);
        anaGarcia.setAdmin(true);

        EquipoData equipo1 = new EquipoData();
        equipo1.setId(1L);
        equipo1.setNombre("Equipo 1");

        when(managerUserSession.usuarioLogeado()).thenReturn(anaGarcia.getId());
        when(usuarioService.findById(1L)).thenReturn(anaGarcia);
        when(equipoService.recuperarEquipo(1L)).thenReturn(equipo1);
        when(managerUserSession.isAdmin()).thenReturn(true);

        this.mockMvc.perform(delete("/equipos/1"));

        this.mockMvc.perform(get("/equipos"))
                .andExpect(content().string(
                        not(containsString("Equipo 1"))));
    }

}
