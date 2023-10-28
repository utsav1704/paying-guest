# paying-guest

This is a Spring Boot micro-services project with three services: API-GATEWAY, AUTH-SERVER, and PG-OWNER.
There is also an EMAIL-SERVICE that sends emails depending on topics using Apache Kafka.
The program employs method-level security, and owners have the ability to add, update, or delete their personal and property information.
Admins must validate property and owner information.
The owner's identity information is encrypted.
