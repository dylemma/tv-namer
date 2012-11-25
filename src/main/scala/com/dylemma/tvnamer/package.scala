package com.dylemma

import scala.io.Source

package object tvnamer {

	lazy val apiKey = {
		val in = getClass.getResourceAsStream("/apikey")
		val src = Source.fromInputStream(in)
		try{ 
			src.mkString
		} 
		finally { 
			src.close
		}
	}
}