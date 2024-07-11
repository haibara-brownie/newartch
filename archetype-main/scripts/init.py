#!/usr/bin/env python
# -*- coding: utf-8 -*-

import sys

from Core import Core

SERVICE_NAME = '${service-name}'


class InitService(Core):
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
        self.out('\n *** ${service-name}初始化进度 *** \n')
        self.out("1 {} 开始初始化")
        cw_install_app_dir = self.install_args.get("base_dir")
        cw_run_user = self.install_args.get("run_user")
        server_addresses = self.pub_ip_port_str('nacos', 'service_port')
        server_list = list()
        for server_addr in server_addresses.split(","):
            server_list.append(server_addr.strip())
        cmd_str = 'bash {}/{}  {} {} {} {} {} {} {} {} {}'.format(
            cw_install_app_dir, "scripts/core.sh", cw_run_user, "init", cw_install_app_dir,
            self.install_args.get("log_dir"), self.para.backup_path
            , server_list[0], self.pub_para_install('username', 'nacos'),
            self.pub_para_install('password_enc', 'nacos'), self.pub_para_install('namespace', 'nacos'))
        print(cmd_str)
        result = self.sys_cmd(cmd_str, ignore_exception=False)

        if result is not None:
            if "0" in result:
                sys.exit(0)
            else:
                print("初始化失败")
                sys.exit(1)


if __name__ == '__main__':
    _ = InitService()
    _.run()
