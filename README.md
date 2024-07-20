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

## Consumer Re-Balancing with CooperativeStickyAssignor

#### _PreRequisite To follow along_

_editConfiguration -> Modify options -> allow multiple instances_
<img width="1259" alt="image" src="https://github.com/user-attachments/assets/1d466ad2-3ace-45fd-9ec9-406b27e03adc">
_run the code to add a new consumer (keep changing server.port in application.yml before running)_

### Consumer 1

<img width="1573" alt="image" src="https://github.com/user-attachments/assets/842a93e2-d5fa-4070-ba01-2ac088900d01">
_initially assigned to 3 partitions (partition-0, partition-1, partition-2)_
<img width="1604" alt="image" src="https://github.com/user-attachments/assets/c0ce0c89-cbc7-433c-a311-8bfabd00ec70">

### When Consumer 2 added

**In Consumer 1**

- partition-2 started to revoked
  <img width="1673" alt="image" src="https://github.com/user-attachments/assets/e1f0e611-e5ad-4a54-b363-c1304048d016">
- partition-2 revoked incrementally
  <img width="1652" alt="image" src="https://github.com/user-attachments/assets/13ff594c-cdc5-4a88-9c1c-ce54d3f5afd8">

**In Consumer 2**

- Init
  <img width="1666" alt="image" src="https://github.com/user-attachments/assets/a55fe224-3b9e-4459-9675-472f2fc65263">
- partition-2 assigned
  <img width="1666" alt="image" src="https://github.com/user-attachments/assets/d3c99048-a4e0-4fde-9305-b85ae94731ca">

### When Consumer 3 added

**In consumer 1**

- partition-1 revoke process started
  <img width="1584" alt="image" src="https://github.com/user-attachments/assets/63d6a0d3-ed5f-487b-bcb9-f43908351056">
- partition-1 revoked incrementally
  <img width="1601" alt="image" src="https://github.com/user-attachments/assets/42c6a859-020e-4faf-940b-554c5cacf1ce">

**In Consumer 2**

- no change

**In Consumer 3**

- Init
  <img width="1563" alt="image" src="https://github.com/user-attachments/assets/8cc66df8-1e8a-4ee3-b3ee-ac3ad23008c4">
- Parition-1 Assigned
  <img width="1594" alt="image" src="https://github.com/user-attachments/assets/9f10d059-df88-4d57-821f-6647451b6f86">


