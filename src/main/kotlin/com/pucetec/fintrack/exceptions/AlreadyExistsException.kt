package com.pucetec.fintrack.exceptions

class AlreadyExistsException(entity: String, value: String) :
    RuntimeException("$entity already exists: $value")
