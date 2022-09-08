mvn clean package -Dmaven.test.skip
docker stop jwt_app || true
docker rm jwt_app || true
docker build . -t jwt_app
docker stop mysqldb || true
docker rm mysqldb || true
docker run -e MYSQL_ROOT_PASSWORD=admin123 -p 3307:3306 -v /Users/karol/mysql:/var/lib/mysql --network jwt_app_network --name mysqldb mysql &
sleep 5
docker run -p 8080:8080 --net jwt_app_network --name jwt_app jwt_app &
