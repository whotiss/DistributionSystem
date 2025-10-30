package app.domain.users

final class Admin(id: String, name: String, email: String)
  extends User(id, name, email, "ADMIN"):
  override def canApprove: Boolean = true
  def generateReports(): Unit = ()
