package edu.iis.mto.similarity;

import edu.iis.mto.searcher.SearchResult;
import edu.iis.mto.searcher.SequenceSearcher;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Field;

import static org.junit.jupiter.api.Assertions.*;

class SimilarityFinderTest {

    @Test
    public void emptySequencesTest() {
        SimilarityFinder finder = new SimilarityFinder(null);
        int[] seq1 = {};
        int[] seq2 = {};
        assertEquals(1, finder.calculateJackardSimilarity(seq1, seq2));
    }

    @Test
    public void equalSequencesTest() {
        SimilarityFinder finder = new SimilarityFinder((elem, sequence) ->
                SearchResult.builder().withFound(true).build());
        int[] seq1 = {0,1,2,3};
        int[] seq2 = {0,1,2,3};
        assertEquals(1, finder.calculateJackardSimilarity(seq1, seq2));
    }

    @Test
    public void notEqualSequencesTest() {
        SimilarityFinder finder = new SimilarityFinder((elem, sequence) ->
                SearchResult.builder().withFound(false).build());
        int[] seq1 = {0,1,4,5};
        int[] seq2 = {6,7,8,9};
        assertEquals(0, finder.calculateJackardSimilarity(seq1, seq2));
    }

    @Test
    public void halfEqualSequencesTest() {
        SimilarityFinder finder = new SimilarityFinder((elem, sequence) ->
                SearchResult.builder().withFound(true).build());
        int[] seq1 = {0,1,2,3};
        int[] seq2 = {0,1};
        assertEquals(0.5, finder.calculateJackardSimilarity(seq2, seq1));
    }

    @Test
    public void zeroInvokesOfSearcherTest() throws NoSuchFieldException, IllegalAccessException {
        SequenceSearcher searcher = new SequenceSearcher() {
            private int invCount = 0;

            @Override
            public SearchResult search(int elem, int[] sequence) {
                invCount++;
                return null;
            }
        };

        SimilarityFinder finder = new SimilarityFinder(searcher);

        int[] seq1 = {};
        int[] seq2 = {};

        finder.calculateJackardSimilarity(seq1, seq2);

        Field searchInvCountField = searcher.getClass()
                .getDeclaredField("invCount");
        searchInvCountField.setAccessible(true);

        int expectedInvCount = 0;
        assertEquals(expectedInvCount, searchInvCountField.getInt(searcher));
    }

    @Test
    public void fourInvokesOfSearcherTest() throws NoSuchFieldException, IllegalAccessException {
        SequenceSearcher searcher = new SequenceSearcher() {
            private int invCount = 0;

            @Override
            public SearchResult search(int elem, int[] sequence) {
                invCount++;
                return SearchResult.builder().withFound(true).build();
            }
        };

        SimilarityFinder finder = new SimilarityFinder(searcher);

        int[] seq1 = {2, 4, 6, 8};
        int[] seq2 = {1, 2, 3, 4, 5, 6};

        finder.calculateJackardSimilarity(seq1, seq2);

        Field searchInvCountField = searcher.getClass()
                .getDeclaredField("invCount");
        searchInvCountField.setAccessible(true);

        int expectedInvCount = 4;
        assertEquals(expectedInvCount, searchInvCountField.getInt(searcher));
    }

    @Test
    public void InvokeSearcherTestWithFiveAsArgument() throws NoSuchFieldException, IllegalAccessException {
        SequenceSearcher searcher = new SequenceSearcher() {
            private int invElement = 0;

            @Override
            public SearchResult search(int elem, int[] sequence) {
                invElement = elem;
                return SearchResult.builder().withFound(true).build();
            }
        };

        SimilarityFinder finder = new SimilarityFinder(searcher);

        int[] seq1 = {5};
        int[] seq2 = {1, 2, 3, 4, 5, 6};

        finder.calculateJackardSimilarity(seq1, seq2);

        Field searchInvElementField = searcher.getClass()
                .getDeclaredField("invElement");
        searchInvElementField.setAccessible(true);

        int expectedInvElement = 5;
        assertEquals(expectedInvElement, searchInvElementField.getInt(searcher));
    }

    @Test
    public void InvokeSearcherTestWithCustomSequenceAsArgument() throws NoSuchFieldException, IllegalAccessException {
        SequenceSearcher searcher = new SequenceSearcher() {

            private int[] invSequence = {};

            @Override
            public SearchResult search(int elem, int[] sequence) {
                invSequence = sequence.clone();
                return SearchResult.builder().withFound(true).build();
            }
        };

        SimilarityFinder finder = new SimilarityFinder(searcher);

        int[] seq1 = {2};
        int[] seq2 = {3, 4, 5};

        finder.calculateJackardSimilarity(seq1, seq2);

        Field invSequenceField = searcher.getClass()
                .getDeclaredField("invSequence");
        invSequenceField.setAccessible(true);
        int[] invSequence = (int[]) invSequenceField.get(searcher);

        assertArrayEquals(seq2, invSequence);
    }
}
