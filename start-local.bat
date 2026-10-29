@echo off
echo Starting Eureka Server...
start Eureka /min java -Dspring.profiles.active=local -jar X:\backend\eureka-server\target\eureka-server-0.0.1-SNAPSHOT.jar
ping 127.0.0.1 -n 11 >nul

echo Starting Config Server...
start Config /min java -Dspring.profiles.active=local,native -jar X:\backend\config-server\target\config-server-0.0.1-SNAPSHOT.jar
ping 127.0.0.1 -n 9 >nul

echo Starting Domain Services...
start UserService /min java -Dspring.profiles.active=local -jar X:\backend\user-service\target\user-service-0.0.1-SNAPSHOT.jar
start ActivityService /min java -Dspring.profiles.active=local -jar X:\backend\activity-service\target\activity-service-0.0.1-SNAPSHOT.jar
start RecommendationService /min java -Dspring.profiles.active=local -jar X:\backend\recommendation-service\target\recommendation-service-0.0.1-SNAPSHOT.jar
start PlanService /min java -Dspring.profiles.active=local -jar X:\backend\plan-service\target\plan-service-0.0.1-SNAPSHOT.jar

echo Starting API Gateway...
start Gateway /min java -Dspring.profiles.active=local -jar X:\backend\api-gateway\target\api-gateway-0.0.1-SNAPSHOT.jar
ping 127.0.0.1 -n 7 >nul

echo Starting Frontend...
cd /d X:\frontend
start Frontend /min npm.cmd run dev

echo All services started!
echo Frontend: http://localhost:5173
echo API Gateway: http://localhost:8080
echo Eureka: http://localhost:8761