package org.edla.versatilist

import javafx.beans.property.SimpleStringProperty
import javafx.beans.property.StringProperty
//import scalafx.beans.property.{ObjectProperty, StringProperty}

object Recette {
  def apply(recette: (String,Set[String])) = new Recette(new SimpleStringProperty(recette._1))
}

class Recette(
    val recetteProperty: StringProperty
) {
  val recette = recetteProperty.get//.dropRight(4)
}