package org.edla.versatilist

import javafx.beans.property.SimpleStringProperty
import javafx.beans.property.StringProperty
//import scalafx.beans.property.{ObjectProperty, StringProperty}

object Recette {
  def apply(recette: String) = new Recette(new SimpleStringProperty(recette))
}

class Recette(
    val recetteProperty: StringProperty
) {
  val recette = recetteProperty.get
}