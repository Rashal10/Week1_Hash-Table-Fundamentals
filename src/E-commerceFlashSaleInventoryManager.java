import java.util.*;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;

public class InventoryManager {
    private final Map<String, AtomicInteger> stockMap = new ConcurrentHashMap<>();
    private final Map<String, Queue<Integer>> waitingListMap = new ConcurrentHashMap<>();

    public void addProduct(String productId, int stock) {
        stockMap.put(productId, new AtomicInteger(stock));
        waitingListMap.put(productId, new ConcurrentLinkedQueue<>());
    }

    public int checkStock(String productId) {
        AtomicInteger stock = stockMap.get(productId);
        return stock != null ? stock.get() : 0;
    }

    public String purchaseItem(String productId, int userId) {
        AtomicInteger stock = stockMap.get(productId);
        if (stock == null) return "Product not found";

        while (true) {
            int currentStock = stock.get();
            if (currentStock > 0) {
                if (stock.compareAndSet(currentStock, currentStock - 1)) {
                    return "Success, " + (currentStock - 1) + " units remaining";
                }
            } else {
                waitingListMap.get(productId).add(userId);
                return "Added to waiting list, position #" + waitingListMap.get(productId).size();
            }
        }
    }

    public void restock(String productId, int amount) {
        AtomicInteger stock = stockMap.get(productId);
        if (stock == null) return;
        stock.addAndGet(amount);

        Queue<Integer> queue = waitingListMap.get(productId);
        while (stock.get() > 0 && !queue.isEmpty()) {
            int userId = queue.poll();
            stock.decrementAndGet();
            System.out.println("Notified user " + userId + " about available stock for " + productId);
        }
    }

    public static void main(String[] args) {
        InventoryManager manager = new InventoryManager();
        manager.addProduct("IPHONE15_256GB", 100);

        System.out.println(manager.checkStock("IPHONE15_256GB"));
        System.out.println(manager.purchaseItem("IPHONE15_256GB", 12345));
        System.out.println(manager.purchaseItem("IPHONE15_256GB", 67890));
    }
}