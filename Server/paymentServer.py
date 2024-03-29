import socket
import sys
import random
import string
import ssl
import time
import threading
import os
import hashlib
import time

from datetime import date

from base64 import b64decode 
from Crypto.PublicKey import RSA
from Crypto.Hash import SHA256
from Crypto.Signature import PKCS1_v1_5

def connectionServerPayment():
    port = 10002
    servicename = "connectionServerPayment"

    serversocket = socket.socket(socket.AF_INET, socket.SOCK_STREAM)
    serversocket.bind((hostname, port))
    print("<{}>:Port {}".format(servicename, port))

    serversocket.listen(5)
    print("<{}>:Listenning".format(servicename))
    while 1:
        (clientsocket, address) = serversocket.accept()
        print("\n<{}>:Got a connection from <{}>".format(servicename, address))

        secureServerPayment = ssl.wrap_socket(clientsocket,
                                              server_side=True,
                                              certfile="pay_dal/pay_dal.crt",
                                              keyfile="pay_dal/pay_dal.key",
                                              ssl_version=ssl.PROTOCOL_TLSv1_2)

        service = secureServerPayment.recv(32).decode("utf-8")
        print("Service Requested: <{}>".format(service))
        if(service == "sendClientIDValueToPay"):
            receiveRandomClientIDValueToPay(secureServerPayment)

        secureServerPayment.close()
        clientsocket.close()

# END of connectionServerPayment()

def connectionClientPayment():
    port = 10003
    servicename = "connectionClientPayment"

    serversocket = socket.socket(socket.AF_INET, socket.SOCK_STREAM)
    serversocket.bind((hostname, port))
    print("<{}>:Port {}".format(servicename, port))

    serversocket.listen(5)
    print("<{}>:Listenning".format(servicename))
    while 1:
        (clientsocket, address) = serversocket.accept()
        print("\n<{}>:Got a connection from <{}>".format(servicename, address))

        secureClientPayment = ssl.wrap_socket(clientsocket,
                                              server_side=True,
                                              certfile="pay_dal/pay_dal.crt",
                                              keyfile="pay_dal/pay_dal.key",
                                              ssl_version=ssl.PROTOCOL_TLSv1_2)

        service = secureClientPayment.recv(32).decode("utf-8")
        print("Service Requested: <{}>".format(service))
        if(service == "RandomID"):
            receiveRandomID(secureClientPayment)

        secureClientPayment.close()
        clientsocket.close()

# END of connectionServerPayment()

def receiveRandomClientIDValueToPay(secureServerPayment): # Received from the restaurant the randomID and the value to pay
   
    servicename = "sendClientIDValueToPay"
    data = secureServerPayment.recv(1024).decode("utf-8")
    aux = data.split(" : ")
    randomIDValueToPay.update({aux[0] : float(aux[1])})
    
    print("<{}>:Received randomID and ValueToPay <{}>".format(servicename, randomIDValueToPay))
    
    #Verify digital signature
    
    pub = RSA.importKey(open('restaurant/pub.pem').read())
    # aux2 = randomID : valueToPay
    aux2 = aux[0] + " : " + aux[1]
    hashData= SHA256.new(str.encode(aux2)).digest()
    # aux[2] - digital signature
    signature = aux[2]
    a = signature.replace("(", "")
    b = a.replace(")", "")
    c = b.replace(",", "")
    d = int(c)
    e = (d,)
    #print(int(signature))
    if(pub.verify(hashData, e)):
        print("Signature is OK!")
    else:
        print("Wrong Signature....")
    
        
# END of receiveRandomClientIDValueToPay()

def receiveRandomID(secureClientPayment): # Received random id and card info from the Customer
    servicename = "RandomID"
    
    data = secureClientPayment.recv(2048).decode("utf-8")
    
    print("\n<{}>:Received randomID <{}>".format(servicename, data))
    
    aux = data.split(" : ")
    randomID = aux[0]
    if(randomIDValueToPay.get(randomID, 'empty') != 'empty'):
        valueToPay = randomIDValueToPay.get(randomID, 'empty')
        
        hash = hashlib.sha256(str.encode(str(valueToPay))).hexdigest()
        
        #If the signature worked with android java
    
        #priv = RSA.importKey(open('pay_dal/pay_dal_priv.pem').read())
        
        
        #signer = PKCS1_v1_5.new(priv)
        #signature = signer.sign(hahs)
        
        secureClientPayment.send(str.encode(hash))
        print("\n<{}>:Send value to pay digest <{}>".format(servicename, str.encode(hash)))
              
        
    else:
        print("\n<{}>: Random ID not found".format(servicename))
    
    cardNumber = aux[1]
    date1 = aux[2].split("/")
    csc = aux[3]
    now = date.today()
    year1 = "20" + date1[1]
    try:
        cardDate = date(int(year1), int(date1[0]), now.day)
    except ValueError:
        secureClientPayment.send(str.encode("Error"))
        
        
    if((len(cardNumber) == 16) and (len(csc) == 3) and (now < cardDate)):
        secureClientPayment.send(str.encode("\nDone"))
        sendConfirmationToRestaurant(randomID)
        
    else:
        secureClientPayment.send(str.encode("Error"))
        			
# END of receiveRandomID() 
    
def sendConfirmationToRestaurant(randomid):
    port = 10004
    servicename = "sendConfirmation"
    serversocket = socket.socket(socket.AF_INET, socket.SOCK_STREAM)

    connstream = ssl.wrap_socket(serversocket,
                                 certfile="restaurant/server.crt",
                                 keyfile="restaurant/server.key",
                                 ssl_version=ssl.PROTOCOL_TLSv1_2)	

    connstream.connect((hostname, port))
    print("<{}>: Port: {}".format(servicename, port))
    
    priv = RSA.importKey(open('pay_dal/pay_dal_priv.pem').read())
    
    aux = randomid
    
    hashData= SHA256.new(str.encode(aux)).digest()
    signature = priv.sign(hashData, '')
    
    dataToSend = aux + " : " + str(signature)
    
    connstream.send(str.encode(dataToSend))
    print("Data Sent: <{}>".format(dataToSend))
    
    for i in randomID :
        if(randomID[i] == randomid):
            randomID.pop(i)
        
    randomIDValueToPay.pop(randomid)
            
    connstream.close()
    serversocket.close()        
    
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
        if(self.name=="connectionServerPayment"):
            connectionServerPayment()
        if(self.name=="connectionClientPayment"):
            connectionClientPayment()
        print("Exiting " + self.name)
# END of myThread

# MAIN PROGRAM -----------------------------------------------------------------------
randomID = [] 		# randomID
randomIDValueToPay = {} # {randomID : ValueToPay}

hostname = ''

BUFFER = 1024

print("Payment Server")

try:
    thread1 = myThread(1, "connectionServerPayment", 1)
    thread2 = myThread(1, "connectionClientPayment", 1)
    thread1.start()
    thread2.start()
    input("Press key for close")
    print("Closing!")
    os._exit(1)
except:
    print("Error!")


# END PROGRAM -----------------------------------------------------------------------
