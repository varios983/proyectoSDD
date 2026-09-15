# Spec-Driven Development (SDD) con GitHub, Copilot y Python

Este proyecto ha sido desarrollado para aprender e implementar la metodología **Spec-Driven Development (SDD)** (Desarrollo Guiado por Especificaciones).
El objetivo principal es construir software robusto en **Python** utilizando **GitHub** como plataforma de control de versiones y **GitHub Copilot** como asistente de Inteligencia Artificial para la generación de código a partir de especificaciones técnicas detalladas.

## Propósito del Proyecto

El desarrollo guiado por especificaciones prioriza la definición clara de los requerimientos, contratos de API y comportamientos del sistema antes de escribir la primera línea de código lógico. Este repositorio sirve como entorno de aprendizaje para:
* Diseñar especificaciones técnicas claras y estructuradas.
* Utilizar **GitHub Copilot** para traducir especificaciones en código Python funcional y pruebas unitarias.
* Adoptar buenas prácticas de control de versiones en **GitHub** mediante ramas y Pull Requests basados en features descritas en la especificación.

## Tecnologías Utilizadas

* **Lenguaje:** [Python](https://www.python.org/)
* **Asistente IA:** [GitHub Copilot](https://github.com/features/copilot)
* **Plataforma:** [GitHub](https://github.com/)
* **Pruebas:** PyTest (para la verificación automatizada de las especificaciones)

## Flujo de Trabajo (SDD)

1. **Definición de la Especificación:** Se crea un archivo de especificación (ej. `spec.md` o documentación OpenAPI) detallando las entradas, salidas y comportamiento esperado.
2. **Configuración del Entorno de Pruebas:** Se escriben los tests automatizados que validan el cumplimiento estricto de la especificación.
3. **Desarrollo con Copilot:** Se interactúa con GitHub Copilot proporcionándole el contexto de la especificación para generar la implementación en Python.
4. **Validación:** Se ejecutan las pruebas hasta que el 100% del código cumpla con los criterios definidos.

## Instalación y Uso

### Prerrequisitos
* Python 3.10 o superior instalado.
* Extensión de GitHub Copilot activa en tu IDE (VS Code / JetBrains).

### Configuración del entorno
```bash
# Clonar el repositorio
git clone https://github.com/tu-usuario/tu-repositorio.git
cd tu-repositorio

# Crear un entorno virtual
python -m venv venv
source venv/bin/activate  # En Windows: venv\Scripts\activate

# Instalar dependencias de desarrollo
pip install -r requirements.txt
```

### Ejecución de Pruebas
Para verificar que el código implementado cumple con las especificaciones definidas:
```bash
pytest
```

## Licencia

Este proyecto está bajo la Licencia MIT. Para más detalles, consulta el archivo LICENSE.
