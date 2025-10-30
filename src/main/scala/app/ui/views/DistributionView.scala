package app.ui.views

import scalafx.scene.layout.{VBox, HBox}
import scalafx.scene.control._
import scalafx.collections.ObservableBuffer
import scalafx.geometry.{Insets, Pos}
import scalafx.util.StringConverter
import app.domain.inventory._
import app.domain.distribution._
import app.domain.users._

object DistributionView:

  final case class LineItemRow(foodId: String, foodName: String, qty: Int)

  def apply(inv: Inventory, svc: DistributionService, admin: Admin, courier: User): VBox =
    val recipients = ObservableBuffer(
      Recipient("r-1", "Family Ali", 5, 90),
      Recipient("r-2", "Shelter Hope", 30, 80),
      Recipient("r-3", "Senior Center", 15, 60)
    )

    val invBuf = ObservableBuffer.from(inv.findAll())

    val recipientChoice = new ChoiceBox[Recipient](recipients):
      value = recipients.headOption.orNull

    val foodChoice = new ChoiceBox[FoodItem](invBuf):
      converter = FoodItemString

    val qtyField = new TextField:
      promptText = "Qty"
      prefWidth = 70

    val addLineBtn = new Button("Add Item") { styleClass += "primary" }

    val lineItems = ObservableBuffer[LineItemRow]()
    
    val foodCol = new TableColumn[LineItemRow, String]:
      text = "Food"; prefWidth = 200
      cellValueFactory = r => scalafx.beans.property.StringProperty(r.value.foodName)
    foodCol.style = "-fx-alignment: CENTER-LEFT;"

    val qtyCol = new TableColumn[LineItemRow, String]:
      text = "Qty";  prefWidth = 80
      cellValueFactory = r => scalafx.beans.property.StringProperty(r.value.qty.toString)
    qtyCol.style = "-fx-alignment: CENTER-RIGHT;"

    val lineTable = new TableView[LineItemRow](lineItems):
      columns.setAll(foodCol, qtyCol)
      fixedCellSize = 32

    addLineBtn.onAction = _ =>
      val fi = foodChoice.value.value
      val qt = qtyField.text.value.trim
      if fi != null && qt.nonEmpty && qt.forall(_.isDigit) then
        if qt.toInt <= fi.quantity then
          lineItems += LineItemRow(fi.id, fi.name, qt.toInt)
          qtyField.clear()
        else warn("Insufficient stock.")
      else warn("Select a food and enter a numeric quantity.")

    val createBtn   = new Button("Create")   { styleClass += "primary" }
    val approveBtn  = new Button("Approve")  { styleClass += "primary" }
    val dispatchBtn = new Button("Dispatch") { styleClass += "ghost"   }
    val deliverBtn  = new Button("Deliver")  { styleClass += "primary" }

    val distBuf = ObservableBuffer[Distribution]()
    
    val distIdCol = new TableColumn[Distribution, String]:
      text = "ID"; prefWidth = 200
      cellValueFactory = d => scalafx.beans.property.StringProperty(d.value.id)
    distIdCol.style = "-fx-alignment: CENTER-LEFT;"

    val recCol = new TableColumn[Distribution, String]:
      text = "Recipient"; prefWidth = 180
      cellValueFactory = d => scalafx.beans.property.StringProperty(d.value.recipient.name)
    recCol.style = "-fx-alignment: CENTER-LEFT;"

    val statusCol = new TableColumn[Distribution, String]:
      text = "Status"; prefWidth = 120
      cellValueFactory = d => scalafx.beans.property.StringProperty(d.value.status.toString)
    statusCol.style = "-fx-alignment: CENTER;"

    val distTable = new TableView[Distribution](distBuf):
      columns.setAll(distIdCol, recCol, statusCol)
      fixedCellSize = 32

    def refresh(): Unit =
      distBuf.setAll(svc.findAll()*)

    createBtn.onAction = _ =>
      val rec = recipientChoice.value.value
      if rec == null || lineItems.isEmpty then
        warn("Pick a recipient and add at least one item.")
      else
        val items = lineItems.map(li => DeliveryItem(li.foodId, li.qty)).toSeq
        val d = svc.create(rec, items, courier)
        distBuf += d
        lineItems.clear()

    approveBtn.onAction = _ =>
      Option(distTable.selectionModel().getSelectedItem).foreach { d =>
        svc.approve(d.id, admin); refresh()
      }

    dispatchBtn.onAction = _ =>
      Option(distTable.selectionModel().getSelectedItem).foreach { d =>
        svc.dispatch(d.id); refresh()
      }

    deliverBtn.onAction = _ =>
      Option(distTable.selectionModel().getSelectedItem).foreach { d =>
        svc.deliver(d.id)   
        refresh()
        invBuf.setAll(inv.findAll()*)
      }

    refresh()

    new VBox:
      spacing = 10
      padding = Insets(10)
      children = Seq(
        new HBox:
          spacing = 8; alignment = Pos.CenterLeft
          children = Seq(
            new Label("Recipient:"), recipientChoice,
            new Label("Food:"), foodChoice,
            new Label("Qty:"), qtyField, addLineBtn,
            new Label("|"), createBtn, approveBtn, dispatchBtn, deliverBtn
          )
        ,
        new Label("Planned Items:"), lineTable,
        new Label("Distributions:"), distTable
      )

  private def warn(msg: String): Unit =
    new Alert(Alert.AlertType.Warning) { headerText = msg }.showAndWait()

  private object FoodItemString extends StringConverter[FoodItem]:
    override def toString(fi: FoodItem): String =
      if fi == null then "" else s"${fi.name} (${fi.quantity} ${fi.unit} available)"
    override def fromString(_s: String): FoodItem = null
