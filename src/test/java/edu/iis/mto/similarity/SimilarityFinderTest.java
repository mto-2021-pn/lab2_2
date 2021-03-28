package edu.iis.mto.similarity;

import static org.junit.jupiter.api.Assertions.assertEquals;
import edu.iis.mto.searcher.SearchResult;
import edu.iis.mto.searcher.SequenceSearcher;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Field;

class SimilarityFinderTest {

    @Test
    void similarityFinderArrayIsTheSame() {
        SimilarityFinder similarityFinder = new SimilarityFinder(new SequenceSearcher() {
            @Override
            public SearchResult search(int elem, int[] sequence) {
                        return SearchResult.builder().withFound(true).withPosition(elem).build();
            }
        });
        int[] seq1 = {1,2,3,4,5};
        int[] seq2 = {1,2,3,4,5};
        assertEquals(1, similarityFinder.calculateJackardSimilarity(seq1, seq2));
    }
    @Test
    void  similarityFinderArrayIsDiffrend() {
        SimilarityFinder similarityFinder = new SimilarityFinder(new SequenceSearcher() {
            @Override
            public SearchResult search(int elem, int[] sequence) {
                if(elem%2 ==0)
                    return SearchResult.builder().withFound(true).withPosition(elem).build();
                else
                    return SearchResult.builder().withFound(false).withPosition(-1).build();
            }
        });
        int[] seq1 = {2,4,6};
        int[] seq2 = {1,2,3,4,5,6};
        assertEquals(0.5, similarityFinder.calculateJackardSimilarity(seq1, seq2));
    }
    @Test
    void similarityFinderArrayIsdifferent() {
        SimilarityFinder similarityFinder = new SimilarityFinder(new SequenceSearcher() {
            @Override
            public SearchResult search(int elem, int[] sequence) {
                return SearchResult.builder().withFound(false).withPosition(-1).build();
            }
        });
        int[] seq1 = {4,5,6,7};
        int[] seq2 = {0,1,2,3};
        assertEquals(0, similarityFinder.calculateJackardSimilarity(seq1, seq2));
    }
    @Test
    void similarityFinderEmpty() {
        SimilarityFinder similarityFinder = new SimilarityFinder(new SequenceSearcher() {
            @Override
            public SearchResult search(int elem, int[] sequence) {
                return SearchResult.builder().withFound(false).withPosition(-1).build();
            }
        });
        int[] seq1 = {};
        int[] seq2 = {};
        assertEquals(1, similarityFinder.calculateJackardSimilarity(seq1, seq2));
    }
    @Test
    void searchMethodDo0Times() throws NoSuchFieldException, IllegalAccessException {

        int expectedValue = 0;
        SequenceSearcher mocksequenceSearcher = new SequenceSearcher() {
            private int anInt = 0;
            @Override
            public SearchResult search(int elem, int[] sequence) {
                anInt++;
                return null;
            }
        };
        SimilarityFinder similarityFinder = new SimilarityFinder(mocksequenceSearcher);
        int[] seq1 = {};
        int[] seq2 = {};
        similarityFinder.calculateJackardSimilarity(seq1, seq2);
        Field field = mocksequenceSearcher.getClass().getDeclaredField("anInt");
        field.setAccessible(true);
        int result = field.getInt(mocksequenceSearcher);
        assertEquals(expectedValue, result);
    }
    @Test
    void searchMethodDo5Times() throws NoSuchFieldException, IllegalAccessException {

        int expectedValue = 4;
        SequenceSearcher mocksequenceSearcher = new SequenceSearcher() {
            private int anInt = 0;
            @Override
            public SearchResult search(int elem, int[] sequence) {
                anInt++;
                return SearchResult.builder().withFound(true).withPosition(elem).build();
            }
        };
        SimilarityFinder similarityFinder = new SimilarityFinder(mocksequenceSearcher);
        int[] seq1 = {1,2,3,4};
        int[] seq2 = {1,2,3,4};

        similarityFinder.calculateJackardSimilarity(seq1, seq2);
        Field field = mocksequenceSearcher.getClass().getDeclaredField("anInt");
        field.setAccessible(true);
        int result = field.getInt(mocksequenceSearcher);
        assertEquals(expectedValue, result);
    }
    @Test
    void searchMethodDoFourTimes() throws NoSuchFieldException, IllegalAccessException {

        int expectedValue = 4;
        SequenceSearcher mocksequenceSearcher = new SequenceSearcher() {
            private int anInt = 0;
            @Override
            public SearchResult search(int elem, int[] sequence) {
                anInt++;
                return SearchResult.builder().withFound(false).withPosition(-1).build();
            }
        };
        SimilarityFinder similarityFinder = new SimilarityFinder(mocksequenceSearcher);
        int[] seq1 = {6,7,8,9};
        int[] seq2 = {1,2,3,4};
        similarityFinder.calculateJackardSimilarity(seq1, seq2);
        Field field = mocksequenceSearcher.getClass().getDeclaredField("anInt");
        field.setAccessible(true);
        int result = field.getInt(mocksequenceSearcher);
        assertEquals(expectedValue, result);
    }

}