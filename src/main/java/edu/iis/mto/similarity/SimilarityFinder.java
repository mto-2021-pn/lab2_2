package edu.iis.mto.similarity;

import edu.iis.mto.searcher.SequenceSearcher;

public class SimilarityFinder {

  private final SequenceSearcher searcher;

  public SimilarityFinder(SequenceSearcher searcher) {
    this.searcher = searcher;
  }

  public double calculateJackardSimilarity(int[] seq1, int[] seq2) {
    if (seq1.length == 0 && seq2.length == 0) {
      return 1.0d;
    }

    int intersectSize = calculateIntersect(seq1, seq2);
    int unionSize = seq1.length + seq2.length - intersectSize;

    return (double) intersectSize / (double) unionSize;
  }

  private int calculateIntersect(int[] seq1, int[] seq2) {
    int intersectSize = 0;
    for (int elem : seq1) {
      if (searcher.search(elem, seq2).isFound()) {
        intersectSize++;
      }
    }
    return intersectSize;
  }
}
