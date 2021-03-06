package com.liyutech.common

import java.io.File
import java.nio.file.{FileSystems, Files}
import java.text.SimpleDateFormat
import java.time.{LocalDate, LocalDateTime}
import java.util.Date
import scala.io.Source
import scala.jdk.CollectionConverters._

object CommonUtil {
  def firstLetterUppercase(str: String): String = {
    Option(str).map(s => s"${s.head.toUpper}${s.tail}").fold(str)(identity)
  }

  def toOrdinal(i: Int): String = {
    val suffix = i.toString.last match {
      case '2' => "nd"
      case '3' => "rd"
      case _ => "th"
    }
    s"$i$suffix"
  }

  def currentClassPath(): String = {
    classLoaderPath(getClass)
  }

  def classLoaderPath(clazz: Class[_]): String = {
    clazz.getProtectionDomain.getCodeSource.getLocation.getPath
  }

  def fullFilePath(path: String, prefixPath: String = currentClassPath()): String = s"$prefixPath/$path"

  // The file will be closed after its content is returned.
  def readAsString(file: File): String = {
    val f = scala.io.Source.fromFile(file)
    val output = f.mkString
    f.close()
    output
  }

  def readFileAsOptionString(fileName: String, prefixPath: String = "."): Option[String] = {
    val optFile: Option[File] = findFirstMatchedRegularFile(prefixPath, fileName)
    optFile.map(readAsString)
  }

  def readFileAsString(fullyQualifiedFileName: String): String = {
    readAsString(new File(fullyQualifiedFileName))
  }

  def readFromClassPath(resourceName: String): String = {
    val resource = getClass.getResource(s"/$resourceName")
    println(s"readFromClassPath $resourceName, resource $resource")
    Source.fromURL(resource).mkString

  }

  def findFirstMatchedRegularFile(rootPath: String, targetFile: String): Option[File] = {
    Option(rootPath) flatMap { root =>
      listFlatFiles(root).collectFirst { case f if f.getAbsolutePath.endsWith(targetFile) => f }
    }
  }

  def listFlatFiles(dirPath: String): Seq[File] = {
    listFlatFiles(new File(dirPath))
  }

  //   Recursively traverse the directory to retrieve all normal files under the current directory.
  def listFlatFiles(dir: File): Seq[File] = {
    listFlatFilesR(dir, Seq[File]())
  }

  private def listFlatFilesR(dir: File, breadcrumb: Seq[File]): Seq[File] = {
    if (dir.isFile) {
      breadcrumb ++ Seq(dir)
    }
    else {
      Option(dir.listFiles()).fold(breadcrumb) { files =>
        val (dirs, flatFiles) = files.partition(_.isDirectory)
        val newBreadcrumb = breadcrumb ++ flatFiles
        if (dirs.isEmpty) {
          newBreadcrumb
        }
        else {
          dirs.flatMap { dir =>
            listFlatFilesR(dir, newBreadcrumb)
          }
        }
      }
    }
  }

  def findFirstMatchDirectory(targetDir: String): Option[File] = {
    findFirstMatchDirectory(currentClassPath(), targetDir)
  }

  def findFirstMatchDirectory(rootPath: String, targetDir: String): Option[File] = {
    val startDir = FileSystems.getDefault.getPath(rootPath)
    Files.walk(startDir).iterator().asScala.collectFirst { case p if p.endsWith(targetDir) => p } map (_.toFile)
  }

  def dateFormat(date: Date): String = {
    new SimpleDateFormat("yyyy-MM-dd").format(date)
  }

  //  def addDays(startDate: String, days: Int): String = {
  //    new LocalDate(startDate).plusDays(days).toString
  //  }

  def minusDays(startDate: Date, days: Long): LocalDateTime = {
    TimeUtil.toLocalDateTime(startDate).minusDays(days)
  }

  def today: LocalDate = TimeUtil.toLocalDate(new Date())

  def timeNow: LocalDateTime = TimeUtil.toLocalDateTime(new Date())

  //  def tomorrow: String = addDays(today, 1)

  //  def startEndDaysTillNow(startDate: String, step: Int = 30): Seq[(String, String)] = {
  //    startEndDaysTill(startDate, today, step)
  //  }


  //  def startEndDaysTill(startDate: String, endDate: String, step: Int): Seq[(String, String)] = {
  //    startDateTill(startDate, endDate, step).sliding(2, 1).map { dates => (dates.head, dates.last) }.toSeq
  //  }

  //  def startDateTillNow(startDate: String, step: Int): Seq[String] = {
  //    startDateTill(startDate, today, step)
  //  }

  // endDate is inclusive.
  //  def startDateTill(startDate: String, endDate: String, step: Int): Seq[String] = {
  //    datesTillNowR(startDate, endDate, step, Seq[String]()).reverse
  //  }

  //  @tailrec
  //  private def datesTillNowR(startDate: String, endDate: String, step: Int, startDates: Seq[String]): Seq[String] = {
  //    if (startDate >= endDate) {
  //      endDate +: startDates
  //    }
  //    else {
  //      val newStartData = addDays(startDate, step)
  //      datesTillNowR(newStartData, endDate, step, startDate +: startDates)
  //    }
  //  }
}