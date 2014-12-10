sudo docker build --rm -t ippontech/metrics-spark .
sudo docker run -ti -p 9999:9999 --name spark ippontech/metrics-spark
