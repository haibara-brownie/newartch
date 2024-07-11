#!/usr/bin/env python
# -*- coding:utf8 -*-

import sys

from Core import Core

SERVICE_NAME = "${service-name}"

class UpgradeService(Core):
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
        暂未开放升级
        """
        print(f"暂未支持使用OMP升级,请点击回滚还原并重新安装该版本!")
        sys.exit(1)


if __name__ == '__main__':
    _ = UpgradeService()
    _.run()
