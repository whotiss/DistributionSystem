package app.domain.inventory

import app.domain.core.Repository
import app.domain.nutrition.NutritionService

final class Inventory extends Repository[FoodItem]:
  
  private var items: Map[String, FoodItem] = Map.empty

  

  override def add(item: FoodItem): Unit =
    items = items + (item.id -> item)

  override def update(item: FoodItem): Unit =
    if items.contains(item.id) then
      items = items.updated(item.id, item)

  override def remove(id: String): Boolean =
    val existed = items.contains(id)
    items = items - id
    existed

  override def findById(id: String): Option[FoodItem] =
    items.get(id)

  override def findAll(): Seq[FoodItem] =
    items.values.toSeq

  

  def findByName(name: String): Seq[FoodItem] =
    val q = name.toLowerCase
    items.values.filter(_.name.toLowerCase.contains(q)).toSeq

  def lowStock(threshold: Int): Seq[FoodItem] =
    items.values.filter(_.quantity <= threshold).toSeq

  
  def totalCalories(): Double =
    items.values.foldLeft(0.0) { (acc, fi) =>
      acc + fi.nutrition.map(_.calories * fi.quantity).getOrElse(0.0)
    }

  
  
  def recomputeNutrition(nutri: NutritionService): Unit =
    items = items.view.mapValues { fi =>
      val prof =
        fi.nutrition
          .orElse(nutri.lookup(fi.name, fi.unit)) 
          .orElse(nutri.lookup(fi.name))          
      fi.copy(nutrition = prof)
    }.toMap

  
  def refreshCalories(nutri: NutritionService): Unit =
    recomputeNutrition(nutri)
