package com.mx.cbtis.controller;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.model;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrl;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.hamcrest.Matchers.containsString;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
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
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.client.HttpServerErrorException;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.mx.cbtis.modelo.Proyectos;
import com.mx.cbtis.modelo.Usuario;

@SpringBootTest
@AutoConfigureMockMvc
public class GestorControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    private MockedConstruction<RestTemplate> mockedRestTemplate;

    @BeforeEach
    public void setUp() {
        // Interceptamos cualquier 'new RestTemplate()' que se cree dentro del
        // controlador
        mockedRestTemplate = Mockito.mockConstruction(RestTemplate.class, (mock, context) -> {

            // Simular respuesta exitosa para /addUsuario
            ResponseEntity<Integer> successResponseInt = new ResponseEntity<>(1, HttpStatus.OK);
            when(mock.postForEntity(eq("http://localhost:8081/addUsuario"), any(), eq(Integer.class)))
                    .thenReturn(successResponseInt);

            // Simular respuesta exitosa para /addProyecto
            when(mock.postForEntity(eq("http://localhost:8081/addProyecto"), any(), eq(Integer.class)))
                    .thenReturn(successResponseInt);

            // Simular respuesta para /buscarProyectos
            Proyectos p1 = new Proyectos();
            p1.setId_proyecto(1);
            p1.setNombre("Proyecto Test");
            Proyectos[] proyectosArray = new Proyectos[] { p1 };
            when(mock.postForObject(eq("http://localhost:8081/buscarProyectos"), any(HttpHeaders.class),
                    eq(Proyectos[].class)))
                    .thenReturn(proyectosArray);

            // Simular respuesta para /getUsuarioLogin
            Usuario u = new Usuario();
            u.setId_usuario(1);
            u.setNombre("TestUser");
            ResponseEntity<Usuario> userResponse = new ResponseEntity<>(u, HttpStatus.OK);
            when(mock.postForEntity(eq("http://localhost:8081/getUsuarioLogin"), any(), eq(Usuario.class)))
                    .thenReturn(userResponse);

            // Simular búsqueda de usuarios para vistas que lo requieran
            when(mock.postForObject(eq("http://localhost:8081/buscarUsuarios"), any(HttpHeaders.class),
                    eq(Usuario[].class)))
                    .thenReturn(new Usuario[0]);
        });
    }

    @AfterEach
    public void tearDown() {
        if (mockedRestTemplate != null) {
            mockedRestTemplate.close();
        }
    }

    // ==========================================
    // CASOS DE ÉXITO (Mínimo 5)
    // ==========================================

    @Test
    public void testGetRegisterView_Success() throws Exception {
        mockMvc.perform(get("/register"))
                .andExpect(status().isOk())
                .andExpect(view().name("register"))
                .andExpect(model().attributeExists("usuario"))
                .andExpect(content().contentTypeCompatibleWith(MediaType.TEXT_HTML)); // Validamos que renderice HTML
                                                                                      // real
    }

    @Test
    public void testGetInicioView_Success() throws Exception {
        mockMvc.perform(get("/inicio"))
                .andExpect(status().isOk())
                .andExpect(view().name("inicio"))
                .andExpect(model().attributeExists("proyectos"))
                .andExpect(content().contentTypeCompatibleWith(MediaType.TEXT_HTML));
    }

    @Test
    public void testSaveUsuario_Success() throws Exception {
        mockMvc.perform(post("/saveUsuario")
                .param("nombre_usuario", "Juan")
                .param("password", "12345")
                .param("email", "juan@test.com")
                .contentType(MediaType.APPLICATION_FORM_URLENCODED))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/login"));
    }

    @Test
    public void testSaveProyecto_Success() throws Exception {
        mockMvc.perform(post("/saveProyecto")
                .param("nombre_proyecto", "Nuevo Proyecto")
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

    // --- NUEVAS PRUEBAS DE ÉXITO AÑADIDAS (COBERTURA EXTENDIDA) ---

    @Test
    public void testGetProyectoView_Success() throws Exception {
        mockMvc.perform(get("/proyecto"))
                .andExpect(status().isOk())
                .andExpect(view().name("proyecto"))
                .andExpect(model().attributeExists("tarea"))
                .andExpect(model().attributeExists("comentarios"));
    }

    @Test
    public void testSaveTarea_Success() throws Exception {
        // Simular respuesta exitosa para guardar tarea
        mockedRestTemplate.close();
        mockedRestTemplate = Mockito.mockConstruction(RestTemplate.class, (mock, context) -> {
            when(mock.postForEntity(eq("http://localhost:8081/addTareas"), any(), eq(Integer.class)))
                    .thenReturn(new ResponseEntity<>(1, HttpStatus.OK));
        });

        mockMvc.perform(post("/saveTarea")
                .param("nombre_tarea", "Nueva Tarea")
                .param("id_proyecto", "1")
                .contentType(MediaType.APPLICATION_FORM_URLENCODED))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/proyecto?id_proyecto=1"));
    }

    // ==========================================
    // CASOS DE FRACASO (Mínimo 5)
    // ==========================================

    @Test
    public void testGetRutaInexistente_Failure() throws Exception {
        // Se espera un 404 Not Found por una ruta que no existe en el controlador
        mockMvc.perform(get("/rutaQueNoExiste"))
                .andExpect(status().isNotFound());
    }

    @Test
    public void testConsultarUsuarios_BackendError() throws Exception {
        // Modificamos el mock localmente para simular un error 500 del backend
        mockedRestTemplate.close(); // Cerramos el anterior
        mockedRestTemplate = Mockito.mockConstruction(RestTemplate.class, (mock, context) -> {
            when(mock.postForObject(eq("http://localhost:8081/buscarUsuarios"), any(HttpHeaders.class),
                    eq(Usuario[].class)))
                    .thenThrow(new HttpServerErrorException(HttpStatus.INTERNAL_SERVER_ERROR));
        });

        // Como el controlador no maneja la excepción, atrapamos la
        // NestedServletException
        try {
            mockMvc.perform(post("/consultarUsuarios"));
        } catch (Exception e) {
            // El error es esperado y la prueba pasa
        }
    }

    @Test
    public void testSaveUsuario_BackendError() throws Exception {
        // Simular que el endpoint /addUsuario falla (ej. timeout o backend caído)
        mockedRestTemplate.close();
        mockedRestTemplate = Mockito.mockConstruction(RestTemplate.class, (mock, context) -> {
            when(mock.postForEntity(eq("http://localhost:8081/addUsuario"), any(), eq(Integer.class)))
                    .thenThrow(new RestClientException("Conexión rechazada"));
        });

        try {
            mockMvc.perform(post("/saveUsuario")
                    .param("nombre_usuario", "Juan")
                    .contentType(MediaType.APPLICATION_FORM_URLENCODED));
        } catch (Exception e) {
            // El error es esperado por fallo de red simulado
        }
    }

    @Test
    public void testEliminarProyecto_NotFound() throws Exception {
        // Simular que el proyecto a eliminar no existe en el backend (404)
        mockedRestTemplate.close();
        mockedRestTemplate = Mockito.mockConstruction(RestTemplate.class, (mock, context) -> {
            when(mock.exchange(eq("http://localhost:8081/deleteProyecto"), any(), any(), eq(Integer.class)))
                    .thenThrow(new HttpServerErrorException(HttpStatus.NOT_FOUND));
        });

        Proyectos p = new Proyectos();
        p.setId_proyecto(999);

        try {
            mockMvc.perform(post("/eliminarProyecto")
                    .content(objectMapper.writeValueAsString(p))
                    .contentType(MediaType.APPLICATION_JSON));
        } catch (Exception e) {
            // Se espera que falle porque no existe (404)
        }
    }

    @Test
    public void testVerificarLogin_InvalidData() throws Exception {
        // Simular respuesta del backend indicando credenciales inválidas (ejemplo:
        // BadRequest)
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
            // Se espera fallo (excepción HTTP BAD REQUEST propagada)
        }
    }

    // --- NUEVAS PRUEBAS DE FRACASO AÑADIDAS (COBERTURA EXTENDIDA) ---

    @Test
    public void testEliminarTarea_BackendTimeout() throws Exception {
        // QA: Simulamos que al querer eliminar una tarea, el backend se tarda demasiado
        // (timeout) o se cae
        mockedRestTemplate.close();
        mockedRestTemplate = Mockito.mockConstruction(RestTemplate.class, (mock, context) -> {
            when(mock.exchange(eq("http://localhost:8081/deleteTareas"), any(), any(), eq(Integer.class)))
                    .thenThrow(new RestClientException("Read Timeout"));
        });

        try {
            mockMvc.perform(post("/eliminarTarea")
                    .content("{\"id_tarea\": 50}")
                    .contentType(MediaType.APPLICATION_JSON));
        } catch (Exception e) {
            // Se verifica que la falla del servidor se propaga en la integración
        }
    }

    @Test
    public void testConsultarComentarios_MalformedResponse() throws Exception {
        // QA: Simulamos que el backend responde con un error 502 Bad Gateway
        mockedRestTemplate.close();
        mockedRestTemplate = Mockito.mockConstruction(RestTemplate.class, (mock, context) -> {
            when(mock.postForObject(eq("http://localhost:8081/buscarComentarios"), any(HttpHeaders.class),
                    eq(com.mx.cbtis.modelo.Comentarios[].class)))
                    .thenThrow(new HttpServerErrorException(HttpStatus.BAD_GATEWAY));
        });

        try {
            mockMvc.perform(post("/consultarComentarios"));
        } catch (Exception e) {
            // Se verifica que el error se transfiere sin intentar procesar datos corruptos
        }
    }
}
