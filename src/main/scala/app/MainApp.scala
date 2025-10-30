package app

import scalafx.application.JFXApp3
import scalafx.application.Platform
import scalafx.scene.Scene
import scalafx.scene.control.{Alert, Tab, TabPane}
import scalafx.geometry.Insets
import scalafx.scene.image.Image
import java.time.LocalDate
import app.domain.inventory._
import app.domain.nutrition._
import app.domain.distribution._
import app.domain.users._
import app.ui.views.{InventoryView, DistributionView, NutritionLookupView, LoginView, SdgShellView}

object MainApp extends JFXApp3:

  
  val inventory = new Inventory
  val nutritionService: NutritionService = new OpenFoodFactsService
  val distService = new DistributionService(inventory)
  val admin     = Admin("u-1", "Azan Admin", "azan@ngo.org")
  val volunteer = Volunteer("u-2", "Ben Volunteer", "ben@ngo.org")

  // Auth
  val auth: AuthService = new SimpleAuthService
  override def start(): Unit =
    
    val loginRoot = LoginView(auth) { _user =>
      showAppAfterLogin()
    }

    stage = new JFXApp3.PrimaryStage:
      title = "Zero Hunger — Food Distribution Manager"
      maximized = true
      
      icons += new Image(getClass.getResourceAsStream("/icons/logo.png"))
      scene = new Scene:
        root = loginRoot

    
    Option(getClass.getResource("/app.css")).foreach { url =>
      stage.getScene.getStylesheets.add(url.toExternalForm)
    }

  
  private def showAppAfterLogin(): Unit =
    try
      seedDemo() 

      val shell = new SdgShellView(inventory, nutritionService, distService, admin, volunteer)
      shell.onLogout = () => Platform.runLater(start())

      
      Platform.runLater {
        stage.getScene.setRoot(shell)
        
        Option(getClass.getResource("/app.css")).foreach { url =>
          val css = url.toExternalForm
          if !stage.getScene.getStylesheets.contains(css) then
            stage.getScene.getStylesheets.add(css)
        }
        println("[NAV] Switched to SDG shell")
      }
    catch
      case t: Throwable =>
        t.printStackTrace()
        new Alert(Alert.AlertType.Error) {
          headerText = "Failed to open main window"
          contentText = Option(t.getMessage).getOrElse("Unknown error")
        }.showAndWait()

  
  private def seedDemo(): Unit =
    val today = LocalDate.now()

    val items = Seq(
      // -------- GRAINS --------
      FoodItem("f-101","Rice (white)",  FoodCategory.GRAIN,     today.plusDays(360), "kg", 420, "b-240801"),
      FoodItem("f-102","Rice (brown)",  FoodCategory.GRAIN,     today.plusDays(340), "kg", 180, "b-240802"),
      FoodItem("f-103","Wheat flour",   FoodCategory.GRAIN,     today.plusDays(300), "kg", 250, "b-240803"),
      FoodItem("f-104","Oats",          FoodCategory.GRAIN,     today.plusDays(420), "kg", 110, "b-240804"),
      FoodItem("f-105","Corn meal",     FoodCategory.GRAIN,     today.plusDays(380), "kg", 90,  "b-240805"),

      // ------ VEGETABLES ------
      FoodItem("f-201","Carrot",        FoodCategory.VEGETABLE, today.plusDays(10),  "kg", 60,  "b-240901"),
      FoodItem("f-202","Potato",        FoodCategory.VEGETABLE, today.plusDays(40),  "kg", 210, "b-240902"),
      FoodItem("f-203","Onion",         FoodCategory.VEGETABLE, today.plusDays(35),  "kg", 150, "b-240903"),
      FoodItem("f-204","Tomato",        FoodCategory.VEGETABLE, today.plusDays(7),   "kg", 55,  "b-240904"),
      FoodItem("f-205","Cabbage",       FoodCategory.VEGETABLE, today.plusDays(9),   "pcs",38,  "b-240905"),
      FoodItem("f-206","Spinach",       FoodCategory.VEGETABLE, today.plusDays(5),   "kg", 22,  "b-240906"),
      FoodItem("f-207","Green beans",   FoodCategory.VEGETABLE, today.plusDays(8),   "kg", 28,  "b-240907"),

      // -------- FRUITS --------
      FoodItem("f-301","Apple",         FoodCategory.FRUIT,     today.plusDays(20),  "pcs",220, "b-240910"),
      FoodItem("f-302","Banana",        FoodCategory.FRUIT,     today.plusDays(6),   "kg", 70,  "b-240911"),
      FoodItem("f-303","Orange",        FoodCategory.FRUIT,     today.plusDays(18),  "pcs",180, "b-240912"),
      FoodItem("f-304","Dates",         FoodCategory.FRUIT,     today.plusDays(240), "kg", 45,  "b-240913"),

      // ---- PROTEIN/LEGUMES ----
      FoodItem("f-401","Beans (kidney)",FoodCategory.PROTEIN,   today.plusDays(480), "kg", 190, "b-240920"),
      FoodItem("f-402","Chickpeas",     FoodCategory.PROTEIN,   today.plusDays(460), "kg", 160, "b-240921"),
      FoodItem("f-403","Lentils",       FoodCategory.PROTEIN,   today.plusDays(500), "kg", 210, "b-240922"),
      FoodItem("f-404","Canned tuna",   FoodCategory.PROTEIN,   today.plusDays(720), "pcs",140, "b-240923"),
      FoodItem("f-405","Eggs (tray)",   FoodCategory.PROTEIN,   today.plusDays(14),  "pcs",120, "b-240924"),

      // ---------- DAIRY --------
      FoodItem("f-501","Milk (UHT)",    FoodCategory.DAIRY,     today.plusDays(180), "L",  260, "b-240930"),
      FoodItem("f-502","Yogurt",        FoodCategory.DAIRY,     today.plusDays(12),  "pcs",90,  "b-240931"),
      FoodItem("f-503","Cheese",        FoodCategory.DAIRY,     today.plusDays(40),  "kg", 35,  "b-240932"),

      // ------- OTHER/STAPLES ----
      FoodItem("f-601","Cooking oil",   FoodCategory.OTHER,     today.plusDays(540), "L",  180, "b-241001"),
      FoodItem("f-602","Sugar",         FoodCategory.OTHER,     today.plusDays(540), "kg", 130, "b-241002"),
      FoodItem("f-603","Salt",          FoodCategory.OTHER,     today.plusDays(720), "kg", 90,  "b-241003"),
      FoodItem("f-604","Tea",           FoodCategory.OTHER,     today.plusDays(720), "pcs",70,  "b-241004"),
      FoodItem("f-605","Baby formula",  FoodCategory.OTHER,     today.plusDays(120), "pcs",34,  "b-241005"),
      FoodItem("f-606","Sanitary pads", FoodCategory.OTHER,     today.plusDays(720), "pcs",160, "b-241006")
    )

    
    items.foreach(inventory.add)
    inventory.recomputeNutrition(nutritionService)

    
    seedDistributions()

  
  private def seedDistributions(): Unit =
    val recipients = Seq(
      Recipient("r-101","Family Ali",        5, 90),
      Recipient("r-102","Family Noor",       4, 88),
      Recipient("r-103","Family Omar",       6, 82),
      Recipient("r-104","Shelter Hope",     30, 80),
      Recipient("r-105","Senior Center",    18, 77),
      Recipient("r-106","Women Support Org",25, 86),
      Recipient("r-107","Community Kitchen",40, 92),
      Recipient("r-108","Family Sara",       3, 84),
      Recipient("r-109","Family Musa",       7, 78),
      Recipient("r-110","Youth Hostel",     20, 75)
    )

    def pack(items: (String, Int)*): Seq[DeliveryItem] =
      items.map { case (id, q) => DeliveryItem(id, q) }

    val d1  = distService.create(recipients(0), pack("f-101"->20, "f-401"->10, "f-201"->5, "f-301"->12), volunteer)
    val d2  = distService.create(recipients(1), pack("f-102"->15, "f-403"->10, "f-202"->10, "f-501"->6), volunteer)
    val d3  = distService.create(recipients(2), pack("f-103"->20, "f-402"->12, "f-205"->4,  "f-303"->10), volunteer)
    val d4  = distService.create(recipients(3), pack("f-101"->60, "f-401"->30, "f-501"->40, "f-601"->20), volunteer)
    val d5  = distService.create(recipients(4), pack("f-104"->10, "f-404"->12, "f-503"->4,  "f-304"->6),  volunteer)
    val d6  = distService.create(recipients(5), pack("f-105"->10, "f-402"->10, "f-202"->12, "f-301"->15), volunteer)
    val d7  = distService.create(recipients(6), pack("f-101"->90, "f-103"->40, "f-403"->30, "f-601"->40), volunteer)
    val d8  = distService.create(recipients(7), pack("f-102"->10, "f-206"->4,  "f-502"->6,  "f-605"->2),  volunteer)
    val d9  = distService.create(recipients(8), pack("f-103"->15, "f-405"->30, "f-204"->6,  "f-302"->6),  volunteer)
    val d10 = distService.create(recipients(9), pack("f-104"->25, "f-404"->24, "f-501"->30, "f-602"->15), volunteer)

   
    Seq(d1, d2, d3, d4, d5, d6).foreach { d =>
      distService.approve(d.id, admin)
      distService.dispatch(d.id)
      distService.deliver(d.id)
    }
    
    distService.approve(d7.id, admin); distService.dispatch(d7.id)
    distService.approve(d8.id, admin)

