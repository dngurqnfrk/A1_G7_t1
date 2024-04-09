import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.*;

public class A1_G7_t1 {
    static HashMap<String, Integer> hashMap = new HashMap<>();
    static HashMap<List<String>, Integer> L = new HashMap<>();
    static HashMap<List<String>, Integer> C = new HashMap<>();
    // static HashMap<List<String>, Integer> transaction = new HashMap<>();
    static List<List<String>> transactions = new ArrayList<>();
    public static void main(String[] args) throws IOException {
        if (args.length < 1) {
            System.exit(1);
        }
        try {
            String fileName = args[0];
            Double minsup = Double.valueOf(args[1]);
            String line;
            String[] tokens;
    
            BufferedReader br = new BufferedReader(new FileReader(fileName));
            Integer N = 0;
            Integer item_num = 0;
            while ((line = br.readLine()) != null) {
                N++;
                tokens = line.strip().split(",");
                transactions.add(Arrays.asList(tokens));
                // HashMap<String, Integer> elemnet = new HashMap<>();
                // for (int i=0;i<tokens.length;i++) {
                //     elemnet.put(tokens[i], i);
                //     if (hashMap.containsKey(tokens[i]))
                //         hashMap.put(tokens[i], hashMap.get(tokens[i]) + 1);
                //     else {
                //         hashMap.put(tokens[i], 0);
                //         item_num++;
                //     }
                // }
                // transaction.put(elemnet, N);
            }
            br.close();
            // Generate C_1
            for (List<String> transaction : transactions) {
                for (String item : transaction) {
                    List<String> itemSet = Collections.singletonList(item);
                    C.put(itemSet, C.getOrDefault(itemSet, 0) + 1);
                }
            }
            List<List<String>> Lk = new ArrayList<>();
            for (Map.Entry<List<String>, Integer> e : C.entrySet()) {
                double sup = (double) e.getValue() / transactions.size();
                if (sup >= minsup) {
                    Lk.add(e.getKey());
                    L.put(e.getKey(), e.getValue());
                    // System.out.println(e.getKey()+" "+ e.getValue()+" "+sup);
                }
            }
            Collections.sort(Lk, new Comparator<List<String>>() {
                @Override
                public int compare(List<String> list1, List<String> list2) {
                    return list1.get(0).compareTo(list2.get(0));
                }
            });
    
            // return ;


            for (int k=2; Lk.size() != 0; k++) {
                List<List<String>> Ck;
                Ck = apriori_gen(Lk);
                // System.out.println("Ck "+k);
                // for (List<String> e : Ck) {
                //     System.out.println(e);
                // }
                // System.out.println("\n");
                // t 가 Ck에 존재하는 지 확인
                for (List<String> transaction : transactions) {
                    for (List<String> e : Ck) {
                        if (transaction.containsAll(e)) {
                            // System.out.println("Ck: "+e);
                            C.put(e, C.getOrDefault(e, 0) + 1);
                        }
                    }
                }
                // count of c in Ck > minsup => Ct_1
                // ==> Lk = Ct_1
                Lk.clear();
                for (Map.Entry<List<String>, Integer> e : C.entrySet()) {
                    double sup = (double) e.getValue() / transactions.size();
                    // System.out.println(e.getKey()+" "+sup);
                    if (sup >= minsup) {
                        Lk.add(e.getKey());
                        L.put(e.getKey(), e.getValue());
                        // System.out.println(e.getKey()+" "+ e.getValue()+" "+sup);
                    }
                }
                // for (List<String> e : Lk) {
                //     System.out.println(e);
                // }
            }
            // System.out.println("-------------------------------------");
            // for (Map.Entry<List<String>, Integer> e : L.entrySet()) {
            //     double sup = (double) e.getValue() / transactions.size();
            //     System.out.println(e.getKey()+" "+ sup);
            // }
            List<Map.Entry<List<String>, Integer>> eList = new ArrayList<>(L.entrySet());
            Collections.sort(eList, new Comparator<Map.Entry<List<String>, Integer>>() {
                @Override
                public int compare(Map.Entry<List<String>, Integer> e1, Map.Entry<List<String>, Integer> e2) {
                    return e1.getValue().compareTo(e2.getValue());
                }
            });
            for (Map.Entry<List<String>, Integer> e : eList) {
                double sup = (double) e.getValue() / transactions.size();
                System.out.println(e.getKey()+" "+ sup);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    static List<List<String>> apriori_gen(List<List<String>> Lk) {
        List<List<String>> C_out = new ArrayList<>();
        C.clear();
        // k-1 해쉬맵으로 k 조합 생성
        // 시간복잡도 줄이는 방법 생각 필요
        for (int i=0; i<Lk.size(); i++) {
            for (int j=i+1; j<Lk.size(); j++) {
                List<String> itemList1 = Lk.get(i);
                List<String> itemList2 = Lk.get(j);
                List<String> intersection = new ArrayList<>();
                int cnt=0;
                for (int l=0; l<itemList1.size(); l++) {
                    String s1 = itemList1.get(l);
                    String s2 = itemList2.get(l);
                    if (s1.equals(s2))
                        intersection.add(s1);
                    else {
                        cnt++;
                        if (s1.compareTo(s2) <= 0) {
                            intersection.add(s1);
                            intersection.add(s2);
                        }
                        else {
                            intersection.add(s2);
                            intersection.add(s1);
                        }
                    }
                }
                if (cnt != 1)
                    intersection.clear();
                else
                    C_out.add(intersection);
                // intersection.retainAll(itemList2);
                // if (intersection.size() == itemList1.size() - 1) {
                //     String s1 = itemList1.get(itemList1.size()-1);
                //     String s2 = itemList2.get(itemList2.size()-1);
                //     for (String e: itemList1) {
                //         System.out.println("item1: "+e);
                //     }
                //     for (String e: intersection) {
                //         System.out.println("in: "+e+" "+s1+" "+s2);
                //     }
                //     System.out.println("\n");
                //     if (s1.compareTo(s2) <= 0) {
                //         intersection.add(s1);
                //         intersection.add(s2);
                //     }
                //     else {
                //         intersection.add(s2);
                //         intersection.add(s1);
                //     }
                //     C_out.add(intersection);
                // }
            }
        }
        return C_out;
    }
    
}

class Box {
    HashMap<String, Integer> set;
    Integer size;
    Box(Integer k) {
        this.size = 0;
        this.set = new HashMap<>();
    }
    Box(String str) {
        this.size = 1;
        this.set = new HashMap<>();
        set.put(str, 1);
    }
    void add(String str) {
        set.put(str, ++size);
    }
}