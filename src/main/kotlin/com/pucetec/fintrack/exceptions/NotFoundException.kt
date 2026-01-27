package com.pucetec.fintrack.exceptions

class NotFoundException(entity: String, id: String) : RuntimeException("$entity with id $id was not found")
