import java.util.concurrent.locks.ReentrantLock;

/**
 * Locking and unlocking was implemented in the <code>Philosopher.hunger()</code>
 */
public class ChopStick {
    private final int id;
    ReentrantLock myLock = new ReentrantLock();

    public ChopStick(int id) {
        this.id = id;
    }


    public int getId() {
        return id;
    }
}

