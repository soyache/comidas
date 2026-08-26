# 🍔 Comidas - Control de Gastos & Registro Diario

Aplicación Android moderna y minimalista desarrollada con **Jetpack Compose** y **Material 3** para el control, registro y seguimiento de gastos diarios de alimentación (desayunos, almuerzos, cenas y pagos/abonos a cuentas acumuladas).

---

## ✨ Características

- 🕒 **Detección Automática de Tiempo de Comida**: Detección según la hora del día (Desayuno `06:00 - 11:59`, Almuerzo `12:00 - 17:59`, Cena `18:00 - 05:59`).
- 🍔 **Escaparate Hero Minimalista**: Tarjeta central inspirada en diseño editorial con etiqueta colgante de precio y botón interactivo para registrar comidas de días anteriores.
- 🛡️ **Modal de Confirmación**: Verificación previa al registro con selector interactivo de porciones (`[-] X [+]`) y desglose de precio total.
- 📅 **Registro de Comidas Pasadas**: Selección de fechas anteriores mediante calendario nativo (`DatePickerDialog`), selector de tiempo de comida, cantidades múltiples y notas opcionales.
- 💳 **Registro de Abonos**: Hoja inferior interactiva para abonar a la deuda con atajos de montos rápidos y saldo total.
- 📊 **Historial y Filtros Avanzados**: Filtrado por tipo de movimiento (Comidas/Abonos) y rangos de fecha flexibles (Hoy, Esta semana, Este mes, y selector personalizado de rango).
- ⚙️ **Configuración de Precios y Moneda**: Personalización de precios por comida, monedas predefinidas (`NIO`, `HNL`, `USD`, `EUR`, `MXN`, `COP`, `CRC`, `ARS`) y soporte para agregar monedas personalizadas con su símbolo y código.
- 🎨 **Diseño Editorial & Animaciones**: Paleta en tono Burgundy profundo (`#240A18`), fondo cálido, barra inferior tipo cápsula flotante transparente y logotipo animado de hamburguesa contable.

---

## 🛠️ Stack Tecnológico

- **Lenguaje**: Kotlin 2.0+
- **UI Toolkit**: Jetpack Compose con Material Design 3
- **Tipografía**: Plus Jakarta Sans (Google Fonts)
- **Animaciones**: Compose Transitions, Spring Physics & Canvas Drawing
- **Build System**: Gradle con Kotlin DSL y Version Catalogs (`libs.versions.toml`)
- **Arquitectura**: Arquitectura Limpia basada en componentes Contenedor-Presentacional

---

## 🚀 Compilación y Ejecución

```bash
# Clonar el repositorio
git clone https://github.com/soyache/comidas.git
cd comidas

# Compilar proyecto
./gradlew compileDebugSources

# Generar APK de depuración
./gradlew assembleDebug
```

---

## 📄 Licencia

Este proyecto está bajo la Licencia MIT.
