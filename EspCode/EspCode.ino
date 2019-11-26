#include<SoftwareSerial.h>
#include <ESP8266HTTPClient.h>
#include <ESP8266WiFi.h>

SoftwareSerial ard(4, 5);  //D2, D1 = SRX, STX

void setup(){
  Serial.begin(9600);
  ard.begin(9600);
  pinMode(D0,OUTPUT);
  digitalWrite(D0,LOW);
  WiFi.begin("JioFi_20F46EC","st8yreg0kt");
  while (WiFi.status() != WL_CONNECTED) {
    delay(500);
  }
  digitalWrite(D0,HIGH);
}

void loop(){
  if(WiFi.status() != WL_CONNECTED) {
    digitalWrite(D0,LOW);
    return;
  }
  
  if(ard.available())
  {
    String s = "";
    while(ard.available()){
      delay(5);
      s += (char)ard.read();
    }
    Serial.println(s);
    HTTPClient http;
    http.begin("http://dgilibraryautomation.000webhostapp.com/issuebook.php");
    http.addHeader("Content-Type", "application/x-www-form-urlencoded");
   int httpCode = http.POST(s);
   String payload = http.getString();
    if(httpCode > 199 && httpCode < 300){
      // Success
      ard.print(payload);
    }else{
      Serial.println("Failed Posting data");
      Serial.println(httpCode);
      Serial.println(payload);
    }
 
   http.end();  //Close connection
  }
}
