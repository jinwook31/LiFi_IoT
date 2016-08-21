#define ID_ON 2
#define ID_OFF 8

//Pin
int relay = 8;
int light_sensor = A4;   //A3(blue : 4,10) or A4(black : 2,8)

//Start receive
boolean start;
int s = 0;

//Data
long sig_time;
int count = -1;
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
      sig_time = millis();
    }else if(isChanged()){
      s++;
    }
  }else{  //get Data (빛이 바뀔때마다 count++)
    //일정 시간동안 변하지 않으면 decode
    if(millis() - sig_time > 4000){
      if(count == ID_ON) OnOff(1);
      else if(count == ID_OFF) OnOff(0);
      start=false;
      s = 0;
      count = -1;
      Serial.println("done");
      delay(1000);
    }else if(isChanged()){
      count++;
      Serial.println(count);
      delay(0.15);
    }
  }
}


boolean isChanged(){
  int currentVal = analogRead(light_sensor);
  //Serial.println(currentVal);
  if( abs(currentVal - pastVal) > 400 ){
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
