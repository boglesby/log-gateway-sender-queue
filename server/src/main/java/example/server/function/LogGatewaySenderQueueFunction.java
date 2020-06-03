package example.server.function;

import org.apache.geode.cache.Cache;
import org.apache.geode.cache.CacheFactory;
import org.apache.geode.cache.Declarable;

import org.apache.geode.cache.execute.Function;
import org.apache.geode.cache.execute.FunctionContext;

import org.apache.geode.cache.wan.GatewaySender;

import java.util.Arrays;

public class LogGatewaySenderQueueFunction implements Function, Declarable {
  
  private final Cache cache;

  public LogGatewaySenderQueueFunction() {
    this.cache = CacheFactory.getAnyInstance();
  }

  @Override
  public void execute(FunctionContext context) {
    // Get the sender id and group-by arguments
    Object[] arguments = (Object[]) context.getArguments();
    String senderIdsArg = (String) arguments[0];
    String[] senderIds = senderIdsArg.split(",");
    boolean groupByBucket = (Boolean) arguments[1];
    System.out.println("Executing function=" + getId() + "; senderIds=" + Arrays.toString(senderIds) + "; groupByBucket=" + groupByBucket);
    this.cache.getLogger().info("Executing function=" + getId() + "; senderIds=" + Arrays.toString(senderIds));

    // Iterate the sender ids
    for (String senderId : senderIds) {
      // Get the GatewaySender for the sender id
      GatewaySender sender = this.cache.getGatewaySender(senderId);
      
      // Process the GatewaySender
      if (sender == null) {
        this.cache.getLogger().warning("GatewaySender " + senderId + " doesn't exist so its queues cannot be logged");
      } else {
        getLogger(sender, groupByBucket).logQueue();
      }
    }

    // Send the response
    context.getResultSender().lastResult(true);
  }
  
  private GatewaySenderQueueLogger getLogger(GatewaySender sender, boolean groupByBucket) {
    GatewaySenderQueueLogger logger = null;
    if (sender.isParallel()) {
      logger = groupByBucket
        ? new ParallelGatewaySenderQueueByBucketLogger(this.cache, sender)
        : new ParallelGatewaySenderQueueLogger(this.cache, sender);
    } else {
      logger = new SerialGatewaySenderQueueLogger(this.cache, sender);
    }
    return logger;
  }

  @Override
  public String getId() {
    return getClass().getSimpleName();
  }
}
