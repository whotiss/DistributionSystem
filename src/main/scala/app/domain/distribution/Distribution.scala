package app.domain.distribution

import java.time.LocalDate
import app.domain.inventory.Inventory
import app.domain.users.User

final case class Distribution(
                               id: String,
                               date: LocalDate,
                               recipient: Recipient,
                               deliveredBy: User,
                               items: Seq[DeliveryItem],
                               status: DistributionStatus = DistributionStatus.PENDING
                             ):
  def totalDeliveredCalories(inv: Inventory): Double =
    items.flatMap { di =>
      inv.findById(di.foodItemId).flatMap(_.nutrition).map(_.calories * di.quantity)
    }.sum
