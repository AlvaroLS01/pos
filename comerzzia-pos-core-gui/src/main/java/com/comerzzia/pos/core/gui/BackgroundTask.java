package com.comerzzia.pos.core.gui;

import java.util.Observable;
import java.util.Observer;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.FutureTask;
import java.util.concurrent.TimeUnit;

import com.comerzzia.core.commons.CoreContextHolder;
import com.comerzzia.core.commons.exception.BusinessException;
import com.comerzzia.core.commons.sessions.ComerzziaThreadSession;
import com.comerzzia.pos.core.gui.components.progresswindow.ProgressWindow;
import com.comerzzia.pos.core.gui.util.thread.CzzThreadFactory;

import javafx.concurrent.Task;
import lombok.extern.log4j.Log4j;

/**
 * Clase para ejecutar tareas en un hilo distinto del UI Thread. Además de ejecutar la tarea (hereda de Task<V>), sacará 
 * la VentanaCargando.
 * Recordar llamar a super() al sobreescribir los métodos de esta clase.
 */
@Log4j
public abstract class BackgroundTask<V> extends Task<V> implements Observer {
	
    protected boolean showLoadingScreen = true;
    protected ExecutorService executor;
    protected long timeoutMillis = 0;

    public BackgroundTask(){
    }
    
    public BackgroundTask(boolean showLoadingScreen){
    	this();
		this.showLoadingScreen = showLoadingScreen;
    }
    
    public BackgroundTask(boolean showLoadingScreen, long timeout){
    	this(showLoadingScreen);
    	
    	setTimeoutMillis(timeout);
    }
    
    public BackgroundTask<V> setTimeoutMillis(long timeoutMillis) {
    	this.timeoutMillis = timeoutMillis;
    	
       return this;
    }


    public void start() {
    	showLoadingScreen();
    	CzzThreadFactory czzThreadFactory = new CzzThreadFactory(getClass().toString(), ComerzziaThreadSession.getComerzziasession());
        executor = Executors.newFixedThreadPool(2, czzThreadFactory);
        executor.submit(this);
    }
    
    @Override
    protected V call() throws Exception {
        FutureTask<V> futureTask = new FutureTask<>(this::execute);
        executor.submit(futureTask);
        try {
        	return timeoutMillis > 0 ? futureTask.get(timeoutMillis, TimeUnit.MILLISECONDS) : futureTask.get();
        } catch (ExecutionException e) {
        	if (e.getCause() instanceof Exception) {
        		throw (Exception)e.getCause(); // Unwrap execution exception
        	}else {
        		throw e;
        	}
        } finally {
        	clearExecutor();
        }
    }
    
    protected abstract V execute() throws Exception;

    /**
     * Igual que Task.getException() pero con casting a com.comerzzia.pos.util.exception.Exception. Si
     * getException() no es de tipo com.comerzzia.pos.util.exception.Exception, se devolverá null
     * @return Task.getException() con casting a com.comerzzia.pos.util.exception.Exception o una nueva BackgroundTaskException que engloba a Task.getException().
     */
    public BusinessException getCMZException(){
        final Throwable throwable = getException();
        if (throwable instanceof BusinessException){
            return (BusinessException) throwable;
        }
        
        return new BackgroundTaskException(throwable.getMessage(), throwable);
    }
    
    protected void showLoadingScreen(){
    	if(showLoadingScreen){
    		MainStageManager stageManager = (MainStageManager) CoreContextHolder.get().getBean("mainStageManager");
    		ProgressWindow.createProgressWindow(this, stageManager.getStage());
    	}
    }

    @Override
    public void update(Observable o, Object arg) {
		if (arg instanceof String) {
			updateProgressMessage(arg.toString());
		}
    }
    
    public void updateProgressMessage(String message) {
    	if(showLoadingScreen) {
    		updateMessage(message);
    	}
    }

    protected void clearExecutor(){
        executor.shutdown();
        try {
            if (!executor.awaitTermination(800, TimeUnit.MILLISECONDS)) {
                executor.shutdownNow();
            }
        } catch (InterruptedException e) {
            executor.shutdownNow();
        }
    }

}
