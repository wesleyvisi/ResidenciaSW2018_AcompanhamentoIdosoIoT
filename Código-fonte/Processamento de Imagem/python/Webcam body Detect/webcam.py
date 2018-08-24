import cv2
import sys

video_capture = cv2.VideoCapture("rtsp://192.168.0.101:554/user=admin&password=admin&channel=1&stream=1.sdp?")
#video_capture.set(3,640)
#video_capture.set(4,480)
#video_capture.set(cv2.CAP_PROP_FPS, 1)

cascPath = "haarcascades/haarcascade_frontalface_default.xml"
cascPathE = "haarcascades/haarcascade_eye.xml"
cascPathB = "haarcascades/haarcascade_fullbody.xml"

faceCascade = cv2.CascadeClassifier(cascPath)
#faceCascadeE = cv2.CascadeClassifier(cascPathE)
faceCascadeB = cv2.CascadeClassifier(cascPathB)

while True:
    # Capture frame-by-frame
    ret, frame = video_capture.read()

    gray = cv2.cvtColor(frame, cv2.COLOR_BGR2GRAY)

    faces = faceCascade.detectMultiScale(gray, scaleFactor=1.2, minNeighbors=3)
    corpo_inteiro = faceCascadeB.detectMultiScale(gray, scaleFactor=1.2, minNeighbors=3)

    # Draw a rectangle around the faces
    for (x, y, w, h) in faces:
        print(" - " + str(len(faces)) + " rostos visiveis!")
        cv2.rectangle(frame, (x, y), (x+w, y+h), (0, 255, 0), 2)
            
    for (xc, yc, l, a) in corpo_inteiro:
        cv2.rectangle(frame, (xc, yc), (xc+l, yc+a), (0, 255, 0), 2)
        
    print(" - " + str(len(faces)) + " rostos visiveis!")
        
    # Display the resulting frame
    cv2.imshow('Video', frame)

    if cv2.waitKey(1) & 0xFF == ord('q'):
        break

# When everything is done, release the capture
video_capture.release()
cv2.destroyAllWindows()
