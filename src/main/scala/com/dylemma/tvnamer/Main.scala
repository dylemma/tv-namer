package com.dylemma.tvnamer

import scala.util._
import scala.concurrent._
import duration._
import ExecutionContext.Implicits.global
import java.io.File

object Main {

	class UsageException extends Exception("Usage: com.dylemma.tvnamer.Main <seriesName> [<workingDirectory>]")

	def main(args: Array[String]): Unit = {
		val getArg = args.toList.lift
		
		val searchTerm: String = getArg(0).getOrElse{ throw new UsageException }
		val workingDir: File = getArg(1).map{new File(_)}.getOrElse(new File("."))
		
		println("Searching for " + searchTerm)
		println("in " + workingDir.getCanonicalPath)
		
		val episodes = for {
			searchResults <- Series.find(searchTerm)
			series <- User.pickSeries(searchResults)
			episodes <- Series.getEpisodes(series)
			_ <- Renamer.rename(workingDir, episodes)
		} yield ()
		
		episodes.onComplete{
			case Failure(err) => println(err.getMessage)
			case Success(eps) => println("Complete!")
		}
		
		Await.ready(episodes, Duration.Inf)
	}
}