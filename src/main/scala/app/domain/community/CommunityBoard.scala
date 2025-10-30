package app.domain.community

import java.time.LocalDateTime
import scala.util.Random

final case class Need(item: String, unit: String, qty: Int)
final case class Post(author: String, text: String, ts: LocalDateTime = LocalDateTime.now)
final case class Channel(
                          id: String,
                          name: String,
                          org: String,
                          location: String,
                          needs: Vector[Need] = Vector.empty,
                          posts: Vector[Post] = Vector.empty
                        )

final class CommunityBoard:
  private var channels: Map[String, Channel] = Map.empty

  def all(): Vector[Channel] = channels.values.toVector.sortBy(_.name.toLowerCase)

  def addChannel(name: String, org: String, location: String): Channel =
    val id = s"c-${Random.alphanumeric.take(6).mkString.toLowerCase}"
    val ch = Channel(id, name.trim, org.trim, location.trim)
    channels = channels + (id -> ch)
    ch

  def updateChannel(ch: Channel): Unit =
    if channels.contains(ch.id) then channels = channels.updated(ch.id, ch)

  def addPost(channelId: String, post: Post): Option[Channel] =
    channels.get(channelId).map { ch =>
      val updated = ch.copy(posts = ch.posts :+ post)
      channels = channels.updated(channelId, updated); updated
    }

  def addNeed(channelId: String, need: Need): Option[Channel] =
    channels.get(channelId).map { ch =>
      val updated = ch.copy(needs = ch.needs :+ need)
      channels = channels.updated(channelId, updated); updated
    }

  def seedDemo(): Unit =
    val s1 = addChannel("Shelter Merdeka", "Hope Malaysia", "Kuala Lumpur")
    val s2 = addChannel("Charity Kitchen", "FoodForAll", "Petaling Jaya")
    addNeed(s1.id, Need("Rice (white)", "kg", 120))
    addNeed(s1.id, Need("Milk (UHT)", "L", 80))
    addNeed(s2.id, Need("Canned tuna", "pcs", 200))
    addPost(s1.id, Post("Aisha", "We’re low on milk this week. Any donors nearby?"))
    addPost(s2.id, Post("Ben", "We can help with tuna deliveries on Friday."))
