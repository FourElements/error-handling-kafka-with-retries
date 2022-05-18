# error-handling-kafka-with-retries

## Problem
This repository serves to address the following question found here:   
https://github.com/brcostamendonca/Retry-POC

"**What is the correct and safe way to retry infinitely?**"

## Solution

Retry "infinitely" but also while maintaining the order redirected events. 
As a core baseline, the solution follows the approach mentioned in Confluent's blog (Pattern 4) \
https://www.confluent.io/blog/error-handling-patterns-in-kafka/

Then it adds some additional complexity to attempt the "infinite" (yes with "" because you should eventually reach a limit and send to a DQL) \
Big picture of the entire solution:

![Solution Design](https://github.com/FourElements/error-handling-kafka-with-retries/blob/main/error_handling_retries.jpg?raw=true)
 
Now going into detail regarding Retry App.
It consumes from retry topic and attempts to execute its main logic, which is to verify if a Device exists before being able to advance.
If Device exists, then it performs the happy path flow:
- maps the event into Person and sends it to the target topic
- send a tombstone record for redirect topic to flag successful completion

On the other hand, if a device still not exists, what to do?
So the idea was to have a retry-counter for each key, stored in a topic, streamed as a GlobalKTable and having a materialized view named person
-retry-counter.\
Also, when consuming an event, if it's to be retried (due to the still missing device), then we create a Header with the key value
 pair to represent the current retry count for that specific event. 
Therefore, the first step to take when actually consuming an event, is to verify that retry counter, and check if it exceeds the maximum amount
 of retries. DLQ flow:
- send event to DQL (this would then require operations team to verify the issue) 
- send a tombstone record for redirect topic to clean that entry

Afterwards, we still need to perform additional validation. It must validate the retry-counter header comparing with the global counter.
Consult the current counter value from the GlobalKTable materialized view. if the event value is inferior, increment the counter and send back the
 event to the topic (which effectively places it last). if the event value is equal, then it's in the proper order and can be processed.
This is to guarantee that events for the same key that arrived later will still respect the order. 
    
## How to run

Run docker-compose.yml

    docker-compose up -d

Bootstrap retry-poc (running RetryApplication) and retrier (running RetrierApplication)
    
Then to see the retries in action, send Create Person 1 without having sent a Create Device 1. Afterwards send a couple more ( changing for
 example the job name and making sure you have a **unique eventId**. Advise to use Postman {{$guid}} , eg: "eventId": "{{$guid}}" ).
Finally, send Create Device 1 and see all events processed properly and in the right order.  
    
## Useful requests:

    - Create Person 1
    
        curl --location --request POST 'http://localhost:8082/topics/bcm.test.queuing.person.in' \
        --header 'Content-Type: application/vnd.kafka.json.v2+json' \
        --header 'Accept: application/vnd.kafka.v2+json' \
        --data-raw '{
            "records": [
                {
                    "key": "test-person-1",
                    "value": {
                        "eventId": "ff9d9af0-e826-4eda-8173-378d6eaff8f5",
                        "id": "test-person-1",
                        "name": "Bruno",
                        "job": "developer",
                        "deviceId": "test-device-1"
                    }
                }
            ]
        }' 

    - Create Device 1

        curl --location --request POST 'http://localhost:8082/topics/bcm.test.queuing.device.in' \
        --header 'Content-Type: application/vnd.kafka.json.v2+json' \
        --header 'Accept: application/vnd.kafka.v2+json' \
        --data-raw '{
                "records": [
                    {
                        "key": "test-device-1",
                        "value": {
                            "eventId": "9fc283f3-8b7d-49ca-a38b-e83375e12599",
                            "id": "test-device-1",
                            "description": "HP laptop i7 32gb ram"
                        }
                    }
                ]
            }'
    
    - Check if device exists in state store

        curl --location --request GET 'http://localhost:9205/stateStore-api/example/v1/device/test-device-1?type=device'    