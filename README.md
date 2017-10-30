# ticket-service
This service is implemented in 3 ways.
1. Queue based service using apache activeMQ camel.
2. Distributed caching using Hazelcast(https://hazelcast.com/)
3. Spring boot web based + Hibernate + in-memory-DB + second level caching using Hazelcast.


# Queue based Service using camel activemq. Persistence layer inmemory DB.

I Queue based, email sent/ for now saving the UUID in file, user and using the UUID and email to reserve the seats(the file is in archive folder configurable in application.properties)
Note for configurations: application.properties
	A.hold.time.out provide in seconds.
	B.uuid.files file location where UUID is saved to reserve within timeout.
	 
1.ticket-serice using activemq , where every request is sent in to queue and customer is sent mail with UUID, customer using screen must
enter UUID and email to reserve the seats The Link expires in certain configure time. 
Please refer:
TicketHolderQueueDequeueRoute.java
TicketHolderQueueEnqueueRoute.java
Technologies, Apache camel , Spring boot, embedded activemq.

Queue will use to save data in H2 DB.

# Distributed cache based Service using Hazelcast.
1.ticket-service is spring boot based application uses hazelcast for data soruce and distributed cache.

Please refer:
TicketServiceImpl.java

2.Ticket life cycle is handled in IMap of Hazelcast with following lifecycle

	Avilable ->    Hold     -> Reserve 
             <- If Timeout
                
3. When ticket is on hold it exipres in give time, IMap.Put with TTL is used.
4. A Event Expire listener is added to IMap, it get triggered when sethold id -> UUID contains List of tickets which is 30 seconds old.
5. To handled concurrent thread handling and cluster locking mechanism Hazlecast is being used.                


# Normal Spring boot webapplication using inmemory DB.

Please refer:
TicketServiceUsingDaoServiceImpl

1. Normal Spring boot web based application
2. In memory DB.
3. Refresh data based on requests, example of x number of seat on hold and timeout for those seats then next request will automaticall refresh 
in number of seats if timeout is met.


Junits is added in both the cases.

Steps to execute:
1. go to ticket-service folder.
2. mvn clean package install
3. copy tar or zip from ticket-service to folder
4. tar -xvf ticket-microservice-1.0-SNAPSHOT-bin.tar.gz in case of linux
5. bash ./start.sh  
6 unzip ticket-microservice-1.0-SNAPSHOT-bin.zip
7 start.bat

For DAO method ticket bookings
http://localhost:8080/async/startBookings


For DAO method ticket bookings
http://localhost:8080/dao/startBookings

For Hz method ticket bookings.
http://localhost:8080/hz/startBookings

For Reset DB data.
http://localhost:8080/resetAll

