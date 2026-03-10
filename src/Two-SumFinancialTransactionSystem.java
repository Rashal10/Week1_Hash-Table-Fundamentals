import java.util.*;

class Transaction {
    int id;
    int amount;
    String merchant;
    long time;

    Transaction(int id, int amount, String merchant, long time) {
        this.id = id;
        this.amount = amount;
        this.merchant = merchant;
        this.time = time;
    }
}

public class FraudDetector {

    public static List<int[]> twoSum(List<Transaction> txns, int target) {

        Map<Integer, Transaction> map = new HashMap<>();
        List<int[]> result = new ArrayList<>();

        for (Transaction t : txns) {

            int complement = target - t.amount;

            if (map.containsKey(complement)) {
                result.add(new int[]{map.get(complement).id, t.id});
            }

            map.put(t.amount, t);
        }

        return result;
    }

    public static void detectDuplicates(List<Transaction> txns) {

        Map<String, List<Transaction>> map = new HashMap<>();

        for (Transaction t : txns) {

            String key = t.amount + "-" + t.merchant;

            map.putIfAbsent(key, new ArrayList<>());
            map.get(key).add(t);
        }

        for (String k : map.keySet()) {
            if (map.get(k).size() > 1) {
                System.out.println("Duplicate transactions: " + map.get(k).size());
            }
        }
    }

    public static void main(String[] args) {

        List<Transaction> txns = new ArrayList<>();

        txns.add(new Transaction(1, 500, "StoreA", 1000));
        txns.add(new Transaction(2, 300, "StoreB", 1010));
        txns.add(new Transaction(3, 200, "StoreC", 1020));

        List<int[]> pairs = twoSum(txns, 500);

        for (int[] p : pairs)
            System.out.println("Pair: " + p[0] + " + " + p[1]);

        detectDuplicates(txns);
    }
}