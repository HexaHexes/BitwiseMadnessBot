module Parsing

open System

type Command = { Key : string
                 Content : string }

let parseCommand (prefix: string) (str: string) =
    let trimmedStr = str.Trim()
    let prefixSize = prefix.Length
    let strSize    = str.Length
    if strSize > prefixSize && trimmedStr.StartsWith(prefix) && trimmedStr <> prefix then  
        let command = 
            let cmdAndContent = trimmedStr.Substring(prefixSize).TrimStart()
            let firstSpace    = cmdAndContent.IndexOf(" ")   
            if firstSpace = -1 then
                { Key = cmdAndContent; Content = "" }
            else 
                { Key = cmdAndContent.Substring(0,firstSpace)
                  Content = cmdAndContent.Substring(firstSpace).TrimStart() }
        Some command 
    else 
        None

