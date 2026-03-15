git pull origin main

cd vr-classroom-backend && mvn clean package -DskipTests 

cd /home/zdd/桌面/HRX/VR-Classroom && docker ps -a | grep vr-classroom | awk '{print $1}' | xargs docker rm -f

cd /home/zdd/桌面/HRX/VR-Classroom && docker-compose up -d --build vr-classroom-backend-1 nginx redis vr-classroom-management

docker ps --filter "name=vr-classroom" --format "table {{.Names}}\t{{.Status}}\t{{.Ports}}"