package me.imlc

class Cli {

    fun version() {
        println("0.0.1")
    }

    fun server() {
        val app = App()
        app.start()

        Runtime.getRuntime().addShutdownHook(object : Thread() {
            override fun run() {
                app.stop()
            }
        })

    }

}