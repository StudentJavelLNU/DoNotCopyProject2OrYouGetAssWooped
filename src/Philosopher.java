import java.util.Random;
import java.util.concurrent.TimeUnit;

/**
 * <h>To stop running, call <code>.setPhilosophising(false)</code> and wait 2000ms to be certain of full stop(interuption)</h>
 */
public class Philosopher implements Runnable {
    private enum State {
        EATING,
        THINKING,
        HUNGRY,
        PICKED_LEFT,
        PICKED_RIGHT,
        DROPPED_LEFT,
        DROPPED_RIGHT;
    }

    private int id;

    private final ChopStick leftChopStick;
    private final ChopStick rightChopStick;

    private Random randomGenerator = new Random();

    private int numberOfEatingTurns = 0;
    private int numberOfThinkingTurns = 0;
    private int numberOfHungryTurns = 0;

    //Why the fuck are these double? Are we using decimal milisecounds?????
    private double thinkingTime = 0;
    private double eatingTime = 0;

    private double hungerStartTime; //CurrentMillis start of hungergames
    private double hungerEndTime;
    private double hungryTime = 0;
    private State state;
    private volatile boolean fed = false;
    private volatile boolean philosophising = true;

    /**
     * @param id
     * @param leftChopStick
     * @param rightChopStick
     * @param seed
     */
    public Philosopher(int id, ChopStick leftChopStick, ChopStick rightChopStick, int seed) {
        this.id = id;
        this.leftChopStick = leftChopStick;
        this.rightChopStick = rightChopStick;

        /*
         * set the seed for this philosopher. To differentiate the seed from the other philosophers, we add the philosopher id to the seed.
         * the seed makes sure that the random numbers are the same every time the application is executed
         * the random number is not the same between multiple calls within the same program execution

         * NOTE
         * In order to get the same average values use the seed 100, and set the id of the philosopher starting from 0 to 4 (0,1,2,3,4).
         * Each philosopher sets the seed to the random number generator as seed+id.
         * The seed for each philosopher is as follows:
         * 	 	P0.seed = 100 + P0.id = 100 + 0 = 100
         * 		P1.seed = 100 + P1.id = 100 + 1 = 101
         * 		P2.seed = 100 + P2.id = 100 + 2 = 102
         * 		P3.seed = 100 + P3.id = 100 + 3 = 103
         * 		P4.seed = 100 + P4.id = 100 + 4 = 104
         * Therefore, if the ids of the philosophers are not 0,1,2,3,4 then different random numbers will be generated.
         */

        randomGenerator.setSeed(id + seed);

    }

    public int getId() {
        return id;
    }

    public double getAverageThinkingTime() {
        if (numberOfThinkingTurns == 0) {
            return 0.0;
        } else {
            return thinkingTime / numberOfThinkingTurns;
        }
    }

    public double getAverageEatingTime() {
        if (numberOfEatingTurns == 0) {
            return 0.0;
        } else {
            return eatingTime / numberOfEatingTurns;
        }
    }

    public double getAverageHungryTime() {
        if (numberOfHungryTurns == 0) {
            return 0.0;
        } else {
            return hungryTime / numberOfHungryTurns;
        }
    }

    public int getNumberOfThinkingTurns() {
        return numberOfThinkingTurns;
    }

    public int getNumberOfEatingTurns() {
        return numberOfEatingTurns;
    }

    public int getNumberOfHungryTurns() {
        return numberOfHungryTurns;
    }

    public double getTotalThinkingTime() {
        return thinkingTime;
    }

    public double getTotalEatingTime() {
        return eatingTime;
    }

    public double getTotalHungryTime() {
        return hungryTime;
    }

    /**
     * Control philosopher, set <code>philosophising</code> false to stop philosopher(interrupt).
     * Can take 2000ms to interrupt
     *
     * @param philosophising False stops philosophising, and sets fed to true, else sets philosophising = true
     */
    public void setPhilosophising(boolean philosophising) {
        if (philosophising == false) {
            this.fed = true;
            this.philosophising = false;
        } else {
            this.philosophising = true;
        }
    }


    /**
     * Returns if <code>philosophising </code> is set to false
     */
    @Override
    public void run() {
        /* TODO
         * Hungry,
         * Eat,
         * Repeat until thread is interrupted
         * Increment the thinking/eating turns after thinking/eating process has finished.
         * Add comprehensive comments to explain your implementation, including deadlock prevention/detection
         */

        //is in thinking?
        while (philosophising) {
            fed = false;
            state = State.THINKING;
            printState();
            think();

            state = State.HUNGRY;
            printState();
            hunger();

        }
    }

    /**
     * Only return once fed, it therefore calls (eat)
     */
    private void hunger() {
        try {
            hungerStartTime = System.currentTimeMillis();
            while (!fed) {
                //Try to access both chopsticks
                if (leftChopStick.myLock.tryLock(1, TimeUnit.MICROSECONDS)) {
                    state = State.PICKED_LEFT;
                    // printState();
                    //try to pick up and lock the other Chopstick
                    if (!rightChopStick.myLock.tryLock(1, TimeUnit.MILLISECONDS)) {
                        /* if (leftChopStick.myLock.getQueueLength() > 0) {
                            if (DiningPhilosopher.DEBUG) {
                                System.out.println("Deadlock philosopher_" + id);
                            }
                            //Sleep 4 milliseconds before trying again
                            Thread.sleep(randomGenerator.nextInt(4));
                            if (!rightChopStick.myLock.tryLock(1, TimeUnit.MILLISECONDS)) {
                        boolean temp = true;
                        while (!(leftChopStick.myLock.getQueueLength()> 0) &&temp) {
                            if(rightChopStick.myLock.tryLock(1, TimeUnit.MILLISECONDS)){
                                temp=false;
                            }
                            if (!philosophising) {
                                temp=false;
                            }
                            //If rightChopSticks lock was not aqcuired and leftChopstick is not wanted by another thread: sleep for  0 or 1ms
                            Thread.sleep(randomGenerator.nextInt(2));
                        }
                        if (!rightChopStick.myLock.isHeldByCurrentThread()) {
                            //leftChopStick has a queue, and right could not be atained
                            leftChopStick.myLock.unlock();
                            state = State.DROPPED_LEFT;
                            // printState();

                        */


                        if (DiningPhilosopher.DEBUG) {
                            System.out.println("Deadlock philosopher_" + id);
                        }
                        //Handle deadlocking by randomizing locking and unlocking, High starvationrate.
                        //TODO if time is given: Reduce starvation.
                        leftChopStick.myLock.unlock();
                        state = State.DROPPED_LEFT;
                        printState();

                    } else {
                        //Philosopher owns both chopsticks
                        hungerEndTime = System.currentTimeMillis();
                        hungryTime = hungerEndTime - hungerStartTime;
                        numberOfHungryTurns++;
                        state = State.PICKED_RIGHT;
                        printState();
                        state = State.EATING;
                        //printState();
                        if (philosophising) {
                            try {
                                eat();
                            } catch (IllegalAccessException e) {
                                System.out.println("Philosopher_" + id + ": tried eating without proper tools: "
                                        + e.getMessage());
                            }

                            fed = true;
                            rightChopStick.myLock.unlock();
                            state = State.DROPPED_RIGHT;
                            printState();
                            leftChopStick.myLock.unlock();
                            state = State.DROPPED_LEFT;
                            printState();
                        }
                    }
                }
                /*}
        }
                 }
}*/

            }
            return;

        } catch (InterruptedException e) {
            System.out.println("Error in chop sticking for philosopher_" + id + ": " + e.getMessage());
        }


    }


    /**
     * Puts thread to sleep for random time between [0,1000) milliseconds, adds it to
     * <code>thinkingTime</code> and increments <code>numberOfThinkingTurns</code>
     */
    private void think() {
        try {
            int rand = randomGenerator.nextInt(1000);
            Thread.sleep(rand);
            thinkingTime += rand;
            numberOfThinkingTurns++;

        } catch (InterruptedException e) {
            System.out.println("Error in thinking for philosopher: " + id + ": " + e.getMessage());
        }
    }

    /**
     * Puts thread to sleep for random time between [0,1000) milliseconds, adds it to
     * <code>eatingTime</code> and increments <code>numberOfEatingTurns</code>
     *
     * @throws IllegalArgumentException if method was called without owning locks to both left and right chopstick.
     */
    private void eat() throws IllegalAccessException {
        if (leftChopStick.myLock.isHeldByCurrentThread() && rightChopStick.myLock.isHeldByCurrentThread()) {
            try {
                int rand = randomGenerator.nextInt(1000);
                Thread.sleep(rand);
                eatingTime += rand;
                numberOfEatingTurns++;

            } catch (InterruptedException e) {
                System.out.println("Error in eating for philosopher_" + id + ": " + e.getMessage());
            }
        } else {
            throw new IllegalAccessException("Both the right and left chopstick must be obtained to eat");
        }
    }

    /**
     * Prints state of philosopher(if DiningPhilosopher.DEBUG = true)
     */
    public void printState() {
        if (DiningPhilosopher.DEBUG == true) {
            switch (state) {
                case EATING:
                    System.out.println("Philosopher_" + this.getId() + ": is Eating");
                    break;
                case THINKING:
                    System.out.println("Philosopher_" + this.getId() + ": is Thinking");
                    break;
                case HUNGRY:
                    System.out.println("Philosopher_" + this.getId() + ": is Hungry");
                    break;
                case PICKED_LEFT:
                    System.out.println("Philosopher_" + this.getId() + ": picked up his left chopstick "
                            + leftChopStick.getId());
                    break;
                case PICKED_RIGHT:
                    System.out.println("Philosopher_" + this.getId() + ": picked up his right chopstick_"
                            + rightChopStick.getId());
                    break;
                case DROPPED_LEFT:
                    System.out.println("Philosopher_" + this.getId() + ": dropped his left chopstick_"
                            + leftChopStick.getId());
                    break;
                case DROPPED_RIGHT:
                    System.out.println("Philosopher_" + this.getId() + ": dropped his right chopstick_"
                            + rightChopStick.getId());
                    break;
            }
        }
    }
}
