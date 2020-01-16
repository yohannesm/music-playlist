import java.io.{BufferedWriter, File, FileWriter}

import cats.syntax.functor._

import io.circe._

import io.circe.generic.extras.Configuration
import io.circe.generic.extras.auto._
import io.circe.syntax._
import io.circe.parser._

import scala.io.Source

object Main extends App {
  if(args.length != 3)
    {
      println("Need 3 arguments for the program")
      System.exit(0)
    }

    implicit val genDevConfig: Configuration = Configuration.default.withDiscriminator("discriminator")

    sealed trait Resource

    case class Playlist(id: Int, user_id: Int, song_ids: List[Int]) extends Resource

    case class Song(id: Int, artist: String, title: String) extends Resource

    case class User(id: Int, name: String) extends Resource

    case class OutputUsers(users: Vector[User])

    case class OutputPlayLists(playlists: Vector[Playlist])

    case class OutputSongs(songs: Vector[Song])

    case class Operation(id: Int, action: String, resource: String, metadata: Resource)

    /***********************************************************************/
    //Start of MAIN
    val (inputUsers, inputPlaylists, inputSongs) = handleInput(args(0))

    val (processedUsers, processedPlayLists, processedSongs) =
      handleOperations(operationFile = args(1), inputUsers, inputPlaylists, inputSongs)

    handleOutput(outputFile = args(2), processedUsers, processedPlayLists, processedSongs)

    //Technically end of MAIN
    /***************************************************************************/

    def handleInput(inputJson :String = "mixtape-data.json"): (Users, Playlists, Songs) = {
      val inputFile = Source.fromFile("mixtape-data.json").getLines.mkString
      val inputJson = parse(inputFile).getOrElse(Json.Null)
      //if JsonNull then throw some exception or printout failure
      val cursor: HCursor = inputJson.hcursor

      val decodedUsers = cursor.downField("users").values.getOrElse(Vector.empty[Json]).map(_.as[User])
      val users = decodedUsers.map { eitherUser =>
        eitherUser match {
          case Left(failure) => {
            println(s"Failed in decoding because of $failure")
            User(-1, "Invalid User")
          }
          case Right(user) => user
        }
      }.filterNot(_.id == -1)

      val decodedPlaylists = cursor.downField("playlists").values.getOrElse(Vector.empty[Json]).map(_.as[Playlist])
      val playlists = decodedPlaylists.map { eitherPlaylist =>
        eitherPlaylist match {
          case Left(failure) => {
            println(s"Failed in decoding because of $failure")
            Playlist(-1, -1, List.empty[Int])
          }
          case Right(playlist) => playlist
        }
      }.filterNot(_.id == -1)

      val decodedSongs = cursor.downField("songs").values.getOrElse(Vector.empty[Json]).map(_.as[Song])
      val songs = decodedSongs.map { eitherSongs =>
        eitherSongs match {
          case Left(failure) => {
            println(s"Failed in decoding because of $failure")
            Song(-1, "", "")
          }
          case Right(song) => song
        }
      }.filterNot(_.id == -1)

      (new Users(users), new Playlists(playlists), new Songs(songs))
    }

    def handleOutput(outputFile: String, users: Users, playlists: Playlists, songs: Songs): Unit = {
      val outputUsers = OutputUsers(users.getUsers()).asJson
      val outputPlaylists = OutputPlayLists(playlists.getPlaylist()).asJson
      val outputSongs = OutputSongs(songs.getSongs()).asJson
      val listResources = Json.fromValues(List(outputUsers, outputPlaylists, outputSongs)).spaces2
      Utility.writeFile(filename = outputFile, List(listResources))
    }

    def handleOperations(operationFile: String,
                       inputUsers: Users,
                       inputPlaylists: Playlists,
                       inputSongs: Songs) : (Users, Playlists, Songs) = {
      val operationsFile = Source.fromFile(operationFile).getLines.mkString
      val operationsJson = parse(operationsFile).getOrElse(Json.Null)
      val opCursor = operationsJson.hcursor

      val decodedOperations = opCursor.downField("operations").values.get.map(_.as[Operation])
      val operations = decodedOperations.map { eitherOp =>
        eitherOp match {
          case Left(failure) => {
            println(s"Failed in decoding because of $failure")
            Operation(-1, "", "", Song(-1, "", ""))
          }
          case Right(op) => op
        }
      }.filterNot(_.id == -1)

      val opEngine = new OperationsEngine(operations, inputSongs, inputUsers, inputPlaylists)
      opEngine.processOperations()
      val processedUsers = opEngine.getProcessedUsers()
      val processedPlaylists = opEngine.getProcessedPlaylists()
      val processedSongs = opEngine.getProcessedSongs()

      (processedUsers, processedPlaylists, processedSongs)
    }

   println("The program has ran successfully")
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
