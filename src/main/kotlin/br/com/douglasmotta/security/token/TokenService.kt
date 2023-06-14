package br.com.douglasmotta.security.token

interface TokenService {

    fun generate(
        config: TokenConfig,
        vararg claims: TokenClaim,
    ): String
}