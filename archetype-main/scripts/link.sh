#!/bin/bash
#2022-03-11 v1.0.4
# 机器上的公共库目录
COM_LIB_ROOT_PATH="/data/app/comLib"
RED_START="\033[31m"
RED_END="\033[0m"

if [ -n "$1" ]; then
  COM_LIB_ROOT_PATH=$1
fi

# 检查comLib目录是否存在
if [ ! -d "$COM_LIB_ROOT_PATH" ]; then
  echo "$RED_START[error] $COM_LIB_ROOT_PATH does not exist, plz input the right root path$RED_END" >&2
  exit 1
fi

# 脚本当前地址
CURRENT_DIR=$(cd "$(dirname "${BASH_SOURCE[0]}")" &>/dev/null && pwd)
# 当前脚本相应的服务根目录
SERVICE_PATH=$(dirname "$CURRENT_DIR")

# 快照所在目录
SNAP_PATH="$SERVICE_PATH/conf/lib-snap"
# 依赖快照
DEPENDENCY_SNAP_FILE="$SNAP_PATH/dependency.snap"

echo "--- start jar move ---"
# 快照文件不存在，放弃拷贝
if [ ! -f "$DEPENDENCY_SNAP_FILE" ]; then
  echo "[warning] $DEPENDENCY_SNAP_FILE does not exist, quit move process"
  exit 0
fi

# 快照文件内容为空，放弃拷贝
ALL_DEPENDENCIES=$(cat "$DEPENDENCY_SNAP_FILE")
jar_num=$(cat "$DEPENDENCY_SNAP_FILE" | wc -l)
test "$jar_num" -eq 0 && echo "[warning] $DEPENDENCY_SNAP_FILE jar count eq 0, quit move process" && exit 0

# 移动依赖jar到公共库
echo "1、cp dependencies from $COM_LIB_ROOT_PATH to $SERVICE_PATH"
#拷贝jar包
jar_cp_cnt=0
suc_cp_msg=""
for line in $ALL_DEPENDENCIES; do
  tmp_sub_dir=${line%/*}
  # 如果存在软连接，取消软连接
  if [ -L "$SERVICE_PATH/$line" ]; then
    unlink "$SERVICE_PATH/$line"
  fi
  # comLib存在，且服务目录下不存在，则从comLib拷贝到服务目录
  if [ ! -f "$SERVICE_PATH/$line" ] && [ -f "$COM_LIB_ROOT_PATH/$line" ]; then
    test ! -d "$SERVICE_PATH/$tmp_sub_dir" && mkdir -p "$SERVICE_PATH/$tmp_sub_dir"
    cp "$COM_LIB_ROOT_PATH/$line" "$SERVICE_PATH/$line"
    suc_cp_msg=$suc_cp_msg"\n-- $SERVICE_PATH/$line"
    jar_cp_cnt=$(expr $jar_cp_cnt + 1)
  fi
done
# 拷贝提示信息
if [ "$jar_cp_cnt" -ne 0 ]; then
  echo "total "$jar_cp_cnt" files are copied"$suc_cp_msg
else
  echo "-- no files need to be copied from comLib"
fi

# 检查jar是否就绪
echo "2、check files existence"
jar_not_ready_cnt=0
failed_link_msg=""
# 检查依赖快照包中的jar是否能一一访问
for line in $ALL_DEPENDENCIES; do
  #判断当前文件是否存在
  if [ ! -f "$SERVICE_PATH/$line" ]; then
    #如果不存在则输出错误提示
    failed_link_msg=$failed_link_msg"\n-- $line does not exist in path [$COM_LIB_ROOT_PATH] or [$SERVICE_PATH], and plz check"
    jar_not_ready_cnt=$(expr $jar_not_ready_cnt + 1)
  fi
done

# 能访问的依赖，跟依赖快照文件数不一致，视为失败
if [ "$jar_not_ready_cnt" -ne 0 ]; then
  echo "$RED_START--- failed to  access ---" >&2
  echo "[error] "$jar_not_ready_cnt "of" $jar_num "dependencies jar missed"$failed_link_msg >&2
  echo "---------end--------------$RED_END" >&2
  exit 1
fi

echo "3、all of "$jar_num "dependencies jar access successfully"
echo "---------end--------------"
exit 0
