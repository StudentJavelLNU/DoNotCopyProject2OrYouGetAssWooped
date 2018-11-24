import java.io.IOException;

public class Main {

    public static void main(String args[]) throws IOException, InterruptedException {
        long start = System.currentTimeMillis();
        DiningPhilosopher dp = new DiningPhilosopher(); // create an instance to DiningPhilosopher class
        dp.DEBUG = true;
        int simulationTime = 10000;//*1000
        int seed = 100;
        if (args.length > 0) // check if parameters are passed as argument
            simulationTime = Integer.parseInt(args[0]); // the first parameter is the simulation time

        dp.initialize(simulationTime, seed); // initialize the required objects
        dp.start(); // start the simulation process
        Thread.sleep(1000);//Give time to interrupt
        dp.printTable();
        long end = System.currentTimeMillis();
        System.out.println(end - start);

        for (Philosopher phil : dp.philosophers) {
            System.out.println("Philosopher_" + phil.getId() + ": total time: " + (phil.getTotalEatingTime()
                    + phil.getTotalHungryTime() + phil.getTotalThinkingTime()));
        }
    }
}