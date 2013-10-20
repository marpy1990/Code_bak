# -*- coding: UTF-8 -*-
import socket
import time
sock = socket.socket(socket.AF_INET, socket.SOCK_STREAM)
sock.bind(("",8001))
sock.listen(-1)
while True:
    con,addr = sock.accept()
    time.sleep(100)
    """
    for i in range(0,10):
        buf = con.recv(1024)
        print "address: "+repr(addr)+" "+buf
    """
con.close()
    