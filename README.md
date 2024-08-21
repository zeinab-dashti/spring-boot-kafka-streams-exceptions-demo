# Exception Handling in Kafka Streams and Spring Boot Application Demo

## Overview
In this repository, you will find various custom exception handlers designed to address different types of exceptions that can occur in the three main components of a typical Kafka Streams application: deserialization, topology for business logic, and serialization.


## Prerequisites
* Java 17 or higher
* Maven
* Docker (optional, for running Docker Compose which include Zookeeper and Apache Kafka)


## Running the Application
1. **Clone the repository**
   ```sh
   git clone <repository-url>
   cd spring-boot-kafka-streams-exceptions-demo
   ```

2. **Start Kafka and Zookeeper by using Docker Compose file in the repository**:
   ```sh
   docker-compose up
   ```

3. **Build**:
   ```sh
   mvn clean install
   ```

4. **Run the application**

   ```sh
   mvn spring-boot:run
   ```

5. **Produce dummy data to test the application with correct data**
    Run [producer](./src/main/java/space/zeinab/demo/kafka/producer/MockCorrectUserDataProducer.java) to producer correct mock data which application processes them successfully.

6. **Test Exception Handlers**

   ***Deserialization Exceptions***
   
   This application expects Json as input. Kafka Streams default behaviour for input which is not Json is log and shut down the application.
   
   We overwrite the default behaviour, so that it will continue process for any dummy and not Json data except 'dummyTextForShutDown' text
 
   - Import dummy and not Json data 
     - Run the following command to test the ``log and continue`` behaviour. Basically you can use any text instead of 'dummyTextMessage' value 
       ```sh
       docker exec -it broker bash -c "echo 'dummyTextMessage' | kafka-console-producer --broker-list localhost:9092 --topic input-topic"
       ```
     - Run the following command to test the ``log and shut down`` behaviour. This behaviour only happens if you 'dummyTextForShutDown' text for the message
       ```sh
       docker exec -it broker bash -c "echo 'dummyTextForShutDown' | kafka-console-producer --broker-list localhost:9092 --topic input-topic"
       ```
       When application is terminated, then you need to run the following command to ``delete topic`` and remove the corrupted data. Otherwise, next time that you start the application it will fail again!
       ```sh
       docker exec -it broker kafka-topics --bootstrap-server localhost:9092 --delete --topic input-topic
       ```
   
   
   ***Topology Related Exceptions***

   This repository has three producer to produce Mock data and test the custom exception handlers

   - Run [producer](./src/main/java/space/zeinab/demo/kafka/producer/MockCorrectUserDataProducer.java) to producer correct mock data.

   - Run [producer](./src/main/java/space/zeinab/demo/kafka/producer/MockUserDataForErrorAndContinueProducer.java) to producer mock data with error and then test the ``continues process the message`` behaviour which will be running the application in an infinite loop.
     
      - When application is terminated, then you need to run the following command to ``delete topic`` and remove the corrupted data. Otherwise, next time that you start the application it will run in an infinite loop again! 
       ```sh
       docker exec -it broker kafka-topics --bootstrap-server localhost:9092 --delete --topic input-topic
       ```
   
   - Run [producer](./src/main/java/space/zeinab/demo/kafka/producer/MockUserDataForErrorAndshoutDownProducer.java) to producer mock data with error and then test the ``shut down`` behaviour because of the exception. 

       - When application is terminated, then you need to run the following command to ``delete topic`` and remove the corrupted data. Otherwise, next time that you start the application it will shut down again!
       ```sh
       docker exec -it broker kafka-topics --bootstrap-server localhost:9092 --delete --topic input-topic
       ```