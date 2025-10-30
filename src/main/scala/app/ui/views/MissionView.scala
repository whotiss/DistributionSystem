package app.ui.views

import scalafx.Includes.*
import scalafx.scene.Node
import scalafx.scene.control.{ScrollPane, Label, Hyperlink}
import scalafx.scene.image.{Image, ImageView, WritableImage}
import scalafx.scene.layout.{VBox, HBox, GridPane, ColumnConstraints, Priority}
import scalafx.geometry.{Insets, Pos}
import java.net.URI


object MissionView:

  
  private def defaultOpen(url: String): Unit =
    try
      val d = java.awt.Desktop.getDesktop
      if d.isSupported(java.awt.Desktop.Action.BROWSE) then d.browse(new URI(url))
      else println(s"[OpenURL] Desktop browse not supported: $url")
    catch
      case t: Throwable => t.printStackTrace()

 
  def apply(openUrl: String => Unit = defaultOpen): Node =
    
    val heroImgView: ImageView =
      Option(getClass.getResource("/images/mission-hero.jpg"))
        .map(_.toExternalForm).map(new Image(_))
        .map(img => new ImageView(img) {
          fitWidth = 740; preserveRatio = true; smooth = true
        })
        .getOrElse(new ImageView(new WritableImage(1,1)))

    val badge = new Label("SDG 2 • Zero Hunger") { styleClass += "pill" }

    val title = new Label("End Hunger, Achieve Food Security & Improve Nutrition") {
      styleClass += "mission-title"; wrapText = true
    }

    val subtitle = new Label(
      "We coordinate food inventory, equitable distribution, and nutrition insights to support households at risk."
    ) { styleClass += "mission-subtitle"; wrapText = true }

    
    val factsLink = new Hyperlink("Read the official SDG‑2 factsheet") {
      styleClass ++= Seq("link-button", "sdg-link")
      onAction = _ => openUrl("https://unstats.un.org/sdgs/report/2022/goal-02/")
    }
    val linkFooter = new HBox(factsLink) {
      alignment = Pos.Center
      padding = Insets(10, 0, 0, 0)
    }

    
    val hero = new VBox(10,
      new VBox(6, badge, title, subtitle) {
        alignment = Pos.TopLeft
        padding = Insets(10, 0, 10, 0)
        maxWidth = 700
      },
      heroImgView,
      linkFooter
    ) {
      alignment = Pos.Center
      padding = Insets(20, 10, 20, 10)
      styleClass += "mission-hero"
    }

    
    val impactGrid = new GridPane {
      hgap = 12; vgap = 12; padding = Insets(6)
      
      columnConstraints ++= Seq(
        new ColumnConstraints { percentWidth = 50; hgrow = Priority.Always },
        new ColumnConstraints { percentWidth = 50; hgrow = Priority.Always }
      )

      
      val c00 = impactCard("47% of countries", "faced soaring food prices in 2020")
      val c10 = impactCard("~1 in 10 people", "suffer from hunger worldwide")
      add(c00, 0, 0); add(c10, 1, 0)

      
      val c01 = impactCard("149.2M children", "under 5 suffer from stunting")
      val c11 = impactCard("Goal by 2030", "halve stunting & expand food access")
      add(c01, 0, 1); add(c11, 1, 1)

      
      val c02 = impactCard("3.1B people", "could not afford a healthy diet in 2020")
      val c12 = impactCard("70% of food insecure", "live in rural areas reliant on farming")
      add(c02, 0, 2); add(c12, 1, 2)

      
      Seq(c00, c10, c01, c11, c02, c12).foreach(n => GridPane.setHgrow(n, Priority.Always))
    }

    val body = new VBox(16,
      hero,
      new Label("Our Approach") { styleClass += "section-title" },
      bulletRow(
        "/icons/wheat.png",
        "Secure, nutritious supply",
        "Partner with donors to source grains, proteins, dairy and fresh produce; track expiry & nutrition."
      ),
      bulletRow(
        "/icons/van.png",
        "Equitable distribution",
        "Deliver by priority and need; audit stock decrements and delivery outcomes."
      ),
      bulletRow(
        "/icons/leaf.png",
        "Nutrition‑aware choices",
        "Use nutrition lookup to maximize calories and micronutrients for each household."
      ),
      new Label("Global Context") { styleClass += "section-title" },
      impactGrid
    ) { alignment = Pos.TopLeft; padding = Insets(16, 16, 24, 16) }

    new ScrollPane {
      fitToWidth = true
      content = body
      styleClass += "mission-scroll"
    }

  
  private def impactCard(h: String, p: String): VBox =
    new VBox(
      new Label(h)  { styleClass += "impact-head"; wrapText = true },
      new Label(p)  { styleClass += "impact-body"; wrapText = true }
    ) {
      styleClass += "impact-card"
      alignment = Pos.CenterLeft
      padding = Insets(14)
      maxWidth = Double.MaxValue
    }

  private def bulletRow(iconPath: String, head: String, body: String): HBox =
    val icon = Option(getClass.getResource(iconPath))
      .map(_.toExternalForm).map(new Image(_))
      .map(img => new ImageView(img){ fitWidth = 26; preserveRatio = true })
      .getOrElse(new ImageView(new WritableImage(1,1)))
    new HBox(12,
      icon,
      new VBox(
        new Label(head){ styleClass += "bullet-head"; wrapText = true },
        new Label(body){ styleClass += "bullet-body"; wrapText = true }
      )
    ) { alignment = Pos.TopLeft; styleClass += "bullet-row" }
