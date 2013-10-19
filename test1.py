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
        
if __name__ == '__main__':
    #help(classmethod)
    #help(XmlTransport)
    #print threading.currentThread().ident
    #help(ThreadManagement)
    help(BlockServer)
    #import socket
    #help(socket.socket.accept)
    #b = b2()
    #b.add(1,2)
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
    



    