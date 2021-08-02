package it.unipd.stage.sl.lib.rsa.exceptions

import java.lang.RuntimeException

class UninitializedPrivateKeyException(message: String = "Private key has not been initialized.") : RuntimeException(message)