package org.edla.versatilist.labo

import java.io._

import org.apache.poi.hwpf.HWPFDocument
import org.apache.poi.hwpf.usermodel.Paragraph
import org.apache.poi.hwpf.usermodel.Range
import org.apache.poi.hwpf.usermodel.Table
import org.apache.poi.hwpf.usermodel.TableCell
import org.apache.poi.hwpf.usermodel.TableRow
import org.apache.poi.poifs.filesystem.POIFSFileSystem
//remove if not needed
import scala.collection.JavaConversions._
import java.io.File


//http://stackoverflow.com/questions/15086474/how-to-retrieve-the-table-from-doc-file-using-apache-poi
object Experiments extends App {

  val root = "ROOT_DIRECTORY_TO_IMPORT/"

  /**
    * Get a recursive listing of all files underneath the given directory.
    * from stackoverflow.com/questions/2637643/how-do-i-list-all-files-in-a-subdirectory-in-scala
    */
  def getRecursiveListOfFiles(dir: File): Array[File] = {
    val these = dir.listFiles
    these ++ these.filter(_.isDirectory).flatMap(getRecursiveListOfFiles)
  }

  val files = getRecursiveListOfFiles(new File(root))
  println(files.length)

  val allIngredients = scala.collection.mutable.SortedSet[String]()
  val recettes = scala.collection.mutable.Map[String, Set[String]]()

  for (f <- files) {
    val file = f.getAbsolutePath
    if (file.endsWith(".doc") && !file.contains("~")) {
      println(file)
      val recipeTitle = file diff root
      try {
        show(file)
      }
      catch {
        case e: IllegalArgumentException => println("XXX ERROR")
      }
    }
  }

  def show(fileName: String) = {
    //Array[String] = {
    val fis = new FileInputStream(fileName)
    val fs = new POIFSFileSystem(fis)
    val doc = new HWPFDocument(fs)
    val range = doc.getRange
    for (i <- 0 until range.numParagraphs()) {
      val par = range.getParagraph(i)
      //println(par.text())
    }
    val tablePar = range.getParagraph(0)
    if (tablePar.isInTable) {
      val table = range.getTable(tablePar)
      for (rowIdx <- 0 until table.numRows()) {
        val row = table.getRow(rowIdx)
        println("row " + (rowIdx + 1) + ",is table header: " + row.isTableHeader)
        for (colIdx <- 0 until row.numCells()) {
          val cell = row.getCell(colIdx)
          println("cell:"+colIdx+":"+cell.text)
        }
      }
    }

  }
}
