import java.util.*;
import java.util.concurrent.*;

class DNSEntry {
    String ip;
    long expiryTime;

    DNSEntry(String ip, long ttlSeconds) {
        this.ip = ip;
        this.expiryTime = System.currentTimeMillis() + ttlSeconds * 1000;
    }

    boolean isExpired() {
        return System.currentTimeMillis() > expiryTime;
    }
}

public class DNSCache {
    private final Map<String, DNSEntry> cache;
    private final int capacity;
    private int hits = 0, misses = 0;

    public DNSCache(int capacity) {
        this.capacity = capacity;
        this.cache = new LinkedHashMap<String, DNSEntry>(capacity, 0.75f, true) {
            protected boolean removeEldestEntry(Map.Entry<String, DNSEntry> eldest) {
                return size() > DNSCache.this.capacity;
            }
        };
        startCleanupThread();
    }

    private void startCleanupThread() {
        ScheduledExecutorService executor = Executors.newSingleThreadScheduledExecutor();
        executor.scheduleAtFixedRate(() -> {
            cache.entrySet().removeIf(entry -> entry.getValue().isExpired());
        }, 10, 10, TimeUnit.SECONDS);
    }

    public String resolve(String domain) {
        synchronized (cache) {
            DNSEntry entry = cache.get(domain);
            if (entry != null && !entry.isExpired()) {
                hits++;
                return "Cache HIT → " + entry.ip;
            } else {
                misses++;
                String ip = queryUpstream(domain);
                cache.put(domain, new DNSEntry(ip, 300));
                return "Cache MISS → " + ip;
            }
        }
    }

    private String queryUpstream(String domain) {
        return "172.217.14." + new Random().nextInt(255);
    }

    public String getStats() {
        int total = hits + misses;
        double hitRate = total == 0 ? 0 : (hits * 100.0 / total);
        return String.format("Hit Rate: %.2f%%, Lookups: %d", hitRate, total);
    }

    public static void main(String[] args) throws InterruptedException {
        DNSCache cache = new DNSCache(3);
        System.out.println(cache.resolve("google.com"));
        System.out.println(cache.resolve("google.com"));
        Thread.sleep(301_000); // wait 301 seconds
        System.out.println(cache.resolve("google.com"));
        System.out.println(cache.getStats());
    }
}