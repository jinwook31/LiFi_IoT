#define ID 5

//Pin
int relay = 10;
int light_sensor = A0;

//Start receive
boolean start;
int s = 0;

//Data
int count;
int pastVal;

void setup() {
  // put your setup code here, to run once:
  pinMode(relay, OUTPUT);
  digitalWrite(relay, LOW);
  Serial.begin(9600);
  start = false;
  pastVal = analogRead(light_sensor);
}

void loop() {
  
  if(!start){ //start signal
    if(isBlink()) s++;
    if(s == 2) start = true;
  }else{  //get Data
    
  }
}


boolean isBlink(){
  int currentVal = analogRead(light_sensor);
  if(){
    pastVal = currentVal;
    return true;
  }
  return false;
}

void OnOff(int sig){
    if(sig == 1){
      digitalWrite(relay, HIGH);
    }else{
      digitalWrite(relay, LOW);
    }
}

