#!/bin/bash

BASEDIR=$( cd "$( dirname "${BASH_SOURCE[0]}" )" && pwd )

cd ${BASEDIR}

num=`sysctl -n vm.max_map_count`

if ((num < 262144));
then
    echo "Operating system limits on mmap counts is too low, which prevents elasticsearch from starting."
    echo "Please, modify the vm.max_map_count setting in /etc/sysctl.conf, set vm.max_map_count=262144"
    exit
fi

if [ ! -d esdata ]; then
  mkdir -p esdata;
fi

docker-compose up -d
