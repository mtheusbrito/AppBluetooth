  String readString; 
  String codigo; 
  String texto;
  int buzzer_pin = 9;
  
  
  int ind1;
  int ind2;

   
  void setup() {
   
    Serial.begin(9600);
   pinMode(buzzer_pin, OUTPUT);
   
  }
  
  void loop() {
  
  
  
    if (Serial.available())  {
      char c = Serial.read(); 
      if (c == '*') {
       
        Serial.println();
        Serial.print("captured String is : "); 
        Serial.println(readString); 
        
        ind1 = readString.indexOf(','); 
        codigo = readString.substring(0, ind1);   
        texto = readString.substring(ind1+1);  
       
        Serial.print("codigo = ");
        Serial.println(codigo); 
        Serial.print("texto = ");
        Serial.println(texto);
     
        Serial.println();
        Serial.println();
        
        if(codigo =="f"){
          
        // Serial.println("Buzzer On"); 
        tone(buzzer_pin, 1500);
       
      }else if(codigo =="b"){
       //Serial.println("Buzzer Off"); 
      noTone(buzzer_pin);  
    } 
        
        readString=""; //limpandoVariaveis
        codigo="";
        texto="";
        
        
        
      }else {     
        readString += c; 
      
      }
    }
  }

