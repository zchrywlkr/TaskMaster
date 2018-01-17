/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package TaskMaster;
import java.util.ArrayList;
import java.util.concurrent.Phaser;
import java.util.function.Function;

/**
 *
 * @author Zach
 */
public class SyncList<In,Out> extends ArrayList<Out> {
    
    private int threadLimit;    
    private ArrayList<FunctionThread> threadList;
    private ArrayList<Out> resultList;     
    private int count = 0;
    private final Phaser phaser;
    private ListThread thread = new ListThread();
    
    public SyncList(int threadLimit){
        this.threadLimit = threadLimit;
        phaser = new Phaser(threadLimit * -1);
    }
    
    public boolean add(Function<In,Out> func, In obj){        
        if(thread.isAlive()){
            thread.interrupt();            
        }        
        threadList.add(new FunctionThread(func,obj));        
        thread.run();        
        return true;
    }
                
    private class FunctionThread extends Thread{
        private Function<In,Out> func;
        private In in;
        public FunctionThread(Function<In,Out> func, In in){
            this.in = in;
            this.func = func;
        }
    
        public void run(){
            Out out = func.apply(in);
            resultList.add(out);
            phaser.arriveAndDeregister();
        }
    }
    
    private class ListThread extends Thread{
        public void run(){             
            while(resultList.size() < threadList.size()){
                if(Thread.interrupted()){
                    return;
                }
                if(threadLimit >= phaser.getPhase()){
                    phaser.arriveAndAwaitAdvance();
                }
                phaser.register();
                threadList.get(count).run();
            }
        }
    }
}

