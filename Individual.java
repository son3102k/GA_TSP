import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Random;

public class Individual implements Comparable<Individual>{
    public static int[][] COST;
    private int len_genes;
    public int[] genes;
    public int fitness = 0;
    public void caculate_fitness() {
        int fitness = 0;
        for (int i = 1 ; i<len_genes ; i++) {
            fitness -= COST[genes[i]][genes[i+1]];
        }
        fitness -= COST[genes[len_genes]][genes[1]];
        this.fitness = fitness;
    }
    public Individual(int n, int[] a) throws FileNotFoundException{
        this.len_genes = n;
        genes = new int[n+1];
        for (int i = 1 ; i<n ; i++) {
            genes[i] = a[i];
            fitness -= COST[a[i]][a[i+1]];
        }
        genes[n] = a[n];
        fitness -= COST[a[n]][a[1]];
    }

    public void mutation(Random rd) {
        int idx1, idx2;
        idx1 = rd.nextInt(len_genes)+1;
        while ((idx2=rd.nextInt(len_genes)+1) == idx1);
        int tmp = genes[idx1];
        genes[idx1] = genes[idx2];
        genes[idx2] = tmp;
        caculate_fitness();
    }

    public ArrayList<Individual> PMX_crossOver(Individual other,Random rd) throws FileNotFoundException {
        ArrayList<Individual> l = new ArrayList<Individual>();
        int[] offSpring1 = new int[len_genes+1];
        int[] offSpring2 = new int[len_genes+1];
        
        int idx1 = rd.nextInt(len_genes/3)+1;
        int idx2 = rd.nextInt((len_genes/3))+ (2*len_genes/3)+1;
        HashMap<Integer,Integer> o1_map_o2 = new HashMap<Integer,Integer>();
        HashMap<Integer,Integer> o2_map_o1 = new HashMap<Integer,Integer>();
        
        for (int i = idx1 ; i<=idx2 ; i++) {
            offSpring1[i] = other.genes[i];
            
            offSpring2[i] = this.genes[i];
            
            o1_map_o2.put(offSpring1[i], offSpring2[i]);
            o2_map_o1.put(offSpring2[i], offSpring1[i]);
        }

        // mapping non-collision

        for (int i = 1 ; i<=len_genes ; i++) {
            if (i==idx1) {
                i=idx2;
                continue;
            }
            if (!o1_map_o2.containsKey(this.genes[i])) {
                offSpring1[i] = this.genes[i];
                
            }
            if (!o2_map_o1.containsKey(other.genes[i])) {
                offSpring2[i] = other.genes[i];
            }

            if (offSpring1[i]!=0) {
                o1_map_o2.put(offSpring1[i], offSpring2[i]);
            }
            if (offSpring2[i]!=0) {
                o2_map_o1.put(offSpring2[i], offSpring1[i]);
            }
        }

        // mapping collision
        
        for (int i = 1 ; i<=len_genes ; i++) {
            if (i==idx1) {
                i=idx2;
                continue;
            }
            if (offSpring1[i]==0) {
                Integer key = this.genes[i];
                while (o1_map_o2.containsKey(key)) {
                    key = o1_map_o2.get(key);
                }
                offSpring1[i] = key;
            }

            if (offSpring2[i]==0) {
                Integer key = other.genes[i];
                while (o2_map_o1.containsKey(key)) {
                    key = o2_map_o1.get(key);
                }
                offSpring2[i] = key;
            }
        }

        l.add(new Individual(len_genes, offSpring1));
        l.add(new Individual(len_genes, offSpring2));

        return l;
    }

    @Override
    public int compareTo(Individual o) {
        
        Integer t = this.fitness;
        Integer q = o.fitness;
        return  t.compareTo(q);
    }

}