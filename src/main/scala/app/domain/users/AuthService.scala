package app.domain.users

trait AuthService:
  def login(email: String, password: String): Option[User]

final class SimpleAuthService extends AuthService:
  
  private val users: Map[String, (String, User)] = Map(
    "azan@ngo.org" -> ("admin123", Admin("u-1", "Azan Admin", "azan@ngo.org")),
    "ben@ngo.org"   -> ("vol123",   Volunteer("u-2", "Ben Volunteer", "ben@ngo.org"))
  )

  override def login(email: String, password: String): Option[User] =
    users.get(email.trim.toLowerCase).collect {
      case (pw, u) if pw == password => u
    }
