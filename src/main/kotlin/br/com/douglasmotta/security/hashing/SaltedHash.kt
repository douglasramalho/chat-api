package br.com.douglasmotta.security.hashing

data class SaltedHash(
    val hash: String,
    val salt: String,
)
