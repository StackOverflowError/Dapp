#!bin/sh

. $HOME/LCMABASS_A/bin/base

CHECK_URL=http://$REMOTE_IP:$INTF_PORT/services/check

if [[ "$1" = "" ]];then
	#发送远程请求
	echo "正在发送关闭主机请求[$CHECK_URL],可能时间有点儿长，需要等待，最多等30s"
	APP_STATUS=`curl -s --connect-timeout 30 $CHECK_URL`
	if [ "true" = "$APP_STATUS" ];then
		echo "应用正在接收请求！[status=true]"
	elif [ "false" = "$APP_STATUS" ];then
		echo "应用不接收请求！[status=false]"
	else
		echo "应用状态异常！应该是应用没有启动！[status=error]"
	fi
else
        clear
	while ((1)); do
		#设置状态
		#先检查进程，如果进程不存在，其他操作都咩有意义！
		PID_RESULT=`ps -ef|grep $INTF_SYSCODE`
		if [[ ! $PID_RESULT =~ java ]];then
			echo "[$INTF_SYSCODE]应用进程不存在,请先启动应用！退出！"
                        exit
                fi
		echo "------------欢迎进入设置状态页面！------------"
		echo ""
		echo "状态列表："
		echo ""
		echo "    1.发送停止接收请求命令[status=die]"
		echo "    2.发送开始接收请求命令[status=live]"
		echo "    3.发送可关闭状态请求命令[status=closeAble]"
		echo "    4.发送查询请求状态命令[/services/check]"	
		echo "    q/Q.退出"	
		echo ""
		echo "请选择需要设置的状态:" ;read STATUS_COMMEND
		
		if [[ $STATUS_COMMEND = q || $STATUS_COMMEND = Q ]];then
			break
		elif [[ $STATUS_COMMEND = 1 ]];then
			CHECK_RES=`curl -s --connect-timeout 30 $CHECK_URL?status=die`
			if [[ $CHECK_RES = true ]];then 
				echo "应用已经停止接收请求！[status=die]"
                        else
                                echo "应用异常[status=error]"
			fi
			continue
		elif [[ $STATUS_COMMEND = 2 ]];then
			CHECK_RES=`curl -s --connect-timeout 30 $CHECK_URL?status=live`
			if [[ $CHECK_RES = true ]];then 
				echo "应用开始接收请求！[status=live]"
                        else
                                echo "应用异常[status=error]"
			fi
			continue
		elif [[  $STATUS_COMMEND = 3 ]];then
			CHECK_RES=`curl -s --connect-timeout 60 $CHECK_URL?status=closeAble`
			if [[ $CHECK_RES = true ]];then 
				echo "应用已经处于可关闭状态！[status=closeAble]"
			else
                echo "应用异常[status=error]"
			fi
			continue
		elif [[  $STATUS_COMMEND = 4 ]];then
			CHECK_RES=`curl -s --connect-timeout 30 $CHECK_URL`
			if [[ $CHECK_RES = true ]];then 
				echo "应用正常，接收请求！[status=true]"
			elif [[ $CHECK_RES = false ]];then
				echo "应用正常，不接收请求！[status=false]"
			else 
				echo "应用异常，[status=error]"
			fi
			continue   
		else
			echo "输入的命令不可识别，输入任意字符，继续！";read COMMEND
			continue
		fi

		echo "发送指令异常：$CHECK_RES"    
	done
fi






