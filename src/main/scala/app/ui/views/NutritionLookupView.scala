package app.ui.views

import scalafx.Includes.*
import scalafx.collections.ObservableBuffer
import scalafx.geometry.{Insets, Pos}
import scalafx.scene.Node
import scalafx.scene.control.*
import scalafx.scene.image.{Image, ImageView, WritableImage}
import scalafx.scene.layout.{BorderPane, VBox, HBox, Priority, GridPane, FlowPane}
import scalafx.beans.property.StringProperty
import app.domain.inventory._
import app.domain.nutrition._

object NutritionLookupView:

  def apply(inv: Inventory, nutri: NutritionService): BorderPane = {
   
    val searchField = new TextField { promptText = "Food name (e.g., Rice)" }
    val unitChoice  = new ChoiceBox[String](ObservableBuffer("kg", "L", "pcs")) { value = "kg" }
    val lookupBtn   = new Button("Lookup")             { styleClass += "primary" }
    val attachBtn   = new Button("Attach to Selected") { styleClass += "ghost"   }
    
    val invBuf = ObservableBuffer.from(inv.findAll())

    val idCol = new TableColumn[FoodItem, String]("ID") {
      prefWidth = 180
      cellValueFactory = fi => StringProperty(fi.value.id)
    }
    idCol.style = "-fx-alignment: CENTER-LEFT;"

    val nameCol = new TableColumn[FoodItem, String]("Name") {
      prefWidth = 220
      cellValueFactory = fi => StringProperty(fi.value.name)
    }
    nameCol.style = "-fx-alignment: CENTER-LEFT;"

    val kcalCol = new TableColumn[FoodItem, String]("kcal/unit") {
      prefWidth = 120
      cellValueFactory = fi =>
        StringProperty(f"${fi.value.nutrition.map(_.calories).getOrElse(0.0)}%.1f / ${fi.value.unit}")
    }
    kcalCol.style = "-fx-alignment: CENTER-RIGHT;"

    val table = new TableView[FoodItem](invBuf) {
      columns.setAll(idCol, nameCol, kcalCol)
      fixedCellSize = 32
      prefWidth = 520
      columnResizePolicy = TableView.ConstrainedResizePolicy
    }

    
    table.selectionModel().selectedItem.onChange { (_, _, sel) =>
      if sel != null then
        searchField.text = sel.name
        unitChoice.value = sel.unit
    }

    
    val titleLbl   = new Label("") { styleClass += "nutri-title" }
    val kcalBig    = new Label("") { styleClass += "nutri-kcal"  }
    val macrosFlow = new FlowPane { hgap = 10; vgap = 10; prefWrapLength = 440 }
    val microsArea = new TextArea  { editable = false; prefRowCount = 6; styleClass += "nutri-micros" }
    val imgView    = new ImageView { fitWidth = 240; preserveRatio = true; smooth = true }

    def imageFrom(path: String, fallback: String = "/icons/food_default.png"): Image = {
      val url = Option(getClass.getResource(path)).map(_.toExternalForm)
        .orElse(Option(getClass.getResource(fallback)).map(_.toExternalForm))
      url.map(new Image(_)).getOrElse(new WritableImage(1, 1))
    }

    def imgPathFor(name: String): String = {
      val n = name.toLowerCase.replaceAll("\\p{Punct}", " ").replaceAll("\\s+", " ").trim
      val key =
        if n.contains("rice") then "rice"
        else if n.contains("milk") then "milk"
        else if n.contains("banana") then "banana"
        else if n.contains("apple") then "apple"
        else if n.contains("carrot") then "carrot"
        else if (n.contains("kidney") && n.contains("bean")) || n.contains("beans kidney") then "kidney_beans"
        else if n.contains("bean") then "beans"
        else if n.contains("lentil") then "lentils"
        else if n.contains("tuna") then "tuna"
        else if n.contains("cheese") then "cheese"
        else if n.contains("yogurt") || n.contains("yoghurt") then "yogurt"
        else if n.contains("pepper") then "bell_pepper"
        else if n.contains("broccoli") then "broccoli"
        else if n.contains("dates") || n == "date" then "dates"
        else if n.contains("oats") || n == "oat" then "oats"
        else if n.contains("wheat") then "wheat_flour"
        else if n.contains("corn") || n.contains("maize") then "corn_meal"
        else if n.contains("orange") then "orange"
        else if n.contains("spinach") then "spinach"
        else if n.contains("sugar") then "sugar"
        else if n.contains("salt") then "salt"
        else if n.contains("egg")  then "eggs"
        else if n.contains("green") && n.contains("bean") then "green_beans"
        else if n.contains("onion") then "onion"
        else if n.contains("tomato") then "tomato"
        else if n.contains("potato") then "potato"
        else if n.contains("oil") then "oil"
        else "generic"
      s"/food/$key.png"
    }

    def pill(text: String): Label =
      new Label(text) {
        styleClass += "nutri-chip"
        padding = Insets(6, 12, 6, 12)
      }

    def showProfile(name: String, unit: String, p: NutritionProfile): Unit = {
      titleLbl.text = s"$name  •  unit: $unit"
      kcalBig.text  = f"${p.calories}%.1f kcal / $unit"
      macrosFlow.children.setAll(
        pill(f"Protein ${p.protein}%.1f g"),
        pill(f"Carbs ${p.carbs}%.1f g"),
        pill(f"Fat ${p.fat}%.1f g")
      )
      val microTxt =
        if p.micros.isEmpty then "Micronutrients: —"
        else p.micros.toSeq.sortBy(_._1).map { case (k, v) => s"$k: ${"%.1f".format(v)}" }.mkString("\n")
      microsArea.text = microTxt
      imgView.image = imageFrom(imgPathFor(name))
    }

    def clearCard(msg: String = "No result"): Unit = {
      titleLbl.text = ""
      kcalBig.text  = msg
      macrosFlow.children.clear()
      microsArea.text = ""
      imgView.image = imageFrom("/icons/food_default.png")
    }

    clearCard("Search a food to see details")

    var lastProfile: Option[(String, String, NutritionProfile)] = None

    
    lookupBtn.onAction = _ => {
      val typed = searchField.text.value.trim
      val sel   = Option(table.selectionModel().getSelectedItem)
      val name  = if typed.nonEmpty then typed else sel.map(_.name).getOrElse("")
      val unitV = unitChoice.value.value
      if name.isEmpty then
        lastProfile = None
        clearCard("Type a name or select a row")
      else
        val pOpt = nutri.lookup(name, unitV).orElse(nutri.lookup(name))
        pOpt match
          case Some(p) => lastProfile = Some((name, unitV, p)); showProfile(name, unitV, p)
          case None    => lastProfile = None; clearCard("No result")
    }

    
    attachBtn.onAction = _ => {
      val sel = Option(table.selectionModel().getSelectedItem)
      lastProfile match
        case Some((name, _, _)) if sel.nonEmpty =>
          val unitForRow = sel.get.unit
          val pAttach = nutri.lookup(name, unitForRow).orElse(nutri.lookup(name))
          pAttach match
            case Some(p) =>
              inv.update(sel.get.copy(nutrition = Some(p)))
              invBuf.setAll(inv.findAll()*)
              new Alert(Alert.AlertType.Information) { headerText = s"Attached as per $unitForRow." }.showAndWait()
            case None =>
              new Alert(Alert.AlertType.Warning) { headerText = "Lookup failed for the selected item." }.showAndWait()
        case _ =>
          new Alert(Alert.AlertType.Warning) { headerText = "Pick an item and lookup first." }.showAndWait()
    }

    
    val searchRow = new HBox(8, searchField, unitChoice, lookupBtn, attachBtn) {
      alignment = Pos.CenterLeft
      padding   = Insets(0, 0, 8, 0)
    }

    val headerGrid = new GridPane {
      hgap = 12; vgap = 6
      add(imgView,   0, 0, 1, 3)
      add(titleLbl,  1, 0)
      add(kcalBig,   1, 1)
      add(macrosFlow,1, 2)
    }

    val resultCard = new VBox(10,
      headerGrid,
      new Label("Micros") { styleClass += "nutri-subtitle" },
      microsArea
    ) {
      padding = Insets(14)
      styleClass += "nutri-card"
      prefWidth = 700
      maxWidth  = 800
    }

    val rightPane = new VBox(10, searchRow, resultCard) {
      padding = Insets(10)
      prefWidth = 740
      VBox.setVgrow(resultCard, Priority.Always)
    }

    val leftPane = new VBox(10,
      new Label("Inventory") { styleClass += "section-title" },
      table
    ) {
      padding = Insets(10)
      VBox.setVgrow(table, Priority.Always)
    }

    new BorderPane {
      left  = leftPane
      right = rightPane
    }
  }
