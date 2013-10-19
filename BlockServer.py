# -*- coding: UTF-8 -*-
"""支持通信的模块（Block）模型，支持与外部网络的通信，支持与其他模块的通信

继承自ThreadManagement模型。
实现了同一线程中不同VIrtualSite之间的通信。
实现了VirtuaSite与外部端口的socket通信。
可以通过继承，实现模块化的完整功能

Basic usage::

    >>> site = BlockServer("site1")
    >>> site.start()
    >>> site.wait()
"""
__docformat__ = "restructuredtext en"

__all__ = [
    "BlockServer"
]

__author__ = "marpy"
__version__ = "1.0.0"
__date__ = "$Date: 2013-10-17$"

# standard library modules
import socket
import threading
import Queue
import time

# user library modules
import Block
from ThreadManagement import*
from VirtualSocket import*

# exceptions
class SocketBindError(Exception):
    """端口绑定错误"""
    def __init__(self, address):
        Exception.__init__(self, "端口 "+repr(address)+" 无法绑定或已经被绑定过")

class CannotFindCurrentSocketError(Exception):
    """名称表中找不到对应VirtualSocket"""
    def __init__(self, thread_info):
        Exception.__init__(self, "线程信息表 "+repr(thread_info)+" 中找不到当前连接")

# classes        
class BlockServer(ThreadManagement):
    """虚拟站点模型，支持网络通信"""
    
    __LOCAL = 1     #连接模式：本地内存
    __REMOTE = 2    #连接模式：网络端口

    __MAX_LISTEN = 65535    #最大监听数

    def __init__(self, name, address = None):
        """
        构造函数，确定模块名和监听的网络端口
        name: 模块名
        address: 绑定的网络地址，形式为(ip, port)，可缺省，缺省时不会监听网络端口
        """
        super(BlockServer, self).__init__(name)
        self.__is_start = False
        self.__wait_sem = None
        self.__is_handle = False
        self.__trans_type = BlockServer.__LOCAL
        self.__address_bind = None
        self.__local_socket = VirtualSocket(name)
        self.__remote_socket = socket.socket()
        self.__socket_queue = Queue.Queue(maxsize = 0)
        self.__local_socket.listen()
        if not address == None:
            self.bind(address)

    def bind(self, address):
        """
        绑定网络端口，开启与外部网络的通信功能
        address: 网络地址，形式为(ip, port)
        """
        self.__trans_type = self.__trans_type|BlockServer.__REMOTE
        try:
            self.__remote_socket.bind(address)
        except:
            raise SocketBindError(address)
        self.__address_bind = address
        self.__remote_socket.listen(BlockServer.__MAX_LISTEN)

    def address_bind(self):
        """返回绑定的网络地址(ip, port)"""
        return self.__address_bind

    def start(self):
        """
        开启模块功能，BlockServer在start前不会工作
        启动 main_thread 线程
        启动 start_accept 线程接收外部的消息
        启动 handle_message 线程处理外部的消息
        """
        if self.__is_start:
            return
        self.__is_start = True
        self.__wait_sem = threading.Semaphore(0)
        main = new_thread(self.main_thread)
        main(self)
        self.start_accept()
        self.start_handle_message()

    def start_handle_message(self):
        """启动消息处理线程，该方法会随start()方法自动调用"""
        if self.__is_handle == True:
            return
        self.__is_handle = True
        self.__handle_socket()

    def stop_handle_message(self):
        """
        停止消息处理线程
        调用该方法后，外部仍能连接本模块，但不会触发handle_message方法
        """
        self.__is_handle = False

    def start_accept(self):
        """启动消息接收线程，该方法会随start()方法自动调用"""
        if self.__trans_type & BlockServer.__LOCAL == BlockServer.__LOCAL:
            self.__local_start_accept()
        if self.__trans_type & BlockServer.__REMOTE == BlockServer.__REMOTE:
            self.__remote_start_accept()

    def stop_accept(self):
        """
        停止消息接收线程
        调用该方法后，外部尝试连接本模块会触发异常
        """
        if self.__trans_type & BlockServer.__LOCAL == BlockServer.__LOCAL:
            self.__local_socket.close()
        if self.__trans_type & BlockServer.__REMOTE == BlockServer.__REMOTE:
            self.__remote_socket.close()

    def stop(self):
        """停止消息接收和处理，并解除主线程的等待"""
        if not self.__is_start:
            return
        self.stop_accept()
        self.stop_handle_message()
        self.__wait_sem.release()

    def wait(self, timeout = 0):
        """
        主线程等待模块运行timeout秒
        timeout: 主线程等待的时间，若timeout<=0，则等待无限长时间。默认timeout为0
        """
        if not self.__is_start:
            return
        if timeout > 0:
            self.__wait_time(timeout)
        self.__wait_sem.acquire()

    @new_thread
    def __wait_time(self, timeout):
        """等待timeout时间后解除主线程的等待"""
        time.sleep(timeout)
        self.__wait_sem.release()

    def main_thread(self, *args, **kwargs):
        """
        在start()方法被调用时触发的线程，供用户继承
        注意在main_thread线程结束后，模块并不会停止工作
        """
        pass

    @new_thread
    def __local_start_accept(self):
        """开启内存消息接收功能"""
        try:
            while True:
                msg_socket, name = self.__local_socket.accept()
                _socket = BlockServer.__socket(msg_socket)
                self.__socket_queue.put(_socket)
        except:
            pass

    @new_thread
    def __remote_start_accept(self):
        """开启网络消息接收功能"""
        try:
            while True:
                msg_socket, address = self.__remote_socket.accept()
                _socket = BlockServer.__socket(msg_socket)
                self.__socket_queue.put(_socket)
        except:
            pass

    @new_thread
    def __handle_socket(self):
        """从链接槽中提取外部的链接"""
        while self.__is_handle:
            msg_socket = self.__socket_queue.get()
            self.__thread_handle_socket(msg_socket)

    @new_thread
    def __thread_handle_socket(self, msg_socket):
        """处理外部链接为msg_socket带来的消息"""
        thread_info = self.current_thread_info()
        if not "connection" in thread_info:
            thread_info["connection"] = []
        thread_info["connection"].insert(0, {"socket": msg_socket})
        msg = msg_socket.recv()
        
        self.handle_message(msg)

    def handle_message(self, message):
        """
        消息处理函数，处理外部传入的消息，供用户继承
        message: 消息文本
        """
        #The code below is just for test!!!
        print message
        if self.name == "a":
            self.connect("b")
            self.send(self.name+" send to b")
            print self.thread_table
            self.disconnect()
        
            self.send("recv")
            text2=self.recv()
            print text2

    def connect(self, name = None, address = None):
        """
        建立与外部的连接
        若address不为None，则与地址为address的外部网络建立连接
        若address为None，则与名为name的模块建立连接
        建立连接后可以使用send和recv方法进行通信

        允许嵌套连接，例如在如下用例中
            >>> cls.connect("block1")
            >>> cls.send("text1")
            >>> cls.connect("block2")
            >>> cls.send("text2")
            >>> cls.disconnect()
            >>> cls.send("text3")
        block1会收到text1和text3，而block2会收到text2
        """
        thread_info = self.current_thread_info()
        _socket = None
        connect_socket = None
        if address == None:
            connect_socket = VirtualSocket()
            connect_socket.connect(name)
        else:
            connect_socket = socket.socket()
            connect_socket.connect(address)
        _socket = BlockServer.__socket(connect_socket)
        if not "connection" in thread_info:
            thread_info["connection"] = []
        thread_info["connection"].insert(0, {"socket": _socket})

    def disconnect(self):
        """
        解除当期线程的最近一次的连接，用法见connect方法
        返回self本身，故支持连续解除连接的写法,例如:
            >>> self.disconnect().disconnect()
        """
        thread_info = self.current_thread_info()
        current_socket = self.get_current_socket()
        current_socket.disconnect()
        del thread_info["connection"][0]
        return self

    def disconnect_all(self):
        """解除当前线程的所有连接"""
        thread_info = self.current_thread_info()
        if "connection" in thread_info:
            connect_socket_list = thread_info["connection"]
            while not len(connect_socket_list) == 0:
                current_socket = connect_socket_list[0]["socket"]
                current_socket.disconnect()
                del connect_socket_list[0]

    def get_current_socket(self):
        """
        获得当前线程的连接
        必须是在已经和外部模块建立连接的条件下
        返回一个连接套接字socket，可以通过其与外部模块通信，用例如下：
            >>> sock = self.get_current_socket()
            >>> sock.send(text)     #通过连接发送消息
            >>> msg = sock.recv()   #通过连接接收消息
            >>> sock.disconnect()   #断开连接
        通常用于需要新建一个子线程处理某一个长连接的情况，新建的子线程可以持有sock进行长期的通信
        """
        thread_info = self.current_thread_info()
        try:
            current_socket = thread_info["connection"][0]["socket"]
        except:
            raise CannotFindCurrentSocketError(thread_info)
        return current_socket

    def send(self, text):
        """
        发送消息，必须是在已经和外部模块建立连接的条件下
        发送目标为该线程最近一次建立的连接，具体用例见connect
        返回self本身，故支持连续发送的写法，例如：
            >>> self.send(text1).send(text2)
        """
        current_socket = self.get_current_socket()
        current_socket.send(text)
        return self

    def recv(self):
        """
        接收消息，必须是在已经和外部模块建立连接的条件下
        接收目标为该线程最近一次建立的连接
        返回接收到的text
        """
        current_socket = self.get_current_socket()
        return current_socket.recv()

    class __socket(object):
        """用于维护连接的套接字，对外隐藏"""
        def __init__(self, _socket):
            """构造函数，确定嵌套字的内核"""
            self.__socket = _socket

        def send(self, text):
            """发送消息"""
            self.__socket.send(text)
            return self

        def recv(self, *arg, **kwargs):
            """接收消息"""
            return self.__socket.recv(65535)

        def disconnect(self):
            """断开连接"""
            self.__socket.close()

if __name__ == '__main__':
    import time
    v=BlockServer("a")
    v.bind(("0.0.0.0",4000))
    v.start()

    v2=BlockServer("b")
    v2.start()
    
    s=socket.socket()
    s.connect(("127.0.0.1",4000))
    s.send("hello world")
    time.sleep(1)
    text = s.recv(1000)
    print text
    print v.thread_table
    s.send("new msg")
    v.wait(5)