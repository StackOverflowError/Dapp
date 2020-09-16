#!bin/sh

. $HOME/LCMABASS_A/bin/base

cd $JETTY_HOME

#判断是否需要停机
STATUS_INFO=`ps -ef|grep $INTF_SYSCODE`
if [[ $STATUS_INFO =~ java ]];then
	#执行关闭命令
	cd $JETTY_HOME/bin
	sh $JETTY_HOME/bin/stop.sh
	STATUS_INFO=`ps -ef|grep $INTF_SYSCODE`
	if [[ $STATUS_INFO =~ java ]];then
		echo "关闭应用失败，请联系管理员重启！status=Undefined"
		exit
	fi
fi

#开始重启
cd $JETTY_HOME/bin
sh $JETTY_HOME/bin/start.sh

#检查状态
echo "等待30s检查状态"
sleep 30s
cd $JETTY_HOME/bin
CHECK_STATUS=`sh $JETTY_HOME/bin/status.sh`
echo "$CHECK_STATUS"
