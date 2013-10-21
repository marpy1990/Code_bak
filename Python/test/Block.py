# -*- coding: UTF-8 -*-
"""用于管理一定资源并实现某些功能的一种模型

模块是占有资源的最小单位，对模块资源的访问只能采用申请应答机制。
在这种架构体系下，一种资源只能被一个模块唯一占有，全局资源是不被允许的。

Basic usage::

    >>> block = Block(name)
"""
__docformat__ = "restructuredtext en"

__all__ = [
    "BlockNameError",
    "Block"
]

__author__ = "marpy"
__version__ = "1.0.0"
__date__ = "$Date: 2013-10-13$"

# exceptions
class BlockNameError(Exception):
    """模块名字已经被注册过"""
    def __init__(self, name):
        Exception.__init__(self, "Block name \""+name+"\" has been used")

    def __repr__(self):
        return "Block name \""+name+"\" has been used"

# classes
class Block(object):
    """占有资源的最小单位"""

    _block_name_table = { }    #模块名称表，模块名称在同进程中是唯一的

    def __init__(self, name):
        super(Block, self).__init__()
        if name in Block._block_name_table:
            raise BlockNameError(name)
        Block._block_name_table[name] = self
        self.name = name      #实例化后的名字

if __name__ == "__main__":
    b1 = Block("test1")
    b2 = Block("test2")
    b3 = Block("test1")
    print Block._block_name_table
    #help(Block)