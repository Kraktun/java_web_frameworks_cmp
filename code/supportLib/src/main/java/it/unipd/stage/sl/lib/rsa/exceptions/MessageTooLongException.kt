package it.unipd.stage.sl.lib.rsa.exceptions

import java.io.IOException

class MessageTooLongException(message: String = "Message is too long for the chosen key length.") : IOException(message)