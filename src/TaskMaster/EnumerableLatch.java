/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package TaskMaster;
import java.util.concurrent.CountDownLatch;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Zach
 */
public class EnumerableLatch{

    private CountDownLatch latch;
    private CountDownLatch blockerLatch;

    public EnumerableLatch(int count) {
        latch = new CountDownLatch(count);
    }    
    
    public void countDown(){
        try {
            blockerLatch.await();
        } catch (InterruptedException ex) {
            Logger.getLogger(EnumerableLatch.class.getName()).log(Level.SEVERE, null, ex);
        }
        latch.countDown();
    }
    
    public void CountUp(){
        blocker();        
        latch = new CountDownLatch((int) (latch.getCount() + 1));
        blockerLatch.countDown();
    }
    
    private void blocker(){
        try {
            blockerLatch.await(); 
        } catch (InterruptedException ex) {
            Logger.getLogger(EnumerableLatch.class.getName()).log(Level.SEVERE, null, ex);
        }        
        blockerLatch = new CountDownLatch(1);
    }        
}
