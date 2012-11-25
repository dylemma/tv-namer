package com.dylemma.tvnamer

object AsInt {
	def unapply(s: String): Option[Int] = {
		try{
			val i = Integer.valueOf(s)
			Some(i)
		} catch {
			case e: NumberFormatException => None
		}
	}
}