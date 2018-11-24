import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;


public class DiningPhilosopher {

    /*
     * Controls whether logs should be shown on the console or not.
     * Logs should print events such as: state of the philosopher, and state of the chopstick
     * 		for example: philosopher # is eating;
     * 		philosopher # picked up his left chopstick (chopstick #)
     */
    public static volatile boolean DEBUG = false;   //Volatile since only accessed from constructionThread
    //thereof no need for atomicity
    private final int NUMBER_OF_PHILOSOPHERS = 5;
    private int SIMULATION_TIME = 10000;
    private int SEED = 0;

    ExecutorService executorService = null;
    ArrayList<Philosopher> philosophers = null;
    ArrayList<ChopStick> chopSticks = null;

    public void start() throws InterruptedException {
        try {
            /*
             * First we start two non-adjacent threads, which are T1 and T3
             */
            for (int i = 1; i < NUMBER_OF_PHILOSOPHERS; i += 2) {
                executorService.execute(philosophers.get(i));
                Thread.sleep(50); //makes sure that this thread kicks in before the next one
            }

            /*
             * Now we start the rest of the threads, which are T0, T2, and T4
             */
            for (int i = 0; i < NUMBER_OF_PHILOSOPHERS; i += 2) {
                executorService.execute(philosophers.get(i));
                Thread.sleep(50); //makes sure that this thread kicks in before the next one
            }

            // Main thread sleeps till time of simulation
            Thread.sleep(SIMULATION_TIME);

            //Makes philosophers stop philosophising:
            for (int i = 0; i < NUMBER_OF_PHILOSOPHERS; i++) {
                philosophers.get(i).setPhilosophising(false);
            }


        } finally {
            executorService.shutdown();
            executorService.awaitTermination(10, TimeUnit.MILLISECONDS);
        }
    }

    public void initialize(int simulationTime, int randomSeed) {
        SIMULATION_TIME = simulationTime;
        SEED = randomSeed;

        philosophers = new ArrayList<Philosopher>(NUMBER_OF_PHILOSOPHERS);
        chopSticks = new ArrayList<ChopStick>(NUMBER_OF_PHILOSOPHERS);

        //create the executor service
        executorService = Executors.newFixedThreadPool(NUMBER_OF_PHILOSOPHERS);

        //Adding chopsticks
        for (int i = 0; i < NUMBER_OF_PHILOSOPHERS; i++) {
            chopSticks.add(new ChopStick(i)); // Adding with id i
        }
        //Add philosophers and hand out chopsticks
        for (int i = 0; i < NUMBER_OF_PHILOSOPHERS; i++) {//offsetting from 0, left chopstick is phil_id+1
            Philosopher phil;
            //Give the last philosopher chopstick 4 and 0;
            if (i == NUMBER_OF_PHILOSOPHERS - 1) {
                phil = new Philosopher(i, chopSticks.get(0), chopSticks.get(i), randomSeed);
                philosophers.add(phil);
            } else {
                phil = new Philosopher(i, chopSticks.get(i + 1), chopSticks.get(i), randomSeed);
                philosophers.add(phil);

            }
        }
    }

    public ArrayList<Philosopher> getPhilosophers() {
        return philosophers;
    }

    /*
     * The following code prints a table where each row corresponds to one of the Philosophers,
     * Columns correspond to the Philosopher ID (PID), average thinking time (ATT), average eating time (AET), average hungry time (AHT), number of thinking turns(#TT), number of eating turns(#ET), and number of hungry turns(#HT).
     * This table should be printed regardless of the DEBUG value
     */
    public void printTable() {
        DecimalFormat df2 = new DecimalFormat(".##");
        System.out.println("\n---------------------------------------------------");
//        System.out.println("PID \tATT \tAET \tAHT \t#TT \t#ET \t#HT");
        StringBuilder stringBuilder = new StringBuilder();

        stringBuilder.append(String.format("%3s", "PID"));
        stringBuilder.append("\t");
        stringBuilder.append(String.format("%6s", "ATT"));
        stringBuilder.append("\t");
        stringBuilder.append(String.format("%6s", "AET"));
        stringBuilder.append("\t");
        stringBuilder.append(String.format("%6s", "AHT"));
        stringBuilder.append("\t");
        stringBuilder.append(String.format("%3s", "#TT"));
        stringBuilder.append("\t");
        stringBuilder.append(String.format("%3s", "#ET"));
        stringBuilder.append("\t");
        stringBuilder.append(String.format("%3s", "#HT"));
        System.out.println(stringBuilder);
        stringBuilder = new StringBuilder();
        for (Philosopher p : philosophers) {

            stringBuilder.append(String.format("%3s", p.getId()));
            stringBuilder.append("\t");
            stringBuilder.append(String.format("%6s", df2.format(p.getAverageThinkingTime())));
            stringBuilder.append("\t");
            stringBuilder.append(String.format("%6s", df2.format(p.getAverageEatingTime())));
            stringBuilder.append("\t");
            stringBuilder.append(String.format("%6s", df2.format(p.getAverageHungryTime())));
            stringBuilder.append("\t");
            stringBuilder.append(String.format("%3s", p.getNumberOfThinkingTurns()));
            stringBuilder.append("\t");
            stringBuilder.append(String.format("%3s", p.getNumberOfEatingTurns()));
            stringBuilder.append("\t");
            stringBuilder.append(String.format("%3s", p.getNumberOfHungryTurns()));
            stringBuilder.append("\n");

        }
        System.out.println(stringBuilder);
        System.out.println("---------------------------------------------------\n");
    }
}
