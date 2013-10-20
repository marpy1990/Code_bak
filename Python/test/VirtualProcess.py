# -*- coding: UTF-8 -*-
""" 虚拟进程模型，拥有线程通信的模块（Block）模型。

继承自ThreadManagement以及XmlTransport模型。
VirtualProcess本身相当于一个黑匣子，可以单独处理一些事件，通过内存传递线程间消息。
通信协议采用XML(Extensible Markup Language)
其中根结点下属的<from> <to> <send_date><topic>属于保留标签

Basic usage::

    >>> vp = VirtualProcess(name)
    >>> vp.start()
    >>> vp.join()
"""
__docformat__ = "restructuredtext en"

__all__ = [
    "VirtualProcess",
]

__author__ = "marpy"
__version__ = "1.0.0"
__date__ = "$Date: 2013-10-15$"

# standard library modules
import ThreadManagement
import threading
import xml.dom.minidom
import functools
import time

# functions


# classes
class VirtualProcess(ThreadManagement.ThreadManagement):
    """提供完整的本地线程通信方法"""

    def __init__(self, name):
        super(VirtualProcess, self).__init__(name)

    @new_thread
    def start(self):
        """开始监听消息"""
        print self.thread_table
        time.sleep(3)

    @ThreadManagement.auto_register
    def run(self):
        print self.thread_table
        time.sleep(1)


if __name__ == "__main__":
    vp=VirtualProcess("aaa")
    print vp.thread_table
    vp.start()
    time.sleep(1)
    vp.run()
    print vp.thread_table
    time.sleep(3)
    print vp.thread_table
    time.sleep(10)

