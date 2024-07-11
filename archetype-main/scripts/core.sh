#!/bin/bash

# 服务名称，按需修改
SERVICE_NAME="${service-name}"
# 是否升级数据库 True 表示升级， False表示不升级
update_database=True
# 是否初始化配置文件 True 表示升级， False表示不初始化
init_config=True
# 是否更新配置文件 True 表示升级， False表示不更新
update_config=True

#配置文件合并策略，暂时配置文件的合并仅支持properties格式的配置文件
#none 表示使用最新的配置文件
#union 表示新配置文件与老配置文件的并集，相同的配置项使用老配置文件的值
#update 更新新配置文件，相同的配置项使用老配置文件的值
config_merge_strategy="none"

#配置文件地址
nacos_config_path="conf/nacos/"

# 配置文件备份路径
nacos_config_back_name="nacos_back"

db_run(){
    base_dir=$3
    result=0
    if [ -f "$(dirname $3)/bash_profile" ]; then
         source "$(dirname $3)/bash_profile"
    fi
    if [[ "$2" == "init" ]]; then
        bash $base_dir/bin/$SERVICE_NAME db update
        result=$?
    elif [[ "$2" == "update" ]]; then
        if [[ "$update_database" == "True" ]]; then
           bash $base_dir/bin/$SERVICE_NAME db update
           result=$?
        fi
    elif [[ "$2" == "rollback" ]]; then
        echo 0
    else
        exit 1
    fi
    if [ $result -ne 0 ];then
         exit 1
    fi

}

nacos_config_run(){
    base_dir=$3
    base_log_dir=$4
    if [ -f "$(dirname $3)/bash_profile" ]; then
         source "$(dirname $3)/bash_profile"
    fi

    nacos_util_jar=$(ls $base_dir/utils/nacos-util-*-jar-with-dependencies.jar)
    nacos_util_run_cmd="java -DconfigMergeStrategy=$config_merge_strategy -DnacosConfigPath=$base_dir/$nacos_config_path -DnacosConfigBackName=$5/$nacos_config_back_name -DCW_NACOS_SERVER=$6 -DCW_NACOS_USERNAME=$7 -DCW_NACOS_PASSWORD=$8 -DCW_NACOS_NAMESPACE=$9 -jar $nacos_util_jar $2"
    if [[ $2 == "init" ]]; then
        if [[ "$init_config" == "True" ]]; then
            echo "${nacos_util_run_cmd}"  | bash >>${base_log_dir}/nacosUtil.log 2>&1
            if [ $? -ne 0 ]; then
               tail -n 100 ${base_log_dir}/nacosUtil.log
               echo "具体日志请查看${base_log_dir}/nacosUtil.log"
               exit 1
            fi
        fi
    elif [[ "$2" == "update" ]]; then
        if [[ "$update_config" == "True" ]]; then
            echo "${nacos_util_run_cmd}"  | bash >>${base_log_dir}/nacosUtil.log 2>&1
            if [ $? -ne 0 ]; then
               tail -n 100 ${base_log_dir}/nacosUtil.log
               echo "具体日志请查看${base_log_dir}/nacosUtil.log"
               exit 1
            fi
        fi
        if [ $? -ne 0 ];then
            echo 0
        fi
    elif [[ "$2" == "rollback" ]]; then
        if [[ "$update_config" == "True" ]]; then
            echo "${nacos_util_run_cmd}"  | bash >>${base_log_dir}/nacosUtil.log 2>&1
            if [ $? -ne 0 ]; then
               tail -n 100 ${base_log_dir}/nacosUtil.log
               echo "具体日志请查看${base_log_dir}/nacosUtil.log"
               exit 1
            fi
        fi
    else
        exit 1
    fi

}

main() {
    db_run $@
    nacos_config_run $@
}
main $@
