package com.mx.cbtis.controller;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.model;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrl;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.MockedConstruction;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.mx.cbtis.modelo.Comentarios;
import com.mx.cbtis.modelo.Proyectos;
import com.mx.cbtis.modelo.Tareas;
import com.mx.cbtis.modelo.Usuario;

import java.util.Map;

@SpringBootTest
@AutoConfigureMockMvc
public class GestorControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private GestorController gestorController;

    private MockedConstruction<RestTemplate> mockedRestTemplate;

    @BeforeEach
    public void setUp() {
        mockedRestTemplate = Mockito.mockConstruction(RestTemplate.class, (mock, context) -> {
            ResponseEntity<Integer> successResponseInt = new ResponseEntity<>(1, HttpStatus.OK);

            // POST /addUsuario
            when(mock.postForEntity(eq("http://localhost:8081/addUsuario"), any(), eq(Integer.class)))
                    .thenReturn(successResponseInt);

            // POST /addProyecto
            when(mock.postForEntity(eq("http://localhost:8081/addProyecto"), any(), eq(Integer.class)))
                    .thenReturn(successResponseInt);

            // POST /buscarProyectos
            Proyectos p1 = new Proyectos();
            p1.setId_proyecto(1);
            p1.setNombre("Proyecto Test");
            p1.setDescripcion("Desc");
            p1.setFecha_inicio("2026-01-01");
            p1.setFecha_finalizacion("2026-12-31");
            p1.setEstado_proyecto("ACTIVO");
            p1.setFondo("#ffffff");
            when(mock.postForObject(eq("http://localhost:8081/buscarProyectos"), any(), eq(Proyectos[].class)))
                    .thenReturn(new Proyectos[] { p1 });

            // GET /buscarProyecto
            when(mock.getForObject(eq("http://localhost:8081/buscarProyecto?id_proyecto={id_proyecto}"),
                    eq(Proyectos.class), any(Map.class)))
                    .thenReturn(p1);

            // DELETE /deleteProyecto
            when(mock.exchange(eq("http://localhost:8081/deleteProyecto"), eq(HttpMethod.DELETE), any(HttpEntity.class), eq(Integer.class)))
                    .thenReturn(successResponseInt);

            // POST /getUsuarioLogin
            Usuario u = new Usuario();
            u.setId_usuario(1);
            u.setNombre("TestUser");
            u.setCorreo_electronico("test@mail.com");
            u.setContrasena("pass");
            u.setRol("ADMIN");
            when(mock.postForEntity(eq("http://localhost:8081/getUsuarioLogin"), any(), eq(Usuario.class)))
                    .thenReturn(new ResponseEntity<>(u, HttpStatus.OK));

            // POST /buscarUsuarios
            when(mock.postForObject(eq("http://localhost:8081/buscarUsuarios"), any(), eq(Usuario[].class)))
                    .thenReturn(new Usuario[] { u });

            // POST /getTarea
            Tareas t1 = new Tareas();
            t1.setId_tarea(10);
            t1.setId_proyecto(1);
            t1.setNombre("Tarea 1");
            t1.setDescripcion("Desc");
            t1.setFecha_vencimiento("2026-06-01");
            t1.setLista("Pendiente");
            t1.setEstado("PENDIENTE");
            when(mock.postForObject(eq("http://localhost:8081/getTarea"), any(), eq(Tareas[].class)))
                    .thenReturn(new Tareas[] { t1 });

            // POST /buscarTareas
            when(mock.postForObject(eq("http://localhost:8081/buscarTareas"), any(), eq(Tareas[].class)))
                    .thenReturn(new Tareas[] { t1 });

            // GET /buscarTarea
            when(mock.getForObject(eq("http://localhost:8081/buscarTarea?id_tarea={id_tarea}"),
                    eq(Tareas.class), any(Map.class)))
                    .thenReturn(t1);

            // POST /addTareas
            when(mock.postForEntity(eq("http://localhost:8081/addTareas"), any(), eq(Integer.class)))
                    .thenReturn(successResponseInt);

            // DELETE /deleteTareas
            when(mock.exchange(eq("http://localhost:8081/deleteTareas"), eq(HttpMethod.DELETE), any(HttpEntity.class), eq(Integer.class)))
                    .thenReturn(successResponseInt);

            // POST /getComentario
            Comentarios c1 = new Comentarios(50, "1", 10, "Comentario 1", "2026-04-01");
            when(mock.postForObject(eq("http://localhost:8081/getComentario"), any(), eq(Comentarios[].class)))
                    .thenReturn(new Comentarios[] { c1 });

            // POST /buscarComentarios
            when(mock.postForObject(eq("http://localhost:8081/buscarComentarios"), any(), eq(Comentarios[].class)))
                    .thenReturn(new Comentarios[] { c1 });

            // POST /addComentario
            when(mock.postForEntity(eq("http://localhost:8081/addComentario"), any(), eq(Integer.class)))
                    .thenReturn(successResponseInt);
        });
    }

    @AfterEach
    public void tearDown() {
        if (mockedRestTemplate != null) {
            mockedRestTemplate.close();
        }
    }

    // ==========================================
    // VISTAS GET
    // ==========================================

    @Test
    public void testGetHomeView() throws Exception {
        mockMvc.perform(get("/"))
                .andExpect(status().isOk())
                .andExpect(view().name("home"));
    }

    @Test
    public void testGetTutorialView() throws Exception {
        mockMvc.perform(get("/tutorial"))
                .andExpect(status().isOk())
                .andExpect(view().name("tutorial"));
    }

    @Test
    public void testGetManualView() throws Exception {
        mockMvc.perform(get("/manual"))
                .andExpect(status().isOk())
                .andExpect(view().name("manual"));
    }

    @Test
    public void testGetLoginView() throws Exception {
        mockMvc.perform(get("/login"))
                .andExpect(status().isOk())
                .andExpect(view().name("login"));
    }

    @Test
    public void testGetRegisterView() throws Exception {
        mockMvc.perform(get("/register"))
                .andExpect(status().isOk())
                .andExpect(view().name("register"))
                .andExpect(model().attributeExists("usuario"));
    }

    @Test
    public void testGetInicioView() throws Exception {
        mockMvc.perform(get("/inicio"))
                .andExpect(status().isOk())
                .andExpect(view().name("inicio"))
                .andExpect(model().attributeExists("proyectos"));
    }

    @Test
    public void testGetProyectoView() throws Exception {
        mockMvc.perform(get("/proyecto"))
                .andExpect(status().isOk())
                .andExpect(view().name("proyecto"))
                .andExpect(model().attributeExists("tarea"))
                .andExpect(model().attributeExists("comentarios"))
                .andExpect(model().attributeExists("listaUsuarios"));
    }

    @Test
    public void testGetTareaView() throws Exception {
        org.springframework.ui.Model model = new org.springframework.ui.ConcurrentModel();
        String view = gestorController.getTarea(model);
        org.junit.jupiter.api.Assertions.assertEquals("proyecto", view);
        org.junit.jupiter.api.Assertions.assertTrue(model.containsAttribute("tareas"));
    }

    // ==========================================
    // CASOS DE ÉXITO (POST / PROYECTOS / USUARIOS / TAREAS)
    // ==========================================

    @Test
    public void testSaveUsuario_Success() throws Exception {
        mockMvc.perform(post("/saveUsuario")
                .param("nombre", "Juan")
                .param("contrasena", "12345")
                .param("correo_electronico", "juan@test.com")
                .contentType(MediaType.APPLICATION_FORM_URLENCODED))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/login"));
    }

    @Test
    public void testSaveProyecto_Success() throws Exception {
        mockMvc.perform(post("/saveProyecto")
                .param("nombre", "Nuevo Proyecto")
                .contentType(MediaType.APPLICATION_FORM_URLENCODED))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/inicio"));
    }

    @Test
    public void testConsultarProyectos_Success() throws Exception {
        mockMvc.perform(post("/consultarProyectos"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].nombre").value("Proyecto Test"));
    }

    @Test
    public void testEliminarProyecto_Success() throws Exception {
        Proyectos p = new Proyectos();
        p.setId_proyecto(1);

        mockMvc.perform(post("/eliminarProyecto")
                .content(objectMapper.writeValueAsString(p))
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/inicio"));
    }

    @Test
    public void testActualizarProyecto_Success() throws Exception {
        Proyectos p = new Proyectos();
        p.setId_proyecto(1);

        mockMvc.perform(post("/actualizarProyecto")
                .content(objectMapper.writeValueAsString(p))
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id_proyecto").value(1));
    }

    @Test
    public void testVerificarLogin_Success() throws Exception {
        Usuario u = new Usuario();
        u.setCorreo_electronico("test@mail.com");
        u.setContrasena("pass");

        mockMvc.perform(post("/verificarLogin")
                .content(objectMapper.writeValueAsString(u))
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.nombre").value("TestUser"));
    }

    @Test
    public void testConsultarUsuarios_Success() throws Exception {
        mockMvc.perform(post("/consultarUsuarios"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].nombre").value("TestUser"));
    }

    @Test
    public void testObtenerTareas_Success() throws Exception {
        Tareas t = new Tareas();
        t.setId_proyecto(1);

        mockMvc.perform(post("/obtenerTareas")
                .content(objectMapper.writeValueAsString(t))
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].nombre").value("Tarea 1"));
    }

    @Test
    public void testObtenerComentarios_Success() throws Exception {
        Comentarios c = new Comentarios();
        c.setId_tarea(10);

        mockMvc.perform(post("/obtenerComentarios")
                .content(objectMapper.writeValueAsString(c))
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].texto_comentario").value("Comentario 1"));
    }

    @Test
    public void testSaveTarea_Success() throws Exception {
        mockMvc.perform(post("/saveTarea")
                .param("nombre_tarea", "Nueva Tarea")
                .param("id_proyecto", "1")
                .contentType(MediaType.APPLICATION_FORM_URLENCODED))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/proyecto?id_proyecto=1"));
    }

    @Test
    public void testSaveComentario_Success() throws Exception {
        Comentarios c = new Comentarios(1, "1", 10, "Nuevo Comentario", "2026-04-12");
        String view = gestorController.setSaveComentario(c);
        org.junit.jupiter.api.Assertions.assertEquals("proyecto", view);
    }

    @Test
    public void testActualizarTarea_Success() throws Exception {
        Tareas t = new Tareas();
        t.setId_tarea(10);

        mockMvc.perform(post("/actualizarTarea")
                .content(objectMapper.writeValueAsString(t))
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id_tarea").value(10));
    }

    @Test
    public void testEliminarTarea_Success() throws Exception {
        Tareas t = new Tareas();
        t.setId_tarea(10);

        mockMvc.perform(post("/eliminarTarea")
                .content(objectMapper.writeValueAsString(t))
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/proyecto"));
    }

    @Test
    public void testVerificarTarea_Success() throws Exception {
        Tareas t = new Tareas();
        t.setId_tarea(10);

        mockMvc.perform(post("/verificarTarea")
                .content(objectMapper.writeValueAsString(t))
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }

    @Test
    public void testCambiosTareas_Success() throws Exception {
        Tareas t = new Tareas();
        t.setId_tarea(10);

        mockMvc.perform(post("/cambiosTareas")
                .content(objectMapper.writeValueAsString(t))
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }

    @Test
    public void testConsultarTareas_Success() throws Exception {
        mockMvc.perform(post("/consultarTareas"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].nombre").value("Tarea 1"));
    }

    @Test
    public void testConsultarComentarios_Success() throws Exception {
        mockMvc.perform(post("/consultarComentarios"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].texto_comentario").value("Comentario 1"));
    }

    // ==========================================
    // CASOS DE FRACASO Y EXCEPCIONES
    // ==========================================

    @Test
    public void testGetRutaInexistente_Failure() throws Exception {
        mockMvc.perform(get("/rutaQueNoExiste"))
                .andExpect(status().isNotFound());
    }

    @Test
    public void testConsultarUsuarios_BackendError() {
        mockedRestTemplate.close();
        mockedRestTemplate = Mockito.mockConstruction(RestTemplate.class, (mock, context) -> {
            when(mock.postForObject(eq("http://localhost:8081/buscarUsuarios"), any(HttpHeaders.class),
                    eq(Usuario[].class)))
                    .thenThrow(new HttpServerErrorException(HttpStatus.INTERNAL_SERVER_ERROR));
        });

        try {
            mockMvc.perform(post("/consultarUsuarios"));
        } catch (Exception e) {
            // Error esperado al fallar el servicio backend
        }
    }

    @Test
    public void testSaveUsuario_BackendError() {
        mockedRestTemplate.close();
        mockedRestTemplate = Mockito.mockConstruction(RestTemplate.class, (mock, context) -> {
            when(mock.postForEntity(eq("http://localhost:8081/addUsuario"), any(), eq(Integer.class)))
                    .thenThrow(new RestClientException("Conexión rechazada"));
        });

        try {
            mockMvc.perform(post("/saveUsuario")
                    .param("nombre", "Juan")
                    .contentType(MediaType.APPLICATION_FORM_URLENCODED));
        } catch (Exception e) {
            // Error esperado por fallo de red
        }
    }

    @Test
    public void testEliminarProyecto_NotFound() {
        mockedRestTemplate.close();
        mockedRestTemplate = Mockito.mockConstruction(RestTemplate.class, (mock, context) -> {
            when(mock.exchange(eq("http://localhost:8081/deleteProyecto"), eq(HttpMethod.DELETE), any(), eq(Integer.class)))
                    .thenThrow(new HttpServerErrorException(HttpStatus.NOT_FOUND));
        });

        Proyectos p = new Proyectos();
        p.setId_proyecto(999);

        try {
            mockMvc.perform(post("/eliminarProyecto")
                    .content(objectMapper.writeValueAsString(p))
                    .contentType(MediaType.APPLICATION_JSON));
        } catch (Exception e) {
            // Error esperado por 404
        }
    }

    @Test
    public void testVerificarLogin_InvalidData() {
        mockedRestTemplate.close();
        mockedRestTemplate = Mockito.mockConstruction(RestTemplate.class, (mock, context) -> {
            when(mock.postForEntity(eq("http://localhost:8081/getUsuarioLogin"), any(), eq(Usuario.class)))
                    .thenThrow(new HttpServerErrorException(HttpStatus.BAD_REQUEST));
        });

        Usuario u = new Usuario();
        u.setNombre("Incorrecto");
        u.setContrasena("Mal");

        try {
            mockMvc.perform(post("/verificarLogin")
                    .content(objectMapper.writeValueAsString(u))
                    .contentType(MediaType.APPLICATION_JSON));
        } catch (Exception e) {
            // Error esperado
        }
    }

    @Test
    public void testEliminarTarea_BackendTimeout() {
        mockedRestTemplate.close();
        mockedRestTemplate = Mockito.mockConstruction(RestTemplate.class, (mock, context) -> {
            when(mock.exchange(eq("http://localhost:8081/deleteTareas"), eq(HttpMethod.DELETE), any(), eq(Integer.class)))
                    .thenThrow(new RestClientException("Read Timeout"));
        });

        try {
            mockMvc.perform(post("/eliminarTarea")
                    .content("{\"id_tarea\": 50}")
                    .contentType(MediaType.APPLICATION_JSON));
        } catch (Exception e) {
            // Error esperado por timeout
        }
    }

    @Test
    public void testConsultarComentarios_MalformedResponse() {
        mockedRestTemplate.close();
        mockedRestTemplate = Mockito.mockConstruction(RestTemplate.class, (mock, context) -> {
            when(mock.postForObject(eq("http://localhost:8081/buscarComentarios"), any(HttpHeaders.class),
                    eq(Comentarios[].class)))
                    .thenThrow(new HttpServerErrorException(HttpStatus.BAD_GATEWAY));
        });

        try {
            mockMvc.perform(post("/consultarComentarios"));
        } catch (Exception e) {
            // Error esperado
        }
    }
}
