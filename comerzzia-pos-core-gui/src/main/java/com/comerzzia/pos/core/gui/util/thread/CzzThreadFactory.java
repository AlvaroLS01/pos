package com.comerzzia.pos.core.gui.util.thread;

import java.util.concurrent.ThreadFactory;
import java.util.concurrent.atomic.AtomicInteger;

import org.slf4j.MDC;

import com.comerzzia.core.commons.sessions.ComerzziaSession;
import com.comerzzia.core.commons.sessions.ComerzziaThreadSession;

import lombok.Data;

@Data
/**
 * CzzThreadFactory is a custom thread factory that creates threads with a specific naming pattern and
 * allows for session propagation.
 * <p>
 * It can be used to create threads that are aware of the ComerzziaSession, which can be useful for
 * maintaining user context across different threads.
 * </p>
 *
 * The implementation is based on the default thread factory from java.util.concurrent.Executors.DefaultThreadFactory.
 */
public class CzzThreadFactory implements ThreadFactory {

    //Default thread factory (from java.util.concurrent.Executors.DefaultThreadFactory)
	protected static final AtomicInteger poolNumber = new AtomicInteger(1);
    protected final ThreadGroup group;
    protected final AtomicInteger threadNumber = new AtomicInteger(1);
    protected final String namePrefix;

    //Czz thread factory
    protected ComerzziaSession comerzziaSession;
    protected boolean daemon;

    public CzzThreadFactory() {
        SecurityManager s = System.getSecurityManager();
        group = (s != null) ? s.getThreadGroup() :
                Thread.currentThread().getThreadGroup();
        namePrefix = "pool-" +
                poolNumber.getAndIncrement() +
                "-thread-";
    }

    public CzzThreadFactory(String namePrefix) {
        SecurityManager s = System.getSecurityManager();
        group = (s != null) ? s.getThreadGroup() :
                Thread.currentThread().getThreadGroup();
        this.namePrefix = namePrefix + "-" +
                poolNumber.getAndIncrement();
    }

    public CzzThreadFactory(String namePrefix, ComerzziaSession comerzziaSession) {
        this(namePrefix);
        this.comerzziaSession = comerzziaSession;
    }

    @Override
    public Thread newThread(Runnable r) {
        Thread t = new Thread(
                group,
                comerzziaSession != null ? new SessionPropagator(r) : r,
                namePrefix + threadNumber.getAndIncrement(),
                0);

        t.setDaemon(daemon);

        if (t.getPriority() != Thread.NORM_PRIORITY)
            t.setPriority(Thread.NORM_PRIORITY);

        return t;
    }

    public class SessionPropagator implements Runnable {
        private final Runnable delegate;

        public SessionPropagator(Runnable target) {
            this.delegate = target;
        }

        @Override
        public void run() {
            ComerzziaThreadSession.setComerzziaSession(comerzziaSession);

            if (comerzziaSession.getUser() != null) {
                MDC.put("user", comerzziaSession.getUser().getUserCode());
            } else {
                MDC.put("user", "boot");
            }
            try {
                delegate.run();
            } finally {
                ComerzziaThreadSession.clear();
            }
        }
    }
}
