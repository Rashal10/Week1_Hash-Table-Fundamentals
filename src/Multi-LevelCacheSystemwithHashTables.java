import java.util.*;

class MultiLevelCache {

    private LinkedHashMap<String, String> L1;
    private Map<String, String> L2;
    private Map<String, String> L3;

    public MultiLevelCache() {

        L1 = new LinkedHashMap<>(10000, 0.75f, true) {
            protected boolean removeEldestEntry(Map.Entry eldest) {
                return size() > 10000;
            }
        };

        L2 = new HashMap<>();
        L3 = new HashMap<>();

        L3.put("video1", "Video Data A");
        L3.put("video2", "Video Data B");
        L3.put("video3", "Video Data C");
    }

    public String getVideo(String id) {

        if (L1.containsKey(id)) {
            System.out.println("L1 HIT");
            return L1.get(id);
        }

        if (L2.containsKey(id)) {
            System.out.println("L2 HIT → Promote to L1");
            String data = L2.get(id);
            L1.put(id, data);
            return data;
        }

        if (L3.containsKey(id)) {
            System.out.println("L3 HIT → Add to L2");
            String data = L3.get(id);
            L2.put(id, data);
            return data;
        }

        return null;
    }

    public static void main(String[] args) {

        MultiLevelCache cache = new MultiLevelCache();

        cache.getVideo("video1");
        cache.getVideo("video1");
        cache.getVideo("video2");
    }
}