# -*- coding: UTF-8 -*-
if __name__ == '__main__':
	import socket
	sock = socket.socket(socket.AF_INET, socket.SOCK_STREAM)
	sock.connect(('localhost', 8001))
	sock.send("asd")