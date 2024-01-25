package edu.escuelaing.arem.ASE.app;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

/**
 * Unit test for simple App.
 */
public class AppTest 
    extends TestCase
{
    HttpServer server = new HttpServer();
    /**
     * Create the test case
     *
     * @param testName name of the test case
     */
    public AppTest( String testName ) {
        super( testName );

    }

    /**
     * @return the suite of tests being tested
     */
    public static Test suite()
    {
        return new TestSuite( AppTest.class );
    }

    /**
     * Rigourous Test :-)
     */
    public void testExternalApiConnection() {
        try {
            String result = HttpServer.movieDataService("/movie?t=Inception");
            assertNotNull(result);
            // Checks if the result is brought from the external API
            assertEquals("{\"Title\":\"Inception\",\"Year\":\"2010\",\"Rated\":\"PG-13\",\"Released\":\"16 Jul 2010\",\"Runtime\":\"148 min\",\"Genre\":\"Action, Adventure, Sci-Fi\",\"Director\":\"Christopher Nolan\",\"Writer\":\"Christopher Nolan\",\"Actors\":\"Leonardo DiCaprio, Joseph Gordon-Levitt, Elliot Page\",\"Plot\":\"A thief who steals corporate secrets through the use of dream-sharing technology is given the inverse task of planting an idea into the mind of a C.E.O., but his tragic past may doom the project and his team to disaster.\",\"Language\":\"English, Japanese, French\",\"Country\":\"United States, United Kingdom\",\"Awards\":\"Won 4 Oscars. 159 wins & 220 nominations total\",\"Poster\":\"https://m.media-amazon.com/images/M/MV5BMjAxMzY3NjcxNF5BMl5BanBnXkFtZTcwNTI5OTM0Mw@@._V1_SX300.jpg\",\"Ratings\":[{\"Source\":\"Internet Movie Database\",\"Value\":\"8.8/10\"},{\"Source\":\"Rotten Tomatoes\",\"Value\":\"87%\"},{\"Source\":\"Metacritic\",\"Value\":\"74/100\"}],\"Metascore\":\"74\",\"imdbRating\":\"8.8\",\"imdbVotes\":\"2,504,988\",\"imdbID\":\"tt1375666\",\"Type\":\"movie\",\"DVD\":\"20 Jun 2013\",\"BoxOffice\":\"$292,587,330\",\"Production\":\"N/A\",\"Website\":\"N/A\",\"Response\":\"True\"}", HttpServer.connectionWithExternalRestApi("Inception"));
            // Checks the content of the answer
            assertTrue(result.contains("\"Title\":\"Inception\",\"Year\":\"2010\""));
        } catch (Exception e) {
            e.printStackTrace();
            fail("Exception thrown: " + e.getMessage());
        }
        assertTrue( true );
    }

    /**
     * Rigourous Test :-)
     */
    public void testCache() {
        try {
            String result = HttpServer.movieDataService("/movie?t=Inception");
            assertNotNull(result);

            String result2 = HttpServer.movieDataService("/movie?t=Inception");
            // Checks if the result was consulted at the cache
            assertEquals("{\"Title\":\"Inception\",\"Year\":\"2010\",\"Rated\":\"PG-13\",\"Released\":\"16 Jul 2010\",\"Runtime\":\"148 min\",\"Genre\":\"Action, Adventure, Sci-Fi\",\"Director\":\"Christopher Nolan\",\"Writer\":\"Christopher Nolan\",\"Actors\":\"Leonardo DiCaprio, Joseph Gordon-Levitt, Elliot Page\",\"Plot\":\"A thief who steals corporate secrets through the use of dream-sharing technology is given the inverse task of planting an idea into the mind of a C.E.O., but his tragic past may doom the project and his team to disaster.\",\"Language\":\"English, Japanese, French\",\"Country\":\"United States, United Kingdom\",\"Awards\":\"Won 4 Oscars. 159 wins & 220 nominations total\",\"Poster\":\"https://m.media-amazon.com/images/M/MV5BMjAxMzY3NjcxNF5BMl5BanBnXkFtZTcwNTI5OTM0Mw@@._V1_SX300.jpg\",\"Ratings\":[{\"Source\":\"Internet Movie Database\",\"Value\":\"8.8/10\"},{\"Source\":\"Rotten Tomatoes\",\"Value\":\"87%\"},{\"Source\":\"Metacritic\",\"Value\":\"74/100\"}],\"Metascore\":\"74\",\"imdbRating\":\"8.8\",\"imdbVotes\":\"2,504,988\",\"imdbID\":\"tt1375666\",\"Type\":\"movie\",\"DVD\":\"20 Jun 2013\",\"BoxOffice\":\"$292,587,330\",\"Production\":\"N/A\",\"Website\":\"N/A\",\"Response\":\"True\"}", server.getMoviesSearched().get("Inception"));
            // Checks if the results are the same
            assertEquals(result, result2);
        } catch (Exception e) {
            e.printStackTrace();
            fail("Exception thrown: " + e.getMessage());
        }
        assertTrue( true );
    }

}
