import sys
import signal
import socket

import path_server

def sendRequest(layer, a_x, a_y, b_x, b_y):

    sock = socket.socket(socket.AF_INET, socket.SOCK_STREAM)
    sock.connect(('127.0.0.1', 8011))

    message = "%i %s %s %s %s" % (layer, a_x, a_y, b_x, b_y)

    path_server.replyWith(sock, message)
    reply = path_server.recieveMessage(sock)

    return(reply)

# Agricola Oriental a IMASS
print sendRequest(1, -99.1660,19.3585, -99.18, 19.33)
print sendRequest(2, -99.1660,19.3585, -99.18, 19.33)
print sendRequest(3, -99.1660,19.3585, -99.18, 19.33)

# Cuauhtemoc con y contra sentido
#print sendRequest(-99.159447,19.378247, -99.160381, 19.374220)
#print sendRequest(-99.160381, 19.374220, -99.159447,19.378247)
