  int BUZZER_INPUT = 9; 
  
  void setup() 
  {
    Serial.begin(9600);   
    pinMode(BUZZER_INPUT,OUTPUT); 
  }
  
  void loop() 
  {  char data2;
    char data; 
    if(Serial.available()) 
    {data=Serial.read(); 
      if(data=='f')//BUZZER ON
      {
        tone(BUZZER_INPUT,1500); 
      }
      if(data=='b')//BUZZER OFF
      {
        noTone(BUZZER_INPUT);
      }
      Serial.println(data);
    }
  
  }

