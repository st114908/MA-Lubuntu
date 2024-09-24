/**
 * Made by Georg Reißner
 */

#include "SerialCustomLib.hpp"
#define SERIAL_RCV_MAX_TYPE_SIZE 10
#define SERIAL_RCV_MSG_SIZE 50
#define SERIAL_RCV_BUF_SIZE SERIAL_RCV_MAX_TYPE_SIZE + 1 + SERIAL_RCV_MSG_SIZE
#define SERIAL_RCV_TIMEOUT 500

#define I2C_EMULATE_SERIAL_DEBUG

/**
 * @brief A struct to store a linked list of I2cReceivers.
 */
struct SerialReceiverListElement
{
    I2cReceiver *receiver;
    struct SerialReceiverListElement *nextElement;
};

static struct SerialReceiverListElement *serialReceiverList = nullptr; /*init an empty list*/

static Stream *serial = &Serial;
static char rcvBuffer[SERIAL_RCV_BUF_SIZE];
static uint8_t rcvBufPos = 0;
unsigned long lastSerialRecv = millis();

static void i2cCommunication_setup(uint8_t serialPortNum)
{
    switch (serialPortNum)
    {
#if defined(ARDUINO_AVR_MEGA) or defined(ARDUINO_AVR_MEGA2560)
    case 1:
        Serial1.begin(115200);
        serial = &Serial1;
#ifdef I2C_EMULATE_SERIAL_DEBUG
        Serial.println("Using Serial1");
#endif
        break;
    case 2:
        Serial2.begin(115200);
        serial = &Serial2;
#ifdef I2C_EMULATE_SERIAL_DEBUG
        Serial.println("Using Serial2");
#endif
        break;
#endif
    case 0:
    default:
        Serial.begin(115200);
        serial = &Serial;
#ifdef I2C_EMULATE_SERIAL_DEBUG
        Serial.println("Using Serial");
#endif
        break;
    }
    memset(rcvBuffer, 0, SERIAL_RCV_BUF_SIZE);
}

static void appendI2cReceiverToList(struct SerialReceiverListElement **list, I2cReceiver *newReceiver)
{
    struct SerialReceiverListElement *newListElement;
    while (*list != nullptr)
    { // navigate to the end of the list
        list = &(*list)->nextElement;
    }
    newListElement = (struct SerialReceiverListElement *)malloc(sizeof(struct SerialReceiverListElement));
    newListElement->receiver = newReceiver;
    newListElement->nextElement = nullptr;
    *list = newListElement; // append the new item to the list
}

static void initAndRegisterI2cReceiver(I2cReceiver *const receiver,
                                       char *const messageTypeName,
                                       size_t bufferCapacity,
                                       size_t messageSize,
                                       bool bufferMode)
{
    receiver->buffer = MessageBuffer_create(bufferCapacity, messageSize, bufferMode);
    receiver->messageTypeName = messageTypeName;
    appendI2cReceiverToList(&serialReceiverList, receiver);
}

static void completeMessageRecevied()
{
    struct SerialReceiverListElement **list = &serialReceiverList;
    MessageBuffer *bufferToFill = nullptr;
    while (*list != nullptr)
    {
        I2cReceiver *currentReceiver = (*list)->receiver;
        if (strcmp(currentReceiver->messageTypeName, rcvBuffer + 1) == 0)
        {
            bufferToFill = currentReceiver->buffer;
            break;
        }
        list = &(*list)->nextElement;
    }
    if (bufferToFill != nullptr)
    {
        MessageBuffer_enqueue(bufferToFill, rcvBuffer + min(strlen(rcvBuffer + 1), SERIAL_RCV_MAX_TYPE_SIZE) + 1);
    }
}

static void i2cEmulationSerial_loop()
{
    unsigned long now = millis();
    if (rcvBufPos != -1 && now - lastSerialRecv > SERIAL_RCV_TIMEOUT)
    {
        // timeout waiting for end of message
        rcvBufPos = 0;
        memset(rcvBuffer, 0, SERIAL_RCV_BUF_SIZE);
#ifdef I2C_EMULATE_SERIAL_DEBUG
        Serial.print("\x02\nTimeout of serial receive\x03");
#endif
    }
    lastSerialRecv = now;
    while (serial->available())
    {
        if (rcvBuffer[0] == 2)
        {
            // ignore message if started with STX (\0x02) until ETX (\0x03) is found => allows debug messages enclosed in STX/ETX
            char c = serial->read();
            if (c == 3)
            {
                rcvBuffer[0] = 0;
                rcvBufPos = 0;
            }
        }
        else
        {
            rcvBuffer[rcvBufPos++] = serial->read();
        }

        if (rcvBufPos >= rcvBuffer[0] + 1 || rcvBufPos >= SERIAL_RCV_BUF_SIZE)
        {
            if (rcvBuffer[0] > 2)
            {
#ifdef I2C_EMULATE_SERIAL_DEBUG
                Serial.print("\x02\nComplete message received. Topic: ");
                Serial.print(rcvBuffer + 1);
                Serial.write(3);
#endif
                completeMessageRecevied();
                rcvBufPos = 0;
                memset(rcvBuffer, 0, SERIAL_RCV_BUF_SIZE);
            }
        }
    }
}

static void sendI2cMessage(uint8_t ignored,
                           char *const messageTypeName,
                           byte *const message,
                           size_t messageLength)
{
    uint8_t typeLen = min(strlen(messageTypeName), SERIAL_RCV_MAX_TYPE_SIZE);
    messageLength = min(messageLength, SERIAL_RCV_MSG_SIZE);
#ifdef I2C_EMULATE_SERIAL_DEBUG
    Serial.print("\x02\nSending message of length: ");
    Serial.print(typeLen + 1 + messageLength);
    Serial.write(3);
#endif
    serial->write((uint8_t)(typeLen + 1 + messageLength));
    serial->write(messageTypeName, typeLen);
    serial->write((uint8_t)0);
    serial->write(message, messageLength);
}
