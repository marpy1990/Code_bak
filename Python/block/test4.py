# -*- coding: UTF-8 -*-
import socket
sock = socket.socket(socket.AF_INET, socket.SOCK_STREAM)
sock.bind(("",8002))
sock.connect(("localhost",8001))
print "44444"