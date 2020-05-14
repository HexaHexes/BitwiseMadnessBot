module Handler

open System
open Configuration
open Parsing
open Discord.WebSocket
open System.Threading.Tasks
open Newtonsoft.Json
open FSharp.Data.HttpRequestHeaders
open FSharp.Data

type MessageCreatedHandler<'T> = SocketMessage -> Task<'T>

type CommandProperty = 
    | Method 
    | Address

type Service = { Address:String; Method: String }

let extractServiceInfo (cmdJson: JsonValue) =
    let method  = Method.ToString()  |> cmdJson.TryGetProperty 
    let address = Address.ToString() |> cmdJson.TryGetProperty 
    match method, address with 
    | Some(method), Some(address) -> Some { Method = method.AsString(); Address = address.AsString() }
    | _ -> None

let makeRequest (message: SocketMessage) service command  =
        let method  = service.Method
        let address = service.Address
        let contentType = [ContentType HttpContentTypes.Json]
        let messageSerialized = 
            {| Id        = message.Id
               AuthorId  = message.Author.Id
               ChannelId = message.Channel.Id 
               Content   = command.Content |} 
            |> JsonConvert.SerializeObject
            |> TextRequest
        Http.AsyncRequest 
            (address,
             headers = contentType,
             httpMethod = method,
             body = messageSerialized)

let onMessageCreated getPrefix tryGetCommand buildParser (message: SocketMessage) = 
    let messageContent = message.Content
    let prefix = getPrefix()
    let command = buildParser prefix messageContent     
    command 
    |> Option.bind (fun cmd -> tryGetCommand cmd.Key)
    |> Option.bind extractServiceInfo
    |> Option.map (fun service -> command 
                                  |> Option.get  
                                  |> makeRequest message service)
    |> Task.FromResult :> Task 
                        
let wrapHandler<'T> handler = 
    new Func<SocketMessage,Task>(fun message -> message |> handler :> Task)