import java.util.*;

class TrieNode {
    Map<Character, TrieNode> children = new HashMap<>();
    boolean isEnd;
    String word;
}

public class AutocompleteSystem {
    private final TrieNode root = new TrieNode();
    private final Map<String, Integer> frequencyMap = new HashMap<>();

    // Add a search query with its frequency
    public void addQuery(String query, int freq) {
        frequencyMap.put(query, frequencyMap.getOrDefault(query, 0) + freq);

        TrieNode node = root;
        for (char c : query.toCharArray()) {
            node = node.children.computeIfAbsent(c, k -> new TrieNode());
        }
        node.isEnd = true;
        node.word = query;
    }

    // Update frequency of a query
    public void updateFrequency(String query) {
        addQuery(query, 1);
    }

    // Search top 10 queries for a prefix
    public List<String> search(String prefix) {
        TrieNode node = root;
        for (char c : prefix.toCharArray()) {
            node = node.children.get(c);
            if (node == null) return Collections.emptyList();
        }

        PriorityQueue<String> heap = new PriorityQueue<>(10, (a, b) -> {
            int cmp = Integer.compare(frequencyMap.get(a), frequencyMap.get(b));
            if (cmp == 0) return b.compareTo(a); // tie-break lexicographically
            return cmp;
        });

        dfs(node, heap);

        List<String> result = new ArrayList<>();
        while (!heap.isEmpty()) result.add(heap.poll());
        Collections.reverse(result);
        return result;
    }

    private void dfs(TrieNode node, PriorityQueue<String> heap) {
        if (node.isEnd) {
            heap.offer(node.word);
            if (heap.size() > 10) heap.poll();
        }
        for (TrieNode child : node.children.values()) {
            dfs(child, heap);
        }
    }

    public static void main(String[] args) {
        AutocompleteSystem system = new AutocompleteSystem();

        system.addQuery("java tutorial", 1234567);
        system.addQuery("javascript", 987654);
        system.addQuery("java download", 456789);
        system.addQuery("java 21 features", 100);

        System.out.println("Search 'jav': " + system.search("jav"));

        system.updateFrequency("java 21 features");
        system.updateFrequency("java 21 features");

        System.out.println("Search 'java': " + system.search("java"));
    }
}