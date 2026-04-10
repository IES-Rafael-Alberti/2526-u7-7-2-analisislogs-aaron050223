data class Configuracion(
    // Entrada
    val inputRuta: String,

    // Filtros
    val fechaDesde: String? = null,
    val fechaHasta: String? = null,
    val niveles: List<String>? = null,

    // Visualización
    val mostrarStats: Boolean = false,
    val generarReporte: Boolean = true,
    val ignoreInvalid: Boolean = false,
    val mostrarAyuda: Boolean = false,

    // Salida
    val mostrarPorConsola: Boolean = false,
    val outputRuta: String? = null
)