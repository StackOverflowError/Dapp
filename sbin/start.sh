#!bin/sh
. $HOME/LCMABASS_A/bin/base

cd $JETTY_HOME

#判断是否已经存在进程
CHECK_PID=`ps -ef|grep $INTF_SYSCODE`

if [[ $CHECK_PID =~ java ]];then
        echo "该应用已经启动，不允许重复启动！"
        echo "执行[ps -ef|grep $INTF_SYSCODE]结果："
        echo "$CHECK_PID"
        echo "退出！"
        exit
fi


#添加启动文件
chmod -R 775 $JETTY_HOME/bin/*
#启动
nohup $JAVA_HOME/bin/java $JAVA_OPTS $JETTY_OPTS $SPRING_OPTS -jar $JETTY_HOME/lib/$PROJECT_NAME-0.0.1-SNAPSHOT.jar >>$START_FILE 2>&1 &

#删除上一次的pid
rm -f $HOME/$INTF_SYSCODE/bin/*.pid
#新建一个空的pid文件
touch $HOME/$INTF_SYSCODE/bin/$INTF_PORT.pid
echo "$!" > $HOME/$INTF_SYSCODE/bin/$INTF_PORT.pid
PID=`cat $HOME/$INTF_SYSCODE/bin/$INTF_PORT.pid`
echo "应用$INTF_SYSCODE的进程号为：$PID!"


#判断是否已经启动，如果已经启动，那么不启动
echo "检查日志备份系统是否已经启动！"
IS_LOG_BACK_STARTED=`ps -ef|grep logback.sh`
if [[ $IS_LOG_BACK_STARTED =~ $INTF_SYSCODE ]];then
        echo "日志备份系统已经启动，无需再次启动！退出！"
        exit
fi

echo "日志备份系统未启动，调用日志备份脚本！启动日志备份！"
#nohup sh $HOME/$INTF_SYSCODE/bin/logback.sh >>/dev/null 2>&1 &