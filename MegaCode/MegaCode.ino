void setup()
{
  Serial.begin(9600);
  Serial1.begin(9600); // ESP8266
  Serial2.begin(9600); // HC05

  pinMode(12,OUTPUT);
  digitalWrite(12,LOW);  
}
String s = "",t = "";
void loop()
{
  if(Serial2.available()){
    while(Serial2.available()){
      delay(10);
      s += (char)Serial2.read();          
    }
  }
  if(s == "ping"){
    Serial2.print("done");
    Serial.println("done");
    s = "";
   }
  if(s.length() > 17){
    Serial1.print(s);
    s = "";
  }

  if(s.length() > 0){
    Serial.println(s);
    s = "";
  }
  
  
  if(Serial1.available()){
    while(Serial1.available()){
      delay(5);
      t += (char)Serial1.read();
    }
  }

  if(t.length() > 0){
    Serial2.print(t);
    t = "";
    digitalWrite(12,HIGH);      
    delay(2000);
    digitalWrite(12,LOW);  
  }
}
