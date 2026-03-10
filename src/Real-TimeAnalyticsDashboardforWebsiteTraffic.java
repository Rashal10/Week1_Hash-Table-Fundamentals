import java.util.*;
import java.util.concurrent.*;

class PageViewEvent {
    String url;
    String userId;
    String source;

    PageViewEvent(String url, String userId, String source) {
        this.url = url;
        this.userId = userId;
        this.source = source;
    }
}

public class RealTimeAnalytics {
    private final Map<String, Integer> pageViews = new ConcurrentHashMap<>();
    private final Map<String, Set<String>> uniqueVisitors = new ConcurrentHashMap<>();
    private final Map<String, Integer> trafficSources = new ConcurrentHashMap<>();
    private final int TOP_N = 10;

    public RealTimeAnalytics() {
        ScheduledExecutorService scheduler = Executors.newSingleThreadScheduledExecutor();
        scheduler.scheduleAtFixedRate(this::updateDashboard, 5, 5, TimeUnit.SECONDS);
    }

    // Process incoming page view event
    public void processEvent(PageViewEvent event) {
        pageViews.merge(event.url, 1, Integer::sum);

        uniqueVisitors.computeIfAbsent(event.url, k -> ConcurrentHashMap.newKeySet()).add(event.userId);

        trafficSources.merge(event.source.toLowerCase(), 1, Integer::sum);
    }

    // Generate top N pages and traffic sources
    public void updateDashboard() {
        System.out.println("\n--- Dashboard Update ---");

        PriorityQueue<Map.Entry<String, Integer>> topPages = new PriorityQueue<>(Map.Entry.comparingByValue());
        for (Map.Entry<String, Integer> entry : pageViews.entrySet()) {
            topPages.offer(entry);
            if (topPages.size() > TOP_N) topPages.poll();
        }

        List<Map.Entry<String, Integer>> topList = new ArrayList<>();
        while (!topPages.isEmpty()) topList.add(topPages.poll());
        Collections.reverse(topList);

        System.out.println("Top Pages:");
        for (Map.Entry<String, Integer> entry : topList) {
            int unique = uniqueVisitors.getOrDefault(entry.getKey(), Collections.emptySet()).size();
            System.out.println(entry.getKey() + " - " + entry.getValue() + " views (" + unique + " unique)");
        }

        System.out.println("\nTraffic Sources:");
        int total = trafficSources.values().stream().mapToInt(Integer::intValue).sum();
        for (Map.Entry<String, Integer> entry : trafficSources.entrySet()) {
            double percent = total == 0 ? 0 : (entry.getValue() * 100.0 / total);
            System.out.printf("%s: %.1f%%\n", entry.getKey(), percent);
        }
    }

    public static void main(String[] args) throws InterruptedException {
        RealTimeAnalytics analytics = new RealTimeAnalytics();

        analytics.processEvent(new PageViewEvent("/article/breaking-news", "user_123", "Google"));
        analytics.processEvent(new PageViewEvent("/article/breaking-news", "user_456", "Facebook"));
        analytics.processEvent(new PageViewEvent("/sports/championship", "user_789", "Direct"));

        // Simulate multiple page views
        for (int i = 0; i < 500; i++) {
            analytics.processEvent(new PageViewEvent("/article/breaking-news", "user_" + i, "Google"));
            analytics.processEvent(new PageViewEvent("/sports/championship", "user_" + (i + 500), "Direct"));
        }

        // Keep program running to see scheduled dashboard updates
        Thread.sleep(10000);
    }
}