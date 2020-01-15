import Main.{Operation, Playlist, Song}

class OperationsEngine(operations: Iterable[Operation], songs: Songs, users: Users, playlists: Playlists) {

  def processOperations(): Unit ={
    for(op <- operations){
      op.resource match {
        case "song" => handleSong(op)
        case "playlist" => handlePlaylist(op)
        case "user" => handleUser(op)
        case _ => ???
      }
    }
  }

  def handlePlaylist(op: Operation): Unit = {
    val playlist = op.metadata.asInstanceOf[Playlist]
    op.action match {
      case "add" => {
        playlists.addPlaylist(playlist)
      }
      case "delete" => {
        playlists.removePlayList(playlist.id)
      }
    }
  }

  def handleSong(op: Operation): Unit = {
    val song = op.metadata.asInstanceOf[Song]
    op.action match {
      case "add" => {
        songs.addSong(song)
      }
      case "delete" => {
        songs.removeSong(song.id)
      }
    }
  }

  def handleUser(op: Operation) = ???
}
