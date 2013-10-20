# -*- coding: UTF-8 -*-
"""拥有通信功能的模块（ThreadManagement）模型

继承自Block模型。
实现了同一进程的不同模块间的通信机制，包括与自身的通信。

通信协议采用XML(Extensible Markup Language)
其中根结点下属的<from> <to> <send_date>属于保留标签，其余可自行定义

Basic usage::

    >>> block1 = Transport(name1)
    >>> block2 = Transport(name2)
    >>> block1.connect(name2)
    >>> block1.send(xml_text)
    >>> block1.disconnect()
    >>> block2.start()
"""
__docformat__ = "restructuredtext en"

__all__ = [
    "Transport",
    "append_xml",
    "LOCAL_TYPE"
]

__author__ = "marpy"
__version__ = "1.0.0"
__date__ = "$Date: 2013-10-16$"

# standard library modules
import Block
import threading
import xml.dom.minidom
import time
import Queue
from ThreadManagement import *

# exceptions
class CannotFindBlockError(Exception):
    """找不到发送目标"""
    def __init__(self, name):
        Exception.__init__(self, "找不到名为 "+name+" 的模块")
class CannotFindConnectionError(Exception):
    """找不到最近的连接"""
    def __init__(self, thread_info):
        Exception.__init__(self, "线程信息 "+repr(thread_info)+" 中找不到最近的连接")
# classes
class Transport(ThreadManagement):
    """提供通信功能的Block模块"""
    def __init__(self, name):
        super(Transport, self).__init__(name)
        self.__message_queue = Queue.Queue(maxsize = 0)     #消息处理队列长度无限
    
    @new_thread
    def listen(self):
        """开启一个线程接收消息"""
        while True:
            xml_text = self.__message_queue.get()
            self._handle(xml_text)

    def _handle(self, xml_text):
        """消息处理，当有消息传入时触发，供子类继承"""
        print xml_text
        time.sleep(1)

    def connect(self, block_name):
        """连接名为block_name的目标模块，支持嵌套连接"""
        if not block_name in Block.Block.block_name_table:
            raise CannotFindBlockError(block_name)
        thread_info = self.current_thread_info()
        if not "connect_list" in thread_info:
            thread_info["connect_list"] = []
        thread_info["connect_list"].insert(0, {"type": LOCAL_TYPE, "target": Block.Block.block_name_table[block_name]})

    def disconnect(self):
        """断开最近的连接"""
        thread_info = self.current_thread_info()
        try:
            del thread_info["connect_list"][0]
        except:
            raise CannotFindConnectionError(thread_info)

    def send(self, xml_text):
        """向最近一次连接的模块发送消息，传输协议为XML"""
        thread_info = self.current_thread_info()
        try:
            target = thread_info["connect_list"][0]["target"]
        except:
            raise CannotFindConnectionError(thread_info)
        xml_text = self.__add_stamp(target.name, xml_text)
        target.__message_queue.put(xml_text)

    def __add_stamp(self, to_name, xml_text):
        """向xml中添加戳"""
        doc = xml.dom.minidom.parseString(xml_text)
        date = time.strftime("%Y-%m-%d-%H-%M-%S",time.localtime(time.time())) 
        node = {
                "form": 
                    {
                        "type":LOCAL_TYPE, 
                        "block": self, 
                        "thread": threading.currentThread()
                    }, 
                "to": 
                    {
                        "block_name": to_name
                    }, 
                "send_date": 
                    date
                }
        append_xml(doc,node)
        return doc.toprettyxml()

# globals
LOCAL_TYPE = "LOCAL_TYPE"

# functions
def append_xml(doc, node):
    """快速向xml中添加结点"""
    root = doc.documentElement
    __append_xml(doc, root, node)

def __append_xml(doc, root, node):
    if isinstance(node, basestring):
        text = doc.createTextNode(node)
        root.appendChild(text)
    else:
        for item_name in node.keys():
            item = doc.createElement(item_name)
            __append_xml(doc, item, node[item_name])
            root.appendChild(item)

if __name__ == "__main__":
        x1=Transport("x1")
        x2=Transport("x2")
        x3=Transport("x3")
        x1.listen()
        x2.listen()
        x3.listen()
        """
        @new_thread
        def test(self, name, xml_text):
            self.connect(name)
            self.send(xml_text)
            self.disconnect()
        test(x2, "x1", "<message><data>i am x2</data></message>")
        test(x3, "x1", "<message><data>i am x3</data></message>")
        time.sleep(10)
        """
        @new_thread
        def test1(self):
            self.connect("x2")
            self.send("<message><data>1-2</data></message>")
            self.connect("x3")
            self.send("<message><data>1-3</data></message>")
            self.disconnect()
            self.send("<message><data>new1-2</data></message>")
            self.disconnect()
        test1(x1)
        time.sleep(10)

        

