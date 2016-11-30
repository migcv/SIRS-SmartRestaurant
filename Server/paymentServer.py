import socket
import sys
import random
import string
import ssl
import time
import threading
import os


def receiveRandomClientIDValueToPay():
    port = 10004
    servicename = "receiveRandomClientIDValueToPay"
    serversocket = socket.socket(socket.AF_INET, socket.SOCK_STREAM)
    serversocket.bind((hostname, port))
    print("<{}>: Port: {}".format(servicename, port))

    serversocket.listen(5)
    print("<{}>:Listenning".format(servicename))
    while 1:
        (clientsocket, address) = serversocket.accept()
        print("\n<{}>:Got a connection from <{}>".format(servicename, address))

        data = clientsocket.recv(1024)
        aux = data.split(" : ")
        randomIDValueToPay.update({aux[0] : float(aux[1])})
        
        print("\n<{}>:Received randomID and ValueToPay <{}>".format(servicename, randomIDValueToPay))
        
        clientsocket.close()
        
# END of receiveRandomClientIDValueToPay()

def receiveRandomID():
    port = 10005
    servicename = "receiveRandomID"
    serversocket = socket.socket(socket.AF_INET, socket.SOCK_STREAM)
    serversocket.bind((hostname, port))
    print("<{}>: Port: {}".format(servicename, port))

    serversocket.listen(5)
    print("<{}>:Listenning".format(servicename))
    while 1:
        (clientsocket, address) = serversocket.accept()
        print("\n<{}>:Got a connection from <{}>".format(servicename, address))

        data = clientsocket.recv(2048)
        
        print("\n<{}>:Received randomID <{}>".format(servicename, data))
        
        randomID = data
        if(randomIDValueToPay.get(randomID, 'empty') != 'empty'):
            clientsocket.send(randomIDValueToPay.get(randomID, 'empty'))
            print("\n<{}>:Send value to pay <{}>".format(servicename, randomIDValueToPay.get(randomID, 'empty')))
            
        else:
            print("\n<{}>: Random ID not found".format(servicename))
            
        clientsocket.close()				
# END of receiveOrderSocket() 


class myThread (threading.Thread):
    def __init__(self, threadID, name, counter):
        threading.Thread.__init__(self)
        self.threadID = threadID
        self.name = name
        self.counter = counter
    def stop(self):
        self._stop.set()	
    def run(self):
        print("Starting " + self.name)
        if(self.name=="receiveRandomID"):
            receiveRandomID()
        print("Exiting " + self.name)
# END of myThread

# MAIN PROGRAM -----------------------------------------------------------------------
randomID = [] 		# randomID
randomIDValueToPay = {} # {randomID : ValueToPay}


hostname = ''

BUFFER = 1024

print("Payment Server")

try:
    thread1 = myThread(1, "receiveRandomID", 1)
    thread2 = myThread(1, "receiveRandomClientIDValueToPay", 1)
    thread1.start()
    thread2.start()
    input()
    print("Closing!")
    os._exit(1)
except: