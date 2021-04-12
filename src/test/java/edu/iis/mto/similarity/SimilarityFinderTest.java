package edu.iis.mto.similarity;

import static org.junit.jupiter.api.Assertions.fail;

import edu.iis.mto.searcher.SearchResult;
import edu.iis.mto.searcher.SequenceSearcher;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

class SimilarityFinderTest {

    @Test
    void testEmptySequences() {
        SimilarityFinder similarityFinder = new SimilarityFinder(new SequenceSearcher() {
            @Override
            public SearchResult search(int elem, int[] sequence) {
                return SearchResult.builder().build();
            }
        });
        int tab[] = new int[0];
        int tab2[] = new int[0];
        double result = similarityFinder.calculateJackardSimilarity(tab, tab2);
        Assertions.assertEquals(1, result);
    }

    @Test
    void testCorrectResultWithSequencesOfEqualLength() {
        SimilarityFinder similarityFinder = new SimilarityFinder(new SequenceSearcher() {
            @Override
            public SearchResult search(int elem, int[] sequence) {
                SearchResult searchResult = null;
                if (elem == 1) {
                    searchResult = SearchResult.builder().withPosition(0).withFound(true)
                            .build();
                } else if (elem == 2) {
                    searchResult = SearchResult.builder().withPosition(1).withFound(false)
                            .build();
                }
                return searchResult;
            }
        });
        int tab[] = {1, 2};
        int tab2[] = {1, 3};
        double result = similarityFinder.calculateJackardSimilarity(tab, tab2);
        Assertions.assertEquals(1 / 3.0, result);
    }

    @Test
    void testCorrectResultWithSequencesOfNotEqualLength() {
        SimilarityFinder similarityFinder = new SimilarityFinder(new SequenceSearcher() {
            @Override
            public SearchResult search(int elem, int[] sequence) {
                SearchResult searchResult = null;
                if (elem == 1) {
                    searchResult = SearchResult.builder().withPosition(0).withFound(true)
                            .build();
                } else if (elem == 2) {
                    searchResult = SearchResult.builder().withPosition(1).withFound(false)
                            .build();
                } else if (elem == 4) {
                    searchResult = SearchResult.builder().withPosition(2).withFound(false)
                            .build();
                }
                return searchResult;
            }
        });
        int tab[] = {1, 2, 4};
        int tab2[] = {1, 3};
        double result = similarityFinder.calculateJackardSimilarity(tab, tab2);
        Assertions.assertEquals(1 / 4.0, result);
    }

    @Test
    void testSequencesAreTheSame() {
        SimilarityFinder similarityFinder = new SimilarityFinder(new SequenceSearcher() {
            @Override
            public SearchResult search(int elem, int[] sequence) {
                SearchResult searchResult = null;
                if (elem == 1) {
                    searchResult = SearchResult.builder().withPosition(0).withFound(true)
                            .build();
                } else if (elem == 2) {
                    searchResult = SearchResult.builder().withPosition(1).withFound(true)
                            .build();
                } else if (elem == 5) {
                    searchResult = SearchResult.builder().withPosition(2).withFound(true)
                            .build();
                }
                return searchResult;
            }
        });
        int tab[] = {1, 2, 5};
        int tab2[] = {1, 2, 5};
        double result = similarityFinder.calculateJackardSimilarity(tab, tab2);
        Assertions.assertEquals(1, result);
    }

    @Test
    void testSequencesAreTotallyDifferent() {
        SimilarityFinder similarityFinder = new SimilarityFinder(new SequenceSearcher() {
            @Override
            public SearchResult search(int elem, int[] sequence) {
                SearchResult searchResult = null;
                if (elem == 3) {
                    searchResult = SearchResult.builder().withPosition(0).withFound(false)
                            .build();
                } else if (elem == 4) {
                    searchResult = SearchResult.builder().withPosition(1).withFound(false)
                            .build();
                } else if (elem == 5) {
                    searchResult = SearchResult.builder().withPosition(2).withFound(false)
                            .build();
                }
                return searchResult;
            }
        });
        int tab[] = {3, 4, 5};
        int tab2[] = {1, 2};
        double result = similarityFinder.calculateJackardSimilarity(tab, tab2);
        Assertions.assertEquals(0, result);
    }

    @Test
    void testThatInvokesMethodThreeTimes() throws NoSuchFieldException, IllegalAccessException {
        SequenceSearcher sequenceSearcher = new SequenceSearcher() {
            private int invokesTimes = 0;

            @Override
            public SearchResult search(int elem, int[] sequence) {
                invokesTimes++;
                SearchResult searchResult = SearchResult.builder().withFound(true).build();
                return searchResult;
            }
        };
        SimilarityFinder similarityFinder = new SimilarityFinder(sequenceSearcher);

        int tab[] = {3, 4, 5};
        int tab2[] = {1, 2};
        double result = similarityFinder.calculateJackardSimilarity(tab, tab2);

        Field invokesTimesField = sequenceSearcher.getClass().getDeclaredField("invokesTimes");
        invokesTimesField.setAccessible(true);

        Assertions.assertEquals(3, invokesTimesField.getInt(sequenceSearcher));
    }

    @Test
    void testIfPassedArgumentsAreTheSequences() throws NoSuchFieldException, IllegalAccessException {
        SequenceSearcher sequenceSearcher = new SequenceSearcher() {
            private List<Integer> seq1 = new ArrayList<>();
            private List<Integer> seq2 = new ArrayList<>();

            @Override
            public SearchResult search(int elem, int[] sequence) {
                seq1.add(elem);
                if (seq2.size() == 0) {
                    seq2.addAll(Arrays.stream(sequence)
                            .boxed()
                            .collect(Collectors.toList()));
                }
                SearchResult searchResult = SearchResult.builder().withFound(true).build();
                return searchResult;
            }
        };
        SimilarityFinder similarityFinder = new SimilarityFinder(sequenceSearcher);

        int tab[] = {3, 4, 5};
        int tab2[] = {1, 2};
        similarityFinder.calculateJackardSimilarity(tab, tab2);

        Field seq1Field = sequenceSearcher.getClass().getDeclaredField("seq1");
        seq1Field.setAccessible(true);
        Field seq2Field = sequenceSearcher.getClass().getDeclaredField("seq2");
        seq2Field.setAccessible(true);
        List<Integer> seq1 = (List<Integer>) seq1Field.get(sequenceSearcher);
        List<Integer> seq2 = (List<Integer>) seq2Field.get(sequenceSearcher);

        Assertions.assertArrayEquals(seq1.stream().mapToInt(Integer::intValue).toArray(), tab);
        Assertions.assertArrayEquals(seq2.stream().mapToInt(Integer::intValue).toArray(), tab2);
    }
}
