import java.io.File

fun main(args: Array<String>) {
    val parser = Parser(args)
    val config = parser.parse()
    val gestor = Gestor(config)

    gestor.ejecutar()
}