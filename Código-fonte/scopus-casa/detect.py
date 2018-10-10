# -*- coding: utf-8 -*-
import numpy as np
import cv2
import time
import sys
from objeto import Objeto
from imagens import Imagens
from camera import Camera
import threading 


ANGULO_CAMERA_0 = 0
ANGULO_CAMERA_90 = 90
ANGULO_CAMERA_180 = 180
ANGULO_CAMERA_270 = 270


#Lista de cameras a serem utilizadas com um angulo de instalção da camera entre os angulos disponiveis a cima e o tamanho da imagem a ser usada em proporção a sua original, sendo 1 100% e 0 0%.
cameras = [
            ["rtsp://192.168.1.109:554/user=admin&password=raspcam&channel=1&stream=0.sdp?",ANGULO_CAMERA_270,0.7],
            ["rtsp://192.168.1.110:554/user=admin&password=raspcam&channel=1&stream=0.sdp?",ANGULO_CAMERA_270,0.7]
        ]



listaCam = []
for item in cameras:
    listaCam.append(Camera(item[0],item[1],item[2]))


#configurar quanto tempo em segundos a pessoa pode ficar parada sem ativar o alerta, e quanto tempo um objeto pode ficar parado antes de ser carregado na imagem de base.
for item in listaCam:
    item.setTempo(10,10)

