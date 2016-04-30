package org.edla


import javafx.fxml.FXMLLoader
import javafx.scene.{Parent, Scene}
import javafx.stage.Stage
import java.io.IOException
import scala.util.control.NonFatal

class VersatiList extends javafx.application.Application {


  val resource = getClass.getResource("versatilist/RecetteTable.fxml")
  if (resource == null) {
    throw new IOException("Cannot load resource: RecetteTable.fxml")
  }

  val loader = new FXMLLoader(resource)

  override def start(stage: Stage): Unit =
    try {
      stage.setTitle("VersatiList 0.2")
      loader.load[Parent]()
      stage.setScene(new Scene(loader.getRoot[Parent]))
      stage.show()
    } catch {
      case NonFatal(e) => e.printStackTrace()
    }
}