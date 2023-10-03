/**
 * SearchByTitlePrefix.java 
 *****************************************************************************
 *                       revision history
 *****************************************************************************
 *
 * 2017 - Nick Harris - part 6 (base code)
 *****************************************************************************
 * Search by Title Prefix searches the titles in the song database 
 * for artists that begin with the input String
 */
package student;

/**
 *
 * @author nicholas
 */
public class SearchByTitlePrefix {
    
    private RaggedArrayList<Song> raggedList;
    
    public SearchByTitlePrefix(SongCollection sc){
        raggedList = new RaggedArrayList(new Song.CmpSongTitle());
        
        for(Song song : sc.getAllSongs()){
            raggedList.add(song);
        }
    }
    
    public Song[] search(String prefix){
        
        RaggedArrayList<Song> sublist = null;
        
        if(prefix.length() == 0){
            //get sublist
            sublist = raggedList;
        }
        else{
            //get prefix2
            int lastIndex = prefix.length() - 1;
            String firstPart = prefix.substring(0, lastIndex);
            String lastPart = "" + nextLetter(prefix.charAt(lastIndex));
            String prefix2 = firstPart + lastPart;
            
            //create Dummy songs
            Song s1 = new Song("", prefix, "");
            Song s2 = new Song("", prefix2, "");
            
            //get sublist
            sublist = raggedList.subList(s1, s2);
        }
        
        Song[] array = new Song[sublist.size];
        return sublist.toArray(array);
    }
    
    private char nextLetter(char c){
        char c2 = 0;
        if(c == 'z' || c == 'Z'){
            c2 = '~';
        }
        else{
            c2 = (char) (c + 1);
        }
        return c2;
    }

    /*
    Copied from Matt Applin's main() method from SearchByArtistPrefix
    Modified by Nick Harris in Part 6 to do titles instead of artists.
    */
    public static void main(String[] args) {
        if (args.length <= 1) {
            System.err.println("usage: prog songfile [search string]");
            return;
        }
        SongCollection sc = null;            
        sc = new SongCollection(args[0]);
                
        SearchByTitlePrefix sbtp = new SearchByTitlePrefix(sc);

        if (args.length > 1) {
            System.out.println("searching for: " + args[1]);
            Song[] byTitleResult = sbtp.search(args[1]);
            System.out.println("Size of Search Results: " + byTitleResult.length);
            for(int i=0; i < 10 && i<=byTitleResult.length-1;i++)
            {
                System.out.println(byTitleResult[i].toString());
            }
            // to do: show first 10 matches
        }
    }
}
