# -*- coding: UTF-8 -*-
import socket
sock = socket.socket(socket.AF_INET, socket.SOCK_STREAM)
sock.bind(("",8002))
sock.listen(-1)
while True:
    con,addr = sock.accept()
    for i in range(0,10):
        buf = con.recv(1024)
        print "address: "+repr(addr)+" "+buf
con.close()