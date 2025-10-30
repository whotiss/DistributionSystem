package app.ui.views

import scalafx.Includes.*
import scalafx.scene.layout.{BorderPane, VBox, HBox, Priority}
import scalafx.scene.control._
import scalafx.collections.ObservableBuffer
import scalafx.geometry.{Insets, Pos}
import java.time.format.DateTimeFormatter
import app.domain.community._
import scalafx.beans.property.StringProperty
import scalafx.scene.control.{ListCell, ListView, TableView, TableColumn}

object CommunityView:

  private val tsFmt = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm")

  def apply(board: CommunityBoard, currentUserName: String = "Guest"): BorderPane =
    val channelsBuf = ObservableBuffer.from(board.all())

    
    val chList = new ListView[Channel](channelsBuf):
      prefWidth = 260
      cellFactory = { (_: ListView[Channel]) =>
        new ListCell[Channel]:
          item.onChange { (_, _, ch) =>
            text = Option(ch)
              .map(c => s"${c.name}\n${c.org} • ${c.location}")
              .getOrElse("")
          }
      }

    val newName = new TextField { promptText = "Shelter / Charity name" }
    val newOrg  = new TextField { promptText = "Organisation" }
    val newLoc  = new TextField { promptText = "Location" }
    val addChBtn= new Button("Add") { styleClass += "primary" }
    addChBtn.onAction = _ =>
      val n = newName.text.value.trim; val o = newOrg.text.value.trim; val l = newLoc.text.value.trim
      if n.nonEmpty && o.nonEmpty && l.nonEmpty then
        board.addChannel(n, o, l)
        channelsBuf.setAll(board.all()*)
        newName.clear(); newOrg.clear(); newLoc.clear()
      else
        new Alert(Alert.AlertType.Warning){ headerText = "Fill name, organisation, and location." }.showAndWait()

    val leftPane = new VBox(8,
      new Label("Communities"),
      chList,
      new Separator(),
      new Label("New community:"),
      newName, newOrg, newLoc, new HBox(8, addChBtn)
    )
    leftPane.padding = Insets(12)
    VBox.setVgrow(chList, Priority.Always)

    
    val needsBuf = ObservableBuffer[Need]()
    val needItem = new TextField { promptText = "Item (e.g., Rice)" }
    val needUnit = new TextField { promptText = "Unit (kg/L/pcs)"; prefWidth = 110 }
    val needQty  = new TextField { promptText = "Qty"; prefWidth = 90 }
    val addNeedBtn = new Button("Add Need") { styleClass += "ghost" }

    val needsTable = new TableView[Need](needsBuf):
      columnResizePolicy = TableView.ConstrainedResizePolicy
      prefWidth = 320
      fixedCellSize = 28
      columns ++= Seq(
        new TableColumn[Need, String]("Item") { cellValueFactory = n => StringProperty(n.value.item); minWidth = 140 },
        new TableColumn[Need, String]("Unit") { cellValueFactory = n => StringProperty(n.value.unit); minWidth = 80 },
        new TableColumn[Need, String]("Qty")  { cellValueFactory = n => StringProperty(n.value.qty.toString); minWidth = 80 }
      )

    addNeedBtn.onAction = _ =>
      val it = needItem.text.value.trim
      val un = needUnit.text.value.trim
      val qt = needQty.text.value.trim
      val sel = Option(chList.selectionModel().getSelectedItem)
      if it.nonEmpty && un.nonEmpty && qt.forall(_.isDigit) && sel.nonEmpty then
        board.addNeed(sel.get.id, Need(it, un, qt.toInt))
        refreshRight(sel.get, needsBuf)
        needItem.clear(); needUnit.clear(); needQty.clear()
      else
        new Alert(Alert.AlertType.Warning){ headerText = "Pick a community and fill item/unit/qty." }.showAndWait()

    val rightPane = new VBox(8,
      new Label("Needs"),
      needsTable,
      new HBox(8, needItem, needUnit, needQty, addNeedBtn)
    )
    rightPane.padding = Insets(12)
    VBox.setVgrow(needsTable, Priority.Always)

    
    val postsBuf = ObservableBuffer[Post]()
    val postsList = new ListView[Post](postsBuf):
      fixedCellSize = 28
      
      cellFactory = { (_: ListView[Post]) =>
        new ListCell[Post]:
          item.onChange { (_, _, p) =>
            text = Option(p)
              .map(pp => s"[${pp.ts.format(tsFmt)}] ${pp.author}: ${pp.text}")
              .getOrElse("")
          }
      }

    val msgField = new TextField { promptText = "Share updates (what’s needed, delivery timing, etc.)" }
    val sendBtn  = new Button("Post") { styleClass += "primary" }
    sendBtn.onAction = _ =>
      val txt = msgField.text.value.trim
      val sel = Option(chList.selectionModel().getSelectedItem)
      if txt.nonEmpty && sel.nonEmpty then
        board.addPost(sel.get.id, Post(currentUserName, txt))
        refreshCenter(sel.get, postsBuf)
        msgField.clear()
      else
        new Alert(Alert.AlertType.Warning){ headerText = "Pick a community and type a message." }.showAndWait()

    val centerPane = new VBox(8,
      new Label("Discussion"),
      postsList,
      new HBox(8, msgField, sendBtn)
    )
    centerPane.padding = Insets(12)
    VBox.setVgrow(postsList, Priority.Always)

    
    chList.selectionModel().selectedItem.onChange { (_, _, ch) =>
      Option(ch).foreach { c =>
        refreshCenter(c, postsBuf)
        refreshRight(c, needsBuf)
      }
    }

    
    if channelsBuf.nonEmpty then chList.selectionModel().select(0)

    new BorderPane:
      left   = leftPane
      center = centerPane
      right  = rightPane

  private def refreshCenter(ch: Channel, postsBuf: ObservableBuffer[Post]): Unit =
    postsBuf.setAll(ch.posts*)

  private def refreshRight(ch: Channel, needsBuf: ObservableBuffer[Need]): Unit =
    needsBuf.setAll(ch.needs*)
