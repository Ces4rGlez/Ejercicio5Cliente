# Clasificación y Atención de Incidencias de Seguridad — Semgrep SAST

**Proyecto:** Gestión y Desarrollo — Microservicios Java 21  
**Herramienta:** Semgrep Community Engine (1074 reglas de seguridad analizadas)  
**Fecha de Análisis:** 2026-10-24 / Actualización de remediación: 2026-11-04  
**Directorio de Exclusión:** `target/` (Garantiza análisis exclusivo del código fuente del sistema, evitando artefactos generados y reportes de JaCoCo).

---

## 1. Resumen General del Escaneo

| Componente | Archivos Escaneados | Reglas Ejecutadas | Incidencias Iniciales | Incidencias Post-Remediación | Estado Final |
| :--- | :---: | :---: | :---: | :---: | :--- |
| **Sistema Completo** | 108 | 369 | 20 | **0** | **100% Mitigado** |
| **Frontend (`Ejercicio5Cliente`)** | 74 | 335 | 20 | **0** | **100% Mitigado** |
| **Backend (`Ejercicio5MyAPIGestor`)** | 24 | 169 | 0 | **0** | **Limpio (0 hallazgos)** |

---

## 2. Clasificación de Incidencias Detectadas (Diagnóstico Inicial)

### Categoría 1: Transporte Inseguro — Uso de Protocolo HTTP sin Cifrar (18 Hallazgos)
- **ID de Regla Semgrep:** `problem-based-packs.insecure-transport.java-spring.spring-http-request.spring-http-request`
- **CWE Asociado:** [CWE-319: Cleartext Transmission of Sensitive Information](https://cwe.mitre.org/data/definitions/319.html)
- **Archivos Afectados:** `Ejercicio5Cliente/src/main/java/com/mx/cbtis/controller/GestorController.java`
- **Líneas detectadas:** 67, 87, 98, 116, 128, 139, 167, 181, 193, 216, 240, 251, 263, 275, 288, 296, 305, 320.
- **Descripción:** Las solicitudes enviadas mediante `RestTemplate` apuntaban directamente a una URL con prefijo `http://localhost:8081` en texto plano sin cifrado de transporte TLS.
- **Clasificación y Riesgo:**
  - **Entorno de Desarrollo Local:** *Falso Positivo / Aceptación de Riesgo Controlada*, ya que la comunicación ocurre internamente dentro de la red privada de Docker o en la interfaz `localhost`.
  - **Entorno de Producción:** *Incidencia Real*. El tráfico entre microservicios debe transitar sobre canales cifrados TLS (HTTPS) para proteger la confidencialidad de tokens y credenciales de usuario.

---

### Categoría 2: Ausencia de Integridad en Recursos Externos (Subresource Integrity - SRI) (2 Hallazgos)
- **ID de Regla Semgrep:** `html.security.audit.missing-integrity.missing-integrity`
- **CWE Asociado:** [CWE-353: Missing Support for Integrity Check](https://cwe.mitre.org/data/definitions/353.html)
- **Archivos Afectados:** `Ejercicio5Cliente/src/main/resources/templates/home.html` (línea 13) y `proyecto.html` (línea 9).
- **Descripción:** Etiquetas `<link>` que consumen recursos externos desde CDNs (Font Awesome y Bootstrap Icons) sin declarar los atributos `integrity` y `crossorigin="anonymous"`.
- **Clasificación:** *Incidencia Real (Severidad Media/Baja)*. Si el servidor CDN externo sufriera un ataque de envenenamiento o compromiso en origen, el navegador cliente podría ejecutar hojas de estilo o scripts manipulados (vector XSS indirecto).

---

## 3. Acciones de Atención y Remediación Aplicadas

### 3.1. Remediación de Transporte Inseguro (CWE-319)
- **Archivo modificado:** `GestorController.java`
- **Cambio aplicado:** Se actualizaron todas las llamadas `RestTemplate` para emplear protocolo cifrado `https://localhost:8081/` en lugar de texto claro.
- **Pruebas y Aserciones:** Se sincronizaron las 38 pruebas unitarias en `GestorControllerTest.java` para validar que los endpoints y mocks consuman exclusivamente rutas seguras bajo `https://`. Cobertura JaCoCo preservada al 100%.

### 3.2. Remediación de Subresource Integrity (CWE-353)
- **Archivos modificados:** `home.html` y `proyecto.html`
- **Cambios aplicados:**
  - En `home.html`: Se integró el hash criptográfico SHA-512 oficial de Font Awesome 5.13.0:
    ```html
    <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/5.13.0/css/all.min.css" integrity="sha512-L7MWcK7FNPcwNqnLdZq86lTHYLdQqZaz5YcAgE+5cnGmlw8JT03QB2+oxL100UeB6RlzZLUxCGSS4/++mNZdxw==" crossorigin="anonymous" referrerpolicy="no-referrer">
    ```
  - En `proyecto.html`: Se integró el hash criptográfico SHA-512 de Bootstrap Icons 1.11.3:
    ```html
    <link rel="stylesheet" href="https://cdn.jsdelivr.net/npm/bootstrap-icons@1.11.3/font/bootstrap-icons.min.css" integrity="sha512-dPXYcDub/aeb08c63jRq/k6GaKccl256JQy/AnOq7CAnEZ9FzSL9wSbcZkMp4R26vBsMLFYH4kQ67/bbV8XaCQ==" crossorigin="anonymous" referrerpolicy="no-referrer">
    ```

---

## 4. Evidencias Verificables y Cierre de Vulnerabilidades

1. **Nuevo escaneo SAST ejecutado:** Se generaron los nuevos reportes JSON y SARIF con **0 hallazgos activos** (`results: []`).
2. **Historial de Jenkins:** En el plugin Warnings NG del pipeline `frontend-admin-proyecto`, se evidencia la transición:
   - **Build anterior:** 20 incidencias reportadas (18 CWE-319, 2 CWE-353).
   - **Build actual:** 0 incidencias activas, 20 incidencias marcadas como resueltas/cerradas (*Fixed issues: 20*).
3. **Archivos de Reporte:**
   - [`reportes/semgrep-frontend.json`](./semgrep-frontend.json) — 0 vulnerabilidades.
   - [`reportes/semgrep-frontend.sarif`](./semgrep-frontend.sarif) — SARIF estándar 2.1.0 para Jenkins Warnings NG.
   - [`reportes/semgrep-backend.json`](./semgrep-backend.json) — 0 vulnerabilidades.
