package app.ui.views

import scalafx.scene.layout.{VBox, HBox, Priority}
import scalafx.scene.control._
import scalafx.collections.ObservableBuffer
import scalafx.geometry.{Insets, Pos}
import app.domain.inventory._
import app.domain.nutrition.NutritionService
import java.time.LocalDate
import java.time.format.DateTimeFormatter

object InventoryView:
  private val dateFmt = DateTimeFormatter.ofPattern("yyyy-MM-dd")

  def apply(inv: Inventory, nutri: NutritionService): VBox =
    val itemsBuf = ObservableBuffer.from(inv.findAll())
    
    val idCol = new TableColumn[FoodItem, String]:
      text = "ID"; prefWidth = 160
      cellValueFactory = fi => scalafx.beans.property.StringProperty(fi.value.id)
    idCol.style = "-fx-alignment: CENTER-LEFT;"

    val nameCol = new TableColumn[FoodItem, String]:
      text = "Name"; prefWidth = 160
      cellValueFactory = fi => scalafx.beans.property.StringProperty(fi.value.name)
    nameCol.style = "-fx-alignment: CENTER-LEFT;"

    val categoryCol = new TableColumn[FoodItem, String]:
      text = "Category"; prefWidth = 110
      cellValueFactory = fi => scalafx.beans.property.StringProperty(fi.value.category.toString)
    categoryCol.style = "-fx-alignment: CENTER;"

    val expiryCol = new TableColumn[FoodItem, String]:
      text = "Expiry"; prefWidth = 110
      cellValueFactory = fi => scalafx.beans.property.StringProperty(fi.value.expiryDate.format(dateFmt))
    expiryCol.style = "-fx-alignment: CENTER;"

    val unitCol = new TableColumn[FoodItem, String]:
      text = "Unit"; prefWidth = 70
      cellValueFactory = fi => scalafx.beans.property.StringProperty(fi.value.unit)
    unitCol.style = "-fx-alignment: CENTER;"

    val qtyCol = new TableColumn[FoodItem, String]:
      text = "Qty"; prefWidth = 70
      cellValueFactory = fi => scalafx.beans.property.StringProperty(fi.value.quantity.toString)
    qtyCol.style = "-fx-alignment: CENTER-RIGHT;"

    val kcalCol = new TableColumn[FoodItem, String]:
      text = "kcal/unit" 
      prefWidth = 120
      cellValueFactory = fi => scalafx.beans.property.StringProperty(
        f"${fi.value.nutrition.map(_.calories).getOrElse(0.0)}%.1f / ${fi.value.unit}"
      )
    kcalCol.style = "-fx-alignment: CENTER-RIGHT;"

    val table = new TableView[FoodItem](itemsBuf):
      columns.setAll(idCol, nameCol, categoryCol, expiryCol, unitCol, qtyCol, kcalCol)
      fixedCellSize = 32
      prefWidth  = 1100
      prefHeight = 520
      columnResizePolicy = TableView.ConstrainedResizePolicy

    
    val idField     = new TextField { promptText = "ID (e.g., f-101)"; prefWidth = 140 }
    val nameField   = new TextField { promptText = "Name" }
    val catChoice   = new ChoiceBox[FoodCategory](ObservableBuffer(FoodCategory.values*)):
      value = FoodCategory.OTHER
    val expiryField = new TextField { promptText = "yyyy-MM-dd" }
    val unitField   = new TextField { promptText = "Unit (kg/pcs/L)" }
    val qtyField    = new TextField { promptText = "Qty" }

    val addBtn          = new Button("Add")
    val rmBtn           = new Button("Remove Selected")
    val refreshNutriBtn = new Button("Refresh Nutrition")
    
    addBtn.styleClass          += "primary"
    rmBtn.styleClass           += "danger"
    refreshNutriBtn.styleClass += "ghost"

    addBtn.onAction = _ =>
      val id = idField.text.value.trim
      val n  = nameField.text.value.trim
      val ct = catChoice.value.value
      val ex = expiryField.text.value.trim
      val un = unitField.text.value.trim
      val qt = qtyField.text.value.trim

      
      val idOk   = id.nonEmpty && id.matches("""[A-Za-z0-9_\-]+""")
      val dateOk = ex.matches("""\d{4}-\d{2}-\d{2}""")
      val qtyOk  = qt.nonEmpty && qt.forall(_.isDigit)

      if !idOk then
        warn("Please enter a valid ID (letters, numbers, _ or -).")
      else if inv.findById(id).nonEmpty then
        warn(s"ID '$id' already exists. Please choose another.")
      else if n.isEmpty || !dateOk || !qtyOk then
        warn("Please check name, expiry format (yyyy-MM-dd), and quantity.")
      else
        val item = FoodItem(
          id = id,
          name = n,
          category = ct,
          expiryDate = LocalDate.parse(ex, dateFmt),
          unit = un,
          quantity = qt.toInt,
          batchId = s"b-${System.currentTimeMillis()}"
        )
        inv.add(item)
        
        inv.update(item.copy(nutrition = nutri.lookup(item.name)))
        itemsBuf.setAll(inv.findAll()*)
        idField.clear(); nameField.clear(); expiryField.clear(); unitField.clear(); qtyField.clear()

    rmBtn.onAction = _ =>
      Option(table.selectionModel().getSelectedItem).foreach { sel =>
        inv.remove(sel.id)
        itemsBuf.setAll(inv.findAll()*)
      }

    refreshNutriBtn.onAction = _ =>
      inv.recomputeNutrition(nutri)
      itemsBuf.setAll(inv.findAll()*)

    val totalsLabel = new Label(s"Total calories (qty-weighted): ${inv.totalCalories()}")
    totalsLabel.styleClass += "totals-label"

    
    table.items.onChange {
      totalsLabel.text = s"Total calories (qty-weighted): ${inv.totalCalories()}"
    }

    new VBox:
      spacing = 10
      padding = Insets(10)
      children = Seq(
        new HBox:
          spacing = 8; alignment = Pos.CenterLeft
          children = Seq(
            new Label("ID:"), idField,
            new Label("Name:"), nameField,
            new Label("Category:"), catChoice,
            new Label("Expiry:"), expiryField,
            new Label("Unit:"), unitField,
            new Label("Qty:"), qtyField,
            addBtn, rmBtn, refreshNutriBtn
          )
        ,
        table,
        totalsLabel
      )
      
      VBox.setVgrow(table, Priority.Always)

  private def warn(msg: String): Unit =
    new Alert(Alert.AlertType.Warning) { headerText = msg }.showAndWait()
