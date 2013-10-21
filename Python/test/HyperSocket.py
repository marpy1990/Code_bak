import Block
import ThreadManagement
import socket
import Queue

class HyperSocket(ThreadManagement.ThreadManagement, socket.socket):
    """docstring for HyperSocket"""
    
    __LOCAL = "LOCAL"
    __SOCKET = "SOCKET"
    __BOTH  = "BOTH"

    def __init__(self, name):
        ThreadManagement.ThreadManagement(name)
        socket.socket(socket.AF_INET, socket.SOCK_STREAM)
        self.__target = None
        self.__listen_type = HyperSocket.__LOCAL
        self.__connect_type = None
        self.accept_queue = Queue.Queue(maxsize = 0)

    def connect(self, name = None, address = None):
        if name != None:
            self.__connect_type = HyperSocket.__LOCAL
            self.__target = Block.find(name)
        else:
            self.__connect_type = HyperSocket.__SOCKET
            socket.socket.connect(address)

    def send(self, text):
        if self.__connect_type == HyperSocket.__SOCKET:
            socket.socket.send(text)
        else:
            pass

    def bind(self, address):
        self.__listen_type = HyperSocket.__BOTH
        socket.socket.bind(self, address)

    def listen(self, backlog = 65535):
        socket.socket.listen(backlog)
    
    @ThreadManagement.new_thread
    def __socket_listen(self):
        while True:
            ,addr = sock.accept()

    def accept(self):
