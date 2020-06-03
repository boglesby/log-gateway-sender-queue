# Log GatewaySender Queue Function
## Description

This project provides a function that logs the contents of parallel or serial GatewaySender queues in the Geode servers.

For each input GatewaySender id, the **LogGatewaySenderQueueFunction**:

- gets the GatewaySender
- creates the appropriate GatewaySenderQueueLogger based on the type of GatewaySender
- invokes logQueue on the GatewaySenderQueueLogger

The **GatewaySenderQueueLogger**:

- gets the Region(s) implementing the GatewaySender queue
- for serial GatewaySenders, gets and logs all entries sorted by key and grouped by dispatcher thread
- for parallel GatewaySenders, gets and logs all primary and secondary entries either sorted by key or grouped by bucket

## Initialization
Modify the **GEODE** environment variable in the *setenv.sh* script to point to a Geode installation directory.
## Build
Build the Spring Boot Client Application and Geode Server Function and logger classes using gradle like:

```
./gradlew clean jar bootJar
```
## Run Example
### Start and Configure Locator and Servers
Start and configure the locator and 3 servers using the *startandconfigure.sh* script like:

```
./startandconfigure.sh
```
### Load Entries
Run the client to load N Trade instances using the *runclient.sh* script like below.

The parameters are:

- operation (load)
- number of entries (1000)

```
./runclient.sh load 1000
```
### Log Parallel GatewaySender Queue
Execute the function to log the parallel GatewaySender queue using the *runclient.sh* script like below.

The parameters are:

- operation (log-gateway-sender-queue)
- sender id (ny)

```
./runclient.sh log-gateway-sender-queue ny
```
### Log Parallel GatewaySender Queue Grouped by Bucket
Execute the function to log the parallel GatewaySender queue grouped by bucket using the *runclient.sh* script like below.

The parameters are:

- operation (log-gateway-sender-queue)
- sender id (ny)
- group by bucket (true)

```
./runclient.sh log-gateway-sender-queue ny true
```
### Log Serial GatewaySender Queue
Execute the function to log the serial GatewaySender queue using the *runclient.sh* script like below.

The parameters are:

- operation (log-gateway-sender-queue)
- sender id (nyserial)

```
./runclient.sh log-gateway-sender-queue nyserial
```
### Shutdown Locator and Servers
Execute the *shutdownall.sh* script to shutdown the servers and locators like:

```
./shutdownall.sh
```
### Remove Locator and Server Files
Execute the *cleanupfiles.sh* script to remove the server and locator files like:

```
./cleanupfiles.sh
```
## Example Sample Output
### Start and Configure Locator and Servers
Sample output from the *startandconfigure.sh* script is:

```
./startandconfigure.sh 
1. Executing - start locator --name=locator --J=-Dgemfire.distributed-system-id=1

............
Locator in <working-directory>/locator on xxx.xxx.x.xx[10334] as locator is currently online.
Process ID: 15671
Uptime: 13 seconds
Geode Version: 1.12.0
Java Version: 1.8.0_151
Log File: <working-directory>/locator/locator.log
JVM Arguments: <jvm-arguments>
Class-Path: <classpath>

Successfully connected to: JMX Manager [host=xxx.xxx.x.xx, port=1099]

Cluster configuration service is up and running.

2. Executing - set variable --name=APP_RESULT_VIEWER --value=any

Value for variable APP_RESULT_VIEWER is now: any.

3. Executing - configure pdx --read-serialized=true

read-serialized = true
ignore-unread-fields = false
persistent = false
Cluster configuration for group 'cluster' is updated.

4. Executing - start server --name=server-1 --server-port=0 --statistic-archive-file=cacheserver.gfs --J=-Dgemfire.log-file=cacheserver.log --J=-Dgemfire.conserve-sockets=false

........
Server in <working-directory>/server-1 on xxx.xxx.x.xx[57113] as server-1 is currently online.
Process ID: 15678
Uptime: 6 seconds
Geode Version: 1.12.0
Java Version: 1.8.0_151
Log File: <working-directory>/server-1/cacheserver.log
JVM Arguments: <jvm-arguments>
Class-Path: <classpath>

5. Executing - start server --name=server-2 --server-port=0 --statistic-archive-file=cacheserver.gfs --J=-Dgemfire.log-file=cacheserver.log --J=-Dgemfire.conserve-sockets=false

........
Server in <working-directory>/server-2 on xxx.xxx.x.xx[57136] as server-2 is currently online.
Process ID: 15680
Uptime: 6 seconds
Geode Version: 1.12.0
Java Version: 1.8.0_151
Log File: <working-directory>/server-2/cacheserver.log
JVM Arguments: <jvm-arguments>
Class-Path: <classpath>

6. Executing - start server --name=server-3 --server-port=0 --statistic-archive-file=cacheserver.gfs --J=-Dgemfire.log-file=cacheserver.log --J=-Dgemfire.conserve-sockets=false

.........
Server in <working-directory>/server-3 on xxx.xxx.x.xx[57163] as server-3 is currently online.
Process ID: 15682
Uptime: 7 seconds
Geode Version: 1.12.0
Java Version: 1.8.0_151
Log File: <working-directory>/server-3/cacheserver.log
JVM Arguments: <jvm-arguments>
Class-Path: <classpath>

7. Executing - list members

Member Count : 4

  Name   | Id
-------- | ---------------------------------------------------------------
locator  | xxx.xxx.x.xx(locator:15671:locator)<ec><v0>:41000 [Coordinator]
server-1 | xxx.xxx.x.xx(server-1:15678)<v1>:41001
server-2 | xxx.xxx.x.xx(server-2:15680)<v2>:41002
server-3 | xxx.xxx.x.xx(server-3:15682)<v3>:41003

8. Executing - create gateway-sender --id=ny --remote-distributed-system-id=2 --parallel=true

 Member  | Status | Message
-------- | ------ | ----------------------------------------
server-1 | OK     | GatewaySender "ny" created on "server-1"
server-2 | OK     | GatewaySender "ny" created on "server-2"
server-3 | OK     | GatewaySender "ny" created on "server-3"

Cluster configuration for group 'cluster' is updated.

9. Executing - create gateway-sender --id=nyserial --remote-distributed-system-id=2 --parallel=false

 Member  | Status | Message
-------- | ------ | ----------------------------------------------
server-1 | OK     | GatewaySender "nyserial" created on "server-1"
server-2 | OK     | GatewaySender "nyserial" created on "server-2"
server-3 | OK     | GatewaySender "nyserial" created on "server-3"

Cluster configuration for group 'cluster' is updated.

10. Executing - sleep --time=5


11. Executing - create region --name=Trade --type=PARTITION_REDUNDANT --gateway-sender-id=ny,nyserial

 Member  | Status | Message
-------- | ------ | -------------------------------------
server-1 | OK     | Region "/Trade" created on "server-1"
server-2 | OK     | Region "/Trade" created on "server-2"
server-3 | OK     | Region "/Trade" created on "server-3"

Cluster configuration for group 'cluster' is updated.

12. Executing - list regions

List of regions
---------------
Trade

13. Executing - deploy --jar=server/build/libs/server-0.0.1-SNAPSHOT.jar

 Member  |       Deployed JAR        | Deployed JAR Location
-------- | ------------------------- | ---------------------------------------------------------
server-1 | server-0.0.1-SNAPSHOT.jar | <working-directory>/server-1/server-0.0.1-SNAPSHOT.v1.jar
server-2 | server-0.0.1-SNAPSHOT.jar | <working-directory>/server-2/server-0.0.1-SNAPSHOT.v1.jar
server-3 | server-0.0.1-SNAPSHOT.jar | <working-directory>/server-3/server-0.0.1-SNAPSHOT.v1.jar

14. Executing - list functions

 Member  | Function
-------- | -----------------------------
server-1 | LogGatewaySenderQueueFunction
server-2 | LogGatewaySenderQueueFunction
server-3 | LogGatewaySenderQueueFunction

************************* Execution Summary ***********************
Script file: startandconfigure.gfsh

Command-1 : start locator --name=locator --J=-Dgemfire.distributed-system-id=1
Status    : PASSED

Command-2 : set variable --name=APP_RESULT_VIEWER --value=any
Status    : PASSED

Command-3 : configure pdx --read-serialized=true
Status    : PASSED

Command-4 : start server --name=server-1 --server-port=0 --statistic-archive-file=cacheserver.gfs --J=-Dgemfire.log-file=cacheserver.log --J=-Dgemfire.conserve-sockets=false
Status    : PASSED

Command-5 : start server --name=server-2 --server-port=0 --statistic-archive-file=cacheserver.gfs --J=-Dgemfire.log-file=cacheserver.log --J=-Dgemfire.conserve-sockets=false
Status    : PASSED

Command-6 : start server --name=server-3 --server-port=0 --statistic-archive-file=cacheserver.gfs --J=-Dgemfire.log-file=cacheserver.log --J=-Dgemfire.conserve-sockets=false
Status    : PASSED

Command-7 : list members
Status    : PASSED

Command-8 : create gateway-sender --id=ny --remote-distributed-system-id=2 --parallel=true
Status    : PASSED

Command-9 : create gateway-sender --id=nyserial --remote-distributed-system-id=2 --parallel=false
Status    : PASSED

Command-10 : sleep --time=5
Status     : PASSED

Command-11 : create region --name=Trade --type=PARTITION_REDUNDANT --gateway-sender-id=ny,nyserial
Status     : PASSED

Command-12 : list regions
Status     : PASSED

Command-13 : deploy --jar=server/build/libs/server-0.0.1-SNAPSHOT.jar
Status     : PASSED

Command-14 : list functions
Status     : PASSED
```
### Load Entries
Sample output from the *runclient.sh* script is:

```
./runclient.sh load 1000

> Task :client:bootRun

2020-06-01 12:45:20.146  INFO 15713 --- [           main] example.client.Client                    : Starting Client on ...
2020-06-01 12:45:23.075  INFO 15713 --- [           main] example.client.Client                    : Started Client in 3.447 seconds (JVM running for 4.103)
2020-06-01 12:45:23.076  INFO 15713 --- [           main] example.client.service.TradeService      : Putting 1000 trades of size 16 bytes
2020-06-01 12:45:23.257  INFO 15713 --- [           main] example.client.service.TradeService      : Saved Trade(id=0, cusip=NKE, shares=58, price=224.49, createTime=1591051523077, updateTime=1591051523077)
2020-06-01 12:45:23.330  INFO 15713 --- [           main] example.client.service.TradeService      : Saved Trade(id=1, cusip=TXN, shares=91, price=761.70, createTime=1591051523257, updateTime=1591051523257)
2020-06-01 12:45:23.396  INFO 15713 --- [           main] example.client.service.TradeService      : Saved Trade(id=2, cusip=ORCL, shares=86, price=651.00, createTime=1591051523330, updateTime=1591051523330)
2020-06-01 12:45:23.439  INFO 15713 --- [           main] example.client.service.TradeService      : Saved Trade(id=3, cusip=KO, shares=92, price=324.60, createTime=1591051523396, updateTime=1591051523396)
2020-06-01 12:45:23.492  INFO 15713 --- [           main] example.client.service.TradeService      : Saved Trade(id=4, cusip=AXP, shares=76, price=126.17, createTime=1591051523439, updateTime=1591051523439)
...
```
### Log Parallel GatewaySender Queue
Sample output from the *runclient.sh* script is:

```
./runclient.sh log-gateway-sender-queue ny

> Task :client:bootRun

2020-06-01 12:46:43.608  INFO 15735 --- [           main] example.client.Client                    : Starting Client on ...
2020-06-01 12:46:46.662  INFO 15735 --- [           main] example.client.Client                    : Started Client in 3.654 seconds (JVM running for 4.295)
2020-06-01 12:46:46.906  INFO 15735 --- [           main] example.client.service.TradeService      : Logged queue for gatewaySenderId=ny; groupByBucket=false; result={server-3=true, server-2=true, server-1=true}
```
Each server's log file will contain messages for its primary and secondary queues like:

```
[info 2020/06/01 12:46:46.802 HST <ServerConnection on port 57163 Thread 2> tid=0x87] 
The queue for parallel GatewaySender ny contains the following 342 primary entries:
  queueKey=113; region=/Trade; operation=CREATE; eventKey=54; value=PDX[26084582,example.client.domain.Trade]{id=54}; possibleDuplicate=false; eventId=EventID[xxx.xxx.x.xx(client:loner):57269:e48f0f72:client;threadID=0x10000|1;sequenceID=54;bucketID=0]
  queueKey=117; region=/Trade; operation=CREATE; eventKey=58; value=PDX[26084582,example.client.domain.Trade]{id=58}; possibleDuplicate=false; eventId=EventID[xxx.xxx.x.xx(client:loner):57269:e48f0f72:client;threadID=0x10004|1;sequenceID=58;bucketID=4]
  queueKey=123; region=/Trade; operation=CREATE; eventKey=93; value=PDX[26084582,example.client.domain.Trade]{id=93}; possibleDuplicate=false; eventId=EventID[xxx.xxx.x.xx(client:loner):57269:e48f0f72:client;threadID=0x1000a|1;sequenceID=93;bucketID=10]
  queueKey=126; region=/Trade; operation=CREATE; eventKey=96; value=PDX[26084582,example.client.domain.Trade]{id=96}; possibleDuplicate=false; eventId=EventID[xxx.xxx.x.xx(client:loner):57269:e48f0f72:client;threadID=0x1000d|1;sequenceID=96;bucketID=13]
  ...
[info 2020/06/01 12:46:46.884 HST <ServerConnection on port 57163 Thread 2> tid=0x87] 
The queue for parallel GatewaySender ny contains the following 331 secondary entries:
  queueKey=116; region=/Trade; operation=CREATE; eventKey=57; value=PDX[26084582,example.client.domain.Trade]{id=57}; possibleDuplicate=false; eventId=EventID[xxx.xxx.x.xx(client:loner):57269:e48f0f72:client;threadID=0x10003|1;sequenceID=57;bucketID=3]
  queueKey=118; region=/Trade; operation=CREATE; eventKey=59; value=PDX[26084582,example.client.domain.Trade]{id=59}; possibleDuplicate=false; eventId=EventID[xxx.xxx.x.xx(client:loner):57269:e48f0f72:client;threadID=0x10005|1;sequenceID=59;bucketID=5]
  queueKey=121; region=/Trade; operation=CREATE; eventKey=91; value=PDX[26084582,example.client.domain.Trade]{id=91}; possibleDuplicate=false; eventId=EventID[xxx.xxx.x.xx(client:loner):57269:e48f0f72:client;threadID=0x10008|1;sequenceID=91;bucketID=8]
  queueKey=125; region=/Trade; operation=CREATE; eventKey=95; value=PDX[26084582,example.client.domain.Trade]{id=95}; possibleDuplicate=false; eventId=EventID[xxx.xxx.x.xx(client:loner):57269:e48f0f72:client;threadID=0x1000c|1;sequenceID=95;bucketID=12]
  ...
```
### Log Parallel GatewaySender Queue Grouped by Bucket
Sample output from the *runclient.sh* script is:

```
./runclient.sh log-gateway-sender-queue ny true

> Task :client:bootRun

2020-06-01 12:47:53.276  INFO 15757 --- [           main] example.client.Client                    : Starting Client on ...
2020-06-01 12:47:56.492  INFO 15757 --- [           main] example.client.Client                    : Started Client in 3.769 seconds (JVM running for 4.378)
2020-06-01 12:47:56.682  INFO 15757 --- [           main] example.client.service.TradeService      : Logged queue for gatewaySenderId=ny; groupByBucket=true; result={server-3=true, server-2=true, server-1=true}
```
Each server's log file will contain messages for its primary and secondary queues like:

```
[info 2020/06/01 12:47:56.665 HST <ServerConnection on port 57163 Thread 3> tid=0x88] 
The queue for parallel GatewaySender ny contains the following 342 primary entries grouped by bucket:

  Bucket 0 contains the following 9 entries:
    queueKey=113; region=/Trade; operation=CREATE; eventKey=54; value=PDX[26084582,example.client.domain.Trade]{id=54}; possibleDuplicate=false; eventId=EventID[xxx.xxx.x.xx(client:loner):57269:e48f0f72:client;threadID=0x10000|1;sequenceID=54;bucketID=0]
    queueKey=226; region=/Trade; operation=CREATE; eventKey=165; value=PDX[26084582,example.client.domain.Trade]{id=165}; possibleDuplicate=false; eventId=EventID[xxx.xxx.x.xx(client:loner):57269:e48f0f72:client;threadID=0x10000|1;sequenceID=165;bucketID=0]
    queueKey=339; region=/Trade; operation=CREATE; eventKey=364; value=PDX[26084582,example.client.domain.Trade]{id=364}; possibleDuplicate=false; eventId=EventID[xxx.xxx.x.xx(client:loner):57269:e48f0f72:client;threadID=0x10000|1;sequenceID=364;bucketID=0]
    queueKey=452; region=/Trade; operation=CREATE; eventKey=449; value=PDX[26084582,example.client.domain.Trade]{id=449}; possibleDuplicate=false; eventId=EventID[xxx.xxx.x.xx(client:loner):57269:e48f0f72:client;threadID=0x10000|1;sequenceID=449;bucketID=0]
    ...
  Bucket 4 contains the following 10 entries:
    queueKey=117; region=/Trade; operation=CREATE; eventKey=58; value=PDX[26084582,example.client.domain.Trade]{id=58}; possibleDuplicate=false; eventId=EventID[xxx.xxx.x.xx(client:loner):57269:e48f0f72:client;threadID=0x10004|1;sequenceID=58;bucketID=4]
    queueKey=230; region=/Trade; operation=CREATE; eventKey=169; value=PDX[26084582,example.client.domain.Trade]{id=169}; possibleDuplicate=false; eventId=EventID[xxx.xxx.x.xx(client:loner):57269:e48f0f72:client;threadID=0x10004|1;sequenceID=169;bucketID=4]
    queueKey=343; region=/Trade; operation=CREATE; eventKey=283; value=PDX[26084582,example.client.domain.Trade]{id=283}; possibleDuplicate=false; eventId=EventID[xxx.xxx.x.xx(client:loner):57269:e48f0f72:client;threadID=0x10004|1;sequenceID=283;bucketID=4]
    queueKey=456; region=/Trade; operation=CREATE; eventKey=368; value=PDX[26084582,example.client.domain.Trade]{id=368}; possibleDuplicate=false; eventId=EventID[xxx.xxx.x.xx(client:loner):57269:e48f0f72:client;threadID=0x10004|1;sequenceID=368;bucketID=4]
    ...
  Bucket 10 contains the following 6 entries:
    queueKey=123; region=/Trade; operation=CREATE; eventKey=93; value=PDX[26084582,example.client.domain.Trade]{id=93}; possibleDuplicate=false; eventId=EventID[xxx.xxx.x.xx(client:loner):57269:e48f0f72:client;threadID=0x1000a|1;sequenceID=93;bucketID=10]
    queueKey=236; region=/Trade; operation=CREATE; eventKey=210; value=PDX[26084582,example.client.domain.Trade]{id=210}; possibleDuplicate=false; eventId=EventID[xxx.xxx.x.xx(client:loner):57269:e48f0f72:client;threadID=0x1000a|1;sequenceID=210;bucketID=10]
    queueKey=349; region=/Trade; operation=CREATE; eventKey=289; value=PDX[26084582,example.client.domain.Trade]{id=289}; possibleDuplicate=false; eventId=EventID[xxx.xxx.x.xx(client:loner):57269:e48f0f72:client;threadID=0x1000a|1;sequenceID=289;bucketID=10]
    queueKey=462; region=/Trade; operation=CREATE; eventKey=488; value=PDX[26084582,example.client.domain.Trade]{id=488}; possibleDuplicate=false; eventId=EventID[xxx.xxx.x.xx(client:loner):57269:e48f0f72:client;threadID=0x1000a|1;sequenceID=488;bucketID=10]
    ...
  Bucket 13 contains the following 6 entries:
    queueKey=126; region=/Trade; operation=CREATE; eventKey=96; value=PDX[26084582,example.client.domain.Trade]{id=96}; possibleDuplicate=false; eventId=EventID[xxx.xxx.x.xx(client:loner):57269:e48f0f72:client;threadID=0x1000d|1;sequenceID=96;bucketID=13]
    queueKey=239; region=/Trade; operation=CREATE; eventKey=213; value=PDX[26084582,example.client.domain.Trade]{id=213}; possibleDuplicate=false; eventId=EventID[xxx.xxx.x.xx(client:loner):57269:e48f0f72:client;threadID=0x1000d|1;sequenceID=213;bucketID=13]
    queueKey=352; region=/Trade; operation=CREATE; eventKey=412; value=PDX[26084582,example.client.domain.Trade]{id=412}; possibleDuplicate=false; eventId=EventID[xxx.xxx.x.xx(client:loner):57269:e48f0f72:client;threadID=0x1000d|1;sequenceID=412;bucketID=13]
    queueKey=465; region=/Trade; operation=CREATE; eventKey=611; value=PDX[26084582,example.client.domain.Trade]{id=611}; possibleDuplicate=false; eventId=EventID[xxx.xxx.x.xx(client:loner):57269:e48f0f72:client;threadID=0x1000d|1;sequenceID=611;bucketID=13]
    ...
  ...
[info 2020/06/01 12:47:56.679 HST <ServerConnection on port 57163 Thread 3> tid=0x88] 
The queue for parallel GatewaySender ny contains the following 331 secondary entries grouped by bucket:

  Bucket 3 contains the following 9 entries:
    queueKey=116; region=/Trade; operation=CREATE; eventKey=57; value=PDX[26084582,example.client.domain.Trade]{id=57}; possibleDuplicate=false; eventId=EventID[xxx.xxx.x.xx(client:loner):57269:e48f0f72:client;threadID=0x10003|1;sequenceID=57;bucketID=3]
    queueKey=229; region=/Trade; operation=CREATE; eventKey=168; value=PDX[26084582,example.client.domain.Trade]{id=168}; possibleDuplicate=false; eventId=EventID[xxx.xxx.x.xx(client:loner):57269:e48f0f72:client;threadID=0x10003|1;sequenceID=168;bucketID=3]
    queueKey=342; region=/Trade; operation=CREATE; eventKey=282; value=PDX[26084582,example.client.domain.Trade]{id=282}; possibleDuplicate=false; eventId=EventID[xxx.xxx.x.xx(client:loner):57269:e48f0f72:client;threadID=0x10003|1;sequenceID=282;bucketID=3]
    queueKey=455; region=/Trade; operation=CREATE; eventKey=367; value=PDX[26084582,example.client.domain.Trade]{id=367}; possibleDuplicate=false; eventId=EventID[xxx.xxx.x.xx(client:loner):57269:e48f0f72:client;threadID=0x10003|1;sequenceID=367;bucketID=3]
    ...
  Bucket 5 contains the following 9 entries:
    queueKey=118; region=/Trade; operation=CREATE; eventKey=59; value=PDX[26084582,example.client.domain.Trade]{id=59}; possibleDuplicate=false; eventId=EventID[xxx.xxx.x.xx(client:loner):57269:e48f0f72:client;threadID=0x10005|1;sequenceID=59;bucketID=5]
    queueKey=231; region=/Trade; operation=CREATE; eventKey=284; value=PDX[26084582,example.client.domain.Trade]{id=284}; possibleDuplicate=false; eventId=EventID[xxx.xxx.x.xx(client:loner):57269:e48f0f72:client;threadID=0x10005|1;sequenceID=284;bucketID=5]
    queueKey=344; region=/Trade; operation=CREATE; eventKey=369; value=PDX[26084582,example.client.domain.Trade]{id=369}; possibleDuplicate=false; eventId=EventID[xxx.xxx.x.xx(client:loner):57269:e48f0f72:client;threadID=0x10005|1;sequenceID=369;bucketID=5]
    queueKey=457; region=/Trade; operation=CREATE; eventKey=483; value=PDX[26084582,example.client.domain.Trade]{id=483}; possibleDuplicate=false; eventId=EventID[xxx.xxx.x.xx(client:loner):57269:e48f0f72:client;threadID=0x10005|1;sequenceID=483;bucketID=5]
    ...
  Bucket 8 contains the following 6 entries:
    queueKey=121; region=/Trade; operation=CREATE; eventKey=91; value=PDX[26084582,example.client.domain.Trade]{id=91}; possibleDuplicate=false; eventId=EventID[xxx.xxx.x.xx(client:loner):57269:e48f0f72:client;threadID=0x10008|1;sequenceID=91;bucketID=8]
    queueKey=234; region=/Trade; operation=CREATE; eventKey=287; value=PDX[26084582,example.client.domain.Trade]{id=287}; possibleDuplicate=false; eventId=EventID[xxx.xxx.x.xx(client:loner):57269:e48f0f72:client;threadID=0x10008|1;sequenceID=287;bucketID=8]
    queueKey=347; region=/Trade; operation=CREATE; eventKey=486; value=PDX[26084582,example.client.domain.Trade]{id=486}; possibleDuplicate=false; eventId=EventID[xxx.xxx.x.xx(client:loner):57269:e48f0f72:client;threadID=0x10008|1;sequenceID=486;bucketID=8]
    queueKey=460; region=/Trade; operation=CREATE; eventKey=685; value=PDX[26084582,example.client.domain.Trade]{id=685}; possibleDuplicate=false; eventId=EventID[xxx.xxx.x.xx(client:loner):57269:e48f0f72:client;threadID=0x10008|1;sequenceID=685;bucketID=8]
    ...
  Bucket 12 contains the following 6 entries:
    queueKey=125; region=/Trade; operation=CREATE; eventKey=95; value=PDX[26084582,example.client.domain.Trade]{id=95}; possibleDuplicate=false; eventId=EventID[xxx.xxx.x.xx(client:loner):57269:e48f0f72:client;threadID=0x1000c|1;sequenceID=95;bucketID=12]
    queueKey=238; region=/Trade; operation=CREATE; eventKey=212; value=PDX[26084582,example.client.domain.Trade]{id=212}; possibleDuplicate=false; eventId=EventID[xxx.xxx.x.xx(client:loner):57269:e48f0f72:client;threadID=0x1000c|1;sequenceID=212;bucketID=12]
    queueKey=351; region=/Trade; operation=CREATE; eventKey=411; value=PDX[26084582,example.client.domain.Trade]{id=411}; possibleDuplicate=false; eventId=EventID[xxx.xxx.x.xx(client:loner):57269:e48f0f72:client;threadID=0x1000c|1;sequenceID=411;bucketID=12]
    queueKey=464; region=/Trade; operation=CREATE; eventKey=610; value=PDX[26084582,example.client.domain.Trade]{id=610}; possibleDuplicate=false; eventId=EventID[xxx.xxx.x.xx(client:loner):57269:e48f0f72:client;threadID=0x1000c|1;sequenceID=610;bucketID=12]
    ...
  ...
```
### Log Serial GatewaySender Queue
Sample output from the *runclient.sh* script is:

```
./runclient.sh log-gateway-sender-queue nyserial

> Task :client:bootRun

2020-06-01 12:49:08.619  INFO 15779 --- [           main] example.client.Client                    : Starting Client on ...
2020-06-01 12:49:11.473  INFO 15779 --- [           main] example.client.Client                    : Started Client in 3.365 seconds (JVM running for 3.988)
2020-06-01 12:49:11.604  INFO 15779 --- [           main] example.client.service.TradeService      : Logged queue for gatewaySenderId=nyserial; groupByBucket=false; result={server-3=true, server-2=true, server-1=true}
```
The primary server's log file will contain a message like:

```
[info 2020/06/01 12:49:11.567 HST <ServerConnection on port 57136 Thread 4> tid=0x87] 
The queue for serial GatewaySender nyserial contains the following 1000 primary entries grouped by dispatcher:

  Queue for dispatcher nyserial.0 contains the following 200 entries:
    queueKey=0; region=/Trade; operation=CREATE; eventKey=2; value=PDX[26084582,example.client.domain.Trade]{id=2}; possibleDuplicate=true; eventId=EventID[xxx.xxx.x.xx(client:loner):57269:e48f0f72:client;threadID=0x1030000|1;sequenceID=2]
    queueKey=1; region=/Trade; operation=CREATE; eventKey=7; value=PDX[26084582,example.client.domain.Trade]{id=7}; possibleDuplicate=false; eventId=EventID[xxx.xxx.x.xx(client:loner):57269:e48f0f72:client;threadID=0x1030000|1;sequenceID=7]
    queueKey=2; region=/Trade; operation=CREATE; eventKey=13; value=PDX[26084582,example.client.domain.Trade]{id=13}; possibleDuplicate=false; eventId=EventID[xxx.xxx.x.xx(client:loner):57269:e48f0f72:client;threadID=0x1030000|1;sequenceID=13]
    queueKey=3; region=/Trade; operation=CREATE; eventKey=18; value=PDX[26084582,example.client.domain.Trade]{id=18}; possibleDuplicate=false; eventId=EventID[xxx.xxx.x.xx(client:loner):57269:e48f0f72:client;threadID=0x1030000|1;sequenceID=18]
    queueKey=4; region=/Trade; operation=CREATE; eventKey=22; value=PDX[26084582,example.client.domain.Trade]{id=22}; possibleDuplicate=false; eventId=EventID[xxx.xxx.x.xx(client:loner):57269:e48f0f72:client;threadID=0x1030000|1;sequenceID=22]
    ...
  Queue for dispatcher nyserial.1 contains the following 200 entries:
    queueKey=0; region=/Trade; operation=CREATE; eventKey=3; value=PDX[26084582,example.client.domain.Trade]{id=3}; possibleDuplicate=true; eventId=EventID[xxx.xxx.x.xx(client:loner):57269:e48f0f72:client;threadID=0x1030001|1;sequenceID=3]
    queueKey=1; region=/Trade; operation=CREATE; eventKey=8; value=PDX[26084582,example.client.domain.Trade]{id=8}; possibleDuplicate=false; eventId=EventID[xxx.xxx.x.xx(client:loner):57269:e48f0f72:client;threadID=0x1030001|1;sequenceID=8]
    queueKey=2; region=/Trade; operation=CREATE; eventKey=14; value=PDX[26084582,example.client.domain.Trade]{id=14}; possibleDuplicate=false; eventId=EventID[xxx.xxx.x.xx(client:loner):57269:e48f0f72:client;threadID=0x1030001|1;sequenceID=14]
    queueKey=3; region=/Trade; operation=CREATE; eventKey=19; value=PDX[26084582,example.client.domain.Trade]{id=19}; possibleDuplicate=false; eventId=EventID[xxx.xxx.x.xx(client:loner):57269:e48f0f72:client;threadID=0x1030001|1;sequenceID=19]
    queueKey=4; region=/Trade; operation=CREATE; eventKey=23; value=PDX[26084582,example.client.domain.Trade]{id=23}; possibleDuplicate=false; eventId=EventID[xxx.xxx.x.xx(client:loner):57269:e48f0f72:client;threadID=0x1030001|1;sequenceID=23]
    ...
  Queue for dispatcher nyserial.2 contains the following 200 entries:
    queueKey=0; region=/Trade; operation=CREATE; eventKey=4; value=PDX[26084582,example.client.domain.Trade]{id=4}; possibleDuplicate=true; eventId=EventID[xxx.xxx.x.xx(client:loner):57269:e48f0f72:client;threadID=0x1030002|1;sequenceID=4]
    queueKey=1; region=/Trade; operation=CREATE; eventKey=9; value=PDX[26084582,example.client.domain.Trade]{id=9}; possibleDuplicate=false; eventId=EventID[xxx.xxx.x.xx(client:loner):57269:e48f0f72:client;threadID=0x1030002|1;sequenceID=9]
    queueKey=2; region=/Trade; operation=CREATE; eventKey=10; value=PDX[26084582,example.client.domain.Trade]{id=10}; possibleDuplicate=false; eventId=EventID[xxx.xxx.x.xx(client:loner):57269:e48f0f72:client;threadID=0x1030002|1;sequenceID=10]
    queueKey=3; region=/Trade; operation=CREATE; eventKey=15; value=PDX[26084582,example.client.domain.Trade]{id=15}; possibleDuplicate=false; eventId=EventID[xxx.xxx.x.xx(client:loner):57269:e48f0f72:client;threadID=0x1030002|1;sequenceID=15]
    queueKey=4; region=/Trade; operation=CREATE; eventKey=24; value=PDX[26084582,example.client.domain.Trade]{id=24}; possibleDuplicate=false; eventId=EventID[xxx.xxx.x.xx(client:loner):57269:e48f0f72:client;threadID=0x1030002|1;sequenceID=24]
    ...
  Queue for dispatcher nyserial.3 contains the following 200 entries:
    queueKey=0; region=/Trade; operation=CREATE; eventKey=0; value=PDX[26084582,example.client.domain.Trade]{id=0}; possibleDuplicate=true; eventId=EventID[xxx.xxx.x.xx(client:loner):57269:e48f0f72:client;threadID=0x1030003|1;sequenceID=0]
    queueKey=1; region=/Trade; operation=CREATE; eventKey=5; value=PDX[26084582,example.client.domain.Trade]{id=5}; possibleDuplicate=true; eventId=EventID[xxx.xxx.x.xx(client:loner):57269:e48f0f72:client;threadID=0x1030003|1;sequenceID=5]
    queueKey=2; region=/Trade; operation=CREATE; eventKey=11; value=PDX[26084582,example.client.domain.Trade]{id=11}; possibleDuplicate=true; eventId=EventID[xxx.xxx.x.xx(client:loner):57269:e48f0f72:client;threadID=0x1030003|1;sequenceID=11]
    queueKey=3; region=/Trade; operation=CREATE; eventKey=16; value=PDX[26084582,example.client.domain.Trade]{id=16}; possibleDuplicate=false; eventId=EventID[xxx.xxx.x.xx(client:loner):57269:e48f0f72:client;threadID=0x1030003|1;sequenceID=16]
    queueKey=4; region=/Trade; operation=CREATE; eventKey=20; value=PDX[26084582,example.client.domain.Trade]{id=20}; possibleDuplicate=false; eventId=EventID[xxx.xxx.x.xx(client:loner):57269:e48f0f72:client;threadID=0x1030003|1;sequenceID=20]
    ...
  Queue for dispatcher nyserial.4 contains the following 200 entries:
    queueKey=0; region=/Trade; operation=CREATE; eventKey=1; value=PDX[26084582,example.client.domain.Trade]{id=1}; possibleDuplicate=true; eventId=EventID[xxx.xxx.x.xx(client:loner):57269:e48f0f72:client;threadID=0x1030004|1;sequenceID=1]
    queueKey=1; region=/Trade; operation=CREATE; eventKey=6; value=PDX[26084582,example.client.domain.Trade]{id=6}; possibleDuplicate=true; eventId=EventID[xxx.xxx.x.xx(client:loner):57269:e48f0f72:client;threadID=0x1030004|1;sequenceID=6]
    queueKey=2; region=/Trade; operation=CREATE; eventKey=12; value=PDX[26084582,example.client.domain.Trade]{id=12}; possibleDuplicate=false; eventId=EventID[xxx.xxx.x.xx(client:loner):57269:e48f0f72:client;threadID=0x1030004|1;sequenceID=12]
    queueKey=3; region=/Trade; operation=CREATE; eventKey=17; value=PDX[26084582,example.client.domain.Trade]{id=17}; possibleDuplicate=false; eventId=EventID[xxx.xxx.x.xx(client:loner):57269:e48f0f72:client;threadID=0x1030004|1;sequenceID=17]
    queueKey=4; region=/Trade; operation=CREATE; eventKey=21; value=PDX[26084582,example.client.domain.Trade]{id=21}; possibleDuplicate=false; eventId=EventID[xxx.xxx.x.xx(client:loner):57269:e48f0f72:client;threadID=0x1030004|1;sequenceID=21]
    ...
```
Any secondary server's log file will contain a message like:

```
[info 2020/06/01 12:49:11.600 HST <ServerConnection on port 57163 Thread 4> tid=0x89] 
The queue for serial GatewaySender nyserial contains the following 1000 secondary entries grouped by dispatcher:

  Queue for dispatcher nyserial.0 contains the following 200 entries:
    queueKey=0; region=/Trade; operation=CREATE; eventKey=2; value=PDX[26084582,example.client.domain.Trade]{id=2}; possibleDuplicate=false; eventId=EventID[xxx.xxx.x.xx(client:loner):57269:e48f0f72:client;threadID=0x1030000|1;sequenceID=2]
    queueKey=1; region=/Trade; operation=CREATE; eventKey=7; value=PDX[26084582,example.client.domain.Trade]{id=7}; possibleDuplicate=false; eventId=EventID[xxx.xxx.x.xx(client:loner):57269:e48f0f72:client;threadID=0x1030000|1;sequenceID=7]
    queueKey=2; region=/Trade; operation=CREATE; eventKey=13; value=PDX[26084582,example.client.domain.Trade]{id=13}; possibleDuplicate=false; eventId=EventID[xxx.xxx.x.xx(client:loner):57269:e48f0f72:client;threadID=0x1030000|1;sequenceID=13]
    queueKey=3; region=/Trade; operation=CREATE; eventKey=18; value=PDX[26084582,example.client.domain.Trade]{id=18}; possibleDuplicate=false; eventId=EventID[xxx.xxx.x.xx(client:loner):57269:e48f0f72:client;threadID=0x1030000|1;sequenceID=18]
    queueKey=4; region=/Trade; operation=CREATE; eventKey=22; value=PDX[26084582,example.client.domain.Trade]{id=22}; possibleDuplicate=false; eventId=EventID[xxx.xxx.x.xx(client:loner):57269:e48f0f72:client;threadID=0x1030000|1;sequenceID=22]
    ...
  Queue for dispatcher nyserial.1 contains the following 200 entries:
    queueKey=0; region=/Trade; operation=CREATE; eventKey=3; value=PDX[26084582,example.client.domain.Trade]{id=3}; possibleDuplicate=false; eventId=EventID[xxx.xxx.x.xx(client:loner):57269:e48f0f72:client;threadID=0x1030001|1;sequenceID=3]
    queueKey=1; region=/Trade; operation=CREATE; eventKey=8; value=PDX[26084582,example.client.domain.Trade]{id=8}; possibleDuplicate=false; eventId=EventID[xxx.xxx.x.xx(client:loner):57269:e48f0f72:client;threadID=0x1030001|1;sequenceID=8]
    queueKey=2; region=/Trade; operation=CREATE; eventKey=14; value=PDX[26084582,example.client.domain.Trade]{id=14}; possibleDuplicate=false; eventId=EventID[xxx.xxx.x.xx(client:loner):57269:e48f0f72:client;threadID=0x1030001|1;sequenceID=14]
    queueKey=3; region=/Trade; operation=CREATE; eventKey=19; value=PDX[26084582,example.client.domain.Trade]{id=19}; possibleDuplicate=false; eventId=EventID[xxx.xxx.x.xx(client:loner):57269:e48f0f72:client;threadID=0x1030001|1;sequenceID=19]
    queueKey=4; region=/Trade; operation=CREATE; eventKey=23; value=PDX[26084582,example.client.domain.Trade]{id=23}; possibleDuplicate=false; eventId=EventID[xxx.xxx.x.xx(client:loner):57269:e48f0f72:client;threadID=0x1030001|1;sequenceID=23]
    ...
  Queue for dispatcher nyserial.2 contains the following 200 entries:
    queueKey=0; region=/Trade; operation=CREATE; eventKey=4; value=PDX[26084582,example.client.domain.Trade]{id=4}; possibleDuplicate=false; eventId=EventID[xxx.xxx.x.xx(client:loner):57269:e48f0f72:client;threadID=0x1030002|1;sequenceID=4]
    queueKey=1; region=/Trade; operation=CREATE; eventKey=9; value=PDX[26084582,example.client.domain.Trade]{id=9}; possibleDuplicate=false; eventId=EventID[xxx.xxx.x.xx(client:loner):57269:e48f0f72:client;threadID=0x1030002|1;sequenceID=9]
    queueKey=2; region=/Trade; operation=CREATE; eventKey=10; value=PDX[26084582,example.client.domain.Trade]{id=10}; possibleDuplicate=false; eventId=EventID[xxx.xxx.x.xx(client:loner):57269:e48f0f72:client;threadID=0x1030002|1;sequenceID=10]
    queueKey=3; region=/Trade; operation=CREATE; eventKey=15; value=PDX[26084582,example.client.domain.Trade]{id=15}; possibleDuplicate=false; eventId=EventID[xxx.xxx.x.xx(client:loner):57269:e48f0f72:client;threadID=0x1030002|1;sequenceID=15]
    queueKey=4; region=/Trade; operation=CREATE; eventKey=24; value=PDX[26084582,example.client.domain.Trade]{id=24}; possibleDuplicate=false; eventId=EventID[xxx.xxx.x.xx(client:loner):57269:e48f0f72:client;threadID=0x1030002|1;sequenceID=24]
    ...
  Queue for dispatcher nyserial.3 contains the following 200 entries:
    queueKey=0; region=/Trade; operation=CREATE; eventKey=0; value=PDX[26084582,example.client.domain.Trade]{id=0}; possibleDuplicate=false; eventId=EventID[xxx.xxx.x.xx(client:loner):57269:e48f0f72:client;threadID=0x1030003|1;sequenceID=0]
    queueKey=1; region=/Trade; operation=CREATE; eventKey=5; value=PDX[26084582,example.client.domain.Trade]{id=5}; possibleDuplicate=false; eventId=EventID[xxx.xxx.x.xx(client:loner):57269:e48f0f72:client;threadID=0x1030003|1;sequenceID=5]
    queueKey=2; region=/Trade; operation=CREATE; eventKey=11; value=PDX[26084582,example.client.domain.Trade]{id=11}; possibleDuplicate=false; eventId=EventID[xxx.xxx.x.xx(client:loner):57269:e48f0f72:client;threadID=0x1030003|1;sequenceID=11]
    queueKey=3; region=/Trade; operation=CREATE; eventKey=16; value=PDX[26084582,example.client.domain.Trade]{id=16}; possibleDuplicate=false; eventId=EventID[xxx.xxx.x.xx(client:loner):57269:e48f0f72:client;threadID=0x1030003|1;sequenceID=16]
    queueKey=4; region=/Trade; operation=CREATE; eventKey=20; value=PDX[26084582,example.client.domain.Trade]{id=20}; possibleDuplicate=false; eventId=EventID[xxx.xxx.x.xx(client:loner):57269:e48f0f72:client;threadID=0x1030003|1;sequenceID=20]
    ...
  Queue for dispatcher nyserial.4 contains the following 200 entries:
    queueKey=0; region=/Trade; operation=CREATE; eventKey=1; value=PDX[26084582,example.client.domain.Trade]{id=1}; possibleDuplicate=false; eventId=EventID[xxx.xxx.x.xx(client:loner):57269:e48f0f72:client;threadID=0x1030004|1;sequenceID=1]
    queueKey=1; region=/Trade; operation=CREATE; eventKey=6; value=PDX[26084582,example.client.domain.Trade]{id=6}; possibleDuplicate=false; eventId=EventID[xxx.xxx.x.xx(client:loner):57269:e48f0f72:client;threadID=0x1030004|1;sequenceID=6]
    queueKey=2; region=/Trade; operation=CREATE; eventKey=12; value=PDX[26084582,example.client.domain.Trade]{id=12}; possibleDuplicate=false; eventId=EventID[xxx.xxx.x.xx(client:loner):57269:e48f0f72:client;threadID=0x1030004|1;sequenceID=12]
    queueKey=3; region=/Trade; operation=CREATE; eventKey=17; value=PDX[26084582,example.client.domain.Trade]{id=17}; possibleDuplicate=false; eventId=EventID[xxx.xxx.x.xx(client:loner):57269:e48f0f72:client;threadID=0x1030004|1;sequenceID=17]
    queueKey=4; region=/Trade; operation=CREATE; eventKey=21; value=PDX[26084582,example.client.domain.Trade]{id=21}; possibleDuplicate=false; eventId=EventID[xxx.xxx.x.xx(client:loner):57269:e48f0f72:client;threadID=0x1030004|1;sequenceID=21]
    ...
```
### Shutdown Locator and Servers
Sample output from the *shutdownall.sh* script is:

```
./shutdownall.sh 

(1) Executing - connect

Connecting to Locator at [host=localhost, port=10334] ..
Connecting to Manager at [host=192.168.1.11, port=1099] ..
Successfully connected to: [host=192.168.1.11, port=1099]


(2) Executing - shutdown --include-locators=true

Shutdown is triggered
```
