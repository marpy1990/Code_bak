# encoding: UTF-8
import threading
import time
import Block
import ThreadManagement
import VirtualSocket
import Queue
import BlockServer

class MyThread(threading.Thread):
    def run(self):
        print threading.currentThread().ident

def test():
    for i in range(5):
        t = MyThread()
        t.start()
        print t.ident

class base(object):
    def __init__(self):
        super(base, self).__init__()
    
    def add(self, x):
        print x
   
class b2(base):
    """docstring for b2"""
    def __init__(self):
        super(b2, self).__init__()
    
    def add(self, x, y):
        base.add(self, x)
        print x+y

def initlog():
        import logging
       
        logger = logging.getLogger()

        hdlr = logging.FileHandler('sendlog.txt')

        formatter = logging.Formatter('%(threadName)s %(asctime)s %(levelname)s %(message)s')
 
        hdlr.setFormatter(formatter)

        logger.addHandler(hdlr)
 
        logger.setLevel(logging.NOTSET)
        return logger

import sys
class x(object):
    """docstring for x"""
    def __init__(self):
        logging=initlog()
        logging.debug('注册')
    
import inspect

def get_caller_name():
    x = inspect.stack()[-1][4][0]
    for y in inspect.stack():
        print y
    xs = x.split(" ")
    v=""
    for v in xs[::-1]:
        if v!="" and v!="\n":
            break
    vs = v.split(".")
    return vs[0]

class MyClass:
    def function_one(self):
        print get_caller_name()
    
    def fun(self):
        self.function_one()
        return 1

if __name__ == '__main__':
    #myclass = MyClass()
    #a = myclass.fun()  
    a=len("1")
    print a
    #help(classmethod)
    #help(XmlTransport)
    #print threading.currentThread().ident
    #help(threading.Thread)
    #help(BlockServer)
    #import socket
    #help(socket.socket.accept)
    #b = b2()
    #b.add(1,2)
    #b= b2()
    #print type(b).__name__
    """
    class B:
        def __init__(self,b):
            self.i=b
            self.x=1

        def p(self):
            print self.i
    
    class A(object):
        
        def q(self):
            x=B("xx")
            x.p()    
    
    b=B("aaa")
    b.p()
    a= A()
    a.q()
    """
    """
    import Queue
    q=Queue.Queue(maxsize = 0)
    q.put((1,2))
    print q.get()
    q.put((1,(1,2)))
    print q.get()
    
    import socket
    #bhelp(socket)
    sock = socket.socket()
    sock.connect(("www.baidu.com",80))
    print sock.getsockname()
    """


    