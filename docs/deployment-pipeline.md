# Pipeline de Despliegue (Actividad 3)

Este documento describe el pipeline de despliegue implementado en
[`.github/workflows/deploy.yml`](../.github/workflows/deploy.yml), que se dispara
automáticamente al finalizar con éxito el pipeline de CI sobre `main`
(o manualmente vía `workflow_dispatch`).

## Etapas del pipeline

1. **acceptance-tests**: empaqueta la aplicación (`mvn package`) y ejecuta
   `mvn verify`, que corre las pruebas de aceptación/integración (Selenium)
   contra el artefacto ya compilado, simulando el ambiente de prueba.
2. **deploy-green**: despliega la nueva versión en un entorno paralelo
   llamado **GREEN**, manteniendo la versión anterior activa en **BLUE**.
   - Se ejecuta un *smoke test* sobre GREEN para verificar que el despliegue
     respondió correctamente.
   - Si el smoke test es exitoso, se conmuta el 100% del tráfico de BLUE
     hacia GREEN (estrategia **Blue-Green Deployment**).
3. **rollback**: se ejecuta únicamente si falla `acceptance-tests` o
   `deploy-green`. En ese caso el tráfico permanece (o vuelve) a BLUE, la
   última versión estable conocida, evitando dejar a los usuarios sobre una
   versión defectuosa.

## Estrategia Blue-Green y rollback

- **BLUE** = versión actualmente en producción (estable).
- **GREEN** = versión nueva que se despliega en paralelo y se valida antes
  de recibir tráfico real.
- Si GREEN pasa el smoke test, se conmuta el tráfico y GREEN pasa a ser la
  nueva versión estable (BLUE en el próximo ciclo).
- Si GREEN falla en cualquier etapa (acceptance tests o smoke test), el job
  `rollback` se activa automáticamente (`if: failure()`) y el tráfico se
  mantiene en BLUE, sin impacto para los usuarios.

## Evidencia de ejecución

Ver capturas de ejecución del pipeline en `docs/evidencias/` y los logs
generados en la pestaña *Actions* del repositorio en GitHub, donde quedan
registrados:

- El resultado de `acceptance-tests`.
- El log del smoke test sobre GREEN.
- La conmutación de tráfico o, en caso de fallo, la ejecución del job de
  `rollback`.
