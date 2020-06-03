package example.server.function;

import org.apache.geode.cache.Cache;
import org.apache.geode.cache.Region;

import org.apache.geode.cache.wan.GatewaySender;

import org.apache.geode.internal.cache.wan.InternalGatewaySender;

import java.util.Comparator;
import java.util.List;

import java.util.stream.Collectors;

public class SerialGatewaySenderQueueLogger extends AbstractGatewaySenderQueueLogger {
  
  public SerialGatewaySenderQueueLogger(Cache cache, GatewaySender sender) {
    super(cache, sender);
  }

  public void logQueue() {
    String header = getHeader();
    List<Region> regions = getRegions();
    String queueContents = getContents(regions, header);
    this.cache.getLogger().info(queueContents);
    System.out.println(queueContents);
  }
  
  private String getHeader() {
    InternalGatewaySender igs = (InternalGatewaySender) this.sender;
    return new StringBuilder()
      .append("\nThe queue for serial GatewaySender ")
      .append(sender.getId())
      .append(" contains the following ")
      .append(igs.getEventQueueSize())
      .append(" ")
      .append(igs.isPrimary() ? "primary" : "secondary")
      .append(" entries grouped by dispatcher:\n\n")
      .toString();
  }
  
  private List<Region> getRegions() {
    return ((InternalGatewaySender) this.sender).getQueues()
      .stream()
      .map(rq -> rq.getRegion())
      .sorted(Comparator.comparing(Region::getName))
      .collect(Collectors.toList());
  }
    
  private String getContents(List<Region> regions, String header) {
    return regions
      .stream()
      .map(br -> getRegion(br))
      .collect(Collectors.joining("\n\n", header, ""));
  }

  private String getRegion(Region region) {
    return getContents(region, getHeader(region));
  }
  
  private String getHeader(Region region) {
    return new StringBuilder()
      .append("\tQueue for dispatcher ")
      .append(region.getName().replace("_SERIAL_GATEWAY_SENDER_QUEUE", ""))
      .append(" contains the following ")
      .append(region.size())
      .append(" entries:\n")
      .toString();
  }
}
