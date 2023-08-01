package br.com.douglasmotta.data.model

class MemberAlreadyExistsException : Exception(
    "There is already a member with that username in the room."
)