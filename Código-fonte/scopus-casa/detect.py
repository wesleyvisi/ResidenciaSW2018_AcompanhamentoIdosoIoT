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

cameras = [
            ["rtsp://192.168.1.109:554/user=admin&password=raspcam&channel=1&stream=0.sdp?",ANGULO_CAMERA_270,1],
            ["rtsp://192.168.1.110:554/user=admin&password=raspcam&channel=1&stream=0.sdp?",ANGULO_CAMERA_270,1]
        ]  


listaCam = []
for item in cameras:
    listaCam.append(Camera(item[0],item[1],item[2]))



for item in listaCam:
    item.setTempo(15,10)

