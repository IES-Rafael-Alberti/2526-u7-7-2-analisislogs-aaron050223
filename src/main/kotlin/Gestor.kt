import java.io.File

class Gestor {
    private val listaLogs = mutableListOf<Log>()
    private var contadorErrores: Int = 0

    fun verListaLogs() {
        listaLogs.forEach { println(it) }
    }

    fun obtenerEntrada(): String {
        print("Ruta al fichero >> ")
        val entradaFichero = readln()
        return entradaFichero
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
        val formato = """^([01]\d|2[0-3]):([0-5]\d)$""".toRegex()
        return hora.matches(formato)
    }

    private fun validarNivel(nivel: String): Boolean {
        return Niveles.entries.any { it.name == nivel }
    }

    private fun obtenerErrores(fecha: String, hora: String, nivel: String): List<String> {
        val errores = mutableListOf<String>()

        if (!validarFecha(fecha)) errores.add("Formato de año no valido")
        if (!validarHora(hora)) errores.add("Formato de hora no valido")
        if (!validarNivel(nivel)) errores.add("Nivel '$nivel' no reconocido")

        if (errores.isNotEmpty()) contadorErrores++

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

    private fun procesarLinea(lineaFichero: String): Log {
        val datosFichero = lineaFichero.split(" ", limit = 4).toMutableList()
        eliminarCorchetes(datosFichero)

        val fecha = datosFichero[0]
        val hora = datosFichero[1]
        val nivel = datosFichero[2]
        val mensaje = datosFichero[3]

        return Log(fecha, hora, nivel, mensaje)

    }

    fun ejecutar(fichero: File) {
        fichero.forEachLine {
            val log = procesarLinea(it)
            addLog(log)
        }
    }
}