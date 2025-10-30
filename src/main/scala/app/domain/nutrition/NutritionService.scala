package app.domain.nutrition

trait NutritionService:
  
  def lookup(foodName: String): Option[NutritionProfile]
  
  def lookup(foodName: String, unit: String): Option[NutritionProfile] =
    
    lookup(foodName)
