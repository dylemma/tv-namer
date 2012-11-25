package com.dylemma.tvnamer

import java.net._
import java.io.InputStream

import scala.concurrent.Future
import scala.concurrent.ExecutionContext.Implicits.global
import scala.io.Source
import scala.xml._

object Request {
	val charset = "UTF-8"

	def apply(urlStr: String, params: (String,String)*) = {
		val encodedParams = params.iterator map {
			case (k,v) => 
				URLEncoder.encode(k, charset) + "=" +
				URLEncoder.encode(v, charset)
		}
		
		val url = new URL(urlStr + "?" + encodedParams.mkString("&"))
		new Request(url)
	}

}

class Request(url: URL){
	
	private def withResponse[A](body: InputStream => A): Future[A] = {
		val futureStream = Future[InputStream]{
			val conn = url.openConnection
			conn.setRequestProperty("Accept-Charset", Request.charset)
			conn.getInputStream
		}
		for(instr <- futureStream) yield {
			try{ body(instr) }
			finally{ instr.close }
		}
	}
	
	def asString: Future[String] = withResponse { instr =>
		Source.fromInputStream(instr).mkString
	}
	
	def asElem: Future[Elem] = withResponse{ instr => XML.load(instr) }
	
}