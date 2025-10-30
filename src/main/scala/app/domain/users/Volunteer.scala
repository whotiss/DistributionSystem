package app.domain.users

final class Volunteer(
                       id: String, name: String, email: String,
                       val assignedAreas: Seq[String] = Seq.empty
                     ) extends User(id, name, email, "VOLUNTEER"):
  override def canApprove: Boolean = false
