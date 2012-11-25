package com.dylemma.tvnamer

import java.io.File
import scala.concurrent._
import ExecutionContext.Implicits.global

object Renamer {

	val illegalChars = """<>:"/\|?*""".toSet

	def rename(dir: File, candidates: Seq[Episode]): Future[Unit] = {
		val files = dir.listFiles.toList
		
		val epMap = candidates.map {
			case Episode(s,e,name) => (s,e) -> name
		} toMap
		
		val renames = for{
			file <- files
			(s,e) <- SeasonEpisode unapply file
			epName <- epMap.get(s->e)
		} yield {
			val newName = name(s, e, epName, fileExtension(file))
			file -> newName
		}
		
		for((file,newName) <- renames) println(file.getName + " -> " + newName)
		if(User.approve){
			Future{
				for((file,newName) <- renames) yield {
					val parent = file.getParent
					val newFile = new File(parent, newName)
					file.renameTo(newFile)
				}
			}
		} else {
			Future.failed(new User.UserCancelledException)
		}
	}

	object SeasonEpisode {
		val Regex = ".*[sS](\\d+) ?[eE](\\d+).*".r
		val Regex2 = ".*(\\d+).?(\\d{2}).*".r
		def unapply(file: File): Option[(Int, Int)] = file.getName match {
			case Regex(AsInt(s), AsInt(e)) => Some(s -> e)
			case Regex2(AsInt(s), AsInt(e)) => Some(s -> e)
			case _ => None
		}
	}
	
	def fileExtension(file: File): Option[String] = {
		val fn = file.getName
		fn.lastIndexOf(".") match {
			case i if i<0 => None
			case i => Some(fn.substring(i))
		}
	}
	
	def name(season: Int, episode: Int, epName: String, extension: Option[String]) = {
		val ext = extension getOrElse ""
		val unsafeName = "S%02dE%02d - %s%s".format(season, episode, epName, ext)
		safeName(unsafeName)
	}
	
	def safeName(fileName: String): String = {
		fileName.filterNot{ illegalChars contains _ }
	}
	
}