# music-playlist

## To run the application
First and foremost you have to install sbt and scala in your system. 
Please look up on the internet on how to install them depending on your OS.
Next, you can call the app with `sbt run` and then its command line arguments
example : `sbt "run mixtape-data.json changes.json output.json" `
NB: The quote["] is really important because of how command line string is being parsed

## changes.json Example explanation
So I added new songs from id 41 - 50. I added new playlists of id 4-8. Then, I removed playlist of id 1 and id 6.

## Data structure choice. 
I am using a LinkedHashMap just because I want things to be in the same order of the input instead of just
using a plan HashMap. Otherwise I think a simple HashMap would suffice in doing the job. 
I am using a HashMap so that Insert and Delete could be in the order of O(1) amortized time. 

## Scaling problem


## Note
### Error handling
I would definitely be more dilligent about the error handling. 
A lot of the exceptions here are uncaught in Main, but when I try to introduced it. 
It has a "forward reference extends over definition" error, so I am skipping over that. 

### Enum and Type safety
Another way I would do is define a better enum for action for each operations and resources
so I can have a better exhaustive case matching in my logic inside OperationsEngine

### Improvements
Another improvement I could see is also in enabling to just pass an ID for delete operations. 
Another one is to note whether each operations is successful or not and log them if any of them failed
(Id does not exist, etc). 
