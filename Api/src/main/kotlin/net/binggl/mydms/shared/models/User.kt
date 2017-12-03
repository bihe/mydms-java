package net.binggl.mydms.shared.models

data class User(val userId: String, val userName: String, val displayName: String, val email: String, val claims: List<Claim>)