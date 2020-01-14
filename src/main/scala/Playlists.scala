import scala.collection.mutable

class Playlists (private val inputPlaylists: Iterable[Playlist]) extends Resources{

  private var playlistMap: mutable.LinkedHashMap[Int, Playlist] = new mutable.LinkedHashMap()
  for(p <- inputPlaylists){
    playlistMap += (p.id -> p)
  }

  def addPlaylist(playlist: Playlist): Unit = {
    //TODO change this later
    assert(playlist.song_ids.nonEmpty)

    playlistMap += (playlist.id -> playlist)
  }

  def removePlayList(playlist_id: Int): Unit = {
    playlistMap -= playlist_id
  }

  def getPlaylist(): Vector[Playlist] = {
    playlistMap.values.toVector
  }

  def printPlaylist(): Unit = {
    for((_, p) <- playlistMap){
      println(p)
    }
  }

}
