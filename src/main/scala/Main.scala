import java.io.{BufferedWriter, File, FileWriter}

import cats.syntax.functor._

import io.circe._

import io.circe.generic.extras.Configuration
import io.circe.generic.extras.auto._
import io.circe.syntax._
import io.circe.parser._

import scala.io.Source

object Main extends App {

  implicit val genDevConfig: Configuration = Configuration.default.withDiscriminator("discriminator")
  sealed trait Resource
  case class Playlist(id: Int, user_id: Int, song_ids: List[Int]) extends Resource
  case class Song(id: Int, artist: String, title: String) extends Resource
  case class User(id: Int, name: String) extends Resource

  case class Operation(id: Int, action: String, resource: String, metadata: Resource)

  def handleInput(inputJson: String): (Users, Playlists, Songs) = {
    (new Users(Vector.empty), new Playlists(Vector.empty), new Songs(Vector.empty))
  }

  def handleOutput(users: Users, playlists: Playlists, songs: Songs): Unit = ???


//  implicit val userDecoder: Decoder[User] = deriveDecoder[User]
//  implicit val userEncoder: Encoder[User] = deriveEncoder[User]
//  implicit val playListDecoder: Decoder[Playlist] = deriveDecoder[Playlist]
//  implicit val playListEncoder: Encoder[Playlist] = deriveEncoder[Playlist]
//  implicit val SongDecoder: Decoder[Song] = deriveDecoder[Song]
//  implicit val SongEncoder: Encoder[Song] = deriveEncoder[Song]
//  implicit val OperationDecoder: Decoder[Operation] = deriveDecoder[Operation]
//  implicit val OperationEncoder: Encoder[Operation] = deriveEncoder[Operation]

//  object GenericDerivation {
//    implicit val encodeResource: Encoder[Resource] = Encoder.instance {
//      case song @ Song(_, _, _) => song.asJson
//      case playlist @ Playlist(_, _, _) => playlist.asJson
//      case user @ User( _, _) => user.asJson
//      case _ => throw new RuntimeException("This encoding for Resource type is not supported")
//    }
//
//    implicit val decodeResource: Decoder[Resource] = Decoder.instance {
//      List[Decoder[Resource]](
//        Decoder[Song].widen, Decoder[User].widen, Decoder[Playlist].widen
//      ).reduceLeft(_ or _)
//    }
//  }

  println("Main is running!")
  val inputFile = Source.fromFile("mixtape-data.json").getLines.mkString
  val inputJson = parse(inputFile).getOrElse(Json.Null)
  //if JsonNull then throw some exception or printout failure
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
        Playlist(-1, -1, List.empty[Int])
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

  //now need to parse the operations.

  val operationsFile = Source.fromFile("small-operations.json").getLines.mkString
  val operationsJson = parse(operationsFile).getOrElse(Json.Null)
  val opCursor = operationsJson.hcursor
  println(operationsJson)

  val decodedOperations = opCursor.downField("operations").values.get.map(_.as[Operation])
  val operations = decodedOperations.map{ eitherOp =>
    eitherOp match {
      case Left(failure) => {
        println(s"Failed in decoding because of $failure")
        Operation(-1, "", "", Song(-1, "", ""))
      }
      case Right(op) => op
    }
  }.filterNot(_.id == -1)

  println(operations)

  //-----------------end input part
  //--------------start output part

//  val outputUsers = Users(users.toVector).asJson
//  val outputPlaylists = Playlists(playlists.toVector).asJson
//  val outputSongs = Songs(songs.toVector).asJson
//  val listResources = Json.fromValues(List(outputUsers, outputPlaylists, outputSongs)).spaces2
//  Utility.writeFile(filename = "output.json", List(listResources))

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
