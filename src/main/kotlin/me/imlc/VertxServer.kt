package me.imlc

import com.google.common.util.concurrent.AbstractIdleService
import com.google.common.util.concurrent.AbstractService
import io.vertx.core.Vertx
import io.vertx.ext.web.Route
import io.vertx.ext.web.Router
import io.vertx.ext.web.handler.StaticHandler
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import java.net.URI

class VertxServer(
    private val vertx: Vertx = Vertx.vertx(),
    val port: Int = 8080,
    private val logger: Logger = LoggerFactory.getLogger(VertxServer::class.java)
) : AbstractService() {

    override fun doStart() {

        val router = Router.router(vertx)
        router.get("/api/v1/hello").handler {
            it.end("Hello, world!")
        }

        // Uncomment below to enable static file handling
        // router.get().handler(createStaticHandler())

        val uri = URI.create("http://localhost:${port}")
        vertx.createHttpServer()
            .webSocketHandler { ws ->
                if (ws.path().equals("/ws"))
                    ws.handler(ws::writeBinaryMessage)
            }
            .requestHandler(router)
            .listen(uri.port, uri.host, {
                if (it.succeeded()) {
                    logger.info("succeeded to start HTTP service at ${uri}")
                    notifyStarted()
                } else {
                    logger.error("failed to start HTTP service at ${uri}")
                    notifyFailed(it.cause())
                }
            })
    }

    override fun doStop() {
        vertx.close().andThen {
            notifyStopped()
        }
            .onFailure {
                notifyFailed(it)
            }
    }

    private fun createStaticHandler(): StaticHandler {
        return StaticHandler.create("./static")
            .setCachingEnabled(true)
            .setFilesReadOnly(true)
            .setDirectoryListing(false)
    }
}