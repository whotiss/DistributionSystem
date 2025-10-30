package app.domain.distribution

final case class Recipient(
                            id: String,
                            name: String,
                            householdSize: Int,
                            priorityScore: Int
                          )
