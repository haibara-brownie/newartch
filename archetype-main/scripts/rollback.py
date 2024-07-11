#!/usr/bin/env python
# -*- coding:utf8 -*-

import os

from Core import Core

SERVICE_NAME = "${service-name}"


class RollBackService(Core):
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
        直接还原目录
        """
        # 服务部署路径
        app_path = os.path.join(os.path.dirname(self.install_args.get("base_dir")), SERVICE_NAME)
        self.out(f"服务部署路径:{app_path},备份目录:{self.para.backup_path}")
        self.sys_cmd(f"rm -rf {app_path}/*")
        self.sys_cmd(f"cp -af {self.para.backup_path}/* {app_path}/")


if __name__ == '__main__':
    _ = RollBackService()
    _.run()
