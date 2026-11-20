# AutoPro - Sistema de Gestión Mecánica

Proyecto web desarrollado en **Java 17 + Spring Boot 3.3 + Thymeleaf + Spring Security + MySQL**, con diseño visual inspirado en la plantilla **AutoPro** (Landing Page pública, Consulta de Seguimiento de Vehículo por placa y Dashboard Administrativo con RBAC).

## Vistas Principales
1. **Landing Page (`/`):** Hero section azul marino oscuro (`#132238`), métricas (+2,500 vehículos, +1,800 clientes, 15 años, 98% satisfacción), beneficios, servicios, solicitud de cita online y acceso rápido.
2. **Seguimiento de Vehículo (`/seguimiento`):** Consulta pública por número de placa para ver el estado en tiempo real de la Orden de Trabajo (`EN_DIAGNOSTICO`, `EN_PROCESO`, `FINALIZADO`, `PAGADO`), diagnóstico técnico, repuestos aplicados y total.
3. **Autenticación y Registro (`/login` y `/registro`):** Inicio de sesión con Spring Security + BCrypt y registro de nuevos clientes/usuarios.
4. **Dashboard Admin (`/dashboard`):** Panel interno con control de acceso por roles (`ADMIN`, `ASESOR`, `MECANICO`) para gestionar Clientes/Vehículos, Órdenes de Trabajo, Inventario y Facturación en PDF.

## Credenciales Precargadas
- **Administrador:** `admin` / `123456`
- **Asesor / Recepcionista:** `asesor` / `123456`
- **Mecánico:** `mecanico` / `123456`
- **Placas de prueba para seguimiento público:** `ABC-123` y `XYZ-789`

## Ejecución en IntelliJ IDEA / VS Code + MySQL
1. Inicia MySQL y verifica las credenciales en `src/main/resources/application.properties` (por defecto `root` / `root`; crea la BD `autopro_db` automáticamente).
2. Ejecuta `AutoProApplication.java` o corre `mvn spring-boot:run`.
3. Abre `http://localhost:8080`.
