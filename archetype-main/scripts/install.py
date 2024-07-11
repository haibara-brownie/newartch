#!/usr/bin/env python
# -*- coding: utf-8 -*-


import os
import sys

from Core import Core

# 服务名称 根据自己包需求修改
SERVICE_NAME = "${service-name}"


class InstallService(Core):
    def __init__(self):
        """
        初始化json数据 及 基础方法
        """
        Core.__init__(self)
        self.SERVICE_NAME = SERVICE_NAME
        self.para = self.parameters()  # 脚本接收到的参数
        self.format_para(self.para)  # 解析脚本接收到的参数， 并初始化参数

    def run(self):
        """
        安装执行过程
        """
        self.out('\n *** ${service-name}安装进度 *** \n')

        # 服务相关路径
        scripts_path = os.path.join(
            self.install_args.get("base_dir"), 'bin/{0}'.format(SERVICE_NAME)
        )

        self.out("1 {} 开始安装")
        # 创建相关文件夹
        self.check_dir()

        # 添加执行权限，创建软连接
        self.create_user_and_change_owner()

        # 从公共库拷贝jar 开始 ----
        # 拼接脚本路径
        common_link_script = os.path.join(self.install_args.get("base_dir"), "{}/{}".format("scripts", "link.sh"))
        # 添加执行权限
        self.sys_cmd('chmod +x {}'.format(common_link_script))
        # 从公共库拷贝jar,并检查快照中的文件是否均可访问，如发生异常则直接退出
        self.sys_cmd('sh {} {}'.format(common_link_script, self.pub_para_install("base_dir", "comLib")),
                     ignore_exception=False)
        # 从公共库拷贝jar逻辑 结束 ----

        # nacos
        cw_nacos_host = self.pub_ip_port_str('nacos', 'service_port')
        cw_nacos_username = self.pub_para_install('username', 'nacos')
        cw_nacos_password = self.pub_para_install('password_enc', 'nacos')
        cw_nacos_namespace = self.pub_para_install('namespace', 'nacos')

        # set start script
        place_holder_script = {
            "CW_RUN_USER": self.install_args.get("run_user"),
            "CW_LOCAL_IP": self.local_ip,
            "CW_JVM_HEAP_SIZE": self.install_args.get("memory"),
            "CW_INSTALL_APP_DIR": os.path.dirname(self.install_args.get("base_dir")),
            "CW_INSTALL_LOGS_DIR": os.path.dirname(self.install_args.get("log_dir")),
            "CW_INSTALL_DATA_DIR": os.path.dirname(self.install_args.get("data_dir")),
            "CW_NACOS_SERVER": cw_nacos_host,
            "CW_NACOS_USERNAME": cw_nacos_username,
            "CW_NACOS_PASSWORD": cw_nacos_password,
            "CW_NACOS_NAMESPACE": cw_nacos_namespace,
            "CW_SERVICE_DBNAME": self.install_args.get("dbname"),
            "CW_SERVICE_PORT": str(self.port.get("service_port")),
            "CW_METRICS_PORT": str(self.port.get("metrics_port")),
            "CW_SERVICE_DUBBO_PORT": str(self.port.get("dubbo_port")),
            "CW_LOG_TOPIC": self.install_args.get("kafka_topic")
        }
        result = self.check_dict_none(place_holder_script)
        if not result:
            self.out("2 有占位符变量值为空")
            sys.exit(1)
        self.out("2 {} 配置：修改bin/${service-name}文件配置")
        self.replace(scripts_path, place_holder_script)
        self.sys_cmd("chmod a+x {0}".format(scripts_path))


if __name__ == '__main__':
    _ = InstallService()
    _.run()
