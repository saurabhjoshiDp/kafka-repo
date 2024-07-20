### Run Locally using brew

/opt/homebrew/opt/kafka/bin/zookeeper-server-start /opt/homebrew/etc/kafka/zookeeper.properties
/opt/homebrew/opt/kafka/bin/kafka-server-start /opt/homebrew/etc/kafka/server.properties

## Topic

### Create

#### create topic with Default Config

 ``` 
 kafka-topics --bootstrap-server localhost:9092 --create --topic first_topic
```

- use `--botstrap-server` instead of `--zookeeper` as it is going to deprecated

#### with partitions

```
kafka-topics --bootstrap-server localhost:9092 --create --topic first_topic —partition 5
```

#### with replication factor

```
kafka-topics --bootstrap-server localhost:9092 --create --topic first_topic --replication-factor 2
```

#### final

```
kafka-topics --bootstrap-server localhost:9092 --create --topic first_topic --replication-factor 1 --partitions 1
```

### List

```
kafka-topics --bootstrap-server localhost:9092 --list
```

### Describe

```
kafka-topics --bootstrap-server localhost:9092 --describe --topic first_topic
```

### Delete

```
kafka-topics --bootstrap-server localhost:9092 --delete --topic first_topic
```

## Producer

#### start producer without key

```
kafka-console-producer --bootstrap-server localhost:9092 --topic first_topic --producer --property acks=all
```

#### start producer without key

```
kafka-console-producer --bootstrap-server localhost:9092 --topic first_topic --producer --property acks=all 
--property parse.key=true --property key.separator=:
```

### producer with partition.class = roundRobin

```
kafka-console-producer --bootstrap-server localhost:9092 --topic first_topic 
--producer-property partitioner.class=org.apache.kafka.clients.producer.RoundRobinPartitioner
```

### Producing to non existing topic

- it will create the topic with default configuration (from server.properties) but
- if topic auto creation is disabled then topic will not appear in the list

## Consumer

#### Earliest

```
kafka-console-consumer --bootstrap-server localhost:9092 --topic first_topic --from-beginning
```

#### Latest

```
kafka-console-consumer --bootstrap-server localhost:9092 --topic first_topic
```

### with other properties

```
kafka-console-consumer --bootstrap-server localhost:9092 --topic first_topic 
--formatter kafka.tools.DefaultMessageFormatter 
--property print.timestamp=true --property print.key=true 
--property print.value=true --property print.partition=true 
--property print.offset=true --from-beginning
```

_**As consumerGroup are not mention hence will be added to a newly created temporary random consumer group by default**_

can verify this using group [describe](#consumer-group-describe) cmd

### start consumer in a consumerGroup

```
kafka-console-consumer --bootstrap-server localhost:9092 --topic first_topic --group my-console-application --from-beginning
```

## Consumer Group

**Consumer Groups will be auto created if mentioned while starting a consumer**

### Consumer Group Describe

```
kafka-consumer-groups --bootstrap-server localhost:9092 --describe --group consumer-group-1
```

#### DEMO

1. **create a topic with 3 partitions**

```
kafka-topics.sh --bootstrap-server localhost:9092 --topic tp_cons_demo --create --partitions 3
```

2. **start one producer and start producing**

```
kafka-console-producer --bootstrap-server localhost:9092 --topic tp_cons_demo --producer-property partitioner.class=org.apache.kafka.clients.producer.RoundRobinPartitioner --property parse.key=true --property key.separator=:
```

3. start one consumer

```
kafka-console-consumer --bootstrap-server localhost:9092 --topic tp_cons_demo --group consumer-group-1 --from-beginning --formatter kafka.tools.DefaultMessageFormatter --property print.timestamp=true --property print.key=true --property print.value=true --property print.partition=true --property print.offset=true
```

4. start another consumer part of the same group. See messages being spread

```
kafka-console-consumer --bootstrap-server localhost:9092 --topic tp_cons_demo --group consumer-group-1 --from-beginning --formatter kafka.tools.DefaultMessageFormatter --property print.timestamp=true --property print.key=true --property print.value=true --property print.partition=true --property print.offset=true
```

5. start another consumer part of a different group from beginning

```
kafka-console-consumer --bootstrap-server localhost:9092 --topic tp_cons_demo --group consumer-group-2 --from-beginning --formatter kafka.tools.DefaultMessageFormatter --property print.timestamp=true --property print.key=true --property print.value=true --property print.partition=true --property print.offset=true
```

6. Describe

```
kafka-consumer-groups --bootstrap-server localhost:9092 --describe --group consumer-group-1
```

## Reset Offset

Reset Offsets

#### Dry Run:

reset the offsets to the beginning of each partition (**only show the result, but not execute, it’s dry run**)

```
kafka-consumer-groups.sh --bootstrap-server localhost:9092 --group my-first-application --reset-offsets --to-earliest --topic third_topic --dry-run
```

#### execute flag is needed

```
kafka-consumer-groups.sh --bootstrap-server localhost:9092 --group my-first-application --reset-offsets --to-earliest --topic third_topic --execute
```