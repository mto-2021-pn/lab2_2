package edu.iis.mto.similarity;

import edu.iis.mto.searcher.SearchResult;
import edu.iis.mto.searcher.SequenceSearcher;
import org.junit.jupiter.api.Test;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import static org.junit.jupiter.api.Assertions.*;

class SimilarityFinderTest {

    @Test
    void testEmptyArrays() {
        int[] array = {};
        int[] array2 = {};
        SequenceSearcher sequenceSearcher = (elem, sequence) -> SearchResult.builder().build();
        SimilarityFinder finder = new SimilarityFinder(sequenceSearcher);
        assertEquals(1, finder.calculateJackardSimilarity(array, array2));
    }

    @Test
    void testEqualsArrays() {
        int[] array = {768, 1232, 9123, 50};
        int[] array2 = {768, 1232, 9123, 50};
        SequenceSearcher sequenceSearcher = new SequenceSearcher() {
            @Override
            public SearchResult search(int elem, int[] sequence) {
                return SearchResult.builder().withPosition(0).withFound(true).build();
            }
        };
        SimilarityFinder finder = new SimilarityFinder(sequenceSearcher);
        assertEquals(1, finder.calculateJackardSimilarity(array, array2));
    }


    @Test
    void testCompletelyDifferentArrays() {
        int[] array = {0, 2354, 100, 700};
        int[] array2 = {678, 2, 9120, 32, 600};
        SequenceSearcher sequenceSearcher = new SequenceSearcher() {
            @Override
            public SearchResult search(int elem, int[] sequence) {
                return SearchResult.builder().withPosition(0).withFound(false).build();
            }
        };
        SimilarityFinder finder = new SimilarityFinder(sequenceSearcher);
        assertEquals(0, finder.calculateJackardSimilarity(array, array2));
    }

    @Test
    void testDifferentArrays() {
        int[] array = {34, 55};
        int[] array2 = {5, 34};
        SequenceSearcher sequenceSearcher = new SequenceSearcher() {
            @Override
            public SearchResult search(int elem, int[] sequence) {
                SearchResult searchResult = null;
                boolean[] types = {true, false};
                for (int i = 0; i < array.length; i++) {
                    if (elem == array[i]) {
                        searchResult = SearchResult.builder().withPosition(i).withFound(types[i]).build();
                    }
                }
                return searchResult;
            }
        };
        SimilarityFinder finder = new SimilarityFinder(sequenceSearcher);
        assertEquals(1.0 / 3.0, finder.calculateJackardSimilarity(array, array2));
    }

    @Test
    void testArraysWithDifferentLengthButWithCommonPart() {
        int[] array = {34, 55, 456, 654, 66, 4};
        int[] array2 = {34, 55, 123};
        SequenceSearcher sequenceSearcher = new SequenceSearcher() {
            @Override
            public SearchResult search(int elem, int[] sequence) {
                SearchResult searchResult = null;
                boolean[] types = {true, true, false, false, false, false};
                for (int i = 0; i < array.length; i++) {
                    if (elem == array[i]) {
                        searchResult = SearchResult.builder().withPosition(i).withFound(types[i]).build();
                    }
                }
                return searchResult;
            }
        };
        SimilarityFinder finder = new SimilarityFinder(sequenceSearcher);
        assertEquals(2.0 / 7.0, finder.calculateJackardSimilarity(array, array2));
    }

    @Test
    void testCallSearchMethodTenTimes() throws NoSuchFieldException, IllegalAccessException {
        int[] array = {675, 433, 756756, 32, 1, 11, 23543, 982, 555, 444};
        int[] array2 = {34, 55, 123, 99, 12, 32, 4444, 15672};
        SequenceSearcher sequenceSearcher = new SequenceSearcher() {
            private int counter = 0;

            @Override
            public SearchResult search(int elem, int[] sequence) {
                counter++;
                return SearchResult.builder().withPosition(counter).withFound(false).build();
            }
        };
        SimilarityFinder finder = new SimilarityFinder(sequenceSearcher);
        finder.calculateJackardSimilarity(array, array2);
        Field field = sequenceSearcher.getClass().getDeclaredField("counter");
        field.setAccessible(true);
        int retValue = field.getInt(sequenceSearcher);
        assertEquals(10, retValue);

    }

    @Test
    void testCallSearchMethodZeroTimes() throws NoSuchFieldException, IllegalAccessException {
        int[] array = {};
        int[] array2 = {};
        SequenceSearcher sequenceSearcher = new SequenceSearcher() {
            private int counter = 0;

            @Override
            public SearchResult search(int elem, int[] sequence) {
                counter++;
                return SearchResult.builder().withFound(false).build();
            }
        };
        SimilarityFinder finder = new SimilarityFinder(sequenceSearcher);
        finder.calculateJackardSimilarity(array, array2);
        Field field = sequenceSearcher.getClass().getDeclaredField("counter");
        field.setAccessible(true);
        int retValue = field.getInt(sequenceSearcher);
        assertEquals(0, retValue);

    }

    @Test
    public void testInputAndOutputArraysMustBeTheSame() {
        int[] array = {45, 45645, 2, 3, 55, 778};
        int[] array2 = {613, 44, 55, 4, 243, 2, 3};
        List<Integer> elementsArray = new ArrayList<>();
        List<Integer> elementsArray2 = new ArrayList<>();
        SequenceSearcher sequenceSearcher = new SequenceSearcher() {
            @Override
            public SearchResult search(int elem, int[] sequence) {
                elementsArray.add(elem);
                if(elementsArray2.isEmpty())
                    elementsArray2.addAll(Arrays.stream(sequence).boxed().collect(Collectors.toList()));
                return SearchResult.builder().withFound(false).build();
            }
        };
        SimilarityFinder finder = new SimilarityFinder(sequenceSearcher);
        finder.calculateJackardSimilarity(array, array2);
        List<Integer> arrayList = Arrays.stream(array).boxed().collect(Collectors.toList());
        List<Integer> array2List = Arrays.stream(array2).boxed().collect(Collectors.toList());
        assertTrue(arrayList.equals(elementsArray) && array2List.equals(elementsArray2));
    }
}