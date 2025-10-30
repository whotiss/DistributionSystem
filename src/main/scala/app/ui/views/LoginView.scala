package app.ui.views

import scalafx.Includes.*
import scalafx.scene.layout.{StackPane, VBox, HBox}
import scalafx.scene.control.*
import scalafx.scene.image.{Image, ImageView, WritableImage}
import scalafx.geometry.{Insets, Pos}
import app.domain.users.{AuthService, User}

object LoginView:

  
  def apply(auth: AuthService)(onLogin: User => Unit): StackPane =
    
    val bgUrl = Option(getClass.getResource("/images/loginpic.jpg"))
      .map(_.toExternalForm)
      .getOrElse("https://picsum.photos/1600/900?blur=2")

    
    val logoView: ImageView =
      Option(getClass.getResource("/icons/logo.png"))
        .map(_.toExternalForm).map(new Image(_))
        .map(img => new ImageView(img) {
          fitWidth = 110
          preserveRatio = true
          smooth = true
        })
        .getOrElse(new ImageView(new WritableImage(1, 1)))

    
    val title = new Label("LOGIN") { styleClass += "login-title" }

    val emailField = new TextField {
      promptText = "Email"
      prefWidth = 320
    }
    val pwField = new PasswordField {
      promptText = "Password"
      prefWidth = 320
    }

    val errorLabel = new Label("") {
      styleClass += "error-text"
      visible = false
      managed = false
    }

    val loginBtn = new Button("Login") {
      defaultButton = true
      styleClass += "primary-btn"
    }

    
    val headerBox = new VBox {
      alignment = Pos.Center
      spacing = 8
      children = Seq(logoView, title)
    }
    val formBox = new VBox {
      alignment = Pos.CenterLeft
      spacing = 8
      children = Seq(
        new Label("Email:"),     emailField,
        new Label("Password:"),  pwField,
        errorLabel,
        new HBox {
          alignment = Pos.CenterLeft
          children = Seq(loginBtn)
        }
      )
    }
    val card = new VBox {
      alignment = Pos.Center
      spacing = 14
      padding = Insets(26)

      prefWidth = 420     
      maxWidth  = 420
      prefHeight = 420    
      maxHeight  = 420

      styleClass += "login-card" 
      children = Seq(headerBox, formBox)
    }

    
    val root = new StackPane {
      styleClass += "login-root"
      prefWidth = 1100
      prefHeight = 700
      children = Seq(card)
    }

    
    val bgStyle =
      s"""
         |-fx-background-image: url('$bgUrl');
         |-fx-background-repeat: no-repeat;
         |-fx-background-position: center center;
         |-fx-background-size: cover;   /* like CSS cover */
         |""".stripMargin
    root.setStyle(bgStyle)

   
    def doLogin(): Unit =
      val email = emailField.text.value.trim
      val pass  = pwField.text.value
      auth.login(email, pass) match
        case Some(u) =>
          println(s"[LOGIN] Success: ${u.email}")
          errorLabel.visible = false
          errorLabel.managed = false
          onLogin(u)
        case None =>
          errorLabel.text = "Invalid email or password"
          errorLabel.visible = true
          errorLabel.managed = true

    loginBtn.onAction = _ => doLogin()
    pwField.onAction  = _ => doLogin()

    root
