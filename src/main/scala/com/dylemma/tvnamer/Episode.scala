package com.dylemma.tvnamer

import scala.xml.Node

case class Episode(
	season: Int,
	episode: Int,
	name: String
)

object Episode {
	def fromXml(node: Node): Option[Episode] = {
		for {
			AsInt(e) <- (node \\ "EpisodeNumber").lift(0).map(_.text)
			AsInt(s) <- (node \\ "SeasonNumber").lift(0).map(_.text)
			name <- (node \\ "EpisodeName").lift(0).map(_.text)
		} yield Episode(s, e, name)
	}
}