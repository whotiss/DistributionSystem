package app.domain.nutrition

final case class NutritionProfile(
                                   calories: Double,
                                   protein: Double,
                                   carbs: Double,
                                   fat: Double,
                                   micros: Map[String, Double] = Map.empty
                                 )
