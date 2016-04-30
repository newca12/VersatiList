package org.edla.versatilist

import java.io._
import javafx.collections.FXCollections
import javafx.collections.ObservableList
import javafx.collections.transformation.FilteredList
import javafx.collections.transformation.SortedList
import javafx.fxml.FXML
import javafx.scene.control.TableColumn
import javafx.scene.control.TableView
import javafx.scene.control.TextField
import javafx.scene.control.TableColumn.CellDataFeatures

object Serializer {
  def objectToBytes[T](foo: T): Array[Byte] = {
    // replace Marshal.dump(foo)
    val ba = new ByteArrayOutputStream()
    val out = new ObjectOutputStream(ba)
    out.writeObject(foo)
    out.close()
    ba.toByteArray
  }

  def bytesToObject[T](fooBytes: Array[Byte]): Option[T] = {
    // replace Marshal.load[T](foo)
    try {
      val in = new ObjectInputStream(new ByteArrayInputStream(fooBytes))
      val o = in.readObject.asInstanceOf[T]
      in.close()
      Some(o)
    }
    catch {
      case e: Exception => {
        throw new RuntimeException("Serialization Problem", e)
        None
      }
    }
  }
}

class RecetteTableController {

  @FXML
  var filterField1: TextField = _
  @FXML
  var filterField2: TextField = _
  @FXML
  var filterField3: TextField = _
  @FXML
  var recetteTable: TableView[Recette] = _
  @FXML
  var recettes: TableColumn[Recette, String] = _


  var masterData: ObservableList[Recette] = FXCollections.observableArrayList()

  val in = getClass().getResourceAsStream("recipes.data")
  val bytes = Stream.continually(in.read).takeWhile(-1 !=).map(_.toByte).toArray
  val recettesOut: scala.collection.mutable.Map[String, Set[String]] = Serializer.bytesToObject(bytes).get

  for (recette <- recettesOut) masterData.add(Recette(recette))

  @FXML
  def initialize() = {
    val lambda = { cellData: CellDataFeatures[Recette, String] => cellData.getValue().recetteProperty }
    val lambdaFx: javafx.util.Callback[javafx.scene.control.TableColumn.CellDataFeatures[Recette, String], javafx.beans.value.ObservableValue[String]] =
      new javafx.util.Callback[javafx.scene.control.TableColumn.CellDataFeatures[Recette, String], javafx.beans.value.ObservableValue[String]] {
        def call(c: javafx.scene.control.TableColumn.CellDataFeatures[Recette, String]): javafx.beans.value.ObservableValue[String] = {
          c.getValue.recetteProperty
        }
      }
    recettes.setCellValueFactory(
      new javafx.util.Callback[javafx.scene.control.TableColumn.CellDataFeatures[Recette, String], javafx.beans.value.ObservableValue[String]] {
        def call(c: javafx.scene.control.TableColumn.CellDataFeatures[Recette, String]): javafx.beans.value.ObservableValue[String] = {
          c.getValue.recetteProperty
        }
      })


    /*import java.util.function.{ Function ⇒ JFunction, Predicate ⇒ JPredicate, BiPredicate }
    //usage example: `i: Int ⇒ true`
    implicit def toJavaPredicate[A](f: Function1[A, Boolean]) = new JPredicate[A] {
      override def test(a: A): Boolean = f(a)
    }
    val filteredData: FilteredList[Person] = new FilteredList[Person](masterData, p => true)*/
    val filteredData: FilteredList[Recette] = new FilteredList[Recette](masterData, new java.util.function.Predicate[Recette] {
      def test(p: Recette): Boolean = true
    })
    filterField1.textProperty().addListener(
      new javafx.beans.value.ChangeListener[String] {
        def changed(observable: javafx.beans.value.ObservableValue[_ <: String], oldValue: String, newValue: String): Unit = {
          filteredData.setPredicate(
            new java.util.function.Predicate[Recette] {
              def test(recette: Recette): Boolean = {
                val lowerCaseFilter = newValue.toLowerCase()
                recettesOut.filter(r => r._2.exists(str => str.contains(filterField2.getText.toLowerCase))).
                  filter(r => r._2.exists(str => str.contains(filterField3.getText.toLowerCase))).get(recette.recette).getOrElse(Nil).exists(str => str.toLowerCase().contains(lowerCaseFilter))

              }
            })
        }
      })
    filterField2.textProperty().addListener(
      new javafx.beans.value.ChangeListener[String] {
        def changed(observable: javafx.beans.value.ObservableValue[_ <: String], oldValue: String, newValue: String): Unit = {
          filteredData.setPredicate(
            new java.util.function.Predicate[Recette] {
              def test(recette: Recette): Boolean = {
                val lowerCaseFilter = newValue.toLowerCase()
                recettesOut.filter(r => r._2.exists(str => str.contains(filterField1.getText.toLowerCase))).
                  filter(r => r._2.exists(str => str.contains(filterField3.getText.toLowerCase))).get(recette.recette).getOrElse(Nil).exists(str => str.toLowerCase().contains(lowerCaseFilter))
              }
            })
        }
      })
    filterField3.textProperty().addListener(
      new javafx.beans.value.ChangeListener[String] {
        def changed(observable: javafx.beans.value.ObservableValue[_ <: String], oldValue: String, newValue: String): Unit = {
          filteredData.setPredicate(
            new java.util.function.Predicate[Recette] {
              def test(recette: Recette): Boolean = {
                val lowerCaseFilter = newValue.toLowerCase()
                recettesOut.filter(r => r._2.exists(str => str.contains(filterField1.getText.toLowerCase))).
                  filter(r => r._2.exists(str => str.contains(filterField2.getText.toLowerCase))).get(recette.recette).getOrElse(Nil).exists(str => str.toLowerCase().contains(lowerCaseFilter))
              }
            })
        }
      })
    val sortedData = new SortedList[Recette](filteredData)
    sortedData.comparatorProperty().bind(recetteTable.comparatorProperty())
    recetteTable.setItems(sortedData)
  }
}