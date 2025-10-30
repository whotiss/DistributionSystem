package app.ui.views

import scalafx.Includes.*   
import scalafx.scene.layout.{BorderPane, HBox, VBox, StackPane, GridPane}
import scalafx.scene.control.{Button, Label, Tooltip, ContentDisplay}
import scalafx.scene.image.{Image, ImageView}
import scalafx.scene.Node
import scalafx.animation.{KeyFrame, Timeline}
import scalafx.util.Duration
import scalafx.geometry.{Insets, Pos}
import scalafx.scene.image.WritableImage
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import scalafx.scene.text.TextAlignment
import scalafx.scene.layout.Priority
import app.ui.views.MissionView
import app.domain.inventory.*
import app.domain.nutrition.*
import app.domain.distribution.*
import app.domain.users.*
import app.domain.community.*
import app.ui.views.CommunityView


final class SdgShellView(
                          inventory: Inventory,
                          nutrition: NutritionService,
                          distSvc:   DistributionService,
                          admin:     Admin,
                          courier:   User
                        ) extends BorderPane:

  
  private def img(path: String, w: Double = 22, h: Double = 22): ImageView =
    val opt = Option(getClass.getResource(path)).map(_.toExternalForm).map(new Image(_))
    val image = opt.getOrElse(new WritableImage(1, 1)) 
    new ImageView(image):
      fitWidth  = w
      fitHeight = h
      preserveRatio = true
      smooth = true

  private def toolbarButton(labelText: String, iconPath: String)(onClick: => Unit): Button =
    new Button(labelText, img(iconPath)):
      styleClass ++= Seq("toolbar-btn", "sdg-accent-hover")
      contentDisplay = ContentDisplay.Left
      tooltip = Tooltip(labelText)
      onAction = _ => onClick

  
  private val sdgIcon  = img("/icons/logo.png", 72, 72)
  private val title    = new Label("Zero Hunger Operations"):
    styleClass += "sdg-title"
  private val subtitle = new Label("SDG 2: End hunger, achieve food security, improve nutrition"):
    styleClass += "sdg-subtitle"

 
  private val communityBoard = new CommunityBoard
  communityBoard.seedDemo()

  private val headerLeft  = new VBox(2, title, subtitle)
  private val headerRight = new HBox(10,
    toolbarButton("Our Mission",   "/icons/sdg2.png")   { show(MissionView()) },
    toolbarButton("Inventory",     "/icons/wheat.png")  { show(InventoryView(inventory, nutrition)) },
    toolbarButton("Distributions", "/icons/van.png")    { show(DistributionView(inventory, distSvc, admin, courier)) },
    toolbarButton("Nutrition",     "/icons/leaf.png")   { show(NutritionLookupView(inventory, nutrition)) },
    toolbarButton("Reports",       "/icons/report.png"){ show(simpleReport()) },
    toolbarButton("Community",     "/icons/talking.png") {
      show(CommunityView(communityBoard, currentUserName = admin.name))
    },
    toolbarButton("Logout",        "/icons/logout.png"){ onLogout() }
  ):
    alignment = Pos.CenterRight

  private val header = new BorderPane:
    left    = new HBox(10, sdgIcon, headerLeft)
    right   = headerRight
    padding = Insets(10, 16, 10, 16)
    styleClass += "sdg-header"

  
  private val clock = new Label():
    styleClass += "kpi-clock"
  private val fmt   = DateTimeFormatter.ofPattern("EEEE, yyyy-MM-dd HH:mm:ss")

  private val timer = new Timeline:
    keyFrames = Seq(
      KeyFrame(
        Duration(1000),
        onFinished = (_: javafx.event.ActionEvent) => {
          clock.text = LocalDateTime.now().format(fmt)
        }
      )
    )
    cycleCount = Timeline.Indefinite
  timer.play()

  private def kpi(label: String, value: () => String, icon: String): VBox =
    val title = new Label(label):
      styleClass += "kpi-label"
      textAlignment = TextAlignment.Center
      alignment = Pos.Center
      wrapText = true

    val v = new Label(value()):
      styleClass += "kpi-value"
      textAlignment = TextAlignment.Center
      alignment = Pos.Center

    timer.keyFrames += KeyFrame(
      Duration(1000),
      onFinished = (_: javafx.event.ActionEvent) => {
        v.text = value()
      }
    )

    new VBox(
      new HBox(8, img(icon, 20, 20), title) { alignment = Pos.Center },
      v
    ):
      alignment = Pos.Center
      spacing = 6
      styleClass += "kpi-card"

  private val kpiTotalStock = kpi("Total Items in Stock", () => inventory.findAll().map(_.quantity).sum.toString, "/icons/box.png")
  private val kpiHouseholds = kpi("Households Served",   () => distSvc.findAll().size.toString,                 "/icons/family.png")
  private val kpiCalories   = kpi("Total Calories",      () => f"${inventory.totalCalories()}%.0f kcal",        "/icons/calories.png")
  private val kpiLowStock   = kpi("Low‑Stock Items",     () => inventory.lowStock(5).size.toString,             "/icons/alert.png")

  private val kpiGrid = new GridPane:
    hgap = 10; vgap = 10; padding = Insets(10)
    add(kpiTotalStock, 0, 0)
    add(kpiHouseholds, 1, 0)
    add(kpiCalories,   0, 1)
    add(kpiLowStock,   1, 1)

  private val leftPane = new VBox(12,
    new Label("Status • live") { styleClass += "kpi-section" },
    clock,
    kpiGrid
  ):
    alignment = Pos.TopCenter
    padding = Insets(14)
    maxWidth = 420
    styleClass += "left-kpi-pane"

  
  private val content = new StackPane:
    padding = Insets(16)
    styleClass += "content"
  show(MissionView())

 
  top    = header
  left   = leftPane
  center = content


  private def show(node: Node): Unit =
    content.children.setAll(node)

  /** Hook set externally by MainApp to handle logout. */
  var onLogout: () => Unit = () => ()


  private def simpleReport(): Node =
    new VBox(12,
      new Label("Impact Snapshot") { styleClass += "section-title" },
      new Label(s"• Distributions recorded: ${distSvc.findAll().size}")           { styleClass += "body-text" },
      new Label(f"• Calories in inventory: ${inventory.totalCalories()}%.0f kcal") { styleClass += "body-text" },
      new Label(s"• Items ≤ threshold (5): ${inventory.lowStock(5).size}")        { styleClass += "body-text" }
    ):
      alignment = Pos.TopLeft
      padding = Insets(8)
