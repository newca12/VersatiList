package org.edla.versatilist

import java.io.File
import java.io.FileInputStream
import java.io.FileNotFoundException
import java.io.IOException
import org.apache.poi.xwpf.extractor.XWPFWordExtractor
import org.apache.poi.xwpf.usermodel.XWPFDocument
import org.apache.poi.hwpf.HWPFDocument
import org.apache.poi.hwpf.extractor.WordExtractor
//remove if not needed
import scala.collection.JavaConversions._

object SimpleImporter extends App {

  val FilePath = "/Users/hack/tmp2/VersatiList/soupe à l'ail.doc"
  var fis: FileInputStream = null
  if (FilePath.substring(FilePath.length - 1) == "x") {
    try {
      fis = new FileInputStream(new File(FilePath))
      val doc = new XWPFDocument(fis)
      val extract = new XWPFWordExtractor(doc)
      println(extract.getText)
    } catch {
      case e: IOException => e.printStackTrace()
    }
  } else {
    try {
      fis = new FileInputStream(new File(FilePath))
      val doc = new HWPFDocument(fis)
      val extractor = new WordExtractor(doc)
      println(extractor.getText)
    } catch {
      case e: IOException => e.printStackTrace()
    }
  }

}