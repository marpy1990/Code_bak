# -*- coding: UTF-8 -*-
if __name__ == '__main__':
	import socket
	sock = socket.socket(socket.AF_INET, socket.SOCK_STREAM)
	sock.bind(("",2000))
	sock.connect(('www.baidu.com', 80))
	for i in range(0,5):
		sock.send(str(i))
	sock.close()
	import time
	time.sleep(2)
	i=0
	while True:
		print i
		i = i+1
		sock.send('1')
		time.sleep(1)
	#sock.close()
	time.sleep(10)