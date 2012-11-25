package com.dylemma.tvnamer

import scala.xml._
import scala.concurrent._
import ExecutionContext.Implicits.global

case class Series(
	id: Int,
	name: String,
	overview: String
)

object Series {
	def fromXml(node: Node): Option[Series] = {
		for {
			AsInt(sid) <- (node \\ "seriesid").lift(0).map(_.text)
			overview <- (node \\ "Overview").lift(0).map(_.text)
			name <- (node \\ "SeriesName").lift(0).map(_.text)
		} yield Series(sid, name, overview)
	}
	
	def find(seriesName: String): Future[Seq[Series]] = {
		val req = Request("http://www.thetvdb.com/api/GetSeries.php", "seriesname" -> seriesName)
		
		for(xml <- req.asElem) yield {
			(xml \\ "Series") flatMap Series.fromXml
		}

	}
	
	def getEpisodes(series: Series): Future[Seq[Episode]] = {
		val req = Request("http://thetvdb.com/api/%s/series/%s/all/en".format(apiKey, series.id))
		
		for(xml <- req.asElem) yield {
			(xml \\ "Episode") flatMap Episode.fromXml
		}
	}
}