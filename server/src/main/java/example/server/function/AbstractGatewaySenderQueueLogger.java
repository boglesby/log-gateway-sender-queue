package example.server.function;

import org.apache.geode.cache.Cache;

import org.apache.geode.cache.wan.GatewayQueueEvent;
import org.apache.geode.cache.wan.GatewaySender;

import org.apache.geode.internal.cache.wan.GatewaySenderEventImpl;

import java.util.Map;

import java.util.stream.Collectors;

public abstract class AbstractGatewaySenderQueueLogger implements GatewaySenderQueueLogger {

  protected Cache cache;
  
  protected GatewaySender sender;

  public AbstractGatewaySenderQueueLogger(Cache cache, GatewaySender sender) {
    this.cache = cache;
    this.sender = sender;
  }

  protected String getContents(Map<Long,GatewayQueueEvent> region, String header) {
    return region.entrySet()
      .stream()
      .sorted(Map.Entry.comparingByKey())
      .map(entry -> getEntry(entry))
      .collect(Collectors.joining("\n", header, ""));
  }

  protected String getEntry(Map.Entry<Long,GatewayQueueEvent> entry) {
    GatewaySenderEventImpl gsei = (GatewaySenderEventImpl) entry.getValue();
    return new StringBuilder()
      .append("\t\tqueueKey=").append(entry.getKey())
      .append("; region=").append(gsei.getRegionPath())
      .append("; operation=").append(gsei.getOperation())
      .append("; eventKey=").append(gsei.getKey())
      .append("; value=").append(gsei.getValueAsString(true))
      .append("; possibleDuplicate=").append(gsei.getPossibleDuplicate())
      .append("; eventId=").append(gsei.getEventId().expensiveToString())
      .toString();
  }
}
