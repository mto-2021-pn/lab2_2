package edu.iis.mto.similarity;

import edu.iis.mto.searcher.SearchResult;
import edu.iis.mto.searcher.SequenceSearcher;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Field;
import java.util.concurrent.atomic.AtomicInteger;

import static org.junit.jupiter.api.Assertions.*;

class SimilarityFinderTest {

    static public int[] seq0;
    static public int[] seq1;
    static public int[] seq2;
    static public int[] seq3;
    static public int[] seq4;
    static public int[] seq5;
    static public int[] seq6;
    static public int[] seq7;
    static public int[] seqEmpty;
    static public double res;
    public SimilarityFinder similarityFinder;
    public SequenceSearcher sequenceSearcher;
    public SearchResult searchResult;
    SequenceSearcher mock;

    @BeforeEach
    void init() {
        seq0 = new int[]{1, 9, 33, 42};
        seq1 = new int[]{5, 4, 56, 8};
        seq2 = new int[]{2, 4, 55, 7};
        seq3 = new int[]{1, 2, 3, 4, 5, 6, 7, 8};
        seq4 = new int[]{2, 3, 4, 5, 6, 7};
        seq5 = new int[]{2, 3, 4};
        seq6 = new int[]{2, 3};
        seq7 = new int[]{2};
        seqEmpty = new int[]{};
    }

    @Test
    void test() {

    }

    @BeforeEach
    void setUp() {
    }

    @AfterEach
    void tearDown() {
    }

    @Test
    void calculateJackardSimilarityFirstNull() {
        similarityFinder = new SimilarityFinder((elem, sequence) -> SearchResult.builder()
                .withFound(false)
                .build());

        assertThrows(NullPointerException.class, () -> similarityFinder.calculateJackardSimilarity(null, seq2));
    }

    @Test
    void calculateJackardSimilaritySecondNull() {
        similarityFinder = new SimilarityFinder((elem, sequence) -> SearchResult.builder()
                .withFound(false)
                .build());
        assertThrows(NullPointerException.class, () -> similarityFinder.calculateJackardSimilarity(seq1, null));
    }

    @Test
    void calculateJackardSimilarityBothNull() {
        similarityFinder = new SimilarityFinder((elem, sequence) -> SearchResult.builder()
                .withFound(false)
                .build());
        assertThrows(NullPointerException.class, () -> similarityFinder.calculateJackardSimilarity(null, null));
    }

    ////////////////////////
    @Test
    void calculateJackardSimilarityFirstEmpty() {
        SimilarityFinder similarityFinder = new SimilarityFinder((elem, sequence) -> null);
        assertEquals(0, similarityFinder.calculateJackardSimilarity(seqEmpty, seq1));
    }

    @Test
    void calculateJackardSimilaritySecondEmpty() {
        similarityFinder = new SimilarityFinder((elem, sequence) -> SearchResult.builder().withFound(false).build());

        assertEquals(0, similarityFinder.calculateJackardSimilarity(seq1, seqEmpty));
    }

    @Test
    void calculateJackardSimilarityBothEmpty() {
        similarityFinder = new SimilarityFinder((elem, sequence) -> null);

        assertEquals(1, similarityFinder.calculateJackardSimilarity(seqEmpty, seqEmpty));
    }
    //////////////////////////////////

    @Test
    void calculateJackardSimilaritySameSeqs() {
        sequenceSearcher = new SequenceSearcher() {
            @Override
            public SearchResult search(int elem, int[] sequence) {
                for (int i = 0; i < sequence.length; i++) {
                    if (elem == sequence[i]) {
                        return SearchResult.builder().withFound(true).withPosition(i).build();

                    }
                }
                return SearchResult.builder().withFound(false).withPosition(-1).build();
            }
        };

        similarityFinder = new SimilarityFinder(sequenceSearcher);
        assertEquals(similarityFinder.calculateJackardSimilarity(seq0, seq0), 1);
    }

    @Test
    void calculateJackardSimilarityNotSameSeqs() {

        sequenceSearcher = new SequenceSearcher() {
            @Override
            public SearchResult search(int elem, int[] sequence) {
                for (int i = 0; i < sequence.length; i++) {
                    if (elem == sequence[i]) {
                        return SearchResult.builder().withFound(true).withPosition(i).build();
                    }
                }
                return SearchResult.builder().withFound(false).withPosition(-1).build();
            }
        };

        similarityFinder = new SimilarityFinder(sequenceSearcher);
        assertEquals(similarityFinder.calculateJackardSimilarity(seq0, seq1), 0.0);
    }

    //////////////////////
    @Test
    void calculateJackardSimilarityDiffSeqs() {
        sequenceSearcher = new SequenceSearcher() {
            @Override
            public SearchResult search(int elem, int[] sequence) {
                for (int i = 0; i < sequence.length; i++) {
                    if (elem == sequence[i]) {
                        return SearchResult.builder().withFound(true).withPosition(i).build();

                    }
                }
                return SearchResult.builder().withFound(false).withPosition(-1).build();
            }
        };

        similarityFinder = new SimilarityFinder(sequenceSearcher);
        res = similarityFinder.calculateJackardSimilarity(seq1, seq2);
        assertTrue(res > 0 && res < 1);
    }

    @Test
    void calculateJackardSimilarityHalfSame() {
        sequenceSearcher = new SequenceSearcher() {
            @Override
            public SearchResult search(int elem, int[] sequence) {
                for (int i = 0; i < sequence.length; i++) {
                    if (elem == sequence[i]) {
                        return SearchResult.builder().withFound(true).withPosition(i).build();
                    }
                }
                return SearchResult.builder().withFound(false).withPosition(-1).build();
            }
        };

        similarityFinder = new SimilarityFinder(sequenceSearcher);
        assertEquals(similarityFinder.calculateJackardSimilarity(seq5, seq4), 0.5);

    }

    //////////////////////////
    @Test
    void calculateJackardSimilarityCountInvoke() throws NoSuchFieldException, IllegalAccessException {
        mock = new SequenceSearcher() {
            int counter = 0;

            @Override
            public SearchResult search(int elem, int[] sequence) {
                counter++;
                return SearchResult.builder().withFound(true).build();
            }
        };
        similarityFinder = new SimilarityFinder(mock);
        similarityFinder.calculateJackardSimilarity(seq1, seq2);
        Field field = mock.getClass().getDeclaredField("counter");
        field.setAccessible(true);

        res = field.getInt(mock);
        assertEquals(res, 4);
    }

    @Test
    void calculateJackardSimilarityCountZeroIfEmpty() throws NoSuchFieldException, IllegalAccessException {
        mock = new SequenceSearcher() {
            int counter = 0;

            @Override
            public SearchResult search(int elem, int[] sequence) {
                counter++;
                return SearchResult.builder().withFound(true).build();
            }
        };
        similarityFinder = new SimilarityFinder(mock);
        similarityFinder.calculateJackardSimilarity(seqEmpty, seqEmpty);
        Field field = mock.getClass().getDeclaredField("counter");
        field.setAccessible(true);

        res = field.getInt(mock);
        assertEquals(res, 0);
    }

}
