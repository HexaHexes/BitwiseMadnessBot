package org.hexahexes.bitwisemadnessbot.api.configuration

import com.beust.klaxon.Klaxon
import io.ktor.client.HttpClient
import org.hexahexes.bitwisemadnessbot.api.messages.command.CommandRoute
import org.hexahexes.bitwisemadnessbot.api.util.HttpMethod
import java.io.File

object Configurations {

    class Route(val name: String, val url: String,val method: String, val args: Array<String>)
    class Client(val servicesAddress: String, val prefix:String, val token: String, val routes: Array<Route>)

    val ROUTER : HashMap<String, CommandRoute>
    val SERVICES_ADDRESS: String
    val PREFIX : String
    val TOKEN  : String
    val CLIENT : HttpClient = HttpClient()

    init {

        val clientConfig = Klaxon().parse<Client>(
            File("resources/client.json")
        )!!

        PREFIX = clientConfig.prefix
        TOKEN = System.getenv(clientConfig.token)

        SERVICES_ADDRESS = clientConfig.servicesAddress
        
        ROUTER = HashMap()
        clientConfig.routes.forEach {
            val routeId = it.name
            val method  = HttpMethod.valueOf(it.method)
            val commandRoute = CommandRoute(method, it.url, it.args)
            ROUTER[routeId] = commandRoute
        }
    }



}
