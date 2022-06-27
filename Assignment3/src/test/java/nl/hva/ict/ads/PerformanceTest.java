package nl.hva.ict.ads;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class PerformanceTest {

    protected Sorter<Archer> sorter = new ArcherSorter();
    protected List<Archer> archers;

    protected Comparator<Archer> scoringScheme = Archer::compareByHighestTotalScoreWithLeastMissesAndLowestId;
    ChampionSelector championSelector = new ChampionSelector(1L);

    @BeforeEach
    private void setup(){
        archers = new ArrayList(championSelector.enrollArchers(100));
    }


    @Test
    void performanceTestSelInsSort(){
        long started;
        long finished;
        double time = 0;
        ArrayList<Double> times = new ArrayList<>();

        System.gc();
        System.out.println("SelectionInsertSort:\n");
        while (archers.size() < 5000000 && time <= 20000){
            started = System.nanoTime();
            sorter.selInsSort(archers, scoringScheme);
            finished = System.nanoTime();
            time = (finished-started)/1E6;
            System.out.printf("%d archers sorted in %.3f ms\n",archers.size(), time);
            times.add(time);
            archers = championSelector.enrollArchers(archers.size());
        }
        System.out.println(times);
    }

    @Test
    void performanceTestQuickSort(){
        long started;
        long finished;
        double time = 0;
        ArrayList<Double> times = new ArrayList<>();

        System.gc();
        System.out.println("QuickSort:\n");
        while (archers.size() < 5000000 && time <= 20000){
            started = System.nanoTime();
            sorter.quickSort(archers, scoringScheme);
            finished = System.nanoTime();
            time = (finished-started)/1E6;
            System.out.printf("%d archers sorted in %.3f ms\n",archers.size(), time);
            times.add(time);
            archers = championSelector.enrollArchers(archers.size());
        }
        System.out.println(times);
    }


}
