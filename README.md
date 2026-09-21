# Examen Final — Automatización de Pruebas

Proyecto de ejemplo que integra control de versiones, pruebas automatizadas
y pipelines de CI/CD, desarrollado para el examen final de la asignatura
Automatización de Pruebas (Iplacex).

## Descripción del proyecto

El proyecto es una pequeña aplicación Java (`Calculadora`) usada como base
para demostrar tres niveles de pruebas y su automatización:

- **Pruebas unitarias (JUnit 5)**: validan la lógica de negocio de
  `Calculadora` de forma aislada.
- **Pruebas de integración/UI (Selenium + WebDriverManager)**: abren un
  navegador en modo headless y validan el comportamiento de una página web.
- **Pipeline de CI**: compila el proyecto y ejecuta ambos tipos de pruebas
  en cada `push`/`pull request` a `main`.
- **Pipeline de despliegue (CD)**: ejecuta acceptance tests y despliega la
  aplicación con estrategia **Blue-Green**, con rollback automático ante
  fallos.

## Estrategia de pruebas

| Tipo de prueba | Herramienta | Cuándo se ejecuta | Archivo |
|---|---|---|---|
| Unitaria | JUnit 5 | `mvn test` | `CalculadoraTest.java` |
| Integración / UI | Selenium + WebDriverManager | `mvn verify` (plugin failsafe) | `BusquedaWebIT.java` |
| Aceptación | `mvn verify` sobre el artefacto empaquetado | Pipeline de despliegue | `deploy.yml` |

Las pruebas unitarias usan el sufijo `*Test.java` (ejecutadas por
`maven-surefire-plugin`) y las de integración usan el sufijo `*IT.java`
(ejecutadas por `maven-failsafe-plugin`), lo que permite separarlas en
distintas etapas del pipeline.

## Flujo de ramas (Trunk-Based Development)

El repositorio usa **Trunk-Based Development**:

- `main` es la rama principal, siempre desplegable.
- Los cambios se hacen en ramas de vida corta (`feature/*`) que se integran
  a `main` mediante Pull Request y se eliminan después del merge.
- No se usa una rama `develop`, ya que eso corresponde a GitFlow.
- Cada push o Pull Request a `main` dispara el pipeline de CI.

## Cómo ejecutar las pruebas localmente

Requisitos: JDK 17 y Maven.

```bash
# Solo pruebas unitarias
mvn test

# Pruebas unitarias + pruebas de integración (requiere Chrome instalado
# y acceso a internet para descargar el driver la primera vez)
mvn verify
```

> **Nota:** si el equipo local no tiene salida a internet, la descarga del
> ChromeDriver falla (`UnknownHostException`). El log de ese intento queda
> en `docs/evidencias/log_pruebas_integracion.txt`. En GitHub Actions esto
> no es problema, ya que el pipeline de CI sí tiene acceso a internet y
> ejecuta la prueba de integración sin inconvenientes (ver
> `.github/workflows/ci.yml`, job `integration-tests`).

## Cómo se ejecutan los pipelines

### CI — `.github/workflows/ci.yml`

Se dispara en cada `push` y `pull request` hacia `main`. Etapas:

1. `build`: compila el proyecto (`mvn compile`).
2. `unit-tests`: ejecuta `mvn test` y publica el reporte de Surefire como
   artefacto descargable.
3. `integration-tests`: instala Chrome, ejecuta `mvn verify` (incluye las
   pruebas de Selenium) y publica el reporte de Failsafe.

### CD — `.github/workflows/deploy.yml`

Se dispara automáticamente cuando el pipeline de CI termina exitosamente
sobre `main` (también puede lanzarse manualmente). Ver el detalle completo
en [`docs/deployment-pipeline.md`](docs/deployment-pipeline.md). Resumen:

1. `acceptance-tests`: empaqueta la app y corre `mvn verify` como
   acceptance tests sobre el artefacto.
2. `deploy-green`: despliega la nueva versión en un entorno GREEN, ejecuta
   un smoke test y, si es exitoso, conmuta el tráfico hacia GREEN
   (**Blue-Green Deployment**).
3. `rollback`: se activa automáticamente si falla cualquier etapa
   anterior, manteniendo el tráfico en BLUE (última versión estable).

## Evidencias

En `docs/evidencias/` se incluyen los logs de ejecución local generados
durante el desarrollo:

- `log_pruebas_unitarias.txt`: ejecución exitosa de `mvn test` (5/5 pruebas
  OK).
- `log_pruebas_integracion.txt`: intento de ejecución de `mvn verify` en el
  entorno sandbox sin acceso a internet (evidencia de la limitación de red
  local, no del pipeline en sí).
- `log_pipeline_despliegue.txt`: simulación local de las etapas del
  pipeline de despliegue, incluyendo un escenario de despliegue exitoso
  (Blue-Green) y un escenario de fallo con rollback.

Las capturas de ejecución exitosa de los workflows en GitHub Actions deben
agregarse a esta misma carpeta una vez el repositorio esté publicado en
GitHub y los pipelines se ejecuten en la nube.

## Estructura del proyecto

```
.
├── .github/workflows/
│   ├── ci.yml              # Pipeline de CI (build + pruebas)
│   └── deploy.yml          # Pipeline de despliegue (Blue-Green + rollback)
├── docs/
│   ├── deployment-pipeline.md
│   └── evidencias/
├── src/
│   ├── main/java/cl/iplacex/automatizacion/Calculadora.java
│   └── test/java/cl/iplacex/automatizacion/
│       ├── CalculadoraTest.java   # Pruebas unitarias
│       └── BusquedaWebIT.java     # Pruebas de integración (Selenium)
├── pom.xml
└── README.md
```

## Autor

Miguel Cespedes
