package hackaton


trait Api {
  def list(path: String): Seq[String]

  def createUser(username: String): Option[User]
}

case class User(username: String)
