#! /bin/sh
set -x #echo on
sudo docker run -p 0.0.0.0:1099:1099 -p 0.0.0.0:1100:1100 -e PEER1=172.16.159.132 -e PEER2=172.16.159.133 -v ${PWD}/logdata:/logdata --rm distributedhello 172.16.159.131
