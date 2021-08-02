# API

- [API](#api)
  - [Overview](#overview)
  - [cURL commands](#curl-commands)

The REST API is divided in two main parts:  

- `/rsa` handles the part related to the encryption, decryption and brute force with RSA keys.  
- `/chapter` handles the part related to [CRUD](https://en.wikipedia.org/wiki/Create,_read,_update_and_delete) operations on a H2 database. An in-memory database has been chosen to avoid bottlenecks related to the underlying data devices, i.e. the disks.  

Note that for `Quarkus` the CRUD API is repeated two more times with prepended endpoints `/async` and `/blocking` to show three different ways of writing response handlers that utilize I/O threads rather than worker threads. However these endpoints have not been tested.  

A [configuration](jmeter/web_frameworks_comparison.postman_collection.json) for [Postman](https://www.postman.com/) is provided in the repository to check the endpoints.  
It assumes the server runs on `localhost` on port `8080`.  

## Overview

Custom objects

```json
PrivateKey {
    String n;
    String d;
};
PublicKey {
    String n;
    String e;
};
Chapter_1 {
    Long id;
    String number;
    String title;
}
Chapter_2 {
    Long id;
}
Event_1 {
    Long id;
    String text;
}
```

The following description does not follow any standard rule, but it's reported in plain text for simplicity. Note that some frameworks (e.g. Quarkus) provide direct integration with automated tools to build HTML pages for the API (e.g. [Swagger](https://swagger.io/)).  

```unknown
/rsa
    GET : get a pair of rsa keys
        with optional parameters: 
            Long length; // in bits
            Long seed;
        returns JSON payload:
            PublicKey publicKey;
            PrivateKey privateKey;

/rsa/encrypt
    POST : encrypt a message
        with JSON payload:
            String text;
            PublicKey publicKey;
        returns JSON payload:
            String message;
        throws:
            MessageTooLongException if text to encrypt is too long for the given key

/rsa/decrypt
    POST : decrypt a message
        with JSON payload:
            String text;
            PublicKey publicKey;
            PrivateKey privateKey;
        returns JSON payload:
            String message;

/rsa/brute
    GET : brute force a public key to find the private key
        with JSON payload:
            String n;
            String e;
        returns JSON payload:
            String n;
            String d;

/chapter
    GET : get a list of all chapters
        returns JSON payload:
            Chapter_1[] chapters; 

    POST : create a new chapter
        with JSON payload:
            String number;
            String title;
        returns Location header and JSON payload:
            Long id;
            String number;
            String title;

/chapter/{chapter}
    {chapter} is the id

    GET : get info about a chapter
        returns JSON payload:
            Long id;
            String number;
            String title;
            Event_1 starter;
            Event_1[] events;
    
    PUT : update a chapter
        with JSON payload:
            String number;
            String title;
            Event_1 starter;
        returns JSON payload:
            Long id;
            String number;
            String title;
            Event_1 starter;
            Event_1[] events; 
    
    DELETE : delete a chapter and all its events

/chapter/{chapter}/start
    {chapter} is the id

    GET : get first event of requested chapter
        returns JSON payload:
            Long id;
            String text;
            Chapter_2 chapter;
    
    PUT : update the first event of a chapter
        with mandatory parameter:
            id
        returns JSON payload:
            Long id;
            String text;
            Chapter_2 chapter;

/chapter/{chapter}/event
    {chapter} is the id

    GET : get a list of all events for this chapter
        returns JSON payload:
            Event_1[] events; 

    POST : create a new event
        with JSON payload:
            String text;
        returns JSON payload:
            Long id;
            String text;
            Chapter_2 chapter;


/chapter/{chapter}/event/{event}
    {event} is the id

    GET : get event
        returns JSON payload:
            Long id;
            String text;
            Chapter_2 chapter;
    
    PUT : update an event
        wih JSON payload:
            String text;
        returns JSON payload:
            Long id;
            String text;
            Chapter_2 chapter;
    
    DELETE : delete an event
        with optional parameters: 
            Boolean force;
            Boolean cascade;
        By default the event is deleted only if it is not the starter of a chapter, otherwise throws EventIsUsedAsStarterException.  
        If force is set to true, the previous condition is ignored and the event is deleted.
        If cascade is set to true delete also the chapter and all other events of this chapter.
        Note that if cascade is true, force is automatically implied as true.
```

## cURL commands

List of `cURL` commands to quickly test the API:

```bash
curl --location --request GET 'http://localhost:8080/api/v2/rsa?length=54&seed=5'

curl --location --request POST 'http://localhost:8080/api/v2/rsa/encrypt' \
--header 'Content-Type: application/json' \
--data-raw '{
    "text": "abcd",
    "publicKey": {
        "n": "11916732686438453",
        "e":"716857081457083"
    }
}'

curl --location --request POST 'http://localhost:8080/api/v2/rsa/decrypt' \
--header 'Content-Type: application/json' \
--data-raw '{
    "text": "Gbmh1vhwmQ==",
    "publicKey": {
        "n": "11916732686438453",
        "e":"716857081457083"
    },
    "privateKey": {
        "n": "11916732686438453",
        "d": "2117879153303347"
    }
}'

curl --location --request POST 'http://localhost:8080/api/v2/rsa/brute' \
--header 'Content-Type: application/json' \
--data-raw '{
    "n": "11916732686438453",
    "e": "716857081457083"
}'
```

```bash
curl --location --request GET 'http://localhost:8080/api/v2/chapter'

curl --location --request POST 'http://localhost:8080/api/v2/chapter' \
--header 'Content-Type: application/json' \
--data-raw '{
    "number": "123",
    "title": "title1"
}'

curl --location --request GET 'http://localhost:8080/api/v2/chapter/1'

curl --location --request PUT 'http://localhost:8080/api/v2/chapter/1' \
--header 'Content-Type: application/json' \
--data-raw '{
    "number": "123",
    "title": "title21"
}'

curl --location --request DELETE 'http://localhost:8080/api/v2/chapter/1'

curl --location --request GET 'http://localhost:8080/api/v2/chapter/1/start'

curl --location --request PUT 'http://localhost:8080/api/v2/chapter/1/start?id=32'

curl --location --request GET 'http://localhost:8080/api/v2/chapter/1/event'

curl --location --request POST 'http://localhost:8080/api/v2/chapter/1/event' \
--header 'Content-Type: application/json' \
--data-raw '{
    "text": "event1"
}'

curl --location --request GET 'http://localhost:8080/api/v2/chapter/1/event/1'

curl --location --request PUT 'http://localhost:8080/api/v2/chapter/1/event/1' \
--header 'Content-Type: application/json' \
--data-raw '{
    "text": "eventnew1"
}'

curl --location --request DELETE 'http://localhost:8080/api/v2/chapter/1/event/32?force=true'
```
