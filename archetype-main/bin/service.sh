#!/bin/bash
# ${service-name}服务启动脚本

CURRENT_DIR=$(cd "$(dirname "${BASH_SOURCE[0]}")" &>/dev/null && pwd)
THIS_SCRIPT="$CURRENT_DIR/$(basename $0)"

######### 服务名 主类
# 各服务独自维护 #
# APP_NAME 为服务名，与服务目录保持一致
APP_NAME="${service-name}"
# 服务主类
MAIN_CLASS="${start-class}"
######### END

######### JVM HEAP SIZE #########
# ${CW_JVM_HEAP_SIZE} 占位符
# 若环境变量中声明了 ENV_JVM_HEAP_SIZE 则使用环境变量中的值，带单位
INNER_JVM_HEAP_SIZE_MIN="${CW_JVM_HEAP_SIZE}"
INNER_JVM_HEAP_SIZE_MAX="${CW_JVM_HEAP_SIZE}"
######### END
######### 内部变量 部署时自动替换占位符 部署完成后若调整需手动修改变量后面的值
# root 用户部署时，${CW_RUN_USER} 占位符会替换成内置的默认普通用户或部署前修改配置文件使用指定普通用户
INNER_RUN_USER="${CW_RUN_USER}"
# 该节点通信地址
INNER_LOCAL_IP="${CW_LOCAL_IP}"
# PATH
INNER_APP_DIR="${CW_INSTALL_APP_DIR}"
INNER_LOG_DIR="${CW_INSTALL_LOGS_DIR}"
INNER_DATA_DIR="${CW_INSTALL_DATA_DIR}"
# Nacos
INNER_NACOS_SERVER="${CW_NACOS_SERVER}"
INNER_NACOS_USERNAME="${CW_NACOS_USERNAME}"
INNER_NACOS_PASSWORD="${CW_NACOS_PASSWORD}"
INNER_NACOS_NAMESPACE="${CW_NACOS_NAMESPACE}"
# SERVICE
INNER_SERVICE_DBNAME="${CW_SERVICE_DBNAME}"
INNER_SERVICE_PORT="${CW_SERVICE_PORT}"
# RPC
INNER_RPC_PORT="${CW_RPC_PORT}"
# METRICS
INNER_METRICS_PORT="${CW_METRICS_PORT}"
# LOG
INNER_LOG_KAFKA_SERVER="${CW_KAFKA_SERVER}"
INNER_LOG_KAFKA_TOPIC="${CW_LOG_TOPIC}"
######### END

######### 服务目录 通过内部变量自动解析
APP_HOME="${INNER_APP_DIR}/${APP_NAME}"
APP_LOG_DIR="${INNER_LOG_DIR}/${APP_NAME}"
APP_PID_FILE="${APP_HOME}/${APP_NAME}.pid"

######### 读取环境
# bash_profile 为一键部署生成的环境变量文件,位于服务安装目录下
test -f /etc/profile && . /etc/profile
test -f ${INNER_APP_DIR}/bash_profile && . ${INNER_APP_DIR}/bash_profile

######### JVM 参数 内部变量 引用上面定义的变量 保持默认
# NACOS
INNER_JVM_NACOS_SERVER="${INNER_NACOS_SERVER}"
INNER_JVM_NACOS_USERNAME="${INNER_NACOS_USERNAME}"
INNER_JVM_NACOS_PASSWORD="${INNER_NACOS_PASSWORD}"
INNER_JVM_NACOS_NAMESPACE="${INNER_NACOS_NAMESPACE}"
# SERVICE
INNER_JVM_SERVICE_DBNAME="${INNER_SERVICE_DBNAME}"
INNER_JVM_SERVICE_PORT="${INNER_SERVICE_PORT}"
INNER_JVM_SERVICE_HOSTNAME="${INNER_LOCAL_IP}"
INNER_JVM_DATA_PATH="${INNER_DATA_DIR}"
INNER_JVM_TMP_PATH="${INNER_DATA_DIR}/tmp"
# RPC
INNER_JVM_RPC_PORT="${INNER_RPC_PORT}"
# METRICS
INNER_JVM_METRICS_PORT="${INNER_METRICS_PORT}"
INNER_JVM_METRICS_HOSTNAME="${INNER_LOCAL_IP}"
# LOG
INNER_JVM_LOGROOT_PATH="${APP_LOG_DIR}"
INNER_JVM_LOG_KAFKA_SERVER="${INNER_LOG_KAFKA_SERVER}"
INNER_JVM_LOG_KAFKA_TOPIC="${INNER_LOG_KAFKA_TOPIC}"
######### END

######### Java 命令检查
if [ -n "$JAVA_HOME" ]; then
  if [ -x "$JAVA_HOME/jre/sh/java" ]; then
    JAVACMD="$JAVA_HOME/jre/sh/java"
  else
    JAVACMD="$JAVA_HOME/bin/java"
  fi
  if [ ! -x "$JAVACMD" ]; then
    echo "ERROR: JAVA_HOME is set to an invalid directory: $JAVA_HOME"
  fi
else
  JAVACMD="java"
  which java >/dev/null 2>&1 || echo "ERROR: JAVA_HOME is not set and no 'java' command could be found in your PATH."
fi
######### END

######### 查询Nacos配置
nacos_query() {
  INNER_NACOS_GROUP=$1
  NACOS_DATA_ID=$2
  NACOS_KEY=$3
  shift
  shift
  shift
  nacos_query_opts=$@
  nacos_client_jar=$(ls -t ${APP_HOME}/utils/*.jar | grep -E 'nacos-client.*with' | head -n 1)
  nacos_query_cmd="$JAVACMD -jar ${nacos_client_jar} username@${INNER_NACOS_USERNAME} password@${INNER_NACOS_PASSWORD} url@${INNER_NACOS_SERVER} namespace@${INNER_NACOS_NAMESPACE} group@${INNER_NACOS_GROUP} dataId@${NACOS_DATA_ID} key@${NACOS_KEY} $nacos_query_opts"
  result=$($nacos_query_cmd)
  if [[ "$(echo "$result" | grep 'nacos_query_config_success:')" != "" ]]; then
    result=${result#*nacos_query_config_success:}
  else
    result=''
  fi
  echo $result
}
######### END

######### JVM CLASSPATH 路径，自动遍历 服务目录下的 lib
for i in "${APP_HOME}"/lib/*.jar; do
  if [[ -z ${CW_CLASSPATH} ]]; then
    CW_CLASSPATH="$i"
  else
    CW_CLASSPATH="$CW_CLASSPATH":"$i"
  fi
done
CW_CLASSPATH=".:$CW_CLASSPATH"
######### END

######### JVM 参数
# 各服务独自维护 #
JVM_OPTS="
 -server
 -Xms${ENV_JVM_HEAP_SIZE:-${INNER_JVM_HEAP_SIZE_MIN}}
 -Xmx${ENV_JVM_HEAP_SIZE:-${INNER_JVM_HEAP_SIZE_MAX}}
 -XX:SurvivorRatio=6
 -XX:+AlwaysPreTouch
 -XX:+UseG1GC
 -XX:GCTimeRatio=4
 -XX:G1HeapRegionSize=8M
 -XX:ConcGCThreads=2
 -XX:ParallelGCThreads=4
 -XX:G1HeapWastePercent=10
 -XX:+UseTLAB
 -XX:+ScavengeBeforeFullGC
 -XX:+DisableExplicitGC
 -XX:+PrintGCDetails
 -XX:-UseGCOverheadLimit
 -XX:+PrintGCDateStamps
 -XX:MaxGCPauseMillis=200
 -XX:InitiatingHeapOccupancyPercent=85
 -Xloggc:$APP_LOG_DIR/gc-%t.log
 -XX:+UseGCLogFileRotation
 -XX:NumberOfGCLogFiles=3
 -XX:GCLogFileSize=30m
 -Duser.timezone=Asia/Shanghai
 -DcwAppHome=${APP_HOME}
 -Dconfig=$APP_HOME/conf
 -Xbootclasspath/a:$APP_HOME/conf
 -D${service-name}.main.class=${MAIN_CLASS}
 -Dlog4j2.configurationFile=$APP_HOME/conf/log4j2.xml
 -Dlog4j2.contextSelector=org.apache.logging.log4j.core.async.AsyncLoggerContextSelector
 -Dlog4j2.asyncLoggerWaitStrategy=Block
 -Dlog4j2.clock=CachedClock
 -Djava.util.concurrent.ForkJoinPool.common.parallelism=128
 -DIGNITE_UPDATE_NOTIFIER=false
 -Dfile.encoding=UTF-8
 -Drocketmq.client.logUseSlf4j=true
 -Ddubbo.json-framework.prefer=gson
  ${OTHER_OPTS}
"
######### END

######### JVM 参数内置变量判断
# NACOS
if ! [[ -z ${INNER_JVM_NACOS_SERVER} ]]; then
  JVM_OPTS="${JVM_OPTS} -DcwNacosServer=${INNER_JVM_NACOS_SERVER}"
fi
if ! [[ -z ${INNER_JVM_NACOS_USERNAME} ]]; then
  JVM_OPTS="${JVM_OPTS} -DcwNacosUserName=${INNER_JVM_NACOS_USERNAME}"
fi
if ! [[ -z ${INNER_JVM_NACOS_PASSWORD} ]]; then
  JVM_OPTS="${JVM_OPTS} -DcwNacosPassword=${INNER_JVM_NACOS_PASSWORD}"
fi
if ! [[ -z ${INNER_JVM_NACOS_NAMESPACE} ]]; then
  JVM_OPTS="${JVM_OPTS} -DcwNacosNamespace=${INNER_JVM_NACOS_NAMESPACE}"
fi
if ! [[ -z ${INNER_JVM_TMP_PATH} ]]; then
  JVM_OPTS="${JVM_OPTS} -DcwTmpPath=${INNER_JVM_TMP_PATH} -Dnacos.logging.path=${INNER_JVM_TMP_PATH}/nacos/logs -Dnacos.logging.path=${INNER_JVM_TMP_PATH}/nacos/logs -DJM.LOG.PATH=${INNER_JVM_TMP_PATH}/nacos -DJM.SNAPSHOT.PATH=${INNER_JVM_TMP_PATH}/nacos -Dcom.alibaba.nacos.naming.cache.dir=${INNER_JVM_TMP_PATH}/nacos"
else
  JVM_OPTS="${JVM_OPTS} -Dnacos.logging.path=${APP_HOME}/nacos/logs -Dnacos.logging.path=${APP_HOME}/nacos/logs -DJM.LOG.PATH=${APP_HOME}/nacos -DJM.SNAPSHOT.PATH=${APP_HOME}/nacos -Dcom.alibaba.nacos.naming.cache.dir=${APP_HOME}/nacos"
fi
# SERVICE
if ! [[ -z ${INNER_JVM_SERVICE_DBNAME} ]]; then
  JVM_OPTS="${JVM_OPTS} -DcwDbName=${INNER_JVM_SERVICE_DBNAME}"
fi
if ! [[ -z ${INNER_JVM_SERVICE_PORT} ]]; then
  JVM_OPTS="${JVM_OPTS} -DcwServicePort=${INNER_JVM_SERVICE_PORT}"
fi
if ! [[ -z ${INNER_JVM_SERVICE_HOSTNAME} ]]; then
  JVM_OPTS="${JVM_OPTS} -DcwServiceHostname=${INNER_JVM_SERVICE_HOSTNAME}"
fi
# RPC
if ! [[ -z ${INNER_JVM_RPC_PORT} ]]; then
  JVM_OPTS="${JVM_OPTS} -DcwRpcPort=${INNER_JVM_RPC_PORT}"
fi
if ! [[ -z ${INNER_JVM_DATA_PATH} ]]; then
  JVM_OPTS="${JVM_OPTS} -DcwDataPath=${INNER_JVM_DATA_PATH}"
fi
if ! [[ -z ${INNER_JVM_TMP_PATH} ]]; then
  JVM_OPTS="${JVM_OPTS} -DcwTmpPath=${INNER_JVM_TMP_PATH}"
fi
# METRICS
if ! [[ -z ${INNER_JVM_METRICS_PORT} ]]; then
  JVM_OPTS="${JVM_OPTS} -DcwMetricsPort=${INNER_JVM_METRICS_PORT}"
fi
if ! [[ -z ${INNER_JVM_METRICS_HOSTNAME} ]]; then
  JVM_OPTS="${JVM_OPTS} -DcwMetricsHostname=${INNER_JVM_METRICS_HOSTNAME}"
fi
# LOG
if ! [[ -z ${INNER_JVM_LOGROOT_PATH} ]]; then
  JVM_OPTS="${JVM_OPTS} -DcwLogRootPath=${INNER_JVM_LOGROOT_PATH}"
fi
if ! [[ -z ${INNER_JVM_LOG_KAFKA_SERVER} ]]; then
  JVM_OPTS="${JVM_OPTS} -DcwLogKafkaServers=${INNER_JVM_LOG_KAFKA_SERVER}"
fi
if ! [[ -z ${INNER_JVM_LOG_KAFKA_TOPIC} ]]; then
  JVM_OPTS="${JVM_OPTS} -DcwLogKafkaTopic=${INNER_JVM_LOG_KAFKA_TOPIC}"
fi

######### END
######### 服务目录检查
if [ ! -d $APP_HOME ]; then
  echo "APP_HOME: $APP_HOME, Directory does not exist."
  exit 1
fi
if [ ! -d $APP_LOG_DIR ]; then
  mkdir -p $APP_LOG_DIR
fi
######### END

######### 调试模式
# -l 参数设置脚本上级目录为服务目录, -f 前台启动
opt_original=$@
opts="$(echo $opt_original | tr ' ' '\n' | grep -Ev '\-l|\-f' | tr '\n' ' ')"
if [[ "$@" == *"-l"* ]]; then
  APP_HOME="$(cd ${CURRENT_DIR}/.. &>/dev/null && pwd)"
  APP_LOG_DIR="${APP_HOME}/logs"
  mkdir -p "${APP_HOME}/pid"
  APP_PID_FILE="${APP_HOME}/pid/${APP_NAME}.pid"
  INNER_DATA_DIR="${APP_HOME}/data"
  app_local="-l"
fi
######### END

######### 执行脚本用户判断
user_id=$(id -u)
if [ ${user_id} -eq 0 ]; then
  runUser=$INNER_RUN_USER
fi
######### END

######### 服务状态检查
alive() {
  PID=$(cat $APP_PID_FILE 2>/dev/null)
  PS_PID=$(ps -ef | grep "DcwAppHome=${APP_HOME}" | grep -v grep | awk '{print $2}')
  ps -p $PID &>/dev/null
  RETVAL=$?
  ps -p $PS_PID &>/dev/null
  PS_RETVAL=$?
  PORT_CHECK_CMD="</dev/tcp/${INNER_LOCAL_IP}/${INNER_SERVICE_PORT}"
  if $(echo ${PORT_CHECK_CMD} | bash &>/dev/null); then
    PORT_RETVAL=0
  else
    PORT_RETVAL=1
  fi
  if [[ $port_check -eq 1 ]]; then
    echo $PORT_RETVAL
  elif [ $RETVAL -eq 0 ]; then
    echo 0
  elif [ $PS_RETVAL -eq 0 ]; then
    echo 0
    echo $PS_PID >$APP_PID_FILE
  else
    echo $RETVAL
  fi
}
######### END

######### 服务状态输出
status() {
  if [ $(alive) -eq 0 ]; then
    echo "$APP_NAME [running]" | column -t
  else
    echo "$APP_NAME [not running]" | column -t
  fi
}
#########

######### 达梦国产化数据库相关
#根据url获取默认db连接，供创建数据库用
get_default_db_url() {
  url=$1
  if [[ $url == "jdbc:dm"* ]]; then
    echo $(echo $jdbc_url | sed "s#\${cwDbName}#sysdba#g")
  elif [[ $url == "jdbc:postgresql"* ]]; then
    echo $(echo $jdbc_url | sed "s#\${cwDbName}#postgres#g")
  else
    echo $(echo $jdbc_url | sed "s#\${cwDbName}#${INNER_JVM_SERVICE_DBNAME}#g")
  fi
}

######### END

######### 内部启动命令
start_inside() {
  shift
  foreground=$1
  if [ $(alive) -eq 0 ]; then
    echo "$APP_NAME [running]" | column -t
  elif [[ "$foreground" == "-f" ]]; then
    cd $APP_HOME && exec $JAVACMD $JVM_OPTS -classpath $CW_CLASSPATH $MAIN_CLASS
  else
    cd $APP_HOME
    # 具体的 java 启动命令
    nohup $JAVACMD $JVM_OPTS -classpath $CW_CLASSPATH $MAIN_CLASS 1>/dev/null 2>${APP_LOG_DIR}/start.log &
    echo $! >$APP_PID_FILE
    sleep 1
    status
  fi
}
######### END

######### 启动服务
start() {
  shift
  foreground=$1
  # 判断执行脚本的用户，执行相应逻辑
  if [ ${user_id} -eq 0 ]; then
    id ${runUser} &>/dev/null
    if [ $? -ne 0 ]; then
      useradd ${runUser}
    fi
    chown -R ${runUser}.${runUser} ${APP_HOME} &>/dev/null
    chown -R ${runUser}.${runUser} ${APP_LOG_DIR} &>/dev/null
    chown -R ${runUser}.${runUser} ${INNER_DATA_DIR}/${APP_NAME} &>/dev/null
    su $runUser -c "$THIS_SCRIPT start_inside $foreground"
  elif [[ "$(uname -s)" == "Darwin" ]]; then
    $THIS_SCRIPT start_inside $foreground $app_local
  else
    exec $THIS_SCRIPT start_inside $foreground $app_local
  fi
}
######### END

######### 停止服务
stop() {
  PID=$(cat $APP_PID_FILE 2>/dev/null)
  kill $PID &>/dev/null
  sleep 2
  if [ $(alive) -eq 0 ]; then
    kill -9 $PID &>/dev/null
  fi
  sleep 1
  status
  rm -f $APP_PID_FILE &>/dev/null
}
######### END

liquibase_run() {
  cd ${APP_HOME}
  secret_key=$(nacos_query commons commons.properties passwdSecretKey)
  username=$(nacos_query commons commons.properties panguDBWriteUser encryp@false passwdSecretKey@${secret_key})
  password=$(nacos_query commons commons.properties panguDBWritePwd encryp@true passwdSecretKey@${secret_key})
  jdbc_url=$(nacos_query commons commons.properties panguDBWriteJdbcUrl)
  jdbc_url_default_db=$(get_default_db_url "$jdbc_url")
  jdbc_url=$(echo $jdbc_url | sed "s#\${cwDbName}#${INNER_JVM_SERVICE_DBNAME}#g")
  liquibase_create_db_cmd="$JAVACMD -DcwDbName=${INNER_JVM_SERVICE_DBNAME} -cp \"${APP_HOME}/lib/*\" liquibase.integration.commandline.LiquibaseCommandLine --changeLogFile=conf/dbchangelogs/domestic/createDomesticDB.xml --url=""'""${jdbc_url_default_db}""'"" --username=""'""${username}""'"" --password=""'""${password}""'"""
  liquibase_run_cmd="$JAVACMD -DcwDbName=${INNER_JVM_SERVICE_DBNAME} -cp \"${APP_HOME}/lib/*\" liquibase.integration.commandline.LiquibaseCommandLine --changeLogFile=conf/dbchangelogs/changelog.xml --url=""'""${jdbc_url}""'"" --username=""'""${username}""'"" --password=""'""${password}""'"""
  if [[ $1 == "update" ]]; then
    if [[ $jdbc_url == "jdbc:dm"* ]]; then
      echo "${liquibase_create_db_cmd} update" | bash >>${APP_LOG_DIR}/liquibase_create_db.log 2>&1
      if [[ $? -eq 0 ]]; then
        echo "liquibase create db [successed]" | column -t
      else
        echo "liquibase create db [failed]" | column -t
      fi
    fi
    echo "${liquibase_run_cmd} update" | bash >>${APP_LOG_DIR}/liquibase.log 2>&1
    if [[ $? -eq 0 ]]; then
      echo "liquibase update [successed]" | column -t
    else
      echo "liquibase update [failed]" | column -t
    fi
  elif [[ $1 == "tag" ]]; then
    if [[ -z $2 ]]; then
      echo "db tag tag_name"
    else
      echo "${liquibase_run_cmd} tag $2" | bash | tee -a ${APP_LOG_DIR}/liquibase.log 2>&1
    fi
  elif [[ $1 == "rollback" ]]; then
    if [[ -z $2 ]]; then
      echo "db rollback tag_name"
    else
      echo "${liquibase_run_cmd} rollback $2" | bash | tee -a ${APP_LOG_DIR}/liquibase.log 2>&1

    fi
  fi
}

liquibase_main() {
  shift
  action=$1
  tag_name=$2
  case $action in
  update)
    liquibase_run update
    ;;
  tag)
    liquibase_run tag $tag_name
    ;;
  rollback)
    liquibase_run rollback $tag_name
    ;;
  *) echo "db update| db tag tag_name|db rollback tag_name|db tag_name" ;;
  esac

}
######### END

main() {
  if [[ "$1" == "status" ]]; then
    port_check=1
  fi
  case $1 in
  start)
    start $@
    ;;
  start_inside)
    start_inside $@
    ;;
  stop)
    stop
    ;;
  restart)
    stop
    start
    ;;
  status)
    status
    ;;
  db)
    liquibase_main $@
    ;;
  *) echo "usage: $0 [-l] [start [-f]|start|start_inside|stop|restart|status|db]" ;;
  esac
}
if [[ "$@" == *"-f"* ]]; then
  main $opts -f
else
  main $opts
fi
