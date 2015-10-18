Garden
======
- Play2 + Maven plugin project configuration setup
- MongoDB java driver APIs in MongoDBManager to create, query and delete
- Play REST API development in GardenController
- Unit tests and Functional test (GardenControllerTest)

To test Garden service
1. Setup remote debugger listen on localhost:9999
2. Start garden service
mvn compile
mvn com.google.code.play2-maven-plugin:play2-maven-plugin:run -Dplay2.serverJvmArgs="-Xrunjdwp:transport=dt_socket,server=y,suspend=n,address=9999"