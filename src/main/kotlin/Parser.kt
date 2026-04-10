class Parser(private val args: Array<String>) {
    fun parse(): Configuracion? {
        var inputRuta: String? = null
        var fechaDesde: String? = null
        var fechaHasta: String? = null
        var niveles: List<String>? = null
        var mostrarStats: Boolean = false
        var generarReporte: Boolean = true
        var ignoreInvalid: Boolean = false
        var mostrarPorConsola: Boolean = false
        var mostrarAyuda: Boolean = false
        var outputRuta: String? = null

        var i = 0
        while (i < args.size) {
            when (args[i]) {
                "-i", "--input" -> {
                    if (i + 1 < args.size) {
                        inputRuta = args[i + 1]
                        i++
                    }
                }
                "-o", "--output" -> {
                    if (i + 1 < args.size) {
                        outputRuta = args[i + 1]
                        i++
                    }
                }
                "-l", "--level" -> {
                    val nivelesRetornar = mutableListOf<String>()
                    if (i + 1 < args.size) {
                        val nivelesSeparados = args[i + 1].split(",")
                        nivelesSeparados.forEach {
                            if (it.uppercase() in listOf("INFO", "WARNING", "ERROR")) {
                                nivelesRetornar.add(it)
                            }
                        }
                    }
                    niveles = nivelesRetornar
                }
                "-s", "--stats" -> {
                    mostrarStats = true
                }
                "--ignore-invalid" -> {
                    ignoreInvalid = true
                }
                "-p", "--stdout" -> {
                    mostrarPorConsola = true
                }
                "-r", "--report" -> {
                    generarReporte = true
                }
                "-h", "--help" -> {
                    mostrarAyuda = true
                }
            }
            i++
        }
        return if (inputRuta == null) {
            null
        } else Configuracion(
            inputRuta,
            fechaDesde,
            fechaHasta,
            niveles,
            mostrarStats,
            generarReporte,
            ignoreInvalid,
            mostrarAyuda,
            mostrarPorConsola,
            outputRuta
        )
    }
}