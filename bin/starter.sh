#!/bin/bash

# 示例 ./starter.sh -c true -e test -o start

while getopts ":c:e:o" opt
do
    case  $opt in
    c)
        compile="$OPTARG"
      ;;
    e)
       env="$OPTARG"
       ;;
    o)
        op="$OPTARG"
    ;;
    d)
       debug="$OPTARG"
       ;;
esac done

pcompile=${compile:-true}
penv=${env:-ut}
pop=${op:-start}

echo "当前环境:$penv"

if [  $compile="true" ]; then
    echo "开始编译 环境:$penv"
   (cd ../ && mvn clean package -Dmaven.test.skip=true)
fi

if [ $penv="test" ]; then
    pdebug=${debug:--agentlib:jdwp=transport=dt_socket,server=y,suspend=n,address=5005}
    echo "debug参数: $pdebug"
fi

if [ $penv="ut" ]; then
    pdebug=${debug:--agentlib:jdwp=transport=dt_socket,server=y,suspend=n,address=5005}
    echo "debug参数: $pdebug"
fi

if [ $pop="start" ];then
   (cd ../ && java "$pdebug" -jar memberclub.starter/target/memberclub-starter.jar --spring.profiles.active="$penv")
fi

