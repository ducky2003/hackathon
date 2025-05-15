package com.vinhuni.senselib.security
import com.vinhuni.senselib.model.User as UserEntity
import org.springframework.security.core.GrantedAuthority
import org.springframework.security.core.userdetails.User
class CustomUser (
    username: String,
    password: String,
    authorities: Collection<GrantedAuthority>,
    val avatar: String?,
    val fullName: String?,
    val userId: Int?
) : User(username, password, authorities){
    companion object {
        fun fromUserEntity(userEntity: UserEntity, authorities: Collection<GrantedAuthority>): CustomUser {
            return CustomUser(
                username = userEntity.username ?: "",
                password = userEntity.password ?: "",
                authorities = authorities,
                avatar = userEntity.avatar,
                fullName = userEntity.fullName,
                userId = userEntity.id
            )
        }
    }
}