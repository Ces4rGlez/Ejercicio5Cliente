package com.mx.cbtis.modelo;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

public class ModelosClienteTest {

    @Test
    public void testUsuario() {
        Usuario u1 = new Usuario();
        u1.setId_usuario(10);
        u1.setNombre("Carlos");
        u1.setCorreo_electronico("carlos@mail.com");
        u1.setContrasena("secret");
        u1.setRol("ADMIN");
        u1.setImagen_usuario("avatar.png");

        assertEquals(10, u1.getId_usuario());
        assertEquals("Carlos", u1.getNombre());
        assertEquals("carlos@mail.com", u1.getCorreo_electronico());
        assertEquals("secret", u1.getContrasena());
        assertEquals("ADMIN", u1.getRol());
        assertEquals("avatar.png", u1.getImagen_usuario());

        Usuario u2 = new Usuario(20, "Ana", "ana@mail.com", "pass", "USER", "ana.png");
        assertEquals(20, u2.getId_usuario());
        assertEquals("Ana", u2.getNombre());
        assertEquals("ana.png", u2.getImagen_usuario());
    }

    @Test
    public void testProyectos() {
        Proyectos p1 = new Proyectos();
        p1.setId_proyecto(5);
        p1.setNombre("Sistema Web");
        p1.setDescripcion("Descripcion corta");
        p1.setFecha_inicio("2026-01-01");
        p1.setFecha_finalizacion("2026-12-31");
        p1.setEstado_proyecto("ACTIVO");
        p1.setFondo("#ffffff");

        assertEquals(5, p1.getId_proyecto());
        assertEquals("Sistema Web", p1.getNombre());
        assertEquals("Descripcion corta", p1.getDescripcion());
        assertEquals("2026-01-01", p1.getFecha_inicio());
        assertEquals("2026-12-31", p1.getFecha_finalizacion());
        assertEquals("ACTIVO", p1.getEstado_proyecto());
        assertEquals("#ffffff", p1.getFondo());

        Proyectos p2 = new Proyectos(6, "App Movil", "Detalle", "2026-02-01", "2026-11-01", "EN_PROCESO", "#000000");
        assertEquals(6, p2.getId_proyecto());
        assertEquals("App Movil", p2.getNombre());
    }

    @Test
    public void testTareas() {
        Tareas t1 = new Tareas();
        t1.setId_tarea(100);
        t1.setNombre("Crear API");
        t1.setDescripcion("Crear endpoints REST");
        t1.setFecha_vencimiento("2026-06-01");
        t1.setLista("Pendientes");
        t1.setEstado("PENDIENTE");
        t1.setId_proyecto(5);

        assertEquals(100, t1.getId_tarea());
        assertEquals("Crear API", t1.getNombre());
        assertEquals("Crear endpoints REST", t1.getDescripcion());
        assertEquals("2026-06-01", t1.getFecha_vencimiento());
        assertEquals("Pendientes", t1.getLista());
        assertEquals("PENDIENTE", t1.getEstado());
        assertEquals(5, t1.getId_proyecto());

        Tareas t2 = new Tareas(101, "Diseno UI", "Hacer mockups", "2026-05-01", "En proceso", "FINALIZADA", 5);
        assertEquals(101, t2.getId_tarea());
        assertEquals("Diseno UI", t2.getNombre());
    }

    @Test
    public void testAsignaciones() {
        Asignaciones a1 = new Asignaciones();
        a1.setId_asignacion(1);
        a1.setId_usuario(10);
        a1.setId_tarea(100);
        a1.setFecha_asignacion("2026-03-01");

        assertEquals(1, a1.getId_asignacion());
        assertEquals(10, a1.getId_usuario());
        assertEquals(100, a1.getId_tarea());
        assertEquals("2026-03-01", a1.getFecha_asignacion());

        Asignaciones a2 = new Asignaciones(2, 20, 101, "2026-03-02");
        assertEquals(2, a2.getId_asignacion());
        assertEquals(20, a2.getId_usuario());
    }

    @Test
    public void testComentarios() {
        Comentarios c1 = new Comentarios();
        c1.setId_comentario(50);
        c1.setId_usuario("10");
        c1.setId_tarea(100);
        c1.setTexto_comentario("Primer comentario de avance");
        c1.setFecha_hora("2026-04-10");

        assertEquals(50, c1.getId_comentario());
        assertEquals("10", c1.getId_usuario());
        assertEquals(100, c1.getId_tarea());
        assertEquals("Primer comentario de avance", c1.getTexto_comentario());
        assertEquals("2026-04-10", c1.getFecha_hora());

        Comentarios c2 = new Comentarios(51, "20", 101, "Segundo comentario", "2026-04-11");
        assertEquals(51, c2.getId_comentario());
        assertEquals("Segundo comentario", c2.getTexto_comentario());
    }
}
