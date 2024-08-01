package com.alibaba.matrix.flow;

import com.alibaba.matrix.logging.MatrixLoggingSystem;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author jifang.zjf@alibaba-inc.com (FeiQing)
 * @version 1.0
 * @since 2017/4/10 15:47.
 */
@SuppressWarnings({"rawtypes", "unchecked"})
public abstract class FlowNode<Context, Result> {

    public static final Logger logger;

    static {
        MatrixLoggingSystem.initLoggingSystem("classpath:matrix-flow-logback.xml", "classpath:matrix-flow-log4j2.xml");
        logger = LoggerFactory.getLogger("FLOW");
    }

    protected String name;

    protected abstract Result execute(Context context) throws Throwable;

    protected Flow.Meta getMeta() {
        return Flow.metas.get().get(name);
    }

    protected String desc(Context context) {
        return this.getClass().getName();
    }

    protected final Result next() throws Throwable {
        Flow.Meta meta = Flow.metas.get().get(name);
        // 已经到达终点
        if (!(++meta.idx < meta.nodes.length)) {
            throw new IllegalStateException("[FLOW:" + name + "] has already reach end !");
        }

        FlowNode node = meta.nodes[meta.idx];
        String desc = String.format("'%s:%d:%s'", name, meta.idx, node.desc(meta.context));

        try {
            meta.setAttribute(desc, System.currentTimeMillis());
            return (Result) node.execute(meta.context);
        } catch (Throwable t) {
            logger.error("{} throw exception.", desc, t);
            throw t;
        } finally {
            long total = System.currentTimeMillis() - (long) meta.removeAttribute(desc);
            if (logger.isTraceEnabled()) {
                logger.trace("{} cost {}/{}.", desc, total - meta.cost, total);
            }
            meta.cost = total;
        }
    }
}
