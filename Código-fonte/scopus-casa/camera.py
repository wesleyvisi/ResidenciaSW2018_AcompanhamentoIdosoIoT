# -*- coding: utf-8 -*-
import numpy as np
import cv2
import time
import sys
from objeto import Objeto
from imagens import Imagens
import threading 





class Camera(object):
    
    
    def __init__(self, camera, angulo, proporcao):
        self.camera = camera
        self.angulo = angulo
        self.proporcao = proporcao
        
        self.tempoPessoa = 30
        self.tempoObjeto = 20
        
        
        print("carregando... ")
        self.imagens = Imagens(self.camera, self.angulo, self.proporcao)
        
        
        self.lista = []
    
        
        self.showThr = threading.Thread(target=self.show,args=())
        self.showThr.start()
        
        self.continua = True
        
        self.runThr = threading.Thread(target=self.run,args=())
        self.runThr.start()
        
        
        
    def run(self):
        
        
        
        
        while self.continua:
            
            if(self.imagens.pegandoBackground == False):
                
                self.imagens.readFrame()
                
                contours = self.imagens.pegarContornos()
                
                
                for contour in contours:
                    
                    (x, y, w, h) = cv2.boundingRect(contour)
                    
                    
                    
                    salva = True
                    for nitem in range(0, len(self.lista)):
                        
                        if(not salva):
                            break
                            
                            
                        item = self.lista[nitem]
                        
                        area = item.verificaArea(x, y, w, h)
                        
        
                        if((cv2.contourArea(contour) < 40*40) & (w < (h * 4)) & (w > (h * 0.20)) & (not area)):
                            salva = False
                            self.imagens.atualizaBackground(x,y,w,h)
                            
                            break
                        
                        if(cv2.contourArea(contour) < 40*40):
                            break
                        
                        
                            
                        if(area):
                            
                            salva = False
            
            
                            if(item.ultimoFrame == self.imagens.numFrame):
                                if x > item.x:
                                    x = item.x
                                    
                                if y > item.y:
                                    y = item.y
                                    
                                if x + w < item.x + item.w:
                                    w = (item.x + item.w) - x
                                    
                                if y + h < item.y + item.h:
                                    h = (item.y + item.h) - y
                            else:
                                item.areaAnterior = [item.x,item.y,item.w,item.h]
                                    
                                
                            item.x = x
                            item.y = y
                            item.w = w
                            item.h = h
                            item.ultimoFrame = self.imagens.numFrame
                            
                            
                            break
                        
                    
                        
                    
                    #se o quadrado não estiver na lista salva ele
                    if(salva & (cv2.contourArea(contour) > 70*70)):
                        self.lista.append(Objeto(x, y, w, h,[x, y, w, h],time.time(),self.imagens.numFrame,self.imagens))
                                
                        
                
                
                
                    
                
                #a cada 5 frames verifica por quadrados duplicados
                if(self.imagens.numFrame % 5 == 0):
                    
                    self.verificaQuadradoDuplicadoOuGrande()
                    
                    
                    
                self.atualizaTime()      
                     
                        
                    
                #Marca quadrados na imagem
                
                for nitem in range(0, len(self.lista)):
                    
                    item = self.lista[nitem]
                    
                    x = item.x
                    y = item.y
                    w = item.w
                    h = item.h
                    
                    
                        
                    
                    #cv2.putText(imagens.frame, "{}".format(str(item.num)), (x, y+50), cv2.FONT_HERSHEY_SIMPLEX, 1.2, color, 2)
                    #cv2.putText(imagens.frame, "{}".format(str(int(time.time() - item.ultimoMovimento))), (x, y+110), cv2.FONT_HERSHEY_SIMPLEX, 1, color, 2)
                    if((item.pessoa()) & (item.tempoParado() > self.tempoPessoa)):
                        cv2.putText(self.imagens.frame, "{}".format(str(item.num)), (x, y+50), cv2.FONT_HERSHEY_SIMPLEX, 1.2, (0,0,255), 2)
                        cv2.putText(self.imagens.frame, "{}".format("SOS"), (x, y+120), cv2.FONT_HERSHEY_SIMPLEX, 2, (0,0,255), 10)
                        cv2.rectangle(self.imagens.frame, (x, y), (x + w, y + h), (0,0,255), 3)
                        
                        
                    elif(item.pessoa() ):
                        cv2.putText(self.imagens.frame, "{}".format(str(item.num)), (x, y+50), cv2.FONT_HERSHEY_SIMPLEX, 1.2, (0,255,0), 2)
                        cv2.putText(self.imagens.frame, "{}".format(str(int(item.tempoParado()))), (x, y+120), cv2.FONT_HERSHEY_SIMPLEX, 1.5, (255,0,255), 3)
                        cv2.rectangle(self.imagens.frame, (x, y), (x + w, y + h), (0,255,0), 3)
                        
                        
                    elif(item.tempoParado() > self.tempoObjeto):
                        self.imagens.atualizaBackground(item.x,item.y,item.w,item.h)  
                        
                    else:
                        cv2.putText(self.imagens.frame, "{}".format(str(item.num)), (x, y+50), cv2.FONT_HERSHEY_SIMPLEX, 1.2, (50,255,255), 2)
                        cv2.rectangle(self.imagens.frame, (x, y), (x + w, y + h), (50,255,255), 3)
                        
                    
                
                
                    
                self.imagens.atualizaFrameShow()
            
            
                
                
                if(self.imagens.numFrame % 5 == 0):
                    self.verificaQuadradosVazios()
                
                
                
            
            
            
            
        cv2.destroyAllWindows()
        
        
        
        
    def show(self):
        
        time.sleep(1)
        while True:
            if(not (self.imagens is None)):
                if(not self.imagens.pegandoBackground):
                    time.sleep(0.2)
                    cv2.imshow("bin - "+str(self.camera),self.imagens.bin)
                    cv2.imshow("bg - "+str(self.camera),self.imagens.bg)
                    cv2.imshow("Frame - "+str(self.camera),self.imagens.frameShow)
                    
                    waitKey = cv2.waitKey(1) 
                    if waitKey & 0xFF == ord('q'):  
                        cv2.destroyAllWindows()
                        self.imagens.stopImagens()
                        for item in self.lista:
                            item.stopObjeto()
                        self.continua = False
                        break
                    
                    if waitKey & 0xFF == ord('n'):     
                        cv2.destroyAllWindows()
                        self.imagens.pegarBackground()
            
    
    
    
    
    def verificaQuadradoDuplicadoOuGrande(self):
        num1 = 0;
        num2 = 0;
        while num1 < len(self.lista):
                        
            if(self.lista[num1].w * self.lista[num1].h >= ((self.imagens.larguraImagem * self.imagens.alturaImagem) * 0.9)):
                self.imagens.pegarBackground()
                            
                            
            num2 = num1 + 1
                        
            while num2 < len(self.lista):
                if ((self.lista[num1].x == self.lista[num2].x) &
                        (self.lista[num1].y == self.lista[num2].y) & (self.lista[num1].w == self.lista[num2].w) &
                        (self.lista[num1].h == self.lista[num2].h)):
                    lista[num2].stopObjeto()
                    self.lista.pop(num2)
                else:
                    num2 = num2 + 1
            num1 = num1 + 1
    
    
    
    def atualizaTime(self):
        for nitem in range(0, len(self.lista)):
            item = self.lista[nitem]
            variacao = 5 
                    
            if((item.x <= (item.areaAnterior[0] - variacao)) |
                (item.x >= (item.areaAnterior[0] + variacao)) |
                (item.y <= (item.areaAnterior[1] - variacao)) |
                (item.y >= (item.areaAnterior[1] + variacao)) |
                (item.w <= (item.areaAnterior[2] - variacao)) |
                (item.w >= (item.areaAnterior[2] + variacao)) |
                (item.h <= (item.areaAnterior[3] - variacao)) |
                (item.h >= (item.areaAnterior[3] + variacao)) ):
                        
                item.ultimoMovimento = time.time() 
    
    
    
    
    def verificaQuadradosVazios(self):
        nitem = 0
        while(nitem < len(self.lista)):
            if(self.lista[nitem].ultimoFrame < self.imagens.numFrame - 2):
                self.lista[nitem].stopObjeto()
                self.lista.pop(nitem)
            else:
                nitem = nitem + 1
    
    
    def setTempo(self,tempoPessoa,tempoObjeto):
        self.tempoPessoa = tempoPessoa
        self.tempoObjeto = tempoObjeto
    
    
    
    
    
    
    
