# WoTExperiments

This repository shows a proof of concept for using the web of thing as a context provider in Orion Context Broker.  The application provided here is a Web of Things servient that has been integrated with Orion Context Broker.

## Prerequisites

- docker
- docker compose
- maven

## Architecture

Components for this software demo is described as follows:

- Parser: it parses the Thing Descriptor (JSON-LD) provided in the as input config for the spring-boot application. The parser aims to retrieve the properties for each of the things described in each JSON-LD file with its accompanying details. Here we intend to store in a map data structure each property with its info about how to retrieve values for each. This process is done in the runtime of the application.

- Web Service: The web service works as a REST-API to retrieve information about the Thing Descriptors. It extracts the values of each of the properties in an NGSI-V1 format to be compatible when queried by Orion Context Broker.

- Postman is a RESTful- API client uses to query Orion Context broker.
- Orion Context broker uses mongo to store its intermediate information

For more information about the concept of context provider, please check this tutorial able [context provider](https://github.com/Fiware/tutorials.Context-Providers).

For more information about thing descriptor and JSON-LD, please check [W3C Thing Description specification](https://w3c.github.io/wot-thing-description/)

![](Architecture.jpg).

## How to run the example

the following bash command uses maven to dockerize the spring-boot application in an image called "wotcp".

```console
mvn package -Pdocker
```
then we can start the docker-compose file to run the component described above at the same time (Orion CB, wotcp, mongodb)

```console
docker-compose orion up
```


## Thing Descriptor Information

We can query the Thing Descriptor that has been provided and the properties for each by:

```console
curl --request GET \ --url http://localhost:8080/properties 
```

The response contains the name of each Thing Descriptor file, the properties described in each JSON-LD file and the address to get the values for each of the properties. Here we use the [WebOfThing Book Public API](http://gateway.webofthings.io/)


```json
{
    "Sensor2": {
        "temperature": "http://gateway.webofthings.io/properties/temperature"
    },
    "myTempSensor": {
        "temperature": "http://gateway.webofthings.io/properties/temperature",
        "relativeHumidity": "http://gateway.webofthings.io/properties/humidity"
    }
}
```

## Simulate the Orion CB request for querying context information

```console
curl --request POST \
  --url http://localhost:8080/queryContext \
  --header 'Accept: application/json' \
  --header 'Content-Type: application/json' \
  --data '	{
    "entities": [
        {
            "type": "Product",
            "isPattern": "false",
            "id": "Sensor2"
        }
    ],
    "attributes": [
        "temperature"
    ]
}'
```

response

```json
{
    "contextResponses": [
        {
            "contextElement": {
                "attributes": [
                    {
                        "metadatas": [
                            {
                                "name": "timestamp",
                                "type": "DateTime",
                                "value": "2018-11-16T09:31:56.695Z"
                            }
                        ],
                        "name": "temperature",
                        "type": "Number",
                        "value": "15"
                    }
                ],
                "id": "Sensor2",
                "isPattern": "false",
                "type": "Product"
            },
            "statusCode": {
                "code": "200",
                "reasonPhrase": "OK"
            }
        }
    ]
}
```

## Entity Creation

```console
curl --request POST \
  --url 'http://{{orion}}/v2/entities/' \
  --header 'Content-Type: application/json' \
  --data ' {
      "id":"Sensor2",
      "type":"Product",
      "name": {
        "type":"Text",
      "value":"Sensor2"
      },
      "MyTemp":{
        "type":"Number",
        "value": 99
      }
}'
```

## Retrieve Entity Information

```console
 curl --request GET \ --url 'http://{{orion}}/v2/entities/Sensor2'
```

```json

{
    "id": "Sensor2",
    "type": "Product",
    "name": {
        "type": "Text",
        "value": "Sensor2",
        "metadata": {}
    }
}

```

## Context Provider Registration

```console
curl --request POST \
  --url 'http://{{orion}}/v2/registrations' \
  --header 'Content-Type: application/json' \
  --data '{
   "description": "Sensor2 web of Thing",
   "dataProvided": {
     "entities": [
       {
         "id" : "Sensor2",
         "type": "Product"
       }
     ],
     "attrs": [
        "name": "temperature"
    ]
   },
   "provider": {
     "http": {
       "url": "http://localhost:8080"
     },
     "legacyForwarding": true
   },
   "status": "active"
}'
```

list all regestrations

```console
curl --request GET \
  --url 'http://{{orion}}/v2/registrations'
```

```json
{
   "description": "Sensor2 web of Thing",
   "dataProvided": {
     "entities": [
       {
         "id" : "Sensor2",
         "type": "Product"
       }
     ],
     "attrs": [
     	"temperature"
    ]
   },
   "provider": {
     "http": {
       "url": "http://wotcp:8080"
     },
     "legacyForwarding": true
   },
   "status": "active"
}
```

to check if the context provider is able to retrieve values.

curl --request GET \
  --url 'http://{{orion}}/v2/entities/Sensor2'

```json

{
    "id": "Sensor2",
    "type": "Product",
    "name": {
        "type": "Text",
        "value": "Sensor2",
        "metadata": {}
    },
    "temperature": {
        "type": "Number",
        "value": "15",
        "metadata": {
            "timestamp": {
                "type": "DateTime",
                "value": "2018-11-16T09:31:56.695Z"
            }
        }
    }
}

```