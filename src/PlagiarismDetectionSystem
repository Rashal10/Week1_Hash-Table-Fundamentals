import java.util.*;

public class PlagiarismDetector {
    private final Map<String, Set<String>> ngramIndex = new HashMap<>();
    private final int N = 5; // using 5-grams for accuracy

    // Index a document's n-grams into the global hash table
    public void indexDocument(String docId, String text) {
        String[] words = text.split("\\s+");
        for (int i = 0; i <= words.length - N; i++) {
            StringBuilder sb = new StringBuilder();
            for (int j = 0; j < N; j++) sb.append(words[i + j]).append(" ");
            String ngram = sb.toString().trim();
            ngramIndex.computeIfAbsent(ngram, k -> new HashSet<>()).add(docId);
        }
    }

    // Analyze a new document against indexed documents
    public Map<String, Double> analyzeDocument(String docId, String text) {
        Map<String, Integer> matchCount = new HashMap<>();
        String[] words = text.split("\\s+");

        for (int i = 0; i <= words.length - N; i++) {
            StringBuilder sb = new StringBuilder();
            for (int j = 0; j < N; j++) sb.append(words[i + j]).append(" ");
            String ngram = sb.toString().trim();

            if (ngramIndex.containsKey(ngram)) {
                for (String otherDoc : ngramIndex.get(ngram)) {
                    if (!otherDoc.equals(docId)) {
                        matchCount.put(otherDoc, matchCount.getOrDefault(otherDoc, 0) + 1);
                    }
                }
            }
        }

        Map<String, Double> similarityPercent = new HashMap<>();
        int totalNgrams = words.length - N + 1;
        for (String other : matchCount.keySet()) {
            double percentage = (matchCount.get(other) * 100.0) / totalNgrams;
            similarityPercent.put(other, percentage);
        }

        return similarityPercent;
    }

    public static void main(String[] args) {
        PlagiarismDetector detector = new PlagiarismDetector();

        detector.indexDocument("essay_089.txt",
                "this is a sample essay for testing plagiarism detection system in a university scenario");
        detector.indexDocument("essay_092.txt",
                "another essay that might contain plagiarism from previous essay samples to test");

        Map<String, Double> results = detector.analyzeDocument("essay_123.txt",
                "this is a test essay for plagiarism detection in previous university essays");

        for (Map.Entry<String, Double> entry : results.entrySet()) {
            String status = entry.getValue() > 50 ? "PLAGIARISM DETECTED" : "suspicious";
            System.out.printf("Found %.0f matching n-grams with \"%s\" → Similarity: %.2f%% (%s)%n",
                    entry.getValue(), entry.getKey(), entry.getValue(), status);
        }
    }
}