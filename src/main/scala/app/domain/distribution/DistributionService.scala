package app.domain.distribution

import app.domain.core.Repository
import app.domain.inventory.Inventory
import app.domain.users.{Admin, User}
import java.util.UUID
import java.time.LocalDate

final class DistributionService(inv: Inventory) extends Repository[Distribution]:
  private var dists: Map[String, Distribution] = Map.empty

  override def add(d: Distribution): Unit =
    dists = dists + (d.id -> d)

  override def update(d: Distribution): Unit =
    if dists.contains(d.id) then
      dists = dists.updated(d.id, d)

  override def remove(id: String): Boolean =
    val existed = dists.contains(id)
    dists = dists - id
    existed

  override def findById(id: String): Option[Distribution] =
    dists.get(id)

  override def findAll(): Seq[Distribution] =
    dists.values.toSeq

  def create(recipient: Recipient, items: Seq[DeliveryItem], by: User): Distribution =
    val d = Distribution(UUID.randomUUID().toString, LocalDate.now(), recipient, by, items)
    add(d)
    d

  def approve(id: String, by: Admin): Unit =
    dists.get(id).foreach { d =>
      update(d.copy(status = DistributionStatus.APPROVED))
    }

  def dispatch(id: String): Unit =
    dists.get(id).foreach { d =>
      update(d.copy(status = DistributionStatus.DISPATCHED))
    }

  def deliver(id: String): Unit =
    dists.get(id).foreach { d =>
      d.items.foreach { li =>
        inv.findById(li.foodItemId).foreach { fi =>
          inv.update(fi.decrement(li.quantity))
        }
      }
      update(d.copy(status = DistributionStatus.DELIVERED))
    }
