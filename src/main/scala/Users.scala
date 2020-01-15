import Main.User

import scala.collection.mutable

class Users(private val inputUsers: Iterable[User]) extends Resources {

  private var userMap: mutable.LinkedHashMap[Int, User] = new mutable.LinkedHashMap()
  for(u <- inputUsers){
    userMap += (u.id -> u)
  }

  def addUser(user: User): Unit = {
    userMap += (user.id -> user)
  }

  def removeUser(user_id: Int): Unit = {
    userMap -= user_id
  }

  def getUsers(): Vector[User] = {
    userMap.values.toVector
  }

  def printUsers(): Unit = {
    for((_, u) <- userMap){
      println(u)
    }
  }
}
