import cv2
import sys

video_capture = cv2.VideoCapture("udp://127.0.0.1:2000")
video_capture2 = cv2.VideoCapture("rtsp://10.42.0.92:554/user=admin&password=admin&channel=1&stream=0.sdp?")
video_capture.set(7, 1)
video_capture2.set(7, 1)
#video_capture.set(3,320)
#video_capture.set(4,240)
#video_capture.set(5,10)

while True :
    
    ret, frame = video_capture.read()
    
    #height,width,channels = frame.shape
    
    #frame = cv2.resize(frame,(int(frame.shape[1] * 0.7),int(frame.shape[0] * 0.7)))
    
    #gray = cv2.cvtColor(frame, cv2.COLOR_BGR2GRAY)
    
    cv2.imshow('Video', frame)
    
    
    ret2, frame2 = video_capture2.read()
    cv2.imshow('Video2', frame2)

    if cv2.waitKey(1) & 0xFF == ord('q'):
        frame
        break
        
    
video_capture.release()
cv2.destroyAllWindows()