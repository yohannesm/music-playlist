import Main.Song

import scala.collection.mutable

class Songs(private val inputSongs: Iterable[Song]) extends Resources{

  private var songMap: mutable.LinkedHashMap[Int, Song] = new mutable.LinkedHashMap()
  for(s <- inputSongs){
    songMap += (s.id -> s)
  }

  def addSong(song: Song): Unit = {
    songMap += (song.id -> song)
  }

  def removeSong(song_id: Int): Unit = {
    songMap -= song_id
  }

  def getSongs(): Vector[Song] = {
    songMap.values.toVector
  }

  def printSongs(): Unit = {
    for((_, s) <- songMap){
      println(s)
    }
  }

}
