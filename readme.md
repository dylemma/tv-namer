TV Namer
========

####About

TV Namer's goal is to rename video files so that the new name contains the season number, episode number, and episode title. It will work on any file that has a name that includes something like `s01e12` or `episode 112`. The resulting format will be `S01E12 - Episode Title`.

Behind the scenes, the program will search for your tv show on  http://thetvdb.com in order to get the correct episode titles.

####Running

TV Namer is built against Scala 2.10.0 RC2, and will require that to run.

The main class is `com.dylemma.tvnamer.Main`, and expects a `seriesName` and `workingDirectory` argument. You can enclose arguments in quotes if they have spaces. I created a `.bat` file for my own convenience and made it available in the `PATH` as `tvnamer.bat`:

    @echo OFF
    SETLOCAL
    set PATH=C:\Program Files (x86)\scala-2.10.0-RC2\bin
    set WD="%CD%"
    scala -classpath D:\Code\Scala\tv-namer\target\scala-2.10\tv-namer_2.10-1.0.jar com.dylemma.tvnamer.Main %1 %WD%
    ENDLOCAL

So that if you are in a directory with a bunch of files for Futurama, you can run `tvnamer futurama` to run the program. Or for a show like How I Met Your Mother, you can run `tvnamer "how i met your mother"`