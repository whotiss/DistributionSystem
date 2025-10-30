package app.domain.inventory

import java.time.LocalDate
import app.domain.nutrition.NutritionProfile

final case class FoodItem(
                           id: String,
                           name: String,
                           category: FoodCategory,
                           expiryDate: LocalDate,
                           unit: String,
                           quantity: Int,
                           batchId: String,
                           nutrition: Option[NutritionProfile] = None
                         ):
  def isExpired(today: LocalDate = LocalDate.now()): Boolean =
    !expiryDate.isAfter(today) 
  def decrement(qty: Int): FoodItem =
    copy(quantity = (quantity - qty).max(0))
