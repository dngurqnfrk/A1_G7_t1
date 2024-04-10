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
                System.out.println(k);
                List<List<String>> Ck;
                // Ck.clear();
                Ck = apriori_gen(Lk);
                // t 가 Ck에 존재하는 지 확인
                System.out.println("size: "+Ck.size());
                for (List<String> e : Ck) {
                    int cnt=0;
                    // System.out.println(e);
                    for (List<String> transaction : transactions) {
                        if (transaction.containsAll(e)) {
                            cnt++;
                        }
                    }
                    C.put(e, cnt);
                }
                // count of c in Ck > minsup => Ct_1
                // ==> Lk = Ct_1
                Lk.clear();
                for (Map.Entry<List<String>, Integer> e : C.entrySet()) {
                    double sup = (double) e.getValue() / transactions.size();
                    // System.out.println(e.getKey()+" "+sup);
                    if (sup >= minsup) {
                        Lk.add(e.getKey());
                        // System.out.println("key: "+e.getKey()+" "+sup+" "+e.getValue());
                        L.put(e.getKey(), e.getValue());
                        // System.out.println(e.getKey()+" "+ e.getValue()+" "+sup);
                    }
                }
            }
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
                int l1=0,l2=0;
                boolean add = true;
                // System.out.println("--");
                while(l1<itemList1.size() && l2<itemList2.size()) {
                    // System.out.println(l1+" "+l2);
                    String s1 = itemList1.get(l1);
                    String s2 = itemList2.get(l2);
                    int sub = l1-l2;
                    if (sub<-1 || sub>1) {
                        add = false;
                        break;
                    }
                    if (s1.equals(s2)) {
                        cnt++;
                        intersection.add(s1);
                        l1++;
                        l2++;
                    }
                    else {
                        if (l2+1<itemList2.size() && s1.equals(itemList2.get(l2+1))) {
                            intersection.add(s2);
                            l2++;
                        }
                        else if (l1+1<itemList1.size() && s2.equals(itemList1.get(l1+1))) {
                            intersection.add(s1);
                            l1++;
                        }
                        else {
                            
                            if (s1.compareTo(s2) <= 0) {
                                intersection.add(s1);
                                intersection.add(s2);
                            }
                            else {
                                intersection.add(s2);
                                intersection.add(s1);
                            }
                            l1++;
                            l2++;
                        }
                    }
                }
                if (add && cnt == itemList1.size()-1) {
                    if (l1>l2)
                        intersection.add(itemList2.get(l2));
                    else if (l1<l2)
                        intersection.add(itemList1.get(l1));
                    C_out.add(intersection);
                }
                else
                    intersection.clear();
            }
        }
        return C_out;
    }
    
}

