package student;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.NoSuchElementException;
import java.util.Scanner;
import java.util.logging.Level;
import java.util.logging.Logger;

/*
 * 2017 - Nick Harris (Pt 1) - coded the constructor to parse files
 * 2016 - Anne Applin - formatting and JavaDoc skeletons added   
 * 2015 - Prof. Bob Boothe - Starting code and main for testing  
 ************************************************************************
 * SongCollection.java 
 * Read the specified data file and build an array of songs.
 */
/**
 *
 * @author boothe
 */
public class SongCollection {

    private Song[] songs;

    /**
     * Note: in any other language, reading input inside a class is simply not
     * done!! No I/O inside classes because you would normally provide
     * precompiled classes and I/O is OS and Machine dependent and therefore not
     * portable. Java runs on a virtual machine that IS portable. So this is
     * permissable because we are programming in Java.
     *
     * @param filename The path and filename to the datafile that we are using
     * must be set in the Project Properties as an argument.
     */
    public SongCollection(String filename) {
        
        songs = new Song[0];
        String formatError = "File contains unexpected text on Line ";
        String eofError = "EOF reached unexpectedly after Line ";
        int currentLine = 0;
        
            // use a try catch block
        try {
            Scanner scan = new Scanner(new FileReader(filename));
            StringBuilder builder = null;
            
            // read in the song file and build the songs array
            ArrayList<Song> list = new ArrayList<>(); //list to create array
            
            while(scan.hasNextLine()){
                
                //read artist
                String artistLine = scan.nextLine();
                currentLine++;
                if(!artistLine.startsWith("ARTIST")){
                    System.err.println(formatError + currentLine);
                    break;      //Just quit loop, and keep the songs we've got.
                                //It's probably just an extra line.
                }
                int startText = artistLine.indexOf("\"") + 1;
                int endText = artistLine.lastIndexOf("\"");
                String artist = artistLine.substring(startText,endText);
                
                //read song
                String songLine = scan.nextLine();
                currentLine++;
                if(!songLine.startsWith("TITLE")){
                    System.err.println(formatError + currentLine);
                    return;
                }
                startText = songLine.indexOf("\"")+1;
                endText = songLine.lastIndexOf("\"");
                String song = songLine.substring(startText,endText);
                
                //read lyrics
                builder = new StringBuilder();
                String lyricLine = scan.nextLine();
                currentLine++;
                if(!lyricLine.startsWith("LYRICS")){
                    System.err.println(formatError + currentLine);
                    return;
                }
                startText = lyricLine.indexOf("\"")+1;
                while(lyricLine.length() == 0 || lyricLine.charAt(0) != '\"'){
                    //add line to lyrics build
                    builder.append(lyricLine.substring(startText) + "\n");
                    //get next line
                    lyricLine = scan.nextLine();
                    currentLine++;
                    startText = 0;
                }
                
                //add new Song to the list
                Song ourSong = new Song(artist,song,builder.toString());
                list.add(ourSong);
            }
            
            songs = new Song[list.size()];
            songs = list.toArray(songs);
            Arrays.sort(songs);
            // you must use a StringBuilder to read in the lyrics!
            // you must add the line feed at the end of each lyric line.
            // sort the songs array using Array.sort (see the Java API)
        } catch (FileNotFoundException ex1) {
            System.err.println("Could not find " + filename);
            String pwd = System.getProperty("user.dir");
            System.err.println("We looked in "+ pwd);
        } catch (NoSuchElementException ex2){
            System.err.println("NoSuchElementException thrown");
            System.err.println(eofError + currentLine);
        }
    }
 
    /**
     * this is used as the data source for building other data structures
     * @return the songs array
     */
    public Song[] getAllSongs() {
        return songs;
    }
 
    /**
     * unit testing method
     * @param args
     */
    public static void main(String[] args) {
        if (args.length == 0) {
            System.err.println("usage: prog songfile");
            return;
        }

        SongCollection sc = new SongCollection(args[0]);
        
        Song[] songArray = sc.getAllSongs();
        // todo: show song count and up to the first 10 songs 
        // (name & title only, 1 per line) 
        int howMany = songArray.length;
        System.out.printf("Total songs = %d, first songs:\n", howMany);
        
        if(howMany > 10)
            howMany = 10;

        for(int i=0; i < howMany; i++){
            System.out.printf("%-30s  \"%s\"\n", songArray[i].getArtist(), 
                    songArray[i].getTitle() );
        }
    }
}