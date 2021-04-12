package edu.iis.mto.similarity;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.fail;

import edu.iis.mto.searcher.SearchResult;
import edu.iis.mto.searcher.SequenceSearcher;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Field;

class SimilarityFinderTest {

    @Test
    public void equalSequencesTest()
    {
        SimilarityFinder finder = new SimilarityFinder((elem, sequence) -> SearchResult.builder().withFound(true).build());
        int [] seq1= {0,1,2,3};
        int [] seq2= {0,1,2,3};
        assertEquals(1, finder.calculateJackardSimilarity(seq1, seq2));
    }
    @Test
    public void notEqualSequencesTest()
    {
        SimilarityFinder finder = new SimilarityFinder((elem, sequence) -> SearchResult.builder().withFound(false).build());
        int [] seq1= {1,2,3,4};
        int [] seq2= {5,6,7,8};
        assertEquals(0.0, finder.calculateJackardSimilarity(seq1, seq2));
    }

    @Test
    public void halfEqualSequencesTest()
    {
        SimilarityFinder finder = new SimilarityFinder((elem, sequence) -> SearchResult.builder().withFound(true).build());
        int [] seq1={1,2} ;
        int [] seq2={1,2,3,4} ;
        assertEquals(0.5, finder.calculateJackardSimilarity(seq1, seq2));
    }

    @Test
    public void emptySequencesTest()
    {
        SimilarityFinder finder = new SimilarityFinder(null);
        int [] seq1= {};
        int [] seq2= {};
        assertEquals(1, finder.calculateJackardSimilarity(seq1, seq2));
    }

    @Test
    public void zeroInvokesTest() throws NoSuchFieldException, IllegalAccessException {
        SequenceSearcher searcher = new SequenceSearcher() {
            private int counter = 0;

            @Override
            public SearchResult search(int elem, int[] sequence) {
                counter++;
                return null;
            }
        };

        SimilarityFinder finder = new SimilarityFinder(searcher);

        int[] seq1 = {};
        int[] seq2 = {};

        finder.calculateJackardSimilarity(seq1, seq2);

        Field field = searcher.getClass().getDeclaredField("counter");
        field.setAccessible(true);

        int expectedCount = 0;
        assertEquals(expectedCount, field.getInt(searcher));
    }

    @Test
    public void fourInvokesTest() throws NoSuchFieldException, IllegalAccessException {
        SequenceSearcher searcher = new SequenceSearcher() {
            private int counter = 0;

            @Override
            public SearchResult search(int elem, int[] sequence) {
                counter++;
                return SearchResult.builder().withFound(true).build();
            }
        };

        SimilarityFinder finder = new SimilarityFinder(searcher);

        int[] seq1 = {2, 4, 6, 8};
        int[] seq2 = {1, 2, 3, 4, 5, 6};

        finder.calculateJackardSimilarity(seq1, seq2);

        Field field = searcher.getClass().getDeclaredField("counter");
        field.setAccessible(true);

        int expectedCount = 4;
        assertEquals(expectedCount, field.getInt(searcher));
    }

    @Test
    public void argumentOfSearcherTest() throws NoSuchFieldException, IllegalAccessException {
        SequenceSearcher searcher = new SequenceSearcher() {
            private int x = 0;
            @Override
            public SearchResult search(int elem, int[] sequence) {
                x = elem;
                return SearchResult.builder().withFound(true).build();
            }
        };

        SimilarityFinder finder = new SimilarityFinder(searcher);

        int[] seq1 = {0};
        int[] seq2 = {0, 1, 2, 3, 4, 5, 6};

        finder.calculateJackardSimilarity(seq1, seq2);

        Field field = searcher.getClass().getDeclaredField("x");
        field.setAccessible(true);

        int expectedX = 0;
        assertEquals(expectedX, field.getInt(searcher));
    }
}
