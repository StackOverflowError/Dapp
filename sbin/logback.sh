#!/bin/sh

. $HOME/LCMABASS_A/bin/base

echo "日志备份根路径：$LOGBACK_ROOT"

while [ 1 ]
do
        cd $HOME/logs
        for syscodeDir in $(ls)
        do
                if [[ -d $syscodeDir && ! $syscodeDir =~ twsecurity && ! $syscodeDir =~ redis ]];then
                        NOW_DATE=`date +%Y%m%d`
                        YEAR=`date +%Y`
                        MONTH=`date +%m`
                        DAY=`date +%d`

                        echo "查找目录【$HOME/logs/$syscodeDir】的console.*.gz文件"
                        for logConsole in `ls $HOME/logs/$syscodeDir |grep console.*.`
                        do
                                #今天的日志不搬迁
                                if [[ $logConsole =~ "$YEAR-$MONTH-$DAY"  ]];then
                                        continue
                                fi

                                if [[ ! $logConsole =~ "gz"  ]];then
                                        echo "压缩目录【$HOME/logs/$syscodeDir】未压缩的文件$logConsole"
                                        gzip $HOME/logs/$syscodeDir/$logConsole
                                fi

                                LOG_YEAR=`echo "$logConsole" |awk -F '.' '{print $2}' |awk -F '-' '{print $1}'`
                                LOG_MONTH=`echo "$logConsole" |awk -F '.' '{print $2}' |awk -F '-' '{print $2}'`
                                LOG_DAY=`echo "$logConsole"|awk -F '.' '{print $2}' |awk -F '-' '{print $3}'`

                                mkdir -p $LOGBACK_ROOT/$syscodeDir/$LOG_YEAR$LOG_MONTH$LOG_DAY
                                mv $HOME/logs/$syscodeDir/console.$LOG_YEAR-$LOG_MONTH-$LOG_DAY.*.gz $LOGBACK_ROOT/$syscodeDir/$LOG_YEAR$LOG_MONTH$LOG_DAY
                        done



                        START_BACK_PATH=$LOGBACK_ROOT/$syscodeDir/start
                        mkdir -p $START_BACK_PATH


                        echo "查找目录【$HOME/logs/$syscodeDir】超过100M的start日志并压缩"
                        find  $HOME/logs/$syscodeDir/start -type f -size +100M -exec gzip {} \;

                        START_LOG_GZ=`ls $HOME/logs/$syscodeDir |grep start.*.gz`
                        if [[ $START_LOG_GZ =~ gz ]];then
                                touch $HOME/logs/$syscodeDir/start
                                mv $HOME/logs/$syscodeDir/start.*.gz $START_BACK_PATH
                        fi

                        echo "日志目录【$HOME/logs/$syscodeDir】备份完成！"
                fi
        done
        sleep 10800
done
