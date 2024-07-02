// Serial Port communication between the Arduino and the ESP.

/**
 * Made by Georg Reißner
 */

#ifndef I2C_CUSTOM_LIB_SERIAL_H
#define I2C_CUSTOM_LIB_SERIAL_H

#include <Arduino.h>
extern "C"
{
#include "MessageBuffer.h"
}

/**
 * @brief An I2C-emulation-Serial receiver receives messages of a certain messageTypeName and stores them in a buffer.
 */
typedef struct I2cReceiver
{
    char *messageTypeName;
    MessageBuffer *buffer;
} I2cSubscriber;

/**
 * @brief The concrete port handle for I2C to store the required configurations.
 */
typedef struct I2cHandle
{
    uint8_t ownI2cAddress;
    uint8_t otherI2cAddress;
    uint8_t numOfReceivers;
    I2cReceiver receivers[];
} I2cHandle;

/**
 * @brief Loop function mus be regularly called to process received serial data.
 * @warning If not called often/fast enough, buffer might overflow causing data to be lost or corrupted
 */
void i2cEmulationSerial_loop();

/**
 * @brief Setup the emulated I2c communication via serial port once per ECU.
 *
 * @param serialPortNum the anumber of the serial port to use (e.g. 0 for "Serial", 1 for "Serial1" etc.).
 */
void i2cCommunication_setup(uint8_t serialPortNum);

/**
 * @brief Initializes and registers an emulated I2c Serial Receiver object
 *
 * @param receiver a pointer to the receiver to be initialized - USERS MUST ALLOCATE MEMORY!
 * @param messageTypeName the message type name of the message to receive
 * @param bufferCapacity the capacity of the buffer
 * @param messageSize the size of the message to be received
 * @param bufferMode false: discard new incoming message; true: replace oldest message
 */
void initAndRegisterI2cReceiver(I2cReceiver *const receiver,
                                char *const messageTypeName,
                                size_t bufferCapacity,
                                size_t messageSize,
                                bool bufferMode);

/**
 * @brief Send a message via Serial (emulated i2c).
 *
 * @param ignored only one ecu can be addressed via serial. This value will be ignored
 * @param messageTypeName the message type name of the message to send
 * @param message the actual message
 * @param messageLength the length of the message
 */
void sendI2cMessage(uint8_t ignored,
                    char *const messageTypeName,
                    byte *const message,
                    size_t messageLength);

#endif
