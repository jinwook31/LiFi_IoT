#define MYID 13

//Pin
int relay = 10;
int light_sensor = A0;

//Start receive
boolean start;

//Data
int count;


void setup() {
  // put your setup code here, to run once:
  pinMode(relay, OUTPUT);
  digitalWrite(relay, LOW);
  Serial.begin(9600);
}

void loop() {
  // put your main code here, to run repeatedly:
  
}


void OnOff(int sig){
    if(sig == 1){
      digitalWrite(relay, HIGH);
    }else{
      digitalWrite(relay, LOW);
    }
}

