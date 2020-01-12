case class Playlist(id: Int, user_id: Int, song_ids: Vector[Int]) {
  require(song_ids.nonEmpty)
}
