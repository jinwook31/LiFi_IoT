#define MYID 13

//Pin
int relay = 10;
int light_sensor = A0;

//Sensor Var
int curr_light;  int prev_light;
int data[5];
int data_size = 0;

//Start bit
boolean start;

//Decode signal
int ID;
int msg;

void setup() {
  // put your setup code here, to run once:
  pinMode(relay, OUTPUT);
  Serial.begin(9600);
  start = false;
  prev_light = analogRead(light_sensor);
  delay(10);
  curr_light = analogRead(light_sensor);
}

void loop() {
  // put your main code here, to run repeatedly:
  if(start == false){  //get start bit
    if(is_changed() == true){
      start = true;
      delay(10);
    }
  }else{  //if start bit is true -> get Data
    if(data_size == 5){  //finish getting signal
      decode_data();
      data_size = 0;
      start = false;
      delay(10);
    }else{ //reading signal
      read_data();
      OnOff();
    }
  }
  print_val();
}

void print_val(){
  for(int i = 0; i < 5; i++){
    Serial.print(data[i]);
  }Serial.println();
}

void read_data(){    //Get Binary code from signal (chagne : 1 / no change : 0) [Manchester Encoding]
  if(is_changed() == true){
    data[data_size] = 1;
  }else{
    data[data_size] = 0;
    delay(250);                         //term!!!!  0.25s
  }
  data_size++;
}

boolean is_changed(){    //check if the binary signal have chaged
  if(abs(curr_light - prev_light) > 500){                            //need to change if enviroment changed!!
    return true;
  }else{
    return false;
  }
}

void decode_data(){
  int j = 1;
  for(int i = 3; i>-1; i--){
    ID += data[i] * j;
    j *= 2;
  }
  msg = data[4];
}

void OnOff(){
  if(ID == MYID){
    if(msg == 1){
      digitalWrite(relay, HIGH);
    }else{
      digitalWrite(relay, LOW);
    }
  }
}

