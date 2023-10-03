/**
 * SearchByArtistPrefix.java 
 *****************************************************************************
 *                       revision history
 *****************************************************************************
 *
 * 2017 - Nick Harris - pre-6 (removed try-catch around SongCollection ctor)
 * 2017 - Nick Harris - Part 2 code (added cmpCnt code)
 * 2017 - Matt Applin - Part 2 code (base code for search() and main()) 
 * 8/2015 Anne Applin - Added formatting and JavaDoc 
 * 2015 - Bob Boothe - starting code  
 *****************************************************************************
 * Search by Artist Prefix searches the artists in the song database 
 * for artists that begin with the input String
 */

/**
 *
 * @author boothe
 */

package student;

import java.io.*;
import java.util.*;


public class SearchByArtistPrefix {
    // keep a local direct reference to the song array
    private Song[] songs;  

    /**
     * constructor initializes the property. [Done]
     * @param sc a SongCollection object
     */
    public SearchByArtistPrefix(SongCollection sc) {
        songs = sc.getAllSongs();
    }

    /**
     * find all songs matching artist prefix uses binary search should operate
     * in time log n + k (# matches)
     * converts artistPrefix to lowercase and creates a Song object with 
     * artist prefix as the artist in order to have a Song to compare.
     * walks back to find the first "beginsWith" match, then walks forward
     * adding to the arrayList until it finds the last match.
     *
     * @param artistPrefix all or part of the artist's name
     * @return an array of songs by artists with substrings that match 
     *    the prefix
     */
    public Song[] search(String artistPrefix) 
    {
        Song searchArtistLowIndex = new Song(artistPrefix,"","");
        Song searchArtistHighIndex = new Song(artistPrefix +'~',"","");
        Song.CmpArtist songComparitor = new Song.CmpArtist();

        int lowIndex=songs.length;
        do
        {
            lowIndex = Arrays.binarySearch(songs,0,lowIndex,searchArtistLowIndex,songComparitor);
        }while(lowIndex>=0);

        int highIndex = -lowIndex-2;
        do
        {
            highIndex = Arrays.binarySearch(songs,highIndex+1,songs.length,searchArtistHighIndex,songComparitor);
        }while(highIndex>=0);

        System.out.println("Total comparisons: " + songComparitor.getCmpCnt()); 
        
        return Arrays.copyOfRange(songs, -lowIndex-1, -highIndex-1);
    }

    /**
     * testing method for this unit
     * @param args  command line arguments set in Project Properties - 
     * the first argument is the data file name and the second is the partial 
     * artist name, e.g. be which should return beatles, beach boys, beegees,
     * etc.
     */
    public static void main(String[] args) {
        if (args.length == 0) {
            System.err.println("usage: prog songfile [search string]");
            return;
        }
        SongCollection sc = null;            
        sc = new SongCollection(args[0]);
                
        SearchByArtistPrefix sbap = new SearchByArtistPrefix(sc);

        if (args.length > 1) {
            System.out.println("searching for: " + args[1]);
            Song[] byArtistResult = sbap.search(args[1]);
            System.out.println("Size of Search Results: " + byArtistResult.length);
            for(int i=0; i < 10 && i<=byArtistResult.length-1;i++)
            {
                System.out.println(byArtistResult[i].toString());
            }
            // to do: show first 10 matches
        }
    }
}