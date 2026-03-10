import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public class UsernameChecker {
    private Map<String, String> usernameMap;
    private Map<String, Integer> attemptFrequency;
    private Random random;

    public UsernameChecker() {
        usernameMap = new ConcurrentHashMap<>();
        attemptFrequency = new ConcurrentHashMap<>();
        random = new Random();
    }

    public boolean registerUsername(String username, String userId) {
        if (usernameMap.containsKey(username)) return false;
        usernameMap.put(username, userId);
        return true;
    }

    public boolean checkAvailability(String username) {
        attemptFrequency.put(username, attemptFrequency.getOrDefault(username, 0) + 1);
        return !usernameMap.containsKey(username);
    }

    public List<String> suggestAlternatives(String username) {
        List<String> suggestions = new ArrayList<>();
        int suffix = 1;
        while (suggestions.size() < 5) {
            String suggestion = username + suffix;
            if (!usernameMap.containsKey(suggestion)) suggestions.add(suggestion);
            suffix++;
        }
        if (!usernameMap.containsKey(username.replace("_", "."))) {
            suggestions.add(username.replace("_", "."));
        }
        return suggestions;
    }

    public String getMostAttempted() {
        String mostAttempted = null;
        int maxCount = 0;
        for (Map.Entry<String, Integer> entry : attemptFrequency.entrySet()) {
            if (entry.getValue() > maxCount) {
                mostAttempted = entry.getKey();
                maxCount = entry.getValue();
            }
        }
        return mostAttempted + " (" + maxCount + " attempts)";
    }

    public static void main(String[] args) {
        UsernameChecker checker = new UsernameChecker();
        checker.registerUsername("john_doe", "user1");
        checker.registerUsername("admin", "user2");

        System.out.println(checker.checkAvailability("john_doe")); // false
        System.out.println(checker.checkAvailability("jane_smith")); // true
        System.out.println(checker.suggestAlternatives("john_doe"));

        checker.checkAvailability("admin");
        checker.checkAvailability("admin");
        checker.checkAvailability("admin");
        System.out.println(checker.getMostAttempted()); // "admin (3 attempts)"
    }
}