#define ID_ON 2
#define ID_OFF 8

//Pin
int relay = 8;
int light_sensor = A3;

//Start receive
boolean start;
int s = 0;

//Data
long sig_time;
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
  if(!start){ //start signal (한번 깜빡여야 시작함)
    if(s == 2){ 
      start = true;
      Serial.println("start");
    }else if(isChanged()){
      s++;
      sig_time = millis();
    }
  }else{  //get Data (빛이 바뀔때마다 count++)
    //일정 시간동안 변하지 않으면 decode
    if(millis() - sig_time > 1000){
      if(count == ID_ON) OnOff(0);
      else if(count == ID_OFF) OnOff(1);
      start=false;
      s = 0;
      Serial.println("done");
      delay(1000);
    }else if(isChanged()){
      count++;
      Serial.println(count);
    }
  }
}


boolean isChanged(){
  int currentVal = analogRead(light_sensor);
  //Serial.println(currentVal);
  if( abs(currentVal - pastVal) > 300 ){
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
