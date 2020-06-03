package example.server.function;

import org.apache.geode.cache.Cache;

import org.apache.geode.cache.partition.PartitionRegionHelper;

import org.apache.geode.cache.wan.GatewayQueueEvent;
import org.apache.geode.cache.wan.GatewaySender;

import org.apache.geode.internal.cache.LocalDataSet;
import org.apache.geode.internal.cache.PartitionedRegion;

import org.apache.geode.internal.cache.wan.parallel.ParallelGatewaySenderQueue;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class ParallelGatewaySenderQueueLogger extends AbstractGatewaySenderQueueLogger {
  
  public ParallelGatewaySenderQueueLogger(Cache cache, GatewaySender sender) {
    super(cache, sender);
  }

  public void logQueue() {
    // Get the region implementing the queue
    PartitionedRegion region = (PartitionedRegion) this.cache.getRegion(sender.getId() + ParallelGatewaySenderQueue.QSTRING);
    
    // Log the primary queue
    Map<Long,GatewayQueueEvent> primaryData = PartitionRegionHelper.getLocalPrimaryData(region);
    logQueue(primaryData, true);
    
    // Log the secondary queue
    Map<Long,GatewayQueueEvent> secondaryData = getLocalSecondaryData(region);
    logQueue(secondaryData, false);
  }
  
  private void logQueue(Map<Long,GatewayQueueEvent> localData, boolean isPrimary) {
    String queueContents = getContents(localData, getHeader(localData, isPrimary));
    this.cache.getLogger().info(queueContents);
    System.out.println(queueContents);
  }
  
  private String getHeader(Map<Long,GatewayQueueEvent> localRegion, boolean isPrimary) {
    return new StringBuilder()
      .append("\nThe queue for parallel GatewaySender ")
      .append(this.sender.getId())
      .append(" contains the following ")
      .append(localRegion.size())
      .append(" ")
      .append(isPrimary ? "primary" : "secondary")
      .append(" entries:\n")
      .toString();
  }
  
  private Map<Long,GatewayQueueEvent> getLocalSecondaryData(PartitionedRegion region) {
    Set<Integer> primaryBucketIds = region.getDataStore().getAllLocalPrimaryBucketIds();
    Set<Integer> allBucketIds = new HashSet<>(region.getDataStore().getAllLocalBucketIds());
    allBucketIds.removeAll(primaryBucketIds);
    return new LocalDataSet(region, allBucketIds);
  }
}
