# encoding: UTF-8
import threadpool
import threading
import time
import Block

class MyThread(threading.Thread):
    a=0
    def __init__(self):
    	MyThread.a=MyThread.a+1
    def run(self):
        for i in range(3):
            time.sleep(1)
            
            print self
def test():
    for i in range(5):
        t = MyThread()
        t.setName(str(MyThread.a))
        t.start()

if __name__ == '__main__':
    #d={"aaa":1}
    #d["aaa"]=2
    #print d
    #help(threading.Thread)
    #help(Exception)
    help(Block)
    #help(threadpool)
