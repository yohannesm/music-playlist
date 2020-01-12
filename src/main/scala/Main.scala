import java.io.{BufferedWriter, File, FileWriter}

import io.circe._
import io.circe.generic.semiauto._
import io.circe.parser._
import io.circe.generic.auto._
import io.circe.syntax._

import scala.io.Source

object Main extends App {

  sealed trait Resources
  case class Users(users: Vector[User]) extends Resources
  case class Playlists(playlists: Vector[Playlist]) extends Resources
  case class Songs(songs: Vector[Song]) extends Resources

  implicit val userDecoder: Decoder[User] = deriveDecoder[User]
  implicit val userEncoder: Encoder[User] = deriveEncoder[User]
  implicit val playListDecoder: Decoder[Playlist] = deriveDecoder[Playlist]
  implicit val playListEncoder: Encoder[Playlist] = deriveEncoder[Playlist]
  implicit val SongDecoder: Decoder[Song] = deriveDecoder[Song]
  implicit val SongEncoder: Encoder[Song] = deriveEncoder[Song]

  println("Main is running!")
  val inputFile = Source.fromFile("mixtape-data.json").getLines.mkString
  val inputJson = parse(inputFile).getOrElse(Json.Null)
  val cursor: HCursor = inputJson.hcursor

  val decodedUsers = cursor.downField("users").values.getOrElse(Vector.empty[Json]).map(_.as[User])
  val users = decodedUsers.map{ eitherUser =>
    eitherUser match {
      case Left(failure) => {
        println(s"Failed in decoding because of $failure")
        User(-1, "Invalid User")
      }
      case Right(user) => user
    }}.filterNot(_.id == -1)
  //println(users)

  val decodedPlaylists = cursor.downField("playlists").values.getOrElse(Vector.empty[Json]).map(_.as[Playlist])
  val playlists = decodedPlaylists.map{ eitherPlaylist =>
    eitherPlaylist match {
      case Left(failure) => {
        println(s"Failed in decoding because of $failure")
        Playlist(-1, -1, Vector.empty[Int])
      }
      case Right(playlist) => playlist
    }}.filterNot(_.id == -1)
  //println(playlists)

  val decodedSongs = cursor.downField("songs").values.getOrElse(Vector.empty[Json]).map(_.as[Song])
  val songs = decodedSongs.map{ eitherSongs =>
    eitherSongs match {
      case Left(failure) => {
        println(s"Failed in decoding because of $failure")
        Song(-1, "", "")
      }
      case Right(song) => song
    }}.filterNot(_.id == -1)
  //println(songs)

  //-----------------end input part
  //--------------start output part

  val outputUsers = Users(users.toVector).asJson
  val outputPlaylists = Playlists(playlists.toVector).asJson
  val outputSongs = Songs(songs.toVector).asJson
  val listResources = Json.fromValues(List(outputUsers, outputPlaylists, outputSongs)).spaces2
  Utility.writeFile(filename = "output.json", List(listResources))

  //println(inputJson)
}

object Utility{
  def writeFile(filename: String, lines: Seq[String]): Unit = {
    val file = new File(filename)
    val bw = new BufferedWriter(new FileWriter(file))
    for (line <- lines) {
      bw.write(line)
    }
    bw.close()
  }
}
