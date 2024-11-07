package madstodolist.controller;

import madstodolist.authentication.ManagerUserSession;
import madstodolist.dto.EquipoData;
import madstodolist.service.EquipoService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.RequestBuilder;

import java.util.Arrays;
import java.util.List;

import static org.hamcrest.Matchers.allOf;
import static org.mockito.Mockito.when;
import static org.hamcrest.Matchers.containsString;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
public class EquiposWebTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired @MockBean
    private EquipoService equipoService;

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

        when(equipoService.findAllOrdenadoPorNombre()).thenReturn(Arrays.asList(equipo1, equipo2));
        when(managerUserSession.usuarioLogeado()).thenReturn(1L);

        this.mockMvc.perform(get("/equipos"))
                .andExpect(content().string(allOf(
                    containsString("Equipo 1"),
                    containsString("Equipo 2")
                )));
    }

    // boton en navbar
}
