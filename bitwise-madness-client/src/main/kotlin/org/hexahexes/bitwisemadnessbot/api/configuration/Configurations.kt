package org.hexahexes.bitwisemadnessbot.api.configuration

import com.beust.klaxon.Klaxon
import io.ktor.client.HttpClient
import io.ktor.client.features.json.JacksonSerializer
import io.ktor.client.features.json.JsonFeature
import io.ktor.http.HttpMethod
import org.hexahexes.bitwisemadnessbot.api.messages.command.CommandRoute

object Configurations {
    
    class Route(val id: String, val url: String, val method: String, val args: Array<String>)
    class Client(val prefix:String, val token: String, val routes: Array<Route>)

    val ROUTER : HashMap<String, CommandRoute>
    val PREFIX : String
    val TOKEN  : String
    val CLIENT : HttpClient = HttpClient(){
        install(JsonFeature) {
            serializer = JacksonSerializer()
        }
    }

    init {

        val inStream = this.javaClass.classLoader.getResourceAsStream("client.json")!!
        val clientConfig = Klaxon().parse<Client>(inStream)!!

        PREFIX = clientConfig.prefix
        val tokenVar = clientConfig.token
        TOKEN = System.getenv(tokenVar)
        
        ROUTER = HashMap()
        clientConfig.routes.forEach {
            val routeId = it.id
            val method  = HttpMethod(it.method)
            val commandRoute = CommandRoute(method, it.url, it.args)
            ROUTER[routeId] = commandRoute
        }
    }



}
