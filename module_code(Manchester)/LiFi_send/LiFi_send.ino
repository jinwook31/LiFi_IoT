int LED = 13;
int signals[5] = {1,1,0,1,1};

void setup() {
  // initialize digital pin 13 as an output.
  pinMode(LED, OUTPUT);
  
  //get ID + msg from DB <in Raspberry pi>  13 / 1

  //convert to Binary  11011

  //LED 초기 상태 = LOW(OFF)

  //send
  digitalWrite(LED, HIGH);  //start bit
  delay(10);
  
  digitalWrite(LED, LOW);  //1
  
  digitalWrite(LED, HIGH);  //1

  delay(250);  //0

  digitalWrite(LED, LOW);   //1

  digitalWrite(LED, HIGH);  //1
}

// the loop function runs over and over again forever
void loop() {
}
