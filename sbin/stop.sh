#!/bin/sh

. $HOME/LCMABASS_A/bin/base

cd $HOME/$INTF_SYSCODE/bin
#先判断是否已经停止，如果已经停止，那么无需停止
CHECK_PID_STATUS=`ps -ef|grep $INTF_SYSCODE`
if [[ ! $CHECK_PID_STATUS =~ java ]];then
	echo "系统已经停止，无需再次停止！"
	exit;
fi

#发送远程请求
REQ_URL=http://$REMOTE_IP:$INTF_PORT/services/check
#停止URL
STOP_URL=http://$REMOTE_IP:$STOP_PORT/management/shutdown

APP_STATUS=false
count=0
while [[ $count < 20 ]]
do 
	echo "正在发送关闭主机请求[$REQ_URL?status=closeAble],需要等待，最多等5s"
	echo "curl -s --connect-timeout 5 $REQ_URL?status=closeAble"
	APP_STATUS=`curl -s --connect-timeout 5 $REQ_URL?status=closeAble`
	echo "$APP_STATUS"	
	if [[ $APP_STATUS = true ]];then
		break
	fi
	sleep 5
	let count++

done
if [[ $APP_STATUS = true ]];then
    echo "已经停止接收请求，可以关闭主机！[status=closeAble]"
    echo "开始关闭应用$INTF_SYSCODE"
    PID_RESULT=`ps -ef|grep $INTF_SYSCODE`
    if [[ $PID_RESULT =~ java ]];then
	PID_RESULT=`ps -ef|grep $INTF_SYSCODE|grep java`
        array=(${PID_RESULT// / })  
        PID=${array[1]}
        kill -9 $PID
        exit
    fi
    echo "系统关闭完成！"
    exit
else
	sleep 10s
	APP_STATUS=`curl -s --connect-timeout 30 $REQ_URL`
	if [[ $APP_STATUS = true ]];then
		echo "主机正在接受请求，关闭主机失败,退出！请稍后重试或者联系管理员！[status=true]"
		cd $JETTY_HOME/bin
		exit
	elif [[ $APP_STATUS = false  ]];then
		echo "系统已经停止接收请求，暂停30s后强制关闭主机![status=false]"
		sleep 30s
		PID_RESULT=`ps -ef|grep $INTF_SYSCODE`
		if [[ $PID_RESULT =~ java ]];then
		    PID_RESULT=`ps -ef|grep $INTF_SYSCODE|grep java`
        	    array=(${PID_RESULT// / })
                    PID=${array[1]}
                    kill -9 $PID
                    exit
		else 
			echo "系统关闭完成！"
			cd $JETTY_HOME/bin
			exit
		fi	
	elif [[ $APP_STATUS =~ http ]];then
		echo "系统异常，可以强制关闭！暂停30s后关闭"
		sleep 30s
		PID_RESULT=`ps -ef|grep $INTF_SYSCODE`
		if [[ $PID_RESULT =~ java ]];then
		    PID_RESULT=`ps -ef|grep $INTF_SYSCODE|grep java`
        	    array=(${PID_RESULT// / })
                    PID=${array[1]}
                    kill -9 $PID
                    exit
		else 
			echo "系统关闭完成！"
			cd $JETTY_HOME/bin
			exit
		fi	
	else 
		echo "系统请求出错，请联系管理员！[status=error]"
		cd $JETTY_HOME/bin
		exit

	fi 
fi




