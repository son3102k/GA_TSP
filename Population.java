import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Random;
import java.util.Scanner;

public class Population {
    private static Random rd = new Random();
    private static final double Pm = 0.2;
    private static final double Pc = 0.8;
    private static final double R_OFFSPRING = 0.1;
    private utils utility = new utils();
    private static final int N_I = 10000;
    private static final int LEN_GEN = 48;
    private  ArrayList<Individual> l = new ArrayList<Individual>();

    public void Init() throws FileNotFoundException {

        Scanner sc = new Scanner(new BufferedReader(new FileReader("att48_d.txt")));
        Individual.COST = new int[LEN_GEN + 1][LEN_GEN + 1];
        while (sc.hasNextLine()) {
            for (int i = 1; i <= LEN_GEN; i++) {
                String[] line = sc.nextLine().trim().split(" ");
                for (int j = 0; j < line.length; j++) {
                    Individual.COST[i][j + 1] = Integer.parseInt(line[j]);
                }
            }
        }

        int[] genes = new int[LEN_GEN + 1];
        for (int i = 1; i <= LEN_GEN; i++) {
            genes[i] = i;
        }

        l.add(new Individual(LEN_GEN, genes));

        for (int j = 2; j <= N_I; j++) {
            utility.shuffleArray(genes);
            l.add(new Individual(LEN_GEN, genes));
        }

        sort_Rank();
    }

    public void sort_Rank() {
        Collections.sort(l, Collections.reverseOrder());
    }

    public ArrayList<Individual> parent_Selection () {
        ArrayList<Individual> parent_selected = new ArrayList<>();
        int n_idv_best = (int)(N_I*0.1*0.7);
        int n_idv_worst = (int)(N_I*0.1*0.3);
        for (int i = 0 ; i<n_idv_best ; i++) {
            parent_selected.add(this.l.get(i));
        }

        for (int i = this.l.size() - n_idv_worst ;  i<this.l.size() ; i++) {
            parent_selected.add(this.l.get(i));
           
        }

        return parent_selected;
    }

    public void elitism(ArrayList<Individual> offSpring_l) {
        for (int i = 0 ; i<offSpring_l.size() ; i++) {
            int idx = this.l.size()-1;
            this.l.remove(idx);
        }
        for (int i = 0 ; i<offSpring_l.size() ; i++) {
            this.l.add(offSpring_l.get(i));
        }
    }

    public static void main(String[] args) throws FileNotFoundException {
        Population p = new Population();
        p.Init();
        final int GENERATION = 9000;
        for (int i = 1 ; i<= GENERATION ; i++) {
            ArrayList<Individual> l_OffSpring = new ArrayList<Individual>();
            ArrayList<Individual> parent_selected =  p.parent_Selection();
            int count = 0 ;
            for (int j = 0 ; j <= parent_selected.size()-1 ; j++) {
                for (int q = j+1 ; q<parent_selected.size() ; q++) {
                    if (count== (int)(R_OFFSPRING*N_I)){
                        break;
                    }
                    else
                    {
                        if (rd.nextDouble()<Pc) {
                            count+=2;
                            ArrayList<Individual> rs = new ArrayList<>();
                            rs = parent_selected.get(j).PMX_crossOver(parent_selected.get(q));
                            if (rd.nextDouble()<Pm) {
                                rs.get(0).mutation();
                            }
                            if (rd.nextDouble()<Pm) {
                                rs.get(1).mutation();
                            }
                            l_OffSpring.add(rs.get(0));
                            l_OffSpring.add(rs.get(1));
                        }
                    }
                }
            }
            p.elitism(l_OffSpring);
            p.sort_Rank();
        }
        System.out.println(p.l.get(0).fitness);
        System.out.println(Arrays.toString(p.l.get(0).genes));
    }
}