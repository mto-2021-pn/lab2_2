package edu.iis.mto.similarity;


import static org.junit.jupiter.api.Assertions.*;
import edu.iis.mto.searcher.SearchResult;
import edu.iis.mto.searcher.SequenceSearcher;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Field;
import java.util.Arrays;


class SimilarityFinderTest {

    private SequenceSearcher ss;
    private SimilarityFinder finder;

    @BeforeEach
    void initialize() {
        finder = new SimilarityFinder((elem, sequence) -> {
            for(int i=0; i< sequence.length; i++)
                if(sequence[i]==elem)
                    return SearchResult.builder().withFound(true).withPosition(i).build();
            return SearchResult.builder().withFound(false).withPosition(-1).build();
        });
    }

    @Test
    void calculateJackardSimilarity_TheSameData() {
        int[] sequence = {-30, -21, 0, 2, 6, 9, 14, 25};
        int[] sequence2 = sequence.clone();
        double expected = 1;
        assertEquals(expected, finder.calculateJackardSimilarity(sequence, sequence2));
    }

    @Test
    void calculateJackardSimilarity_TheSameData_InDifferentOrder() {
        int[] sequence = {1, 3, 5, 7, 9};
        int[] sequence2 = {9, 7, 5, 3, 1};
        double expected = 1;
        assertEquals(expected, finder.calculateJackardSimilarity(sequence, sequence2));
    }

    @Test
    void calculateJackardSimilarity_EmptyArrays() {
        int[] sequence = {};
        int[] sequence2 = {};
        double expected = 1;
        assertEquals(expected, finder.calculateJackardSimilarity(sequence, sequence2));
    }

    @Test
    void calculateJackardSimilarity_DifferentData() {
        int[] sequence = {-30, -21, 0, 2, 6, 9, 14, 25};
        int[] sequence2 = {-18, -5, 1, 4, 12};
        double expected = 0;
        assertEquals(expected, finder.calculateJackardSimilarity(sequence, sequence2));
    }

    @Test
    void calculateJackardSimilarity_FirstArrayIsEmpty() {
        int[] sequence = {};
        int[] sequence2 = {-18, -5, 1, 4, 12};
        double expected = 0;
        assertEquals(expected, finder.calculateJackardSimilarity(sequence, sequence2));
    }

    @Test
    void calculateJackardSimilarity_SecondArrayIsEmpty() {
        int[] sequence = {-30, -21, 0, 2, 6, 9, 14, 25};
        int[] sequence2 = {};
        double expected = 0;
        assertEquals(expected, finder.calculateJackardSimilarity(sequence, sequence2));
    }

    @Test
    void calculateJackardSimilarity_FirstArrayContainsSecond() {
        int[] sequence = {-30, -21, 0, 2, 6, 9, 14, 25};
        int[] sequence2 = Arrays.copyOfRange(sequence, 1, 4);
        int common = sequence2.length;
        double expected = (double)common/(sequence.length+sequence2.length-common);
        assertEquals(expected, finder.calculateJackardSimilarity(sequence, sequence2));
    }

    @Test
    void calculateJackardSimilarity_SecondArrayContainsFirst() {
        int[] sequence2 = {-30, -21, 0, 2, 6, 9, 14, 25};
        int[] sequence = Arrays.copyOfRange(sequence2, 1, 4);
        int common = sequence.length;
        double expected = (double)common/(sequence.length+sequence2.length-common);
        assertEquals(expected, finder.calculateJackardSimilarity(sequence, sequence2));
    }

    @Test
    void calculateJackardSimilarity_OneCommonElement() {
        int[] sequence2 = {0, 1, 2, 3, 4, 5, 6, 7, 8, 9};
        int[] sequence = {-4, -3, -2, -1, 0};
        int common = 1;
        double expected = (double)common/(sequence.length+sequence2.length-common);
        assertEquals(expected, finder.calculateJackardSimilarity(sequence, sequence2));
    }

    @Test
    void calculateJackardSimilarity_LotCommonElements() {
        int[] sequence2 = {12, 13, 14, 15, 16, 17, 18, 19, 20};
        int[] sequence = {7, 8, 9, 10, 11, 12, 13, 14};
        int common = 3;
        double expected = (double)common/(sequence.length+sequence2.length-common);
        assertEquals(expected, finder.calculateJackardSimilarity(sequence, sequence2));
    }



    @Test
    void testSearch_CalledCorrectAmountOfIteration() throws NoSuchFieldException, IllegalAccessException {
        ss = new SequenceSearcher() {
            private int countFunctionCalls = 0;
            @Override
            public SearchResult search(int elem, int[] sequence) {
                countFunctionCalls++;
                return SearchResult.builder().build();
            }
        };
        finder = new SimilarityFinder(ss);

        int[] sequence = {1, 3, 5, 7, 9};
        int[] sequence2 = {9, 7, 5, 3, 1};
        finder.calculateJackardSimilarity(sequence, sequence2);

        Field argument = ss.getClass().getDeclaredField("countFunctionCalls");
        argument.setAccessible(true);
        assertEquals(sequence.length, argument.getInt(ss));
    }

    @Test
    void testSearch_CalledZeroTimes() throws NoSuchFieldException, IllegalAccessException {
        ss = new SequenceSearcher() {
            private int countFunctionCalls = 0;
            @Override
            public SearchResult search(int elem, int[] sequence) {
                countFunctionCalls++;
                return SearchResult.builder().build();
            }
        };
        finder = new SimilarityFinder(ss);

        int[] sequence = {};
        int[] sequence2 = {9, 7, 5, 3, 1};
        finder.calculateJackardSimilarity(sequence, sequence2);

        Field argument = ss.getClass().getDeclaredField("countFunctionCalls");
        argument.setAccessible(true);
        assertEquals(sequence.length, argument.getInt(ss));
    }

    @Test
    void testSearch_CalledWithCorrectFirstParameter() throws NoSuchFieldException, IllegalAccessException {
        ss = new SequenceSearcher() {
            private int argumentInFunction = 0;
            @Override
            public SearchResult search(int elem, int[] sequence) {
                argumentInFunction=elem;
                return SearchResult.builder().build();
            }
        };
        finder = new SimilarityFinder(ss);

        int[] sequence = {1};
        int[] sequence2 = {2};
        finder.calculateJackardSimilarity(sequence, sequence2);

        Field argument = ss.getClass().getDeclaredField("argumentInFunction");
        argument.setAccessible(true);
        assertEquals(sequence[0], argument.getInt(ss));
    }

    @Test
    void testSearch_CalledWithCorrectSecondParameter() throws NoSuchFieldException, IllegalAccessException {
        ss = new SequenceSearcher() {
            private int[] arraytInFunction;
            @Override
            public SearchResult search(int elem, int[] sequence) {
                arraytInFunction = sequence.clone();
                return SearchResult.builder().build();
            }
        };
        finder = new SimilarityFinder(ss);

        int[] sequence = {1};
        int[] sequence2 = {2};
        finder.calculateJackardSimilarity(sequence, sequence2);

        Field argument = ss.getClass().getDeclaredField("arraytInFunction");
        argument.setAccessible(true);
        assertArrayEquals(sequence2, (int[])argument.get(ss));
    }


}
