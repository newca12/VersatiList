package org.edla.versatilist

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

object Serializer {
  def objectToBytes[T](foo: T): Array[Byte] = { // replace Marshal.dump(foo)
  val ba = new ByteArrayOutputStream()
    val out = new ObjectOutputStream(ba)
    out.writeObject(foo)
    out.close()
    ba.toByteArray
  }

  def bytesToObject[T](fooBytes: Array[Byte]): Option[T] = { // replace Marshal.load[T](foo)
      try {
        val in = new ObjectInputStream(new ByteArrayInputStream(fooBytes))
        val o = in.readObject.asInstanceOf[T]
        in.close()
        Some(o)
      }
      catch {
        case e: Exception => {
          throw new RuntimeException ("Serialization Problem", e)
          None
        }
      }
  }
}

//http://stackoverflow.com/questions/15086474/how-to-retrieve-the-table-from-doc-file-using-apache-poi
object ComplexImporter extends App {

  val root = "ROOT_DIRECTORY_TO_IMPORT/"
  
  /** Convert from bytes into hex string. */
  def bytesToHex(bytes: List[Byte]) =
    bytes.map { b => String.format("%02X", java.lang.Byte.valueOf(b)) }.mkString(" ")

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
  val recettes = scala.collection.mutable.Map[String,Set[String]]()

  for (f <- files) {
    val file = f.getAbsolutePath
    if (file.endsWith(".doc") && !file.contains("~") && !(file diff root).toLowerCase().contains("z_")) {

      try {
        println(file)
        val (title,ingredients) = getIngredients(file)
        val recipeTitle = (file diff root take(2))+title.trim
        recettes += (recipeTitle -> ingredients.toSet)
        for (ingredient <- ingredients) { 
          allIngredients += ingredient  
          println("-"+ingredient)
        }
      } catch {
        case e: IllegalArgumentException => println("XXX ERROR")
      }
    }
  }
  //for (ingredient <- allIngredients) println(ingredient)
  //println(allIngredients.size)
  //println(recettes)
  //recettes foreach( r => if(r._2.contains("jambon")) println(r._1))
  //println(recettes.filter( r => r._2.exists(str => str.contains("jambon"))))

  val out = new FileOutputStream("/tmp/out")
  out.write(Serializer.objectToBytes(recettes))
  out.close

/*
  val in = new FileInputStream("/tmp/out")
  val bytes = Stream.continually(in.read).takeWhile(-1 !=).map(_.toByte).toArray
  val recettesOut: scala.collection.mutable.Map[String,Set[String]] = Serializer.bytesToObject(bytes).get

  println(recettesOut.size)
  println(recettesOut)
*/

  def getIngredients(fileName: String): (String, Array[String]) = {
    val fis = new FileInputStream(fileName)
    val fs = new POIFSFileSystem(fis)
    val doc = new HWPFDocument(fs)
    val range = doc.getRange
    for (i <- 0 until range.numParagraphs()) {
      val par = range.getParagraph(i)
      //println(par.text())
    }
    val tablePar = range.getParagraph(0)

    //if (tablePar.isInTable) {
    val table = range.getTable(tablePar)
    val title = table.getRow(0).getCell(1).text()
    val row = table.getRow(2)
    val cell = row.getCell(1)
    val ingredients = cell.text().
      replaceAll(("(HYPERLINK) \".*\""), "").
      replaceAll("""(?:\d*[.,])?\d+( à (?:\d*[.,])?\d+)?(½)?\b (g |gr |l |cc|cl|cm|cs|dl|kg|ml)?( )*(de |d’|d')?(ou )*""", "")
      .split("[\u000B\u000D]").map(_.trim) // else ( "ANOMALIE" )
    (title, ingredients)
  }

}