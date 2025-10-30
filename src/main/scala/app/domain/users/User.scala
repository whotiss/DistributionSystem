package app.domain.users

abstract class User(
                     val id: String,
                     val name: String,
                     val email: String,
                     val role: String
                   ):
  def canApprove: Boolean
