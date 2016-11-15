package hackaton


trait Api {
  def list(filter:String): List[User]

  def createUser(username: String): User
}

case class User(username: String)
