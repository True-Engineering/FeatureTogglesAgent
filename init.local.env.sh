#!/bin/bash

PROJECT_PATH=$(pwd)

LOCAL_ALIAS=172.16.255.255

unameOut="$(uname -s)"
case "${unameOut}" in
    Linux*)     sudo ifconfig lo:0 $LOCAL_ALIAS up;;
    Darwin*)    sudo ifconfig lo0 alias $LOCAL_ALIAS;;
    *)          echo 'Cant set loopback alias for your OS. Try to set local loopback alias manually with IP 172.16.222.111'; exit 1;;
esac

rm $PROJECT_PATH/config/dev/docker/.env
touch $PROJECT_PATH/config/dev/docker/.env

echo 'PROJECT_PATH='$PROJECT_PATH >>$PROJECT_PATH/config/dev/docker/.env
echo 'LOCAL_ALIAS='$LOCAL_ALIAS >>$PROJECT_PATH/config/dev/docker/.env

cat $PROJECT_PATH/config/dev/docker/.env

FF_AGENT_CONFIG_PATH=$PROJECT_PATH/config/dev

if sudo grep -q FF_AGENT_CONFIG_PATH /etc/environment; then
  cat /etc/environment
else
  sudo bash -c "echo FF_AGENT_CONFIG_PATH=$FF_AGENT_CONFIG_PATH >> /etc/environment"
fi

cat /etc/environment
