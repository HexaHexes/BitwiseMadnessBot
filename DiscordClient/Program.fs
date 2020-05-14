// Learn more about F# at http://fsharp.org

open System
open Configuration
open Parsing
open Discord
open Discord.WebSocket
open Handler
open FSharp.Data
open System.IO 

[<EntryPoint>]
let main argv = 
    async {
        use client = new DiscordSocketClient() 

        let! configuration = async {
            use configStream = getResourceAsStream CONFIG_FILENAME |> Option.get
            use reader = new StreamReader(configStream)
            
            let! loaded = reader.ReadToEndAsync() |> Async.AwaitTask
            
            return Configuration.Parse loaded
        }

        let token = Environment.GetEnvironmentVariable configuration.Token
        
        let! _ = client.StartAsync() |> Async.AwaitTask
        let! _ = client.LoginAsync(TokenType.Bot, token) |> Async.AwaitTask

        let getPrefix() = configuration.Prefix 
        
        let tryGetCommand key = 
            configuration.Commands.JsonValue.TryGetProperty key  
            
        onMessageCreated getPrefix tryGetCommand parseCommand
        |> wrapHandler
        |> client.add_MessageReceived
        
        printfn "Client listening..."
        
        Console.Read() |> ignore 

        printfn "Client logging out..."

        let! _ = client.LogoutAsync() |> Async.AwaitTask
        
        printfn "Client shutting down..."
        
        return 0
    } |> Async.RunSynchronously
    
