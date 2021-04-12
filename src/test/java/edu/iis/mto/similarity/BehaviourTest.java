package edu.iis.mto.similarity;

import edu.iis.mto.searcher.SearchResult;
import edu.iis.mto.searcher.SequenceSearcher;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Field;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class BehaviourTest {

    @Test
    public void emptySeq() throws NoSuchFieldException, IllegalAccessException {
        SequenceSearcher searcher = new SequenceSearcher() {
            private int counter = 0;

            @Override
            public SearchResult search(int elem, int[] sequence) {
                counter ++;
                SearchResult searchResult = SearchResult.builder().build();
                return searchResult;
            }
        };

        SimilarityFinder finder = new SimilarityFinder(searcher);

        int[] seq1 = {};
        int[] seq2 = {};
        Field searchField = searcher.getClass().getDeclaredField("counter");
        searchField.setAccessible(true);
        finder.calculateJackardSimilarity(seq1, seq2);
        assertEquals(0, searchField.getInt(searcher));
    }

    @Test
    void oneElementSeqTest() throws NoSuchFieldException, IllegalAccessException {
        SequenceSearcher searcher = new SequenceSearcher() {
            private int counter = 0;

            @Override
            public SearchResult search(int elem, int[] sequence) {
                counter ++;
                SearchResult searchResult = SearchResult.builder().build();
                return searchResult;
            }
        };

        SimilarityFinder finder = new SimilarityFinder(searcher);

        int[] seq1 = {1};
        int[] seq2 = {1};
        Field searchField = searcher.getClass().getDeclaredField("counter");
        searchField.setAccessible(true);
        finder.calculateJackardSimilarity(seq1, seq2);
        assertEquals(1, searchField.getInt(searcher));
    }

    @Test
    void fourElementSeqTest() throws NoSuchFieldException, IllegalAccessException {
        SequenceSearcher searcher = new SequenceSearcher() {
            private int counter = 0;

            @Override
            public SearchResult search(int elem, int[] sequence) {
                counter ++;
                SearchResult searchResult = SearchResult.builder().build();
                return searchResult;
            }
        };

        SimilarityFinder finder = new SimilarityFinder(searcher);

        int[] seq1 = {1, 2, 3, 4};
        int[] seq2 = {4, 4, 10, 11, 21};
        Field searchField = searcher.getClass().getDeclaredField("counter");
        searchField.setAccessible(true);
        finder.calculateJackardSimilarity(seq1, seq2);
        assertEquals(4, searchField.getInt(searcher));
    }

    @Test
    void checkIfSearcherSearchGivenSequence() throws NoSuchFieldException, IllegalAccessException {
        SequenceSearcher searcher = new SequenceSearcher() {
            private int [] seq = {};

            @Override
            public SearchResult search(int elem, int[] sequence) {
                seq = sequence.clone();
                SearchResult searchResult = SearchResult.builder().build();
                return searchResult;
            }
        };

        int [] seq = {1, 2, 3};
        int [] seq2 = {3, 4, 5};
        SimilarityFinder similarityFinder = new SimilarityFinder(searcher);
        similarityFinder.calculateJackardSimilarity(seq, seq2);
        Field searchField = searcher.getClass().getDeclaredField("seq");
        searchField.setAccessible(true);
        int [] array = (int [])searchField.get(searcher);
        assertArrayEquals(seq2, array);
    }

}
