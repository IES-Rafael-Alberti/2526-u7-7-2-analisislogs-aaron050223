import java.io.File

class Gestor(val config: Configuracion?) {
    private val listaLogs = mutableListOf<Log>()
    private var contadorErrores: Int = 0

    fun verListaLogs() {
        listaLogs.forEach { println(it) }
    }

    private fun eliminarPrimerCorchete(lista: MutableList<String>) {
        lista[0] = lista[0].substringAfter("[")
    }

    private fun eliminarSegundoCorchete(lista: MutableList<String>) {
        lista[1] = lista[1].substringBefore("]")
    }

    private fun eliminarCorchetes(datosFichero: MutableList<String>) {
        eliminarPrimerCorchete(datosFichero)
        eliminarSegundoCorchete(datosFichero)
    }

    private fun validarFecha(fecha: String): Boolean {
        val formato = """^\d{4}-(0[1-9]|1[0-2])-(0[1-9]|[12]\d|3[01])$""".toRegex()
        return fecha.matches(formato)
    }

    private fun validarHora(hora: String): Boolean {
        val formato = """^([01]\d|2[0-3]):([0-5]\d):([0-5]\d)$""".toRegex()
        return hora.matches(formato)
    }

    private fun validarNivel(nivel: String): Boolean {
        return Niveles.entries.any { it.name == nivel }
    }

    fun obtenerErrores(fecha: String, hora: String, nivel: String, ignoreInvalid: Boolean): List<String> {
        val errores = mutableListOf<String>()

        if (!validarFecha(fecha)) errores.add("Formato de año no valido")
        if (!validarHora(hora)) errores.add("Formato de hora no valido")
        if (!validarNivel(nivel)) errores.add("Nivel '$nivel' no reconocido")

        if (errores.isNotEmpty() && ignoreInvalid) contadorErrores++

        return errores
    }

    private fun mostrarErrores(errores: List<String>) {
        if (errores.isNotEmpty()){
            errores.forEach {
                println(it)
            }
        }
    }

    private fun validar(errores: List<String>): Boolean {
        return errores.isEmpty()
    }

    private fun addLog(log: Log) {
        listaLogs.add(log)
    }

    private fun procesarLinea(lineaFichero: String): Log? {
        val datosFichero = lineaFichero.split(" ", limit = 4).toMutableList()
        eliminarCorchetes(datosFichero)

        val fecha = datosFichero[0]
        val hora = datosFichero[1]
        val nivel = datosFichero[2]
        val mensaje = datosFichero[3]

        val errores = obtenerErrores(fecha, hora, nivel, config?.ignoreInvalid ?: false)
        val validacion = validar(errores)

        return if (validacion) Log(fecha, hora, nivel, mensaje) else null

    }

    fun procesarLineas(fichero: File) {
        fichero.forEachLine {
            val log = procesarLinea(it)
            if (log != null) {addLog(log)}
        }
    }

    fun ejecutar() {
        if (config == null) {
            println("Error: Ningún comandos proporcionado")
            return
        }
        val fichero = File(config.inputRuta)

        if (!fichero.exists()) {
            println("Error: El fichero '${config.inputRuta}' no existe")
            return
        }

        procesarLineas(fichero)

        var resultado = buildString {
            appendLine("\n\n=== INFORME DE LOGS ===")
            appendLine("> Ruta: ${config.inputRuta}")

            if (config.mostrarStats) {
                appendLine("> Resumen:")
                appendLine("  - Líneas procesadas: ${listaLogs.size + contadorErrores}")
                appendLine("  - Líneas válidas: ${listaLogs.size}")
                appendLine("  - Líneas inválidas: $contadorErrores")
            }
            if (config.niveles != null) {
                appendLine("> Conteo por nivel:")
                config.niveles.forEach { nivel ->
                    val cantidad = listaLogs.filter { log -> log.nivel in nivel }.size
                    appendLine("  - $nivel: $cantidad")
                }
            }
        }
        if (config.mostrarAyuda) {
            println("\n\n=== DESCRIPCIÓN ===\n" +
                    "> Procesa un fichero de logs con formato:\n" +
                    "  - [YYYY-MM-DD HH:MM:SS] NIVEL Mensaje\n" +
                    "\n" +
                    "Opciones:\n" +
                    "  -i, --input <fichero>        Fichero de entrada (obligatorio)\n" +
                    "  -f, --from <fechaHora>       Fecha/hora inicial inclusive\n" +
                    "                               Formato: \"YYYY-MM-DD HH:MM:SS\"\n" +
                    "  -t, --to <fechaHora>         Fecha/hora final inclusive\n" +
                    "                               Formato: \"YYYY-MM-DD HH:MM:SS\"\n" +
                    "  -l, --level <niveles>        Filtra niveles: INFO, WARNING, ERROR\n" +
                    "                               Puede indicarse una lista separada por comas\n" +
                    "  -s, --stats                  Muestra solo estadísticas\n" +
                    "  -r, --report                 Genera informe completo\n" +
                    "  -o, --output <fichero>       Guarda la salida en un fichero\n" +
                    "  -p, --stdout                 Muestra la salida por consola\n" +
                    "      --ignore-invalid         Ignora líneas inválidas y continúa\n" +
                    "  -h, --help                   Muestra esta ayuda")
        }

        if (config.outputRuta != null) {
            val archivo = File(config.outputRuta)
            archivo.parentFile?.mkdirs()
            archivo.writeText(resultado)
            resultado += "> Informe generado en ${config.outputRuta}"
        }
        if (config.mostrarPorConsola) println(resultado)
    }
}