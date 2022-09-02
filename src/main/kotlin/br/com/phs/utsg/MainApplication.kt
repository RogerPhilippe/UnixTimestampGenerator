package br.com.phs.utsg

import java.awt.Toolkit
import java.awt.datatransfer.StringSelection
import java.util.*
import javax.swing.JOptionPane

fun main(args: Array<String>) {
    MainApplication(args)
}

class MainApplication(args: Array<String>) {

    init {

        if (args.isEmpty() || args.first() == "-g") {
            gui()
        } else if (args.first() == "-cli") {
            cli()
        }

    }

    private fun gui() {
        JOptionPane.showOptionDialog(
            null,
            "Tipo do timestamp",
            "Gerador Timestamp",
            JOptionPane.YES_NO_OPTION,
            JOptionPane.INFORMATION_MESSAGE,
            null,
            arrayOf("Hora atual", "Setar hora"),
            null
        ).run {
            when (this) {
                0 -> {
                    val timestamp = generateDateTimestampNow()
                    showTimestamp(timestamp.toString())
                }
                1 -> {
                    val content = JOptionPane.showInputDialog("Insira o dia mês e ano separados por ;\nExemplo: 28;7;2022")
                    handlerContent(
                        content,
                        { showTimestamp(it.toString()) },
                        { JOptionPane.showMessageDialog(null, it) }
                    )

                    if (content.isNullOrEmpty() || content.filter { it == ';' }.length != 2) {
                        JOptionPane.showMessageDialog(null, "Erro na etrada dos dados!")
                    } else {
                        val timestamp = generateDateTimestamp(content)
                        if (timestamp <= 0) {
                            JOptionPane.showMessageDialog(null, "Erro no retorno do timestamp!")
                        } else {
                            showTimestamp(timestamp.toString())
                        }
                    }
                }
            }
        }
    }

    private fun cli() {

        println("Opções: 1 - Hora atual || 2 - Setar hora || quit - sair")
        val scanner = Scanner(System.`in`)
        val cmd = scanner.nextLine()
        when (cmd.toString()) {
            "1" -> {
                val timestamp = generateDateTimestampNow()
                println("Timestamp gerado: $timestamp")
                cli()
            }
            "2" -> {
                println("Insira o dia mês e ano separados por ;\nExemplo: 28;7;2022")
                val content = scanner.nextLine()
                handlerContent(
                    content,
                    {
                        println("Timestamp gerado: $it")
                        cli()
                    },
                    {
                        println("Timestamp gerado: $it")
                        cli()
                    }
                )
            }
            "quit" -> { }
            else -> {
                println("Command $cmd não é um comando válido!")
                cli()
            }
        }

    }

    private fun generateDateTimestampNow(): Long {
        return Calendar.getInstance(Locale.getDefault()).timeInMillis
    }

    private fun generateDateTimestamp(dayMonthYear: String): Long {
        val (day, month, year) = dayMonthYear.split(";")
        val calendar = Calendar.getInstance()
        try {
            calendar.set(year.toInt(), (month.toInt()+1), day.toInt())
        } catch (ex: Exception) {
            return -1
        }
        return calendar.timeInMillis
    }

    private fun handlerContent(data: String, done: (content: Long)->Unit, error: (error: String)->Unit) {
        if (data.isEmpty() || data.filter { it == ';' }.length != 2) {
            error.invoke("Erro na etrada dos dados!")
        } else {
            val timestamp = generateDateTimestamp(data)
            if (timestamp <= 0) {
                error.invoke("Erro no retorno do timestamp!")
            } else {
                done.invoke(timestamp)
            }
        }
    }

    private fun showTimestamp(timestamp: String) {
        println("Timestamp gerado: $timestamp")
        val clipboard = Toolkit.getDefaultToolkit().systemClipboard
        val stringSelection = StringSelection(timestamp)
        clipboard.setContents(stringSelection, null)
        JOptionPane.showMessageDialog(null, "Timestamp: $timestamp\nCopiado para a área de transferência.")
    }

}