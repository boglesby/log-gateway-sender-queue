package example.server.function;

import org.apache.geode.cache.Cache;

import org.apache.geode.cache.partition.PartitionRegionHelper;

import org.apache.geode.cache.wan.GatewayQueueEvent;
import org.apache.geode.cache.wan.GatewaySender;

import org.apache.geode.internal.cache.BucketRegion;
import org.apache.geode.internal.cache.LocalDataSet;
import org.apache.geode.internal.cache.PartitionedRegion;

import org.apache.geode.internal.cache.wan.parallel.ParallelGatewaySenderQueue;

import java.util.Comparator;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import java.util.stream.Collectors;

public class ParallelGatewaySenderQueueByBucketLogger extends AbstractGatewaySenderQueueLogger {

  public ParallelGatewaySenderQueueByBucketLogger(Cache cache, GatewaySender sender) {
    super(cache, sender);
  }
  
  public void logQueue() {
    // Get the region implementing the queue
    PartitionedRegion region = (PartitionedRegion) this.cache.getRegion(sender.getId() + ParallelGatewaySenderQueue.QSTRING);
    
    // Log the primary queue by bucket
    Map<Long,GatewayQueueEvent> primaryData = PartitionRegionHelper.getLocalPrimaryData(region);
    Set<BucketRegion> primaryBucketRegions = region.getDataStore().getAllLocalPrimaryBucketRegions();
    logQueue(primaryData, primaryBucketRegions, true);
    
    // Log the secondary queue by bucket
    Map<Long,GatewayQueueEvent> secondaryData = getLocalSecondaryData(region);
    Set<BucketRegion> secondaryBucketRegions = getAllLocalSecondaryBucketRegions(region);
    logQueue(secondaryData, secondaryBucketRegions, false);
  }
  
  private void logQueue(Map<Long,GatewayQueueEvent> localData, Set<BucketRegion> bucketRegions, boolean isPrimary) {
    String queueContents = getContents(bucketRegions, getHeader(localData, isPrimary));
    this.cache.getLogger().info(queueContents);
    System.out.println(queueContents);
  }
  
  private String getHeader(Map<Long,GatewayQueueEvent> localData, boolean isPrimary) {
    return new StringBuilder()
      .append("\nThe queue for parallel GatewaySender ")
      .append(this.sender.getId())
      .append(" contains the following ")
      .append(localData.size())
      .append(" ")
      .append(isPrimary ? "primary" : "secondary")
      .append(" entries grouped by bucket:\n\n")
      .toString();
  }
  
  private String getContents(Set<BucketRegion> bucketRegions, String header) {
    return bucketRegions
      .stream()
      .sorted(Comparator.comparingInt(BucketRegion::getId))
      .map(br -> getBucketRegion(br))
      .collect(Collectors.joining("\n\n", header, ""));
  }
  
  private String getBucketRegion(BucketRegion bucketRegion) {
    return getContents(bucketRegion, getHeader(bucketRegion));
  }
  
  private String getHeader(BucketRegion bucketRegion) {
    return new StringBuilder()
      .append("\tBucket ")
      .append(bucketRegion.getId())
      .append(" contains the following ")
      .append(bucketRegion.size())
      .append(" entries:\n")
      .toString();
  }
  
  private Map<Long,GatewayQueueEvent> getLocalSecondaryData(PartitionedRegion region) {
    Set<Integer> primaryBucketIds = region.getDataStore().getAllLocalPrimaryBucketIds();
    Set<Integer> allBucketIds = new HashSet<>(region.getDataStore().getAllLocalBucketIds());
    allBucketIds.removeAll(primaryBucketIds);
    return new LocalDataSet(region, allBucketIds);
  }
  
  private Set<BucketRegion> getAllLocalSecondaryBucketRegions(PartitionedRegion region) {
    Set<BucketRegion> primaryBucketRegions = region.getDataStore().getAllLocalPrimaryBucketRegions();
    Set<BucketRegion> allBucketRegions = new HashSet<>(region.getDataStore().getAllLocalBucketRegions());
    allBucketRegions.removeAll(primaryBucketRegions);
    return allBucketRegions;
  }
}
