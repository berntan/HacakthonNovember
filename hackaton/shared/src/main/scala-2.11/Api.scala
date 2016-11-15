package hackaton

trait Api {
  def list(path: String): Seq[String]
}
