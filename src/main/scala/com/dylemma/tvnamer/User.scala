package com.dylemma.tvnamer

import scala.util._
import scala.concurrent._
import ExecutionContext.Implicits.global
import java.io.File

object User {

	class UserCancelledException extends Exception("User cancelled")

	def pickSeries(options: Seq[Series]): Future[Series] = {
		val zipped = options.zipWithIndex
		def displayOptions = {
			println
			println("====================")
			println("User Input Required!")
			println("====================")
		
			for{(opt,idx) <- zipped}{
				println(" [%d] - %s".format(idx, opt.name))
				println("    %s".format(opt.overview))
				println
			}
			
			println("  [quit] - to exit this prompt")
			println
		}
		
		def promptUser: Int = {
			displayOptions
			println("Enter the number of the correct option, or 'quit':")
			val input = readLine
			
			println("You entered '%s'".format(input))
			
			input match {
				case "quit" | "" => throw new UserCancelledException
				case AsInt(num) if num < zipped.size => num
				case _ => promptUser
			}
		}
		
		val series = for(idx <- Try{promptUser}) yield options(idx)
		val p = Promise[Series].complete(series)
		p.future
	}
	
	def pickDir(prompt: String): Future[File] = {
		def promptUser: File = {
			println(prompt + " (or 'quit'):")
			val file = readLine match {
				case "quit" => throw new UserCancelledException
				case fn => new File(fn)
			}
			if(file.isDirectory) file
			else promptUser
		}
		
		Promise[File].complete(Try{promptUser}).future
	}
	
	def approve: Boolean = {
		println("Do you approve? (y/N):")
		readLine match {
			case "y" | "Y" => true
			case _ => false
		}
	}
	
}