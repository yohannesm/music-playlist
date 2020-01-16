# music-playlist

## To run the application
Call it with `sbt run` and then its command line arguments
example : `sbt "run mixtape-data.json changes.json output.json" `
NB: The quote["] is really important because of how command line string is being parsed

## Note
### Error handling
I would definitely be more dilligent about the error handling. 
A lot of the exceptions here are uncaught in Main, but when I try to introduced it. 
It has a "forward reference extends over definition" error, so I am skipping over that. 

### Enum and Type safety
Another way I would do is define a better enum for action for each operations and resources
so I can have a better exhaustive case matching in my logic inside OperationsEngine
