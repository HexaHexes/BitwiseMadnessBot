module Configuration

open FSharp.Data
open System.Reflection

let [<Literal>] private CONFIG_PATH = @"Resources/Configuration.json"
let [<Literal>] CONFIG_FILENAME     = "Configuration.json"

type Configuration = JsonProvider<CONFIG_PATH>

let private assembly = Assembly.GetExecutingAssembly() 
let private resources = assembly.GetManifestResourceNames()

let getResourceAsStream (fileName: string) = 
    resources 
    |> Array.tryFind (fun uri -> uri.EndsWith(fileName))
    |> Option.map assembly.GetManifestResourceStream


