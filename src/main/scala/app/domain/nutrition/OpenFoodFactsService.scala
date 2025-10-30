package app.domain.nutrition

final class OpenFoodFactsService extends NutritionService:

  
  private enum BaseUnit:
    case Per100g, Per100ml, PerPiece
  import BaseUnit.*

  private final case class Entry(
                                  kcal: Double,
                                  protein: Double = 0.0,
                                  carbs: Double = 0.0,
                                  fat: Double = 0.0,
                                  micros: Map[String, Double] = Map.empty,
                                  base: BaseUnit
                                )

  
  private def norm(s: String): String =
    s.toLowerCase.replaceAll("""[\p{Punct}]""", " ").replaceAll("\\s+", " ").trim

  private def singularizeToken(t: String): String =
    
    if t.endsWith("ies") && t.length > 3 then t.dropRight(3) + "y"
    else if t.endsWith("es") && t.length > 3 then t.dropRight(2)
    else if t.endsWith("s")  && t.length > 3 then t.dropRight(1)
    else t

  private def tokens(s: String): Set[String] =
    norm(s).split(" ").filter(_.nonEmpty).map(singularizeToken).toSet

  
  private val db: Map[String, Entry] = Map(
    // VEGETABLES (per 100g)
    "carrot"        -> Entry(41, 0.9, 10.0, 0.2, base = Per100g),
    "green beans"   -> Entry(31, 1.8, 7.0,  0.1, base = Per100g),
    "bell pepper"   -> Entry(31, 1.0, 6.0,  0.3, base = Per100g),
    "broccoli"      -> Entry(34, 2.8, 7.0,  0.4, base = Per100g),
    "cabbage"       -> Entry(25, 1.9, 5.0,  0.3, base = Per100g),
    "lettuce"       -> Entry(15, 1.4, 2.9,  0.2, base = Per100g),
    "tomato"        -> Entry(18, 0.9, 3.9,  0.2, base = Per100g),
    "onion"         -> Entry(40, 1.1, 9.3,  0.1, base = Per100g),
    "potato"        -> Entry(77, 2.0, 17.0, 0.1, base = Per100g),
    "cucumber"      -> Entry(16, 0.7, 3.6,  0.1, base = Per100g),
    "spinach"       -> Entry(23, 2.9, 3.6,  0.4, base = Per100g),

    // FRUITS (per 100g)
    "apple"         -> Entry(52, 0.3, 14.0, 0.2, Map("vitaminC" -> 4.6), base = Per100g),
    "orange"        -> Entry(47, 0.9, 12.0, 0.1, base = Per100g),
    "banana"        -> Entry(89, 1.1, 23.0, 0.3, base = Per100g),
    "dates"         -> Entry(282, 2.5, 75.0, 0.4, base = Per100g),
    "mango"         -> Entry(60, 0.8, 15.0, 0.4, base = Per100g),
    "grapes"        -> Entry(69, 0.7, 18.0, 0.2, base = Per100g),
    "watermelon"    -> Entry(30, 0.6, 8.0,  0.2, base = Per100g),
    "pineapple"     -> Entry(50, 0.5, 13.0, 0.1, base = Per100g),
    "pear"          -> Entry(57, 0.4, 15.0, 0.1, base = Per100g),
    "strawberry"    -> Entry(33, 0.7, 8.0,  0.3, base = Per100g),

    // STAPLES / GRAINS (per 100g, dry unless noted)
    "rice (white)"  -> Entry(360, 6.7, 80.0, 0.6, base = Per100g),
    "rice (brown)"  -> Entry(365, 7.5, 76.0, 2.7, base = Per100g),
    "wheat flour"   -> Entry(364, 10.0, 76.0, 1.0, base = Per100g),
    "oats"          -> Entry(389, 16.9, 66.0, 6.9, base = Per100g),
    "corn meal"     -> Entry(370, 9.0,  79.0, 1.7, base = Per100g),
    "pasta (dry)"   -> Entry(371, 13.0, 75.0, 1.5, base = Per100g),
    "bread (white)" -> Entry(265, 9.0, 49.0, 3.2, base = Per100g),
    "sugar"         -> Entry(387, 0.0, 100.0, 0.0, base = Per100g),

    // PROTEINS (per 100g unless per piece)
    "lentils (dry)"      -> Entry(353, 25.0, 60.0, 1.1, base = Per100g),
    "beans (dry)"        -> Entry(347, 21.0, 63.0, 1.2, base = Per100g),
    "chickpeas"    -> Entry(364, 19.0, 61.0, 6.0, base = Per100g),
    "kidney beans (dry)" -> Entry(333, 24.0, 60.0, 1.0, base = Per100g),
    "black beans (dry)"  -> Entry(339, 21.0, 63.0, 0.9, base = Per100g),
    "canned tuna"        -> Entry(132, 29.0, 0.0, 1.0, base = Per100g),
    "chicken breast"     -> Entry(165, 31.0, 0.0, 3.6, base = Per100g),
    "beef (lean)"        -> Entry(250, 26.0, 0.0, 15.0, base = Per100g),
    "fish (tilapia)"     -> Entry(129, 26.0, 0.0, 2.7, base = Per100g),
    "egg"                -> Entry(78,  6.3, 0.6, 5.3, base = PerPiece),
    "peanut butter"      -> Entry(588, 25.0, 20.0, 50.0, base = Per100g),
    "almonds"            -> Entry(579, 21.0, 22.0, 50.0, base = Per100g),

    // DAIRY / FATS
    "cheese (cheddar)"   -> Entry(403, 25.0, 1.3, 33.0, base = Per100g),
    "yogurt (plain)"     -> Entry(61,  3.5, 4.7, 3.3, base = Per100g),
    "milk (uht)"         -> Entry(60,  3.2, 4.7, 3.3, base = Per100ml), 
    "milk (skim)"        -> Entry(35,  3.4, 5.1, 0.2, base = Per100ml),
    "cooking oil"        -> Entry(884, 0.0, 0.0, 100.0, base = Per100ml), 

    // MISC
    "tea"                -> Entry(1, 0, 0, 0, base = Per100ml), 
    "baby formula"       -> Entry(514, 11.0, 57.0, 26.0, base = Per100g),
    "salt"               -> Entry(0, 0, 0, 0, base = Per100g)
  )

  
  private val aliases: Map[String, String] = Map(
    // grains & staples
    "rice" -> "rice (white)", "white rice" -> "rice (white)", "brown rice" -> "rice (brown)",
    "wheat" -> "wheat flour", "cornmeal" -> "corn meal", "maize meal" -> "corn meal",
    "oat" -> "oats", "pasta" -> "pasta (dry)", "bread" -> "bread (white)",

    // vegetables
    "capsicum" -> "bell pepper", "red pepper" -> "bell pepper", "pepper" -> "bell pepper",
    "green bean" -> "green beans", "lettuces" -> "lettuce", "tomatoes" -> "tomato",
    "onions" -> "onion", "potatoes" -> "potato", "carrots" -> "carrot", "cucumbers" -> "cucumber",

    // fruits
    "apples" -> "apple", "oranges" -> "orange", "bananas" -> "banana",
    "grape" -> "grapes", "water melons" -> "watermelon",
    "strawberries" -> "strawberry", "pears" -> "pear",

    // protein
    "beans" -> "beans (dry)", "bean" -> "beans (dry)", "lentils" -> "lentils (dry)",
    "kidney bean" -> "kidney beans (dry)", "kidney beans" -> "kidney beans (dry)",
    "black bean" -> "black beans (dry)",
    "tuna" -> "canned tuna",
    "chicken" -> "chicken breast", "beef" -> "beef (lean)", "tilapia" -> "fish (tilapia)",
    "egg carton" -> "egg", "eggs" -> "egg", "eggs tray" -> "egg", "egg tray" -> "egg",

    // dairy / fats
    "cheese" -> "cheese (cheddar)", "cheddar" -> "cheese (cheddar)",
    "yoghurt" -> "yogurt (plain)", "yogurt" -> "yogurt (plain)",
    "milk" -> "milk (uht)", "uht milk" -> "milk (uht)", "whole milk" -> "milk (uht)",
    "skim milk" -> "milk (skim)", "vegetable oil" -> "cooking oil", "cooking oils" -> "cooking oil"
  ).map { case (k, v) => norm(k) -> v }

  
  private def resolveKey(name: String): Option[Entry] =
    val n  = norm(name)
    val nt = tokens(n)

    
    db.get(n)
      
      .orElse(aliases.get(n).flatMap(db.get))
      .orElse {
        val candidates =
          db.keys.toSeq
            .map(k => (k, tokens(k)))
            .filter { case (_, kt) => kt.subsetOf(nt) || kt.intersect(nt).size >= 2 }
            .sortBy { case (k, kt) => (-kt.intersect(nt).size, -kt.size, -k.length) }
        candidates.headOption.flatMap { case (k, _) => db.get(k) }
      }

  private def scale(entry: Entry, unit: String): (Double, Double, Double, Double, Map[String, Double]) =
    val u = norm(unit)
    val factor =
      (u, entry.base) match
        case ("kg", Per100g)   => 10.0
        case ("l",  Per100ml)  => 10.0
        case ("pcs", PerPiece) => 1.0
        case ("l",  Per100g)   => 10.0
        case ("kg", PerPiece)  => 1.0
        case ("pcs", Per100g)  => 1.0
        case ("pcs", Per100ml) => 1.0
        case (_,    _)         => 1.0

    val microsScaled = entry.micros.view.mapValues(_ * factor).toMap
    (entry.kcal * factor, entry.protein * factor, entry.carbs * factor, entry.fat * factor, microsScaled)

  // -
  override def lookup(foodName: String, unit: String): Option[NutritionProfile] =
    resolveKey(foodName).map { e =>
      val u = norm(unit)

      val factor =
        (u, e.base) match
          case ("kg",  Per100g)  => 10.0
          case ("l",   Per100ml) => 10.0
          case ("pcs", PerPiece) => 1.0
          case ("pcs", Per100g)  => pieceWeightFor(foodName).map(_ / 100.0).getOrElse(1.0)
          case ("pcs", Per100ml) => 1.0
          case ("l",   Per100g)  => 10.0
          case ("kg",  PerPiece) => 1.0
          case (_,     _)        => 1.0

      val microsScaled = e.micros.view.mapValues(_ * factor).toMap
      NutritionProfile(
        calories = e.kcal * factor,
        protein  = e.protein * factor,
        carbs    = e.carbs * factor,
        fat      = e.fat * factor,
        micros   = microsScaled
      )
    }


  override def lookup(foodName: String): Option[NutritionProfile] =
    resolveKey(foodName).map { e =>
      NutritionProfile(e.kcal, e.protein, e.carbs, e.fat, e.micros)
    }


private def pieceWeightFor(name: String): Option[Double] =
  
  val n = name.toLowerCase
    .replaceAll("""[\p{Punct}]""", " ")
    .replaceAll("\\s+", " ")
    .trim

  if n.contains("banana") then Some(120.0)
  else if n.contains("apple") then Some(180.0)
  else if n.contains("orange") then Some(130.0)
  else if n.contains("egg") then Some(50.0)
  else if n.contains("onion") then Some(110.0)
  else if n.contains("potato") then Some(170.0)
  else if n.contains("tomato") then Some(120.0)
  else if n.contains("carrot") then Some(60.0)
  else if n.contains("bell") || n.contains("pepper") then Some(120.0)
  else if (n.contains("green") && n.contains("bean")) || n.contains("beans kidney") then Some(55.0)
  else None

