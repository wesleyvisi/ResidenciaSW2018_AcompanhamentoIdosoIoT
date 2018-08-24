#include "BLEDevice.h"
#include <PubSubClient.h>


static BLEAddress *pServerAddress;

#define LED 5

BLEScan* pBLEScan;
BLEClient*  pClient;
bool dispositivoEncontrado = false;

String enderecoConhecido[] = { "f5:d1:7e:d6:d9:2f" };

 unsigned long entry;

class MyAdvertisedDeviceCallbacks: public BLEAdvertisedDeviceCallbacks {
    /**
        inicia a pesquisa de dispositivos BLE
    */
    void onResult(BLEAdvertisedDevice advertisedDevice) {
      Serial.print("BLE Dispositivo Encontrado: ");
      Serial.println(advertisedDevice.toString().c_str());
      pServerAddress = new BLEAddress(advertisedDevice.getAddress());

      bool known = false;
      for (int i = 0; i < (sizeof(enderecoConhecido) / sizeof(enderecoConhecido[0])); i++) {
        if (strcmp(pServerAddress->toString().c_str(), enderecoConhecido[i].c_str()) == 0) known = true;
      }
      if (known) {
        Serial.print("Dispositivo encontrado:");
        Serial.println(advertisedDevice.getRSSI());
        if (advertisedDevice.getRSSI() > -85) dispositivoEncontrado = true;
        else dispositivoEncontrado = false;
        Serial.println(pServerAddress->toString().c_str());
        advertisedDevice.getScan()->stop();
      }
    }
};


void setup() {
  Serial.begin(115200);
  Serial.println("Iniciando cliente de pesquisa BLE");
  pinMode(LED, OUTPUT);
  digitalWrite(LED, HIGH);

  BLEDevice::init("");

  pClient  = BLEDevice::createClient();
  Serial.println("Cliente criado");
  pBLEScan = BLEDevice::getScan();
  pBLEScan->setAdvertisedDeviceCallbacks(new MyAdvertisedDeviceCallbacks());
  pBLEScan->setActiveScan(true);
}

void loop() {

  Serial.println();
  Serial.println("Escaneando novamente");
  dispositivoEncontrado = false;
  BLEScanResults scanResults = pBLEScan->start(5);
  if (dispositivoEncontrado) {
    Serial.println("on");
    digitalWrite(LED, HIGH);
    Serial.println("Espere 10 segundos");
    delay(10000);

  }
  else {
    Serial.println("off");
    digitalWrite(LED, LOW);
    delay(3000);
  }
}
