# -*- coding: UTF-8 -*-
import socket
sock = socket.socket(socket.AF_INET, socket.SOCK_STREAM)
sock.bind(("",8001))
print repr(socket.gethostbyname("www.baidu.com"))
print repr(socket.getfqdn("119.75.217.56"))
sock.listen(-1)
while True:
    con,addr = sock.accept()
    for i in range(0,10):
        buf = con.recv(1024)
        print "address: "+repr(addr)+" "+buf
con.close()
    